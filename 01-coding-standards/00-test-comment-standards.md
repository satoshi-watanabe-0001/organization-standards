# テストコードコメント規約

## 📖 概要

本ドキュメントは、全言語共通のテストコードにおけるコメント記述規約を定義します。

### 目的

- **テストケースの意図を明確化**: コードを読まなくても、コメントだけでテストの目的・手順・検証内容が理解できること
- **保守性の向上**: テストが失敗した際、コメントから迅速に原因を特定できること
- **ドキュメントとしての機能**: テストコードが仕様書の役割を果たすこと

---

## 🌍 言語と記述ルール

### 必須要件

**すべてのコメントは日本語で記述すること**

```python
# ❌ 悪い例（英語）
# Test user registration with valid email

# ✅ 良い例（日本語）
# 【テスト対象】有効なメールアドレスでのユーザー登録
```

### 例外

以下の場合のみ英語使用を許可:
- 技術用語（API、OAuth、JWT等）
- ライブラリ固有の用語（pytest fixture、JUnit runner等）
- 国際的に確立された略語（HTTP、SQL等）

---

## 📝 テストコメントの構造

### 1. テストファイルヘッダー

各テストファイルの冒頭に、以下の情報を記述:

```python
"""
【テスト対象モジュール】: user_service.py
【テスト範囲】: ユーザー登録・認証・権限管理
【前提条件】: データベース接続が有効であること
【依存関係】: PostgreSQL 14以上、Redis 6以上
【最終更新】: 2025-11-14
"""
```

---

### 2. テストクラス/グループコメント

関連するテストケースをまとめる際のコメント:

```python
class TestUserRegistration:
    """
    【テスト分類】: ユーザー登録機能
    
    【テストシナリオ】:
    - 正常系: 有効な入力での登録成功
    - 異常系: メールアドレス重複、パスワード不正、必須項目欠損
    - 境界値: 最小/最大文字数、特殊文字含む入力
    
    【ビジネス要件】:
    - メールアドレスは一意であること
    - パスワードは8文字以上、英数字+記号を含むこと
    - 登録完了後、確認メールが送信されること
    """
```

---

### 3. 個別テストケースコメント

各テストメソッド/関数には、**4要素を明記**:

#### 必須コメント要素

```python
def test_register_user_with_valid_email():
    """
    【テスト対象】: 有効なメールアドレスでのユーザー登録
    
    【テストケース】:
    正規のフォーマット（user@example.com）でメールアドレスを指定し、
    その他すべての必須項目を有効な値で入力した場合、
    ユーザーが正常に登録されることを検証する。
    
    【期待結果】:
    - HTTPステータス201（Created）が返却される
    - データベースに新規ユーザーレコードが作成される
    - パスワードがハッシュ化されて保存される
    - 登録完了メールが送信キューに追加される
    
    【ビジネス要件】:
    [REQ-USER-001] ユーザーは有効なメールアドレスで登録できること
    [REQ-SEC-003] パスワードは平文保存禁止、bcryptでハッシュ化すること
    """
```

---

### 4. Given-When-Thenセクションコメント

テストの各段階に詳細コメントを追加:

```python
def test_register_user_with_duplicate_email():
    """
    【テスト対象】: メールアドレス重複時の登録拒否
    【テストケース】: 既存ユーザーと同じメールアドレスで登録を試みた場合、409エラーが返されることを検証
    【期待結果】: HTTPステータス409、エラーメッセージ「このメールアドレスは既に使用されています」
    【ビジネス要件】: [REQ-USER-002] メールアドレスは一意制約を持つこと
    """
    
    # === Given: 事前準備 ===
    # 既存ユーザーを作成（メールアドレス: existing@example.com）
    # データベースに直接INSERTし、アプリケーション層をバイパスすることで
    # テスト対象を「登録処理」のみに限定する
    existing_user = create_user(email="existing@example.com")
    
    # === When: 実行 ===
    # 同じメールアドレスで新規登録APIを呼び出す
    # この時点で、データベースの一意制約またはアプリケーション層のバリデーションが
    # 重複を検出し、登録を拒否することを期待
    response = client.post("/api/users", json={
        "email": "existing@example.com",  # 既存と同じメールアドレス
        "password": "NewPass123!",
        "name": "新規ユーザー"
    })
    
    # === Then: 検証 ===
    # HTTPステータスコード409（Conflict）が返されることを確認
    # 409は「リソースの状態が競合している」ことを示す標準ステータス
    assert response.status_code == 409
    
    # エラーメッセージが日本語で、ユーザーに分かりやすい内容であることを確認
    # フロントエンドがこのメッセージをそのまま表示することを想定
    assert response.json()["error"] == "このメールアドレスは既に使用されています"
    
    # データベースのユーザー数が増えていないことを確認
    # つまり、重複登録が実際に防止されたことを保証
    assert User.count() == 1  # 既存ユーザー1件のみ
```

