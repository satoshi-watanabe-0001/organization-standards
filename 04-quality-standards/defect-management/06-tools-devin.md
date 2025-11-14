# 欠陥管理標準

## ツールと自動化

### 欠陥追跡ツールの選定基準

**主要機能要件**:
- ワークフロー管理とカスタマイズ
- 優先度・重要度の設定
- 添付ファイル対応（スクリーンショット、ログ）
- 検索とフィルタリング
- レポーティングとダッシュボード
- 通知とアラート
- API連携

**統合要件**:
- バージョン管理システム（Git）
- CI/CDパイプライン
- テスト管理ツール
- コミュニケーションツール（Slack、Teams）

### 自動化の機会

<details>
<summary>自動化実装例</summary>

```kotlin
// 自動欠陥検出システム
class AutomatedDefectDetection {
    
    // ログから自動検出
    fun detectFromLogs(logs: List<LogEntry>): List<PotentialDefect> {
        val potentialDefects = mutableListOf<PotentialDefect>()
        
        logs.forEach { log ->
            when {
                isErrorPattern(log) -> {
                    potentialDefects.add(
                        PotentialDefect(
                            type = DefectType.ERROR,
                            severity = determineSeverityFromLog(log),
                            evidence = log,
                            confidence = 0.9
                        )
                    )
                }
                isPerformanceAnomaly(log) -> {
                    potentialDefects.add(
                        PotentialDefect(
                            type = DefectType.PERFORMANCE,
                            severity = Severity.MEDIUM,
                            evidence = log,
                            confidence = 0.7
                        )
                    )
                }
            }
        }
        
        return potentialDefects
    }
    
    // メトリクスから自動検出
    fun detectFromMetrics(metrics: SystemMetrics): List<PotentialDefect> {
        val potentialDefects = mutableListOf<PotentialDefect>()
        
        // エラー率のスパイク検出
        if (metrics.errorRate > metrics.errorRateThreshold * 2) {
            potentialDefects.add(
                PotentialDefect(
                    type = DefectType.RELIABILITY,
                    severity = Severity.HIGH,
                    evidence = "Error rate spike: ${metrics.errorRate}",
                    confidence = 0.85
                )
            )
        }
        
        // レスポンスタイム劣化検出
        if (metrics.responseTime > metrics.responseTimeBaseline * 1.5) {
            potentialDefects.add(
                PotentialDefect(
                    type = DefectType.PERFORMANCE,
                    severity = Severity.MEDIUM,
                    evidence = "Response time degradation: ${metrics.responseTime}ms",
                    confidence = 0.8
                )
            )
        }
        
        return potentialDefects
    }
    
    // 自動トリアージ
    fun autoTriage(
        potentialDefect: PotentialDefect
    ): TriageRecommendation {
        val priority = determinePriorityFromMetrics(potentialDefect)
        val assignee = findBestAssigneeForDefectType(potentialDefect.type)
        
        return TriageRecommendation(
            shouldCreateTicket = potentialDefect.confidence > 0.7,
            suggestedPriority = priority,
            suggestedAssignee = assignee,
            reasoning = "Based on ${potentialDefect.type} and ${potentialDefect.severity}"
        )
    }
}

// 自動通知システム
class DefectNotificationSystem {
    
    fun notifyOnStatusChange(
        defect: Defect,
        oldStatus: DefectStatus,
        newStatus: DefectStatus
    ) {
        val notification = createNotification(defect, oldStatus, newStatus)
        
        // 関係者に通知
        val recipients = determineRecipients(defect, newStatus)
        recipients.forEach { recipient ->
            sendNotification(recipient, notification)
        }
    }
    
    fun notifyOnSLAViolation(defect: Defect) {
        val sla = getSLAForPriority(defect.priority)
        val elapsed = Duration.between(defect.reportedAt, getCurrentTimestamp())
        
        if (elapsed > sla.responseTime && defect.assignee == null) {
            escalate(
                defect = defect,
                reason = "SLA violation: No assignee after ${elapsed.toHours()} hours",
                escalateTo = defect.reporter.manager
            )
        }
    }
    
    private fun determineRecipients(
        defect: Defect,
        newStatus: DefectStatus
    ): List<User> {
        return when (newStatus) {
            DefectStatus.OPEN -> listOf(defect.assignee ?: defect.reporter)
            DefectStatus.FIXED -> listOfNotNull(defect.reporter, getQALead())
            DefectStatus.VERIFIED -> listOf(defect.assignee, defect.reporter)
            DefectStatus.REOPENED -> listOf(defect.assignee)
            else -> emptyList()
        }
    }
}

// 自動メトリクス収集
class AutomatedMetricsCollection {
    
    fun collectDailyMetrics(): DailyMetricsReport {
        val defects = getAllActiveDefects()
        val calculator = DefectMetricsCalculator()
        
        val report = DailyMetricsReport(
            date = getCurrentDate(),
            newDefects = defects.count { it.reportedAt.isToday() },
            fixedDefects = defects.count { 
                it.status == DefectStatus.FIXED && 
                it.fixCompletedAt?.isToday() == true 
            },
            openDefects = defects.count { it.isOpen() },
            criticalOpenDefects = defects.count { 
                it.isOpen() && it.severity == Severity.CRITICAL 
            },
            averageAge = calculator.calculateAverageAge(defects)
        )
        
        // 自動配信
        distributeReport(report)
        
        // アラート判定
        checkAlertsAndNotify(report)
        
        return report
    }
    
    private fun checkAlertsAndNotify(report: DailyMetricsReport) {
        // クリティカル欠陥が閾値超過
        if (report.criticalOpenDefects > CRITICAL_THRESHOLD) {
            sendAlert(
                AlertType.CRITICAL_DEFECTS_HIGH,
                "Critical open defects: ${report.criticalOpenDefects}"
            )
        }
        
        // オープン欠陥の増加傾向
        val trend = analyzeTrend(report.openDefects)
        if (trend == TrendDirection.INCREASING) {
            sendAlert(
                AlertType.OPEN_DEFECTS_INCREASING,
                "Open defects trending upward"
            )
        }
    }
}
```

