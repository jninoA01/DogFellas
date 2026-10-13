from fastapi import FastAPI
from sqlalchemy import text

from app.database import Base, engine

# Importamos los modelos para que SQLAlchemy los registre
from app.models.animal_reportado import AnimalReportado
from app.models.usuario import Usuario
from app.models.solicitud_adopcion import SolicitudAdopcion
from app.models.atencion_medica import Emergencia, Medicamento

from app.routers.usuarios import router as usuarios_router
from app.routers.animales_reportados import router as animales_reportados_router
from app.routers.auth_test import router as auth_test_router
from app.routers.atencion_medica import router as atencion_medica_router
from app.models.historial_estado_animal import HistorialEstadoAnimal

from app.routers.historial_animales import (
    router as historial_animales_router
)

from app.routers.solicitudes_adopcion import (
    router as solicitudes_adopcion_router
)

# Crear las tablas que todavía no existan
Base.metadata.create_all(bind=engine)


app = FastAPI(
    title="DogFellas API",
    description="Backend de la aplicación DogFellas",
    version="1.0.0"
)

app.include_router(usuarios_router)
app.include_router(animales_reportados_router)
app.include_router(auth_test_router)
app.include_router(
    solicitudes_adopcion_router
)
app.include_router(atencion_medica_router)
app.include_router(historial_animales_router)

@app.get("/")
def inicio():
    return {
        "mensaje": "DogFellas API funcionando",
        "version": "1.0.0",
        "estado": "OK"
    }


@app.get("/health")
def health():
    return {
        "estado": "saludable"
    }


@app.get("/database")
def database():
    try:
        with engine.connect() as connection:
            result = connection.execute(
                text("SELECT current_database(), current_user")
            )

            row = result.fetchone()

            return {
                "estado": "conectado",
                "database": row[0],
                "usuario": row[1]
            }

    except Exception as e:
        return {
            "estado": "error",
            "detalle": str(e)
        }
