# データ保護標準 / Data Protection Standards

## バージョン情報 / Version Information
- **最終更新日 / Last Updated**: 2025-10-24
- **バージョン / Version**: 1.0
- **対象 / Target**: すべてのプロジェクト / All Projects
- **適用範囲 / Scope**: 推奨 / Recommended (Tier 2)

---

## 目的 / Purpose

このドキュメントは、データ保護のベストプラクティスと標準を定義します。個人情報（PII）の取り扱い、データ暗号化、マスキング、バックアップ戦略など、データのライフサイクル全体にわたる保護措置を規定します。

This document defines best practices and standards for data protection, including handling of Personally Identifiable Information (PII), data encryption, masking, backup strategies, and protection measures throughout the entire data lifecycle.

---

## 1. 個人情報（PII）の取り扱い基準 / PII Handling Standards

### 1.1 PII分類 / PII Classification

#### 高リスクPII / High-Risk PII
以下の情報は特に厳格な保護が必要です：

- **生体認証情報 / Biometric Data**: 指紋、顔認証、虹彩スキャン
- **金融情報 / Financial Information**: クレジットカード番号、銀行口座情報
- **医療情報 / Health Information**: 診断情報、処方箋、治療記録
- **認証情報 / Credentials**: パスワード、APIキー、秘密鍵
- **社会保障番号 / Social Security Numbers**: マイナンバー、SSN

#### 中リスクPII / Medium-Risk PII
- **氏名 / Full Name**
- **メールアドレス / Email Address**
- **電話番号 / Phone Number**
- **住所 / Physical Address**
- **生年月日 / Date of Birth**
- **IPアドレス / IP Address**

#### 低リスクPII / Low-Risk PII
- **ユーザーID / User ID**（仮名化済み）
- **一般的な属性情報 / General Attributes**: 性別、年齢層

### 1.2 PII取り扱い原則 / PII Handling Principles

#### データ最小化 / Data Minimization
```python
# ✅ 良い例: 必要最小限のデータのみ収集
class UserRegistration:
    def __init__(self, email: str, username: str):
        self.email = email  # 必須
        self.username = username  # 必須
        # 不要な情報は収集しない

# ❌ 悪い例: 不要な情報まで収集
class UserRegistration:
    def __init__(self, email: str, username: str, ssn: str, 
                 phone: str, address: str, dob: str):
        # サービスに不要な情報まで収集している
        pass
```

#### 目的制限 / Purpose Limitation
```python
# ✅ 良い例: 明確な目的と同意
class ConsentManager:
    def collect_data_with_consent(self, user_id: str, purpose: str):
        consent = self.get_user_consent(user_id, purpose)
        if consent.is_granted():
            return self.collect_data(user_id, purpose)
        raise PermissionError("User consent not granted")

# ❌ 悪い例: 目的外利用
def marketing_campaign(user_data):
    # 認証目的で収集したメールアドレスをマーケティングに流用
    send_promotional_email(user_data.email)  # NG
```

---

## 2. データ暗号化標準 / Data Encryption Standards

### 2.1 保存時の暗号化 / Encryption at Rest

#### データベース暗号化
```python
# ✅ 良い例: フィールドレベル暗号化
from cryptography.fernet import Fernet
from sqlalchemy import Column, String, LargeBinary

class User(Base):
    __tablename__ = 'users'
    
    id = Column(String, primary_key=True)
    email = Column(String)  # 検索可能なフィールド
    ssn_encrypted = Column(LargeBinary)  # 暗号化された機密情報
    
    def set_ssn(self, ssn: str, cipher: Fernet):
        self.ssn_encrypted = cipher.encrypt(ssn.encode())
    
    def get_ssn(self, cipher: Fernet) -> str:
        return cipher.decrypt(self.ssn_encrypted).decode()

# ❌ 悪い例: 平文で保存
class User(Base):
    __tablename__ = 'users'
    
    id = Column(String, primary_key=True)
    email = Column(String)
    ssn = Column(String)  # 平文で機密情報を保存 - NG
```

