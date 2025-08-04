from logging.config import fileConfig

from sqlalchemy import engine_from_config
from sqlalchemy import pool

from alembic import context

# Import settings
from app.core.config.settings import get_settings
from app.infrastructure.database import Base

# Interpret the config file for Python logging.
fileConfig(context.config.config_file_name)

# Get the target metadata
target_metadata = Base.metadata

# Set up alembic configuration
settings = get_settings()
context.config.set_main_option('sqlalchemy.url', settings.database_url_sync)


def run_migrations_offline() -> None:
    """Run migrations in 'offline' mode."""
    url = context.config.get_main_option("sqlalchemy.url")
    context.configure( url=url, target_metadata=target_metadata, literal_binds=True, dialect_opts={"paramstyle": "named"}, )

    with context.begin_transaction():
        context.run_migrations()


def run_migrations_online() -> None:
    """Run migrations in 'online' mode."""
    connectable = engine_from_config( 
        context.config.get_section(context.config.config_ini_section, {}), 
        prefix="sqlalchemy.", 
        poolclass=pool.NullPool, 
    )

    with connectable.connect() as connection:
        context.configure(connection=connection, target_metadata=target_metadata)

        with context.begin_transaction():
            context.run_migrations()


if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
