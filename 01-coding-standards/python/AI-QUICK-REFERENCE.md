# Python Standards - AI Quick Reference 🤖

## 📋 概要

このドキュメントは、AI（Devin、ChatGPT、GitHub Copilot等）がPythonコードをレビュー・生成する際に参照すべきTOP 30チェック項目です。

**対象**: AI支援開発、コードレビュー、自動生成コード検証

---

## 🎯 TOP 30 チェック項目

### 1️⃣ 基本設定・ツール（02-setup-tools.md）

**C1. 仮想環境の使用**
- ✅ 全プロジェクトで`venv`または`virtualenv`を使用
- ✅ `requirements.txt`または`pyproject.toml`で依存関係を管理
- ❌ グローバルPython環境に直接インストールしない

**C2. コードフォーマット**
- ✅ `black`でコード整形（行長88文字）
- ✅ `isort`でimport文を自動整理
- ✅ プロジェクトルートに`pyproject.toml`設定

**C3. Linterの実行**
- ✅ `pylint`または`flake8`で静的解析
- ✅ `mypy`で型チェック（Python 3.6+）
- ✅ コミット前に必ず実行

---

### 2️⃣ 命名規則・スタイル（03-naming-style.md）

**C4. 命名規則遵守**
- ✅ クラス: `PascalCase` (例: `UserManager`)
- ✅ 関数/変数: `snake_case` (例: `calculate_total`)
- ✅ 定数: `UPPER_SNAKE_CASE` (例: `MAX_RETRY_COUNT`)
- ✅ プライベート: `_leading_underscore` (例: `_internal_method`)

**C5. 意味のある命名**
- ✅ 説明的な名前を使用 (例: `user_count` ✓, `uc` ✗)
- ✅ 動詞+名詞パターン (例: `get_user`, `validate_email`)
- ❌ 単一文字変数は避ける（ループカウンタ以外）

**C6. コメント規約の遵守（2025-11-14追加）** ✨
- ✅ **すべてのコメントを日本語で記述**（技術用語を除く）
- ✅ **WHY原則**：「WHAT」ではなく「WHY」を説明
- ✅ **複雑度10以上の関数**に詳細コメントを追加
- ✅ **テストコメント**：【テスト対象】【テストケース】【期待結果】【ビジネス要件】を明記
- ✅ TODO/FIXME/HACKに担当者・期限・理由を記載
- ❓ 詳細: [python-inline-comment-examples.md](python-inline-comment-examples.md) | [python-test-comment-examples.md](python-test-comment-examples.md)

**C7. Import順序**
```python
# 1. 標準ライブラリ
import os
import sys

# 2. サードパーティ
import numpy as np
import pandas as pd

# 3. ローカルモジュール
from myapp.models import User
```

---

### 3️⃣ プロジェクト構造（04-project-structure.md）

**C7. ディレクトリ構造**
```
project/
├── src/              # ソースコード
├── tests/            # テストコード
├── docs/             # ドキュメント
├── requirements.txt
└── pyproject.toml
```

**C8. モジュール分割**
- ✅ 単一責任原則：1ファイル1目的
- ✅ ファイルサイズ: 300-500行を目安
- ✅ `__init__.py`でパッケージAPIを定義

---

### 4️⃣ エラーハンドリング（05-error-handling.md）

**C9. 例外処理の基本**
```python
# ✅ Good: 具体的な例外をキャッチ
try:
    value = int(user_input)
except ValueError as e:
    logger.error(f"Invalid input: {e}")
    
# ❌ Bad: 広範囲すぎる例外
try:
    do_something()
except Exception:  # 避ける
    pass
```

**C10. カスタム例外**
- ✅ ドメイン固有の例外クラスを定義
- ✅ `Exception`を継承
- ✅ 詳細なエラーメッセージを提供

**C11. リソース管理**
```python
# ✅ Good: コンテキストマネージャ使用
with open('file.txt', 'r') as f:
    data = f.read()
    
# ❌ Bad: 手動クローズ
f = open('file.txt', 'r')
data = f.read()
f.close()  # 例外時にスキップされる可能性
```

