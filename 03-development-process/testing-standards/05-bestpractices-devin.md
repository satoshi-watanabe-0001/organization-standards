# テスト標準

## Devin AIガイドライン

### テスト開発へのDevin AIの使用

Devin AIは、次の方法でテスト開発を支援できます:

1. **テストケースの生成**
   - 既存のコードに基づいてテストケースを生成
   - エッジケースとコーナーケースを特定
   - 包括的なテストスイートを作成

2. **テストカバレッジの改善**
   - カバレッジギャップを分析
   - 欠落しているテストケースを提案
   - カバレッジメトリクスを改善

3. **テストリファクタリング**
   - テストの重複を特定
   - テストの保守性を改善
   - ベストプラクティスに従うようにテストを更新

4. **テストの自動修正**
   - 失敗したテストを分析
   - 問題の根本原因を特定
   - 修正を提案して実装

### Devinプロンプトの例

#### テスト生成

```
"Generate comprehensive unit tests for the UserService class. Include tests for:
- Creating a new user with valid data
- Handling validation errors
- Password hashing
- Email uniqueness validation
- Edge cases and error conditions

Use Jest and follow AAA pattern. Mock external dependencies."
```

#### カバレッジ改善

```
"Analyze the current test coverage for the authentication module.
Identify gaps in coverage and generate tests to improve coverage to at least 90%.
Focus on:
- Branch coverage
- Error handling paths
- Edge cases

Provide a summary of the coverage improvements."
```

#### テストリファクタリング

```
"Refactor the existing test suite for the UserController to:
- Remove test duplication
- Improve test organization
- Add better test descriptions
- Extract common test helpers
- Follow testing best practices

Maintain all existing test functionality."
```

#### E2Eテスト生成

```
"Create E2E tests using Playwright for the user registration flow:
1. Navigate to registration page
2. Fill in registration form
3. Submit form
4. Verify success message
5. Verify user can login
6. Test validation errors

Include tests for both happy path and error scenarios."
```

### Devinのテスト実行

```
"Run the full test suite and:
1. Fix any failing tests
2. Update deprecated test syntax
3. Improve test performance
4. Generate a coverage report
5. Identify and fix flaky tests

Provide a summary of changes made and test results."
```

### Devinのテストメンテナンス

```
"Review and update all tests in the project to:
1. Use the latest testing library versions
2. Follow current best practices
3. Improve test reliability
4. Remove obsolete tests
5. Update test documentation

Create a PR with the changes and a detailed description."
```

### Devinとのベストプラクティス

1. **明確な指示を提供**: テスト目標と要件を明確に述べる
2. **コンテキストを含める**: 関連するコードと既存のテストパターンを提供
3. **制約を指定**: 使用すべきテストフレームワークとライブラリを指定
4. **結果を検証**: Devinが生成したテストを常にレビューして検証
5. **段階的に反復**: 一度にすべてを生成するのではなく、小さな変更から始める

### テストレビューチェックリスト

Devinが生成したテストをレビューする際:

- [ ] テストは独立して実行されるか?
- [ ] テストは適切に構造化されているか(AAA/Given-When-Then)?
- [ ] モックは適切に使用されているか?
- [ ] テスト名は明確でわかりやすいか?
- [ ] エッジケースがカバーされているか?
- [ ] エラー処理がテストされているか?
- [ ] テストは高速に実行されるか?
- [ ] テストは保守可能か?
- [ ] テストはベストプラクティスに従っているか?
- [ ] カバレッジ要件が満たされているか?

---

## まとめ

これらのテスト標準は、組織全体で高品質で信頼性の高い保守可能なソフトウェアを確保するための包括的なフレームワークを提供します。これらの標準に従うことで、バグを早期に発見し、リグレッションを防止し、コードの品質を維持できます。

テストは継続的なプロセスであり、コード自体と同じくらい重要です。これらの標準に従い、Devin AIなどのツールを活用することで、効率的で効果的なテスト戦略を実現できます。

最終更新日: 2024-01-20