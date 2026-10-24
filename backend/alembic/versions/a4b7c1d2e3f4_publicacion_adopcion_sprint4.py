"""publicacion de animales en adopcion (sprint 4)

Revision ID: a4b7c1d2e3f4
Revises: 6014546796c1
Create Date: 2026-10-23
"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


revision: str = "a4b7c1d2e3f4"
down_revision: Union[str, Sequence[str], None] = "6014546796c1"
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.add_column("animales_reportados", sa.Column("nombre_provisional", sa.String(100), nullable=True))
    op.add_column("animales_reportados", sa.Column("edad", sa.String(50), nullable=True))
    op.add_column("animales_reportados", sa.Column("sexo", sa.String(20), nullable=True))
    op.add_column("animales_reportados", sa.Column("salud", sa.Text(), nullable=True))
    op.add_column("animales_reportados", sa.Column("caracteristicas", sa.Text(), nullable=True))


def downgrade() -> None:
    op.drop_column("animales_reportados", "caracteristicas")
    op.drop_column("animales_reportados", "salud")
    op.drop_column("animales_reportados", "sexo")
    op.drop_column("animales_reportados", "edad")
    op.drop_column("animales_reportados", "nombre_provisional")
