# Python テストコメント実装例

## 📖 概要

本ドキュメントは、Python（pytest/unittest）におけるテストコメントの具体例を提供します。

**共通原則**: [00-test-comment-standards.md](../00-test-comment-standards.md)を必ず参照してください。

---

## 🧪 pytestスタイル

### 1. 基本的なテストケース

```python
def test_create_user_with_valid_data():
    """
    【テスト対象】: 有効なデータでのユーザー作成
    
    【テストケース】:
    必須項目（メール、パスワード、名前）をすべて指定し、
    正常にユーザーが作成されることを検証する。
    
    【期待結果】:
    - ユーザーオブジェクトが返される
    - データベースにレコードが保存される
    - パスワードがハッシュ化される
    - IDと作成日時が自動設定される
    
    【ビジネス要件】:
    [REQ-USER-001] 有効なデータでユーザー登録ができること
    """
    
    # === Given: テストデータの準備 ===
    # 有効なユーザーデータ（すべての必須項目を含む）
    user_data = {
        "email": "test@example.com",
        "password": "SecurePass123!",
        "name": "テストユーザー"
    }
    
    # === When: ユーザー作成を実行 ===
    user = create_user(**user_data)
    
    # === Then: 結果を検証 ===
    # ユーザーオブジェクトが正常に生成されていることを確認
    assert user is not None
    assert user.email == "test@example.com"
    assert user.name == "テストユーザー"
    
    # パスワードがハッシュ化されていることを確認
    # セキュリティ要件: 平文パスワードは保存禁止
    assert user.password != "SecurePass123!"
    assert user.password.startswith("$2b$")  # bcryptハッシュの識別子
    
    # 自動設定項目の確認
    assert user.id is not None  # IDが発行されている
    assert user.created_at is not None  # 作成日時が記録されている
    
    # データベース永続化の確認
    # データベースから取得して、正しく保存されているか検証
    saved_user = User.query.get(user.id)
    assert saved_user.email == "test@example.com"
```

---

### 2. parametrizeを使ったテーブル駆動テスト

```python
@pytest.mark.parametrize("password,expected_valid,reason", [
    # 【正常系】
    ("ValidPass123!", True, "英数字+記号を含む8文字（最小要件）"),
    ("SecurePassword2025!", True, "長いパスワード（推奨）"),
    ("パスワード123!", True, "日本語を含むパスワード（許可）"),
    
    # 【異常系: 長さ不足】
    ("Pass12!", False, "7文字（8文字未満）"),
    ("Abc1!", False, "5文字（短すぎる）"),
    
    # 【異常系: 複雑度不足】
    ("password123!", False, "大文字なし"),
    ("PASSWORD123!", False, "小文字なし"),
    ("PasswordABC!", False, "数字なし"),
    ("Password123", False, "記号なし"),
    
    # 【境界値】
    ("Pass123!", True, "最小要件をちょうど満たす（8文字、英大小数記号）"),
    ("a" * 128 + "A1!", True, "最大長（128文字）"),
    ("a" * 129 + "A1!", False, "最大長超過（129文字）"),
])
def test_password_validation(password, expected_valid, reason):
    """
    【テスト対象】: パスワードバリデーション
    
    【テストケース】:
    様々なパターンのパスワードが、セキュリティポリシーに従って
    正しく検証されることを確認する。
    
    【期待結果】:
    - 正常系: 英数字+記号を含む8文字以上のパスワードが有効
    - 異常系: 要件を満たさないパスワードが無効
    - 境界値: 最小/最大長が正しく判定される
    
    【ビジネス要件】:
    [REQ-SEC-002] パスワードは8文字以上、英大文字・小文字・数字・記号を含むこと
    
    【テストケース詳細】:
    parametrizeの各行が1つのテストケースに対応。
    reasonパラメータで、各ケースの検証内容を説明。
    """
    # パスワードバリデーションを実行
    is_valid = validate_password(password)
    
    # 期待結果と一致することを確認
    # テスト失敗時にreasonが表示され、どのケースで失敗したか分かる
    assert is_valid == expected_valid, f"テスト失敗: {reason}"
```

---

### 3. フィクスチャの使用

