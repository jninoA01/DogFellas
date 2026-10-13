from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.atencion_medica import Emergencia, Medicamento
from app.schemas.atencion_medica import (
    EmergenciaResponse,
    MedicamentoResponse,
)


router = APIRouter(
    prefix="/atencion-medica",
    tags=["Atención Médica"],
)


def coincide_especie(especies: list[str] | None, especie: str | None) -> bool:
    if not especie:
        return True

    if not especies:
        return False

    especie_buscada = especie.strip().casefold()

    return any(
        isinstance(item, str) and item.strip().casefold() == especie_buscada
        for item in especies
    )


@router.get(
    "/emergencias",
    response_model=list[EmergenciaResponse],
)
def obtener_emergencias(
    especie: str | None = None,
    db: Session = Depends(get_db),
):
    emergencias = (
        db.query(Emergencia)
        .order_by(Emergencia.titulo.asc())
        .all()
    )

    return [
        emergencia
        for emergencia in emergencias
        if coincide_especie(emergencia.especie, especie)
    ]


@router.get(
    "/medicamentos",
    response_model=list[MedicamentoResponse],
)
def obtener_medicamentos(
    especie: str | None = None,
    db: Session = Depends(get_db),
):
    medicamentos = (
        db.query(Medicamento)
        .order_by(Medicamento.nombre.asc())
        .all()
    )

    return [
        medicamento
        for medicamento in medicamentos
        if coincide_especie(medicamento.especie, especie)
    ]
