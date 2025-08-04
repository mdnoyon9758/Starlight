import requests
import json

packages = [
    'fastapi', 'uvicorn', 'pydantic', 'sqlalchemy', 'celery', 
    'redis', 'asyncpg', 'alembic', 'pytest', 'ruff', 'mypy'
]

print("Current vs Latest Package Versions:")
print("=" * 50)

for pkg in packages:
    try:
        response = requests.get(f"https://pypi.org/pypi/{pkg}/json")
        if response.status_code == 200:
            data = response.json()
            latest_version = data["info"]["version"]
            print(f"{pkg}: {latest_version}")
        else:
            print(f"{pkg}: Unable to fetch")
    except Exception as e:
        print(f"{pkg}: Error - {e}")