---

## 🎯 コメントすべき重要ポイント

### 1. 境界値の選定理由

境界値テストでは、**なぜその値を選んだか**を必ず説明:

```python
def test_password_minimum_length():
    """
    【テスト対象】: パスワードの最小文字数制約
    【テストケース】: 7文字のパスワードで登録を試み、バリデーションエラーが返されることを検証
    【期待結果】: HTTPステータス400、エラーメッセージ「パスワードは8文字以上である必要があります」
    【ビジネス要件】: [REQ-SEC-002] パスワードは8文字以上であること
    """
    
    # 境界値として7文字を選定
    # 理由: 要件が「8文字以上」のため、境界値は7文字（不正）と8文字（正常）
    # 本テストでは不正側の境界値をテストし、正常に拒否されることを確認
    invalid_password = "Pass12!"  # 7文字（8文字未満）
    
    response = client.post("/api/users", json={
        "email": "user@example.com",
        "password": invalid_password,
        "name": "テストユーザー"
    })
    
    assert response.status_code == 400
    assert "8文字以上" in response.json()["error"]
```

---

### 2. モック・スタブの設定意図

テストダブル（モック、スタブ等）を使用する際は、**なぜそれが必要か**を説明:

```python
def test_user_registration_with_email_service_failure():
    """
    【テスト対象】: メール送信失敗時のユーザー登録処理
    【テストケース】: メール送信サービスが一時的に利用不可の場合でも、ユーザー登録自体は成功することを検証
    【期待結果】: ユーザーはデータベースに登録され、メール送信は非同期リトライキューに追加される
    【ビジネス要件】: [REQ-RELIABLE-001] 外部サービス障害時も主機能は継続すること
    """
    
    # モックの設定理由:
    # 実際のメール送信サービス（SendGrid）を呼び出すと、以下の問題が発生:
    # 1. テスト実行に時間がかかる（ネットワーク遅延）
    # 2. 外部サービスの状態に依存し、テストが不安定になる
    # 3. 実際にメールが送信され、テスト用メールアドレスが汚染される
    # 
    # モックで例外をraiseすることで、メール送信失敗シナリオを再現
    mock_email_service.send.side_effect = EmailServiceUnavailableError("Service temporarily down")
    
    response = client.post("/api/users", json={
        "email": "user@example.com",
        "password": "ValidPass123!",
        "name": "テストユーザー"
    })
    
    # ユーザー登録は成功する（メール送信失敗に影響されない）
    assert response.status_code == 201
    
    # メール送信タスクがリトライキューに追加されていることを確認
    # これにより、後でメール送信が再試行される
    assert retry_queue.contains(task_type="send_welcome_email", user_id=response.json()["id"])
```

---

### 3. 複雑なアサーションの説明

単純な等価比較以外のアサーションには、検証内容を説明:

```python
def test_get_user_list_pagination():
    """
    【テスト対象】: ユーザー一覧取得のページネーション
    【テストケース】: 100件のユーザーが存在する状態で、2ページ目（20件/ページ）を取得
    【期待結果】: 21〜40番目のユーザーが昇順で返される
    【ビジネス要件】: [REQ-PERF-001] 大量データでもパフォーマンスを維持すること
    """
    
    # 100件のユーザーを作成（ID: 1〜100）
    create_users(count=100)
    
    response = client.get("/api/users?page=2&per_page=20")
    users = response.json()["users"]
    
    # 20件が返されることを確認
    assert len(users) == 20
    
    # ページネーションが正しく機能しているかを検証:
    # 2ページ目なので、21番目（page=2の開始）から40番目（page=2の終了）のユーザーが
    # 取得されているはず
    # IDの昇順でソートされていることも同時に確認
    assert users[0]["id"] == 21  # 2ページ目の最初
    assert users[19]["id"] == 40  # 2ページ目の最後
    
    # すべてのユーザーIDが連続していることを確認
    # これにより、スキップや重複がないことを保証
    user_ids = [user["id"] for user in users]
    assert user_ids == list(range(21, 41))  # [21, 22, ..., 40]
```

