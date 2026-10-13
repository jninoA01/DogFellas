import uuid

from sqlalchemy import Column, String, Text, Float
from sqlalchemy.dialects.postgresql import JSONB

from app.database import Base


def generar_id():
    return str(uuid.uuid4())


class Emergencia(Base):
    __tablename__ = "emergencias"

    id = Column(String(100), primary_key=True, default=generar_id)
    titulo = Column(String(200), nullable=False)
    icono = Column(String(50), nullable=False, default="")
    nivel = Column(String(50), nullable=False, default="")
    que_hacer = Column(Text, nullable=False, default="")
    cuando_ir_al_vet = Column(Text, nullable=False, default="")
    especie = Column(JSONB, nullable=False, default=list)
    sintomas = Column(JSONB, nullable=False, default=list)


class Medicamento(Base):
    __tablename__ = "medicamentos"

    id = Column(String(100), primary_key=True, default=generar_id)
    nombre = Column(String(200), nullable=False)
    descripcion = Column(Text, nullable=False, default="")
    especie = Column(JSONB, nullable=False, default=list)
    dosis = Column(Text, nullable=False, default="")
    precio = Column(Float, nullable=False, default=0.0)
    busqueda = Column(String(300), nullable=False, default="")
    icono = Column(String(50), nullable=False, default="💊")
    categoria = Column(String(100), nullable=False, default="")
