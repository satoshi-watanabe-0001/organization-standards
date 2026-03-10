# Java インラインコメント実装例

## 📖 概要

本ドキュメントは、Java固有のインラインコメント記述の具体例を提供します。

**共通原則**: [00-inline-comment-standards.md](../00-inline-comment-standards.md)を必ず参照してください。

---

## 🎯 Java特有のコメント場面

### 1. Stream API

```java
// ❌ 悪い例: コメントなし（複雑なStream処理が不明瞭）
List<User> activeUsers = users.stream()
    .filter(u -> u.isActive() && u.getLastLogin().isAfter(cutoffDate))
    .collect(Collectors.toList());

// ✅ 良い例: フィルタ条件の理由を説明
// 過去30日以内にログインがあるアクティブユーザーのみを抽出
// 理由: 休眠ユーザーへの通知送信を避けるため（コスト削減）
LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
List<User> activeUsers = users.stream()
    .filter(u -> u.isActive() && u.getLastLogin().isAfter(cutoffDate))
    .collect(Collectors.toList());
```

---

### 2. アノテーション

```java
// ❌ 悪い例: アノテーションの意図が不明
@Transactional
@Cacheable("users")
@PreAuthorize("hasRole('ADMIN')")
public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow();
}

// ✅ 良い例: 各アノテーションの目的を説明
/**
 * IDでユーザーを取得
 * 
 * @param id ユーザーID
 * @return ユーザー情報
 */
@Transactional(readOnly = true)  // 読み取り専用トランザクション（パフォーマンス最適化）
@Cacheable("users")              // Redis キャッシュ（頻繁にアクセスされるため）
@PreAuthorize("hasRole('ADMIN')")  // 管理者のみアクセス可能（機密情報保護）
public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow();
}
```

---

### 3. 例外処理

```java
// ❌ 悪い例: 例外を無視する理由がない
try {
    processPayment(order);
} catch (PaymentException e) {
    // Do nothing
}

// ✅ 良い例: 例外を無視する理由を明記
try {
    processPayment(order);
} catch (PaymentException e) {
    // 決済エラーは既にログに記録済み（PaymentServiceで処理）
    // ここでは注文ステータスを「決済失敗」に更新するのみ
    // 理由: ユーザーには決済画面で既にエラーメッセージが表示されているため
    order.setStatus(OrderStatus.PAYMENT_FAILED);
    orderRepository.save(order);
}
```

---

### 4. Optional型

```java
// ❌ 悪い例: Optional使用の意図が不明
Optional<User> user = findUser(email);
if (user.isPresent()) {
    return user.get();
}
return null;

// ✅ 良い例: Optional使用とフォールバック戦略を説明
// Optionalを使用する理由:
// - NPEを防ぐため（nullチェック漏れによるバグ回避）
// - 「値が存在しないかもしれない」ことを型で明示
// 
// フォールバック戦略:
// ユーザーが見つからない場合、ゲストユーザーを返す
// （認証不要の公開ページでの使用を想定）
Optional<User> user = findUser(email);
return user.orElseGet(() -> createGuestUser());
```

---

### 5. ラムダ式・メソッド参照

```java
// ❌ 悪い例: なぜラムダ式を使うか不明
users.forEach(u -> sendEmail(u));

// ✅ 良い例: ラムダ式の意図とパフォーマンス考慮を説明
// ラムダ式でメール送信処理を並列化
// 理由: ユーザー数が多い（数千件）場合、順次処理では時間がかかる
// 並列処理により、送信時間を1/4に短縮（4コアCPU想定）
users.parallelStream()
    .forEach(user -> sendEmail(user));
```

---

## 🔢 複雑度が高いコード例

### 例1: ネストしたループ（複雑度12）

