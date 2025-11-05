# インシデント管理 / Incident Management

---

**メタデータ / Metadata**
```yaml
version: 1.0.0
last_updated: 2025-01-15
status: active
owner: Operations Team
category: development-process
```

---

## 📋 目次 / Table of Contents

1. [概要](#概要--overview)
2. [インシデントの定義と分類](#インシデントの定義と分類--incident-definition-and-classification)
3. [インシデント対応プロセス](#インシデント対応プロセス--incident-response-process)
4. [役割と責任](#役割と責任--roles-and-responsibilities)
5. [コミュニケーション](#コミュニケーション--communication)
6. [事後分析](#事後分析--post-incident-analysis)
7. [予防と改善](#予防と改善--prevention-and-improvement)
8. [ツールとリソース](#ツールとリソース--tools-and-resources)

---

## 概要 / Overview

### 目的 / Purpose

このドキュメントは、本番環境で発生するインシデントに対する組織的な対応プロセスを定義します。迅速な復旧、効果的なコミュニケーション、継続的な改善を通じて、サービスの信頼性と顧客満足度を維持します。

### 適用範囲 / Scope

```yaml
covered_systems:
  production_services:
    - 顧客向けWebアプリケーション
    - モバイルアプリケーション
    - API サービス
    - バックエンドサービス
  
  infrastructure:
    - クラウドインフラストラクチャ
    - データベース
    - ネットワーク
    - CDN
  
  dependencies:
    - サードパーティサービス
    - 外部API
    - 決済システム
    - 認証システム
```

### 目標 / Goals

- **迅速な復旧**: MTTR(平均復旧時間)の最小化
- **透明なコミュニケーション**: ステークホルダーへの適切な情報提供
- **継続的改善**: インシデントから学び、再発を防止
- **チーム協力**: 効果的なチームワークの促進
- **顧客信頼**: サービス品質と信頼性の維持

---

## インシデントの定義と分類 / Incident Definition and Classification

### インシデントとは / What is an Incident

**インシデント(Incident)**とは、サービスの標準的な運用を妨げる、またはサービス品質を低下させる予期しない事象です。

### インシデントの判定基準 / Incident Criteria

```yaml
incident_indicators:
  service_availability:
    - サービスの全体的または部分的なダウン
    - エンドポイントのアクセス不能
    - タイムアウトの頻発
  
  performance_degradation:
    - レスポンスタイムの著しい増加(通常の2倍以上)
    - スループットの大幅な低下(50%以上)
    - エラーレートの急増(>5%)
  
  data_integrity:
    - データの損失または破損
    - データ不整合の発見
    - トランザクション失敗の急増
  
  security:
    - セキュリティ侵害の疑い
    - 異常なアクセスパターン
    - データ漏洩の可能性
  
  user_impact:
    - 顧客からの苦情急増
    - サポートチケットの異常な増加
    - ソーシャルメディアでの問題報告
```

### 重要度分類 / Severity Classification

#### Severity 1 (Critical / クリティカル)

```yaml
sev1_critical:
  definition: "サービス全体または主要機能の完全停止"
  
  examples:
    - 本番サービスの完全ダウン
    - すべてのユーザーがアクセス不能
    - 重大なセキュリティ侵害
    - 大規模なデータ損失
    - 決済システムの停止
  
  impact:
    - すべてまたは大多数のユーザーに影響
    - ビジネスへの重大な影響
    - 収益への直接的な影響
    - ブランドイメージへの重大なリスク
  
  response:
    - 即座のエスカレーション
    - 24/7対応必須
    - 全社的な対応態勢
    - エグゼクティブへの即時報告
  
  sla:
    - 検知から15分以内に対応開始
    - 1時間以内に初期評価完了
    - 4時間以内に復旧または回避策
    - 最大許容ダウンタイム: 2時間
```

#### Severity 2 (High / 高)

```yaml
sev2_high:
  definition: "主要機能の著しい機能低下または部分停止"
  
  examples:
    - 主要機能の一部が利用不可
    - パフォーマンスの大幅な低下
    - 一部ユーザーがアクセス不能
    - 重要でない機能の完全停止
    - データの部分的な不整合
  
  impact:
    - 多数のユーザーに影響
    - ビジネスへの顕著な影響
    - 回避策が存在するが不便
    - 顧客満足度への影響
  
  response:
    - 迅速なエスカレーション
    - 営業時間内は即座対応
    - 営業時間外はオンコール対応
    - 関連チームの動員
  
  sla:
    - 検知から30分以内に対応開始
    - 2時間以内に初期評価完了
    - 8時間以内に復旧または回避策
    - 最大許容ダウンタイム: 8時間
```

#### Severity 3 (Medium / 中)

```yaml
sev3_medium:
  definition: "副次的機能の問題または軽度のパフォーマンス低下"
  
  examples:
    - 非主要機能の不具合
    - 軽微なパフォーマンス低下
    - 一部ユーザーへの限定的影響
    - UIの表示問題
    - 非クリティカルなエラーの増加
  
  impact:
    - 限定的なユーザーへの影響
    - ビジネスへの軽微な影響
    - 回避策が容易に利用可能
    - 顧客体験への限定的影響
  
  response:
    - 標準対応プロセス
    - 営業時間内に対応
    - 関連チームによる調査
  
  sla:
    - 検知から1時間以内に対応開始
    - 4時間以内に初期評価完了
    - 24時間以内に修正または回避策
    - 最大許容ダウンタイム: 24時間
```

#### Severity 4 (Low / 低)

```yaml
sev4_low:
  definition: "軽微な問題、ユーザー影響なしまたは最小限"
  
  examples:
    - ドキュメントの誤り
    - ログの異常(機能影響なし)
    - 美観的な問題
    - 非本番環境の問題
  
  impact:
    - ユーザーへの影響なしまたは極小
    - ビジネスへの影響なし
    - 運用上の軽微な不便
  
  response:
    - 通常の開発プロセス
    - バックログに追加
    - 次回リリースで対応
  
  sla:
    - 営業時間内に確認
    - 1営業日以内に評価
    - 次回スプリントで対応検討
```

---

## インシデント対応プロセス / Incident Response Process

### フェーズ1: 検知と報告 / Detection and Reporting

```yaml
detection_methods:
  automated_monitoring:
    - APM(Application Performance Monitoring)アラート
    - インフラストラクチャ監視アラート
    - ログ異常検知
    - Synthetic モニタリング
    - エラーレート監視
  
  manual_detection:
    - エンジニアによる発見
    - QAチームからの報告
    - 顧客サポートからの通知
    - ビジネスチームからの報告
  
  user_reports:
    - カスタマーサポートチケット
    - ソーシャルメディア
    - コミュニティフォーラム
    - 直接的なフィードバック

reporting_process:
  immediate_actions:
    1. インシデント管理システムにチケット作成
    2. 初期重要度の判定
    3. オンコールエンジニアへの通知
    4. 関連チームへの初期通知
  
  required_information:
    - 発見時刻
    - 症状の詳細
    - 影響を受けるサービス/コンポーネント
    - 影響を受けるユーザー数(推定)
    - 再現手順(可能な場合)
    - 関連するログ/スクリーンショット
```

### フェーズ2: トリアージと評価 / Triage and Assessment

```yaml
triage_process:
  timeline: "検知から15分以内(Sev1)、30分以内(Sev2)"
  
  responsible: "Incident Commander または On-call Engineer"
  
  assessment_checklist:
    impact_assessment:
      - [ ] 影響を受けるユーザー数の特定
      - [ ] 影響を受ける機能/サービスの特定
      - [ ] ビジネスへの影響の評価
      - [ ] データ整合性の確認
    
    scope_determination:
      - [ ] 問題の範囲の特定
      - [ ] 関連システムへの影響確認
      - [ ] 根本原因の初期仮説
    
    resource_assessment:
      - [ ] 必要なチーム/専門家の特定
      - [ ] 現在のリソース可用性確認
      - [ ] 追加リソース必要性の判断
  
  severity_adjustment:
    - 初期評価に基づく重要度の調整
    - より多くの情報が得られた場合の再評価
    - エスカレーションの必要性判断
```

### フェーズ3: 対応と復旧 / Response and Recovery

```yaml
response_process:
  incident_war_room:
    setup:
      - 専用Slackチャネルの作成: #incident-YYYY-MM-DD-NNN
      - ビデオ会議の開始(Sev1/Sev2)
      - インシデント管理ダッシュボードの開始
    
    participants:
      - Incident Commander(指揮者)
      - On-call Engineer(技術対応)
      - Subject Matter Experts(専門家)
      - Communications Lead(コミュニケーション担当)
      - Customer Support Rep(必要に応じて)
  
  response_strategies:
    immediate_mitigation:
      priority: "最優先"
      goal: "サービスの迅速な復旧"
      actions:
        - 既知の回避策の適用
        - トラフィックの再ルーティング
        - 問題コンポーネントの隔離
        - フェイルオーバーの実行
        - ロールバックの実施
    
    investigation:
      priority: "高"
      goal: "根本原因の特定"
      actions:
        - ログ分析
        - メトリクス確認
        - トレース分析
        - 変更履歴の確認
        - 依存サービスの確認
    
    resolution:
      priority: "高"
      goal: "永続的な修正"
      actions:
        - 修正の開発・テスト
        - 修正のデプロイ
        - 動作確認
        - モニタリング強化
  
  decision_making:
    quick_decisions:
      - Incident Commander が迅速に決定
      - 技術的判断はSMEに委譲
      - 必要に応じてエスカレーション
    
    documented_actions:
      - すべての主要な決定を記録
      - タイムラインの維持
      - 試行した対策の記録
```

### フェーズ4: 復旧確認 / Recovery Verification

```yaml
verification_process:
  health_checks:
    - [ ] すべてのヘルスチェックエンドポイントが正常
    - [ ] エラーレートが通常レベルに戻った(<1%)
    - [ ] レスポンスタイムが正常範囲内(p95 < 閾値)
    - [ ] スループットが期待レベル
  
  functional_verification:
    - [ ] 主要ユーザーフローのテスト
    - [ ] エンドツーエンドテストの実行
    - [ ] データ整合性の確認
    - [ ] 統合テストの成功
  
  monitoring_period:
    duration: "復旧後最低1時間(Sev1/2)、30分(Sev3/4)"
    activities:
      - メトリクスの継続監視
      - ログの継続確認
      - ユーザーフィードバックの監視
      - サポートチケットの監視
  
  rollback_readiness:
    - ロールバック手順の準備維持
    - 再発時の即座対応準備
    - モニタリングアラートの維持
```

### フェーズ5: インシデントクローズ / Incident Closure

```yaml
closure_criteria:
  technical:
    - [ ] サービスが完全に復旧
    - [ ] すべてのメトリクスが正常
    - [ ] モニタリング期間が完了
    - [ ] 再発リスクが最小化
  
  communication:
    - [ ] すべてのステークホルダーに通知
    - [ ] ステータスページが更新済み
    - [ ] 顧客への最終通知完了
  
  documentation:
    - [ ] インシデントレポートが作成済み
    - [ ] タイムラインが完成
    - [ ] すべてのアクションが記録済み
  
  follow_up:
    - [ ] Post-Incident Review (PIR) がスケジュール済み
    - [ ] アクションアイテムがチケット化済み
    - [ ] 関連ドキュメントが更新済み
```

---

## 役割と責任 / Roles and Responsibilities

### Incident Commander (IC) / インシデント指揮官

```yaml
incident_commander:
  selection:
    - 経験豊富なシニアエンジニアまたはTech Lead
    - インシデント対応の訓練を受けた者
    - Sev1の場合は自動的にエスカレーション
  
  responsibilities:
    coordination:
      - インシデント対応全体の指揮
      - チームメンバーの役割割り当て
      - 意思決定の促進と最終判断
      - タイムラインの維持
    
    communication:
      - 定期的なステータス更新
      - ステークホルダーへの報告
      - エスカレーションの判断
      - 外部コミュニケーションの承認
    
    process:
      - 対応プロセスの遵守確保
      - ドキュメンテーションの監督
      - リソース追加の判断
      - クロージャ基準の判定
  
  authority:
    - 技術的決定への最終承認
    - リソース動員の権限
    - コミュニケーション内容の承認
    - インシデントクローズの決定
  
  handoff:
    - 長期インシデントでの引継ぎ手順
    - 次のICへの完全な状況共有
    - ドキュメントの更新確認
```

### On-call Engineer / オンコールエンジニア

```yaml
on_call_engineer:
  schedule:
    - 24/7カバレッジ
    - 通常1週間のローテーション
    - Primary と Secondary の2階層
  
  responsibilities:
    immediate_response:
      - アラートへの迅速な応答(15分以内)
      - 初期トリアージの実施
      - 重要度の初期判定
      - ICへのエスカレーション(必要時)
    
    technical_investigation:
      - ログとメトリクスの分析
      - 根本原因の調査
      - 修正の実装
      - デプロイの実施
    
    documentation:
      - 調査結果の記録
      - 試行した対策の記録
      - タイムラインへの貢献
  
  escalation:
    - 15分以内に解決の見込みがない場合
    - 専門知識が必要な場合
    - Sev1の場合は即座にエスカレーション
```

### Subject Matter Expert (SME) / 専門家

```yaml
subject_matter_expert:
  role:
    - 特定領域の深い専門知識を提供
    - 技術的な意思決定を支援
    - 複雑な問題のデバッグ
  
  domains:
    - データベース専門家
    - ネットワーク専門家
    - セキュリティ専門家
    - 特定サービスのオーナー
  
  responsibilities:
    - 専門領域の詳細調査
    - 技術的推奨事項の提供
    - 修正の実装支援
    - リスク評価
  
  activation:
    - ICまたはOn-call Engineerからの要請
    - 自動エスカレーションルール
    - 重要度に基づく自動召集
```

### Communications Lead / コミュニケーションリード

```yaml
communications_lead:
  role:
    - すべての外部・内部コミュニケーションの統括
    - ステークホルダーへの情報提供
    - メッセージの一貫性確保
  
  responsibilities:
    internal_communication:
      - 社内ステークホルダーへの更新
      - エグゼクティブへの報告
      - チーム間の調整
    
    external_communication:
      - 顧客向けステータス更新
      - ステータスページの更新
      - ソーシャルメディア対応
      - カスタマーサポートへの情報提供
    
    documentation:
      - コミュニケーションログの維持
      - 送信メッセージの記録
      - フィードバックの収集
  
  coordination:
    - ICとの密接な連携
    - マーケティング/PRチームとの調整
    - 法務/コンプライアンスチームとの調整
```

### Customer Support Representative / カスタマーサポート担当

```yaml
customer_support_rep:
  role:
    - 顧客からの問い合わせ対応
    - 技術チームへのフィードバック
    - 顧客影響の報告
  
  responsibilities:
    - 顧客問い合わせへの対応
    - FAQの準備と更新
    - 顧客感情の報告
    - 回避策の顧客への説明
  
  information_needs:
    - 現在のステータス
    - 推定復旧時間
    - 影響を受ける機能
    - 利用可能な回避策
    - 顧客への推奨アクション
```

---

## コミュニケーション / Communication

### 内部コミュニケーション / Internal Communication

```yaml
internal_communication:
  incident_channel:
    setup: "#incident-YYYY-MM-DD-NNN"
    purpose: "リアルタイムの技術的議論"
    participants: "対応チームメンバー"
    guidelines:
      - 技術的詳細の共有
      - 決定事項の記録
      - アクションアイテムの追跡
      - 外部への情報漏洩に注意
  
  status_updates:
    frequency:
      sev1: "15-30分ごと"
      sev2: "30-60分ごと"
      sev3: "1-2時間ごと"
    
    template: |
      **Status Update - [Time]**
      
      **Current Status**: [Investigating/Identified/Monitoring/Resolved]
      
      **What We Know**:
      - [簡潔な現状説明]
      
      **Impact**:
      - [影響範囲]
      
      **Next Steps**:
      - [予定されているアクション]
      
      **ETA**: [推定復旧時間]
    
    distribution:
      - #incidents チャネル
      - Engineering Leadership
      - Product Management
      - Customer Support
      - Executive Team(Sev1/2)
  
  executive_communication:
    trigger:
      - Sev1 インシデント
      - Sev2 で2時間以上継続
      - 重大なビジネス影響
      - メディア注目の可能性
    
    content:
      - ビジネス影響のサマリー
      - 技術的詳細(簡潔に)
      - 現在の対応状況
      - 推定復旧時間
      - 必要なリソース/決定
    
    channel:
      - 専用Slackチャネル
      - メール(必要に応じて)
      - 電話(緊急時)
```

### 外部コミュニケーション / External Communication

```yaml
external_communication:
  status_page:
    platform: "[Status Page Tool]"
    url: "status.example.com"
    
    update_frequency:
      sev1: "30分ごと"
      sev2: "1時間ごと"
      sev3: "必要に応じて"
    
    incident_lifecycle:
      investigating:
        message: "問題を調査中です。影響と範囲を確認しています。"
      identified:
        message: "問題を特定しました。修正作業を進めています。"
      monitoring:
        message: "修正を実施しました。状況を監視しています。"
      resolved:
        message: "問題は解決しました。サービスは正常に動作しています。"
  
  customer_notifications:
    email:
      trigger:
        - Sev1 インシデント
        - 多数のユーザーに影響(>10%)
        - 2時間以上のダウンタイム
      
      content:
        - 問題の簡潔な説明
        - 影響を受ける機能
        - 現在の状況
        - 推定復旧時間
        - 回避策(利用可能な場合)
        - 問い合わせ先
      
      timing:
        - 初期通知: インシデント開始後1時間以内
        - 更新: 大きな進展があった場合
        - 最終通知: 解決後
    
    in_app_notification:
      use_cases:
        - サービス品質低下の警告
        - メンテナンスモードへの移行通知
        - 機能制限の通知
  
  social_media:
    platforms:
      - Twitter/X
      - LinkedIn
      - Facebook
    
    guidelines:
      - 簡潔で明確なメッセージ
      - 技術的詳細は避ける
      - ステータスページへのリンク
      - 定期的な更新
      - 誠実で透明なトーン
    
    approval:
      - Communications Lead による承認
      - Marketing/PR チームとの調整
```

### コミュニケーションテンプレート / Communication Templates

```markdown
## 初期通知テンプレート / Initial Notification

**Subject**: [サービス名] で問題が発生しています

皆様

現在、[サービス名]で技術的な問題が発生しており、一部のお客様がサービスにアクセスできない、または遅延が発生している状況です。

**影響範囲**:
- [影響を受ける機能]
- [影響を受けるユーザー数/割合]

**現在の状況**:
当社のチームが問題を調査中です。根本原因を特定し、迅速な解決に向けて作業を進めています。

**次の更新予定**:
[時刻]までに追加情報を提供いたします。

最新情報は以下でご確認いただけます:
[ステータスページURL]

ご迷惑をおかけし申し訳ございません。

---

## 進捗更新テンプレート / Progress Update

**Subject**: [サービス名] 問題の進捗状況

皆様

[サービス名]の問題について、以下の進捗をご報告いたします。

**現在の状況**:
[問題の現状説明]

**実施した対策**:
- [対策1]
- [対策2]

**次のステップ**:
- [予定アクション1]
- [予定アクション2]

**推定復旧時間**:
[ETA]

最新情報: [ステータスページURL]

引き続きご迷惑をおかけし申し訳ございません。

---

## 解決通知テンプレート / Resolution Notification

**Subject**: [サービス名] の問題は解決しました

皆様

[サービス名]の問題は解決し、サービスは正常に動作しています。

**インシデントサマリー**:
- 発生時刻: [時刻]
- 復旧時刻: [時刻]
- 影響時間: [時間]
- 影響範囲: [詳細]

**根本原因**:
[簡潔な説明]

**実施した対策**:
[解決策の概要]

**再発防止策**:
今後このような問題を防ぐため、以下の対策を実施します:
- [対策1]
- [対策2]

詳細な事後分析レポートは[X]日以内に公開予定です。

ご迷惑をおかけし、誠に申し訳ございませんでした。

詳細: [ステータスページURL]
```

---

## 事後分析 / Post-Incident Analysis

### Post-Incident Review (PIR) / 事後レビュー

```yaml
pir_process:
  scheduling:
    timing: "インシデント解決後24-48時間以内"
    duration: "60-90分"
    participants:
      required:
        - Incident Commander
        - 主要な対応メンバー
        - サービスオーナー
        - Engineering Manager
      optional:
        - 他チームの代表
        - Product Manager
        - 関心のあるエンジニア
  
  preparation:
    - インシデントタイムラインの完成
    - 関連メトリクス・ログの収集
    - 参加者への事前資料送付
    - 会議室とビデオ会議の準備
  
  meeting_structure:
    1_introduction: "5分"
      - インシデント概要の共有
      - PIRの目的の確認
      - ブレームレス文化の強調
    
    2_timeline_review: "15-20分"
      - 時系列での事象の確認
      - 各フェーズでの決定の振り返り
      - 質問と明確化
    
    3_what_went_well: "10-15分"
      - うまくいった対応
      - 効果的だったツール/プロセス
      - チームワークの良い例
    
    4_what_went_wrong: "15-20分"
      - 問題のあった点
      - 遅延の原因
      - プロセスのギャップ
      - ツールの不足
    
    5_root_cause: "10-15分"
      - 根本原因の特定
      - 5 Whys 分析
      - 寄与要因の議論
    
    6_action_items: "15-20分"
      - 改善アクションの特定
      - 優先度付け
      - 担当者と期限の割り当て
      - 追跡方法の決定
  
  principles:
    blameless:
      - 個人を責めない
      - システムとプロセスに焦点
      - 学習の機会として捉える
      - 心理的安全性の確保
    
    actionable:
      - 具体的なアクションアイテム
      - 実行可能な改善策
      - 明確な担当者と期限
      - 測定可能な成果
```

### PIRレポート / PIR Report

```markdown
# Post-Incident Review Report

## メタデータ / Metadata

**インシデントID**: INC-YYYY-MM-DD-NNN
**発生日時**: YYYY-MM-DD HH:MM (JST)
**解決日時**: YYYY-MM-DD HH:MM (JST)
**総影響時間**: [時間]
**重要度**: Sev1/2/3/4
**Incident Commander**: [名前]

---

## エグゼクティブサマリー / Executive Summary

[3-5段落でインシデントの概要、影響、根本原因、主要な学びを説明]

---

## インシデント詳細 / Incident Details

### 影響範囲 / Impact

**ユーザー影響**:
- 影響を受けたユーザー数: [数値] ([全体の%])
- 影響を受けた機能: [リスト]
- ユーザーエクスペリエンスへの影響: [説明]

**ビジネス影響**:
- 推定収益損失: [金額](該当する場合)
- SLA違反: [はい/いいえ]
- ブランドイメージへの影響: [評価]

**システム影響**:
- 影響を受けたサービス: [リスト]
- データ整合性: [状態]
- パフォーマンスメトリクス: [詳細]

### タイムライン / Timeline

| 時刻 (JST) | イベント | 担当者 |
|-----------|---------|--------|
| HH:MM | 問題の最初の兆候(自動検知/ユーザー報告) | [名前/システム] |
| HH:MM | インシデントチケット作成 | [名前] |
| HH:MM | On-call エンジニアが対応開始 | [名前] |
| HH:MM | Sev1 にエスカレーション | [名前] |
| HH:MM | Incident Commander 参加 | [名前] |
| HH:MM | War Room 開設 | [名前] |
| HH:MM | 根本原因を特定 | [名前] |
| HH:MM | 修正をデプロイ | [名前] |
| HH:MM | サービス復旧確認 | [名前] |
| HH:MM | モニタリング期間完了 | [名前] |
| HH:MM | インシデントクローズ | [名前] |

---

## 根本原因分析 / Root Cause Analysis

### 直接的原因 / Immediate Cause

[問題を直接引き起こした技術的要因]

### 根本原因 / Root Cause

[5 Whys分析などを用いた深層原因]

**5 Whys分析**:
1. **なぜ問題が発生したか?** → [回答]
2. **なぜ[回答1]だったのか?** → [回答]
3. **なぜ[回答2]だったのか?** → [回答]
4. **なぜ[回答3]だったのか?** → [回答]
5. **なぜ[回答4]だったのか?** → [回答] ← 根本原因

### 寄与要因 / Contributing Factors

- [要因1]: [説明]
- [要因2]: [説明]
- [要因3]: [説明]

---

## 対応の評価 / Response Evaluation

### うまくいった点 / What Went Well

1. **[項目1]**
   - [詳細説明]
   - [なぜうまくいったか]

2. **[項目2]**
   - [詳細説明]
   - [なぜうまくいったか]

3. **[項目3]**
   - [詳細説明]
   - [なぜうまくいったか]

### 改善が必要な点 / What Needs Improvement

1. **[項目1]**
   - [問題の説明]
   - [影響]
   - [改善の方向性]

2. **[項目2]**
   - [問題の説明]
   - [影響]
   - [改善の方向性]

3. **[項目3]**
   - [問題の説明]
   - [影響]
   - [改善の方向性]

---

## アクションアイテム / Action Items

### 即座の対応 / Immediate Actions (完了済み)

| アクション | 担当者 | 完了日 | ステータス |
|-----------|--------|--------|-----------|
| [アクション1] | [名前] | YYYY-MM-DD | ✅ 完了 |
| [アクション2] | [名前] | YYYY-MM-DD | ✅ 完了 |

### 短期改善 / Short-term Improvements (1-4週間)

| アクション | 担当者 | 期限 | 優先度 | チケット |
|-----------|--------|------|--------|---------|
| [アクション1] | [名前] | YYYY-MM-DD | High | [JIRA-XXX] |
| [アクション2] | [名前] | YYYY-MM-DD | Medium | [JIRA-XXX] |

### 長期改善 / Long-term Improvements (1-3ヶ月)

| アクション | 担当者 | 期限 | 優先度 | チケット |
|-----------|--------|------|--------|---------|
| [アクション1] | [名前] | YYYY-MM-DD | Medium | [JIRA-XXX] |
| [アクション2] | [名前] | YYYY-MM-DD | Low | [JIRA-XXX] |

---

## 学びと推奨事項 / Lessons Learned and Recommendations

### 技術的学び / Technical Lessons

1. [学び1]
2. [学び2]
3. [学び3]

### プロセスの学び / Process Lessons

1. [学び1]
2. [学び2]
3. [学び3]

### 組織的学び / Organizational Lessons

1. [学び1]
2. [学び2]
3. [学び3]

---

## 添付資料 / Attachments

- [ダッシュボードスクリーンショット]
- [関連ログ]
- [メトリクスグラフ]
- [アーキテクチャ図]
- [Slackスレッドのエクスポート]

---

**レポート作成者**: [名前]
**作成日**: YYYY-MM-DD
**レビュアー**: [名前リスト]
**承認者**: [Engineering Manager名]
```

---

## 予防と改善 / Prevention and Improvement

### 予防策 / Preventive Measures

```yaml
prevention_strategies:
  design_for_failure:
    - Circuit Breaker パターンの実装
    - Graceful Degradation の設計
    - タイムアウトと再試行ロジック
    - Rate Limiting の実装
  
  redundancy:
    - Multi-AZ/Multi-Region デプロイメント
    - データベースレプリケーション
    - ロードバランシング
    - フェイルオーバーメカニズム
  
  capacity_planning:
    - 負荷テストの定期実施
    - キャパシティモニタリング
    - オートスケーリング設定
    - リソース使用率の追跡
  
  change_management:
    - 段階的ロールアウト
    - カナリアデプロイメント
    - Feature Flags の活用
    - ロールバック計画の準備
```

### モニタリング強化 / Enhanced Monitoring

```yaml
monitoring_improvements:
  proactive_monitoring:
    - Synthetic モニタリング
    - Real User Monitoring (RUM)
    - APM (Application Performance Monitoring)
    - インフラストラクチャ監視
  
  alerting_refinement:
    - アラート疲れの軽減
    - 意味のある閾値設定
    - アラートの優先度付け
    - False Positive の削減
  
  observability:
    - 詳細なロギング
    - 分散トレーシング
    - メトリクスの充実
    - ダッシュボードの改善
```

### 継続的改善 / Continuous Improvement

```yaml
improvement_cycle:
  metrics_tracking:
    - MTTR (Mean Time To Recovery)
    - MTTD (Mean Time To Detection)
    - MTBF (Mean Time Between Failures)
    - インシデント頻度
    - PIR完了率
    - アクションアイテム完了率
  
  quarterly_review:
    - インシデントトレンド分析
    - 繰り返しパターンの特定
    - プロセス改善の評価
    - ツール有効性の評価
  
  knowledge_sharing:
    - PIRレポートの公開
    - インシデント事例研究
    - ランチ&ラーンセッション
    - Runbook の充実
  
  training:
    - インシデント対応訓練
    - Game Day の実施
    - ロールプレイ演習
    - 新メンバーへのオンボーディング
```

---

## ツールとリソース / Tools and Resources

### インシデント管理ツール / Incident Management Tools

```yaml
tools:
  incident_management:
    primary: "[Tool Name - e.g., PagerDuty]"
    features:
      - アラート集約
      - オンコールスケジュール管理
      - エスカレーションポリシー
      - インシデントタイムライン
      - ステータスページ統合
  
  communication:
    slack:
      - 専用インシデントチャネル
      - Bot による自動化
      - ステータス更新の統合
    
    video_conferencing:
      - Zoom/Google Meet
      - 画面共有機能
      - 録画機能
  
  monitoring_and_alerting:
    apm: "[Tool - e.g., Datadog, New Relic]"
    infrastructure: "[Tool - e.g., Prometheus, CloudWatch]"
    logging: "[Tool - e.g., ELK Stack, Splunk]"
    tracing: "[Tool - e.g., Jaeger, Zipkin]"
  
  documentation:
    wiki: "[Tool - e.g., Confluence]"
    runbooks: "[Location]"
    postmortems: "[Repository]"
```

### Runbook / ランブック

```yaml
runbook_structure:
  service_overview:
    - サービスの目的と機能
    - アーキテクチャ図
    - 依存関係
    - 重要なエンドポイント
  
  common_issues:
    - よくある問題とその解決策
    - トラブルシューティング手順
    - ログの確認方法
    - メトリクスの解釈
  
  operational_procedures:
    - デプロイ手順
    - ロールバック手順
    - スケーリング手順
    - メンテナンス手順
  
  emergency_contacts:
    - サービスオーナー
    - オンコール担当
    - エスカレーション先
    - 外部ベンダー連絡先
```

---

## 関連ドキュメント / Related Documents

### 内部リンク
- [Change Management](./change-management.md) - 変更管理プロセス
- [On-call Guide](../06-operations/on-call-guide.md) - オンコール担当ガイド
- [Monitoring Strategy](../06-operations/monitoring-strategy.md) - 監視戦略

### テンプレート
- PIRレポートテンプレート(本ドキュメント内)
- コミュニケーションテンプレート(本ドキュメント内)
- Runbookテンプレート

### 外部参考資料
- [Google SRE Book - Incident Management](https://sre.google/sre-book/managing-incidents/)
- [Atlassian Incident Management Handbook](https://www.atlassian.com/incident-management)
- [PagerDuty Incident Response](https://response.pagerduty.com/)

---

## バージョン履歴 / Version History

```yaml
changelog:
  v1.0.0:
    date: "2025-01-15"
    changes:
      - 初版リリース
      - インシデント分類の定義
      - 対応プロセスの確立
      - 役割と責任の明確化
      - コミュニケーション戦略の策定
      - PIRプロセスの定義
    author: "Operations Team"
```

---

## 承認 / Approval

```yaml
approvals:
  - role: "VP of Engineering"
    name: "[Name]"
    date: "2025-01-15"
    status: "Approved"
  
  - role: "Head of Operations"
    name: "[Name]"
    date: "2025-01-15"
    status: "Approved"
  
  - role: "Head of SRE"
    name: "[Name]"
    date: "2025-01-15"
    status: "Approved"
```

---

## 連絡先 / Contact Information

```yaml
contacts:
  incident_response:
    team: "Operations Team"
    email: "ops@example.com"
    slack: "#incidents"
    on_call: "PagerDuty"
  
  process_questions:
    team: "SRE Team"
    email: "sre@example.com"
    slack: "#sre"
```
