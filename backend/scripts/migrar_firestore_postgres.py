import json
from datetime import datetime, date

import app.services.firebase_auth

from firebase_admin import auth, firestore
from sqlalchemy import text

from app.database import Base, engine, SessionLocal
from app.models.usuario import Usuario
from app.models.animal_reportado import AnimalReportado
from app.models.solicitud_adopcion import SolicitudAdopcion
from app.models.atencion_medica import Emergencia, Medicamento


# ============================================================
# UTILIDADES
# ============================================================

def obtener(datos, *nombres, defecto=None):
    for nombre in nombres:
        valor = datos.get(nombre)
        if valor is not None:
            return valor
    return defecto


def convertir_fecha(valor):
    if valor is None:
        return datetime.utcnow()

    if hasattr(valor, "to_datetime"):
        valor = valor.to_datetime()

    if isinstance(valor, datetime):
        return valor.replace(tzinfo=None)

    if isinstance(valor, date):
        return datetime.combine(valor, datetime.min.time())

    if isinstance(valor, str):
        try:
            return datetime.fromisoformat(
                valor.replace("Z", "+00:00")
            ).replace(tzinfo=None)
        except ValueError:
            return datetime.utcnow()

    return datetime.utcnow()


def convertir_json(valor):
    if hasattr(valor, "latitude") and hasattr(valor, "longitude"):
        return {
            "latitud": valor.latitude,
            "longitud": valor.longitude,
        }

    if isinstance(valor, dict):
        return {
            str(k): convertir_json(v)
            for k, v in valor.items()
        }

    if isinstance(valor, (list, tuple)):
        return [convertir_json(v) for v in valor]

    if isinstance(valor, (datetime, date)):
        return valor.isoformat()

    if valor is None or isinstance(
        valor, (str, int, float, bool)
    ):
        return valor

    return str(valor)


def texto(valor, defecto=""):
    if valor is None:
        return defecto
    return str(valor)


def obtener_uid(datos):
    return texto(
        obtener(datos, "usuarioId", "usuario_id", "usuarioReporta")
    )


# ============================================================
# TABLAS AUXILIARES Y REFERENCIAS FIRESTORE
# ============================================================

def preparar_esquema(conexion):
    # Estas columnas nuevas permiten rastrear el documento original.
    conexion.execute(text("""
        ALTER TABLE animales_reportados
        ADD COLUMN IF NOT EXISTS firestore_id VARCHAR(150)
    """))

    conexion.execute(text("""
        CREATE UNIQUE INDEX IF NOT EXISTS
        ux_animales_reportados_firestore_id
        ON animales_reportados (firestore_id)
        WHERE firestore_id IS NOT NULL
    """))

    conexion.execute(text("""
        ALTER TABLE solicitudes_adopcion
        ADD COLUMN IF NOT EXISTS firestore_id VARCHAR(150)
    """))

    conexion.execute(text("""
        CREATE UNIQUE INDEX IF NOT EXISTS
        ux_solicitudes_adopcion_firestore_id
        ON solicitudes_adopcion (firestore_id)
        WHERE firestore_id IS NOT NULL
    """))

    conexion.execute(text("""
        CREATE TABLE IF NOT EXISTS animal_subcolecciones (
            id BIGSERIAL PRIMARY KEY,
            animal_id INTEGER NOT NULL
                REFERENCES animales_reportados(id)
                ON DELETE CASCADE,
            firestore_id VARCHAR(150) NOT NULL,
            coleccion VARCHAR(100) NOT NULL,
            datos JSONB NOT NULL DEFAULT '{}'::jsonb,
            fecha_migracion TIMESTAMP NOT NULL DEFAULT NOW(),
            UNIQUE (animal_id, coleccion, firestore_id)
        )
    """))


def buscar_animal(conexion, firestore_id):
    resultado = conexion.execute(
        text("""
            SELECT id
            FROM animales_reportados
            WHERE firestore_id = :fid
        """),
        {"fid": firestore_id},
    ).scalar()

    if resultado is not None:
        return resultado

    # Compatibilidad con registros antiguos que usaban el ID
    # original de Firestore en la columna usuario_reporta no aplica:
    # no se usa ese campo para identificar animales.
    return None


# ============================================================
# USUARIOS
# ============================================================

