# セキュリティアーキテクチャ標準

## ドキュメント情報

- **バージョン**: 1.0.0
- **最終更新日**: 2025-10-24
- **ステータス**: 有効
- **所有者**: セキュリティチーム / アーキテクチャチーム
- **カテゴリ**: アーキテクチャ標準

## 目次

1. [概要](#概要)
2. [セキュリティアーキテクチャの原則](#セキュリティアーキテクチャの原則)
3. [多層防御](#多層防御)
4. [ゼロトラストアーキテクチャ](#ゼロトラストアーキテクチャ)
5. [ネットワークセキュリティ](#ネットワークセキュリティ)
6. [アプリケーションセキュリティ](#アプリケーションセキュリティ)
7. [データセキュリティ](#データセキュリティ)
8. [ID・アクセス管理](#idアクセス管理)
9. [クラウドセキュリティアーキテクチャ](#クラウドセキュリティアーキテクチャ)
10. [セキュリティ監視とインシデント対応](#セキュリティ監視とインシデント対応)
11. [コンプライアンスとガバナンス](#コンプライアンスとガバナンス)
12. [セキュリティアーキテクチャパターン](#セキュリティアーキテクチャパターン)

---

## 概要

### 目的

本ドキュメントは、組織の資産、データ、システムをサイバー脅威から保護するための包括的なセキュリティアーキテクチャ標準を確立します。適切に設計されたセキュリティアーキテクチャは、複数層の防御を提供し、システム設計のあらゆる側面にセキュリティを統合します。

### 適用範囲

- **予防的セキュリティ**: セキュリティインシデントを防止する対策
- **検知的セキュリティ**: セキュリティ脅威を検知するメカニズム
- **対応的セキュリティ**: セキュリティイベントに対応する手順
- **復旧的セキュリティ**: セキュリティインシデントから復旧するプロセス

### 主要原則

1. **セキュリティバイデザイン**: 最初からセキュリティを統合
2. **多層防御**: 複数層のセキュリティ制御
3. **最小権限**: 必要最小限のアクセス権
4. **ゼロトラスト**: 信頼せず、常に検証
5. **フェイルセキュア**: システムは安全な状態で失敗
6. **セキュリティの透明性**: 可視化されたセキュリティ態勢
7. **継続的監視**: 常時稼働のセキュリティ監視

---

## セキュリティアーキテクチャの原則

### 1. セキュリティバイデザイン

**後付けではなく、最初からセキュリティを統合**

#### 設計フェーズのセキュリティ考慮事項

```
要件定義 → 脅威モデリング → セキュア設計 → 実装 → テスト → デプロイ
    ↓           ↓              ↓          ↓       ↓        ↓
セキュリティ   セキュリティ    セキュリティ   セキュア  セキュリティ セキュリティ
  要件         分析           パターン      コーディング テスト     監視
```

**実装チェックリスト**:
- ✅ 設計フェーズでの脅威モデリング
- ✅ セキュリティ要件のドキュメント化
- ✅ セキュアな設計パターンの選択
- ✅ 実装前のセキュリティレビュー
- ✅ CI/CDパイプラインでのセキュリティテスト
- ✅ デプロイ後のセキュリティ監視

### 2. 多層防御（Defense in Depth）

**複数層のセキュリティ制御**

```
┌─────────────────────────────────────────┐
│         物理セキュリティ層               │  建物アクセス、サーバールーム
├─────────────────────────────────────────┤
│        ネットワークセキュリティ層        │  ファイアウォール、IDS/IPS、セグメンテーション
├─────────────────────────────────────────┤
│         境界セキュリティ層               │  WAF、APIゲートウェイ、DDoS保護
├─────────────────────────────────────────┤
│      アプリケーションセキュリティ層      │  入力検証、認証
├─────────────────────────────────────────┤
│         データセキュリティ層             │  暗号化、DLP、マスキング
├─────────────────────────────────────────┤
│       エンドポイントセキュリティ層       │  アンチウイルス、EDR、デバイス管理
└─────────────────────────────────────────┘
```

**原則**: 1つの層が侵害されても、他の層が保護を提供します。

### 3. 最小権限の原則

**職務遂行に必要な最小限のアクセス権を付与**

```typescript
// 例: 最小権限によるロールベースアクセス制御
const userPermissions = {
  viewer: ['read:documents'],
  editor: ['read:documents', 'write:documents'],
  admin: ['read:documents', 'write:documents', 'delete:documents', 'manage:users']
};

function checkPermission(user: User, action: string): boolean {
  const userRole = user.role;
  const allowedActions = userPermissions[userRole] || [];
  return allowedActions.includes(action);
}

// 使用例
if (checkPermission(currentUser, 'delete:documents')) {
  // 削除を許可
} else {
  throw new ForbiddenError('権限が不足しています');
}
```

**実装ガイドライン**:
- デフォルトで全アクセスを拒否
- 明示的に権限を付与
- 定期的に権限をレビュー（四半期ごと）
- 不要になったらすぐにアクセスを削除
- 管理タスクには時間制限付きの昇格アクセスを使用

### 4. フェイルセキュアの原則

**システムは安全な状態で失敗すべき**

```typescript
// 例: セキュアな失敗処理
async function authenticateUser(credentials: Credentials): Promise<User> {
  try {
    const user = await userRepository.findByEmail(credentials.email);
    
    if (!user) {
      // フェイルセキュア: ユーザーの存在を明かさない
      throw new AuthenticationError('認証情報が無効です');
    }
    
    const isValid = await verifyPassword(credentials.password, user.passwordHash);
    
    if (!isValid) {
      // フェイルセキュア: 同じエラーメッセージ
      throw new AuthenticationError('認証情報が無効です');
    }
    
    return user;
    
  } catch (error) {
    // フェイルセキュア: エラーをログに記録するが一般的なメッセージを返す
    logger.error('認証エラー', { error });
    throw new AuthenticationError('認証情報が無効です');
  }
}
```

**フェイルセキュアの例**:
- 認証失敗 → アクセス拒否（許可しない）
- 認可チェック失敗 → アクセス拒否
- 暗号化失敗 → 平文でデータを送信しない
- 証明書検証失敗 → 接続をブロック
- データベース接続失敗 → エラーを返し、データを公開しない

### 5. セキュリティの透明性

**可視化された監査可能なセキュリティ態勢**

```yaml
# 例: セキュリティログと監視
security_events:
  - authentication_attempts  # 認証試行
  - authorization_failures   # 認可失敗
  - data_access_logs        # データアクセスログ
  - configuration_changes   # 設定変更
  - privilege_escalations   # 権限昇格
  - suspicious_activities   # 疑わしいアクティビティ

audit_trail:
  retention: 1_year          # 保持期間: 1年
  immutable: true            # 不変
  encryption: at_rest        # 保存時暗号化
  access_control: security_team_only  # アクセス制御: セキュリティチームのみ
```

---

## 多層防御

### 第1層: 物理セキュリティ

**物理的資産とインフラの保護**

#### データセンターセキュリティ
- **アクセス制御**: 生体認証、マントラップドア
- **監視**: 24時間365日のCCTV監視
- **環境制御**: 消火設備、温度監視
- **資産追跡**: 機器のRFIDタグ
- **訪問者管理**: サインイン記録、エスコート要件

#### オフィスセキュリティ
- **バッジアクセス**: アクセスレベル付き従業員バッジ
- **クリーンデスクポリシー**: 機密情報を放置しない
- **画面ロック**: 5分間のアイドル後に自動ロック
- **安全な廃棄**: 機密文書のシュレッダー処理

### 第2層: ネットワークセキュリティ

**ネットワークインフラとトラフィックの保護**

#### ネットワークセグメンテーション

```
┌───────────────────────────────────────────────────────┐
│                    インターネット                      │
└─────────────────────┬─────────────────────────────────┘
                      │
                      ▼
            ┌─────────────────────┐
            │  ファイアウォール/WAF │
            └──────────┬──────────┘
                       │
           ┌───────────┴──────────┐
           │                      │
           ▼                      ▼
    ┌─────────────┐      ┌─────────────┐
    │   DMZ       │      │   プライベート│
    │ (公開)      │      │   ネットワーク│
    └─────────────┘      └──────┬──────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
                    ▼                       ▼
            ┌──────────────┐      ┌──────────────┐
            │ アプリケーション│      │ データベース  │
            │   サブネット  │      │  サブネット   │
            │ 10.0.1.0/24  │      │ 10.0.2.0/24  │
            └──────────────┘      └──────────────┘
```

**セグメンテーションルール**:
- インターネット → DMZ: HTTPS (443), HTTP (80)
- DMZ → アプリケーションサブネット: カスタムアプリポート
- アプリケーション → データベース: データベースポートのみ (3306, 5432)
- インターネット → データベースへの直接接続なし

#### ファイアウォールルール

```yaml
# 例: ファイアウォール設定（宣言的）
firewall_rules:
  # デフォルトで全て拒否
  default_policy: DENY
  
  # 特定のインバウンドトラフィックを許可
  inbound:
    - name: "HTTPS許可"
      protocol: TCP
      port: 443
      source: 0.0.0.0/0
      destination: dmz_subnet
      action: ALLOW
    
    - name: "VPNからのSSHのみ許可"
      protocol: TCP
      port: 22
      source: vpn_subnet
      destination: management_subnet
      action: ALLOW
  
  # 特定のアウトバウンドトラフィックを許可
  outbound:
    - name: "アプリからデータベースへ"
      protocol: TCP
      port: 5432
      source: app_subnet
      destination: db_subnet
      action: ALLOW
    
    - name: "アウトバウンドHTTPS許可"
      protocol: TCP
      port: 443
      source: app_subnet
      destination: 0.0.0.0/0
      action: ALLOW
```

#### 侵入検知・防止（IDS/IPS）

```yaml
# 例: Snort IDSルール
ids_rules:
  - alert: "SQLインジェクション試行"
    pattern: "(union.*select|select.*from)"
    action: BLOCK
    severity: HIGH
  
  - alert: "クロスサイトスクリプティング（XSS）"
    pattern: "<script[^>]*>.*?</script>"
    action: BLOCK
    severity: HIGH
  
  - alert: "ポートスキャン検出"
    condition: "connection_attempts > 100 in 60 seconds"
    action: ALERT
    severity: MEDIUM
```

### 第3層: 境界セキュリティ

**アプリケーション境界の保護**

#### Webアプリケーションファイアウォール（WAF）

```yaml
# 例: WAF設定（AWS WAF）
waf_rules:
  - name: "レート制限"
    type: RATE_BASED
    limit: 2000  # 5分間に2000リクエスト
    action: BLOCK
  
  - name: "SQLインジェクション保護"
    type: SQL_INJECTION
    sensitivity: HIGH
    action: BLOCK
  
  - name: "XSS保護"
    type: XSS
    action: BLOCK
  
  - name: "地理ブロッキング"
    type: GEO_MATCH
    blocked_countries: ["CN", "RU", "KP"]
    action: BLOCK
  
  - name: "既知の悪意ある入力"
    type: STRING_MATCH
    patterns:
      - "../../../etc/passwd"
      - "'; DROP TABLE"
      - "<script>alert("
    action: BLOCK
```

#### APIゲートウェイセキュリティ

```yaml
# 例: APIゲートウェイセキュリティ設定
api_gateway:
  authentication:
    type: JWT
    issuer: "https://auth.example.com"
    audience: "api.example.com"
  
  rate_limiting:
    default: 1000  # 1分あたりのリクエスト数
    per_user: 100
    burst: 50
  
  cors:
    allowed_origins:
      - "https://app.example.com"
      - "https://admin.example.com"
    allowed_methods: ["GET", "POST", "PUT", "DELETE"]
    allowed_headers: ["Authorization", "Content-Type"]
    max_age: 3600
  
  request_validation:
    enabled: true
    schema_validation: true
    max_body_size: "10MB"
  
  response_headers:
    - "X-Content-Type-Options: nosniff"
    - "X-Frame-Options: DENY"
    - "X-XSS-Protection: 1; mode=block"
    - "Strict-Transport-Security: max-age=31536000"
```

### 第4層: アプリケーションセキュリティ

**アプリケーションコードとロジックの保護**

#### 入力検証

```typescript
// 例: 包括的な入力検証
import { z } from 'zod';

const UserInputSchema = z.object({
  email: z.string().email().max(255),
  name: z.string().min(2).max(100).regex(/^[a-zA-Z\s]+$/),
  age: z.number().int().min(0).max(150),
  phone: z.string().regex(/^\+?[1-9]\d{1,14}$/),
  website: z.string().url().optional(),
});

function validateUserInput(input: unknown): User {
  try {
    return UserInputSchema.parse(input);
  } catch (error) {
    if (error instanceof z.ZodError) {
      throw new ValidationError(error.errors);
    }
    throw error;
  }
}

// 使用例
app.post('/api/users', (req, res) => {
  try {
    const validatedData = validateUserInput(req.body);
    // 検証済みデータを処理
  } catch (error) {
    res.status(400).json({ error: '無効な入力です' });
  }
});
```

#### 出力エンコーディング

```typescript
// 例: XSS防止のための出力エンコーディング
import DOMPurify from 'dompurify';
import { escape } from 'html-escaper';

// HTMLコンテキスト
function sanitizeHTML(userInput: string): string {
  return DOMPurify.sanitize(userInput, {
    ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a'],
    ALLOWED_ATTR: ['href']
  });
}

// JavaScriptコンテキスト
function escapeJavaScript(userInput: string): string {
  return userInput
    .replace(/\\/g, '\\\\')
    .replace(/'/g, "\\'")
    .replace(/"/g, '\\"')
    .replace(/\n/g, '\\n')
    .replace(/\r/g, '\\r')
    .replace(/\t/g, '\\t');
}

// URLコンテキスト
function encodeURL(userInput: string): string {
  return encodeURIComponent(userInput);
}

// テンプレートでの使用
const safeHTML = sanitizeHTML(userGeneratedContent);
const safeURL = encodeURL(userProvidedURL);
```

#### セキュアなセッション管理

```typescript
// 例: セキュアなセッション設定
import session from 'express-session';
import RedisStore from 'connect-redis';

app.use(session({
  store: new RedisStore({ client: redisClient }),
  secret: process.env.SESSION_SECRET,  // 強力なランダムシークレット
  name: 'sessionId',  // デフォルト名を使用しない
  resave: false,
  saveUninitialized: false,
  cookie: {
    secure: true,        // HTTPSのみ
    httpOnly: true,      // JavaScriptアクセス不可
    maxAge: 3600000,     // 1時間
    sameSite: 'strict',  // CSRF保護
    domain: '.example.com'
  }
}));

// 権限昇格時のセッションローテーション
function escalatePrivileges(req: Request): void {
  const oldSession = req.session;
  req.session.regenerate(() => {
    // 必要なデータを保持
    req.session.userId = oldSession.userId;
    req.session.elevated = true;
    req.session.elevatedAt = Date.now();
  });
}
```

### 第5層: データセキュリティ

**機密データの保護**

#### データ分類

| 分類 | 例 | 暗号化 | アクセス制御 | 保持期間 |
|------|-----|--------|--------------|----------|
| **公開** | マーケティング資料 | オプション | オープン | 無期限 |
| **内部** | 従業員ハンドブック | 推奨 | 従業員のみ | ポリシーに従う |
| **機密** | 顧客データ、契約 | 必須 | 知る必要性に基づく | 7年 |
| **制限** | PII、財務データ、機密情報 | 必須（強力） | 厳格なRBAC | 規制に従う |

#### 暗号化標準

**保存時の暗号化**:
```yaml
encryption_at_rest:
  algorithm: AES-256-GCM
  key_management: AWS KMS / Azure Key Vault
  key_rotation: 90日ごと
  
  # データベース暗号化
  database:
    - フルディスク暗号化（LUKS、BitLocker）
    - 透過的データ暗号化（TDE）
    - PIIのカラムレベル暗号化
  
  # ファイルストレージ暗号化
  storage:
    - S3バケット暗号化（SSE-KMS）
    - Azure Blob Storage暗号化
    - 暗号化されたEBSボリューム
```

**転送時の暗号化**:
```yaml
encryption_in_transit:
  protocols:
    - TLS 1.3（推奨）
    - TLS 1.2（最小）
  
  cipher_suites:
    - TLS_AES_256_GCM_SHA384
    - TLS_CHACHA20_POLY1305_SHA256
    - TLS_AES_128_GCM_SHA256
  
  certificate:
    type: X.509
    key_size: 4096ビット（RSA）または256ビット（ECDSA）
    signature_algorithm: SHA-256
    validity: 最大1年
    
  # 安全でないプロトコルを無効化
  disabled:
    - SSL 2.0、SSL 3.0
    - TLS 1.0、TLS 1.1
    - 弱い暗号（RC4、DES、3DES）
```

#### データ損失防止（DLP）

```yaml
dlp_rules:
  - name: "PII流出防止"
    data_types: ["SSN", "クレジットカード", "メール", "電話"]
    channels: ["email", "file_upload", "copy_paste"]
    action: BLOCK
    
  - name: "機密文書共有"
    classification: CONFIDENTIAL
    channels: ["external_email", "public_link"]
    action: REQUIRE_APPROVAL
    
  - name: "データベースエクスポート監視"
    data_types: ["customer_data", "financial_records"]
    action: ALERT_AND_LOG
```

### 第6層: エンドポイントセキュリティ

**エンドユーザーデバイスの保護**

```yaml
endpoint_security:
  antivirus:
    provider: "CrowdStrike / Microsoft Defender"
    real_time_protection: true
    scheduled_scans: daily
  
  edr:  # エンドポイント検知・対応
    behavioral_analysis: true
    threat_hunting: enabled
    automated_response: true
  
  device_management:
    mdm: "Intune / Jamf"
    policies:
      - disk_encryption: required
      - screen_lock: 5_minutes
      - password_complexity: enforced
      - automatic_updates: enabled
      - remote_wipe: available
  
  application_control:
    whitelist_mode: true
    allowed_applications: verified_only
    block_unsigned: true
```

---

## ゼロトラストアーキテクチャ

### コア原則

1. **信頼せず、常に検証**: すべてのアクセス要求を検証
2. **最小権限アクセス**: 必要最小限の権限
3. **侵害を前提**: 攻撃者が既に内部にいると仮定して設計
4. **明示的に検証**: 認可に利用可能なすべてのデータを使用
5. **マイクロセグメンテーション**: ネットワークを小さなゾーンに分割

### ゼロトラストモデル

```
┌─────────────────────────────────────────────────────────┐
│                 ユーザー/デバイス                        │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │  ID検証        │  多要素認証
              │                │  デバイスヘルスチェック
              └────────┬───────┘
                       │
                       ▼
              ┌────────────────┐
              │  ポリシー      │  コンテキスト認識ポリシー
              │  エンジン      │  リスクベースの判断
              └────────┬───────┘
                       │
                       ▼
              ┌────────────────┐
              │  アクセス      │  ジャストインタイムアクセス
              │  ブローカー    │  時間制限付きセッション
              └────────┬───────┘
                       │
                       ▼
              ┌────────────────┐
              │  リソース      │  アプリケーション/データ
              │  (保護対象)    │  継続的監視
              └────────────────┘
```

### 実装例

```typescript
// 例: ゼロトラストアクセス制御
interface AccessRequest {
  user: User;
  device: Device;
  resource: Resource;
  action: Action;
  context: Context;
}

interface Context {
  ipAddress: string;
  location: GeoLocation;
  time: Date;
  networkTrust: 'trusted' | 'untrusted';
}

async function evaluateAccess(request: AccessRequest): Promise<AccessDecision> {
  // 1. IDを検証
  const identityVerified = await verifyIdentity(request.user);
  if (!identityVerified) {
    return { allowed: false, reason: 'ID検証失敗' };
  }
  
  // 2. デバイスヘルスをチェック
  const deviceTrusted = await checkDeviceHealth(request.device);
  if (!deviceTrusted) {
    return { allowed: false, reason: 'デバイスが信頼されていません' };
  }
  
  // 3. ポリシーを評価
  const policy = await getPolicyForResource(request.resource);
  const policyResult = evaluatePolicy(policy, request);
  
  // 4. リスク評価
  const riskScore = calculateRiskScore(request);
  
  if (riskScore > RISK_THRESHOLD) {
    // ステップアップ認証を要求
    return {
      allowed: false,
      reason: '高リスク',
      requireStepUp: true,
      stepUpMethod: 'mfa'
    };
  }
  
  // 5. 時間制限付きアクセスを付与
  if (policyResult.allowed) {
    return {
      allowed: true,
      expiresAt: Date.now() + (15 * 60 * 1000),  // 15分
      permissions: policyResult.permissions
    };
  }
  
  return { allowed: false, reason: 'ポリシーが拒否' };
}
```

---

## ネットワークセキュリティ

### セキュアなネットワーク設計

```
┌─────────────────────────────────────────────────┐
│           インターネット（信頼できない）          │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
            ┌──────────────────┐
            │  エッジファイアウォール│
            │  + DDoS保護       │
            └─────────┬────────┘
                      │
                      ▼
            ┌──────────────────┐
            │   DMZゾーン      │
            │  (Webサーバー)   │
            └─────────┬────────┘
                      │
                      ▼
            ┌──────────────────┐
            │ 内部ファイアウォール│
            └─────────┬────────┘
                      │
          ┌───────────┴───────────┐
          │                       │
          ▼                       ▼
   ┌─────────────┐        ┌─────────────┐
   │ アプリケーション│        │  管理      │
   │   ゾーン    │        │  ゾーン     │
   └──────┬──────┘        └─────────────┘
          │
          ▼
   ┌─────────────┐
   │ データベース │
   │   ゾーン    │
   └─────────────┘
```

### VPN設定

```yaml
vpn_configuration:
  protocol: IKEv2 / OpenVPN
  encryption: AES-256-GCM
  authentication:
    type: certificate + MFA
    mfa_methods: ["TOTP", "プッシュ通知"]
  
  split_tunneling: false  # すべてのトラフィックをVPN経由
  
  access_policies:
    - user_group: "developers"
      allowed_networks: ["10.0.1.0/24", "10.0.2.0/24"]
      
    - user_group: "support"
      allowed_networks: ["10.0.3.0/24"]
      
  session:
    timeout: 8_hours
    idle_timeout: 30_minutes
    concurrent_sessions: 1
```

---

## アプリケーションセキュリティ

### セキュア開発ライフサイクル（SDL）

```
要件定義 → 設計 → 実装 → テスト → デプロイ → 保守
    ↓      ↓     ↓      ↓       ↓       ↓
セキュリティ 脅威  セキュア セキュリティ セキュリティ パッチ
  要件    モデル コーディング テスト     強化      管理
```

### OWASP Top 10 対策

| 脆弱性 | 緩和戦略 |
|--------|----------|
| **A01: アクセス制御の不備** | RBAC実装、デフォルト拒否、サーバー側検証 |
| **A02: 暗号化の失敗** | TLS 1.3使用、PII暗号化、安全な鍵管理 |
| **A03: インジェクション** | パラメータ化クエリ、入力検証、出力エスケープ |
| **A04: 安全でない設計** | 脅威モデリング、セキュアな設計パターン |
| **A05: セキュリティ設定ミス** | セキュアなデフォルト、自動化された強化、最小攻撃面 |
| **A06: 脆弱なコンポーネント** | SCAスキャン、パッチ管理、バージョン管理 |
| **A07: 認証の失敗** | MFA、パスワードポリシー、セッション管理 |
| **A08: データ整合性の失敗** | デジタル署名、整合性チェック、セキュアなパイプライン |
| **A09: ログ記録の失敗** | 包括的なログ、SIEM統合、アラート |
| **A10: SSRF** | URLホワイトリスト、ネットワークセグメンテーション、入力検証 |

### セキュアコーディング例

```typescript
// 例: セキュアなAPIエンドポイント実装
import { Router } from 'express';
import { body, validationResult } from 'express-validator';
import rateLimit from 'express-rate-limit';

const router = Router();

// レート制限
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,  // 15分
  max: 100  // IPごとに最大100リクエスト
});

router.post('/api/users',
  limiter,  // レート制限
  
  // 認証
  authenticateJWT,
  
  // 認可
  requireRole(['admin']),
  
  // 入力検証
  body('email').isEmail().normalizeEmail(),
  body('name').trim().isLength({ min: 2, max: 100 }).escape(),
  body('age').isInt({ min: 0, max: 150 }),
  
  async (req, res) => {
    // 入力を検証
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }
    
    try {
      // パラメータ化クエリ（SQLインジェクション防止）
      const user = await db.query(
        'INSERT INTO users (email, name, age) VALUES ($1, $2, $3) RETURNING *',
        [req.body.email, req.body.name, req.body.age]
      );
      
      // セキュリティイベントをログ
      auditLog.info('ユーザー作成', {
        userId: req.user.id,
        newUserId: user.id,
        ipAddress: req.ip
      });
      
      // サニタイズされたレスポンスを返す
      res.status(201).json({
        id: user.id,
        email: user.email,
        name: user.name
      });
      
    } catch (error) {
      // 一般的なエラーメッセージ（詳細を漏らさない）
      logger.error('ユーザー作成失敗', { error });
      res.status(500).json({ error: '内部サーバーエラー' });
    }
  }
);
```

---

## データセキュリティ

### データライフサイクルセキュリティ

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  作成    │ →  │  保存    │ →  │  使用    │ →  │ アーカイブ│ →  │  破棄    │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
     ↓               ↓               ↓               ↓               ↓
  機密性     保存時の        アクセス        暗号化された      安全な
  分類       暗号化          制御           バックアップ      削除
```

### データベースセキュリティ

```sql
-- 例: データベースセキュリティのベストプラクティス

-- 1. 最小権限の原則
CREATE ROLE app_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO app_readonly;

CREATE ROLE app_readwrite;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO app_readwrite;

-- 2. 行レベルセキュリティ
ALTER TABLE customer_data ENABLE ROW LEVEL SECURITY;

CREATE POLICY customer_isolation ON customer_data
  USING (customer_id = current_setting('app.current_customer_id')::integer);

-- 3. 監査ログ
CREATE TABLE audit_log (
  id SERIAL PRIMARY KEY,
  table_name VARCHAR(100),
  operation VARCHAR(10),
  user_name VARCHAR(100),
  timestamp TIMESTAMP DEFAULT NOW(),
  old_data JSONB,
  new_data JSONB
);

CREATE FUNCTION audit_trigger() RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO audit_log (table_name, operation, user_name, old_data, new_data)
  VALUES (TG_TABLE_NAME, TG_OP, current_user, row_to_json(OLD), row_to_json(NEW));
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4. 機密列の暗号化
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 保存前にデータを暗号化
INSERT INTO users (email, ssn_encrypted)
VALUES (
  'john@example.com',
  pgp_sym_encrypt('123-45-6789', 'encryption_key')
);

-- 取得時にデータを復号化
SELECT email, pgp_sym_decrypt(ssn_encrypted, 'encryption_key') as ssn
FROM users;
```

---

## ID・アクセス管理

### IAMアーキテクチャ

```
┌────────────────────────────────────────────────┐
│        IDプロバイダー（IdP）                    │
│  (Okta, Azure AD, Auth0, AWS Cognito)          │
└─────────────────────┬──────────────────────────┘
                      │
          ┌───────────┴───────────┐
          │                       │
          ▼                       ▼
    ┌──────────┐           ┌──────────┐
    │   SAML   │           │  OAuth/  │
    │   SSO    │           │  OIDC    │
    └────┬─────┘           └────┬─────┘
         │                      │
         └──────────┬───────────┘
                    │
                    ▼
          ┌──────────────────┐
          │  認可サービス     │
          └─────────┬────────┘
                    │
                    ▼
          ┌──────────────────┐
          │  アプリケーション │
          │  リソース        │
          └──────────────────┘
```

### 多要素認証（MFA）

```typescript
// 例: MFA実装
async function login(credentials: Credentials): Promise<LoginResult> {
  // 1. ユーザー名とパスワードを検証
  const user = await authenticateCredentials(credentials);
  if (!user) {
    throw new AuthError('認証情報が無効です');
  }
  
  // 2. MFAが必要かチェック
  if (user.mfaEnabled) {
    // MFAチャレンジを送信
    const challenge = await sendMFAChallenge(user, 'totp');
    
    return {
      status: 'mfa_required',
      userId: user.id,
      challengeId: challenge.id,
      methods: ['totp', 'sms', 'email']
    };
  }
  
  // 3. セッションを作成
  const session = await createSession(user);
  return {
    status: 'success',
    token: session.token,
    user: sanitizeUser(user)
  };
}

async function verifyMFA(
  userId: string,
  challengeId: string,
  code: string
): Promise<SessionToken> {
  // MFAコードを検証
  const valid = await verifyTOTP(userId, code);
  
  if (!valid) {
    throw new AuthError('無効なMFAコードです');
  }
  
  // MFA成功後にセッションを作成
  const user = await getUser(userId);
  const session = await createSession(user);
  
  return session.token;
}
```

---

## クラウドセキュリティアーキテクチャ

### AWSセキュリティベストプラクティス

```yaml
aws_security:
  # ネットワークセキュリティ
  vpc:
    - 複数のアベイラビリティゾーンを使用
    - データベースはプライベートサブネット
    - ロードバランサーのみパブリックサブネット
    - VPC Flow Logs有効化
  
  # ID・アクセス
  iam:
    - ルートアカウントの使用禁止
    - 全ユーザーにMFA強制
    - ロールベースアクセス（長期認証情報なし）
    - 最小権限ポリシー
    - 定期的なアクセスレビュー
  
  # データ保護
  encryption:
    - S3バケット暗号化（SSE-KMS）
    - EBSボリューム暗号化
    - RDS保存時暗号化
    - 転送時は全てTLS
  
  # 監視
  cloudwatch:
    - CloudTrail有効化（全リージョン）
    - GuardDuty脅威検知
    - Security Hubコンプライアンス
    - Config設定監視
  
  # 追加サービス
  waf: "公開APIに有効化"
  shield: "DDoS保護"
  secrets_manager: "シークレットの自動ローテーション"
```

### Azureセキュリティベストプラクティス

```yaml
azure_security:
  # ネットワークセキュリティ
  networking:
    - ネットワークセキュリティグループ（NSG）
    - トラフィックフィルタリング用Azure Firewall
    - DDoS Protection Standard
    - PaaSサービス用Private Link
  
  # ID
  active_directory:
    - 条件付きアクセスポリシー
    - Privileged Identity Management（PIM）
    - Identity Protection
    - MFA強制
  
  # データ保護
  encryption:
    - シークレット用Azure Key Vault
    - Transparent Data Encryption（TDE）
    - Storage Service Encryption（SSE）
    - SQL用Always Encrypted
  
  # 監視
  security_center:
    - Security Center Standard有効化
    - 継続的評価
    - ジャストインタイムVM アクセス
    - 適応型アプリケーション制御
```

---

## セキュリティ監視とインシデント対応

### セキュリティ情報・イベント管理（SIEM）

```yaml
siem_configuration:
  log_sources:
    - アプリケーションログ
    - システムログ
    - ネットワークログ（ファイアウォール、IDS/IPS）
    - データベース監査ログ
    - クラウドプロバイダーログ
    - IDプロバイダーログ
  
  correlation_rules:
    - 複数回のログイン失敗
    - 権限昇格
    - 異常なデータアクセスパターン
    - 異常なネットワークトラフィック
    - 疑わしいAPIコール
  
  alerting:
    critical:
      - channels: ["pagerduty", "slack", "email"]
      - response_time: "15分"
    
    high:
      - channels: ["slack", "email"]
      - response_time: "1時間"
    
    medium:
      - channels: ["email"]
      - response_time: "4時間"
```

### インシデント対応計画

```
┌──────────────┐
│  検知        │  セキュリティアラート発動
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  分析        │  範囲と影響を判断
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  封じ込め    │  影響を受けたシステムを隔離
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  根絶        │  脅威を除去
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  復旧        │  通常運用を復元
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  教訓        │  インシデント後レビュー
└──────────────┘
```

---

## コンプライアンスとガバナンス

### コンプライアンスフレームワーク

| フレームワーク | 目的 | 主要要件 |
|---------------|------|----------|
| **SOC 2** | サービス組織 | セキュリティ、可用性、機密性 |
| **ISO 27001** | 情報セキュリティ | リスク管理、ISMS |
| **PCI DSS** | カード会員データ | カード会員データ保護 |
| **HIPAA** | 医療データ | PHI保護、プライバシー |
| **GDPR** | EUの個人データ | プライバシー、データ保護 |

### セキュリティガバナンス

```yaml
governance_structure:
  security_committee:
    chair: CISO
    members:
      - CTO
      - 法務顧問
      - コンプライアンス責任者
      - リスク管理者
    frequency: 月次
  
  responsibilities:
    - セキュリティポリシーの承認
    - リスク受容判断
    - インシデントレビュー
    - 予算配分
    - コンプライアンス監督
  
  reporting:
    - 取締役会（四半期）
    - 経営陣（月次）
    - 監査委員会（四半期）
```

---

## セキュリティアーキテクチャパターン

### マイクロサービスセキュリティパターン

```
┌─────────────────────────────────────────────┐
│        APIゲートウェイ（エッジ）             │
│  - 認証                                      │
│  - レート制限                                │
│  - リクエスト検証                            │
└──────────────────┬──────────────────────────┘
                   │
     ┌─────────────┼─────────────┐
     │             │             │
     ▼             ▼             ▼
┌─────────┐  ┌─────────┐  ┌─────────┐
│サービスA│  │サービスB│  │サービスC│
│         │  │         │  │         │
│ - JWT   │  │ - JWT   │  │ - JWT   │
│ - mTLS  │←─┤ - mTLS  │→─┤ - mTLS  │
└─────────┘  └─────────┘  └─────────┘
     │             │             │
     └─────────────┼─────────────┘
                   ▼
          ┌────────────────┐
          │ サービスメッシュ│
          │ (Istio/Linkerd)│
          │  - 暗号化      │
          │  - 認証        │
          │  - 可観測性    │
          └────────────────┘
```

### シークレット管理パターン

```yaml
# 例: Vaultによるシークレット管理
secrets_management:
  provider: HashiCorp Vault
  
  access_pattern:
    - アプリケーション起動
    - Vaultに認証（AppRole）
    - 短命トークンを受信
    - シークレットを取得
    - 期限前にトークンを更新
    - シークレットはメモリのみにキャッシュ
  
  secret_rotation:
    database_credentials: 7_days
    api_keys: 30_days
    certificates: 90_days
  
  audit:
    - すべてのシークレットアクセスをログ
    - 異常検知有効化
    - 疑わしいアクセスにアラート
```

---

## 付録

### セキュリティチェックリスト

**ネットワークセキュリティ**:
- [ ] ファイアウォールルールが文書化されレビュー済み
- [ ] ネットワークセグメンテーション実装済み
- [ ] IDS/IPSデプロイおよび監視中
- [ ] リモートアクセス用VPNにMFA設定

**アプリケーションセキュリティ**:
- [ ] すべてのユーザー入力に入力検証
- [ ] XSS防止のための出力エンコーディング
- [ ] パラメータ化クエリ（SQLインジェクションなし）
- [ ] セキュアなセッション管理
- [ ] HTTPS全面適用（TLS 1.2以上）

**データセキュリティ**:
- [ ] 機密度によるデータ分類
- [ ] 機密データの保存時暗号化
- [ ] 転送時暗号化（TLS）
- [ ] 定期バックアップのテスト
- [ ] 安全なデータ廃棄プロセス

**ID・アクセス**:
- [ ] 全ユーザーにMFA強制
- [ ] 最小権限アクセス
- [ ] 定期的なアクセスレビュー
- [ ] 自動化されたオフボーディング

**監視**:
- [ ] SIEMデプロイおよび設定済み
- [ ] セキュリティアラート定義済み
- [ ] インシデント対応計画文書化
- [ ] 定期的なセキュリティテスト（ペンテスト）

### 有用なリソース

- [NISTサイバーセキュリティフレームワーク](https://www.nist.gov/cyberframework)
- [OWASPアプリケーションセキュリティ](https://owasp.org/)
- [CISコントロール](https://www.cisecurity.org/controls)
- [SANSセキュリティリソース](https://www.sans.org/security-resources)
- [クラウドセキュリティアライアンス](https://cloudsecurityalliance.org/)

### 変更履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|----------|
| 1.0.0 | 2025-10-24 | 包括的なセキュリティアーキテクチャ標準の初版 |

---

**ドキュメント所有者**: セキュリティチーム / アーキテクチャチーム  
**レビュー頻度**: 四半期ごと  
**次回レビュー**: 2025-01-24
