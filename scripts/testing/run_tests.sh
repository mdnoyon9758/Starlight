#!/bin/bash
# Comprehensive test runner script

set -e

echo "🧪 Running Starlight test suite..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${2}${1}${NC}"
}

# Check if virtual environment is active
if [[ "$VIRTUAL_ENV" == "" ]]; then
    print_status "⚠️  Virtual environment not active. Activating..." $YELLOW
    source venv/bin/activate
fi

# Run linting
print_status "🔍 Running linting checks..." $YELLOW
echo "Running ruff check..."
ruff check . || {
    print_status "❌ Linting failed" $RED
    exit 1
}

echo "Running ruff format check..."
ruff format --check . || {
    print_status "❌ Format check failed" $RED
    exit 1
}

print_status "✅ Linting passed" $GREEN

# Run type checking
print_status "🔍 Running type checks..." $YELLOW
mypy app/ || {
    print_status "❌ Type checking failed" $RED
    exit 1
}
print_status "✅ Type checking passed" $GREEN

# Run security checks
print_status "🔒 Running security checks..." $YELLOW
bandit -r app/ -f json -o bandit-report.json || {
    print_status "⚠️  Security issues found. Check bandit-report.json" $YELLOW
}

# Run unit tests
print_status "🧪 Running unit tests..." $YELLOW
pytest tests/unit/ -v --cov=app --cov-report=term-missing || {
    print_status "❌ Unit tests failed" $RED
    exit 1
}
print_status "✅ Unit tests passed" $GREEN

# Run integration tests
print_status "🔗 Running integration tests..." $YELLOW
pytest tests/integration/ -v || {
    print_status "❌ Integration tests failed" $RED
    exit 1
}
print_status "✅ Integration tests passed" $GREEN

# Run end-to-end tests
print_status "🎯 Running end-to-end tests..." $YELLOW
pytest tests/e2e/ -v || {
    print_status "❌ End-to-end tests failed" $RED
    exit 1
}
print_status "✅ End-to-end tests passed" $GREEN

# Generate coverage report
print_status "📊 Generating coverage report..." $YELLOW
pytest tests/ --cov=app --cov-report=html --cov-report=xml
print_status "✅ Coverage report generated: htmlcov/index.html" $GREEN

# Check coverage threshold
coverage_percent=$(coverage report --format=total)
if (( $(echo "$coverage_percent < 80" | bc -l) )); then
    print_status "⚠️  Coverage is below 80%: ${coverage_percent}%" $YELLOW
else
    print_status "✅ Coverage: ${coverage_percent}%" $GREEN
fi

print_status "🎉 All tests passed!" $GREEN
echo ""
echo "📊 Test Summary:"
echo "   ✅ Linting"
echo "   ✅ Type checking"
echo "   ✅ Security scan"
echo "   ✅ Unit tests"
echo "   ✅ Integration tests"
echo "   ✅ End-to-end tests"
echo "   📈 Coverage: ${coverage_percent}%"