```java
/**
 * ユーザーアクティビティスコアを計算
 */
public Map<Long, Integer> calculateUserActivityScore(
        List<User> users, 
        List<Activity> activities, 
        DateRange dateRange) {
    
    Map<Long, Integer> scores = new HashMap<>();
    
    // 各ユーザーについて、指定期間内のアクティビティを集計
    // 複雑度が高い理由: ネストループ + 複数の条件分岐
    for (User user : users) {
        int userScore = 0;
        
        // アクティビティタイプごとにスコアを計算
        // 理由: タイプによって重み付けが異なる（ビジネスロジック）
        for (Activity activity : activities) {
            if (!activity.getUserId().equals(user.getId())) {
                continue;  // 他ユーザーのアクティビティはスキップ
            }
            
            // 期間外のアクティビティは対象外
            if (!dateRange.contains(activity.getCreatedAt())) {
                continue;
            }
            
            // アクティビティタイプに応じたスコア加算
            // ビジネスルール: 投稿=10点、コメント=5点、いいね=1点
            switch (activity.getType()) {
                case POST:
                    userScore += 10;
                    break;
                case COMMENT:
                    userScore += 5;
                    break;
                case LIKE:
                    userScore += 1;
                    break;
            }
            
            // 連続アクティビティボーナス
            // 理由: 継続的な利用を促進するため（エンゲージメント向上施策）
            if (activity.isConsecutive()) {
                userScore += 2;
            }
        }
        
        scores.put(user.getId(), userScore);
    }
    
    return scores;
}
```

---

### 例2: 複雑な条件分岐（複雑度15）

```java
/**
 * 配送料を決定
 */
public BigDecimal determineShippingFee(Order order, Customer customer, Address destination) {
    BigDecimal baseFee = new BigDecimal("500");  // 基本配送料
    
    // 配送料計算の複雑なビジネスルール
    // 理由: 複数の割引条件が組み合わさる（マーケティング戦略）
    
    // 条件1: プレミアム会員は送料無料
    // ビジネス要件: 会員特典としてリピート購入を促進
    if (customer.isPremiumMember()) {
        return BigDecimal.ZERO;
    }
    
    // 条件2: 購入金額が5000円以上で送料無料
    // ビジネス要件: 客単価向上のため（平均購入額を引き上げる）
    if (order.getTotalAmount().compareTo(new BigDecimal("5000")) >= 0) {
        return BigDecimal.ZERO;
    }
    
    // 条件3: 離島・遠隔地は追加料金
    // 理由: 運送会社の追加料金を転嫁（コスト回収）
    if (destination.isRemoteArea()) {
        baseFee = baseFee.add(new BigDecimal("300"));
    }
    
    // 条件4: 大型商品は追加料金
    // 理由: 大型商品は特別配送が必要（サイズ制限）
    if (order.hasLargeItems()) {
        baseFee = baseFee.add(new BigDecimal("500"));
    }
    
    // 条件5: 初回購入者は半額
    // ビジネス要件: 新規顧客獲得キャンペーン（2025年Q4期間限定）
    if (customer.isFirstTimeBuyer() && 
        LocalDateTime.now().isBefore(LocalDateTime.of(2025, 12, 31, 23, 59))) {
        baseFee = baseFee.divide(new BigDecimal("2"), RoundingMode.HALF_UP);
    }
    
    // 条件6: クーポン適用
    // 理由: マーケティングキャンペーンでの送料割引
    if (order.hasShippingCoupon()) {
        BigDecimal discount = order.getShippingCoupon().getDiscountAmount();
        // クーポン割引額が配送料を超える場合は0円（負の値にしない）
        baseFee = baseFee.subtract(discount).max(BigDecimal.ZERO);
    }
    
    return baseFee;
}
```

---

## 🧩 Java特有の記法のコメント

### 1. Builder パターン

```java
// ❌ 悪い例: Builder使用の理由が不明
User user = User.builder()
    .email("user@example.com")
    .name("テストユーザー")
    .build();

// ✅ 良い例: Builder使用の理由を説明
// Builderパターンを使用する理由:
// 1. コンストラクタの引数が多い（10個以上）場合、可読性が悪化
// 2. 必須パラメータと任意パラメータを明確に区別
// 3. イミュータブルオブジェクトの生成を簡潔に記述
User user = User.builder()
    .email("user@example.com")    // 必須
    .name("テストユーザー")         // 必須
    .phoneNumber("090-1234-5678")  // 任意
    .build();
```

