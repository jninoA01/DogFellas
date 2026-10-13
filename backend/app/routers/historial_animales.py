from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.animal_reportado import AnimalReportado
from app.models.historial_estado_animal import HistorialEstadoAnimal
from app.models.usuario import Usuario
from app.schemas.historial_estado_animal import (
    HistorialEstadoCreate,
    HistorialEstadoResponse
)
from app.services.firebase_auth import obtener_firebase_uid


router = APIRouter(
    prefix="/animales-reportados",
    tags=["Historial de animales"]
)


# =========================================================
# CONSULTAR HISTORIAL DE UN ANIMAL
# =========================================================

@router.get(
    "/{animal_id}/historial",
    response_model=list[HistorialEstadoResponse]
)
def obtener_historial(
    animal_id: int,
    db: Session = Depends(get_db)
):
    animal = (
        db.query(AnimalReportado)
        .filter(AnimalReportado.id == animal_id)
        .first()
    )

    if not animal:
        raise HTTPException(
            status_code=404,
            detail="Animal reportado no encontrado"
        )

    return (
        db.query(HistorialEstadoAnimal)
        .filter(
            HistorialEstadoAnimal.animal_id == animal_id
        )
        .order_by(HistorialEstadoAnimal.fecha.asc())
        .all()
    )


# =========================================================
# REGISTRAR CAMBIO DE ESTADO
# Actualiza el animal y guarda su historial en una operación
# =========================================================

@router.post(
    "/{animal_id}/historial",
    response_model=HistorialEstadoResponse,
    status_code=201
)
def registrar_cambio_estado(
    animal_id: int,
    datos: HistorialEstadoCreate,
    firebase_uid: str = Depends(obtener_firebase_uid),
    db: Session = Depends(get_db)
):
    usuario = (
        db.query(Usuario)
        .filter(Usuario.firebase_uid == firebase_uid)
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail="El usuario autenticado no existe en PostgreSQL"
        )

    animal = (
        db.query(AnimalReportado)
        .filter(AnimalReportado.id == animal_id)
        .with_for_update()
        .first()
    )

    if not animal:
        raise HTTPException(
            status_code=404,
            detail="Animal reportado no encontrado"
        )

    # Solo el propietario puede cambiar el estado.
    if animal.usuario_id != usuario.id:
        raise HTTPException(
            status_code=403,
            detail="No tienes permiso para cambiar el estado de este animal"
        )

    estado = datos.estado.strip().upper()

    estados_permitidos = {
        "REPORTADO",
        "EN_ATENCION",
        "EN_ADOPCION",
        "ADOPTADO",
        "RESUELTO"
    }

    if estado not in estados_permitidos:
        raise HTTPException(
            status_code=422,
            detail="El estado indicado no es válido"
        )

    try:
        animal.estado = estado

        registro = HistorialEstadoAnimal(
            animal_id=animal.id,
            estado=estado,
            fecha=datetime.utcnow(),
            usuario_id=usuario.id,
            observacion=datos.observacion.strip()
        )

        db.add(registro)
        db.commit()
        db.refresh(registro)

        return registro

    except Exception:
        db.rollback()
        raise HTTPException(
            status_code=500,
            detail="No fue posible registrar el cambio de estado"
        )
