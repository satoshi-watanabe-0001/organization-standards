# TypeScript テストコメント実装例

## 📖 概要

本ドキュメントは、TypeScript（Jest/Vitest）におけるテストコメントの具体例を提供します。

**共通原則**: [00-test-comment-standards.md](../00-test-comment-standards.md)を必ず参照してください。

---

## 🧪 Jestスタイル

### 1. 基本的なテストケース

```typescript
describe('UserService', () => {
  test('有効なデータでのユーザー作成', async () => {
    /**
     * 【テスト対象】: 有効なデータでのユーザー作成
     * 
     * 【テストケース】:
     * 必須項目（メール、パスワード、名前）をすべて指定し、
     * 正常にユーザーが作成されることを検証する。
     * 
     * 【期待結果】:
     * - ユーザーオブジェクトが返される
     * - データベースにレコードが保存される
     * - パスワードがハッシュ化される
     * - IDと作成日時が自動設定される
     * 
     * 【ビジネス要件】:
     * [REQ-USER-001] 有効なデータでユーザー登録ができること
     */
    
    // === Given: テストデータの準備 ===
    // 有効なユーザーデータ（すべての必須項目を含む）
    const userData = {
      email: 'test@example.com',
      password: 'SecurePass123!',
      name: 'テストユーザー',
    };
    
    // === When: ユーザー作成を実行 ===
    const user = await userService.createUser(userData);
    
    // === Then: 結果を検証 ===
    // ユーザーオブジェクトが正常に生成されていることを確認
    expect(user).toBeDefined();
    expect(user.email).toBe('test@example.com');
    expect(user.name).toBe('テストユーザー');
    
    // パスワードがハッシュ化されていることを確認
    // セキュリティ要件: 平文パスワードは保存禁止
    expect(user.password).not.toBe('SecurePass123!');
    expect(user.password).toMatch(/^\$2[aby]\$/);  // bcryptハッシュの識別子
    
    // 自動設定項目の確認
    expect(user.id).toBeDefined();  // IDが発行されている
    expect(user.createdAt).toBeInstanceOf(Date);  // 作成日時が記録されている
    
    // データベース永続化の確認
    // データベースから取得して、正しく保存されているか検証
    const savedUser = await userRepository.findById(user.id);
    expect(savedUser?.email).toBe('test@example.com');
  });
});
```

---

### 2. test.eachを使ったテーブル駆動テスト

```typescript
describe('PasswordValidator', () => {
  test.each([
    // 【正常系】
    ['ValidPass123!', true, '英数字+記号を含む8文字（最小要件）'],
    ['SecurePassword2025!', true, '長いパスワード（推奨）'],
    ['パスワード123!', true, '日本語を含むパスワード（許可）'],
    
    // 【異常系: 長さ不足】
    ['Pass12!', false, '7文字（8文字未満）'],
    ['Abc1!', false, '5文字（短すぎる）'],
    
    // 【異常系: 複雑度不足】
    ['password123!', false, '大文字なし'],
    ['PASSWORD123!', false, '小文字なし'],
    ['PasswordABC!', false, '数字なし'],
    ['Password123', false, '記号なし'],
    
    // 【境界値】
    ['Pass123!', true, '最小要件をちょうど満たす（8文字、英大小数記号）'],
    ['a'.repeat(128) + 'A1!', true, '最大長（128文字）'],
    ['a'.repeat(129) + 'A1!', false, '最大長超過（129文字）'],
  ])('パスワードバリデーション: %s', (password, expectedValid, reason) => {
    /**
     * 【テスト対象】: パスワードバリデーション
     * 
     * 【テストケース】:
     * 様々なパターンのパスワードが、セキュリティポリシーに従って
     * 正しく検証されることを確認する。
     * 
     * 【期待結果】:
     * - 正常系: 英数字+記号を含む8文字以上のパスワードが有効
     * - 異常系: 要件を満たさないパスワードが無効
     * - 境界値: 最小/最大長が正しく判定される
     * 
     * 【ビジネス要件】:
     * [REQ-SEC-002] パスワードは8文字以上、英大文字・小文字・数字・記号を含むこと
     */
    
    // パスワードバリデーションを実行
    const isValid = validatePassword(password);
    
    // 期待結果と一致することを確認
    // テスト失敗時にreasonが表示され、どのケースで失敗したか分かる
    expect(isValid).toBe(expectedValid); // reason は表示名に含まれる
  });
});
```

---

### 3. モックを使ったテスト

