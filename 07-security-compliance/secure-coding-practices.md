# Secure Coding Practices / セキュアコーディングプラクティス

**Version / バージョン**: 1.0.0  
**Last Updated / 最終更新**: 2024-10-24  
**Document Type / ドキュメント種別**: Security Standard / セキュリティ標準  
**Tier / 階層**: Tier 3 (Optional - MAY / 任意)

---

## Overview / 概要

### Purpose / 目的

**English:**
This document provides comprehensive secure coding practices to prevent common vulnerabilities and security flaws in application development. It covers OWASP Top 10 mitigations, language-specific security patterns, and practical guidelines for writing secure code.

**日本語:**
このドキュメントは、アプリケーション開発における一般的な脆弱性とセキュリティ上の欠陥を防ぐための包括的なセキュアコーディングプラクティスを提供します。OWASP Top 10の対策、言語固有のセキュリティパターン、安全なコードを書くための実践的なガイドラインをカバーします。

### Scope / 適用範囲

**English:**
These practices apply to all application code, including:
- Web applications (frontend and backend)
- Mobile applications
- APIs and microservices
- Infrastructure as Code
- Database queries and stored procedures
- Third-party integrations

**日本語:**
これらのプラクティスは、以下を含むすべてのアプリケーションコードに適用されます：
- Webアプリケーション（フロントエンドとバックエンド）
- モバイルアプリケーション
- APIとマイクロサービス
- Infrastructure as Code
- データベースクエリとストアドプロシージャ
- サードパーティ統合

---

## 1. OWASP Top 10 Mitigations / OWASP Top 10対策

### A01: Broken Access Control / アクセス制御の不備

**English:**

**Vulnerability:** Users can access resources or perform actions they shouldn't be authorized for.

**Mitigations:**
- Implement principle of least privilege
- Deny by default
- Validate permissions on server-side for every request
- Use centralized access control mechanisms
- Log access control failures
- Invalidate JWT tokens on logout

**日本語:**

**脆弱性:** ユーザーが権限のないリソースへのアクセスやアクションを実行できる。

**対策:**
- 最小権限の原則を実装
- デフォルトで拒否
- すべてのリクエストでサーバー側で権限を検証
- 集中型アクセス制御メカニズムを使用
- アクセス制御失敗をログに記録
- ログアウト時にJWTトークンを無効化

**✅ Good Example:**
```javascript
// Express.js - Proper authorization middleware
const authorize = (allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({ error: 'Unauthorized' });
    }
    
    if (!allowedRoles.includes(req.user.role)) {
      return res.status(403).json({ error: 'Forbidden' });
    }
    
    next();
  };
};

// Apply to specific routes
app.delete('/api/users/:id', 
  authenticate,
  authorize(['admin']),
  async (req, res) => {
    // Delete user logic
  }
);
```

**❌ Bad Example:**
```javascript
// Client-side only authorization (insecure!)
app.delete('/api/users/:id', async (req, res) => {
  // No server-side authorization check
  await User.deleteById(req.params.id);
  res.json({ success: true });
});

// Relying on client-provided role
if (req.body.userRole === 'admin') {
  // This can be manipulated by attacker
}
```

---

### A02: Cryptographic Failures / 暗号化の失敗

**English:**

**Vulnerability:** Sensitive data exposure due to weak or missing encryption.

**Mitigations:**
- Encrypt sensitive data at rest and in transit
- Use strong, modern algorithms (AES-256, RSA-2048+)
- Never store passwords in plaintext
- Use bcrypt, Argon2, or scrypt for password hashing
- Implement proper key management
- Use TLS 1.3 for data in transit

**日本語:**

**脆弱性:** 弱いまたは欠落した暗号化による機密データの露出。

**対策:**
- 静止状態および転送中の機密データを暗号化
- 強力な最新アルゴリズムを使用（AES-256、RSA-2048+）
- パスワードを平文で保存しない
- パスワードハッシュ化にbcrypt、Argon2、scryptを使用
- 適切な鍵管理を実装
- 転送中のデータにTLS 1.3を使用