def importar_usuario_uid(db, uid, datos_solicitud=None):
    if not uid:
        return None

    usuario = (
        db.query(Usuario)
        .filter(Usuario.firebase_uid == uid)
        .first()
    )

    if usuario:
        return usuario

    correo = None
    nombre = None
    telefono = None

    # Firebase Authentication es la fuente de identidad.
    try:
        usuario_firebase = auth.get_user(uid)
        correo = usuario_firebase.email
        nombre = usuario_firebase.display_name
        telefono = usuario_firebase.phone_number
    except Exception:
        pass

    # Solo usamos el correo de la solicitud si Firebase no lo entrega.
    if not correo and datos_solicitud:
        correo = obtener(
            datos_solicitud, "correo", "email"
        )

    if not correo:
        print(
            f"AVISO: no se importó el usuario {uid}: "
            "Firebase no proporcionó un correo."
        )
        return None

    # No crear duplicados de correo ni atribuir otra cuenta.
    existente = (
        db.query(Usuario)
        .filter(Usuario.correo == correo)
        .first()
    )

    if existente:
        if existente.firebase_uid == uid:
            return existente

        print(
            "AVISO: correo ya asociado a otra cuenta; "
            f"no se vinculó automáticamente el UID {uid}."
        )
        return None

    usuario = Usuario(
        firebase_uid=uid,
        nombre=nombre or "Usuario de Firebase",
        correo=correo,
        telefono=telefono,
        fecha_registro=datetime.utcnow(),
    )

    db.add(usuario)
    db.flush()
    return usuario


def importar_usuarios_firestore(db, fs):
    cantidad = 0

    for documento in fs.collection("usuarios").stream():
        datos = documento.to_dict() or {}

        uid = documento.id
        correo = obtener(datos, "correo", "email")

        if not correo:
            print(
                f"AVISO: usuario {uid} sin correo; "
                "se conserva para revisión manual."
            )
            continue

        existente = (
            db.query(Usuario)
            .filter(Usuario.firebase_uid == uid)
            .first()
        )

        if existente:
            cantidad += 1
            continue

        existente_correo = (
            db.query(Usuario)
            .filter(Usuario.correo == correo)
            .first()
        )

        if existente_correo:
            print(
                "AVISO: usuario Firestore no importado por "
                "conflicto de correo."
            )
            continue

        db.add(
            Usuario(
                firebase_uid=uid,
                nombre=texto(
                    obtener(datos, "nombre", "name"),
                    "Usuario de Firebase",
                ),
                correo=correo,
                telefono=obtener(datos, "telefono", "phone"),
                fecha_registro=datetime.utcnow(),
            )
        )
        db.flush()
        cantidad += 1

    return cantidad


# ============================================================
# ANIMALES REPORTADOS
# ============================================================

def importar_animales(db, fs):
    cantidad = 0
    mapa = {}

    for documento in fs.collection("animales_reportados").stream():
        datos = documento.to_dict() or {}
        firestore_id = documento.id

        existente = db.execute(
            text("""
                SELECT id
                FROM animales_reportados
                WHERE firestore_id = :fid
            """),
            {"fid": firestore_id},
        ).scalar()

        ubicacion = obtener(datos, "ubicacion")
        latitud = None
        longitud = None

        if hasattr(ubicacion, "latitude"):
            latitud = ubicacion.latitude
            longitud = ubicacion.longitude
        elif isinstance(ubicacion, dict):
            latitud = obtener(
                ubicacion, "latitud", "latitude", "lat"
            )
            longitud = obtener(
                ubicacion, "longitud", "longitude", "lng", "lon"
            )

        uid = texto(
            obtener(datos, "usuarioReporta", "usuario_reporta")
        )

        usuario = importar_usuario_uid(db, uid) if uid else None

        if existente is not None:
            animal_id = existente
        else:
            animal = AnimalReportado(
                especie=texto(
                    obtener(datos, "especie"), "No especificada"
                ),
                condicion=texto(
                    obtener(datos, "condicion"), "No especificada"
                ),
                urgencia=texto(
                    obtener(datos, "urgencia"), "Media"
                ),
                descripcion=texto(
                    obtener(datos, "descripcion")
                ),
                foto_uri=obtener(
                    datos, "fotoUri", "foto_uri"
                ),
                latitud=latitud,
                longitud=longitud,
                usuario_id=usuario.id if usuario else None,
                usuario_reporta=uid or "desconocido",
                estado=texto(
                    obtener(datos, "estado"), "REPORTADO"
                ),
                fecha_reporte=convertir_fecha(
                    obtener(datos, "fechaReporte", "fecha_reporte")
                ),
            )

            db.add(animal)
            db.flush()
            animal_id = animal.id

            db.execute(
                text("""
                    UPDATE animales_reportados
                    SET firestore_id = :fid
                    WHERE id = :id
                """),
                {"fid": firestore_id, "id": animal_id},
            )

        mapa[firestore_id] = animal_id
        cantidad += 1

    db.flush()
    return mapa, cantidad


