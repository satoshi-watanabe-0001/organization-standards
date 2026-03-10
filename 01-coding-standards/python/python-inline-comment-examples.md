# Python インラインコメント実装例

## 📖 概要

本ドキュメントは、Python固有のインラインコメント記述の具体例を提供します。

**共通原則**: [00-inline-comment-standards.md](../00-inline-comment-standards.md)を必ず参照してください。

---

## 🎯 Python特有のコメント場面

### 1. リスト内包表記

```python
# ❌ 悪い例: コメントなし（複雑なロジックが不明瞭）
active_users = [u for u in users if u.is_active and u.last_login > cutoff_date]

# ✅ 良い例: フィルタ条件の理由を説明
# 過去30日以内にログインがあるアクティブユーザーのみを抽出
# 理由: 休眠ユーザーへの通知送信を避けるため（コスト削減）
cutoff_date = datetime.now() - timedelta(days=30)
active_users = [u for u in users if u.is_active and u.last_login > cutoff_date]
```

---

### 2. デコレーター

```python
# ❌ 悪い例: デコレーターの意図が不明
@retry(max_attempts=3)
@timeout(seconds=30)
def fetch_user_data(user_id: int) -> dict:
    return external_api.get_user(user_id)

# ✅ 良い例: なぜそのデコレーターが必要かを説明
@retry(max_attempts=3)  # 外部API一時障害対策（ネットワーク瞬断を想定）
@timeout(seconds=30)    # API応答遅延時のハング防止（SLA: 30秒以内）
def fetch_user_data(user_id: int) -> dict:
    """外部APIからユーザーデータを取得"""
    return external_api.get_user(user_id)
```

---

### 3. 例外処理

```python
# ❌ 悪い例: catchするだけで理由がない
try:
    result = process_payment(order)
except PaymentError:
    pass

# ✅ 良い例: 例外を無視する理由を明記
try:
    result = process_payment(order)
except PaymentError as e:
    # 決済エラーは既にログに記録済み（PaymentServiceで処理）
    # ここでは注文ステータスを「決済失敗」に更新するのみ
    # 理由: ユーザーには決済画面で既にエラーメッセージが表示されているため
    order.status = OrderStatus.PAYMENT_FAILED
    order.save()
```

---

### 4. ジェネレーター

```python
# ❌ 悪い例: なぜジェネレーターを使うか不明
def read_large_file(filepath):
    with open(filepath) as f:
        for line in f:
            yield line.strip()

# ✅ 良い例: メモリ効率の理由を説明
def read_large_file(filepath):
    """
    大容量ファイルを行単位で読み込むジェネレーター
    
    理由: 一度にすべての行をメモリに読み込むと、数GB規模のファイルで
          OutOfMemoryErrorが発生する。ジェネレーターを使うことで、
          1行ずつ処理し、メモリ使用量を数MB以内に抑える。
    
    使用例: 日次で生成される10GB超のアクセスログ解析
    """
    with open(filepath) as f:
        for line in f:
            yield line.strip()
```

---

### 5. コンテキストマネージャー

```python
# ❌ 悪い例: with文の理由がない
with transaction():
    user.balance -= amount
    user.save()
    transaction_log.create(user_id=user.id, amount=amount)

# ✅ 良い例: トランザクションの必要性を説明
# トランザクション内で実行することで、残高更新とログ記録の原子性を保証
# 理由: どちらか一方だけが成功すると、会計が不整合になる
# （例: 残高は減ったがログに記録されない → 監査で検出不可）
with transaction():
    user.balance -= amount
    user.save()
    transaction_log.create(user_id=user.id, amount=amount)
```

---

## 🔢 複雑度が高いコード例

### 例1: ネストしたループ（複雑度12）

```python
def calculate_user_activity_score(users, activities, date_range):
    """ユーザーアクティビティスコアを計算"""
    scores = {}
    
    # 各ユーザーについて、指定期間内のアクティビティを集計
    # 複雑度が高い理由: ネストループ + 複数の条件分岐
    for user in users:
        user_score = 0
        
        # アクティビティタイプごとにスコアを計算
        # 理由: タイプによって重み付けが異なる（ビジネスロジック）
        for activity in activities:
            if activity.user_id != user.id:
                continue  # 他ユーザーのアクティビティはスキップ
            
            # 期間外のアクティビティは対象外
            if not (date_range.start <= activity.created_at <= date_range.end):
                continue
            
            # アクティビティタイプに応じたスコア加算
            # ビジネスルール: 投稿=10点、コメント=5点、いいね=1点
            if activity.type == "post":
                user_score += 10
            elif activity.type == "comment":
                user_score += 5
            elif activity.type == "like":
                user_score += 1
            
            # 連続アクティビティボーナス
            # 理由: 継続的な利用を促進するため（エンゲージメント向上施策）
            if activity.is_consecutive():
                user_score += 2
        
        scores[user.id] = user_score
    
    return scores
```

---

### 例2: 複雑な条件分岐（複雑度15）

