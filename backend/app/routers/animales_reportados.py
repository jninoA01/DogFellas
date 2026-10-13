from math import atan2, cos, radians, sin, sqrt

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.animal_reportado import AnimalReportado
from app.models.usuario import Usuario

from app.schemas.animal_reportado import (
    AnimalReportadoCreate,
    AnimalReportadoUpdate,
    AnimalReportadoResponse
)

from app.services.firebase_auth import obtener_firebase_uid


router = APIRouter(
    prefix="/animales-reportados",
    tags=["Animales Reportados"]
)


# =========================================================
# CREAR ANIMAL REPORTADO
# =========================================================

@router.post(
    "/",
    response_model=AnimalReportadoResponse,
    status_code=201
)
def crear_animal_reportado(
    datos: AnimalReportadoCreate,
    firebase_uid: str = Depends(obtener_firebase_uid),
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
            detail="El usuario autenticado no existe en PostgreSQL"
        )

    nuevo_animal = AnimalReportado(
        especie=datos.especie,
        condicion=datos.condicion,
        urgencia=datos.urgencia,
        descripcion=datos.descripcion,
        foto_uri=datos.foto_uri,
        latitud=datos.latitud,
        longitud=datos.longitud,
        usuario_id=usuario.id,
        usuario_reporta=usuario.firebase_uid,
        estado=datos.estado
    )

    db.add(nuevo_animal)
    db.commit()
    db.refresh(nuevo_animal)

    return nuevo_animal


# =========================================================
# OBTENER TODOS LOS ANIMALES REPORTADOS
# =========================================================

@router.get(
    "/",
    response_model=list[AnimalReportadoResponse]
)
def obtener_animales_reportados(
    db: Session = Depends(get_db)
):
    animales = (
        db.query(AnimalReportado)
        .order_by(
            AnimalReportado.fecha_reporte.desc()
        )
        .all()
    )

    return animales


# =========================================================
# OBTENER ANIMALES EN ADOPCIÓN
# =========================================================

@router.get(
    "/adopcion",
    response_model=list[AnimalReportadoResponse]
)
def obtener_animales_en_adopcion(
    db: Session = Depends(get_db)
):
    animales = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.estado == "EN_ADOPCION"
        )
        .order_by(
            AnimalReportado.fecha_reporte.desc()
        )
        .all()
    )

    return animales


# =========================================================
# OBTENER ANIMALES CERCANOS
# =========================================================

@router.get(
    "/cercanos",
    response_model=list[dict]
)
def obtener_animales_cercanos(
    latitud: float,
    longitud: float,
    radio_km: float = 5,
    db: Session = Depends(get_db)
):
    if radio_km <= 0:
        raise HTTPException(
            status_code=400,
            detail="El radio debe ser mayor que 0"
        )

    animales = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.latitud.isnot(None),
            AnimalReportado.longitud.isnot(None)
        )
        .all()
    )

    animales_cercanos = []

    radio_tierra_km = 6371.0

    latitud_usuario = radians(latitud)
    longitud_usuario = radians(longitud)

    for animal in animales:

        latitud_animal = radians(
            animal.latitud
        )

        longitud_animal = radians(
            animal.longitud
        )

        diferencia_latitud = (
            latitud_animal - latitud_usuario
        )

        diferencia_longitud = (
            longitud_animal - longitud_usuario
        )

        a = (
            sin(diferencia_latitud / 2) ** 2
            +
            cos(latitud_usuario)
            *
            cos(latitud_animal)
            *
            sin(diferencia_longitud / 2) ** 2
        )

        c = 2 * atan2(
            sqrt(a),
            sqrt(1 - a)
        )

        distancia_km = (
            radio_tierra_km * c
        )

        if distancia_km <= radio_km:

            animales_cercanos.append({
                "id": animal.id,
                "especie": animal.especie,
                "condicion": animal.condicion,
                "urgencia": animal.urgencia,
                "descripcion": animal.descripcion,
                "foto_uri": animal.foto_uri,
                "latitud": animal.latitud,
                "longitud": animal.longitud,
                "usuario_id": animal.usuario_id,
                "usuario_reporta": animal.usuario_reporta,
                "estado": animal.estado,
                "fecha_reporte": animal.fecha_reporte,
                "distancia_km": round(
                    distancia_km,
                    3
                )
            })

    animales_cercanos.sort(
        key=lambda animal:
        animal["distancia_km"]
    )

    return animales_cercanos


# =========================================================
# OBTENER ANIMALES DE UN USUARIO
# =========================================================

@router.get(
    "/usuario/{usuario_id}",
    response_model=list[AnimalReportadoResponse]
)
def obtener_animales_por_usuario(
    usuario_id: int,
    db: Session = Depends(get_db)
):
    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.id == usuario_id
        )
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail="El usuario indicado no existe"
        )

    animales = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.usuario_id == usuario_id
        )
        .order_by(
            AnimalReportado.fecha_reporte.desc()
        )
        .all()
    )

    return animales


# =========================================================
# OBTENER UN ANIMAL POR ID
# =========================================================

@router.get(
    "/{animal_id}",
    response_model=AnimalReportadoResponse
)
def obtener_animal_reportado(
    animal_id: int,
    db: Session = Depends(get_db)
):
    animal = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.id == animal_id
        )
        .first()
    )

    if not animal:
        raise HTTPException(
            status_code=404,
            detail="Animal reportado no encontrado"
        )

    return animal


# =========================================================
# ACTUALIZAR ANIMAL
# =========================================================

@router.put(
    "/{animal_id}",
    response_model=AnimalReportadoResponse
)
def actualizar_animal_reportado(
    animal_id: int,
    datos: AnimalReportadoUpdate,
    db: Session = Depends(get_db)
):
    animal = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.id == animal_id
        )
        .first()
    )

    if not animal:
        raise HTTPException(
            status_code=404,
            detail="Animal reportado no encontrado"
        )

    datos_actualizados = datos.model_dump(
        exclude_unset=True
    )

    for campo, valor in datos_actualizados.items():
        setattr(
            animal,
            campo,
            valor
        )

    db.commit()
    db.refresh(animal)

    return animal


# =========================================================
# ELIMINAR ANIMAL
# =========================================================

@router.delete(
    "/{animal_id}",
    status_code=204
)
def eliminar_animal_reportado(
    animal_id: int,
    db: Session = Depends(get_db)
):
    animal = (
        db.query(AnimalReportado)
        .filter(
            AnimalReportado.id == animal_id
        )
        .first()
    )

    if not animal:
        raise HTTPException(
            status_code=404,
            detail="Animal reportado no encontrado"
        )

    db.delete(animal)
    db.commit()

    return None