**✅ Good Example:**
```python
# Python - Secure password hashing with bcrypt
import bcrypt

def hash_password(password: str) -> str:
    """Hash password using bcrypt with salt."""
    salt = bcrypt.gensalt(rounds=12)  # Cost factor 12
    hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
    return hashed.decode('utf-8')

def verify_password(password: str, hashed: str) -> bool:
    """Verify password against hash."""
    return bcrypt.checkpw(
        password.encode('utf-8'),
        hashed.encode('utf-8')
    )

# Usage
user.password_hash = hash_password(user_provided_password)

# Encrypt sensitive data at rest
from cryptography.fernet import Fernet

def encrypt_sensitive_data(data: str, key: bytes) -> bytes:
    """Encrypt sensitive data using Fernet (AES-128)."""
    f = Fernet(key)
    return f.encrypt(data.encode())

def decrypt_sensitive_data(encrypted: bytes, key: bytes) -> str:
    """Decrypt sensitive data."""
    f = Fernet(key)
    return f.decrypt(encrypted).decode()
```

**❌ Bad Example:**
```python
# NEVER DO THIS - Weak hashing
import hashlib

def bad_hash_password(password: str) -> str:
    # MD5 is broken, no salt!
    return hashlib.md5(password.encode()).hexdigest()

# NEVER DO THIS - Plaintext storage
user.password = user_provided_password  # Stored in plaintext!

# NEVER DO THIS - Weak encryption
def weak_encrypt(data: str) -> str:
    # XOR "encryption" is trivially broken
    key = 42
    return ''.join(chr(ord(c) ^ key) for c in data)
```

---

### A03: Injection / インジェクション

**English:**

**Vulnerability:** Untrusted data is sent to an interpreter as part of a command or query.

**Types:**
- SQL Injection
- NoSQL Injection
- OS Command Injection
- LDAP Injection
- XML Injection

**Mitigations:**
- Use parameterized queries (prepared statements)
- Use ORMs properly
- Validate and sanitize all inputs
- Apply principle of least privilege to database accounts
- Use allowlists for input validation

**日本語:**

**脆弱性:** 信頼できないデータがコマンドやクエリの一部としてインタープリタに送信される。

**種類:**
- SQLインジェクション
- NoSQLインジェクション
- OSコマンドインジェクション
- LDAPインジェクション
- XMLインジェクション

**対策:**
- パラメータ化クエリ（プリペアドステートメント）を使用
- ORMを適切に使用
- すべての入力を検証およびサニタイズ
- データベースアカウントに最小権限の原則を適用
- 入力検証にホワイトリストを使用

**✅ Good Example:**
```javascript
// Node.js - Parameterized query (SQL Injection prevention)
const getUserByEmail = async (email) => {
  // Using parameterized query
  const query = 'SELECT * FROM users WHERE email = $1';
  const result = await db.query(query, [email]);
  return result.rows[0];
};

// ORM (Sequelize)
const user = await User.findOne({
  where: { email: email }  // Sequelize handles escaping
});

// Input validation with allowlist
const validateUserId = (id) => {
  // Only allow numeric IDs
  if (!/^\d+$/.test(id)) {
    throw new Error('Invalid user ID format');
  }
  return parseInt(id, 10);
};
```

**❌ Bad Example:**
```javascript
// NEVER DO THIS - String concatenation (SQL Injection vulnerability!)
const getUserByEmail = async (email) => {
  const query = `SELECT * FROM users WHERE email = '${email}'`;
  // Attacker can inject: ' OR '1'='1
  const result = await db.query(query);
  return result.rows[0];
};

// NEVER DO THIS - Dynamic query construction
const searchUsers = async (searchTerm) => {
  // Command injection vulnerability
  const command = `grep ${searchTerm} /var/log/users.log`;
  exec(command, (error, stdout) => {
    // Attacker can inject: "; rm -rf /"
  });
};
```

**NoSQL Injection Prevention / NoSQLインジェクション対策:**