---

### 2. Enum

```java
// ❌ 悪い例: Enum値の意味が不明
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

// ✅ 良い例: 各Enum値の具体的な意味を説明
/**
 * ユーザーステータス
 */
public enum UserStatus {
    /**
     * アクティブ: 通常利用可能
     * - ログイン可能
     * - すべての機能にアクセス可能
     */
    ACTIVE,
    
    /**
     * 非アクティブ: 休眠状態
     * - 90日以上ログインなし
     * - ログインは可能だが、警告メッセージが表示される
     */
    INACTIVE,
    
    /**
     * 停止中: アカウント凍結
     * - 利用規約違反により管理者が凍結
     * - ログイン不可
     * - 復旧には管理者の承認が必要
     */
    SUSPENDED
}
```

---

### 3. Generic型

```java
// ❌ 悪い例: Generic型の制約理由が不明
public <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
    return list.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}

// ✅ 良い例: Generic型使用とワイルドカードの説明
/**
 * リストをフィルタリング
 * 
 * Generic型を使用する理由:
 * - 型安全性を保ちながら、任意の型のリストに対応
 * - User, Order, Product等、様々なエンティティで再利用可能
 * 
 * @param <T> リスト要素の型（制約なし）
 * @param list フィルタ対象のリスト
 * @param predicate フィルタ条件
 * @return フィルタ結果
 */
public <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
    return list.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

---

## 🔧 マジックナンバー・定数

### 1. マジックナンバーの説明

```java
// ❌ 悪い例: 数値の意味が不明
if (user.getLoginAttempts() >= 5) {
    lockAccount(user);
}

// ✅ 良い例: 定数化 + 理由を説明
// ログイン試行回数の上限
// ビジネスルール: 5回失敗でアカウントロック（ブルートフォース攻撃対策）
// セキュリティ要件: [REQ-SEC-006]
private static final int MAX_LOGIN_ATTEMPTS = 5;

if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
    lockAccount(user);
}
```

---

### 2. タイムアウト・リトライ値

```java
// ❌ 悪い例: タイムアウト値の根拠がない
RestTemplate restTemplate = new RestTemplate();
restTemplate.getForObject(url, String.class);

// ✅ 良い例: タイムアウト値の決定理由を説明
// タイムアウト値の設定根拠:
// - 外部API仕様: 95パーセンタイルで8秒以内に応答
// - 10秒に設定することで、ネットワーク遅延を考慮しつつ、
//   ハングアップを防ぐ（SLA: 10秒以内）
private static final int API_TIMEOUT_SECONDS = 10;

SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
factory.setConnectTimeout(API_TIMEOUT_SECONDS * 1000);
factory.setReadTimeout(API_TIMEOUT_SECONDS * 1000);
RestTemplate restTemplate = new RestTemplate(factory);
```

---

## 🧪 Javadoc vs インラインコメント

### Javadocの役割

```java
/**
 * 顧客向けの割引額を計算
 * 
 * @param price 商品価格（税抜）
 * @param customer 顧客情報（会員ランク、購入履歴を含む）
 * @return 割引額（円）
 * @throws IllegalArgumentException 価格が0以下の場合
 */
