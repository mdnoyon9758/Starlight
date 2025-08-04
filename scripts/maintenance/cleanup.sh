#!/bin/bash
# Cleanup script for development environment

set -e

echo "🧹 Cleaning up Starlight development environment..."

# Remove Python cache files
echo "🗑️  Removing Python cache files..."
find . -type f -name "*.pyc" -delete
find . -type d -name "__pycache__" -delete
find . -type d -name "*.egg-info" -exec rm -rf {} +

# Remove test artifacts
echo "🧪 Removing test artifacts..."
rm -rf .pytest_cache/
rm -rf htmlcov/
rm -f .coverage
rm -f coverage.xml
rm -f bandit-report.json
rm -f test.db

# Remove build artifacts
echo "📦 Removing build artifacts..."
rm -rf build/
rm -rf dist/
rm -rf .ruff_cache/
rm -rf .mypy_cache/

# Remove temporary files
echo "📄 Removing temporary files..."
find . -name "*.tmp" -delete
find . -name "*.temp" -delete
find . -name "*.log" -delete

# Remove Docker volumes (optional)
read -p "🐳 Remove Docker volumes? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose down -v
    echo "✅ Docker volumes removed"
fi

# Remove virtual environment (optional)
read -p "🐍 Remove virtual environment? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    rm -rf venv/
    echo "✅ Virtual environment removed"
fi

echo "✅ Cleanup complete!"
