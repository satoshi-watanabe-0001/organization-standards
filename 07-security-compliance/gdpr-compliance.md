# GDPR対応標準

**最終更新**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発チーム全体

---

## 📖 概要

このドキュメントは、**GDPR（EU一般データ保護規則）**への対応標準を定義します。欧州経済領域（EEA）内の個人データを扱うすべてのプロジェクトは、このガイドラインに準拠する必要があります。

**適用範囲**:
- EEA内の個人データを処理するすべてのシステム
- EEA在住者にサービスを提供するアプリケーション
- EEA在住者の行動をモニタリングするシステム

---

## 🎯 GDPR基本原則

### 1. 合法性・公平性・透明性（Lawfulness, Fairness, Transparency）
- データ処理の法的根拠を明確にする
- データ主体に対して処理目的を明示
- プライバシーポリシーの公開

### 2. 目的限定（Purpose Limitation）
- 収集目的を明確に定義
- 目的外利用の禁止
- 目的変更時の再同意取得

### 3. データ最小化（Data Minimisation）
- 必要最小限のデータのみ収集
- 過剰な情報収集の禁止

### 4. 正確性（Accuracy）
- データの正確性を維持
- 不正確なデータの訂正・削除

### 5. 保存期間制限（Storage Limitation）
- 保存期間の明確化
- 期間経過後の自動削除

### 6. 完全性・機密性（Integrity and Confidentiality）
- 技術的・組織的セキュリティ対策
- 不正アクセス・漏洩の防止

### 7. 説明責任（Accountability）
- コンプライアンスの証明責任
- 記録・監査証跡の保持

---

## 🔒 個人データの定義

### 直接識別情報
- 氏名、住所、電話番号
- メールアドレス
- 身分証明書番号（パスポート、運転免許証等）
- 銀行口座情報、クレジットカード情報

### 間接識別情報
- IPアドレス
- Cookie識別子
- 位置情報
- デバイスID
- 行動履歴データ

### 特別カテゴリの個人データ（機密性が高い）
- 人種・民族的出身
- 政治的意見
- 宗教的・哲学的信条
- 労働組合への加入
- 遺伝データ
- 生体認証データ
- 健康データ
- 性生活・性的指向

---

## ✅ GDPR対応チェックリスト

### Phase 1: 設計・開発前

#### データマッピング

```markdown
□ 収集する個人データの種類を特定
□ データ収集の法的根拠を明確化（同意、契約、正当な利益等）
□ データフローの図式化（収集→処理→保存→削除）
□ 第三者へのデータ提供先をリスト化
□ データ保存期間の定義
```

#### Privacy by Design

```markdown
□ プライバシー影響評価（DPIA）の実施
□ デフォルトでプライバシー保護（最小限の設定）
□ データ暗号化の実装（保存時・転送時）
□ アクセス制御の設計
□ 匿名化・仮名化の検討
```

### Phase 2: 実装

#### 同意管理（Consent Management）

```markdown
□ 明確で平易な言語での同意文言
□ 事前の同意取得（オプトイン方式）
□ 個別の同意項目（マーケティング、分析等）
□ 同意の撤回機能の実装
□ 同意記録の保持（誰が、いつ、何に同意したか）
```

**実装例（TypeScript）**:

```typescript
interface ConsentRecord {
  userId: string;
  consentType: 'marketing' | 'analytics' | 'thirdParty';
  granted: boolean;
  timestamp: Date;
  ipAddress: string;
  version: string; // プライバシーポリシーのバージョン
}

class ConsentManager {
  async recordConsent(record: ConsentRecord): Promise<void> {
    // 同意記録をデータベースに保存（監査証跡）
    await db.consents.insert(record);
  }

  async checkConsent(userId: string, type: string): Promise<boolean> {
    const consent = await db.consents.findOne({ userId, consentType: type });
    return consent?.granted ?? false;
  }

  async revokeConsent(userId: string, type: string): Promise<void> {
    await db.consents.update(
      { userId, consentType: type },
      { granted: false, revokedAt: new Date() }
    );
  }
}
```

