# セキュリティテスト標準
## Security Testing Standards

**最終更新日**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: セキュリティエンジニア・開発者・QAエンジニア・DevOps・自律型AI Devin  
**適用範囲**: 全プロジェクト共通セキュリティテスト基準

---

## 📖 概要

このドキュメントは、アプリケーションのセキュリティを確保するためのテスト標準を定義します。OWASP Top 10への対応、セキュリティテストの種類、実施方法、ツールを包括的にカバーし、開発ライフサイクル全体でのセキュリティ品質を保証します。

### 🎯 目的

- **脆弱性の早期発見**: 開発段階でのセキュリティ問題の検出
- **OWASP Top 10対応**: 主要な脆弱性への体系的な対策
- **継続的なセキュリティ**: CI/CDパイプラインへのセキュリティテスト統合
- **コンプライアンス遵守**: セキュリティ標準・規制への準拠
- **インシデント予防**: 本番環境でのセキュリティインシデント防止

---

## 📂 目次

1. [セキュリティテストの種類](#1-セキュリティテストの種類)
2. [OWASP Top 10への対応](#2-owasp-top-10への対応)
3. [静的アプリケーションセキュリティテスト（SAST）](#3-静的アプリケーションセキュリティテストsast)
4. [動的アプリケーションセキュリティテスト（DAST）](#4-動的アプリケーションセキュリティテストdast)
5. [依存関係の脆弱性管理](#5-依存関係の脆弱性管理)
6. [APIセキュリティテスト](#6-apiセキュリティテスト)
7. [認証・認可のテスト](#7-認証認可のテスト)
8. [シークレット管理とスキャン](#8-シークレット管理とスキャン)
9. [ペネトレーションテスト](#9-ペネトレーションテスト)
10. [CI/CDパイプライン統合](#10-cicdパイプライン統合)
11. [セキュリティインシデント対応](#11-セキュリティインシデント対応)
12. [Devin AIガイドライン](#12-devin-aiガイドライン)

---

## 1. セキュリティテストの種類

### 1.1 テストタイプの分類

#### **SAST（Static Application Security Testing）**

**特徴**:
```yaml
実施タイミング:  コーディング中・コミット前
分析対象:       ソースコード、バイトコード
検出対象:       コーディングパターンの脆弱性
実行速度:       高速（数分）
カバレッジ:     全コード（実行不要）
誤検出:         中〜高（False Positive多め）
```

**利点**:
- 開発初期段階での検出
- 全コードのスキャン可能
- 脆弱性の場所を特定
- CI/CDへの統合が容易

**欠点**:
- 実行時の問題を検出できない
- 設定ミスを検出しにくい
- 誤検出が多い

---

#### **DAST（Dynamic Application Security Testing）**

**特徴**:
```yaml
実施タイミング:  テスト環境・ステージング環境
分析対象:       実行中のアプリケーション
検出対象:       実行時の脆弱性、設定ミス
実行速度:       低速（数時間）
カバレッジ:     アクセス可能な部分のみ
誤検出:         低（False Positive少なめ）
```

**利点**:
- 実際の攻撃をシミュレート
- 設定ミスを検出
- 統合された脆弱性を発見
- 言語非依存

**欠点**:
- 遅い実行時間
- 脆弱性の場所特定が困難
- カバレッジが限定的

---

#### **SCA（Software Composition Analysis）**

**特徴**:
```yaml
実施タイミング:  ビルド時・定期スキャン
分析対象:       依存ライブラリ、オープンソース
検出対象:       既知の脆弱性（CVE）
実行速度:       高速（数分）
カバレッジ:     全依存関係
誤検出:         低
```

**利点**:
- 既知脆弱性の迅速な検出
- ライセンスコンプライアンス
- 自動パッチ提案

---

#### **IAST（Interactive Application Security Testing）**

**特徴**:
```yaml
実施タイミング:  テスト実行中
分析方法:       アプリケーション内部に計測
検出対象:       実行時の詳細な脆弱性
実行速度:       中速
カバレッジ:     テストでカバーされた部分
誤検出:         非常に低
```

**利点**:
- SASTとDASTの長所を組み合わせ
- 低い誤検出率
- 詳細なコンテキスト情報

**欠点**:
- 計測オーバーヘッド
- テストカバレッジに依存

---

### 1.2 テスト実施タイミング

#### **Shift-Left アプローチ**

```
開発フェーズ              セキュリティテスト
────────────────────────────────────────────
コーディング中         │ IDE統合SAST
                       │ リアルタイムフィードバック
                       │
コミット前             │ Pre-commit SAST
                       │ シークレットスキャン
                       │
プルリクエスト         │ SAST + SCA
                       │ 依存関係脆弱性チェック
                       │ コードレビュー
                       │
ビルド・テスト         │ SAST + SCA + Unit Tests
                       │ セキュリティユニットテスト
                       │
統合テスト             │ DAST + IAST
                       │ API セキュリティテスト
                       │
ステージング           │ DAST + ペネトレーションテスト
                       │ 脆弱性スキャン
                       │
リリース前             │ 総合セキュリティ評価
                       │ セキュリティゲート
                       │
本番環境               │ 継続的モニタリング
                       │ ランタイムセキュリティ
```

---

## 2. OWASP Top 10への対応

### 2.1 A01: アクセス制御の不備（Broken Access Control）

#### **脆弱性の例**
- 権限チェックのバイパス
- 垂直権限昇格（一般ユーザー→管理者）
- 水平権限昇格（他ユーザーのデータアクセス）
- IDOR（Insecure Direct Object Reference）

#### **テスト方法**

**手動テスト**:
```yaml
1. 権限なしでの保護されたリソースアクセス
   - ログアウト状態でAPIを呼び出す
   - 期待: 401 Unauthorized

2. 他ユーザーのデータアクセス
   - ユーザーAでログイン
   - ユーザーBのIDでリソースアクセス
   - 期待: 403 Forbidden

3. 権限昇格の試行
   - role=user → role=admin に改ざん
   - 期待: 403 Forbidden

4. IDORテスト
   - /api/users/123 → /api/users/124
   - 期待: 自分のIDのみアクセス可能
```

**自動テスト例（Python）**:
```python
import pytest
from fastapi.testclient import TestClient

def test_unauthorized_access(client: TestClient):
    """認証なしでの保護されたエンドポイントへのアクセス"""
    response = client.get("/api/admin/users")
    assert response.status_code == 401
    assert "Unauthorized" in response.json()["error"]

def test_forbidden_access(client: TestClient, regular_user_token):
    """権限不足でのアクセス"""
    headers = {"Authorization": f"Bearer {regular_user_token}"}
    response = client.get("/api/admin/users", headers=headers)
    assert response.status_code == 403
    assert "Forbidden" in response.json()["error"]

def test_idor_prevention(client: TestClient, user_a_token, user_b_id):
    """他ユーザーのデータへのアクセス防止"""
    headers = {"Authorization": f"Bearer {user_a_token}"}
    response = client.get(f"/api/users/{user_b_id}/profile", headers=headers)
    assert response.status_code == 403
```

---

### 2.2 A02: 暗号化の失敗（Cryptographic Failures）

#### **脆弱性の例**
- 平文での機密データ保存
- 弱い暗号化アルゴリズム（MD5、SHA1）
- 不適切な鍵管理
- TLS/SSL設定ミス

#### **テスト方法**

**静的解析チェック**:
```yaml
禁止パターン:
  - MD5ハッシュの使用
  - SHA1ハッシュの使用
  - DESアルゴリズムの使用
  - ハードコードされた暗号鍵
  - HTTPでの機密データ送信

推奨パターン:
  - bcrypt/argon2でのパスワードハッシュ
  - AES-256-GCMでの暗号化
  - TLS 1.2以上の使用
  - 環境変数での鍵管理
```

**自動テストコード例**:
```python
def test_password_hashing_strength():
    """パスワードハッシュの強度テスト"""
    from auth import hash_password
    password = "SecureP@ssw0rd"
    hashed = hash_password(password)
    
    # bcrypt形式であることを確認
    assert hashed.startswith("$2b$")
    # 平文パスワードが含まれていないこと
    assert password not in hashed
    # ソルトが含まれていること（ハッシュが毎回異なる）
    assert hash_password(password) != hashed

def test_no_sensitive_data_in_logs():
    """ログに機密情報が含まれていないことを確認"""
    import logging
    from io import StringIO
    
    log_capture = StringIO()
    handler = logging.StreamHandler(log_capture)
    logger = logging.getLogger()
    logger.addHandler(handler)
    
    # ログ出力を含む処理を実行
    process_user_login("user@example.com", "SecretPassword")
    
    log_output = log_capture.getvalue()
    assert "SecretPassword" not in log_output
    assert "password" not in log_output.lower()
```

---

### 2.3 A03: インジェクション（Injection）

#### **脆弱性の例**
- SQLインジェクション
- NoSQLインジェクション
- OSコマンドインジェクション
- LDAPインジェクション
- XPath/XMLインジェクション

#### **SQLインジェクションテスト**

**テストケース**:
```yaml
入力パターン:
  - ' OR '1'='1
  - admin'--
  - ' UNION SELECT * FROM users--
  - '; DROP TABLE users;--
  - 1' AND 1=1--
  - 1' AND 1=2--

期待結果:
  - エラーが表示されない
  - SQLエラーメッセージが露出しない
  - 不正なクエリが実行されない
  - 適切なエラーハンドリング
```

**自動テスト例**:
```python
def test_sql_injection_prevention():
    """SQLインジェクション対策テスト"""
    # 危険な入力パターン
    malicious_inputs = [
        "' OR '1'='1",
        "admin'--",
        "1' UNION SELECT * FROM users--",
    ]
    
    for malicious_input in malicious_inputs:
        response = client.get(
            f"/api/users/search?name={malicious_input}"
        )
        # 正常なエラーハンドリング
        assert response.status_code in [200, 400]
        # SQLエラーが露出していないこと
        assert "SQL" not in response.text
        assert "syntax error" not in response.text.lower()
        # すべてのユーザーが返されていないこと
        if response.status_code == 200:
            data = response.json()
            assert len(data) < 100  # 異常に多いデータでないこと
```

**安全なコード例**:
```python
# ❌ 悪い例: SQLインジェクションの脆弱性あり
def get_user_by_name(name: str):
    query = f"SELECT * FROM users WHERE name = '{name}'"
    return db.execute(query)

# ✅ 良い例: パラメータ化クエリ
def get_user_by_name(name: str):
    query = "SELECT * FROM users WHERE name = ?"
    return db.execute(query, (name,))

# ✅ 良い例: ORMの使用
def get_user_by_name(name: str):
    return User.query.filter_by(name=name).first()
```

---

### 2.4 A04: 安全でない設計（Insecure Design）

#### **テスト観点**
- ビジネスロジックの脆弱性
- レート制限の欠如
- リソース枯渇攻撃
- ワークフロー制御の不備

#### **レート制限テスト**

```python
def test_rate_limiting():
    """レート制限の有効性テスト"""
    # 短時間に多数のリクエスト
    responses = []
    for i in range(101):  # 制限: 100req/min
        response = client.get("/api/public-endpoint")
        responses.append(response)
    
    # 制限を超えたリクエストは429を返すこと
    status_codes = [r.status_code for r in responses]
    assert 429 in status_codes
    assert status_codes.count(429) > 0
    
    # Retry-Afterヘッダーが含まれること
    rate_limited_response = next(r for r in responses if r.status_code == 429)
    assert "Retry-After" in rate_limited_response.headers
```

---

### 2.5 A05: セキュリティ設定のミス（Security Misconfiguration）

#### **チェック項目**

```yaml
アプリケーション設定:
  - デバッグモードが本番で無効
  - デフォルトアカウントが無効化
  - 不要な機能が無効化
  - エラーメッセージが適切

サーバー設定:
  - 不要なHTTPメソッドが無効
  - セキュリティヘッダーが設定
  - ディレクトリリスティング無効
  - バナー情報の非表示

依存関係:
  - 最新バージョンの使用
  - 既知の脆弱性なし
  - 不要なライブラリなし
```

**セキュリティヘッダーテスト**:
```python
def test_security_headers():
    """必須セキュリティヘッダーの確認"""
    response = client.get("/")
    headers = response.headers
    
    # 必須ヘッダー
    required_headers = {
        "X-Content-Type-Options": "nosniff",
        "X-Frame-Options": "DENY",
        "Strict-Transport-Security": "max-age=31536000",
        "Content-Security-Policy": lambda v: "default-src" in v,
        "X-XSS-Protection": "1; mode=block",
    }
    
    for header, expected_value in required_headers.items():
        assert header in headers, f"{header} ヘッダーが設定されていません"
        if callable(expected_value):
            assert expected_value(headers[header])
        else:
            assert headers[header] == expected_value

def test_debug_mode_disabled():
    """本番環境でデバッグモードが無効であることを確認"""
    # 意図的にエラーを発生させる
    response = client.get("/api/nonexistent-endpoint")
    
    # デバッグ情報が露出していないこと
    assert "Traceback" not in response.text
    assert "File" not in response.text
    assert "line" not in response.text
```

---

### 2.6 A06: 脆弱で古いコンポーネント（Vulnerable and Outdated Components）

#### **テスト方法**

**依存関係スキャン**:
```bash
# npm (Node.js)
$ npm audit
$ npm audit fix

# pip (Python)
$ pip-audit
$ safety check

# Maven (Java)
$ mvn dependency-check:check

# 自動化ツール
$ snyk test
$ dependabot
```

**CI/CDでの自動チェック**:
```yaml
# GitHub Actions
- name: Run Snyk Security Scan
  uses: snyk/actions/node@master
  env:
    SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
  with:
    args: --severity-threshold=high
```

---

### 2.7 A07: 識別と認証の失敗（Identification and Authentication Failures）

#### **テストケース**

**パスワードポリシーテスト**:
```python
def test_password_complexity():
    """パスワード複雑性要件のテスト"""
    weak_passwords = [
        "123456",         # 短すぎる
        "password",       # 一般的すぎる
        "abc123",         # 単純すぎる
        "user@example",   # ユーザー名を含む
    ]
    
    for weak_password in weak_passwords:
        response = client.post("/api/register", json={
            "username": "testuser",
            "email": "test@example.com",
            "password": weak_password
        })
        assert response.status_code == 400
        assert "password" in response.json()["errors"]

def test_account_lockout():
    """アカウントロックアウトのテスト"""
    # 5回連続で失敗したログイン試行
    for i in range(5):
        response = client.post("/api/login", json={
            "username": "testuser",
            "password": "wrongpassword"
        })
    
    # 6回目はロックアウトされること
    response = client.post("/api/login", json={
        "username": "testuser",
        "password": "correctpassword"  # 正しいパスワードでも
    })
    assert response.status_code == 423  # Locked
    assert "locked" in response.json()["error"].lower()

def test_session_timeout():
    """セッションタイムアウトのテスト"""
    # ログインしてトークン取得
    login_response = client.post("/api/login", json={
        "username": "testuser",
        "password": "correctpassword"
    })
    token = login_response.json()["token"]
    
    # 30分後をシミュレート（実際はモックで時間を進める）
    with freeze_time(datetime.now() + timedelta(minutes=31)):
        response = client.get("/api/protected-resource", headers={
            "Authorization": f"Bearer {token}"
        })
        assert response.status_code == 401
        assert "expired" in response.json()["error"].lower()
```

---

### 2.8 A08: ソフトウェアとデータの整合性の不備

#### **テストケース**

**署名検証テスト**:
```python
def test_jwt_signature_verification():
    """JWT署名検証のテスト"""
    # 正しい署名のトークン
    valid_token = create_jwt_token({"user_id": 123})
    response = client.get("/api/user/profile", headers={
        "Authorization": f"Bearer {valid_token}"
    })
    assert response.status_code == 200
    
    # 改ざんされたトークン
    tampered_token = valid_token[:-5] + "XXXXX"
    response = client.get("/api/user/profile", headers={
        "Authorization": f"Bearer {tampered_token}"
    })
    assert response.status_code == 401

def test_integrity_check_for_uploads():
    """アップロードファイルの整合性チェック"""
    file_content = b"test content"
    checksum = hashlib.sha256(file_content).hexdigest()
    
    response = client.post("/api/upload", files={
        "file": ("test.txt", file_content)
    }, data={
        "checksum": checksum
    })
    assert response.status_code == 200
    
    # 不正なチェックサム
    response = client.post("/api/upload", files={
        "file": ("test.txt", file_content)
    }, data={
        "checksum": "invalid_checksum"
    })
    assert response.status_code == 400
```

---

### 2.9 A09: セキュリティログとモニタリングの不備

#### **テストケース**

```python
def test_security_event_logging():
    """セキュリティイベントのログ記録テスト"""
    with capture_logs() as logs:
        # ログイン失敗
        client.post("/api/login", json={
            "username": "admin",
            "password": "wrongpassword"
        })
        
        # ログに記録されていることを確認
        assert any("login_failed" in log for log in logs)
        assert any("admin" in log for log in logs)
        assert any("wrongpassword" not in log for log in logs)  # パスワードは記録しない
    
    with capture_logs() as logs:
        # 権限外アクセス
        regular_user_token = get_regular_user_token()
        client.get("/api/admin/sensitive-data", headers={
            "Authorization": f"Bearer {regular_user_token}"
        })
        
        # アクセス拒否がログに記録されること
        assert any("access_denied" in log for log in logs)
        assert any("admin/sensitive-data" in log for log in logs)
```

---

### 2.10 A10: サーバーサイドリクエストフォージェリ（SSRF）

#### **テストケース**

```python
def test_ssrf_prevention():
    """SSRF攻撃の防止テスト"""
    # 内部IPへのアクセス試行
    malicious_urls = [
        "http://localhost/admin",
        "http://127.0.0.1/secrets",
        "http://169.254.169.254/latest/meta-data/",  # AWS metadata
        "http://10.0.0.1/internal",
        "file:///etc/passwd",
    ]
    
    for malicious_url in malicious_urls:
        response = client.post("/api/fetch-url", json={
            "url": malicious_url
        })
        # リクエストが拒否されること
        assert response.status_code == 400
        assert "invalid" in response.json()["error"].lower() or \
               "forbidden" in response.json()["error"].lower()
```

---

## 3. 静的アプリケーションセキュリティテスト（SAST）

### 3.1 SAST ツール

#### **言語別推奨ツール**

**Python**:
```yaml
必須:
  - bandit:          セキュリティ脆弱性スキャン
  - safety:          依存関係の脆弱性チェック
  - semgrep:         高度なパターンマッチング

推奨:
  - pysa:            Facebookの静的解析ツール
  - SonarQube:       総合的な品質・セキュリティ分析
```

**実行例**:
```bash
# Bandit
$ bandit -r src/ -f json -o bandit-report.json
$ bandit -r src/ -ll  # High/Mediumのみ

# Safety
$ safety check --json
$ safety check --full-report

# Semgrep
$ semgrep --config=p/security-audit src/
$ semgrep --config=p/owasp-top-ten src/
```

**TypeScript/JavaScript**:
```yaml
必須:
  - ESLint:          security plugin
  - npm audit:       依存関係スキャン
  - semgrep:         セキュリティルール

推奨:
  - SonarQube:       総合分析
  - Snyk Code:       深い静的解析
```

**Java**:
```yaml
必須:
  - SpotBugs:        バグ・脆弱性検出
  - Find Security Bugs: セキュリティ特化プラグイン
  - Dependency-Check: 依存関係スキャン

推奨:
  - SonarQube:       総合分析
  - Checkmarx:       エンタープライズ SAST
```

---

### 3.2 SAST CI/CD統合

**GitHub Actions例**:
```yaml
name: SAST Security Scan

on: [push, pull_request]

jobs:
  sast:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      # Python Bandit
      - name: Run Bandit Security Scan
        run: |
          pip install bandit
          bandit -r src/ -f json -o bandit-report.json
          bandit -r src/ -ll --exit-zero
      
      # Semgrep
      - name: Run Semgrep
        uses: returntocorp/semgrep-action@v1
        with:
          config: >-
            p/security-audit
            p/owasp-top-ten
            p/cwe-top-25
      
      # SonarQube
      - name: SonarQube Scan
        uses: sonarsource/sonarqube-scan-action@master
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      
      # 結果アップロード
      - name: Upload SAST Results
        uses: github/codeql-action/upload-sarif@v2
        with:
          sarif_file: bandit-report.json
```

---

## 4. 動的アプリケーションセキュリティテスト（DAST）

### 4.1 DAST ツール

**推奨ツール**:
```yaml
オープンソース:
  - OWASP ZAP:       包括的なスキャナー
  - Nuclei:          高速脆弱性スキャナー
  - W3af:            Webアプリケーション攻撃フレームワーク

商用:
  - Burp Suite Pro:  プロフェッショナルツール
  - Acunetix:        自動スキャナー
  - Netsparker:      自動検証機能付き
```

---

### 4.2 OWASP ZAP 自動スキャン

**スクリプト例**:
```bash
#!/bin/bash
# ZAP Baseline Scan

docker run -v $(pwd):/zap/wrk/:rw \
  -t owasp/zap2docker-stable zap-baseline.py \
  -t https://staging.example.com \
  -g gen.conf \
  -r zap-report.html \
  -J zap-report.json
```

**CI/CD統合**:
```yaml
# GitLab CI
zap_scan:
  stage: security
  image: owasp/zap2docker-stable
  script:
    - zap-baseline.py -t $STAGING_URL -r zap-report.html
  artifacts:
    reports:
      junit: zap-report.xml
    paths:
      - zap-report.html
  only:
    - staging
```

---

## 5. 依存関係の脆弱性管理

### 5.1 依存関係スキャンツール

**ツール比較**:
```yaml
Snyk:
  カバレッジ:    広い（多言語対応）
  脆弱性DB:      独自DB + NVD
  修正提案:      自動PR作成
  価格:          フリー〜エンタープライズ

Dependabot:
  カバレッジ:    GitHub統合
  脆弱性DB:      GitHub Advisory Database
  修正提案:      自動PR作成
  価格:          無料（GitHub統合）

npm audit / pip-audit:
  カバレッジ:    言語特化
  脆弱性DB:      公式DB
  修正提案:      コマンドライン
  価格:          無料
```

---

### 5.2 自動化設定

**Dependabot設定（.github/dependabot.yml）**:
```yaml
version: 2
updates:
  # npm
  - package-ecosystem: "npm"
    directory: "/"
    schedule:
      interval: "daily"
    open-pull-requests-limit: 10
    reviewers:
      - "security-team"
    labels:
      - "dependencies"
      - "security"
  
  # pip
  - package-ecosystem: "pip"
    directory: "/"
    schedule:
      interval: "daily"
    open-pull-requests-limit: 10
```

**Snyk設定**:
```yaml
# .snyk
version: v1.22.0
ignore:
  SNYK-JS-LODASH-590103:
    - '*':
        reason: 'Fix not available yet, low severity'
        expires: '2025-12-31T00:00:00.000Z'
```

---

## 6. APIセキュリティテスト

### 6.1 API セキュリティチェックリスト

```yaml
認証・認可:
  - [ ] すべてのエンドポイントで認証が必要
  - [ ] JWTトークンの署名検証
  - [ ] トークンの有効期限チェック
  - [ ] ロールベースのアクセス制御

入力検証:
  - [ ] すべての入力値の検証
  - [ ] パラメータの型チェック
  - [ ] 長さ制限の実施
  - [ ] SQLインジェクション対策

レート制限:
  - [ ] APIレート制限の実装
  - [ ] 429 Too Many Requests の返却
  - [ ] Retry-After ヘッダー

データ保護:
  - [ ] HTTPSの強制
  - [ ] 機密データのマスキング
  - [ ] PII情報の適切な処理
```

---

### 6.2 API セキュリティテストコード

```python
def test_api_authentication_required():
    """認証が必要なエンドポイントのテスト"""
    protected_endpoints = [
        "/api/users",
        "/api/orders",
        "/api/admin",
    ]
    
    for endpoint in protected_endpoints:
        # 認証なしでのアクセス
        response = client.get(endpoint)
        assert response.status_code == 401
        
        # 不正なトークン
        response = client.get(endpoint, headers={
            "Authorization": "Bearer invalid_token"
        })
        assert response.status_code == 401

def test_api_input_validation():
    """API入力検証のテスト"""
    # 不正な型
    response = client.post("/api/users", json={
        "age": "not_a_number",  # 数値型を期待
        "email": "invalid_email"  # メール形式を期待
    })
    assert response.status_code == 400
    assert "validation" in response.json()["error"].lower()
    
    # 長すぎる入力
    response = client.post("/api/users", json={
        "username": "a" * 1000  # 制限: 50文字
    })
    assert response.status_code == 400

def test_api_rate_limiting():
    """APIレート制限のテスト"""
    # 100req/minの制限を想定
    for i in range(101):
        response = client.get("/api/public-data")
    
    # 制限を超えた場合
    assert response.status_code == 429
    assert "Retry-After" in response.headers
```

---

## 7. 認証・認可のテスト

### 7.1 認証テストケース

```python
def test_password_storage():
    """パスワードが安全に保存されることを確認"""
    from database import User
    
    # ユーザー作成
    user = User.create(
        username="testuser",
        password="SecureP@ssw0rd123"
    )
    
    # データベースから取得
    stored_user = User.query.filter_by(username="testuser").first()
    
    # 平文パスワードが保存されていないこと
    assert stored_user.password != "SecureP@ssw0rd123"
    # bcrypt形式であること
    assert stored_user.password.startswith("$2b$")
    # 検証可能であること
    assert stored_user.verify_password("SecureP@ssw0rd123")

def test_token_expiration():
    """トークンの有効期限テスト"""
    token = create_access_token(user_id=123, expires_delta=timedelta(minutes=15))
    
    # 有効期限内
    with freeze_time(datetime.now() + timedelta(minutes=10)):
        assert verify_token(token) is not None
    
    # 有効期限切れ
    with freeze_time(datetime.now() + timedelta(minutes=20)):
        with pytest.raises(TokenExpiredError):
            verify_token(token)
```

---

## 8. シークレット管理とスキャン

### 8.1 シークレットスキャンツール

**推奨ツール**:
```yaml
gitleaks:
  用途:     シークレット検出
  速度:     高速
  誤検出:   低
  
truffleHog:
  用途:     Git履歴のスキャン
  速度:     中速
  検出力:   高
  
detect-secrets:
  用途:     Pre-commitフック
  速度:     高速
  カスタマイズ: 容易
```

---

### 8.2 Pre-commitフック設定

```yaml
# .pre-commit-config.yaml
repos:
  - repo: https://github.com/gitleaks/gitleaks
    rev: v8.18.0
    hooks:
      - id: gitleaks
        
  - repo: https://github.com/Yelp/detect-secrets
    rev: v1.4.0
    hooks:
      - id: detect-secrets
        args: ['--baseline', '.secrets.baseline']
```

---

## 9. ペネトレーションテスト

### 9.1 ペネトレーションテストの種類

**ブラックボックステスト**:
```yaml
実施者:    外部の知識なし
目的:      実際の攻撃者視点
期間:      長期（数週間）
コスト:    高
```

**ホワイトボックステスト**:
```yaml
実施者:    完全な内部知識あり
目的:      徹底的な脆弱性発見
期間:      中期（数日〜1週間）
コスト:    中
```

**グレーボックステスト**:
```yaml
実施者:    部分的な知識あり
目的:      バランスの取れた評価
期間:      中期
コスト:    中
```

---

### 9.2 ペネトレーションテスト計画

**実施スケジュール**:
```
1. 計画フェーズ（1週間）
   - スコープ定義
   - 目標設定
   - ルール策定
   
2. 情報収集（2-3日）
   - OSINT
   - ネットワークスキャン
   - アプリケーション調査
   
3. 脆弱性スキャン（2-3日）
   - 自動スキャン
   - 手動テスト
   
4. エクスプロイト（1週間）
   - 脆弱性の悪用試行
   - 権限昇格
   - データアクセス
   
5. 報告書作成（1週間）
   - 発見事項の文書化
   - リスク評価
   - 修正提案
```

---

## 10. CI/CDパイプライン統合

### 10.1 セキュリティパイプライン全体像

```yaml
stages:
  - pre-commit          # 開発者ローカル
  - commit              # コミット時
  - pull-request        # PR作成時
  - build               # ビルド時
  - test                # テスト時
  - security-scan       # セキュリティスキャン
  - deploy-staging      # ステージングデプロイ
  - penetration-test    # ペネトレーションテスト
  - deploy-production   # 本番デプロイ

pre-commit:
  - IDE統合SAST
  - Pre-commitフック（gitleaks）
  - ローカルリンター

commit:
  - シークレットスキャン
  - 基本的なSAST

pull-request:
  - SAST（全体）
  - SCA（依存関係）
  - ユニットセキュリティテスト
  - コードレビュー

build:
  - SAST
  - SCA
  - コンテナイメージスキャン

security-scan:
  - SAST（詳細）
  - DAST（軽量）
  - SCA（詳細）
  - シークレットスキャン

deploy-staging:
  - DAST（完全）
  - API セキュリティテスト
  - 設定スキャン

penetration-test:
  - 自動ペネトレーションテスト
  - 脆弱性スキャン

deploy-production:
  - 最終セキュリティゲート
  - コンプライアンスチェック
```

---

### 10.2 セキュリティゲート基準

```yaml
コミット前ゲート:
  ✅ シークレットなし
  ✅ 基本的なSAST: Critical 0件

PRマージゲート:
  ✅ SAST: Blocker 0件, Critical 0件
  ✅ SCA: High/Critical 0件
  ✅ セキュリティユニットテスト: All passing
  ✅ レビュー承認: 1名以上

ステージングデプロイゲート:
  ✅ DAST: High/Critical 0件
  ✅ コンテナスキャン: High/Critical 0件
  ✅ 設定スキャン: Critical 0件

本番デプロイゲート:
  ✅ すべてのセキュリティテスト合格
  ✅ ペネトレーションテスト完了
  ✅ コンプライアンスチェック合格
  ✅ セキュリティ責任者承認
```

---

## 11. セキュリティインシデント対応

### 11.1 脆弱性発見時の対応フロー

```
脆弱性発見
    ↓
リスク評価
    ↓
Critical/High → 緊急対応（24時間以内）
Medium       → 通常対応（1週間以内）
Low          → 計画的対応（次回スプリント）
    ↓
修正実装
    ↓
テスト・検証
    ↓
デプロイ
    ↓
事後分析・予防策
```

---

### 11.2 脆弱性の重要度評価

**CVSS（Common Vulnerability Scoring System）**:
```yaml
Critical（9.0-10.0）:
  対応期限:   即座（24時間以内）
  影響:       システム全体の侵害
  例:         リモートコード実行、管理者権限取得

High（7.0-8.9）:
  対応期限:   緊急（1週間以内）
  影響:       重要なデータ漏洩
  例:         SQLインジェクション、認証バイパス

Medium（4.0-6.9）:
  対応期限:   通常（1ヶ月以内）
  影響:       限定的な情報漏洩
  例:         XSS、情報開示

Low（0.1-3.9）:
  対応期限:   計画的（次回リリース）
  影響:       軽微
  例:         情報開示（非機密）、DoS（低影響）
```

---

## 12. Devin AIガイドライン

### 12.1 セキュアコード生成チェックリスト

**Devinが実装時に確認すべき項目**:

```yaml
入力検証:
  - [ ] すべての外部入力を検証
  - [ ] ホワイトリストベースの検証
  - [ ] 型チェックの実施
  - [ ] 長さ制限の設定

認証・認可:
  - [ ] すべての保護されたエンドポイントで認証チェック
  - [ ] ロールベースのアクセス制御
  - [ ] トークンの有効期限設定
  - [ ] セッション管理の適切な実装

データ保護:
  - [ ] パスワードのハッシュ化（bcrypt/argon2）
  - [ ] 機密データの暗号化
  - [ ] HTTPSの使用
  - [ ] ログに機密情報を含めない

SQLインジェクション対策:
  - [ ] パラメータ化クエリの使用
  - [ ] ORMの適切な使用
  - [ ] 動的SQLの回避

XSS対策:
  - [ ] 出力のエスケープ
  - [ ] CSPヘッダーの設定
  - [ ] innerHTML使用の回避

CSRF対策:
  - [ ] CSRFトークンの実装
  - [ ] SameSite Cookie属性
  - [ ] ステートフルな操作での検証
```

---

### 12.2 セキュリティテスト自動生成プロンプト

```
【タスク】
以下のAPIエンドポイントに対するセキュリティテストを作成してください。

【エンドポイント】
POST /api/users
- 機能: 新規ユーザー登録
- 入力: username, email, password
- 認証: 不要

【必須テスト項目】
1. 入力検証テスト
   - 不正な型の入力
   - 長すぎる入力（各フィールド）
   - 不正なメールアドレス形式
   - 弱いパスワード

2. SQLインジェクションテスト
   - 'OR'1'='1 等の入力
   - UNION SELECT 攻撃
   - コメントアウト攻撃

3. XSSテスト
   - <script>alert('XSS')</script>
   - イベントハンドラー注入

4. レート制限テスト
   - 連続100回のリクエスト
   - 429エラーの確認

5. セキュリティヘッダーテスト
   - Content-Type
   - X-Content-Type-Options
   - CSP

【出力フォーマット】
- pytest形式のテストコード
- 各テストケースに説明コメント
- アサーションの明確な記述
```

---

## 📊 クイックリファレンス

### セキュリティテストチェックリスト

| カテゴリー | チェック項目 | ツール | 頻度 |
|-----------|------------|--------|------|
| **SAST** | ソースコードスキャン | Bandit, ESLint, SpotBugs | 毎コミット |
| **SCA** | 依存関係スキャン | Snyk, Dependabot | 毎日 |
| **シークレット** | 認証情報漏洩チェック | gitleaks, truffleHog | 毎コミット |
| **DAST** | 実行時スキャン | OWASP ZAP, Nuclei | 毎デプロイ |
| **認証** | 認証・認可テスト | 自動テスト | 毎PR |
| **API** | APIセキュリティ | Postman, Burp Suite | 毎リリース |
| **ペンテスト** | 侵入テスト | 専門業者 | 四半期 |

---

## 📚 関連ドキュメント

### 内部リソース
- **セキュアコーディング**: `/07-security-compliance/secure-coding-practices.md`
- **認証・認可**: `/07-security-compliance/authentication-authorization.md`
- **データ保護**: `/07-security-compliance/data-protection.md`
- **脆弱性管理**: `/07-security-compliance/vulnerability-management.md`

### 外部参考資料
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [OWASP ZAP](https://www.zaproxy.org/)
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)

---

**最終更新**: 2025-10-27  
**次回レビュー予定**: 2026-01-27
