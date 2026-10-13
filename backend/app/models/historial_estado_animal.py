from datetime import datetime

from sqlalchemy import (
    Column,
    DateTime,
    ForeignKey,
    Integer,
    Text,
    String
)
from sqlalchemy.orm import relationship

from app.database import Base


class HistorialEstadoAnimal(Base):
    __tablename__ = "historial_estados_animales"

    id = Column(
        Integer,
        primary_key=True,
        index=True
    )

    animal_id = Column(
        Integer,
        ForeignKey(
            "animales_reportados.id",
            ondelete="CASCADE"
        ),
        nullable=False,
        index=True
    )

    estado = Column(
        String(30),
        nullable=False
    )

    fecha = Column(
        DateTime,
        nullable=False,
        default=datetime.utcnow
    )

    usuario_id = Column(
        Integer,
        ForeignKey("usuarios.id"),
        nullable=True
    )

    observacion = Column(
        Text,
        nullable=False,
        default=""
    )

    animal = relationship(
        "AnimalReportado",
        backref="historial_estados"
    )

    usuario = relationship(
        "Usuario"
    )