```python
@pytest.fixture
def sample_user(db_session):
    """
    【フィクスチャ】: テスト用サンプルユーザー
    
    【提供データ】:
    - メールアドレス: test@example.com
    - パスワード: TestPass123!（ハッシュ化済み）
    - 権限: 一般ユーザー（管理者権限なし）
    
    【使用目的】:
    認証・認可のテストで、既存ユーザーが必要な場合に使用。
    フィクスチャを使うことで、テストごとにユーザー作成コードを
    重複して書く必要がなくなる。
    
    【スコープ】:
    function（各テスト関数ごとに新規作成・削除）
    
    【クリーンアップ】:
    テスト終了後、自動的にデータベースからロールバックされる。
    """
    user = User(
        email="test@example.com",
        password=hash_password("TestPass123!"),
        name="テストユーザー",
        role="user"
    )
    db_session.add(user)
    db_session.commit()
    
    yield user  # テストに提供
    
    # テスト後のクリーンアップは不要（db_sessionがロールバック）


def test_login_with_correct_password(sample_user):
    """
    【テスト対象】: 正しいパスワードでのログイン
    
    【テストケース】:
    登録済みユーザーが正しいパスワードでログインを試みた場合、
    認証が成功し、JWTトークンが発行されることを検証。
    
    【期待結果】:
    - 認証成功（True）
    - JWTトークンが返される
    - トークンにユーザーIDとメールアドレスが含まれる
    
    【ビジネス要件】:
    [REQ-AUTH-001] 正しい認証情報でログインできること
    """
    
    # === Given: 既存ユーザー（sample_userフィクスチャ） ===
    # フィクスチャにより、test@example.com のユーザーが準備済み
    
    # === When: ログイン試行 ===
    # 正しいメールアドレスとパスワードでログイン
    result = authenticate_user(
        email="test@example.com",
        password="TestPass123!"  # フィクスチャで設定したパスワード
    )
    
    # === Then: 認証成功を検証 ===
    # 認証が成功していることを確認
    assert result["success"] is True
    
    # JWTトークンが発行されていることを確認
    assert "token" in result
    token = result["token"]
    
    # トークンをデコードして、ペイロードを検証
    # トークンには、ユーザーIDとメールアドレスが含まれるべき
    payload = decode_jwt(token)
    assert payload["user_id"] == sample_user.id
    assert payload["email"] == "test@example.com"
```

---

### 4. モックを使ったテスト

```python
def test_send_welcome_email_on_registration(mocker):
    """
    【テスト対象】: ユーザー登録時のウェルカムメール送信
    
    【テストケース】:
    新規ユーザーが登録した際、ウェルカムメールが送信されることを検証。
    実際のメール送信サービスは呼び出さず、モックで代替する。
    
    【期待結果】:
    - ユーザー登録が成功する
    - send_emailメソッドが1回呼ばれる
    - 送信先がユーザーのメールアドレスである
    - 件名が「登録ありがとうございます」である
    
    【ビジネス要件】:
    [REQ-USER-002] 登録完了後、ウェルカムメールを送信すること
    """
    
    # === Given: メール送信サービスをモック ===
    # モック設定の理由:
    # 1. 実際のメール送信は時間がかかる（テストが遅くなる）
    # 2. 外部サービスの状態に依存しない（テストの安定性向上）
    # 3. 実際にメールが送信されない（テスト用アドレスの汚染を防ぐ）
    mock_email_service = mocker.patch("app.services.email_service.send_email")
    
    # === When: ユーザー登録を実行 ===
    user = create_user(
        email="newuser@example.com",
        password="ValidPass123!",
        name="新規ユーザー"
    )
    
    # === Then: メール送信が呼ばれたことを検証 ===
    # send_emailが1回だけ呼ばれたことを確認
    mock_email_service.assert_called_once()
    
    # 呼び出し時の引数を取得
    call_args = mock_email_service.call_args
    
    # メール送信先が正しいことを確認
    assert call_args.kwargs["to"] == "newuser@example.com"
    
    # 件名が正しいことを確認
    assert call_args.kwargs["subject"] == "登録ありがとうございます"
    
    # メール本文にユーザー名が含まれることを確認
    # パーソナライズされたメールであることを検証
    assert "新規ユーザー" in call_args.kwargs["body"]
```

---

### 5. 例外テスト

