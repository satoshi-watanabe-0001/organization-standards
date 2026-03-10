# TypeScript + Express.js API Template

> Organization standard template for building RESTful APIs with TypeScript and Express.js

## 📋 Overview

This template follows the organization's coding standards and best practices for building production-ready APIs.

### Key Features

- ✅ TypeScript 5.x with strict mode
- ✅ Express.js 4.x with security middleware
- ✅ PostgreSQL database support
- ✅ JWT authentication ready
- ✅ Input validation with Zod
- ✅ Comprehensive test setup
- ✅ ESLint + Prettier configured
- ✅ Environment-based configuration

### Standards Compliance

This template complies with:
- `/01-coding-standards/typescript/` - TypeScript coding standards
- `/05-technology-stack/backend-stack.md` - Backend technology stack
- `/07-security-compliance/` - Security requirements

## 🚀 Quick Start

### Prerequisites

- Node.js >= 20.0.0
- PostgreSQL >= 15.0
- npm >= 10.0.0

### Installation

```bash
# Clone or copy this template
cp -r typescript-express-api-template my-new-api
cd my-new-api

# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Edit .env with your configuration
nano .env

# Run database migrations (if applicable)
npm run migrate

# Start development server
npm run dev
```

## 📁 Project Structure

```
src/
├── config/           # Configuration files
│   ├── database.ts   # Database configuration
│   ├── env.ts        # Environment variables
│   └── logger.ts     # Logger configuration
├── controllers/      # Request handlers
│   └── example.controller.ts
├── middlewares/      # Express middlewares
│   ├── auth.middleware.ts
│   ├── error.middleware.ts
│   └── validation.middleware.ts
├── models/           # Data models
│   └── example.model.ts
├── routes/           # Route definitions
│   ├── index.ts
│   └── example.routes.ts
├── services/         # Business logic
│   └── example.service.ts
├── utils/            # Utility functions
│   ├── errors.ts
│   └── logger.ts
├── validators/       # Input validation schemas
│   └── example.validator.ts
└── app.ts            # Application entry point

tests/
├── unit/             # Unit tests
└── integration/      # Integration tests
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file based on `.env.example`:

```env
# Server
NODE_ENV=development
PORT=3000

# Database
DATABASE_URL=postgresql://user:password@localhost:5432/dbname

# JWT
JWT_SECRET=your-secret-key-change-this
JWT_EXPIRES_IN=1h
REFRESH_TOKEN_EXPIRES_IN=7d

# CORS
CORS_ORIGIN=http://localhost:3000
```

### Path Aliases

TypeScript path aliases are configured in `tsconfig.json`:

- `@/*` - src root
- `@config/*` - src/config
- `@controllers/*` - src/controllers
- `@middlewares/*` - src/middlewares
- `@models/*` - src/models
- `@routes/*` - src/routes
- `@services/*` - src/services
- `@utils/*` - src/utils
- `@validators/*` - src/validators

## 🧪 Testing

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
```

### Test Structure

- **Unit tests**: Test individual functions and classes
- **Integration tests**: Test API endpoints end-to-end

## 📝 Development

### Available Scripts

```bash
npm run dev          # Start development server with hot reload
npm run build        # Build for production
npm start            # Start production server
npm run lint         # Run ESLint
npm run lint:fix     # Fix ESLint errors
npm run format       # Format code with Prettier
npm run type-check   # Check TypeScript types
```

### Code Style

This project follows the organization's TypeScript coding standards:

- **Naming**: camelCase for variables/functions, PascalCase for classes
- **Line length**: Maximum 100 characters
- **Indentation**: 2 spaces
- **Quotes**: Single quotes for strings
- **Semicolons**: Required

### Security Best Practices

Built-in security features:
- ✅ Helmet.js for security headers
- ✅ CORS configuration
- ✅ Rate limiting
- ✅ Input validation with Zod
- ✅ Password hashing with bcrypt
- ✅ JWT authentication support

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Routes Layer                │  HTTP routing
├─────────────────────────────────────┤
│       Controllers Layer             │  Request/Response handling
├─────────────────────────────────────┤
│       Services Layer                │  Business logic
├─────────────────────────────────────┤
│       Models Layer                  │  Data access
└─────────────────────────────────────┘
```

### Request Flow

```
Request
  ↓
Middlewares (auth, validation, etc.)
  ↓
Routes
  ↓
Controllers (request handling)
  ↓
Services (business logic)
  ↓
Models (database access)
  ↓
Response
```

## 📚 Additional Resources

### Organization Standards
- [TypeScript Coding Standards](/01-coding-standards/typescript/)
- [API Design Standards](/02-architecture-standards/api/)
- [Security Compliance](/07-security-compliance/)
- [Testing Standards](/04-quality-standards/)

### External Documentation
- [Express.js Documentation](https://expressjs.com/)
- [TypeScript Documentation](https://www.typescriptlang.org/)
- [Zod Documentation](https://zod.dev/)

## 🤝 Contributing

Follow the organization's development process:
1. Create a feature branch
2. Implement with tests
3. Run linting and type checking
4. Submit for code review

## 📄 License

MIT License - Organization Internal Use

---

**Template Version**: 1.0.0  
**Last Updated**: 2025-11-20  
**Maintained by**: Engineering Team
