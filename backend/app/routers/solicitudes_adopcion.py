from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db

from app.models.animal_reportado import AnimalReportado
from app.models.usuario import Usuario
from app.models.solicitud_adopcion import SolicitudAdopcion

from app.schemas.solicitud_adopcion import (
    SolicitudAdopcionCreate,
    SolicitudAdopcionResponse
)

from app.services.firebase_auth import obtener_firebase_uid


router = APIRouter(
    prefix="/solicitudes-adopcion",
    tags=["Solicitudes de Adopción"]
)


# =========================================================
# CREAR SOLICITUD
# =========================================================

@router.post(
    "/",
    response_model=SolicitudAdopcionResponse,
    status_code=201
)
def crear_solicitud(
    datos: SolicitudAdopcionCreate,
    firebase_uid: str = Depends(
        obtener_firebase_uid
    ),
    db: Session = Depends(get_db)
):

    # -----------------------------------------------------
    # BUSCAR USUARIO AUTENTICADO
    # -----------------------------------------------------

    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.firebase_uid == firebase_uid
        )
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail=(
                "El usuario autenticado "
                "no existe en PostgreSQL"
            )
        )

    # -----------------------------------------------------
    # COMPROBAR ANIMAL
    # -----------------------------------------------------

    animal = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.id == datos.reporte_id
        )
        .first()
    )

    if not animal:
        raise HTTPException(
            status_code=404,
            detail=(
                "El animal indicado "
                "no existe"
            )
        )

    # -----------------------------------------------------
    # COMPROBAR QUE ESTÉ EN ADOPCIÓN
    # -----------------------------------------------------

    if animal.estado != "EN_ADOPCION":
        raise HTTPException(
            status_code=400,
            detail=(
                "El animal seleccionado "
                "no está disponible para adopción"
            )
        )

    # -----------------------------------------------------
    # COMPROBAR SOLICITUD EXISTENTE
    # -----------------------------------------------------

    solicitud_existente = (
        db.query(SolicitudAdopcion)
        .filter(
            SolicitudAdopcion.reporte_id
            == datos.reporte_id,
            SolicitudAdopcion.usuario_id
            == usuario.id
        )
        .first()
    )

    if solicitud_existente:
        raise HTTPException(
            status_code=409,
            detail=(
                "Ya existe una solicitud "
                "de adopción para este animal"
            )
        )

    # -----------------------------------------------------
    # CREAR SOLICITUD
    # -----------------------------------------------------

    nueva_solicitud = SolicitudAdopcion(

        reporte_id=datos.reporte_id,

        usuario_id=usuario.id,

        nombre_completo=datos.nombre_completo,
        tipo_documento=datos.tipo_documento,
        numero_documento=datos.numero_documento,
        fecha_nacimiento=datos.fecha_nacimiento,
        telefono=datos.telefono,
        correo=datos.correo,

        ciudad=datos.ciudad,
        direccion=datos.direccion,
        tipo_vivienda=datos.tipo_vivienda,
        vivienda=datos.vivienda,
        autorizacion_propietario=(
            datos.autorizacion_propietario
        ),

        con_quien_vive=datos.con_quien_vive,
        personas_hogar=datos.personas_hogar,
        acuerdo_familia=datos.acuerdo_familia,

        ha_tenido_mascotas=(
            datos.ha_tenido_mascotas
        ),
        experiencia_animales=(
            datos.experiencia_animales
        ),
        mascotas_actuales=(
            datos.mascotas_actuales
        ),
        cantidad_mascotas=(
            datos.cantidad_mascotas
        ),
        mascotas_vacunadas=(
            datos.mascotas_vacunadas
        ),
        mascotas_esterilizadas=(
            datos.mascotas_esterilizadas
        ),
        situacion_mascotas_anteriores=(
            datos.situacion_mascotas_anteriores
        ),

        lugar_animal=datos.lugar_animal,
        tiempo_solo=datos.tiempo_solo,
        responsable_alimentacion=(
            datos.responsable_alimentacion
        ),
        responsable_gastos=(
            datos.responsable_gastos
        ),
        capacidad_veterinaria=(
            datos.capacidad_veterinaria
        ),

        motivo=datos.motivo,
        por_que_animal=(
            datos.por_que_animal
        ),
        expectativas=datos.expectativas,

        compromiso_adopcion=(
            datos.compromiso_adopcion
        ),

        estado=datos.estado
    )

    db.add(nueva_solicitud)
    db.commit()
    db.refresh(nueva_solicitud)

    return nueva_solicitud


# =========================================================
# BUSCAR SOLICITUD DEL USUARIO PARA UN ANIMAL
# =========================================================

@router.get(
    "/animal/{reporte_id}",
    response_model=SolicitudAdopcionResponse | None
)
def obtener_solicitud_existente(
    reporte_id: int,
    firebase_uid: str = Depends(
        obtener_firebase_uid
    ),
    db: Session = Depends(get_db)
):

    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.firebase_uid == firebase_uid
        )
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail=(
                "El usuario autenticado "
                "no existe en PostgreSQL"
            )
        )

    solicitud = (
        db.query(SolicitudAdopcion)
        .filter(
            SolicitudAdopcion.reporte_id
            == reporte_id,
            SolicitudAdopcion.usuario_id
            == usuario.id
        )
        .first()
    )

    return solicitud


# =========================================================
# OBTENER SOLICITUD POR ID
# =========================================================

@router.get(
    "/{solicitud_id}",
    response_model=SolicitudAdopcionResponse
)
def obtener_solicitud(
    solicitud_id: int,
    db: Session = Depends(get_db)
):

    solicitud = (
        db.query(SolicitudAdopcion)
        .filter(
            SolicitudAdopcion.id
            == solicitud_id
        )
        .first()
    )

    if not solicitud:
        raise HTTPException(
            status_code=404,
            detail="Solicitud no encontrada"
        )

    return solicitud