```javascript
// MongoDB - Secure query
const findUser = async (username) => {
  // ✅ Good: Explicit query structure
  const user = await User.findOne({
    username: { $eq: username }  // Use $eq operator
  });
  return user;
};

// ❌ Bad: Accepting arbitrary query objects
const findUser = async (query) => {
  // User can pass: { $where: "malicious code" }
  const user = await User.findOne(query);
  return user;
};
```

---

### A04: Insecure Design / 安全でない設計

**English:**

**Vulnerability:** Missing or ineffective security controls in design phase.

**Mitigations:**
- Threat modeling during design
- Security requirements from the start
- Design patterns for security (e.g., secure defaults)
- Principle of least privilege
- Defense in depth
- Separation of duties

**日本語:**

**脆弱性:** 設計段階でのセキュリティコントロールの欠如または非効果。

**対策:**
- 設計時の脅威モデリング
- 最初からセキュリティ要件を設定
- セキュリティのための設計パターン（例：セキュアなデフォルト）
- 最小権限の原則
- 多層防御
- 職務分離

**Example - Rate Limiting:**

```typescript
// ✅ Good: Rate limiting for authentication
import rateLimit from 'express-rate-limit';

const loginLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // 5 attempts
  message: 'Too many login attempts, please try again later',
  standardHeaders: true,
  legacyHeaders: false,
  // Store in Redis for distributed systems
  store: new RedisStore({
    client: redisClient,
    prefix: 'rl:login:'
  })
});

app.post('/api/login', loginLimiter, async (req, res) => {
  // Login logic
});

// ❌ Bad: No rate limiting on authentication
app.post('/api/login', async (req, res) => {
  // Vulnerable to brute force attacks
});
```

---

### A05: Security Misconfiguration / セキュリティ設定ミス

**English:**

**Vulnerability:** Insecure default configurations, incomplete setups, or verbose error messages.

**Mitigations:**
- Remove default accounts and passwords
- Disable unnecessary features and services
- Keep software up to date
- Implement security headers
- Don't expose stack traces in production
- Use secure session configuration

**日本語:**

**脆弱性:** 安全でないデフォルト設定、不完全なセットアップ、詳細なエラーメッセージ。

**対策:**
- デフォルトアカウントとパスワードを削除
- 不要な機能とサービスを無効化
- ソフトウェアを最新に保つ
- セキュリティヘッダーを実装
- 本番環境でスタックトレースを公開しない
- セキュアなセッション設定を使用

**✅ Good Example:**
```javascript
// Express.js - Security headers with helmet
import helmet from 'helmet';
import express from 'express';

const app = express();

// Apply security headers
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      scriptSrc: ["'self'", "'unsafe-inline'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      imgSrc: ["'self'", "data:", "https:"],
    }
  },
  hsts: {
    maxAge: 31536000,
    includeSubDomains: true,
    preload: true
  }
}));

// Secure session configuration
app.use(session({
  secret: process.env.SESSION_SECRET, // From environment
  name: 'sessionId', // Don't use default name
  resave: false,
  saveUninitialized: false,
  cookie: {
    secure: true, // HTTPS only
    httpOnly: true, // No JavaScript access
    maxAge: 3600000, // 1 hour
    sameSite: 'strict' // CSRF protection
  }
}));

// Production error handling (no stack traces)
if (process.env.NODE_ENV === 'production') {
  app.use((err, req, res, next) => {
    console.error(err); // Log for debugging
    res.status(500).json({ error: 'Internal server error' });
  });
} else {
  app.use((err, req, res, next) => {
    res.status(500).json({ error: err.message, stack: err.stack });
  });
}
```

**❌ Bad Example:**
```javascript
// No security headers
// Default session configuration
app.use(session({
  secret: 'hardcoded-secret', // Hardcoded!
  resave: true,
  saveUninitialized: true,
  cookie: {
    secure: false, // Works over HTTP
    httpOnly: false // Vulnerable to XSS
  }
}));

// Exposing stack traces in production
app.use((err, req, res, next) => {
  res.status(500).json({
    error: err.message,
    stack: err.stack, // Leaks internal information!
    env: process.env // NEVER expose environment variables!
  });
});
```

---

### A06: Vulnerable and Outdated Components / 脆弱で古いコンポーネント

