# E2Eテスト標準

## トラブルシューティング

### よくある問題と解決策

| 問題 | 原因 | 解決策 |
|------|------|--------|
| **要素が見つからない** | タイミング、動的読み込み | 明示的待機の追加 |
| **StaleElementException** | DOM再レンダリング | 要素の再取得 |
| **タイムアウト** | 処理遅延、ネットワーク問題 | タイムアウト延長、リトライ |
| **不正なクリック** | 要素重なり、アニメーション | 要素の安定化待ち |
| **フレーム切り替え失敗** | iframe内要素へのアクセス | `switchTo().frame()` |

<details>
<summary>トラブルシューティング実装例</summary>

```kotlin
// トラブルシューティングヘルパー
class TroubleshootingHelper(private val driver: WebDriver) {
    
    // 要素が見つからない問題の解決
    fun findElementWithRetry(
        locator: By,
        maxAttempts: Int = 3
    ): WebElement {
        var lastException: Exception? = null
        
        repeat(maxAttempts) { attempt ->
            try {
                waitForElement(locator)
                return driver.findElement(locator)
            } catch (e: NoSuchElementException) {
                lastException = e
                logger.warn("Element not found, attempt ${attempt + 1}")
                Thread.sleep(1000)
                
                // ページをリフレッシュして再試行
                if (attempt == maxAttempts - 1) {
                    driver.navigate().refresh()
                }
            }
        }
        
        throw lastException ?: NoSuchElementException("Element not found after retries")
    }
    
    // StaleElementException対策
    fun clickWithStaleRetry(locator: By, maxAttempts: Int = 3) {
        repeat(maxAttempts) { attempt ->
            try {
                val element = driver.findElement(locator)
                element.click()
                return
            } catch (e: StaleElementReferenceException) {
                if (attempt == maxAttempts - 1) {
                    throw e
                }
                logger.warn("Stale element, retrying...")
                Thread.sleep(500)
            }
        }
    }
    
    // iframe処理
    fun executeInFrame(frameLocator: By, action: () -> Unit) {
        try {
            // フレームに切り替え
            val frame = driver.findElement(frameLocator)
            driver.switchTo().frame(frame)
            
            // アクション実行
            action()
        } finally {
            // デフォルトコンテキストに戻る
            driver.switchTo().defaultContent()
        }
    }
    
    // オーバーレイ要素の処理
    fun clickThroughOverlay(locator: By) {
        // オーバーレイを閉じる
        try {
            val overlay = driver.findElement(By.className("overlay-close"))
            overlay.click()
            Thread.sleep(500)
        } catch (e: NoSuchElementException) {
            // オーバーレイがない場合は無視
        }
        
        // 目的の要素をクリック
        val element = driver.findElement(locator)
        element.click()
    }
    
    // JavaScriptによる強制クリック
    fun forceClick(locator: By) {
        val element = driver.findElement(locator)
        val jsExecutor = driver as JavascriptExecutor
        jsExecutor.executeScript("arguments[0].click();", element)
    }
}
```

</details>

### デバッグ戦略

<details>
<summary>デバッグツールの実装例</summary>