---

### 4. 非同期処理・タイミング依存のテスト

時間やタイミングに依存する処理のコメント:

```python
def test_session_timeout_after_30_minutes():
    """
    【テスト対象】: セッションの自動タイムアウト
    【テストケース】: 最終アクセスから30分経過後、セッションが無効化されることを検証
    【期待結果】: タイムアウト後のAPI呼び出しで401エラーが返される
    【ビジネス要件】: [REQ-SEC-005] セッションは30分でタイムアウトすること
    """
    
    # ユーザーログインし、セッショントークンを取得
    token = login_user(email="user@example.com", password="ValidPass123!")
    
    # 時刻を30分進める
    # 理由: 実際に30分待つとテスト実行時間が非現実的になるため、
    # システム時刻をモックして時間経過をシミュレート
    # freeze_timeは時刻を固定し、tick()で指定時間進めることができる
    with freeze_time("2025-11-14 10:00:00") as frozen_time:
        # 初回アクセス（この時点でセッションの最終アクセス時刻が記録される）
        response = client.get("/api/profile", headers={"Authorization": f"Bearer {token}"})
        assert response.status_code == 200
        
        # 30分経過（29分59秒ではタイムアウトしないことを確認）
        frozen_time.tick(delta=timedelta(minutes=29, seconds=59))
        response = client.get("/api/profile", headers={"Authorization": f"Bearer {token}"})
        assert response.status_code == 200  # まだ有効
        
        # さらに2秒進めて、30分1秒経過させる
        frozen_time.tick(delta=timedelta(seconds=2))
        
        # この時点でセッションはタイムアウトし、401が返されるはず
        response = client.get("/api/profile", headers={"Authorization": f"Bearer {token}"})
        assert response.status_code == 401
        assert response.json()["error"] == "セッションがタイムアウトしました"
```

---

## 🚨 特殊ケースのコメントルール

### 1. 既知の問題・回避策

テストコードに既知のバグや制約がある場合:

```python
def test_user_profile_update_with_unicode():
    """
    【テスト対象】: ユニコード文字を含むプロフィール更新
    【テストケース】: 絵文字やCJK文字を含む名前でプロフィール更新が成功することを検証
    【期待結果】: プロフィールが正常に更新され、文字化けが発生しない
    【ビジネス要件】: [REQ-I18N-001] 多言語・絵文字をサポートすること
    """
    
    # TODO: 現在の実装では一部の絵文字（サロゲートペア）で文字化けが発生する
    # Issue #1234で修正予定（2025年Q1リリース予定）
    # 本テストは基本的な絵文字のみを対象とし、サロゲートペアは別テストで検証
    
    user = create_user(email="user@example.com")
    response = client.patch(f"/api/users/{user.id}", json={
        "name": "山田太郎 👨‍💻"  # 基本的な絵文字（U+1F468 U+200D U+1F4BB）
    })
    
    assert response.status_code == 200
    assert response.json()["name"] == "山田太郎 👨‍💻"
```

---

### 2. セキュリティ関連のテスト

セキュリティテストでは、攻撃シナリオと防御メカニズムを説明:

```python
def test_prevent_sql_injection_in_search():
    """
    【テスト対象】: 検索機能におけるSQLインジェクション防止
    【テストケース】: SQL文を含む悪意ある検索クエリを送信し、適切にエスケープされることを検証
    【期待結果】: SQLインジェクションが発生せず、安全に検索結果が返される
    【ビジネス要件】: [REQ-SEC-010] すべての入力値はサニタイズすること
    """
    
    # SQLインジェクション攻撃のシミュレーション:
    # 攻撃者が "'; DROP TABLE users; --" という文字列を検索に入力した場合、
    # 不適切な実装では以下のようなSQL文が生成されてしまう:
    #   SELECT * FROM users WHERE name LIKE '%'; DROP TABLE users; --%';
    # これによりusersテーブルが削除される
    malicious_query = "'; DROP TABLE users; --"
    
    response = client.get(f"/api/users/search?q={malicious_query}")
    
    # 正常にレスポンスが返される（エラーにならない）
    assert response.status_code == 200
    
    # SQLインジェクションが成功していないことを確認:
    # 1. usersテーブルが存在する（削除されていない）
    assert User.count() > 0
    
    # 2. 検索結果が空である（悪意あるクエリに一致するユーザーはいない）
    assert len(response.json()["users"]) == 0
    
    # 3. クエリがエスケープされ、文字列として扱われている
    # ログを確認すると、以下のようなプレースホルダーを使った安全なSQLが実行されているはず:
    #   SELECT * FROM users WHERE name LIKE ?
    #   パラメータ: ["%'; DROP TABLE users; --%"]
```