```python
def test_create_user_with_duplicate_email_raises_error():
    """
    【テスト対象】: メールアドレス重複時のエラー
    
    【テストケース】:
    既存ユーザーと同じメールアドレスでユーザー作成を試みた場合、
    DuplicateEmailError例外が発生することを検証。
    
    【期待結果】:
    - DuplicateEmailError例外がraiseされる
    - エラーメッセージが「このメールアドレスは既に使用されています」
    - データベースにユーザーが追加されない（1件のまま）
    
    【ビジネス要件】:
    [REQ-USER-003] メールアドレスは一意であること
    """
    
    # === Given: 既存ユーザーを作成 ===
    # メールアドレス: existing@example.com のユーザーを作成
    existing_user = create_user(
        email="existing@example.com",
        password="Pass123!",
        name="既存ユーザー"
    )
    
    # === When & Then: 同じメールアドレスで作成し、例外を期待 ===
    # pytest.raisesコンテキストマネージャーで例外をキャッチ
    with pytest.raises(DuplicateEmailError) as exc_info:
        create_user(
            email="existing@example.com",  # 既存と同じメールアドレス
            password="NewPass456!",
            name="新規ユーザー"
        )
    
    # 例外メッセージが適切であることを確認
    # ユーザーに表示されるエラーメッセージなので、日本語で分かりやすい内容
    assert str(exc_info.value) == "このメールアドレスは既に使用されています"
    
    # データベースにユーザーが追加されていないことを確認
    # つまり、重複登録が実際に防止された
    assert User.query.count() == 1  # 既存ユーザー1件のみ
```

---

## 🔄 非同期テスト（asyncio）

### 1. async関数のテスト

```python
@pytest.mark.asyncio
async def test_async_fetch_user_data():
    """
    【テスト対象】: 非同期ユーザーデータ取得
    
    【テストケース】:
    外部APIから非同期でユーザーデータを取得し、
    正しいデータが返されることを検証。
    
    【期待結果】:
    - ユーザーデータが正常に取得される
    - レスポンスタイムが3秒以内（非同期の効果）
    - 複数リクエストを並列実行できる
    
    【ビジネス要件】:
    [REQ-PERF-001] 外部API呼び出しは非同期で実行し、レスポンスタイムを短縮すること
    """
    
    # === Given: 外部APIモック ===
    # 非同期関数をモック化する理由:
    # - 実際のAPI呼び出しは遅い（ネットワーク遅延）
    # - 外部サービスの状態に依存しない
    with aioresponses() as mocked:
        mocked.get(
            "https://api.example.com/users/123",
            payload={"id": 123, "name": "API User"},
            status=200
        )
        
        # === When: 非同期で取得 ===
        start_time = time.time()
        user_data = await fetch_user_data(user_id=123)
        elapsed_time = time.time() - start_time
        
        # === Then: 結果検証 ===
        # データが正しく取得されていることを確認
        assert user_data["id"] == 123
        assert user_data["name"] == "API User"
        
        # レスポンスタイムが3秒以内であることを確認
        # 非同期処理により、同期処理より高速であることを検証
        assert elapsed_time < 3.0, f"レスポンスタイムが遅い: {elapsed_time}秒"
```

---

## 🗄️ データベーステスト

### 1. トランザクション制御

```python
def test_rollback_on_error(db_session):
    """
    【テスト対象】: エラー時のトランザクションロールバック
    
    【テストケース】:
    ユーザー作成中にエラーが発生した場合、
    トランザクションがロールバックされ、データベースに影響がないことを検証。
    
    【期待結果】:
    - 例外が発生する
    - データベースにユーザーが追加されない
    - トランザクションが正しくロールバックされる
    
    【ビジネス要件】:
    [REQ-DATA-001] エラー時はトランザクションをロールバックし、データ整合性を保つこと
    """
    
    # === Given: 初期状態のユーザー数を記録 ===
    initial_count = User.query.count()
    
    # === When: エラーが発生するユーザー作成 ===
    # 無効なメールアドレスでユーザー作成を試みる
    # データベース制約違反によりエラーが発生するはず
    with pytest.raises(InvalidEmailError):
        with db_session.begin():
            user = User(
                email="invalid-email",  # 無効な形式
                password="Pass123!",
                name="テストユーザー"
            )
            db_session.add(user)
            db_session.flush()  # ここでバリデーションエラー
    
    # === Then: ロールバック確認 ===
    # ユーザー数が変わっていないことを確認
    # つまり、トランザクションが正しくロールバックされた
    assert User.query.count() == initial_count
    
    # データベース接続が正常であることを確認
    # ロールバック後も、データベース操作が可能
    test_user = create_user(
        email="valid@example.com",
        password="Pass123!",
        name="正常ユーザー"
    )
    assert test_user.id is not None
```

---

## 🔐 セキュリティテスト

### 1. SQLインジェクション対策