#### ファイル暗号化
```python
# ✅ 良い例: AES-256でファイル暗号化
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import os

def encrypt_file(input_path: str, output_path: str, key: bytes):
    """AES-256-GCMでファイルを暗号化"""
    iv = os.urandom(12)  # GCMモード用の初期化ベクトル
    cipher = Cipher(
        algorithms.AES(key),  # 256-bit key
        modes.GCM(iv),
        backend=default_backend()
    )
    
    encryptor = cipher.encryptor()
    
    with open(input_path, 'rb') as f_in:
        plaintext = f_in.read()
    
    ciphertext = encryptor.update(plaintext) + encryptor.finalize()
    
    with open(output_path, 'wb') as f_out:
        f_out.write(iv)  # IVを先頭に保存
        f_out.write(encryptor.tag)  # 認証タグ
        f_out.write(ciphertext)
```

### 2.2 転送時の暗号化 / Encryption in Transit

#### TLS/SSL設定
```nginx
# ✅ 良い例: 強力なTLS設定
server {
    listen 443 ssl http2;
    
    # TLS 1.2以上のみ許可
    ssl_protocols TLSv1.2 TLSv1.3;
    
    # 強力な暗号スイート
    ssl_ciphers 'ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers on;
    
    # HSTSヘッダー
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
    # 証明書設定
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
}

# ❌ 悪い例: 弱いTLS設定
server {
    listen 443 ssl;
    
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;  # 古いプロトコルを許可 - NG
    ssl_ciphers 'ALL';  # 弱い暗号も許可 - NG
}
```

#### API通信の暗号化
```python
# ✅ 良い例: HTTPS通信の強制
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

class SecureAPIClient:
    def __init__(self, base_url: str):
        if not base_url.startswith('https://'):
            raise ValueError("HTTPSのみ許可されています")
        
        self.base_url = base_url
        self.session = requests.Session()
        
        # リトライ戦略
        retry = Retry(total=3, backoff_factor=1)
        adapter = HTTPAdapter(max_retries=retry)
        self.session.mount('https://', adapter)
    
    def get(self, endpoint: str):
        return self.session.get(
            f"{self.base_url}/{endpoint}",
            verify=True,  # 証明書検証を有効化
            timeout=30
        )

# ❌ 悪い例: HTTP通信を許可
def api_call(url):
    return requests.get(url, verify=False)  # 証明書検証を無効化 - NG
```

---

## 3. データマスキング・匿名化 / Data Masking and Anonymization

### 3.1 マスキング手法 / Masking Techniques

#### 部分マスキング / Partial Masking
```python
# ✅ 良い例: 適切な部分マスキング
import re

def mask_email(email: str) -> str:
    """メールアドレスの部分マスキング"""
    username, domain = email.split('@')
    if len(username) <= 2:
        masked_username = '*' * len(username)
    else:
        masked_username = username[0] + '*' * (len(username) - 2) + username[-1]
    return f"{masked_username}@{domain}"

def mask_credit_card(card_number: str) -> str:
    """クレジットカード番号のマスキング"""
    # 最後の4桁のみ表示
    return '*' * (len(card_number) - 4) + card_number[-4:]

def mask_phone(phone: str) -> str:
    """電話番号のマスキング"""
    return re.sub(r'\d(?=\d{4})', '*', phone)

# 使用例
print(mask_email("john.doe@example.com"))  # j******e@example.com
print(mask_credit_card("1234567890123456"))  # ************3456
print(mask_phone("090-1234-5678"))  # ***-****-5678
```

#### トークン化 / Tokenization
```python
# ✅ 良い例: トークン化による機密情報の保護
import secrets
import hashlib
from typing import Dict

class TokenizationService:
    def __init__(self):
        self.token_store: Dict[str, str] = {}
    
    def tokenize(self, sensitive_data: str) -> str:
        """機密データをトークンに変換"""
        # ランダムなトークンを生成
        token = secrets.token_urlsafe(32)
        
        # トークンと元データの関連を暗号化して保存
        self.token_store[token] = sensitive_data
        
        return token
    
    def detokenize(self, token: str) -> str:
        """トークンから元データを復元"""
        if token not in self.token_store:
            raise ValueError("無効なトークン")
        return self.token_store[token]

# 使用例
tokenizer = TokenizationService()
token = tokenizer.tokenize("1234-5678-9012-3456")
# トークンをログに記録しても安全
print(f"処理中のトークン: {token}")
```

### 3.2 匿名化 / Anonymization