---

### 5️⃣ テスト戦略（06-testing-qa.md）

**C12. テストカバレッジ**
- ✅ 最低80%のコードカバレッジ
- ✅ 重要パスは100%カバー
- ✅ `pytest-cov`で測定

**C13. テストの種類**
- ✅ Unit Test: 全関数・クラスをテスト
- ✅ Integration Test: モジュール間連携をテスト
- ✅ E2E Test: ユーザーシナリオをテスト

**C14. テストの原則**
```python
# ✅ Good: Arrange-Act-Assert
def test_user_creation():
    # Arrange
    username = "testuser"
    
    # Act
    user = User.create(username)
    
    # Assert
    assert user.username == username
    assert user.is_active
```

---

### 6️⃣ パフォーマンス（07/08-performance）

**C15. データ構造選択**
- ✅ リスト検索 → `set`に変更（O(n) → O(1)）
- ✅ 頻繁なpop(0) → `collections.deque`使用
- ✅ キーでソート → `dict`で順序保持（Python 3.7+）

**C16. ジェネレータ活用**
```python
# ✅ Good: メモリ効率的
def read_large_file(path):
    with open(path) as f:
        for line in f:
            yield process(line)
            
# ❌ Bad: 全データをメモリ展開
def read_large_file_bad(path):
    with open(path) as f:
        return [process(line) for line in f]
```

**C17. プロファイリング**
- ✅ `cProfile`でボトルネック特定
- ✅ `memory_profiler`でメモリ使用量測定
- ✅ 推測ではなく計測に基づく最適化

---

### 7️⃣ セキュリティ（09-security.md）

**C18. 入力検証**
```python
# ✅ Good: 入力を検証
def process_user_input(user_id: int):
    if not isinstance(user_id, int) or user_id <= 0:
        raise ValueError("Invalid user ID")
    # 処理...
```

**C19. SQLインジェクション対策**
```python
# ✅ Good: パラメータ化クエリ
cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))

# ❌ Bad: 文字列結合
cursor.execute(f"SELECT * FROM users WHERE id = {user_id}")
```

**C20. シークレット管理**
- ✅ 環境変数から読み込み
- ✅ `.env`ファイル使用（`.gitignore`に追加）
- ❌ コード内にハードコードしない

---

### 8️⃣ 監視・ログ（10-monitoring-logging.md）

**C21. ログレベル**
```python
# ✅ Good: 適切なレベル使用
logger.debug("Detailed debug info")
logger.info("Normal operation")
logger.warning("Potential issue")
logger.error("Error occurred")
logger.critical("System failure")
```

**C22. 構造化ログ**
```python
# ✅ Good: 構造化ログ
logger.info("User login", extra={
    "user_id": user.id,
    "ip_address": request.ip,
    "timestamp": datetime.now()
})
```

**C23. センシティブ情報**
- ❌ パスワード、トークン、個人情報をログに出力しない
- ✅ 必要な場合はマスキング処理

---

### 9️⃣ デプロイメント（11-deployment-cicd.md）

**C24. 環境分離**
- ✅ 開発/ステージング/本番環境を明確に分離
- ✅ 環境変数で設定を切り替え
- ✅ Docker使用で環境の再現性確保

**C25. CI/CD必須チェック**
```yaml
# ✅ 全てパス必須
- Linter (pylint/flake8)
- Type Check (mypy)
- Unit Tests (pytest)
- Security Scan (bandit)
- Code Coverage (>80%)
```

---

### 🔟 AI/ML（12-ai-ml-standards.md）

**C26. データバージョニング**
- ✅ DVC (Data Version Control) 使用
- ✅ データセット、モデル、実験をバージョン管理
- ✅ 再現性確保

**C27. モデル評価**
```python
# ✅ Good: 複数メトリクス評価
from sklearn.metrics import accuracy_score, f1_score, confusion_matrix

metrics = {
    "accuracy": accuracy_score(y_true, y_pred),
    "f1_score": f1_score(y_true, y_pred),
    "confusion_matrix": confusion_matrix(y_true, y_pred)
}
```