```typescript
describe('UserService - メール送信', () => {
  test('ユーザー登録時のウェルカムメール送信', async () => {
    /**
     * 【テスト対象】: ユーザー登録時のウェルカムメール送信
     * 
     * 【テストケース】:
     * 新規ユーザーが登録した際、ウェルカムメールが送信されることを検証。
     * 実際のメール送信サービスは呼び出さず、モックで代替する。
     * 
     * 【期待結果】:
     * - ユーザー登録が成功する
     * - sendEmailメソッドが1回呼ばれる
     * - 送信先がユーザーのメールアドレスである
     * - 件名が「登録ありがとうございます」である
     * 
     * 【ビジネス要件】:
     * [REQ-USER-002] 登録完了後、ウェルカムメールを送信すること
     */
    
    // === Given: メール送信サービスをモック ===
    // モック設定の理由:
    // 1. 実際のメール送信は時間がかかる（テストが遅くなる）
    // 2. 外部サービスの状態に依存しない（テストの安定性向上）
    // 3. 実際にメールが送信されない（テスト用アドレスの汚染を防ぐ）
    const mockSendEmail = jest.spyOn(emailService, 'sendEmail').mockResolvedValue(undefined);
    
    // === When: ユーザー登録を実行 ===
    const userData = {
      email: 'newuser@example.com',
      password: 'ValidPass123!',
      name: '新規ユーザー',
    };
    
    const user = await userService.createUser(userData);
    
    // === Then: メール送信が呼ばれたことを検証 ===
    // sendEmailが1回だけ呼ばれたことを確認
    expect(mockSendEmail).toHaveBeenCalledTimes(1);
    
    // 呼び出し時の引数を確認
    expect(mockSendEmail).toHaveBeenCalledWith(
      'newuser@example.com',      // 送信先
      '登録ありがとうございます',   // 件名
      expect.stringContaining('新規ユーザー')  // 本文にユーザー名が含まれる
    );
    
    // モックを復元
    mockSendEmail.mockRestore();
  });
});
```

---

### 4. 例外テスト

```typescript
describe('UserService - エラーハンドリング', () => {
  test('メールアドレス重複時のエラー', async () => {
    /**
     * 【テスト対象】: メールアドレス重複時のエラー
     * 
     * 【テストケース】:
     * 既存ユーザーと同じメールアドレスでユーザー作成を試みた場合、
     * DuplicateEmailError例外が発生することを検証。
     * 
     * 【期待結果】:
     * - DuplicateEmailError例外がthrowされる
     * - エラーメッセージが「このメールアドレスは既に使用されています」
     * - データベースにユーザーが追加されない（1件のまま）
     * 
     * 【ビジネス要件】:
     * [REQ-USER-003] メールアドレスは一意であること
     */
    
    // === Given: 既存ユーザーを作成 ===
    // メールアドレス: existing@example.com のユーザーを作成
    await userService.createUser({
      email: 'existing@example.com',
      password: 'Pass123!',
      name: '既存ユーザー',
    });
    
    // === When & Then: 同じメールアドレスで作成し、例外を期待 ===
    // rejects.toThrowで非同期例外をキャッチ
    await expect(
      userService.createUser({
        email: 'existing@example.com',  // 既存と同じメールアドレス
        password: 'NewPass456!',
        name: '新規ユーザー',
      })
    ).rejects.toThrow(DuplicateEmailError);
    
    // エラーメッセージが適切であることも確認
    await expect(
      userService.createUser({
        email: 'existing@example.com',
        password: 'NewPass456!',
        name: '新規ユーザー',
      })
    ).rejects.toThrow('このメールアドレスは既に使用されています');
    
    // データベースにユーザーが追加されていないことを確認
    // つまり、重複登録が実際に防止された
    const userCount = await userRepository.count();
    expect(userCount).toBe(1);  // 既存ユーザー1件のみ
  });
});
```

---

## 🔐 セキュリティテスト

### 1. SQLインジェクション対策

