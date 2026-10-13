from datetime import datetime

from pydantic import BaseModel, ConfigDict


class SolicitudAdopcionBase(BaseModel):

    reporte_id: int

    nombre_completo: str
    tipo_documento: str
    numero_documento: str
    fecha_nacimiento: str
    telefono: str
    correo: str

    ciudad: str
    direccion: str
    tipo_vivienda: str
    vivienda: str
    autorizacion_propietario: str

    con_quien_vive: str
    personas_hogar: str
    acuerdo_familia: str

    ha_tenido_mascotas: str
    experiencia_animales: str
    mascotas_actuales: str
    cantidad_mascotas: str
    mascotas_vacunadas: str
    mascotas_esterilizadas: str
    situacion_mascotas_anteriores: str

    lugar_animal: str
    tiempo_solo: str
    responsable_alimentacion: str
    responsable_gastos: str
    capacidad_veterinaria: str

    motivo: str
    por_que_animal: str
    expectativas: str

    compromiso_adopcion: bool = False

    estado: str = "PENDIENTE"


class SolicitudAdopcionCreate(
    SolicitudAdopcionBase
):
    pass


class SolicitudAdopcionResponse(
    SolicitudAdopcionBase
):

    id: int
    usuario_id: int
    fecha_solicitud: datetime

    model_config = ConfigDict(
        from_attributes=True
    )