```python
def determine_shipping_fee(order, customer, destination):
    """配送料を決定"""
    base_fee = 500  # 基本配送料
    
    # 配送料計算の複雑なビジネスルール
    # 理由: 複数の割引条件が組み合わさる（マーケティング戦略）
    
    # 条件1: プレミアム会員は送料無料
    # ビジネス要件: 会員特典としてリピート購入を促進
    if customer.is_premium_member:
        return 0
    
    # 条件2: 購入金額が5000円以上で送料無料
    # ビジネス要件: 客単価向上のため（平均購入額を引き上げる）
    if order.total_amount >= 5000:
        return 0
    
    # 条件3: 離島・遠隔地は追加料金
    # 理由: 運送会社の追加料金を転嫁（コスト回収）
    if destination.is_remote_area:
        base_fee += 300
    
    # 条件4: 大型商品は追加料金
    # 理由: 大型商品は特別配送が必要（サイズ制限）
    if order.has_large_items:
        base_fee += 500
    
    # 条件5: 初回購入者は半額
    # ビジネス要件: 新規顧客獲得キャンペーン（2025年Q4期間限定）
    if customer.is_first_time_buyer and datetime.now() < datetime(2025, 12, 31):
        base_fee = base_fee // 2
    
    # 条件6: クーポン適用
    # 理由: マーケティングキャンペーンでの送料割引
    if order.has_shipping_coupon:
        discount = order.shipping_coupon.discount_amount
        # クーポン割引額が配送料を超える場合は0円（負の値にしない）
        base_fee = max(0, base_fee - discount)
    
    return base_fee
```

---

## 🧩 Pythonic記法のコメント

### 1. スライス操作

```python
# ❌ 悪い例: スライスの意図が不明
data = items[::2]

# ✅ 良い例: スライスの目的を説明
# 偶数インデックスの要素のみを抽出（0, 2, 4, ...）
# 理由: センサーデータのノイズ除去（偶数番目のみが有効データ）
data = items[::2]

# ネガティブインデックスの説明
# 最後から3件を除外（直近3件は確定していないデータのため）
confirmed_data = items[:-3]
```

---

### 2. アンパック操作

```python
# ❌ 悪い例: アンパックの意図がない
first, *rest = items

# ✅ 良い例: アンパックの目的を説明
# 先頭要素をヘッダーとして扱い、残りをデータとして処理
# 理由: CSVファイルの1行目はカラム名、2行目以降がデータ
header, *rows = csv_lines
```

---

### 3. defaultdict / Counter

```python
# ❌ 悪い例: なぜdefaultdictを使うか不明
from collections import defaultdict
word_count = defaultdict(int)
for word in words:
    word_count[word] += 1

# ✅ 良い例: defaultdictを使う理由を説明
from collections import defaultdict

# defaultdictを使用する理由:
# 通常のdictでは、未登場の単語に対して word_count[word] += 1 を実行すると
# KeyErrorが発生する。if文でキーの存在確認をすると冗長になるため、
# defaultdict(int)を使用し、未登場の単語は自動的に0で初期化される
word_count = defaultdict(int)
for word in words:
    word_count[word] += 1
```

---

## 🐍 型ヒントとコメント

### 1. 複雑な型定義

```python
from typing import Dict, List, Optional, Union

# ❌ 悪い例: 型ヒントだけで意図が伝わらない
UserCache = Dict[int, Dict[str, Union[str, int, List[str]]]]

# ✅ 良い例: 型の構造と使用目的を説明
# ユーザーキャッシュの型定義
# 構造: {user_id: {"name": str, "age": int, "roles": List[str]}}
# 理由: Redisから取得したユーザー情報を型安全に扱うため
# 注意: rolesは必ず存在するが、空リストの可能性あり
UserCache = Dict[int, Dict[str, Union[str, int, List[str]]]]
```

---

### 2. Optional型の使用

```python
# ❌ 悪い例: Noneが返される条件が不明
def find_user(email: str) -> Optional[User]:
    return User.query.filter_by(email=email).first()

# ✅ 良い例: Noneが返される条件を明記
def find_user(email: str) -> Optional[User]:
    """
    メールアドレスでユーザーを検索
    
    Returns:
        User: ユーザーが見つかった場合
        None: 該当ユーザーが存在しない場合
             （削除済みユーザーも含む - 論理削除のため）
    """
    return User.query.filter_by(email=email, is_deleted=False).first()
```

---

## 🔧 マジックナンバー・定数

### 1. マジックナンバーの説明

```python
# ❌ 悪い例: 数値の意味が不明
if user.login_attempts >= 5:
    lock_account(user)

# ✅ 良い例: 定数化 + 理由を説明
# ログイン試行回数の上限
# ビジネスルール: 5回失敗でアカウントロック（ブルートフォース攻撃対策）
# セキュリティ要件: [REQ-SEC-006]
MAX_LOGIN_ATTEMPTS = 5

if user.login_attempts >= MAX_LOGIN_ATTEMPTS:
    lock_account(user)
```

---

### 2. タイムアウト・リトライ値