# ============================================================
# EMERGENCIAS Y MEDICAMENTOS
# ============================================================

def importar_atencion_medica(db, fs):
    emergencias = 0
    medicamentos = 0

    for documento in fs.collection("emergencias").stream():
        d = documento.to_dict() or {}
        registro = db.get(Emergencia, documento.id)

        valores = {
            "titulo": texto(obtener(d, "titulo"), "Sin título"),
            "icono": texto(obtener(d, "icono")),
            "nivel": texto(obtener(d, "nivel")),
            "que_hacer": texto(
                obtener(d, "queHacer", "que_hacer")
            ),
            "cuando_ir_al_vet": texto(
                obtener(d, "cuandoIrAlVet", "cuando_ir_al_vet")
            ),
            "especie": convertir_json(
                obtener(d, "especie", defecto=[])
            ),
            "sintomas": convertir_json(
                obtener(d, "sintomas", defecto=[])
            ),
        }

        if registro is None:
            registro = Emergencia(id=documento.id, **valores)
            db.add(registro)
        else:
            for campo, valor in valores.items():
                setattr(registro, campo, valor)

        emergencias += 1

    for documento in fs.collection("medicamentos").stream():
        d = documento.to_dict() or {}
        registro = db.get(Medicamento, documento.id)

        precio = obtener(d, "precio", defecto=0)
        try:
            precio = float(precio or 0)
        except (TypeError, ValueError):
            precio = 0.0

        valores = {
            "nombre": texto(obtener(d, "nombre"), "Sin nombre"),
            "descripcion": texto(obtener(d, "descripcion")),
            "especie": convertir_json(
                obtener(d, "especie", defecto=[])
            ),
            "dosis": texto(obtener(d, "dosis")),
            "precio": precio,
            "busqueda": texto(obtener(d, "busqueda")),
            "icono": texto(obtener(d, "icono"), "💊"),
            "categoria": texto(obtener(d, "categoria")),
        }

        if registro is None:
            registro = Medicamento(id=documento.id, **valores)
            db.add(registro)
        else:
            for campo, valor in valores.items():
                setattr(registro, campo, valor)

        medicamentos += 1

    return emergencias, medicamentos


# ============================================================
# SOLICITUDES DE ADOPCIÓN
# ============================================================

MAPEO_SOLICITUD = {
    "nombre_completo": "nombreCompleto",
    "tipo_documento": "tipoDocumento",
    "numero_documento": "numeroDocumento",
    "fecha_nacimiento": "fechaNacimiento",
    "telefono": "telefono",
    "correo": "correo",
    "ciudad": "ciudad",
    "direccion": "direccion",
    "tipo_vivienda": "tipoVivienda",
    "vivienda": "vivienda",
    "autorizacion_propietario": "autorizacionPropietario",
    "con_quien_vive": "conQuienVive",
    "personas_hogar": "personasHogar",
    "acuerdo_familia": "acuerdoFamilia",
    "ha_tenido_mascotas": "haTenidoMascotas",
    "experiencia_animales": "experienciaAnimales",
    "mascotas_actuales": "mascotasActuales",
    "cantidad_mascotas": "cantidadMascotas",
    "mascotas_vacunadas": "mascotasVacunadas",
    "mascotas_esterilizadas": "mascotasEsterilizadas",
    "situacion_mascotas_anteriores": "situacionMascotasAnteriores",
    "lugar_animal": "lugarAnimal",
    "tiempo_solo": "tiempoSolo",
    "responsable_alimentacion": "responsableAlimentacion",
    "responsable_gastos": "responsableGastos",
    "capacidad_veterinaria": "capacidadVeterinaria",
    "motivo": "motivo",
    "por_que_animal": "porQueAnimal",
    "expectativas": "expectativas",
}


