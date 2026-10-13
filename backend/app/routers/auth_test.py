from fastapi import APIRouter, Depends

from app.services.firebase_auth import verificar_token_firebase


router = APIRouter(
    prefix="/auth-test",
    tags=["Autenticación"]
)


@router.get("/")
def probar_autenticacion(
    token_data: dict = Depends(
        verificar_token_firebase
    )
):
    return {
        "autenticado": True,
        "firebase_uid": token_data.get("uid"),
        "correo": token_data.get("email")
    }