#### データ主体の権利実装

```markdown
□ アクセス権（Right to Access）- 自己データの閲覧
□ 訂正権（Right to Rectification）- データの修正
□ 削除権（Right to Erasure / Right to be Forgotten）- データの削除
□ 処理制限権（Right to Restriction）- 処理の一時停止
□ データポータビリティ権（Right to Data Portability）- データのエクスポート
□ 異議申立権（Right to Object）- 処理への異議
□ 自動処理に関する権利（Rights related to Automated Decision Making）
```

**実装例（REST API）**:

```typescript
// GET /api/v1/user/data - アクセス権
router.get('/user/data', authenticate, async (req, res) => {
  const userId = req.user.id;
  const userData = await getUserData(userId);
  res.json(userData);
});

// DELETE /api/v1/user/data - 削除権（忘れられる権利）
router.delete('/user/data', authenticate, async (req, res) => {
  const userId = req.user.id;
  
  // 個人データの完全削除
  await anonymizeUserData(userId);
  await deleteUserAccount(userId);
  
  // 削除ログの記録（監査証跡）
  await auditLog.record({
    action: 'DATA_DELETION',
    userId,
    timestamp: new Date(),
  });
  
  res.status(204).send();
});

// GET /api/v1/user/data/export - データポータビリティ
router.get('/user/data/export', authenticate, async (req, res) => {
  const userId = req.user.id;
  const exportData = await generateDataExport(userId);
  
  res.setHeader('Content-Type', 'application/json');
  res.setHeader('Content-Disposition', 'attachment; filename="user-data.json"');
  res.json(exportData);
});
```

### Phase 3: 運用

#### データ侵害対応

```markdown
□ データ侵害検知の仕組み
□ 72時間以内の監督機関への通知体制
□ データ主体への通知プロセス
□ インシデント対応計画の策定
□ 侵害記録の保持
```

#### 監査・記録

```markdown
□ 処理活動記録（Record of Processing Activities）の作成
□ データ保護影響評価（DPIA）の定期実施
□ 監査ログの保持（アクセス、変更、削除）
□ 第三者処理者との契約書（Data Processing Agreement）
□ 従業員へのGDPR研修の実施
```

---

## 🔐 技術的対策

### 1. データ暗号化

#### 保存時の暗号化（Encryption at Rest）

```python
from cryptography.fernet import Fernet
import os

class EncryptionService:
    def __init__(self):
        # 暗号化キーは環境変数または鍵管理サービスから取得
        self.key = os.getenv('ENCRYPTION_KEY').encode()
        self.cipher = Fernet(self.key)
    
    def encrypt_pii(self, data: str) -> bytes:
        """個人情報を暗号化"""
        return self.cipher.encrypt(data.encode())
    
    def decrypt_pii(self, encrypted_data: bytes) -> str:
        """個人情報を復号化"""
        return self.cipher.decrypt(encrypted_data).decode()

# データベーススキーマ例
"""
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email_encrypted BYTEA NOT NULL,  -- 暗号化されたメールアドレス
    name_encrypted BYTEA NOT NULL,   -- 暗号化された氏名
    created_at TIMESTAMP NOT NULL,
    consent_marketing BOOLEAN DEFAULT FALSE,
    consent_analytics BOOLEAN DEFAULT FALSE
);
"""
```

#### 転送時の暗号化（Encryption in Transit）

```nginx
# nginx.conf - TLS 1.3強制
server {
    listen 443 ssl http2;
    server_name example.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # TLS 1.3のみ許可
    ssl_protocols TLSv1.3;
    ssl_ciphers 'TLS_AES_128_GCM_SHA256:TLS_AES_256_GCM_SHA384';
    ssl_prefer_server_ciphers off;
    
    # HSTSヘッダー
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
}
```

### 2. アクセス制御