```kotlin
// デバッグヘルパー
class E2EDebugHelper(private val driver: WebDriver) {
    
    // スクリーンショット付きログ
    fun logWithScreenshot(message: String) {
        logger.info(message)
        
        val screenshot = captureScreenshot()
        val filename = "debug-${System.currentTimeMillis()}.png"
        saveScreenshot(screenshot, filename)
        
        logger.info("Screenshot saved: $filename")
    }
    
    // ページ状態のダンプ
    fun dumpPageState(): PageState {
        val jsExecutor = driver as JavascriptExecutor
        
        return PageState(
            url = driver.currentUrl,
            title = driver.title,
            html = driver.pageSource,
            cookies = driver.manage().cookies,
            localStorage = getLocalStorage(jsExecutor),
            sessionStorage = getSessionStorage(jsExecutor),
            console = getConsoleLogs()
        )
    }
    
    // ブラウザコンソールログの取得
    private fun getConsoleLogs(): List<LogEntry> {
        return driver.manage().logs().get(LogType.BROWSER).all
    }
    
    // LocalStorage内容の取得
    private fun getLocalStorage(jsExecutor: JavascriptExecutor): Map<String, String> {
        val script = """
            var items = {};
            for (var i = 0; i < localStorage.length; i++) {
                var key = localStorage.key(i);
                items[key] = localStorage.getItem(key);
            }
            return items;
        """
        
        @Suppress("UNCHECKED_CAST")
        return jsExecutor.executeScript(script) as Map<String, String>
    }
    
    // 要素のハイライト表示
    fun highlightElement(locator: By) {
        val element = driver.findElement(locator)
        val jsExecutor = driver as JavascriptExecutor
        
        jsExecutor.executeScript("""
            arguments[0].style.border = '3px solid red';
            arguments[0].style.backgroundColor = 'yellow';
        """, element)
        
        Thread.sleep(2000)  // 2秒間ハイライト表示
    }
    
    // ステップごとの実行（デバッグモード）
    fun executeStep(stepName: String, action: () -> Unit) {
        logger.info("=== Executing Step: $stepName ===")
        
        try {
            action()
            logger.info("Step completed successfully")
        } catch (e: Exception) {
            logger.error("Step failed: ${e.message}")
            logWithScreenshot("Failure in step: $stepName")
            dumpPageState()
            throw e
        }
    }
}
```

</details>

---

## Devin AIガイドライン

### AIによるE2Eテスト生成

**プロンプト例**:

```
タスク: ユーザー登録フローのE2Eテストを生成してください

要件:
- 有効な入力でのハッピーパス
- 無効なメールアドレスでのエラーケース
- 既存メールでの重複エラーケース
- Page Object Modelを使用
- 適切な待機戦略を実装

技術スタック:
- 言語: [プロジェクトに応じて選択]
- フレームワーク: [プロジェクトに応じて選択]
- ブラウザ: Chrome
```

### AIレビューチェックリスト

**E2Eテストコードのレビューポイント**:
- ✅ Page Object Modelが適切に実装されている
- ✅ ハードコーディングされた待機（sleep）を使用していない
- ✅ 明示的待機が適切に設定されている
- ✅ テストデータがハードコードされていない
- ✅ テスト後のクリーンアップが実装されている
- ✅ エラーハンドリングが適切
- ✅ スクリーンショットが失敗時にキャプチャされる
- ✅ ロケーターがメンテナブル
- ✅ アサーションが明確で意味のあるメッセージを持つ
- ✅ テストが独立して実行可能

### AI支援によるメンテナンス

**定期的なメンテナンスタスク**:
1. **フレークテストの特定**: 安定性スコアが低いテストを検出
2. **ロケーターの更新**: UI変更に伴うロケーター修正
3. **待機時間の最適化**: 過剰なタイムアウトの削減
4. **重複コードの削除**: 共通ロジックのユーティリティ化
5. **カバレッジギャップの特定**: テストされていないフローの検出

**AIプロンプト例（メンテナンス）**:

```
タスク: E2Eテストスイートのフレークを分析し、安定化してください

現状:
- テストスイートの成功率: 85%
- フレークが疑われるテスト: [テスト名リスト]

実行してほしいこと:
1. フレークの原因を特定
2. 適切な待機戦略を提案
3. リトライメカニズムを追加
4. 安定性を95%以上に改善

分析対象: [テストファイルパス]
```

---

## 関連ドキュメント

- [testing-strategy.md](testing-strategy.md) - テスト全体戦略
- [unit-testing.md](unit-testing.md) - ユニットテスト実装ガイド
- [integration-testing.md](integration-testing.md) - 統合テスト実装ガイド
- [defect-management.md](defect-management.md) - 欠陥管理プロセス

---

## バージョン履歴

| バージョン | 日付 | 変更内容 |
|------------|------|----------|
| 1.0.0 | 2025-10-27 | 初版作成 |

---

**注意**: このドキュメントは言語非依存です。具体的な実装詳細は各言語の[01-coding-standards](../01-coding-standards/)を参照してください。