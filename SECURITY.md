# Security Policy

## Supported Versions

We actively support the following versions of Starlight with security updates:

| Version | Supported          |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security vulnerability in Starlight, please report it privately.

### How to Report

1. **DO NOT** create a public GitHub issue for security vulnerabilities
2. Send an email to **security@starlight.dev** with:
   - A clear description of the vulnerability
   - Steps to reproduce the issue
   - Potential impact assessment
   - Any suggested fixes (if available)

### What to Expect

- **Acknowledgment**: We'll acknowledge receipt within 24 hours
- **Assessment**: Initial assessment within 72 hours
- **Updates**: Regular updates on our progress
- **Resolution**: We aim to resolve critical vulnerabilities within 7 days

### Disclosure Timeline

- **Day 0**: Vulnerability reported
- **Day 1**: Acknowledgment sent
- **Day 3**: Initial assessment completed
- **Day 7**: Fix developed and tested
- **Day 14**: Security update released
- **Day 21**: Public disclosure (if applicable)

## Security Best Practices

When using Starlight in production:

1. **Environment Variables**: Never commit sensitive data to version control
2. **HTTPS**: Always use HTTPS in production
3. **Database**: Use strong passwords and network restrictions
4. **Updates**: Keep dependencies updated regularly
5. **Monitoring**: Enable security monitoring and alerting
6. **Backups**: Maintain regular, tested backups

## Security Features

Starlight includes several built-in security features:

- JWT token authentication with expiration
- Password hashing with bcrypt
- Rate limiting middleware
- CORS protection
- Security headers middleware
- SQL injection protection via SQLAlchemy
- Input validation with Pydantic
- Dependency vulnerability scanning

## Hardening Checklist

Before deploying to production:

- [ ] Update all dependencies to latest versions
- [ ] Run security audits (`pip-audit`, `bandit`, `safety`)
- [ ] Configure proper CORS settings
- [ ] Set up HTTPS/TLS certificates
- [ ] Enable security headers
- [ ] Configure rate limiting
- [ ] Set up monitoring and alerting
- [ ] Review and rotate secrets
- [ ] Enable database connection encryption
- [ ] Configure proper firewall rules

## Contact

For security-related questions or concerns:
- Email: security@YOUR_USERNAME.github.io (or create GitHub security advisory)
- For general issues: [GitHub Issues](https://github.com/YOUR_USERNAME/starlight/issues)
- Security advisories: [GitHub Security](https://github.com/YOUR_USERNAME/starlight/security)

Thank you for helping keep Starlight secure!