**English:**

**Vulnerability:** Using components with known vulnerabilities.

**Mitigations:**
- Maintain inventory of components and versions
- Monitor CVE databases
- Use automated dependency scanning (Dependabot, Snyk)
- Apply patches promptly
- Remove unused dependencies
- Use Software Composition Analysis (SCA) tools

**日本語:**

**脆弱性:** 既知の脆弱性を持つコンポーネントの使用。

**対策:**
- コンポーネントとバージョンのインベントリを維持
- CVEデータベースを監視
- 自動依存関係スキャンを使用（Dependabot、Snyk）
- パッチを速やかに適用
- 未使用の依存関係を削除
- ソフトウェア構成解析（SCA）ツールを使用

**Good Practices:**

```yaml
# GitHub Dependabot configuration
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "npm"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 10
    reviewers:
      - "security-team"
    labels:
      - "dependencies"
      - "security"

  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "weekly"
```

```bash
# Regular dependency audits
npm audit
npm audit fix

# Check for outdated packages
npm outdated

# Python
pip-audit
safety check

# Java
mvn dependency:check
./gradlew dependencyCheckAnalyze
```

---

### A07: Identification and Authentication Failures / 識別と認証の失敗

**English:**

**Vulnerability:** Weak authentication mechanisms allowing attackers to compromise accounts.

**Mitigations:**
- Implement multi-factor authentication (MFA)
- Use strong password policies
- Protect against brute force (rate limiting, account lockout)
- Implement secure password recovery
- Use secure session management
- Avoid exposing session IDs in URLs

**日本語:**

**脆弱性:** 弱い認証メカニズムにより攻撃者がアカウントを侵害できる。

**対策:**
- 多要素認証（MFA）を実装
- 強力なパスワードポリシーを使用
- ブルートフォース攻撃から保護（レート制限、アカウントロックアウト）
- セキュアなパスワード回復を実装
- セキュアなセッション管理を使用
- URLにセッションIDを公開しない

**✅ Good Example:**
```typescript
// Password strength validation
import zxcvbn from 'zxcvbn';

const validatePasswordStrength = (password: string): boolean => {
  const result = zxcvbn(password);
  
  // Require strong password (score 3-4)
  if (result.score < 3) {
    throw new Error(
      `Weak password. Suggestions: ${result.feedback.suggestions.join(', ')}`
    );
  }
  
  return true;
};

// MFA implementation with TOTP
import speakeasy from 'speakeasy';

const verifyMFA = (token: string, secret: string): boolean => {
  return speakeasy.totp.verify({
    secret: secret,
    encoding: 'base32',
    token: token,
    window: 1 // Allow 30-second time window
  });
};

// Secure password reset flow
const requestPasswordReset = async (email: string) => {
  const user = await User.findByEmail(email);
  
  if (!user) {
    // Don't reveal if email exists (timing-safe)
    await sleep(randomInt(100, 300));
    return { success: true };
  }
  
  // Generate secure token
  const token = crypto.randomBytes(32).toString('hex');
  const hashedToken = await bcrypt.hash(token, 10);
  
  await user.update({
    resetToken: hashedToken,
    resetTokenExpiry: Date.now() + 3600000 // 1 hour
  });
  
  // Send email with token (via secure channel)
  await sendPasswordResetEmail(user.email, token);
  
  return { success: true };
};
```

**❌ Bad Example:**
```typescript
// Weak password validation
const validatePassword = (password: string): boolean => {
  return password.length >= 6; // Too short, no complexity requirements!
};

// No MFA

// Insecure password reset
const resetPassword = async (email: string, newPassword: string) => {
  const user = await User.findByEmail(email);
  
  if (!user) {
    throw new Error('User not found'); // Reveals if email exists!
  }
  
  // No verification of identity, anyone can reset any password!
  user.password = newPassword;
  await user.save();
};
```

---

### A08: Software and Data Integrity Failures / ソフトウェアとデータの整合性の失敗

**English:**

**Vulnerability:** Code and infrastructure that don't protect against integrity violations.

