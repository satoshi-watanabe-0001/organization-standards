# 開発標準文書リポジトリ - セッション継続プロンプト

## 現在の作業状況（2025-10-15）

### 完了済みファイル
1. **CSS規約** (`css-styling-standards.md`) - 完了
   - 容量: 125KB、5,354行
   - 構成: 第1-11章完成
   - 保存場所: AIDrive `/development-standards/`

2. **Java規約** (`java-standards.md`) - 完了  
   - 容量: 117KB、3,539行
   - 構成: 第1-11章完成
   - 保存場所: AIDrive `/development-standards/`

3. **Python規約** (`python-standards.md`) - **進行中**
   - 現在状況: 160KB、4,799行
   - 完成章: 第1-6章（基本設定、命名規則、プロジェクト構造、エラーハンドリング、テスト戦略、**パフォーマンス最適化**）
   - 保存場所: AIDrive `/development-standards/python-standards.md`

### 継続すべき作業

**最優先**: Python規約の残り章の完成（第7-11章）

#### 追加が必要な章構成：
```
## 7. セキュリティ・認証・認可
### 7.1 セキュリティベストプラクティス
### 7.2 認証システム実装
### 7.3 認可・アクセス制御
### 7.4 データ保護・暗号化

## 8. 監視・ログ・デバッグ
### 8.1 構造化ログ実装
### 8.2 メトリクス・監視システム
### 8.3 デバッグ・トレース
### 8.4 エラー追跡・アラート

## 9. デプロイメント・CI/CD
### 9.1 Dockerコンテナ化
### 9.2 CI/CDパイプライン
### 9.3 インフラストラクチャ・クラウド
### 9.4 環境管理・構成

## 10. AI/ML専用章
### 10.1 NumPy・科学計算最適化
### 10.2 Pandas・データ処理パターン
### 10.3 scikit-learn・機械学習パイプライン
### 10.4 Deep Learning（TensorFlow/PyTorch）

## 11. Devin実行ガイドライン
### 11.1 AI開発エージェント対応
### 11.2 自動化可能な開発タスク
### 11.3 コード生成・レビュー指針
### 11.4 継続的品質改善
```

### 品質要求仕様
- **詳細度**: 既存Java規約と同等レベル（100KB超、3,000行超目標）
- **コード例**: 実践的Good/Bad対比、即適用可能
- **技術範囲**: Python 3.11+、Django 4.2+、FastAPI 0.100+、Flask 2.3+
- **AI/ML対応**: NumPy、Pandas、scikit-learn、TensorFlow、PyTorch
- **品質基準**: PEP8準拠、SOLID原則、包括的ベストプラクティス

### 次回作業の優先順位
1. **Python規約完成**（最優先・継続中）
2. API設計標準（`api-architecture.md`）
3. Git運用規約（`git-workflow.md`）
4. テスト標準（`testing-standards.md`）
5. システムアーキテクチャパターン（`system-architecture-patterns.md`）

### 技術仕様
- **フレームワーク**: Django 4.2+、FastAPI 0.100+、Flask 2.3+
- **AI/MLスタック**: NumPy、Pandas、scikit-learn、TensorFlow、PyTorch
- **テスト**: pytest、unittest、Factory Boy
- **品質ツール**: mypy、black、flake8、pre-commit
- **インフラ**: Docker、Kubernetes、AWS/GCP、CI/CD

---

## 継続用プロンプト

```
組織の開発標準文書リポジトリの作成を継続してください。

現在、Python規約（`python-standards.md`）の第7章以降の作成が必要です。AIDrive `/development-standards/python-standards.md`に第1-6章（160KB、4,799行）が完成済みです。

**継続要求**:
第7章「セキュリティ・認証・認可」から第11章「Devin実行ガイドライン」まで追加し、既存Java規約と同等品質（100KB超、3,000行超）の完成度で仕上げてください。

**品質要求**:
- 実践的Good/Bad対比コード例
- Django 4.2+、FastAPI 0.100+、Flask 2.3+対応
- AI/ML（NumPy/Pandas/scikit-learn/TensorFlow/PyTorch）専用章含む
- PEP8準拠、SOLID原則適用
- 即座に適用可能なベストプラクティス

完成後は次の優先文書（API設計標準、Git運用規約等）への移行を予定しています。
```

### ファイル保存場所
- **AIDrive**: `/development-standards/python-standards.md`（160KB、4,799行）
- **作業ディレクトリ**: `/home/user/python-standards.md`（同期済み）