public BigDecimal calculateDiscount(BigDecimal price, Customer customer) {
    if (price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("価格は正の値である必要があります");
    }
    
    // ここからインラインコメント開始
    // 会員ランクに応じた割引率
    // ビジネスルール: ゴールド15%, シルバー10%, ブロンズ5%
    BigDecimal discountRate = BigDecimal.ZERO;
    switch (customer.getRank()) {
        case GOLD:
            discountRate = new BigDecimal("0.15");
            break;
        case SILVER:
            discountRate = new BigDecimal("0.10");
            break;
        case BRONZE:
            discountRate = new BigDecimal("0.05");
            break;
    }
    
    // 累計購入額ボーナス（10万円以上でさらに5%）
    // 理由: 優良顧客へのロイヤルティプログラム
    if (customer.getTotalPurchases().compareTo(new BigDecimal("100000")) >= 0) {
        discountRate = discountRate.add(new BigDecimal("0.05"));
    }
    
    return price.multiply(discountRate);
}
```

---

## 🔍 デバッグ・開発用コメント

### 1. TODO/FIXME/HACK

```java
// TODO: [担当: 山田] [期限: 2025-12-01] Redis接続エラー時のフォールバック処理を実装
// 現在はRedis障害時にアプリケーション全体が停止する
// 対応方針: ローカルキャッシュへのフォールバック
public Optional<String> getCachedData(String key) {
    return redisTemplate.opsForValue().get(key);
}

// FIXME: [Issue #1234] ユニコードサロゲートペアで文字化けが発生
// 再現条件: 絵文字「👨‍👩‍👧‍👦」を含む名前で保存すると、取得時に破損
// 暫定対応: 絵文字を除去（根本対応は別途検討）
public String sanitizeName(String name) {
    return name.replaceAll("[^\\p{L}\\p{N}\\s]", "");
}

// HACK: 本来はJPAのCriteriaAPIを使うべきだが、パフォーマンス問題により生SQL使用
// 背景: JPAのN+1問題で応答時間が5秒→30秒に悪化
// リスク: SQLインジェクションの可能性（NamedParameterで対策済み）
// TODO: JPAのEntityGraphで最適化できるか検証（次スプリント）
@Query(value = "SELECT * FROM users WHERE department_id = :deptId", nativeQuery = true)
List<User> findByDepartmentIdNative(@Param("deptId") Long departmentId);
```

---

### 2. パフォーマンス最適化コメント

```java
// パフォーマンス最適化: リスト走査を避けるため、Mapで O(1)検索
// 変更前: O(n) - リストをループして検索
// 変更後: O(1) - Mapのキー検索
// 効果: 10,000件のデータで、100ms → 1ms に改善
Map<Long, User> userMap = users.stream()
    .collect(Collectors.toMap(User::getId, Function.identity()));
User targetUser = userMap.get(targetId);
```

---

## 🚫 避けるべきパターン

### 1. コードの繰り返し

```java
// ❌ 悪い例: コードを日本語に翻訳しただけ
// ユーザーのメールアドレスを取得
String email = user.getEmail();
// メールアドレスがnullでないかチェック
if (email != null) {
    // メールを送信
    sendEmail(email);
}

// ✅ 良い例: WHYを説明
// メールアドレスが未設定のユーザーには送信しない
// 理由: 無効なアドレスへの送信は、送信サービスの信頼度スコアを下げる
String email = user.getEmail();
if (email != null) {
    sendEmail(email);
}
```

---

### 2. 古いコメント

```java
// ❌ 悪い例: 過去の仕様が残っている
// パスワードは6文字以上である必要がある（古い要件）
private static final int MIN_PASSWORD_LENGTH = 8;  // 実際は8文字

// ✅ 良い例: 変更履歴を残す
// パスワードの最小文字数
// 変更履歴:
// - 2024-01-01: 6文字 → セキュリティ基準を満たさないため変更
// - 2025-01-01: 8文字 → 現在の基準（NIST推奨）
private static final int MIN_PASSWORD_LENGTH = 8;
```

---

## ✅ レビューチェックリスト

Javaコードレビュー時に確認:

- [ ] Stream APIに処理内容のコメントがある
- [ ] アノテーションの使用理由が明記されている
- [ ] 例外を無視する理由が説明されている
- [ ] 複雑度10以上のメソッドに詳細コメントがある
- [ ] マジックナンバーが定数化され、理由が記載されている
- [ ] TODO/FIXME/HACKに担当者・期限・理由がある
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-inline-comment-standards.md](../00-inline-comment-standards.md) - 共通原則
- [java/test-comment-examples.md](test-comment-examples.md) - Javaテストコメント例
- [java-coding-standards.md](java-coding-standards.md) - Java全体のコーディング規約
