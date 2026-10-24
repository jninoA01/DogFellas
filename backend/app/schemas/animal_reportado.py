from datetime import datetime

from pydantic import BaseModel, ConfigDict


# =========================================================
# DATOS BASE
# =========================================================

class AnimalReportadoBase(BaseModel):

    especie: str

    condicion: str

    urgencia: str = "Media"

    descripcion: str

    foto_uri: str | None = None

    latitud: float | None = None

    longitud: float | None = None

    nombre_provisional: str | None = None

    edad: str | None = None

    sexo: str | None = None

    salud: str | None = None

    caracteristicas: str | None = None

    estado: str = "REPORTADO"


# =========================================================
# CREAR ANIMAL
# =========================================================
#
# IMPORTANTE:
# usuario_id y usuario_reporta NO vienen desde Android.
# El backend los obtiene mediante el Firebase ID Token.
#

class AnimalReportadoCreate(AnimalReportadoBase):
    pass


# =========================================================
# ACTUALIZAR ANIMAL
# =========================================================

class AnimalReportadoUpdate(BaseModel):

    especie: str | None = None

    condicion: str | None = None

    urgencia: str | None = None

    descripcion: str | None = None

    foto_uri: str | None = None

    latitud: float | None = None

    longitud: float | None = None

    nombre_provisional: str | None = None

    edad: str | None = None

    sexo: str | None = None

    salud: str | None = None

    caracteristicas: str | None = None

    estado: str | None = None


# =========================================================
# RESPUESTA
# =========================================================

class AnimalReportadoResponse(AnimalReportadoBase):

    id: int

    fecha_reporte: datetime

    usuario_id: int | None = None

    usuario_reporta: str | None = None

    model_config = ConfigDict(
        from_attributes=True
    )
