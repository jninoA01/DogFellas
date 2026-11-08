
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.usuario import Usuario
from app.schemas.usuario import UsuarioResponse
from app.services.firebase_auth import (
    obtener_firebase_uid,
    verificar_token_firebase
)

router = APIRouter(
    prefix="/usuarios",
    tags=["Usuarios"]
)


# =========================================================
# SINCRONIZAR USUARIO AUTENTICADO
# =========================================================

@router.post(
    "/sincronizar",
    response_model=UsuarioResponse
)
def sincronizar_usuario(
    datos: dict,
    token_data: dict = Depends(verificar_token_firebase),
    db: Session = Depends(get_db)
):
    """
    Crea o actualiza el usuario de PostgreSQL
    utilizando la identidad validada por Firebase.
    """

    firebase_uid = token_data.get("uid")
    correo_firebase = token_data.get("email")

    if not firebase_uid:
        raise HTTPException(
            status_code=401,
            detail="El token no contiene un Firebase UID válido"
        )

    if not correo_firebase:
        raise HTTPException(
            status_code=401,
            detail="El token no contiene un correo válido"
        )

    nombre = datos.get("nombre")

    if not nombre or not str(nombre).strip():
        raise HTTPException(
            status_code=400,
            detail="El nombre es obligatorio"
        )

    telefono = datos.get("telefono")

    usuario = (
        db.query(Usuario)
        .filter(Usuario.firebase_uid == firebase_uid)
        .first()
    )

    try:
        # El usuario ya existe: actualizar sus datos.
        if usuario:
            usuario.nombre = str(nombre).strip()
            usuario.correo = correo_firebase

            if telefono is not None:
                usuario.telefono = str(telefono).strip()

            db.commit()
            db.refresh(usuario)

            return usuario

        # Comprobar que el correo no pertenezca a otro usuario.
        usuario_por_correo = (
            db.query(Usuario)
            .filter(Usuario.correo == correo_firebase)
            .first()
        )

        if usuario_por_correo:
            raise HTTPException(
                status_code=409,
                detail=(
                    "El correo de Firebase ya está asociado "
                    "a otro usuario de PostgreSQL"
                )
            )

        # Crear usuario.
        nuevo_usuario = Usuario(
            firebase_uid=firebase_uid,
            nombre=str(nombre).strip(),
            correo=correo_firebase,
            telefono=(
                str(telefono).strip()
                if telefono is not None
                else None
            )
        )

        db.add(nuevo_usuario)
        db.commit()
        db.refresh(nuevo_usuario)

        return nuevo_usuario

    except HTTPException:
        db.rollback()
        raise

    except Exception:
        db.rollback()
        raise HTTPException(
            status_code=500,
            detail="No fue posible sincronizar el usuario"
        )


# =========================================================
# OBTENER USUARIO ACTUAL AUTENTICADO
# =========================================================

@router.get(
    "/me",
    response_model=UsuarioResponse
)
def obtener_usuario_actual(
    firebase_uid: str = Depends(obtener_firebase_uid),
    db: Session = Depends(get_db)
):
    """
    Devuelve únicamente la información del usuario
    autenticado mediante Firebase.
    """

    usuario = (
        db.query(Usuario)
        .filter(Usuario.firebase_uid == firebase_uid)
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail=(
                "El usuario autenticado no existe "
                "en PostgreSQL"
            )
        )

    return usuario

