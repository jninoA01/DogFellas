from datetime import datetime

from pydantic import BaseModel, EmailStr


class UsuarioBase(BaseModel):
    firebase_uid: str
    nombre: str
    correo: EmailStr
    telefono: str | None = None


class UsuarioCreate(UsuarioBase):
    pass


class UsuarioResponse(UsuarioBase):
    id: int
    fecha_registro: datetime

    class Config:
        from_attributes = True
