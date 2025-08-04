"""Load testing configuration using Locust."""

from locust import HttpUser, task, between
import json
import random


class StarLightApiUser(HttpUser):
    """Load test user for Starlight API."""
    
    wait_time = between(1, 3)
    
    def on_start(self):
        """Initialize user session."""
        self.token = None
        self.register_and_login()
    
    def register_and_login(self):
        """Register a new user and login to get token."""
        # Generate random user data
        user_id = random.randint(1000, 9999)
        user_data = {
            "username": f"loadtest_user_{user_id}",
            "email": f"loadtest_{user_id}@example.com",
            "password": "LoadTest123!"
        }
        
        # Register user
        response = self.client.post("/api/v1/auth/register", json=user_data)
        
        if response.status_code == 200:
            # Login to get token
            login_data = {
                "username": user_data["username"],
                "password": user_data["password"]
            }
            
            login_response = self.client.post("/api/v1/auth/login", json=login_data)
            
            if login_response.status_code == 200:
                self.token = login_response.json()["access_token"]
    
    @task(5)
    def test_health_check(self):
        """Test health check endpoint."""
        self.client.get("/health")
    
    @task(3)
    def test_root_endpoint(self):
        """Test root endpoint."""
        self.client.get("/")
    
    @task(2)
    def test_api_docs(self):
        """Test API documentation endpoint."""
        self.client.get("/api/v1/docs")
    
    @task(1)
    def test_authenticated_request(self):
        """Test authenticated endpoint."""
        if self.token:
            headers = {"Authorization": f"Bearer {self.token}"}
            self.client.get("/api/v1/users/me", headers=headers)
    
    @task(1)
    def test_file_upload_simulation(self):
        """Simulate file upload."""
        if self.token:
            headers = {"Authorization": f"Bearer {self.token}"}
            files = {'file': ('test.txt', 'Test file content', 'text/plain')}
            self.client.post("/api/v1/files/upload", files=files, headers=headers)


class HeavyLoadUser(HttpUser):
    """Heavy load user for stress testing."""
    
    wait_time = between(0.1, 0.5)
    
    @task
    def rapid_health_checks(self):
        """Rapid health check requests."""
        self.client.get("/health")


class AdminUser(HttpUser):
    """Admin user simulation."""
    
    wait_time = between(2, 5)
    
    def on_start(self):
        """Setup admin user."""
        self.admin_token = None
        self.setup_admin()
    
    def setup_admin(self):
        """Setup admin user and get token."""
        admin_data = {
            "username": "admin_user",
            "email": "admin@example.com",
            "password": "AdminPass123!"
        }
        
        # Try to register admin (might fail if exists)
        self.client.post("/api/v1/auth/register", json=admin_data)
        
        # Login as admin
        login_response = self.client.post("/api/v1/auth/login", json={
            "username": admin_data["username"],
            "password": admin_data["password"]
        })
        
        if login_response.status_code == 200:
            self.admin_token = login_response.json()["access_token"]
    
    @task
    def admin_metrics_check(self):
        """Check metrics endpoint."""
        self.client.get("/metrics")
    
    @task
    def admin_health_detailed(self):
        """Detailed health check."""
        if self.admin_token:
            headers = {"Authorization": f"Bearer {self.admin_token}"}
            self.client.get("/health", headers=headers)