def importar_solicitudes(db, fs, mapa_animales):
    cantidad = 0

    for documento in fs.collection("solicitudes_adopcion").stream():
        d = documento.to_dict() or {}
        firestore_id = documento.id

        ya_importada = db.execute(
            text("""
                SELECT id
                FROM solicitudes_adopcion
                WHERE firestore_id = :fid
            """),
            {"fid": firestore_id},
        ).scalar()

        if ya_importada is not None:
            cantidad += 1
            continue

        reporte_firestore_id = texto(
            obtener(d, "reporteId", "reporte_id")
        )
        reporte_id = mapa_animales.get(reporte_firestore_id)

        if reporte_id is None:
            print(
                "AVISO: solicitud no importada: "
                "no se encontró su animal relacionado."
            )
            continue

        uid = obtener_uid(d)
        usuario = importar_usuario_uid(db, uid, d)

        if usuario is None:
            print(
                "AVISO: solicitud no importada: "
                "no se pudo resolver el usuario sin conflicto."
            )
            continue

        valores = {}
        for columna, campo_firebase in MAPEO_SOLICITUD.items():
            valor = obtener(d, campo_firebase, columna)

            if valor is None:
                valor = ""

            valores[columna] = texto(valor)

        compromiso = obtener(
            d, "compromisoAdopcion", "compromiso_adopcion",
            defecto=False,
        )
        if isinstance(compromiso, str):
            compromiso = compromiso.strip().lower() in (
                "true", "sí", "si", "1", "yes"
            )

        solicitud = SolicitudAdopcion(
            reporte_id=reporte_id,
            usuario_id=usuario.id,
            **valores,
            compromiso_adopcion=bool(compromiso),
            estado=texto(
                obtener(d, "estado"), "PENDIENTE"
            ),
            fecha_solicitud=convertir_fecha(
                obtener(d, "fechaSolicitud", "fecha_solicitud")
            ),
        )

        db.add(solicitud)
        db.flush()

        db.execute(
            text("""
                UPDATE solicitudes_adopcion
                SET firestore_id = :fid
                WHERE id = :id
            """),
            {"fid": firestore_id, "id": solicitud.id},
        )

        cantidad += 1

    return cantidad


# ============================================================
# SUBCOLECCIONES DE ANIMALES
# ============================================================

SUBCOLECCIONES = [
    "comentarios",
    "historial_estados",
    "citas_veterinarias",
    "desparasitaciones",
    "emergencias_atencion",
    "historial_medico",
    "medicamentos",
    "vacunaciones",
]


def importar_subcolecciones(conexion, fs, mapa_animales):
    cantidad = 0

    for firestore_id, animal_id in mapa_animales.items():
        referencia = fs.collection(
            "animales_reportados"
        ).document(firestore_id)

        for nombre_coleccion in SUBCOLECCIONES:
            documentos = referencia.collection(
                nombre_coleccion
            ).stream()

            for documento in documentos:
                datos = convertir_json(
                    documento.to_dict() or {}
                )

                conexion.execute(
                    text("""
                        INSERT INTO animal_subcolecciones (
                            animal_id,
                            firestore_id,
                            coleccion,
                            datos
                        )
                        VALUES (
                            :animal_id,
                            :firestore_id,
                            :coleccion,
                            CAST(:datos AS JSONB)
                        )
                        ON CONFLICT (
                            animal_id, coleccion, firestore_id
                        )
                        DO UPDATE SET datos = EXCLUDED.datos
                    """),
                    {
                        "animal_id": animal_id,
                        "firestore_id": documento.id,
                        "coleccion": nombre_coleccion,
                        "datos": json.dumps(
                            datos, ensure_ascii=False
                        ),
                    },
                )

                cantidad += 1

    return cantidad


# ============================================================
# EJECUCIÓN
# ============================================================

def main():
    fs = firestore.client()

    print("Iniciando migración de Firestore a PostgreSQL...")

    Base.metadata.create_all(bind=engine)

    with engine.begin() as conexion:
        preparar_esquema(conexion)

    db = SessionLocal()

    try:
        usuarios = importar_usuarios_firestore(db, fs)
        db.commit()

        mapa_animales, animales = importar_animales(db, fs)
        db.commit()

        emergencias, medicamentos = importar_atencion_medica(db, fs)
        db.commit()

        solicitudes = importar_solicitudes(
            db, fs, mapa_animales
        )
        db.commit()

        with engine.begin() as conexion:
            subcolecciones = importar_subcolecciones(
                conexion, fs, mapa_animales
            )

        print("\n=== RESUMEN DE MIGRACIÓN ===")
        print(f"Usuarios Firestore procesados: {usuarios}")
        print(f"Animales procesados: {animales}")
        print(f"Solicitudes procesadas: {solicitudes}")
        print(f"Emergencias procesadas: {emergencias}")
        print(f"Medicamentos procesados: {medicamentos}")
        print(f"Registros de subcolecciones: {subcolecciones}")
        print("\nMigración terminada.")

    except Exception:
        db.rollback()
        raise
    finally:
        db.close()


if __name__ == "__main__":
    main()
