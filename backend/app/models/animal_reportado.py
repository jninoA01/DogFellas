from datetime import datetime

from sqlalchemy import (
    Column,
    DateTime,
    Float,
    ForeignKey,
    Integer,
    String,
    Text
)
from sqlalchemy.orm import relationship

from app.database import Base


class AnimalReportado(Base):
    __tablename__ = "animales_reportados"

    id = Column(
        Integer,
        primary_key=True,
        index=True
    )

    especie = Column(
        String(50),
        nullable=False
    )

    condicion = Column(
        String(100),
        nullable=False
    )

    urgencia = Column(
        String(20),
        nullable=False,
        default="Media"
    )

    descripcion = Column(
        Text,
        nullable=False
    )

    foto_uri = Column(
        String(500),
        nullable=True
    )

    latitud = Column(
        Float,
        nullable=True
    )

    longitud = Column(
        Float,
        nullable=True
    )

    # =====================================================
    # RELACIÓN CON USUARIO
    # =====================================================

    usuario_id = Column(
        Integer,
        ForeignKey("usuarios.id"),
        nullable=True,
        index=True
    )

    usuario = relationship(
        "Usuario",
        backref="animales_reportados"
    )

    # =====================================================
    # CAMPO ANTIGUO
    # =====================================================

    usuario_reporta = Column(
        String(255),
        nullable=False
    )

    estado = Column(
        String(30),
        nullable=False,
        default="REPORTADO"
    )

    fecha_reporte = Column(
        DateTime,
        nullable=False,
        default=datetime.utcnow
    )
