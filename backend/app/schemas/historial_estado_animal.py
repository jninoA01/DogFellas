from datetime import datetime

from pydantic import BaseModel, ConfigDict


class HistorialEstadoCreate(BaseModel):
    estado: str
    observacion: str = ""


class HistorialEstadoResponse(BaseModel):
    id: int
    animal_id: int
    estado: str
    fecha: datetime
    usuario_id: int | None = None
    observacion: str

    model_config = ConfigDict(from_attributes=True)