```python
def test_prevent_sql_injection_in_user_search():
    """
    【テスト対象】: ユーザー検索におけるSQLインジェクション対策
    
    【テストケース】:
    SQLインジェクション攻撃を試み、適切にエスケープされることを検証。
    攻撃者が悪意あるSQL文を検索クエリに含めても、
    データベースに影響がないことを確認。
    
    【期待結果】:
    - SQLインジェクションが成功しない
    - データベースが破壊されない
    - 安全に検索結果が返される
    
    【ビジネス要件】:
    [REQ-SEC-010] すべての入力値はサニタイズし、SQLインジェクションを防止すること
    """
    
    # === Given: テスト用ユーザーを作成 ===
    create_user(email="user1@example.com", password="Pass1!", name="ユーザー1")
    create_user(email="user2@example.com", password="Pass2!", name="ユーザー2")
    initial_user_count = User.query.count()
    
    # === When: SQLインジェクション攻撃を試行 ===
    # 攻撃シミュレーション: "'; DROP TABLE users; --" という文字列を検索
    # 不適切な実装では、以下のSQL文が実行されてしまう:
    #   SELECT * FROM users WHERE name LIKE '%'; DROP TABLE users; --%';
    # この場合、usersテーブルが削除される
    malicious_query = "'; DROP TABLE users; --"
    
    # 検索を実行
    # 正しい実装では、プレースホルダーを使用し、文字列としてエスケープされる
    results = search_users(query=malicious_query)
    
    # === Then: セキュリティ検証 ===
    # 1. 検索結果が安全に返される（エラーにならない）
    assert isinstance(results, list)
    
    # 2. 悪意あるクエリに一致するユーザーはいない（空の結果）
    assert len(results) == 0
    
    # 3. データベースが破壊されていないことを確認
    # usersテーブルが削除されていない
    assert User.query.count() == initial_user_count
    
    # 4. 正常な検索が可能であることを確認
    # つまり、テーブル構造が保持されている
    normal_results = search_users(query="ユーザー1")
    assert len(normal_results) == 1
    assert normal_results[0].name == "ユーザー1"
```

---

## 📊 パフォーマンステスト

### 1. レスポンスタイム測定

```python
def test_bulk_user_creation_performance():
    """
    【テスト対象】: 大量ユーザー作成のパフォーマンス
    
    【テストケース】:
    1,000件のユーザーを一括作成し、処理時間が5秒以内であることを検証。
    バルクインサートを使用することで、1件ずつ作成するより高速であることを確認。
    
    【期待結果】:
    - 1,000件のユーザーが作成される
    - 処理時間が5秒以内
    - メモリ使用量が適切（メモリリークなし）
    
    【ビジネス要件】:
    [REQ-PERF-002] 1,000件のユーザー一括作成は5秒以内に完了すること
    """
    
    # === Given: 1,000件のユーザーデータを生成 ===
    # メモリ効率を考慮し、ジェネレーターを使用
    # 理由: すべてのデータをリストで保持すると、メモリを大量に消費する
    user_data_list = [
        {
            "email": f"user{i}@example.com",
            "password": "Pass123!",
            "name": f"ユーザー{i}"
        }
        for i in range(1000)
    ]
    
    # === When: バルクインサートを実行 ===
    # 処理時間を計測
    start_time = time.time()
    
    # バルクインサートの利点:
    # - 1件ずつINSERTするより100倍高速
    # - データベース接続の回数が減る
    # - トランザクションオーバーヘッドが減る
    bulk_create_users(user_data_list)
    
    elapsed_time = time.time() - start_time
    
    # === Then: パフォーマンス検証 ===
    # 処理時間が要件（5秒）を満たすことを確認
    assert elapsed_time < 5.0, (
        f"パフォーマンス要件を満たしていません: "
        f"{elapsed_time:.2f}秒（要件: 5秒以内）"
    )
    
    # すべてのユーザーが作成されたことを確認
    assert User.query.count() == 1000
    
    # パフォーマンスメトリクスをログに記録
    # CI/CDで経時変化をモニタリングし、パフォーマンス劣化を検出
    log_performance_metric(
        test_name="bulk_user_creation",
        records_count=1000,
        elapsed_time=elapsed_time,
        threshold=5.0
    )
```

---

## ✅ レビューチェックリスト

Pythonテストコードレビュー時に確認:

- [ ] すべてのテスト関数にdocstringがある
- [ ] 4要素（対象・ケース・期待結果・要件）が記載されている
- [ ] Given-When-Thenセクションにコメントがある
- [ ] フィクスチャに使用目的が説明されている
- [ ] モックを使う理由が明記されている
- [ ] parametrizeの各ケースに説明がある
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-test-comment-standards.md](../00-test-comment-standards.md) - 共通テストコメント原則
- [python/inline-comment-examples.md](inline-comment-examples.md) - Pythonインラインコメント例
- [python-testing-guide.md](python-testing-guide.md) - Pythonテスト全体のガイド