---

### 3. パフォーマンステスト

パフォーマンス要件と測定方法を明記:

```python
def test_bulk_user_import_performance():
    """
    【テスト対象】: 大量ユーザーの一括インポート処理
    【テストケース】: 10,000件のユーザーデータを一括インポートし、処理時間が30秒以内であることを検証
    【期待結果】: 30秒以内に完了し、すべてのユーザーが正常に登録される
    【ビジネス要件】: [REQ-PERF-003] 10,000件のインポートは30秒以内に完了すること
    """
    
    # 10,000件のユーザーデータを生成
    # メモリ効率を考慮し、ジェネレーターを使用
    user_data = generate_user_data(count=10000)
    
    # 処理時間を計測
    # パフォーマンス要件: 10,000件を30秒以内
    # バルクインサートを使用することで、1件ずつINSERTするより100倍高速
    start_time = time.time()
    
    response = client.post("/api/users/bulk-import", json={"users": user_data})
    
    elapsed_time = time.time() - start_time
    
    # 30秒以内に完了していることを確認
    assert elapsed_time < 30, f"処理時間が要件を超過: {elapsed_time}秒（要件: 30秒以内）"
    
    # すべてのユーザーが登録されていることを確認
    assert response.json()["imported_count"] == 10000
    assert User.count() == 10000
    
    # パフォーマンスログに記録（CI/CDで経時変化をモニタリング）
    log_performance_metric(
        test_name="bulk_user_import",
        records_count=10000,
        elapsed_time=elapsed_time,
        threshold=30
    )
```

---

## ❌ アンチパターン

### 避けるべきコメント例

#### 1. コードの繰り返し

```python
# ❌ 悪い例: コードをそのまま日本語に翻訳しただけ
# ユーザーを作成する
user = create_user()
# レスポンスのステータスコードが200であることを確認する
assert response.status_code == 200

# ✅ 良い例: WHYを説明
# 既存ユーザーを作成（権限テストのための事前データ）
user = create_user()
# 管理者権限がないため、アクセスが許可される（200 OK）
assert response.status_code == 200
```

---

#### 2. 曖昧な説明

```python
# ❌ 悪い例: 具体性に欠ける
def test_user_creation():
    """ユーザー作成のテスト"""
    # テストを実行
    result = create_user()
    # 確認
    assert result is not None

# ✅ 良い例: 具体的で明確
def test_create_user_with_valid_data():
    """
    【テスト対象】: 有効なデータでのユーザー作成
    【テストケース】: 必須項目（メール、パスワード、名前）をすべて指定した場合、
                      ユーザーが正常に作成され、データベースに保存されることを検証
    【期待結果】: ユーザーIDが発行され、作成日時が記録される
    【ビジネス要件】: [REQ-USER-001] 有効なデータでユーザー登録ができること
    """
    # 有効なユーザーデータ（すべての必須項目を含む）
    result = create_user(
        email="user@example.com",
        password="ValidPass123!",
        name="山田太郎"
    )
    
    # ユーザーが作成され、IDが発行されていることを確認
    assert result.id is not None
    # 作成日時が現在時刻に近いことを確認（±1秒の誤差を許容）
    assert abs((datetime.now() - result.created_at).total_seconds()) < 1
```

---

#### 3. 更新されていないコメント

```python
# ❌ 悪い例: コードと矛盾するコメント（過去の仕様が残っている）
def test_password_validation():
    """
    【テスト対象】: パスワードバリデーション
    【テストケース】: パスワードが6文字以上であることを確認  # ← 古い仕様（現在は8文字）
    """
    # 実際のコードは8文字でテストしている
    assert validate_password("Pass123!") == True  # 8文字

# ✅ 良い例: コードとコメントが一致
def test_password_minimum_length():
    """
    【テスト対象】: パスワードの最小文字数制約
    【テストケース】: パスワードが8文字以上であることを検証
    【期待結果】: 8文字以上の場合True、7文字以下の場合Falseが返される
    【ビジネス要件】: [REQ-SEC-002] パスワードは8文字以上であること
    【変更履歴】: 2025-11-01 - 最小文字数を6文字→8文字に変更（セキュリティ強化）
    """
    assert validate_password("Pass123!") == True   # 8文字（有効）
    assert validate_password("Pass12!") == False   # 7文字（無効）
```

