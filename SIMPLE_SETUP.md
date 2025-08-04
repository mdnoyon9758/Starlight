# 🚀 Quick Start - Simple Development Setup

*Want to try Starlight without the full enterprise stack? Start here!*

## 🎯 Simple Setup (5 minutes)

Perfect for:
- 👨‍💻 Quick prototyping
- 🚀 Learning FastAPI patterns
- 📱 Simple REST APIs
- 🔧 Local development

### 1. **Clone & Install**
```bash
git clone https://github.com/mdnoyon9758/Starlight.git
cd Starlight
pip install -e ".[dev]"
```

### 2. **Simple Environment Setup**
```bash
# Create minimal .env file
echo "ENVIRONMENT=development
SECRET_KEY=dev-secret-key-change-in-production
DATABASE_URL=sqlite+aiosqlite:///./starlight.db
REDIS_URL=redis://localhost:6379/0" > .env
```

### 3. **Run in Simple Mode**
```bash
# Without Docker - just the API
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 4. **Access Your API**
- 🔗 **API**: http://localhost:8000
- 📖 **Docs**: http://localhost:8000/api/v1/docs
- ❤️ **Health**: http://localhost:8000/health

## ✨ What You Get (Simple Mode)

### ✅ **Working Features**
- 🔐 **JWT Authentication** (register/login)
- 📊 **Auto-generated API docs**
- 🗄️ **SQLite database** (no setup needed)
- ❤️ **Health checks**
- 📝 **Request logging**
- 🔒 **Basic security headers**

### ⚠️ **Limited Features** (Redis required)
- 🚫 **Caching** - will work but without Redis
- 🚫 **Rate limiting** - bypassed gracefully
- 🚫 **Background tasks** - need Celery setup
- 🚫 **Metrics** - basic only

## 🎯 Try These Examples

### **🌟 Hello World**
```bash
curl http://localhost:8000/api/v1/demo/hello
```

### **📋 List Features**
```bash
curl http://localhost:8000/api/v1/demo/features
```

### **📊 Check Stats**
```bash
curl http://localhost:8000/api/v1/demo/stats
```

### **🔐 Register a User**
```bash
curl -X POST "http://localhost:8000/api/v1/auth/register" \
     -H "Content-Type: application/json" \
     -d '{"username": "demo", "email": "demo@example.com", "password": "demo123"}'
```

### **🔑 Login & Get Token**
```bash
curl -X POST "http://localhost:8000/api/v1/auth/login" \
     -H "Content-Type: application/json" \
     -d '{"username": "demo", "password": "demo123"}'
```

### **👤 Get Profile (with auth)**
```bash
# First get your token from login, then:
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
     http://localhost:8000/api/v1/demo/profile
```

### **🔄 Echo Test**
```bash
curl -X POST "http://localhost:8000/api/v1/demo/echo" \
     -H "Content-Type: application/json" \
     -d '{"message": "Hello Starlight!", "test": true}'
```

## 🚀 Ready to Upgrade?

When you're ready for the full enterprise experience:

```bash
# Install Docker and run full stack
docker-compose up -d

# Access enterprise features:
# - Redis caching
# - Celery tasks
# - Prometheus metrics
# - Grafana dashboards
```

## 🔧 Troubleshooting

**Port already in use?**
```bash
python -m uvicorn app.main:app --reload --port 8001
```

**Import errors?**
```bash
pip install -e ".[dev,test]" --force-reinstall
```

**Database issues?**
```bash
rm starlight.db  # Reset SQLite database
```

---

**Need help?** Check the [full documentation](README.md) or [open an issue](https://github.com/mdnoyon9758/Starlight/issues).