#### K-匿名性 / K-Anonymity
```python
# ✅ 良い例: K-匿名性の実装
import pandas as pd

def generalize_age(age: int) -> str:
    """年齢を年齢層に一般化"""
    if age < 20:
        return "10代"
    elif age < 30:
        return "20代"
    elif age < 40:
        return "30代"
    elif age < 50:
        return "40代"
    elif age < 60:
        return "50代"
    else:
        return "60代以上"

def generalize_location(address: str) -> str:
    """住所を都道府県レベルに一般化"""
    # 郵便番号や詳細住所を除去
    prefecture = address.split()[0]  # 簡略化した例
    return prefecture

def anonymize_dataset(df: pd.DataFrame) -> pd.DataFrame:
    """データセットの匿名化"""
    df_anonymized = df.copy()
    
    # 識別子を除去
    df_anonymized = df_anonymized.drop(['user_id', 'name', 'email'], axis=1)
    
    # 準識別子を一般化
    df_anonymized['age'] = df_anonymized['age'].apply(generalize_age)
    df_anonymized['location'] = df_anonymized['address'].apply(generalize_location)
    df_anonymized = df_anonymized.drop(['address'], axis=1)
    
    return df_anonymized
```

---

## 4. バックアップとリカバリー / Backup and Recovery

### 4.1 バックアップ戦略 / Backup Strategy

#### 3-2-1ルール
- **3**: データの3つのコピーを保持
- **2**: 2種類の異なるメディアに保存
- **1**: 1つのコピーをオフサイトに保管

```yaml
# ✅ 良い例: バックアップ設定
backup_strategy:
  # プライマリコピー（本番環境）
  primary:
    location: "production-db"
    type: "live"
  
  # セカンダリコピー（ローカルバックアップ）
  secondary:
    location: "local-storage"
    type: "disk"
    schedule: "daily at 02:00 UTC"
    retention: "30 days"
  
  # ターシャリコピー（オフサイトバックアップ）
  tertiary:
    location: "aws-s3-backup"
    type: "cloud"
    schedule: "daily at 04:00 UTC"
    retention: "90 days"
    encryption: "AES-256"
```

#### バックアップの暗号化
```python
# ✅ 良い例: 暗号化されたバックアップ
import subprocess
import os
from datetime import datetime

def create_encrypted_backup(db_name: str, backup_dir: str, encryption_key: str):
    """暗号化されたデータベースバックアップを作成"""
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    backup_file = f"{backup_dir}/{db_name}_{timestamp}.sql.gz.enc"
    
    # バックアップ、圧縮、暗号化をパイプライン処理
    cmd = f"""
    pg_dump {db_name} | \
    gzip | \
    openssl enc -aes-256-cbc -salt -pbkdf2 -pass pass:{encryption_key} \
    -out {backup_file}
    """
    
    subprocess.run(cmd, shell=True, check=True)
    
    # バックアップファイルの検証
    if os.path.exists(backup_file):
        print(f"✅ バックアップ作成成功: {backup_file}")
        return backup_file
    else:
        raise Exception("バックアップの作成に失敗しました")

# ❌ 悪い例: 暗号化なしのバックアップ
def create_backup(db_name: str, backup_dir: str):
    backup_file = f"{backup_dir}/{db_name}.sql"
    subprocess.run(f"pg_dump {db_name} > {backup_file}", shell=True)
    # 暗号化されていない - NG
```

### 4.2 リカバリー手順 / Recovery Procedures

#### Point-in-Time Recovery (PITR)
```python
# ✅ 良い例: ポイントインタイムリカバリー
class PITRManager:
    def __init__(self, backup_dir: str, wal_archive: str):
        self.backup_dir = backup_dir
        self.wal_archive = wal_archive
    
    def restore_to_point(self, target_time: str):
        """特定の時点にデータベースを復元"""
        # 1. 最新のベースバックアップを復元
        latest_backup = self.get_latest_backup()
        self.restore_base_backup(latest_backup)
        
        # 2. WALアーカイブを指定時刻まで適用
        recovery_conf = f"""
        restore_command = 'cp {self.wal_archive}/%f %p'
        recovery_target_time = '{target_time}'
        recovery_target_action = 'promote'
        """
        
        with open('/var/lib/postgresql/data/recovery.conf', 'w') as f:
            f.write(recovery_conf)
        
        # 3. PostgreSQLを起動してリカバリー実行
        self.start_database()
        
        print(f"✅ {target_time}の時点に復元しました")
    
    def get_latest_backup(self) -> str:
        """最新のバックアップファイルを取得"""
        backups = sorted(os.listdir(self.backup_dir))
        return backups[-1] if backups else None
```