```python
# ❌ 悪い例: タイムアウト値の根拠がない
response = requests.get(url, timeout=10)

# ✅ 良い例: タイムアウト値の決定理由を説明
# タイムアウト値の設定根拠:
# - 外部API仕様: 95パーセンタイルで8秒以内に応答
# - 10秒に設定することで、ネットワーク遅延を考慮しつつ、
#   ハングアップを防ぐ（SLA: 10秒以内）
API_TIMEOUT_SECONDS = 10
response = requests.get(url, timeout=API_TIMEOUT_SECONDS)
```

---

## 🧪 Docstring vs インラインコメント

### Docstringの役割

```python
def calculate_discount(price: float, customer: Customer) -> float:
    """
    顧客向けの割引額を計算
    
    Args:
        price: 商品価格（税抜）
        customer: 顧客情報（会員ランク、購入履歴を含む）
    
    Returns:
        割引額（円）
    
    Raises:
        ValueError: 価格が0以下の場合
    
    Example:
        >>> customer = Customer(rank="gold", total_purchases=100000)
        >>> calculate_discount(10000, customer)
        1500.0  # ゴールド会員: 15%割引
    """
    if price <= 0:
        raise ValueError("価格は正の値である必要があります")
    
    # ここからインラインコメント開始
    # 会員ランクに応じた割引率
    # ビジネスルール: ゴールド15%, シルバー10%, ブロンズ5%
    discount_rate = 0.0
    if customer.rank == "gold":
        discount_rate = 0.15
    elif customer.rank == "silver":
        discount_rate = 0.10
    elif customer.rank == "bronze":
        discount_rate = 0.05
    
    # 累計購入額ボーナス（10万円以上でさらに5%）
    # 理由: 優良顧客へのロイヤルティプログラム
    if customer.total_purchases >= 100000:
        discount_rate += 0.05
    
    return price * discount_rate
```

---

## 🔍 デバッグ・開発用コメント

### 1. TODO/FIXME/HACK

```python
# TODO: [担当: 山田] [期限: 2025-12-01] Redis接続エラー時のフォールバック処理を実装
# 現在はRedis障害時にアプリケーション全体が停止する
# 対応方針: ローカルキャッシュへのフォールバック
def get_cached_data(key: str) -> Optional[dict]:
    return redis_client.get(key)

# FIXME: [Issue #1234] ユニコードサロゲートペアで文字化けが発生
# 再現条件: 絵文字「👨‍👩‍👧‍👦」を含む名前で保存すると、取得時に破損
# 暫定対応: 絵文字を除去（根本対応は別途検討）
def sanitize_name(name: str) -> str:
    return name.encode('utf-8', 'ignore').decode('utf-8')

# HACK: 本来はORM経由で取得すべきだが、パフォーマンス問題により生SQLを使用
# 背景: ORMのN+1問題で応答時間が5秒→30秒に悪化
# リスク: SQLインジェクションの可能性（パラメータバインディングで対策済み）
# TODO: ORMのeager loadingで最適化できるか検証（次スプリント）
query = "SELECT * FROM users WHERE department_id = %s"
users = db.execute(query, [department_id])
```

---

### 2. パフォーマンス最適化コメント

```python
# パフォーマンス最適化: リスト走査を避けるため、辞書でO(1)検索
# 変更前: O(n) - リストをループして検索
# 変更後: O(1) - 辞書のキー検索
# 効果: 10,000件のデータで、100ms → 1ms に改善
user_dict = {user.id: user for user in users}
target_user = user_dict.get(target_id)
```

---

## 🚫 避けるべきパターン

### 1. コードの繰り返し

```python
# ❌ 悪い例: コードを日本語に翻訳しただけ
# ユーザーのメールアドレスを取得
email = user.email
# メールアドレスが空でないかチェック
if email:
    # メールを送信
    send_email(email)

# ✅ 良い例: WHYを説明
# メールアドレスが未設定のユーザーには送信しない
# 理由: 無効なアドレスへの送信は、送信サービスの信頼度スコアを下げる
email = user.email
if email:
    send_email(email)
```

---

### 2. 古いコメント

```python
# ❌ 悪い例: 過去の仕様が残っている
# パスワードは6文字以上である必要がある（古い要件）
MIN_PASSWORD_LENGTH = 8  # 実際は8文字

# ✅ 良い例: 変更履歴を残す
# パスワードの最小文字数
# 変更履歴:
# - 2024-01-01: 6文字 → セキュリティ基準を満たさないため変更
# - 2025-01-01: 8文字 → 現在の基準（NIST推奨）
MIN_PASSWORD_LENGTH = 8
```

---

## ✅ レビューチェックリスト

Pythonコードレビュー時に確認:

- [ ] リスト内包表記・ジェネレーターに目的コメントがある
- [ ] デコレーターの使用理由が明記されている
- [ ] 例外を無視する理由が説明されている
- [ ] 複雑度10以上の関数に詳細コメントがある
- [ ] マジックナンバーが定数化され、理由が記載されている
- [ ] TODO/FIXME/HACKに担当者・期限・理由がある
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-inline-comment-standards.md](../00-inline-comment-standards.md) - 共通原則
- [python/test-comment-examples.md](test-comment-examples.md) - Pythonテストコメント例
- [python-coding-standards.md](python-coding-standards.md) - Python全体のコーディング規約