**Mitigations:**
- Use digital signatures for updates
- Verify integrity of dependencies (SRI, checksums)
- Implement CI/CD pipeline security
- Use code signing
- Verify software supply chain

**日本語:**

**脆弱性:** 整合性違反から保護しないコードとインフラストラクチャ。

**対策:**
- 更新にデジタル署名を使用
- 依存関係の整合性を検証（SRI、チェックサム）
- CI/CDパイプラインセキュリティを実装
- コード署名を使用
- ソフトウェアサプライチェーンを検証

**Good Practices:**

```html
<!-- Subresource Integrity (SRI) for CDN resources -->
<script 
  src="https://cdn.example.com/library.js"
  integrity="sha384-oqVuAfXRKap7fdgcCY5uykM6+R9GqQ8K/ux..."
  crossorigin="anonymous">
</script>
```

```yaml
# GitHub Actions - Secure CI/CD pipeline
name: Secure Build

on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
      
    steps:
      - uses: actions/checkout@v3
        with:
          persist-credentials: false
      
      # Verify dependencies
      - name: Verify package-lock.json
        run: |
          npm ci --audit
          
      # Run security scans
      - name: Run Snyk
        uses: snyk/actions/node@master
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
      
      # Sign artifacts
      - name: Sign artifact
        run: |
          gpg --import <(echo "${{ secrets.GPG_PRIVATE_KEY }}")
          gpg --detach-sign --armor artifact.tar.gz
```

---

### A09: Security Logging and Monitoring Failures / セキュリティログと監視の失敗

**English:**

**Vulnerability:** Insufficient logging and monitoring to detect breaches.

**Mitigations:**
- Log all authentication attempts
- Log access control failures
- Ensure logs are tamper-resistant
- Implement real-time alerting
- Centralize log management
- Regular log review

**日本語:**

**脆弱性:** 侵害を検出するための不十分なログと監視。

**対策:**
- すべての認証試行をログに記録
- アクセス制御失敗をログに記録
- ログが改ざん不可能であることを確保
- リアルタイムアラートを実装
- ログ管理を集中化
- 定期的なログレビュー

**✅ Good Example:**
```typescript
// Structured security logging
import winston from 'winston';

const securityLogger = winston.createLogger({
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.File({ 
      filename: 'security.log',
      level: 'info'
    })
  ]
});

// Log authentication attempts
const logAuthAttempt = (req, success: boolean) => {
  securityLogger.info({
    event: 'authentication',
    success,
    username: req.body.username,
    ip: req.ip,
    userAgent: req.headers['user-agent'],
    timestamp: new Date().toISOString()
  });
};

// Log access control failures
const logAccessDenied = (req, resource: string) => {
  securityLogger.warn({
    event: 'access_denied',
    userId: req.user?.id,
    resource,
    action: req.method,
    ip: req.ip,
    timestamp: new Date().toISOString()
  });
};

// Alert on suspicious activity
const checkSuspiciousActivity = async (userId: string) => {
  const recentFailures = await getRecentAuthFailures(userId, '15m');
  
  if (recentFailures > 5) {
    await sendSecurityAlert({
      type: 'brute_force_suspected',
      userId,
      failureCount: recentFailures
    });
  }
};
```

---

### A10: Server-Side Request Forgery (SSRF) / サーバーサイドリクエストフォージェリ

**English:**

**Vulnerability:** Web application fetches remote resources without validating user-supplied URL.

**Mitigations:**
- Validate and sanitize all URLs
- Use allowlists for domains
- Disable HTTP redirections
- Don't send raw responses to clients
- Implement network segmentation

**日本語:**

**脆弱性:** Webアプリケーションがユーザー提供のURLを検証せずにリモートリソースを取得。

**対策:**
- すべてのURLを検証およびサニタイズ
- ドメインのホワイトリストを使用
- HTTPリダイレクトを無効化
- 生のレスポンスをクライアントに送信しない
- ネットワークセグメンテーションを実装