#### リカバリーテスト
```python
# ✅ 良い例: 定期的なリカバリーテスト
import schedule
import time

def test_recovery():
    """バックアップからのリカバリーをテスト"""
    test_db_name = "test_recovery_db"
    
    try:
        # 1. テスト環境にバックアップを復元
        restore_backup_to_test_env(test_db_name)
        
        # 2. データ整合性を検証
        verify_data_integrity(test_db_name)
        
        # 3. テスト結果を記録
        log_recovery_test_result(success=True)
        
        print("✅ リカバリーテスト成功")
        
    except Exception as e:
        log_recovery_test_result(success=False, error=str(e))
        print(f"❌ リカバリーテスト失敗: {e}")
    
    finally:
        # 4. テスト環境をクリーンアップ
        cleanup_test_env(test_db_name)

# 毎週日曜日にリカバリーテストを実行
schedule.every().sunday.at("03:00").do(test_recovery)

while True:
    schedule.run_pending()
    time.sleep(3600)
```

---

## 5. データライフサイクル管理 / Data Lifecycle Management

### 5.1 データ保持ポリシー / Data Retention Policy

```python
# ✅ 良い例: データ保持ポリシーの実装
from datetime import datetime, timedelta
from enum import Enum

class DataCategory(Enum):
    TRANSACTION = "transaction"
    USER_PROFILE = "user_profile"
    LOG_DATA = "log_data"
    TEMPORARY = "temporary"

class RetentionPolicy:
    RETENTION_PERIODS = {
        DataCategory.TRANSACTION: timedelta(days=2555),  # 7年（法的要件）
        DataCategory.USER_PROFILE: timedelta(days=1825),  # 5年
        DataCategory.LOG_DATA: timedelta(days=90),  # 90日
        DataCategory.TEMPORARY: timedelta(days=1),  # 1日
    }
    
    @classmethod
    def get_expiration_date(cls, category: DataCategory, 
                           created_at: datetime) -> datetime:
        """データの有効期限を計算"""
        retention_period = cls.RETENTION_PERIODS[category]
        return created_at + retention_period
    
    @classmethod
    def should_delete(cls, category: DataCategory, 
                     created_at: datetime) -> bool:
        """データを削除すべきか判定"""
        expiration_date = cls.get_expiration_date(category, created_at)
        return datetime.now() > expiration_date

# 使用例
def cleanup_expired_data():
    """期限切れデータのクリーンアップ"""
    for data in query_all_data():
        if RetentionPolicy.should_delete(data.category, data.created_at):
            delete_data(data.id)
            log_deletion(data.id, "retention policy")
```

### 5.2 データ削除 / Data Deletion

#### 安全な削除
```python
# ✅ 良い例: 多段階削除プロセス
from enum import Enum

class DeletionStatus(Enum):
    ACTIVE = "active"
    SOFT_DELETED = "soft_deleted"
    SCHEDULED_FOR_DELETION = "scheduled_for_deletion"
    PERMANENTLY_DELETED = "permanently_deleted"

class DataDeletionManager:
    def soft_delete(self, record_id: str):
        """ソフト削除（論理削除）"""
        # ステップ1: ステータスを更新
        update_record_status(record_id, DeletionStatus.SOFT_DELETED)
        update_record_field(record_id, 'deleted_at', datetime.now())
        
        # ログ記録
        log_audit_event('soft_delete', record_id)
    
    def schedule_permanent_deletion(self, record_id: str, 
                                   grace_period_days: int = 30):
        """永久削除をスケジュール"""
        # ステップ2: 猶予期間を設定
        deletion_date = datetime.now() + timedelta(days=grace_period_days)
        update_record_field(record_id, 'permanent_deletion_date', deletion_date)
        update_record_status(record_id, DeletionStatus.SCHEDULED_FOR_DELETION)
        
        # 通知（必要に応じて）
        notify_data_owner(record_id, deletion_date)
    
    def permanently_delete(self, record_id: str):
        """永久削除（物理削除）"""
        # ステップ3: 関連データも含めて完全削除
        
        # 3.1 バックアップから除外
        exclude_from_backup(record_id)
        
        # 3.2 データベースから削除
        delete_from_database(record_id)
        
        # 3.3 ストレージから削除
        delete_from_storage(record_id)
        
        # 3.4 キャッシュから削除
        delete_from_cache(record_id)
        
        # 3.5 検索インデックスから削除
        delete_from_search_index(record_id)
        
        # 最終ログ記録
        log_audit_event('permanent_delete', record_id)
        update_record_status(record_id, DeletionStatus.PERMANENTLY_DELETED)

# ❌ 悪い例: 直接削除
def delete_user(user_id):
    db.execute(f"DELETE FROM users WHERE id = {user_id}")
    # 監査ログなし、復元不可 - NG
```