```typescript
// RBAC（Role-Based Access Control）実装例
enum Role {
  ADMIN = 'admin',
  USER = 'user',
  DATA_PROTECTION_OFFICER = 'dpo',
}

enum Permission {
  READ_PII = 'read:pii',
  WRITE_PII = 'write:pii',
  DELETE_PII = 'delete:pii',
  EXPORT_PII = 'export:pii',
}

const rolePermissions: Record<Role, Permission[]> = {
  [Role.ADMIN]: [Permission.READ_PII, Permission.WRITE_PII, Permission.DELETE_PII],
  [Role.USER]: [],
  [Role.DATA_PROTECTION_OFFICER]: [Permission.READ_PII, Permission.EXPORT_PII],
};

function checkPermission(userRole: Role, requiredPermission: Permission): boolean {
  return rolePermissions[userRole].includes(requiredPermission);
}
```

### 3. データ保持期間管理

```sql
-- PostgreSQL: 自動削除ジョブ
CREATE OR REPLACE FUNCTION delete_expired_data()
RETURNS void AS $$
BEGIN
    -- 2年以上前の非アクティブユーザーデータを削除
    DELETE FROM users 
    WHERE last_login < NOW() - INTERVAL '2 years'
    AND consent_retention = FALSE;
    
    -- 削除ログを記録
    INSERT INTO audit_log (action, timestamp, details)
    VALUES ('AUTO_DELETE_EXPIRED_DATA', NOW(), 'Deleted inactive users older than 2 years');
END;
$$ LANGUAGE plpgsql;

-- 毎日実行するcronジョブ
SELECT cron.schedule('delete-expired-data', '0 2 * * *', 'SELECT delete_expired_data();');
```

---

## 📋 プライバシーポリシー必須項目

```markdown
1. データ管理者の身元・連絡先
2. データ保護責任者（DPO）の連絡先
3. 処理目的と法的根拠
4. 収集する個人データの種類
5. データの保存期間
6. 第三者へのデータ提供先
7. データ主体の権利
8. 苦情申立て先（監督機関）
9. データの国際転送（EEA域外への移転）
10. 自動化された意思決定の有無
```

---

## 🌍 国際データ転送

### 標準契約条項（Standard Contractual Clauses, SCC）

EEA域外へのデータ転送時に必要な契約:

```markdown
□ EU承認のSCCテンプレートを使用
□ データ輸入者（受領側）との契約締結
□ 追加的保護措置の実施（暗号化等）
□ 転送影響評価（Transfer Impact Assessment）の実施
```

### 主要国・地域の適切性認定

| 国・地域 | 適切性認定 | 備考 |
|---------|-----------|-----|
| 日本 | ✅ 有 | 2019年認定 |
| 英国 | ✅ 有 | Brexit後も継続 |
| スイス | ✅ 有 | EEA非加盟だが認定済み |
| 米国 | ⚠️ 部分的 | EU-US Data Privacy Framework（2023年～） |
| 中国 | ❌ 無 | SCCまたは追加措置が必要 |

---

## 🚨 違反時の罰則

### 制裁金の上限

- **軽微な違反**: 最大1,000万ユーロまたは全世界年間売上高の2%
- **重大な違反**: 最大2,000万ユーロまたは全世界年間売上高の4%

### 重大違反の例

- データ主体の同意なしの処理
- データ主体の権利侵害
- 第三国への不正な個人データ移転
- データ侵害の通知義務違反

---

## 📚 参考リソース

- **GDPR全文**: https://gdpr-info.eu/
- **European Data Protection Board (EDPB)**: https://edpb.europa.eu/
- **ICO (英国情報コミッショナー)**: https://ico.org.uk/
- **個人情報保護委員会（日本）**: https://www.ppc.go.jp/

---

## 🔄 更新履歴

### v1.0.0 (2025-10-27)
- 初版作成
- GDPR基本原則の定義
- 対応チェックリストの作成
- 技術的実装例の追加
- プライバシーポリシー必須項目の整理