**✅ Good Example:**
```javascript
// Secure URL fetching with allowlist
const ALLOWED_DOMAINS = ['api.example.com', 'cdn.example.com'];

const fetchRemoteResource = async (url) => {
  const parsedUrl = new URL(url);
  
  // Validate protocol
  if (!['http:', 'https:'].includes(parsedUrl.protocol)) {
    throw new Error('Invalid protocol');
  }
  
  // Check against allowlist
  if (!ALLOWED_DOMAINS.includes(parsedUrl.hostname)) {
    throw new Error('Domain not allowed');
  }
  
  // Prevent access to internal resources
  const isPrivate = await isPrivateIP(parsedUrl.hostname);
  if (isPrivate) {
    throw new Error('Cannot access private network');
  }
  
  const response = await fetch(url, {
    redirect: 'manual', // Don't follow redirects
    timeout: 5000
  });
  
  return response;
};
```

**❌ Bad Example:**
```javascript
// NEVER DO THIS - SSRF vulnerability
app.get('/fetch', async (req, res) => {
  const url = req.query.url; // User-controlled!
  
  // Attacker can provide: http://localhost:6379/
  // or http://169.254.169.254/latest/meta-data/
  const response = await fetch(url);
  res.send(await response.text());
});
```

---

## 2. Input Validation / 入力検証

### Validation Principles / 検証原則

**English:**

1. **Validate on Server-Side**: Never trust client-side validation alone
2. **Use Allowlists**: Define what is allowed, not what is forbidden
3. **Validate Data Type**: Ensure correct type (string, number, etc.)
4. **Validate Length**: Enforce minimum and maximum lengths
5. **Validate Format**: Use regex or validation libraries
6. **Validate Range**: For numeric inputs
7. **Canonicalize Input**: Convert to simplest form before validation

**日本語:**

1. **サーバー側で検証**: クライアント側の検証のみを信頼しない
2. **ホワイトリストを使用**: 禁止されるものではなく許可されるものを定義
3. **データ型を検証**: 正しい型（文字列、数値等）を確保
4. **長さを検証**: 最小および最大長を強制
5. **フォーマットを検証**: 正規表現または検証ライブラリを使用
6. **範囲を検証**: 数値入力の場合
7. **入力を正規化**: 検証前に最も単純な形式に変換

**✅ Good Example:**
```typescript
// Using Joi for validation
import Joi from 'joi';

const userSchema = Joi.object({
  username: Joi.string()
    .alphanum()
    .min(3)
    .max(30)
    .required(),
    
  email: Joi.string()
    .email()
    .required(),
    
  age: Joi.number()
    .integer()
    .min(13)
    .max(120)
    .required(),
    
  password: Joi.string()
    .pattern(new RegExp('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$'))
    .required()
    .messages({
      'string.pattern.base': 'Password must contain uppercase, lowercase, number, and special character'
    }),
    
  website: Joi.string()
    .uri()
    .optional()
});

// Validate request
const validateUserInput = (req, res, next) => {
  const { error, value } = userSchema.validate(req.body, {
    abortEarly: false, // Return all errors
    stripUnknown: true // Remove unknown properties
  });
  
  if (error) {
    return res.status(400).json({
      errors: error.details.map(d => ({
        field: d.path.join('.'),
        message: d.message
      }))
    });
  }
  
  req.validatedBody = value;
  next();
};
```

---

## 3. Output Encoding / 出力エンコーディング

### XSS Prevention / XSS対策

**English:**

**Cross-Site Scripting (XSS)** occurs when untrusted data is included in web pages without proper validation or escaping.

**Types:**
- **Stored XSS**: Malicious script stored in database
- **Reflected XSS**: Script reflected from request
- **DOM-based XSS**: Vulnerability in client-side code

**Mitigations:**
- Encode output based on context (HTML, JavaScript, CSS, URL)
- Use Content Security Policy (CSP)
- Use frameworks that auto-escape (React, Angular)
- Never use `innerHTML` with user data
- Validate and sanitize inputs

**日本語:**

**クロスサイトスクリプティング（XSS）**は、信頼できないデータが適切な検証やエスケープなしにWebページに含まれる場合に発生します。

**種類:**
- **Stored XSS**: データベースに保存された悪意あるスクリプト
- **Reflected XSS**: リクエストから反映されたスクリプト
- **DOM-based XSS**: クライアント側コードの脆弱性

