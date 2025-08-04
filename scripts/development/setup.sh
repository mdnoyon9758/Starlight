#!/bin/bash
# Development setup script

set -e

echo "🚀 Setting up Starlight development environment..."

# Check Python version
echo "📋 Checking Python version..."
python_version=$(python3 --version 2>&1 | cut -d' ' -f2 | cut -d'.' -f1,2)
required_version="3.12"

if [ "$(printf '%s\n' "$required_version" "$python_version" | sort -V | head -n1)" != "$required_version" ]; then
    echo "❌ Python $required_version or higher is required. Found: $python_version"
    exit 1
fi

echo "✅ Python version: $python_version"

# Create virtual environment
echo "🐍 Creating virtual environment..."
if [ ! -d "venv" ]; then
    python3 -m venv venv
fi

# Activate virtual environment
echo "🔄 Activating virtual environment..."
source venv/bin/activate

# Upgrade pip
echo "📦 Upgrading pip..."
pip install --upgrade pip

# Install dependencies
echo "📚 Installing dependencies..."
pip install -e ".[dev,test]"

# Install pre-commit hooks
echo "🪝 Installing pre-commit hooks..."
pre-commit install

# Create .env file if it doesn't exist
if [ ! -f ".env" ]; then
    echo "📝 Creating .env file..."
    cp .env.example .env
    echo "⚠️  Please update .env with your configuration"
fi

# Run initial database setup
echo "🗄️  Setting up database..."
if command -v docker &> /dev/null; then
    echo "🐳 Starting Docker services..."
    docker-compose up -d db redis
    
    # Wait for database to be ready
    echo "⏳ Waiting for database to be ready..."
    sleep 10
    
    # Run migrations
    echo "🔄 Running database migrations..."
    alembic upgrade head
else
    echo "⚠️  Docker not found. Please start PostgreSQL and Redis manually."
    echo "   Then run: alembic upgrade head"
fi

# Run tests
echo "🧪 Running tests..."
pytest tests/ -v

echo "✅ Development environment setup complete!"
echo ""
echo "📋 Next steps:"
echo "   1. Update .env with your configuration"
echo "   2. Start the development server: uvicorn app.main:app --reload"
echo "   3. Open http://localhost:8000/api/v1/docs"
echo "   4. Happy coding! 🎉"