---

## ✅ 推奨パターン

### 1. テーブル駆動テスト

複数のケースを一度にテストする場合のコメント:

```python
@pytest.mark.parametrize("email,expected_valid,reason", [
    # 【正常系】
    ("user@example.com", True, "標準的なメールアドレス形式"),
    ("user.name@example.co.jp", True, "ドット・サブドメイン含む形式"),
    ("user+tag@example.com", True, "プラスエイリアス形式（Gmail等で使用）"),
    
    # 【異常系: フォーマット不正】
    ("", False, "空文字列（必須項目欠損）"),
    ("invalid", False, "@記号なし"),
    ("@example.com", False, "ローカル部なし"),
    ("user@", False, "ドメイン部なし"),
    ("user @example.com", False, "スペース含む（RFC違反）"),
    
    # 【境界値】
    ("a@b.co", True, "最短形式（6文字）"),
    ("u" * 64 + "@example.com", True, "ローカル部最大長（64文字）"),
    ("u" * 65 + "@example.com", False, "ローカル部超過（65文字）"),
])
def test_email_validation(email, expected_valid, reason):
    """
    【テスト対象】: メールアドレスのバリデーション
    【テストケース】: 各種フォーマットのメールアドレスが正しく検証されることを確認
    【期待結果】: RFC 5322準拠のメールアドレスのみが有効と判定される
    【ビジネス要件】: [REQ-USER-003] メールアドレスは国際標準に準拠すること
    
    【テストケース詳細】:
    - 正常系: 一般的なフォーマット、プラスエイリアス対応
    - 異常系: 空文字、記号欠損、スペース含む等
    - 境界値: RFC規定の最小/最大長
    """
    # parametrizeのreasonパラメータが、このテストケースの意図を説明している
    result = validate_email(email)
    assert result == expected_valid, f"テスト失敗: {reason}"
```

---

### 2. フィクスチャの活用

テストデータ準備用のフィクスチャにもコメントを:

```python
@pytest.fixture
def sample_users():
    """
    【フィクスチャ】: サンプルユーザーデータセット
    
    【提供データ】:
    - admin_user: 管理者権限を持つユーザー（全機能アクセス可能）
    - normal_user: 一般ユーザー権限（参照のみ可能）
    - guest_user: ゲストユーザー（ログイン不可、公開情報のみ閲覧可能）
    
    【使用目的】:
    権限ベースのアクセス制御テストで使用。
    各ユーザーの権限レベルによって、APIレスポンスが適切に制限されることを検証。
    
    【注意事項】:
    - フィクスチャは各テストメソッド実行後に自動削除される（scope="function"）
    - パスワードはすべて "TestPass123!" で統一
    """
    admin = create_user(email="admin@example.com", role="admin")
    normal = create_user(email="user@example.com", role="user")
    guest = create_user(email="guest@example.com", role="guest")
    
    return {
        "admin_user": admin,
        "normal_user": normal,
        "guest_user": guest
    }
```

---

### 3. 統合テスト・E2Eテスト

複数のコンポーネントをまたぐテストのコメント:

