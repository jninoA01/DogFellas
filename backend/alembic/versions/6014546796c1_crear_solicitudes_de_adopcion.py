"""crear solicitudes de adopcion

Revision ID: 6014546796c1
Revises: f24859df9d56
Create Date: 2026-10-08 16:36:05.230064

"""

from typing import Sequence, Union

from alembic import op


# revision identifiers, used by Alembic.
revision: str = "6014546796c1"

down_revision: Union[
    str,
    Sequence[str],
    None
] = "f24859df9d56"

branch_labels: Union[str, Sequence[str], None] = None

depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """
    La tabla solicitudes_adopcion ya fue creada
    por SQLAlchemy mediante Base.metadata.create_all().

    Esta migración solamente sincroniza el historial
    de Alembic con el estado actual de PostgreSQL.
    """
    pass


def downgrade() -> None:
    """
    No eliminamos la tabla porque esta revisión
    solamente sincroniza el historial de Alembic.
    """
    pass
