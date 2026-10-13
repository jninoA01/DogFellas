from datetime import datetime

from sqlalchemy import Column, DateTime, Integer, String

from app.database import Base


class Usuario(Base):
    __tablename__ = "usuarios"

    id = Column(Integer, primary_key=True, index=True)

    firebase_uid = Column(
        String(128),
        unique=True,
        nullable=False,
        index=True
    )

    nombre = Column(
        String(150),
        nullable=False
    )

    correo = Column(
        String(255),
        unique=True,
        nullable=False,
        index=True
    )

    telefono = Column(
        String(30),
        nullable=True
    )

    fecha_registro = Column(
        DateTime,
        nullable=False,
        default=datetime.utcnow
    )
