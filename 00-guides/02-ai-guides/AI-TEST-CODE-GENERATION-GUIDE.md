---
title: "AI Test Code Generation Guide - AI向けテストコード生成ガイド"
version: "1.0.0"
created_date: "2025-11-20"
last_updated: "2025-11-20"
status: "Active"
audience: "AI Agents (Devin, Cursor, GitHub Copilot, etc.)"
category: "ai-guides"
related_documents:
  - "/03-development-process/testing-standards/"
  - "/04-quality-standards/"
  - "/01-coding-standards/{language}/06-testing.md"
---

# AI向けテストコード生成ガイド

> 自律型AIがテストコードを効率的かつ高品質に生成するための包括的ガイド

**対象読者**: 🤖 自律型AIエージェント（Devin, Cursor, GitHub Copilot等）  
**目的**: テストコード生成の品質向上、カバレッジ最大化、開発効率化  
**前提知識**: 基本的なテスト概念、AAA/Given-When-Thenパターン

---

## 📚 目次

1. [テストコード生成の基本原則](#1-テストコード生成の基本原則)
2. [言語別テストコード生成パターン](#2-言語別テストコード生成パターン)
3. [テストタイプ別生成ガイド](#3-テストタイプ別生成ガイド)
4. [プロンプトテンプレート集](#4-プロンプトテンプレート集)
5. [テストコード品質検証](#5-テストコード品質検証)
6. [ベストプラクティスとアンチパターン](#6-ベストプラクティスとアンチパターン)
7. [テストデータ生成戦略](#7-テストデータ生成戦略)
8. [カバレッジ最適化](#8-カバレッジ最適化)
9. [トラブルシューティング](#9-トラブルシューティング)
10. [実践例とケーススタディ](#10-実践例とケーススタディ)

---

## 1. テストコード生成の基本原則

### 1.1 テスト可能なコードの特徴

**AIがテストを生成しやすいコードの特性**:
```
✅ 単一責任の原則に従っている
✅ 依存関係が明確（DIパターン）
✅ 純粋関数が多い（副作用が少ない）
✅ 適切な粒度（クラス・関数サイズ）
✅ 明確なインターフェース
```

**テスト生成が困難なコード**:
```
❌ グローバル状態への依存
❌ ハードコードされた依存関係
❌ 複雑な条件分岐（ネスト深度>3）
❌ 巨大な関数（>50行）
❌ 副作用が多い
```

---

### 1.2 テストファーストアプローチ

**推奨ワークフロー**:
```
1. 要件理解 → 2. テスト設計 → 3. テスト生成 → 4. 実装 → 5. リファクタリング
```

**AIによるテスト先行開発**:
```markdown
# Step 1: 要件からテストケース抽出
要件: ユーザー登録機能
↓
テストケース:
- 正常系: 有効なデータでユーザー登録成功
- 異常系: 無効なメールアドレスでエラー
- 異常系: パスワードが短すぎてエラー
- 境界値: ユーザー名が最大長
- セキュリティ: SQLインジェクション対策

# Step 2: テストコード生成
各テストケースに対応するテストコードを生成

# Step 3: 実装
テストを通すための最小限の実装
```

---

### 1.3 カバレッジ目標の設定

**推奨カバレッジ目標**:

| コンポーネントタイプ | 行カバレッジ | ブランチカバレッジ | 優先度 |
|---------------------|-------------|------------------|--------|
| **ビジネスロジック** | 90-95% | 85-90% | 最高 |
| **APIエンドポイント** | 85-90% | 80-85% | 高 |
| **ユーティリティ** | 80-90% | 75-80% | 高 |
| **UIコンポーネント** | 70-80% | 65-75% | 中 |
| **設定ファイル** | 60-70% | 50-60% | 低 |

**カバレッジを上げすぎない領域**:
- 自動生成コード
- サードパーティライブラリのラッパー
- 単純なGetter/Setter
- 定数定義

---

## 2. 言語別テストコード生成パターン

### 2.1 Python (pytest)

#### 基本テンプレート

```python
"""
テストモジュール: {module_name}
対象: {target_module}
"""
import pytest
from unittest.mock import Mock, patch, MagicMock
from {module} import {class_or_function}


class Test{ClassName}:
    """
    {ClassName}のテストスイート
    """
    
    @pytest.fixture
    def setup_data(self):
        """テストデータのセットアップ"""
        return {
            "valid_input": {...},
            "invalid_input": {...},
        }
    
    @pytest.fixture
    def mock_dependency(self):
        """依存関係のモック"""
        return Mock(spec=DependencyClass)
    
    # 正常系テスト
    def test_{method_name}_success(self, setup_data, mock_dependency):
        """
        正常系: {method_name}が正しく動作する
        
        Given: 有効な入力データ
        When: {method_name}を呼び出す
        Then: 期待される結果を返す
        """
        # Arrange
        instance = {ClassName}(dependency=mock_dependency)
        input_data = setup_data["valid_input"]
        
        # Act
        result = instance.{method_name}(input_data)
        
        # Assert
        assert result == expected_result
        mock_dependency.method.assert_called_once_with(input_data)
    
    # 異常系テスト
    def test_{method_name}_invalid_input(self, setup_data):
        """
        異常系: 無効な入力でValueErrorを発生させる
        
        Given: 無効な入力データ
        When: {method_name}を呼び出す
        Then: ValueErrorが発生する
        """
        # Arrange
        instance = {ClassName}()
        invalid_data = setup_data["invalid_input"]
        
        # Act & Assert
        with pytest.raises(ValueError) as exc_info:
            instance.{method_name}(invalid_data)
        
        assert "expected error message" in str(exc_info.value)
    
    # 境界値テスト
    @pytest.mark.parametrize("input_value,expected", [
        (0, result_for_0),
        (1, result_for_1),
        (999, result_for_999),
        (1000, result_for_1000),
    ])
    def test_{method_name}_boundary_values(self, input_value, expected):
        """
        境界値テスト: 境界値で正しく動作する
        """
        # Arrange
        instance = {ClassName}()
        
        # Act
        result = instance.{method_name}(input_value)
        
        # Assert
        assert result == expected
```

#### プロンプトテンプレート（Python）

```
Generate comprehensive pytest unit tests for the following Python code:

[CODE]
{paste your code here}
[/CODE]

Requirements:
1. Use pytest framework with fixtures
2. Follow AAA (Arrange-Act-Assert) pattern
3. Include:
   - Happy path tests (正常系)
   - Error cases (異常系)
   - Boundary value tests (境界値)
   - Edge cases
4. Mock external dependencies using unittest.mock
5. Use parametrize for multiple test cases
6. Add clear docstrings (Given-When-Then format)
7. Aim for 90%+ code coverage
8. Test both return values and side effects

Additional context:
- Target coverage: 90%+
- Critical paths: [specify if any]
- Known edge cases: [specify if any]
```

---

### 2.2 TypeScript/JavaScript (Jest)

#### 基本テンプレート

```typescript
/**
 * テストスイート: {ComponentName}
 * 対象: {target_file}
 */
import { {ComponentName} } from './{file}';
import { mock, MockProxy } from 'jest-mock-extended';

describe('{ComponentName}', () => {
  let instance: {ComponentName};
  let mockDependency: MockProxy<DependencyType>;
  
  beforeEach(() => {
    // Arrange: テスト前のセットアップ
    mockDependency = mock<DependencyType>();
    instance = new {ComponentName}(mockDependency);
  });
  
  afterEach(() => {
    // クリーンアップ
    jest.clearAllMocks();
  });
  
  describe('{methodName}', () => {
    // 正常系テスト
    it('should {expected_behavior} when {condition}', async () => {
      // Arrange
      const input = { /* valid input */ };
      const expectedOutput = { /* expected result */ };
      mockDependency.method.mockResolvedValue(expectedOutput);
      
      // Act
      const result = await instance.{methodName}(input);
      
      // Assert
      expect(result).toEqual(expectedOutput);
      expect(mockDependency.method).toHaveBeenCalledWith(input);
      expect(mockDependency.method).toHaveBeenCalledTimes(1);
    });
    
    // 異常系テスト
    it('should throw error when {error_condition}', async () => {
      // Arrange
      const invalidInput = { /* invalid input */ };
      
      // Act & Assert
      await expect(
        instance.{methodName}(invalidInput)
      ).rejects.toThrow('Expected error message');
    });
    
    // 境界値テスト
    it.each([
      { input: 0, expected: result0 },
      { input: 1, expected: result1 },
      { input: 999, expected: result999 },
      { input: 1000, expected: result1000 },
    ])('should return $expected when input is $input', 
      async ({ input, expected }) => {
        // Act
        const result = await instance.{methodName}(input);
        
        // Assert
        expect(result).toBe(expected);
      }
    );
    
    // スナップショットテスト（UI コンポーネント）
    it('should match snapshot', () => {
      const component = render(<{ComponentName} {...props} />);
      expect(component).toMatchSnapshot();
    });
  });
});
```

#### プロンプトテンプレート（TypeScript/Jest）

```
Generate comprehensive Jest unit tests for the following TypeScript code:

[CODE]
{paste your TypeScript code here}
[/CODE]

Requirements:
1. Use Jest testing framework
2. Use jest-mock-extended for type-safe mocks
3. Follow AAA pattern
4. Include:
   - Happy path tests
   - Error handling tests
   - Boundary value tests
   - Async/Promise handling tests
5. Use describe/it structure
6. Use beforeEach/afterEach for setup/cleanup
7. Use it.each for parameterized tests
8. Add clear test descriptions
9. Aim for 90%+ coverage
10. Mock all external dependencies

Additional context:
- TypeScript version: 5.x
- Test environment: Node.js / jsdom
- Special requirements: [specify if any]
```

---

### 2.3 Java (JUnit 5)

#### 基本テンプレート

```java
/**
 * テストクラス: {ClassName}Test
 * 対象: {ClassName}
 */
package com.example.{package};

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("{ClassName}のテストスイート")
class {ClassName}Test {
    
    @Mock
    private DependencyType mockDependency;
    
    @InjectMocks
    private {ClassName} instance;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @AfterEach
    void tearDown() {
        // クリーンアップ
    }
    
    @Nested
    @DisplayName("{methodName}のテスト")
    class {MethodName}Test {
        
        @Test
        @DisplayName("正常系: 有効な入力で期待される結果を返す")
        void shouldReturnExpectedResult_WhenValidInput() {
            // Arrange
            InputType input = new InputType(/* valid data */);
            OutputType expected = new OutputType(/* expected data */);
            when(mockDependency.method(input)).thenReturn(expected);
            
            // Act
            OutputType actual = instance.{methodName}(input);
            
            // Assert
            assertEquals(expected, actual);
            verify(mockDependency, times(1)).method(input);
        }
        
        @Test
        @DisplayName("異常系: 無効な入力でIllegalArgumentExceptionを投げる")
        void shouldThrowException_WhenInvalidInput() {
            // Arrange
            InputType invalidInput = new InputType(/* invalid data */);
            
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                instance.{methodName}(invalidInput);
            });
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, expected0",
            "1, expected1",
            "999, expected999",
            "1000, expected1000"
        })
        @DisplayName("境界値テスト: 各境界値で正しく動作する")
        void shouldHandleBoundaryValues(int input, String expected) {
            // Act
            String actual = instance.{methodName}(input);
            
            // Assert
            assertEquals(expected, actual);
        }
    }
}
```

---

## 3. テストタイプ別生成ガイド

### 3.1 ユニットテスト生成

#### 原則
```
✅ テスト対象を完全に分離
✅ 依存関係はすべてモック
✅ 高速実行（<1秒/テスト）
✅ 決定論的（常に同じ結果）
✅ 独立実行可能
```

#### 生成プロンプト（ユニットテスト）

```
Generate unit tests for the {MethodName} method:

Target method:
{paste method code}

Test requirements:
1. Test type: Unit test
2. Isolation: Mock all dependencies
3. Coverage goal: 95%+
4. Test cases:
   a) Happy path with valid input
   b) Null/undefined/empty input
   c) Boundary values (min, max, zero, negative)
   d) Invalid input types
   e) Exception handling
5. Performance: Each test should complete in <100ms
6. Use {framework name} framework
7. Follow {language} best practices

Expected test structure:
- Setup fixtures
- Mock dependencies
- Test happy path
- Test error cases
- Test boundary values
- Verify mock interactions
```

---

### 3.2 統合テスト生成

#### 原則
```
✅ 複数コンポーネントの統合
✅ 実際の依存関係を使用（データベース、外部API等）
✅ トランザクション管理
✅ テストデータのセットアップとクリーンアップ
```

#### 生成プロンプト（統合テスト）

```
Generate integration tests for the {ComponentName} component:

Components under test:
{list components}

Integration points:
- Database: {database type}
- External APIs: {list APIs}
- Message queues: {list queues}

Test requirements:
1. Test type: Integration test
2. Use test database with transactions
3. Coverage: Major integration flows
4. Test cases:
   a) End-to-end happy path
   b) Error handling across components
   c) Data consistency
   d) Transaction rollback scenarios
5. Setup: Docker containers for dependencies
6. Cleanup: Rollback transactions after each test
7. Framework: {framework name}

Include:
- Database migration scripts
- Test data fixtures
- Container configuration
- Cleanup procedures
```

---

### 3.3 E2Eテスト生成（UI）

#### 原則
```
✅ ユーザーシナリオベース
✅ 実際のブラウザを使用
✅ ビジュアル回帰テスト
✅ アクセシビリティチェック
```

#### 生成プロンプト（E2E/Playwright）

```
Generate E2E tests using Playwright for the following user flow:

User Story:
{describe user story}

Test scenario:
1. {Step 1 description}
2. {Step 2 description}
3. {Step 3 description}
...

Test requirements:
1. Framework: Playwright
2. Browser: Chromium, Firefox, WebKit
3. Viewport: Desktop (1920x1080) and Mobile (375x667)
4. Test cases:
   a) Happy path through complete flow
   b) Form validation errors
   c) Authentication required scenarios
   d) Responsive design verification
5. Include:
   - Page Object Model pattern
   - Waiting strategies (explicit waits)
   - Screenshot on failure
   - Accessibility checks (axe-core)
   - Visual regression tests
6. Assertions:
   - URL verification
   - Element visibility
   - Text content
   - Form submission results

Code structure:
- Page objects for each page
- Test fixtures for common setup
- Helper functions for common actions
- Clear test descriptions
```

---

### 3.4 セキュリティテスト生成

#### 生成プロンプト（セキュリティテスト）

```
Generate security tests for the {ComponentName}:

Security concerns:
- OWASP Top 10 coverage
- Authentication/Authorization
- Input validation
- SQL Injection
- XSS attacks
- CSRF protection

Test requirements:
1. Test type: Security test
2. Framework: {testing framework} + security testing tools
3. Test cases:
   a) SQL Injection attempts
   b) XSS payload injection
   c) Authentication bypass attempts
   d) Authorization escalation tests
   e) CSRF token validation
   f) Input sanitization verification
   g) Password strength validation
   h) Session management tests

Include:
- Malicious input payloads
- Authentication bypass scenarios
- Authorization boundary tests
- OWASP ZAP integration (optional)
- Security assertion helpers

Expected outcome:
- All attacks should be blocked
- Proper error messages (no info leakage)
- Security headers verification
- Audit log verification
```

---

## 4. プロンプトテンプレート集

### 4.1 カバレッジ改善プロンプト

```
Analyze test coverage for {ModuleName} and generate additional tests:

Current coverage report:
{paste coverage report}

Instructions:
1. Identify uncovered lines and branches
2. Prioritize by criticality:
   - Critical: Business logic, security, data integrity
   - High: Error handling, edge cases
   - Medium: Utility functions
   - Low: Simple getters/setters
3. Generate tests to improve coverage to 90%+
4. Focus on:
   - Uncovered branches in conditionals
   - Exception paths
   - Boundary conditions
   - Edge cases
5. Provide coverage improvement summary

Output format:
- List of new test cases
- Expected coverage increase
- Test code for each case
- Updated coverage report estimate
```

---

### 4.2 テストリファクタリングプロンプト

```
Refactor the following test suite to improve maintainability:

Current test code:
{paste test code}

Refactoring goals:
1. Remove duplication
2. Extract common setup to fixtures/beforeEach
3. Improve test names (be more descriptive)
4. Extract helper functions
5. Organize tests with describe/context blocks
6. Add missing assertions
7. Improve readability
8. Follow {language} testing best practices

Maintain:
- All existing test coverage
- All test assertions
- Test isolation

Output:
- Refactored test code
- List of improvements made
- Explanation of changes
```

---

### 4.3 Flaky Test修正プロンプト

```
Fix flaky test that fails intermittently:

Failing test:
{paste test code}

Failure symptoms:
- Fails {X}% of the time
- Error message: {paste error}
- Failure pattern: {describe pattern}

Common causes to check:
1. Race conditions (async/await issues)
2. Timing dependencies
3. External service dependencies
4. Shared state between tests
5. Non-deterministic data
6. Environment-specific issues

Instructions:
1. Identify root cause of flakiness
2. Implement fix using:
   - Proper async/await handling
   - Explicit waits instead of sleeps
   - Mock external dependencies
   - Isolate test state
   - Use deterministic data
3. Add retry logic if necessary (last resort)
4. Explain the fix

Output:
- Root cause analysis
- Fixed test code
- Explanation of changes
- How to verify stability
```

---

### 4.4 パフォーマンステスト生成プロンプト

```
Generate performance tests for {ComponentName}:

Performance requirements:
- Response time: < {X} ms (p95)
- Throughput: > {Y} requests/sec
- Memory usage: < {Z} MB
- CPU usage: < {W}%

Test scenarios:
1. Baseline performance test
2. Load test (sustained load)
3. Stress test (peak load)
4. Spike test (sudden traffic increase)
5. Endurance test (long duration)

Test requirements:
1. Framework: {performance testing framework}
2. Duration: {duration}
3. Virtual users: {number}
4. Ramp-up period: {period}
5. Metrics to collect:
   - Response time (min, max, avg, p95, p99)
   - Throughput (req/sec)
   - Error rate
   - Resource utilization

Include:
- Test setup code
- Load generation script
- Metric collection
- Performance assertions
- Results visualization
```

---

### 4.5 モック生成プロンプト

```
Generate mocks for the following dependencies:

Dependencies to mock:
{list dependencies with interfaces}

Mocking requirements:
1. Framework: {mocking framework}
2. Mock behavior:
   - Return predefined values for happy path
   - Throw exceptions for error cases
   - Track method calls
   - Verify call arguments
3. Mock types:
   - Interface mocks
   - Class mocks
   - Function mocks
   - HTTP request mocks
4. Mock data:
   - Realistic test data
   - Edge case data
   - Error scenarios

Output:
- Mock setup code
- Mock factory functions
- Test data fixtures
- Mock verification helpers
- Usage examples
```

---

## 5. テストコード品質検証

### 5.1 自己レビューチェックリスト

**AIが生成したテストを自己レビューする基準**:

#### 基本構造
```
✅ テストは独立して実行できるか？
✅ テスト名は何をテストしているか明確か？
✅ Given-When-Then構造に従っているか？
✅ 1テストで1つの概念のみをテストしているか？
✅ Arrange-Act-Assert順序が明確か？
```

#### モックとスタブ
```
✅ 外部依存はすべてモックされているか？
✅ モックの設定は適切か？
✅ モックの呼び出し検証があるか？
✅ 過度なモック（over-mocking）を避けているか？
✅ テストダブルの種類は適切か（Mock vs Stub vs Fake）？
```

#### テストカバレッジ
```
✅ 正常系がテストされているか？
✅ 異常系がテストされているか？
✅ 境界値がテストされているか？
✅ エッジケースがテストされているか？
✅ エラーハンドリングがテストされているか？
```

#### 保守性
```
✅ テストコードは読みやすいか？
✅ 重複コードは最小限か？
✅ マジックナンバーを避けているか？
✅ テストデータは意味のある名前か？
✅ ヘルパー関数は適切に使用されているか？
```

#### パフォーマンス
```
✅ テストは高速に実行されるか（<1秒/ユニットテスト）？
✅ 不要なセットアップは避けているか？
✅ データベースアクセスは最小限か？
✅ ネットワークアクセスはモックされているか？
```

---

### 5.2 品質スコアリング

**テストコード品質スコア（100点満点）**:

| カテゴリ | 配点 | チェック項目 |
|---------|------|------------|
| **構造** | 20点 | AAA構造、独立性、明確性 |
| **カバレッジ** | 30点 | 正常系、異常系、境界値、エッジケース |
| **モック** | 15点 | 適切なモック、検証 |
| **保守性** | 20点 | 可読性、重複排除、命名 |
| **パフォーマンス** | 10点 | 実行速度、リソース使用 |
| **ドキュメント** | 5点 | コメント、テスト説明 |

**品質判定**:
- 90-100点: 優秀（Excellent）
- 80-89点: 良好（Good）
- 70-79点: 合格（Acceptable）
- 70点未満: 改善必要（Needs Improvement）

---

## 6. ベストプラクティスとアンチパターン

### 6.1 ベストプラクティス

#### 1. 明確なテスト名
```python
# ✅ 良い例: 何をテストしているか明確
def test_create_user_with_valid_email_returns_user_object():
    pass

def test_create_user_with_duplicate_email_raises_value_error():
    pass

# ❌ 悪い例: 曖昧
def test_create_user():
    pass

def test_error():
    pass
```

#### 2. Given-When-Then構造
```typescript
// ✅ 良い例
it('should return 404 when user not found', async () => {
  // Given: ユーザーが存在しない状態
  const userId = 'non-existent-id';
  mockUserRepository.findById.mockResolvedValue(null);
  
  // When: ユーザー取得を試みる
  const result = await userService.getUser(userId);
  
  // Then: 404エラーが返される
  expect(result.status).toBe(404);
  expect(result.error).toBe('User not found');
});
```

#### 3. テストデータビルダーパターン
```python
# ✅ 良い例: テストデータビルダー
class UserBuilder:
    def __init__(self):
        self.username = "testuser"
        self.email = "test@example.com"
        self.password = "password123"
    
    def with_username(self, username):
        self.username = username
        return self
    
    def with_invalid_email(self):
        self.email = "invalid-email"
        return self
    
    def build(self):
        return User(
            username=self.username,
            email=self.email,
            password=self.password
        )

# 使用例
def test_user_creation():
    user = UserBuilder().with_username("john").build()
    # テスト処理
```

#### 4. パラメータ化テスト
```python
# ✅ 良い例: 複数ケースを簡潔に
@pytest.mark.parametrize("input,expected", [
    ("", False),           # 空文字
    ("a", False),          # 短すぎる
    ("password", True),    # 有効
    ("P@ssw0rd!", True),   # 複雑なパスワード
    ("12345678", True),    # 数字のみ
])
def test_password_validation(input, expected):
    assert validate_password(input) == expected
```

---

### 6.2 アンチパターン

#### 1. テストの相互依存
```python
# ❌ 悪い例: テストが順序に依存
class TestUserService:
    def test_1_create_user(self):
        self.user = create_user("test")  # グローバル状態を変更
    
    def test_2_update_user(self):
        update_user(self.user)  # test_1に依存

# ✅ 良い例: 各テストが独立
class TestUserService:
    @pytest.fixture
    def user(self):
        return create_user("test")
    
    def test_create_user(self):
        user = create_user("test")
        assert user is not None
    
    def test_update_user(self, user):
        updated = update_user(user)
        assert updated.version > user.version
```

#### 2. 過度なモック
```typescript
// ❌ 悪い例: すべてをモック（テストの価値が低い）
it('should process order', () => {
  const mockOrder = mock<Order>();
  const mockPayment = mock<Payment>();
  const mockInventory = mock<Inventory>();
  const mockShipping = mock<Shipping>();
  const mockNotification = mock<Notification>();
  
  mockOrder.getTotal.mockReturnValue(100);
  // 実際のロジックをテストしていない
});

// ✅ 良い例: 外部依存のみモック
it('should process order', () => {
  const order = new Order([item1, item2]);
  const mockPaymentGateway = mock<PaymentGateway>();
  
  orderService.process(order, mockPaymentGateway);
  
  expect(order.status).toBe('PROCESSED');
});
```

#### 3. 脆弱なテスト（Fragile Test）
```java
// ❌ 悪い例: 実装詳細に依存
@Test
void testUserRegistration() {
    // 内部実装に依存したテスト
    verify(emailService).sendEmail(any());
    verify(database).insert(any());
    verify(logger).log(contains("User registered"));
    // 実装変更で簡単に壊れる
}

// ✅ 良い例: 振る舞いをテスト
@Test
void testUserRegistration() {
    User user = userService.register("test@example.com");
    
    assertNotNull(user.getId());
    assertTrue(user.isActive());
    // 実装詳細ではなく結果をテスト
}
```

#### 4. Sleepの使用
```python
# ❌ 悪い例: sleepでタイミング制御
def test_async_operation():
    start_async_task()
    time.sleep(5)  # 不確実、遅い
    assert task_is_complete()

# ✅ 良い例: 明示的な待機
def test_async_operation():
    task = start_async_task()
    result = wait_for_completion(task, timeout=10)
    assert result.is_complete()
```

---

## 7. テストデータ生成戦略

### 7.1 Fixtureパターン

```python
# pytest fixtures
@pytest.fixture
def sample_user():
    """標準的なテストユーザー"""
    return User(
        username="testuser",
        email="test@example.com",
        role="user"
    )

@pytest.fixture
def admin_user():
    """管理者ユーザー"""
    return User(
        username="admin",
        email="admin@example.com",
        role="admin"
    )

@pytest.fixture
def users_list(sample_user, admin_user):
    """複数ユーザーのリスト"""
    return [sample_user, admin_user]

# 使用例
def test_user_permissions(sample_user, admin_user):
    assert sample_user.can_view()
    assert not sample_user.can_delete()
    assert admin_user.can_delete()
```

---

### 7.2 ファクトリーパターン

```typescript
// Test factory
class UserFactory {
  static create(overrides: Partial<User> = {}): User {
    return {
      id: faker.string.uuid(),
      username: faker.internet.userName(),
      email: faker.internet.email(),
      createdAt: new Date(),
      ...overrides
    };
  }
  
  static createMany(count: number): User[] {
    return Array.from({ length: count }, () => this.create());
  }
  
  static createAdmin(): User {
    return this.create({ role: 'admin' });
  }
}

// 使用例
describe('UserService', () => {
  it('should handle multiple users', () => {
    const users = UserFactory.createMany(10);
    const result = userService.processBatch(users);
    expect(result).toHaveLength(10);
  });
  
  it('should grant admin privileges', () => {
    const admin = UserFactory.createAdmin();
    expect(admin.role).toBe('admin');
  });
});
```

---

## 8. カバレッジ最適化

### 8.1 効率的なカバレッジ向上戦略

**ステップ1: カバレッジレポート分析**
```bash
# Python
pytest --cov=src --cov-report=html --cov-report=term-missing

# TypeScript/Jest
jest --coverage --coverageReporters=html --coverageReporters=text

# Java
mvn test jacoco:report
```

**ステップ2: 優先順位付け**
```
1. クリティカルパス（ビジネスロジック、セキュリティ）
2. 複雑な条件分岐（サイクロマティック複雑度が高い）
3. エラーハンドリング
4. 境界値条件
5. エッジケース
```

**ステップ3: ギャップ分析プロンプト**
```
Analyze the following coverage report and generate tests for uncovered code:

Coverage report:
{paste coverage report}

Current coverage: {X}%
Target coverage: 90%

Instructions:
1. Identify uncovered lines by priority:
   - Red: Critical business logic
   - Orange: Error handling
   - Yellow: Edge cases
2. For each uncovered section, explain:
   - Why it's currently uncovered
   - What test case would cover it
   - Risk if left untested
3. Generate test code for top 5 priority items
4. Estimate coverage improvement

Output format:
- Priority list with rationale
- Test code for each item
- Expected coverage after adding tests
```

---

## 9. トラブルシューティング

### 9.1 Flaky Test（不安定なテスト）

**症状**: テストが時々失敗する

**原因と対策**:

```python
# 原因1: 非同期処理の不適切な待機
# ❌ 悪い例
def test_async_task():
    task.start()
    time.sleep(1)  # 不確実
    assert task.is_done()

# ✅ 良い例
def test_async_task():
    task.start()
    result = wait_until(lambda: task.is_done(), timeout=5)
    assert result is True

# 原因2: 共有状態
# ❌ 悪い例
shared_cache = {}

def test_cache_1():
    shared_cache['key'] = 'value1'
    assert shared_cache['key'] == 'value1'

def test_cache_2():
    assert 'key' not in shared_cache  # test_cache_1の影響を受ける

# ✅ 良い例
@pytest.fixture
def cache():
    return {}

def test_cache_1(cache):
    cache['key'] = 'value1'
    assert cache['key'] == 'value1'

def test_cache_2(cache):
    assert 'key' not in cache  # 独立
```

---

### 9.2 遅いテスト

**症状**: テスト実行に時間がかかる

**対策**:

```typescript
// 原因1: データベースアクセス
// ❌ 悪い例: 毎回DBにアクセス
beforeEach(async () => {
  await database.clear();
  await database.seed();  // 遅い
});

// ✅ 良い例: トランザクションを使用
beforeEach(async () => {
  await database.beginTransaction();
});

afterEach(async () => {
  await database.rollback();  // 高速
});

// 原因2: 外部API呼び出し
// ❌ 悪い例
it('should fetch user data', async () => {
  const data = await externalAPI.getUser();  // 実際のAPI呼び出し
  expect(data).toBeDefined();
});

// ✅ 良い例
it('should fetch user data', async () => {
  mockAPI.getUser.mockResolvedValue({ id: 1, name: 'Test' });
  const data = await service.getUser();
  expect(data).toBeDefined();
});
```

---

## 10. 実践例とケーススタディ

### 10.1 ケーススタディ1: 認証APIのテスト

**要件**: JWT認証APIのテストコード生成

**使用プロンプト**:
```
Generate comprehensive tests for JWT authentication API:

API endpoints:
1. POST /auth/login - User login
2. POST /auth/logout - User logout
3. POST /auth/refresh - Token refresh

Requirements:
- Framework: pytest (Python) / Jest (TypeScript)
- Coverage target: 95%+
- Security focus: OWASP Top 10

Test cases needed:
1. Happy path: Valid credentials
2. Invalid credentials
3. Missing credentials
4. Token expiration handling
5. Token refresh
6. SQL injection attempts
7. XSS attempts
8. Rate limiting
9. Concurrent requests
10. Session management
```

**生成されたテスト例**:
```python
class TestAuthAPI:
    """認証APIテストスイート"""
    
    def test_login_success_returns_valid_token(
        self, client, test_user, mock_token_service
    ):
        """正常系: 有効な認証情報でトークンを取得"""
        # Arrange
        credentials = {
            "username": test_user.username,
            "password": "correct_password"
        }
        expected_token = "valid.jwt.token"
        mock_token_service.generate.return_value = expected_token
        
        # Act
        response = client.post('/auth/login', json=credentials)
        
        # Assert
        assert response.status_code == 200
        assert 'access_token' in response.json()
        assert response.json()['access_token'] == expected_token
        assert 'expires_in' in response.json()
    
    def test_login_invalid_credentials_returns_401(self, client):
        """異常系: 無効な認証情報で401エラー"""
        # Arrange
        credentials = {
            "username": "user",
            "password": "wrong_password"
        }
        
        # Act
        response = client.post('/auth/login', json=credentials)
        
        # Assert
        assert response.status_code == 401
        assert 'error' in response.json()
        assert response.json()['error'] == 'Invalid credentials'
    
    def test_login_sql_injection_attempt_blocked(self, client):
        """セキュリティ: SQLインジェクション試行をブロック"""
        # Arrange
        malicious_input = {
            "username": "admin' OR '1'='1",
            "password": "password"
        }
        
        # Act
        response = client.post('/auth/login', json=malicious_input)
        
        # Assert
        assert response.status_code == 401
        # SQLインジェクションは成功してはいけない
    
    @pytest.mark.parametrize("rate_limit_count", [6, 10, 20])
    def test_login_rate_limiting(self, client, rate_limit_count):
        """セキュリティ: レート制限が機能する"""
        # Arrange
        credentials = {"username": "user", "password": "password"}
        
        # Act: 制限を超えるリクエスト
        responses = [
            client.post('/auth/login', json=credentials)
            for _ in range(rate_limit_count)
        ]
        
        # Assert: 最後のリクエストは429
        assert responses[-1].status_code == 429
```

---

### 10.2 ケーススタディ2: CRUD操作のテスト

**要件**: ユーザーCRUD操作の包括的テスト

**生成されたテスト例**:
```typescript
describe('UserCRUD', () => {
  let userService: UserService;
  let mockRepository: MockProxy<UserRepository>;
  
  beforeEach(() => {
    mockRepository = mock<UserRepository>();
    userService = new UserService(mockRepository);
  });
  
  describe('Create', () => {
    it('should create user with valid data', async () => {
      // Arrange
      const userData = UserFactory.create();
      mockRepository.save.mockResolvedValue(userData);
      
      // Act
      const result = await userService.createUser(userData);
      
      // Assert
      expect(result).toEqual(userData);
      expect(mockRepository.save).toHaveBeenCalledWith(userData);
    });
    
    it('should reject duplicate email', async () => {
      // Arrange
      const userData = UserFactory.create();
      mockRepository.findByEmail.mockResolvedValue(userData);
      
      // Act & Assert
      await expect(
        userService.createUser(userData)
      ).rejects.toThrow('Email already exists');
    });
  });
  
  describe('Read', () => {
    it('should return user by id', async () => {
      // Arrange
      const user = UserFactory.create();
      mockRepository.findById.mockResolvedValue(user);
      
      // Act
      const result = await userService.getUser(user.id);
      
      // Assert
      expect(result).toEqual(user);
    });
    
    it('should return null for non-existent user', async () => {
      // Arrange
      mockRepository.findById.mockResolvedValue(null);
      
      // Act
      const result = await userService.getUser('non-existent');
      
      // Assert
      expect(result).toBeNull();
    });
  });
  
  describe('Update', () => {
    it('should update user data', async () => {
      // Arrange
      const existingUser = UserFactory.create();
      const updates = { username: 'new-username' };
      const updatedUser = { ...existingUser, ...updates };
      
      mockRepository.findById.mockResolvedValue(existingUser);
      mockRepository.save.mockResolvedValue(updatedUser);
      
      // Act
      const result = await userService.updateUser(
        existingUser.id,
        updates
      );
      
      // Assert
      expect(result.username).toBe('new-username');
    });
  });
  
  describe('Delete', () => {
    it('should delete user', async () => {
      // Arrange
      const user = UserFactory.create();
      mockRepository.findById.mockResolvedValue(user);
      mockRepository.delete.mockResolvedValue(true);
      
      // Act
      const result = await userService.deleteUser(user.id);
      
      // Assert
      expect(result).toBe(true);
      expect(mockRepository.delete).toHaveBeenCalledWith(user.id);
    });
  });
});
```

---

## 📋 まとめ

このガイドは、AI開発者がテストコードを効率的かつ高品質に生成するための包括的なリファレンスです。

### 重要ポイント

1. **テスト可能なコード設計**: 依存関係の注入、単一責任の原則
2. **適切なカバレッジ**: 90%+を目指すが、100%は不要
3. **品質重視**: カバレッジより品質が重要
4. **保守性**: 読みやすく、変更に強いテスト
5. **高速実行**: ユニットテスト<1秒、統合テスト<10秒

### 次のステップ

1. このガイドを参照しながらテスト生成
2. 生成したテストを自己レビュー（セクション5のチェックリスト）
3. カバレッジレポートで不足箇所を特定
4. 継続的に改善

### 関連ドキュメント

- `/03-development-process/testing-standards/` - テスト標準全般
- `/04-quality-standards/` - 品質基準
- `/01-coding-standards/{language}/06-testing.md` - 言語別テスト標準

---

**最終更新日**: 2025-11-20  
**バージョン**: 1.0.0  
**メンテナンス**: 四半期ごとに更新