---

## 6. 法規制への対応 / Compliance with Regulations

### 6.1 GDPR対応 / GDPR Compliance

#### データポータビリティ / Data Portability
```python
# ✅ 良い例: データエクスポート機能
import json
from typing import Dict, Any

class GDPRDataExporter:
    def export_user_data(self, user_id: str) -> Dict[str, Any]:
        """ユーザーデータを機械可読形式でエクスポート"""
        user_data = {
            'personal_info': self.get_personal_info(user_id),
            'account_history': self.get_account_history(user_id),
            'preferences': self.get_preferences(user_id),
            'transactions': self.get_transactions(user_id),
            'consent_records': self.get_consent_records(user_id),
        }
        
        # JSON形式でエクスポート
        export_file = f"user_data_{user_id}_{datetime.now().isoformat()}.json"
        with open(export_file, 'w', encoding='utf-8') as f:
            json.dump(user_data, f, ensure_ascii=False, indent=2)
        
        log_audit_event('data_export', user_id)
        return user_data
```

#### 削除権（忘れられる権利）/ Right to Erasure
```python
# ✅ 良い例: 忘れられる権利の実装
class RightToErasureHandler:
    def process_erasure_request(self, user_id: str, reason: str):
        """削除リクエストを処理"""
        # 1. 削除可能性を検証
        if not self.can_be_deleted(user_id):
            raise Exception("法的義務により削除できません")
        
        # 2. 削除範囲を特定
        data_to_delete = self.identify_user_data(user_id)
        
        # 3. 削除を実行
        deletion_manager = DataDeletionManager()
        for data_item in data_to_delete:
            deletion_manager.soft_delete(data_item.id)
        
        # 4. 削除証明書を発行
        certificate = self.issue_deletion_certificate(user_id, data_to_delete)
        
        # 5. 監査ログ
        log_audit_event('gdpr_erasure', user_id, reason)
        
        return certificate
    
    def can_be_deleted(self, user_id: str) -> bool:
        """削除可能かチェック"""
        # 法的保持義務があるデータは削除不可
        has_legal_hold = check_legal_hold(user_id)
        has_pending_transactions = check_pending_transactions(user_id)
        
        return not (has_legal_hold or has_pending_transactions)
```

### 6.2 CCPA対応 / CCPA Compliance

```python
# ✅ 良い例: CCPAデータ開示
class CCPADisclosureHandler:
    def generate_disclosure(self, user_id: str) -> Dict[str, Any]:
        """CCPAに基づくデータ開示"""
        return {
            'categories_collected': self.get_data_categories(user_id),
            'sources': self.get_data_sources(user_id),
            'business_purposes': self.get_usage_purposes(user_id),
            'third_parties': self.get_third_party_sharing(user_id),
            'retention_periods': self.get_retention_info(user_id),
        }
    
    def process_do_not_sell(self, user_id: str):
        """販売オプトアウトを処理"""
        update_user_preference(user_id, 'do_not_sell', True)
        notify_third_parties_opt_out(user_id)
        log_audit_event('ccpa_do_not_sell', user_id)
```

---

## 7. Devin向けの利用パターン / Usage Patterns for Devin

### 7.1 データ保護実装のプロンプト例