**対策:**
- コンテキストに基づいて出力をエンコード（HTML、JavaScript、CSS、URL）
- コンテンツセキュリティポリシー（CSP）を使用
- 自動エスケープするフレームワークを使用（React、Angular）
- ユーザーデータで`innerHTML`を使用しない
- 入力を検証およびサニタイズ

**✅ Good Example:**
```javascript
// React - Auto-escapes by default
const UserProfile = ({ name }) => {
  return <div>Hello, {name}!</div>; // Automatically escaped
};

// Using DOMPurify for sanitization
import DOMPurify from 'dompurify';

const displayUserContent = (htmlContent) => {
  const clean = DOMPurify.sanitize(htmlContent, {
    ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a'],
    ALLOWED_ATTR: ['href']
  });
  
  return <div dangerouslySetInnerHTML={{ __html: clean }} />;
};

// Template literals with encoding
import he from 'he';

const renderTemplate = (username) => {
  const encoded = he.encode(username);
  return `<div>Welcome, ${encoded}!</div>`;
};
```

**❌ Bad Example:**
```javascript
// NEVER DO THIS - XSS vulnerability
const displayUserInput = (input) => {
  document.getElementById('output').innerHTML = input; // Dangerous!
};

// User input: <img src=x onerror=alert('XSS')>
displayUserInput(userInput); // Script executes!

// Eval with user data
eval(userInput); // Extremely dangerous!

// Dynamic script creation
const script = document.createElement('script');
script.textContent = userInput; // XSS!
document.body.appendChild(script);
```

---

## 4. Authentication Best Practices / 認証ベストプラクティス

### Password Management / パスワード管理

**✅ Good Practices:**

```python
# Secure password requirements
PASSWORD_MIN_LENGTH = 12
PASSWORD_REQUIRE_UPPERCASE = True
PASSWORD_REQUIRE_LOWERCASE = True
PASSWORD_REQUIRE_NUMBERS = True
PASSWORD_REQUIRE_SPECIAL = True

# Password history (prevent reuse)
PASSWORD_HISTORY_COUNT = 5

# Password expiry
PASSWORD_MAX_AGE_DAYS = 90

# Failed login attempts
MAX_LOGIN_ATTEMPTS = 5
LOCKOUT_DURATION_MINUTES = 30
```

### Multi-Factor Authentication (MFA) / 多要素認証

```typescript
// TOTP-based MFA
import speakeasy from 'speakeasy';
import QRCode from 'qrcode';

const setupMFA = async (userId: string) => {
  // Generate secret
  const secret = speakeasy.generateSecret({
    name: `MyApp (${userId})`,
    issuer: 'MyApp'
  });
  
  // Generate QR code for user
  const qrCodeUrl = await QRCode.toDataURL(secret.otpauth_url);
  
  // Store secret securely (encrypted)
  await saveEncryptedSecret(userId, secret.base32);
  
  return {
    secret: secret.base32,
    qrCode: qrCodeUrl
  };
};

const verifyMFAToken = (userId: string, token: string) => {
  const secret = await getDecryptedSecret(userId);
  
  return speakeasy.totp.verify({
    secret,
    encoding: 'base32',
    token,
    window: 2 // Allow 1-minute time drift
  });
};
```

---

## 5. API Security / APIセキュリティ

### API Authentication / API認証

**✅ Good Example - JWT with Refresh Tokens:**

