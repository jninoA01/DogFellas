from datetime import datetime

from sqlalchemy import (
    Boolean,
    Column,
    DateTime,
    ForeignKey,
    Integer,
    String,
    Text
)

from app.database import Base


class SolicitudAdopcion(Base):
    __tablename__ = "solicitudes_adopcion"

    id = Column(
        Integer,
        primary_key=True,
        index=True
    )

    # =====================================================
    # RELACIONES
    # =====================================================

    reporte_id = Column(
        Integer,
        ForeignKey("animales_reportados.id"),
        nullable=False,
        index=True
    )

    usuario_id = Column(
        Integer,
        ForeignKey("usuarios.id"),
        nullable=False,
        index=True
    )

    # =====================================================
    # DATOS PERSONALES
    # =====================================================

    nombre_completo = Column(
        String(200),
        nullable=False
    )

    tipo_documento = Column(
        String(50),
        nullable=False
    )

    numero_documento = Column(
        String(50),
        nullable=False
    )

    fecha_nacimiento = Column(
        String(30),
        nullable=False
    )

    telefono = Column(
        String(30),
        nullable=False
    )

    correo = Column(
        String(255),
        nullable=False
    )

    # =====================================================
    # RESIDENCIA
    # =====================================================

    ciudad = Column(
        String(100),
        nullable=False
    )

    direccion = Column(
        String(300),
        nullable=False
    )

    tipo_vivienda = Column(
        String(100),
        nullable=False
    )

    vivienda = Column(
        String(100),
        nullable=False
    )

    autorizacion_propietario = Column(
        String(100),
        nullable=False
    )

    # =====================================================
    # COMPOSICIÓN DEL HOGAR
    # =====================================================

    con_quien_vive = Column(
        String(200),
        nullable=False
    )

    personas_hogar = Column(
        String(50),
        nullable=False
    )

    acuerdo_familia = Column(
        String(100),
        nullable=False
    )

    # =====================================================
    # EXPERIENCIA CON ANIMALES
    # =====================================================

    ha_tenido_mascotas = Column(
        String(100),
        nullable=False
    )

    experiencia_animales = Column(
        Text,
        nullable=False
    )

    mascotas_actuales = Column(
        String(200),
        nullable=False
    )

    cantidad_mascotas = Column(
        String(50),
        nullable=False
    )

    mascotas_vacunadas = Column(
        String(100),
        nullable=False
    )

    mascotas_esterilizadas = Column(
        String(100),
        nullable=False
    )

    situacion_mascotas_anteriores = Column(
        Text,
        nullable=False
    )

    # =====================================================
    # CONDICIONES PARA EL ANIMAL
    # =====================================================

    lugar_animal = Column(
        String(200),
        nullable=False
    )

    tiempo_solo = Column(
        String(100),
        nullable=False
    )

    responsable_alimentacion = Column(
        String(200),
        nullable=False
    )

    responsable_gastos = Column(
        String(200),
        nullable=False
    )

    capacidad_veterinaria = Column(
        String(100),
        nullable=False
    )

    # =====================================================
    # MOTIVACIÓN
    # =====================================================

    motivo = Column(
        Text,
        nullable=False
    )

    por_que_animal = Column(
        Text,
        nullable=False
    )

    expectativas = Column(
        Text,
        nullable=False
    )

    # =====================================================
    # COMPROMISO
    # =====================================================

    compromiso_adopcion = Column(
        Boolean,
        nullable=False,
        default=False
    )

    # =====================================================
    # ESTADO
    # =====================================================

    estado = Column(
        String(30),
        nullable=False,
        default="PENDIENTE"
    )

    fecha_solicitud = Column(
        DateTime,
        nullable=False,
        default=datetime.utcnow
    )