```typescript
describe('UserRepository - セキュリティ', () => {
  test('ユーザー検索におけるSQLインジェクション対策', async () => {
    /**
     * 【テスト対象】: ユーザー検索におけるSQLインジェクション対策
     * 
     * 【テストケース】:
     * SQLインジェクション攻撃を試み、適切にエスケープされることを検証。
     * 攻撃者が悪意あるSQL文を検索クエリに含めても、
     * データベースに影響がないことを確認。
     * 
     * 【期待結果】:
     * - SQLインジェクションが成功しない
     * - データベースが破壊されない
     * - 安全に検索結果が返される
     * 
     * 【ビジネス要件】:
     * [REQ-SEC-010] すべての入力値はサニタイズし、SQLインジェクションを防止すること
     */
    
    // === Given: テスト用ユーザーを作成 ===
    await userRepository.create({
      email: 'user1@example.com',
      password: 'Pass1!',
      name: 'ユーザー1',
    });
    
    await userRepository.create({
      email: 'user2@example.com',
      password: 'Pass2!',
      name: 'ユーザー2',
    });
    
    const initialCount = await userRepository.count();
    
    // === When: SQLインジェクション攻撃を試行 ===
    // 攻撃シミュレーション: "'; DROP TABLE users; --" という文字列を検索
    // 不適切な実装では、以下のSQL文が実行されてしまう:
    //   SELECT * FROM users WHERE name LIKE '%'; DROP TABLE users; --%';
    // この場合、usersテーブルが削除される
    const maliciousQuery = "'; DROP TABLE users; --";
    
    // 検索を実行
    // 正しい実装では、プレースホルダーを使用し、文字列としてエスケープされる
    const results = await userRepository.searchByName(maliciousQuery);
    
    // === Then: セキュリティ検証 ===
    // 1. 検索結果が安全に返される（エラーにならない）
    expect(results).toBeDefined();
    expect(Array.isArray(results)).toBe(true);
    
    // 2. 悪意あるクエリに一致するユーザーはいない（空の結果）
    expect(results).toHaveLength(0);
    
    // 3. データベースが破壊されていないことを確認
    // usersテーブルが削除されていない
    expect(await userRepository.count()).toBe(initialCount);
    
    // 4. 正常な検索が可能であることを確認
    // つまり、テーブル構造が保持されている
    const normalResults = await userRepository.searchByName('ユーザー1');
    expect(normalResults).toHaveLength(1);
    expect(normalResults[0].name).toBe('ユーザー1');
  });
});
```

---

## 📊 パフォーマンステスト

### 1. レスポンスタイム測定

```typescript
describe('UserService - パフォーマンス', () => {
  test('大量ユーザー作成のパフォーマンス', async () => {
    /**
     * 【テスト対象】: 大量ユーザー作成のパフォーマンス
     * 
     * 【テストケース】:
     * 1,000件のユーザーを一括作成し、処理時間が5秒以内であることを検証。
     * バルクインサートを使用することで、1件ずつ作成するより高速であることを確認。
     * 
     * 【期待結果】:
     * - 1,000件のユーザーが作成される
     * - 処理時間が5秒以内
     * - メモリ使用量が適切（メモリリークなし）
     * 
     * 【ビジネス要件】:
     * [REQ-PERF-002] 1,000件のユーザー一括作成は5秒以内に完了すること
     */
    
    // === Given: 1,000件のユーザーデータを生成 ===
    const userDataList = Array.from({ length: 1000 }, (_, i) => ({
      email: `user${i}@example.com`,
      password: 'Pass123!',
      name: `ユーザー${i}`,
    }));
    
    // === When: バルクインサートを実行 ===
    // 処理時間を計測
    const startTime = Date.now();
    
    // バルクインサートの利点:
    // - 1件ずつINSERTするより100倍高速
    // - データベース接続の回数が減る
    // - トランザクションオーバーヘッドが減る
    await userService.bulkCreateUsers(userDataList);
    
    const elapsedTime = Date.now() - startTime;
    
    // === Then: パフォーマンス検証 ===
    // 処理時間が要件（5秒 = 5000ms）を満たすことを確認
    expect(elapsedTime).toBeLessThan(5000);
    
    // すべてのユーザーが作成されたことを確認
    expect(await userRepository.count()).toBe(1000);
    
    // パフォーマンスメトリクスをログに記録
    // CI/CDで経時変化をモニタリングし、パフォーマンス劣化を検出
    console.log(`Performance Test - bulk_user_creation: 1000 records in ${elapsedTime}ms`);
  }, 10000);  // タイムアウトを10秒に設定
});
```

---

## ✅ レビューチェックリスト

TypeScriptテストコードレビュー時に確認:

- [ ] すべてのテスト関数にコメントがある
- [ ] 4要素（対象・ケース・期待結果・要件）が記載されている
- [ ] Given-When-Thenセクションにコメントがある
- [ ] モックを使う理由が明記されている
- [ ] test.eachの各ケースに説明がある
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-test-comment-standards.md](../00-test-comment-standards.md) - 共通テストコメント原則
- [typescript/inline-comment-examples.md](inline-comment-examples.md) - TypeScriptインラインコメント例
- [typescript-testing-guide.md](typescript-testing-guide.md) - TypeScriptテスト全体のガイド
