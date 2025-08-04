#!/usr/bin/env python3
"""
🚀 Starlight Simple Startup Script

Quick way to start Starlight for development without Docker or complex setup.
Perfect for beginners, prototyping, or when you just want to try the API.
"""

import os
import sys
import subprocess
import shutil
from pathlib import Path

def print_banner():
    """Print startup banner"""
    print("🌟" + "="*60 + "🌟")
    print("🚀 Starting Starlight in Simple Mode")
    print("🎯 Perfect for development and testing!")
    print("="*64)

def check_python_version():
    """Check if Python version is supported"""
    if sys.version_info < (3, 12):
        print("❌ Python 3.12+ required. Current:", sys.version)
        print("💡 Please upgrade Python: https://python.org")
        sys.exit(1)
    print(f"✅ Python {sys.version_info.major}.{sys.version_info.minor} - Good!")

def setup_environment():
    """Set up simple environment configuration"""
    env_file = Path(".env")
    simple_env = Path(".env.simple")
    
    if not env_file.exists():
        if simple_env.exists():
            print("📋 Creating .env from simple template...")
            shutil.copy(simple_env, env_file)
            print("✅ Environment configured!")
        else:
            print("⚠️  No .env file found. Creating minimal config...")
            with open(env_file, "w") as f:
                f.write("""ENVIRONMENT=development
SECRET_KEY=dev-secret-key-change-in-production
DATABASE_URL=sqlite+aiosqlite:///./starlight.db
REDIS_URL=redis://localhost:6379/0
ENABLE_METRICS=false
""")
            print("✅ Basic .env created!")
    else:
        print("✅ Using existing .env file")

def check_dependencies():
    """Check if required packages are installed"""
    try:
        import fastapi
        import uvicorn
        import sqlalchemy
        print("✅ Core dependencies found!")
        return True
    except ImportError as e:
        print(f"❌ Missing dependencies: {e}")
        print("💡 Run: pip install -e \".[dev]\"")
        return False

def start_server():
    """Start the FastAPI server"""
    print("\n🚀 Starting Starlight API server...")
    print("📖 API Docs: http://localhost:8000/api/v1/docs")
    print("❤️  Health: http://localhost:8000/health")
    print("🛑 Press Ctrl+C to stop")
    print("-" * 50)
    
    try:
        # Start uvicorn server
        cmd = [
            sys.executable, "-m", "uvicorn", 
            "app.main:app", 
            "--reload", 
            "--host", "0.0.0.0", 
            "--port", "8000"
        ]
        subprocess.run(cmd)
    except KeyboardInterrupt:
        print("\n🛑 Server stopped by user")
    except Exception as e:
        print(f"❌ Error starting server: {e}")
        print("💡 Check the troubleshooting section in SIMPLE_SETUP.md")

def main():
    """Main startup function"""
    print_banner()
    
    # Pre-flight checks
    check_python_version()
    setup_environment()
    
    if not check_dependencies():
        print("\n🔧 Installing dependencies...")
        try:
            subprocess.run([sys.executable, "-m", "pip", "install", "-e", ".[dev]"], check=True)
            print("✅ Dependencies installed!")
        except subprocess.CalledProcessError:
            print("❌ Failed to install dependencies")
            print("💡 Try: pip install fastapi uvicorn sqlalchemy aiosqlite")
            sys.exit(1)
    
    # Start the server
    start_server()

if __name__ == "__main__":
    main()
