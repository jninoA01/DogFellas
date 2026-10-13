from pydantic import BaseModel, ConfigDict, Field


class EmergenciaResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: str
    titulo: str
    icono: str
    nivel: str
    que_hacer: str
    cuando_ir_al_vet: str
    especie: list[str] = Field(default_factory=list)
    sintomas: list[str] = Field(default_factory=list)


class MedicamentoResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: str
    nombre: str
    descripcion: str
    especie: list[str] = Field(default_factory=list)
    dosis: str
    precio: float
    busqueda: str
    icono: str
    categoria: str
