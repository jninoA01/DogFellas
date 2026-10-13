from pathlib import Path
import os

import firebase_admin
from firebase_admin import credentials, auth

from dotenv import load_dotenv

from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer


# =========================================================
# CARGAR .ENV DEL PROYECTO
# =========================================================

BASE_DIR = Path(__file__).resolve().parents[2]

ENV_FILE = BASE_DIR / ".env"

load_dotenv(
    dotenv_path=ENV_FILE
)


# =========================================================
# SEGURIDAD HTTP BEARER
# =========================================================

security = HTTPBearer()


# =========================================================
# INICIALIZAR FIREBASE ADMIN
# =========================================================

def inicializar_firebase():

    if firebase_admin._apps:
        return firebase_admin.get_app()

    ruta_credenciales = os.getenv(
        "FIREBASE_CREDENTIALS_PATH"
    )

    if not ruta_credenciales:
        raise RuntimeError(
            "No se encontró FIREBASE_CREDENTIALS_PATH "
            f"en {ENV_FILE}"
        )

    ruta = Path(ruta_credenciales)

    if not ruta.exists():
        raise RuntimeError(
            "No se encontró el archivo de credenciales "
            f"de Firebase: {ruta}"
        )

    credencial = credentials.Certificate(
        str(ruta)
    )

    return firebase_admin.initialize_app(
        credencial
    )


# Inicializar Firebase
inicializar_firebase()


# =========================================================
# VERIFICAR TOKEN DE FIREBASE
# =========================================================

def verificar_token_firebase(
    credenciales: HTTPAuthorizationCredentials = Depends(
        security
    )
):

    try:

        token = credenciales.credentials

        decoded_token = auth.verify_id_token(
            token
        )

        return decoded_token

    except Exception:

        raise HTTPException(
            status_code=401,
            detail="Token de Firebase inválido o expirado"
        )


# =========================================================
# OBTENER FIREBASE UID
# =========================================================

def obtener_firebase_uid(
    token_data: dict = Depends(
        verificar_token_firebase
    )
):

    uid = token_data.get("uid")

    if not uid:

        raise HTTPException(
            status_code=401,
            detail="El token no contiene un Firebase UID válido"
        )

    return uid