#### プロンプト1: PII検出と保護
```
タスク: コードベース内のPIIを検出し、適切な保護措置を実装してください

要件:
1. 以下のPIIパターンを検出:
   - メールアドレス
   - クレジットカード番号
   - 社会保障番号
   - 電話番号

2. 各PII項目に対して:
   - データベースでの暗号化
   - ログでのマスキング
   - API応答での部分マスキング

3. 実装基準:
   - このドキュメントのセクション2（暗号化）とセクション3（マスキング）に従う
   - AES-256暗号化を使用
   - 適切なマスキング関数を実装
```

#### プロンプト2: バックアップシステムの構築
```
タスク: 3-2-1ルールに基づく自動バックアップシステムを実装してください

要件:
1. バックアップ構成:
   - プライマリ: 本番データベース
   - セカンダリ: ローカルストレージへの日次バックアップ
   - ターシャリ: AWS S3へのオフサイトバックアップ

2. セキュリティ要件:
   - すべてのバックアップをAES-256で暗号化
   - 転送時のTLS 1.3使用
   - キーローテーション機能

3. 実装基準:
   - このドキュメントのセクション4（バックアップ）に従う
   - 定期的なリカバリーテストを含める
```

#### プロンプト3: GDPR準拠の実装
```
タスク: GDPR第15条（アクセス権）と第17条（削除権）を実装してください

要件:
1. データエクスポート機能:
   - ユーザーの全データを機械可読形式で提供
   - JSON形式でのエクスポート
   - 30日以内に応答

2. 削除機能:
   - 多段階削除プロセス（ソフト削除→猶予期間→永久削除）
   - 法的保持義務のチェック
   - 削除証明書の発行

3. 実装基準:
   - このドキュメントのセクション6（法規制対応）に従う
   - 監査ログの記録
```

### 7.2 自動化スクリプト生成プロンプト

#### プロンプト4: データ保持ポリシーの自動化
```
タスク: データ保持ポリシーに基づく自動クリーンアップスクリプトを作成してください

要件:
1. 以下のデータカテゴリを処理:
   - トランザクションデータ: 7年保持
   - ユーザープロファイル: 5年保持
   - ログデータ: 90日保持
   - 一時データ: 1日保持

2. 実装機能:
   - 毎日午前3時に自動実行
   - 期限切れデータの特定と削除
   - 削除レポートの生成
   - エラーハンドリングとアラート

3. 実装基準:
   - このドキュメントのセクション5（ライフサイクル管理）に従う
```

---

## 8. チェックリスト / Checklist

### データ保護実装チェックリスト

#### 設計フェーズ
- [ ] PII分類を実施し、高/中/低リスクを特定
- [ ] データ最小化原則に基づく設計
- [ ] 暗号化要件の定義（保存時・転送時）
- [ ] データ保持ポリシーの策定
- [ ] 法規制要件の確認（GDPR、CCPA等）

#### 実装フェーズ
- [ ] データベースフィールドレベル暗号化
- [ ] TLS 1.2以上でのAPI通信
- [ ] ログマスキング機能の実装
- [ ] バックアップの暗号化
- [ ] データエクスポート機能
- [ ] 多段階削除プロセス

#### テストフェーズ
- [ ] 暗号化・復号化のテスト
- [ ] マスキング機能のテスト
- [ ] バックアップ・リカバリーのテスト
- [ ] データポータビリティのテスト
- [ ] 削除機能のテスト

#### 運用フェーズ
- [ ] 定期的なバックアップの実行
- [ ] リカバリーテストの定期実施（月次）
- [ ] データ保持ポリシーの自動適用
- [ ] 監査ログの記録と保存
- [ ] セキュリティインシデントへの対応準備

---

## 9. 関連ドキュメント / Related Documents

- [セキュリティチェックリスト](./security-checklist.md)
- [認証・認可標準](./authentication-authorization.md)
- [コーディング標準](../01-coding-standards/README.md)
- [アーキテクチャ設計原則](../02-architecture-standards/design-principles.md)

---

## 10. 更新履歴 / Change History

| バージョン | 日付 | 変更内容 | 作成者 |
|---------|------|---------|-------|
| 1.0 | 2025-10-24 | 初版作成 | Development Team |

---

**このドキュメントの維持管理についてのお問い合わせは、セキュリティチームまでご連絡ください。**