```typescript
import jwt from 'jsonwebtoken';

// Generate tokens
const generateTokens = (userId: string) => {
  const accessToken = jwt.sign(
    { userId, type: 'access' },
    process.env.JWT_SECRET!,
    { expiresIn: '15m' } // Short-lived
  );
  
  const refreshToken = jwt.sign(
    { userId, type: 'refresh' },
    process.env.JWT_REFRESH_SECRET!,
    { expiresIn: '7d' } // Longer-lived
  );
  
  return { accessToken, refreshToken };
};

// Verify access token
const verifyAccessToken = (token: string) => {
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET!);
    if (decoded.type !== 'access') {
      throw new Error('Invalid token type');
    }
    return decoded;
  } catch (error) {
    throw new Error('Invalid or expired token');
  }
};

// Refresh access token
const refreshAccessToken = async (refreshToken: string) => {
  const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET!);
  
  // Check if refresh token is revoked
  const isRevoked = await checkTokenRevocation(refreshToken);
  if (isRevoked) {
    throw new Error('Refresh token revoked');
  }
  
  const { accessToken, refreshToken: newRefreshToken } = 
    generateTokens(decoded.userId);
  
  // Optionally revoke old refresh token (rotation)
  await revokeRefreshToken(refreshToken);
  
  return { accessToken, refreshToken: newRefreshToken };
};
```

### Rate Limiting / レート制限

```typescript
import rateLimit from 'express-rate-limit';
import RedisStore from 'rate-limit-redis';

// General API rate limit
const apiLimiter = rateLimit({
  store: new RedisStore({ client: redisClient }),
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // 100 requests per window
  message: 'Too many requests from this IP'
});

// Strict rate limit for authentication
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 5,
  skipSuccessfulRequests: true // Only count failed attempts
});

app.use('/api/', apiLimiter);
app.use('/api/auth/', authLimiter);
```

---

## 6. Checklist / チェックリスト

### Pre-Deployment Security Checklist / デプロイ前セキュリティチェックリスト

**Authentication & Authorization / 認証と認可:**
- [ ] MFA implemented for sensitive accounts
- [ ] Password strength requirements enforced (min 12 chars, complexity)
- [ ] Bcrypt/Argon2 used for password hashing (cost factor ≥ 12)
- [ ] Session tokens are cryptographically random
- [ ] Sessions expire after inactivity (max 30 minutes)
- [ ] Logout invalidates session tokens
- [ ] Authorization checked on server-side for every request
- [ ] Principle of least privilege applied

**Input Validation / 入力検証:**
- [ ] All inputs validated on server-side
- [ ] Allowlist validation used where possible
- [ ] SQL injection prevented (parameterized queries)
- [ ] XSS prevented (output encoding)
- [ ] CSRF protection implemented
- [ ] File upload validation (type, size, content)
- [ ] URL validation for SSRF prevention

**Cryptography / 暗号化:**
- [ ] TLS 1.3 enforced for all connections
- [ ] Sensitive data encrypted at rest (AES-256)
- [ ] Secrets stored in environment variables or secret managers
- [ ] No hardcoded credentials in code
- [ ] Proper key rotation implemented

**Logging & Monitoring / ログと監視:**
- [ ] Authentication events logged
- [ ] Authorization failures logged
- [ ] Security events trigger alerts
- [ ] Logs don't contain sensitive data (passwords, tokens)
- [ ] Log integrity protection implemented

**Dependencies / 依存関係:**
- [ ] All dependencies up to date
- [ ] Dependency vulnerability scanning automated
- [ ] No dependencies with known critical vulnerabilities
- [ ] Software Bill of Materials (SBOM) generated

**Configuration / 設定:**
- [ ] Security headers configured (CSP, HSTS, X-Frame-Options)
- [ ] CORS properly configured (not allow *)
- [ ] Default accounts disabled/removed
- [ ] Verbose error messages disabled in production
- [ ] Debug mode disabled in production

---

## References / 参考資料

**English:**
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [SANS Top 25 Software Errors](https://www.sans.org/top25-software-errors/)

**日本語:**
- OWASP Top 10
- OWASP チートシートシリーズ
- CWE Top 25
- SANS Top 25ソフトウェアエラー

### Related Standards / 関連標準
- [07-security-compliance/README.md](../README.md)
- [07-security-compliance/security-checklist.md](./security-checklist.md)
- [07-security-compliance/data-protection.md](./data-protection.md)

---

## Change Log / 変更履歴

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0.0 | 2024-10-24 | Initial version | Security Team |

---

**Document Owner / ドキュメント所有者**: Security Team  
**Review Cycle / レビューサイクル**: Quarterly / 四半期ごと  
**Next Review Date / 次回レビュー日**: 2025-01-24
