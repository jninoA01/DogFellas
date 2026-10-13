"""agregar relacion entre usuarios y animales

Revision ID: f24859df9d56
Revises: e93b828bd26f
Create Date: 2026-10-04 17:23:06.248813

"""

from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = "f24859df9d56"

down_revision: Union[str, Sequence[str], None] = "e93b828bd26f"

branch_labels: Union[str, Sequence[str], None] = None

depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Agregar relación entre usuarios y animales reportados."""

    op.add_column(
        "animales_reportados",
        sa.Column(
            "usuario_id",
            sa.Integer(),
            nullable=True
        )
    )

    op.create_index(
        op.f("ix_animales_reportados_usuario_id"),
        "animales_reportados",
        ["usuario_id"],
        unique=False
    )

    op.create_foreign_key(
        "fk_animales_reportados_usuario_id",
        "animales_reportados",
        "usuarios",
        ["usuario_id"],
        ["id"]
    )


def downgrade() -> None:
    """Eliminar relación entre usuarios y animales reportados."""

    op.drop_constraint(
        "fk_animales_reportados_usuario_id",
        "animales_reportados",
        type_="foreignkey"
    )

    op.drop_index(
        op.f("ix_animales_reportados_usuario_id"),
        table_name="animales_reportados"
    )

    op.drop_column(
        "animales_reportados",
        "usuario_id"
    )