```python
def test_user_registration_to_first_login_flow():
    """
    【テスト対象】: ユーザー登録から初回ログインまでの完全フロー
    
    【テストシナリオ】:
    1. ユーザーが登録フォームから情報を入力
    2. 登録APIが呼ばれ、データベースにユーザーが作成される
    3. 確認メールが送信される
    4. ユーザーがメール内のリンクをクリック（メール認証）
    5. アカウントが有効化される
    6. ユーザーがログインできる
    
    【期待結果】:
    - 各ステップが正常に完了する
    - メール認証前はログイン不可
    - メール認証後はログイン可能
    
    【ビジネス要件】:
    [REQ-USER-001] ユーザー登録機能
    [REQ-SEC-004] メールアドレス認証必須
    [REQ-AUTH-001] 認証済みユーザーのみログイン可能
    
    【テスト範囲】:
    - API層: /api/users (POST), /api/users/verify (GET), /api/auth/login (POST)
    - サービス層: UserService, EmailService, AuthService
    - データ層: User, EmailVerificationToken
    - 外部連携: メール送信サービス（モック）
    """
    
    # === Step 1: ユーザー登録 ===
    # ユーザーがWebフォームから登録情報を送信
    register_response = client.post("/api/users", json={
        "email": "newuser@example.com",
        "password": "ValidPass123!",
        "name": "新規ユーザー"
    })
    
    # 登録が成功し、HTTPステータス201が返されることを確認
    assert register_response.status_code == 201
    user_id = register_response.json()["id"]
    
    # === Step 2: 確認メール送信 ===
    # メール送信サービスが呼ばれ、確認リンクが含まれることを確認
    sent_emails = get_sent_emails()  # モックから送信されたメールを取得
    assert len(sent_emails) == 1
    
    # 確認メールの内容を検証
    verification_email = sent_emails[0]
    assert verification_email["to"] == "newuser@example.com"
    assert "アカウント確認" in verification_email["subject"]
    
    # メール本文から確認トークンを抽出
    # 実際のメールには以下のようなリンクが含まれる:
    # https://example.com/verify?token=abc123xyz
    verification_token = extract_token_from_email(verification_email["body"])
    
    # === Step 3: メール認証前のログイン試行（失敗することを期待） ===
    # この時点ではまだアカウントが有効化されていないため、ログインは拒否されるはず
    login_response = client.post("/api/auth/login", json={
        "email": "newuser@example.com",
        "password": "ValidPass123!"
    })
    
    # HTTPステータス403（Forbidden）が返される
    # 理由: メール認証が未完了のため
    assert login_response.status_code == 403
    assert "メールアドレスの確認が必要です" in login_response.json()["error"]
    
    # === Step 4: メールアドレス認証 ===
    # ユーザーがメール内のリンクをクリックしたことをシミュレート
    verify_response = client.get(f"/api/users/verify?token={verification_token}")
    
    # 認証が成功することを確認
    assert verify_response.status_code == 200
    assert verify_response.json()["message"] == "メールアドレスが確認されました"
    
    # データベースでユーザーのステータスが「有効」になっていることを確認
    user = User.get(user_id)
    assert user.email_verified == True
    assert user.email_verified_at is not None
    
    # === Step 5: メール認証後のログイン試行（成功することを期待） ===
    # 認証済みのため、今度はログインが成功するはず
    login_response = client.post("/api/auth/login", json={
        "email": "newuser@example.com",
        "password": "ValidPass123!"
    })
    
    # HTTPステータス200（OK）が返される
    assert login_response.status_code == 200
    
    # JWTトークンが発行されることを確認
    assert "access_token" in login_response.json()
    access_token = login_response.json()["access_token"]
    
    # 発行されたトークンが有効であることを確認
    # トークンを使って保護されたエンドポイントにアクセス
    profile_response = client.get(
        "/api/profile",
        headers={"Authorization": f"Bearer {access_token}"}
    )
    
    # プロフィール情報が取得できることを確認
    assert profile_response.status_code == 200
    assert profile_response.json()["email"] == "newuser@example.com"
    assert profile_response.json()["name"] == "新規ユーザー"
```

---

## 📚 言語別の実装例

各言語固有のテストフレームワーク・記法に応じた具体例は、以下のドキュメントを参照:

- **Python**: [python/test-comment-examples.md](python/test-comment-examples.md)
- **Java**: [java/test-comment-examples.md](java/test-comment-examples.md)
- **TypeScript**: [typescript/test-comment-examples.md](typescript/test-comment-examples.md)
- **SQL**: [sql/test-comment-examples.md](sql/test-comment-examples.md)

---

## 🔄 レビューチェックリスト

テストコードレビュー時に、以下を確認:

- [ ] すべてのテストメソッド/関数に4要素（対象・ケース・期待結果・要件）が記載されている
- [ ] Given-When-Thenの各セクションにコメントがある
- [ ] 境界値の選定理由が説明されている
- [ ] モック・スタブの使用理由が明記されている
- [ ] 複雑なアサーションに検証内容の説明がある
- [ ] すべてのコメントが日本語で記述されている
- [ ] コードとコメントが一致している（矛盾がない）
- [ ] コメントがWHYを説明している（WHATの繰り返しでない）

---

## 📝 更新履歴

- **2025-11-14**: 初版作成（インラインコメント規約との統合）
- **対象言語**: Python, Java, TypeScript, SQL

---

## 🔗 関連ドキュメント

- [00-inline-comment-standards.md](00-inline-comment-standards.md) - インラインコメント規約
- [00-general-principles.md](00-general-principles.md) - コーディング共通原則
- [言語別テストフレームワークガイド](../04-quality-standards/) - 各言語のテスト規約