</details>

---

## Devin AIガイドライン

### AIによる欠陥分析

**プロンプト例（欠陥分類）**:

```
タスク: 以下の欠陥レポートを分析し、適切な分類を提案してください

欠陥情報:
タイトル: "決済ページでタイムアウトエラー"
説明: "ユーザーが支払い確定ボタンをクリックすると、30秒後にタイムアウトエラーが表示される"
再現手順: [手順詳細]
環境: 本番環境、Chrome 120、Windows 11

分析してほしい項目:
1. 重要度（Critical/High/Medium/Low）
2. 優先度（P1/P2/P3/P4）
3. 欠陥タイプ（Functional/Performance/Security等）
4. 影響を受けるコンポーネント
5. 推定修正時間
6. 潜在的な根本原因の仮説
```

### AIによる根本原因分析支援

**プロンプト例（RCA支援）**:

```
タスク: 以下の欠陥に対して5-Why分析を実施してください

欠陥: "顧客データが更新操作中に削除された"

提供情報:
- エラーログ: [ログ内容]
- データベースクエリ履歴: [クエリログ]
- コード変更履歴: [最近のコミット]

実施してほしいこと:
1. 5-Why分析の実施
2. 根本原因の特定
3. 是正措置の提案（短期・長期）
4. 予防策の提案
5. 類似問題のリスク評価
```

### AIによる予防策提案

**プロンプト例（予防策）**:

```
タスク: 過去3ヶ月の欠陥データを分析し、予防策を提案してください

データ:
- 総欠陥数: 45件
- クリティカル: 5件
- 主なカテゴリ: データ検証エラー（15件）、UIバグ（12件）、パフォーマンス（8件）

分析してほしい項目:
1. 頻出する欠陥パターンの特定
2. 各パターンに対する予防策
3. プロセス改善の提案
4. 自動化の機会の特定
5. トレーニングニーズの評価
```

### AI活用のベストプラクティス

**効果的な使用方法**:
- **欠陥レポート品質向上**: AIによるレポートの完全性チェック
- **重複検出**: 過去の欠陥との類似度分析
- **トリアージ支援**: 優先度決定の補助
- **パターン分析**: 大量の欠陥データからの傾向抽出
- **ドキュメント生成**: RCAレポートや改善計画書の下書き作成

**制限事項と注意点**:
- AIの提案は必ず人間がレビュー
- ビジネスコンテキストの理解が必要な判断は人間が実施
- センシティブな情報の取り扱いに注意
- 最終的な意思決定は人間の責任

---

## 関連ドキュメント

- [testing-strategy.md](testing-strategy.md) - テスト全体戦略
- [quality-metrics.md](quality-metrics.md) - 品質メトリクス
- [code-quality-standards.md](code-quality-standards.md) - コード品質基準
- [test-data-management.md](test-data-management.md) - テストデータ管理

---

## バージョン履歴

| バージョン | 日付 | 変更内容 |
|------------|------|----------|
| 1.0.0 | 2025-10-27 | 初版作成 |

---

**注意**: このドキュメントは言語非依存です。具体的な実装詳細は各言語の[01-coding-standards](../01-coding-standards/)を参照してください。