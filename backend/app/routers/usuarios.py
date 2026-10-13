from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.usuario import Usuario
from app.schemas.usuario import UsuarioCreate, UsuarioResponse
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
    token_data: dict = Depends(
        verificar_token_firebase
    ),
    db: Session = Depends(get_db)
):
    """
    Crea o actualiza el usuario de PostgreSQL
    utilizando la identidad validada por Firebase Auth.
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
        .filter(
            Usuario.firebase_uid == firebase_uid
        )
        .first()
    )

    # -----------------------------------------------------
    # EL USUARIO YA EXISTE
    # -----------------------------------------------------

    if usuario:

        usuario.nombre = str(nombre).strip()
        usuario.correo = correo_firebase

        if telefono is not None:
            usuario.telefono = str(telefono).strip()

        db.commit()
        db.refresh(usuario)

        return usuario

    # -----------------------------------------------------
    # COMPROBAR QUE EL CORREO NO ESTÉ USADO
    # -----------------------------------------------------

    usuario_por_correo = (
        db.query(Usuario)
        .filter(
            Usuario.correo == correo_firebase
        )
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

    # -----------------------------------------------------
    # CREAR USUARIO
    # -----------------------------------------------------

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


# =========================================================
# CREAR USUARIO MANUALMENTE
# =========================================================

@router.post(
    "/",
    response_model=UsuarioResponse,
    status_code=201
)
def crear_usuario(
    usuario: UsuarioCreate,
    db: Session = Depends(get_db)
):
    usuario_existente = (
        db.query(Usuario)
        .filter(
            (Usuario.firebase_uid == usuario.firebase_uid)
            | (Usuario.correo == usuario.correo)
        )
        .first()
    )

    if usuario_existente:
        raise HTTPException(
            status_code=409,
            detail="El usuario ya existe"
        )

    nuevo_usuario = Usuario(
        firebase_uid=usuario.firebase_uid,
        nombre=usuario.nombre,
        correo=usuario.correo,
        telefono=usuario.telefono
    )

    db.add(nuevo_usuario)
    db.commit()
    db.refresh(nuevo_usuario)

    return nuevo_usuario


# =========================================================
# OBTENER USUARIO ACTUAL
# =========================================================

@router.get(
    "/me",
    response_model=UsuarioResponse
)
def obtener_usuario_actual(
    firebase_uid: str = Depends(
        obtener_firebase_uid
    ),
    db: Session = Depends(get_db)
):
    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.firebase_uid == firebase_uid
        )
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


# =========================================================
# OBTENER TODOS LOS USUARIOS
# =========================================================

@router.get(
    "/",
    response_model=list[UsuarioResponse]
)
def obtener_usuarios(
    db: Session = Depends(get_db)
):
    usuarios = (
        db.query(Usuario)
        .order_by(
            Usuario.id.asc()
        )
        .all()
    )

    return usuarios


# =========================================================
# OBTENER USUARIO POR ID
# =========================================================

@router.get(
    "/{usuario_id}",
    response_model=UsuarioResponse
)
def obtener_usuario(
    usuario_id: int,
    db: Session = Depends(get_db)
):
    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.id == usuario_id
        )
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail="Usuario no encontrado"
        )

    return usuario


# =========================================================
# ACTUALIZAR USUARIO
# =========================================================

@router.put(
    "/{usuario_id}",
    response_model=UsuarioResponse
)
def actualizar_usuario(
    usuario_id: int,
    datos: UsuarioCreate,
    db: Session = Depends(get_db)
):
    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.id == usuario_id
        )
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail="Usuario no encontrado"
        )

    usuario_existente = (
        db.query(Usuario)
        .filter(
            (
                (Usuario.firebase_uid == datos.firebase_uid)
                |
                (Usuario.correo == datos.correo)
            ),
            Usuario.id != usuario_id
        )
        .first()
    )

    if usuario_existente:
        raise HTTPException(
            status_code=409,
            detail=(
                "El Firebase UID o correo ya pertenece "
                "a otro usuario"
            )
        )

    usuario.firebase_uid = datos.firebase_uid
    usuario.nombre = datos.nombre
    usuario.correo = datos.correo
    usuario.telefono = datos.telefono

    db.commit()
    db.refresh(usuario)

    return usuario


# =========================================================
# ELIMINAR USUARIO
# =========================================================

@router.delete(
    "/{usuario_id}",
    status_code=204
)
def eliminar_usuario(
    usuario_id: int,
    db: Session = Depends(get_db)
):
    usuario = (
        db.query(Usuario)
        .filter(
            Usuario.id == usuario_id
        )
        .first()
    )

    if not usuario:
        raise HTTPException(
            status_code=404,
            detail="Usuario no encontrado"
        )

    db.delete(usuario)
    db.commit()

    return None
