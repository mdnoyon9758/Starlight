"""create users table

Revision ID: 0001
Revises: 
Create Date: 2025-08-03 17:00:00

"""

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = '0001'
down_revision = None
branch_labels = None
depends_on = None

def upgrade() - None:
    """Create the users table."""
    op.create_table(
        'users',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('username', sa.String(length=255), nullable=False, index=True),
        sa.Column('email', sa.String(length=255), nullable=False, index=True),
        sa.Column('hashed_password', sa.String(length=255), nullable=False),
        sa.Column('is_active', sa.Boolean(), nullable=True, index=True),
        sa.Column('is_superuser', sa.Boolean(), nullable=True),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('username'),
        sa.UniqueConstraint('email'),
    )


def downgrade() -> None:
    """Drop the users table."""
    op.drop_table('users')