**C28. 実験管理**
- ✅ MLflow/Weights & Biases で実験トラッキング
- ✅ ハイパーパラメータ、メトリクス、モデルを記録

---

### 1️⃣1️⃣ Devin特有（13-devin-guidelines.md）

**C29. Devin指示の明確化**
```
# ✅ Good: 具体的な指示
「03-naming-style.mdに従って、user_manager.pyの命名規則を修正してください。
特にクラス名とメソッド名を確認し、PEP 8準拠にしてください。」

# ❌ Bad: 曖昧な指示
「コードを良くして」
```

**C30. Devin実行後の検証**
- ✅ 生成コードを必ずレビュー
- ✅ テストを実行して動作確認
- ✅ セキュリティチェック（bandit実行）

---

## 🚨 重大な禁止事項（絶対に避ける）

### 🔴 Critical Violations

1. **❌ グローバル変数の乱用**
   - 状態管理は明示的に（クラス、関数引数）

2. **❌ eval() / exec() の使用**
   - セキュリティリスク大

3. **❌ bare except**
   ```python
   # ❌ 絶対に避ける
   try:
       something()
   except:  # 何でもキャッチ
       pass
   ```

4. **❌ 変更可能なデフォルト引数**
   ```python
   # ❌ Bad
   def append_to(element, target=[]):
       target.append(element)
       return target
   
   # ✅ Good
   def append_to(element, target=None):
       if target is None:
           target = []
       target.append(element)
       return target
   ```

5. **❌ パスワード・シークレットのハードコード**

---

## 📖 詳細ドキュメント参照

各チェック項目の詳細は以下のファイルを参照：

| 項目 | ファイル |
|-----|---------|
| C1-C3 | [02-setup-tools.md](02-setup-tools.md) |
| C4-C6 | [03-naming-style.md](03-naming-style.md) |
| C7-C8 | [04-project-structure.md](04-project-structure.md) |
| C9-C11 | [05-error-handling.md](05-error-handling.md) |
| C12-C14 | [06-testing-qa.md](06-testing-qa.md) |
| C15-C17 | [07-performance-part1.md](07-performance-part1.md) |
| C18-C20 | [09-security.md](09-security.md) |
| C21-C23 | [10-monitoring-logging.md](10-monitoring-logging.md) |
| C24-C25 | [11-deployment-cicd.md](11-deployment-cicd.md) |
| C26-C28 | [12-ai-ml-standards.md](12-ai-ml-standards.md) |
| C29-C30 | [13-devin-guidelines.md](13-devin-guidelines.md) |

---

## 🔍 使用方法

### AIツールでの活用

**例1: コードレビュー依頼**
```
「AI-QUICK-REFERENCE.mdの30項目に基づいて、
以下のPythonコードをレビューしてください：

[コードを貼り付け]
```

**例2: コード生成指示**
```
「AI-QUICK-REFERENCE.mdの基準に従って、
ユーザー管理APIのPythonコードを生成してください。
特にC4-C6（命名規則）とC9-C11（エラーハンドリング）を重視してください。」
```

### 自動チェックスクリプト

```bash
# 基本チェック
black --check .
isort --check-only .
pylint src/
mypy src/
pytest --cov=src --cov-report=term

# セキュリティチェック
bandit -r src/

# 全てまとめて実行
./run-checks.sh
```

---

## 📊 チェック項目の優先度

| 優先度 | 項目 | 説明 |
|--------|------|------|
| 🔴 Critical | C9, C18-C20, 禁止事項 | セキュリティ、安定性に直結 |
| 🟠 High | C1-C3, C12-C14, C21-C23 | コード品質、保守性 |
| 🟡 Medium | C4-C8, C15-C17, C24-C25 | ベストプラクティス |
| 🟢 Low | C26-C30 | プロジェクト特化、AI支援 |

---

**最終更新**: 2025-11-13 | **Phase**: 10 | **Status**: ✅ Complete

**注意**: このリファレンスは定期的に更新されます。最新版を参照してください。
