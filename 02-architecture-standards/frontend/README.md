# Frontend Architecture Standards

**対象**: Next.js、React、TypeScript、Vite  
**最終更新**: 2025-11-13  
**元ファイル**: `frontend-architecture.md` (98.8 KB) → 8ファイル（平均12.8 KB、削減率87%）

---

## 📋 概要

本規約は、フロントエンドアーキテクチャの標準を定義し、Next.js、React、TypeScript、Viteを使用した統一的で保守可能なコードベースの構築を支援します。コンポーネント駆動開発、宣言的UI、状態管理分離の原則に基づき、高パフォーマンス、アクセシブル、セキュアなフロントエンドアプリケーションの開発を目指します。

**主要スコープ**:
- アーキテクチャ原則とパターン
- アプリケーション構造とルーティング
- 状態管理戦略（TanStack Query、Zustand、Redux Toolkit）
- コンポーネント設計とデータフェッチング
- パフォーマンス最適化とSEO対策
- アクセシビリティとセキュリティ
- テスト戦略と国際化
- ビルド最適化、モニタリング、ベストプラクティス

---

## 📁 ドキュメント構成

### 1. [Introduction & Principles](01-introduction-principles.md)（5.6 KB）
**概要、目的、アーキテクチャ原則、基本パターン**

- 概要と目的
- 技術スタック（Next.js、React、TypeScript、Vite）
- 基本原則（コンポーネント駆動、宣言的UI、状態管理分離）
- アーキテクチャパターン（レイヤー分離、依存性注入）

**いつ読む**: プロジェクト開始時、アーキテクチャ設計時

---

### 2. [Application Structure & Routing](02-application-structure-routing.md)（11.7 KB）
**アプリケーション構造、ディレクトリ設計、ルーティング**

- Next.js App Routerディレクトリ構造
- Vite + Reactディレクトリ構造
- 命名規則（ファイル、コンポーネント、フォルダ）
- Next.js App Router（推奨）
- React Router（Vite）

**いつ読む**: プロジェクトセットアップ時、ディレクトリ構成決定時、ルーティング実装時

---

### 3. [State Management](03-state-management.md)（9.5 KB）
**状態管理戦略、TanStack Query、Zustand、Redux Toolkit**

- 状態管理戦略（サーバー状態 vs クライアント状態）
- TanStack Query（サーバー状態、推奨）
- Zustand（軽量グローバル状態）
- Redux Toolkit（複雑な状態管理）

**いつ読む**: 状態管理ライブラリ選定時、データフェッチング実装時、グローバル状態設計時

---

### 4. [Component Design & Data Fetching](04-component-design-data-fetching.md)（13.7 KB）
**コンポーネント設計、分類、データフェッチング戦略**

- コンポーネント分類（Page、Feature、UI、Layout）
- コンポーネント設計原則（単一責任、再利用性、Props設計）
- データフェッチング戦略
- Next.js Server Components
- Client-Side Data Fetching
- エラーハンドリング

**いつ読む**: コンポーネント設計時、データフェッチング実装時、リファクタリング時

---

### 5. [Performance Optimization & SEO](05-performance-seo.md)（14.4 KB）
**パフォーマンス最適化、Core Web Vitals、SEO対策**

- Core Web Vitals目標値（LCP < 2.5s、FID < 100ms、CLS < 0.1）
- 最適化手法（コード分割、遅延ロード、画像最適化）
- パフォーマンスモニタリング（Lighthouse、Web Vitals）
- メタデータ管理（`<title>`, `<meta>`, Open Graph）
- 構造化データ（JSON-LD）
- sitemap.xml、robots.txt

**いつ読む**: パフォーマンス問題発生時、SEO最適化時、Lighthouseスコア改善時

---

### 6. [Accessibility & Security](06-accessibility-security.md)（13.9 KB）
**アクセシビリティ、WCAG 2.1準拠、セキュリティ対策**

- WCAG 2.1 AAレベル準拠
- アクセシブルなコンポーネント（フォーム、ナビゲーション、モーダル）
- セキュリティ原則（最小権限、深層防御、セキュアデフォルト）
- JWT認証、セッション管理
- XSS、CSRF、CSP対策
- データ保護・暗号化

**いつ読む**: アクセシビリティレビュー時、セキュリティ監査時、認証実装時

---

### 7. [Testing Strategy & Internationalization](07-testing-i18n.md)（17.7 KB）
**テスト戦略、テストピラミッド、国際化**

- テストピラミッド（ユニット70%、統合20%、E2E10%）
- ユニットテスト（Vitest、React Testing Library）
- 統合テスト、E2Eテスト（Playwright）
- ビジュアルリグレッションテスト（Chromatic、Percy）
- テストカバレッジ（80%以上）
- 国際化戦略（next-intl）
- 翻訳ファイル構造（JSON、YAML）
- 日時・数値フォーマット、RTL対応

**いつ読む**: テスト実装時、国際化対応時、多言語サイト構築時

---

### 8. [Build, Monitoring & Best Practices](08-build-monitoring-best-practices.md)（15.5 KB）
**ビルド最適化、モニタリング、エラートラッキング、ベストプラクティス**

- ビルド設定（Next.js、Vite）
- バンドルサイズ最適化、Tree Shaking
- コード分割戦略（Dynamic Import、Route-based Split）
- キャッシュ戦略（ISR、SWR、Stale-While-Revalidate）
- CDN最適化
- Error Tracking（Sentry）
- Performance Monitoring（Vercel Analytics、Google Analytics）
- Analytics実装
- コーディング規約チェックリスト
- パフォーマンス・アクセシビリティチェックリスト
- 参考資料、公式ドキュメント

**いつ読む**: 本番デプロイ準備時、ビルド最適化時、モニタリング設定時

---

## 🚀 クイックスタートガイド

### プロジェクト開始時のチェックリスト
1. **[01-introduction-principles.md](01-introduction-principles.md)** でアーキテクチャ原則を理解
2. **[02-application-structure-routing.md](02-application-structure-routing.md)** でディレクトリ構造をセットアップ
3. **[03-state-management.md](03-state-management.md)** で状態管理戦略を決定
4. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** で必須チェック項目TOP25を確認

### コンポーネント実装時のチェックリスト
1. **コンポーネント分類**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md) でPage/Feature/UI/Layoutを判断
2. **データフェッチング**: [04-component-design-data-fetching.md](04-component-design-data-fetching.md) でServer Components vs Client-Sideを選択
3. **状態管理**: [03-state-management.md](03-state-management.md) でTanStack Query/Zustandを活用
4. **アクセシビリティ**: [06-accessibility-security.md](06-accessibility-security.md) でWCAG 2.1準拠を確認

### 本番デプロイ前のチェックリスト
1. **パフォーマンス**: [05-performance-seo.md](05-performance-seo.md) でCore Web Vitals目標値達成
2. **SEO**: [05-performance-seo.md](05-performance-seo.md) でメタデータ、構造化データ設定
3. **セキュリティ**: [06-accessibility-security.md](06-accessibility-security.md) でXSS、CSRF、CSP対策
4. **テスト**: [07-testing-i18n.md](07-testing-i18n.md) でカバレッジ80%以上達成
5. **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)** で必須チェック項目TOP25を再確認

---

## 💡 AI活用のヒント

### Devinへの指示例
```
以下の規約に厳密に従って、ユーザーダッシュボードコンポーネントを実装してください：
- 規約: /devin-organization-standards/02-architecture-standards/frontend/
- 必須確認: AI-QUICK-REFERENCE.mdのチェック項目TOP25
- 重点: コンポーネント設計（分類、単一責任）、データフェッチング（TanStack Query）、アクセシビリティ（WCAG 2.1）
- パフォーマンス: Core Web Vitals目標値達成（LCP < 2.5s）
```

### AIレビューの活用
```
以下のコンポーネントをFrontend Architecture規約に基づいてレビューしてください：
- 規約: /devin-organization-standards/02-architecture-standards/frontend/
- チェック項目: コンポーネント設計、データフェッチング、パフォーマンス、アクセシビリティ、セキュリティ
- 出力: 違反項目リスト、修正提案、ベストプラクティス適用案
```

---

## 📊 分割効果

| 項目 | 分割前 | 分割後 | 効果 |
|------|--------|--------|------|
| **ファイル数** | 1 | 8 | +700% |
| **最大ファイルサイズ** | 98.8 KB | 17.7 KB | -82% |
| **平均ファイルサイズ** | 98.8 KB | 12.8 KB | -87% |
| **AI読み込み効率** | 中 | 高 | - |
| **検索性** | 低 | 高 | - |

**削減率**: **87%**（98.8 KB → 平均12.8 KB）

---

## 🔗 関連ドキュメント

- **[AI-QUICK-REFERENCE.md](AI-QUICK-REFERENCE.md)**: AI用必須チェック項目TOP25（3分で確認）
- **Java Standards**: `/devin-organization-standards/01-coding-standards/java/`
- **SQL Standards**: `/devin-organization-standards/01-coding-standards/sql/`
- **CSS Standards**: `/devin-organization-standards/01-coding-standards/css/`
- **API Architecture Standards**: `/devin-organization-standards/02-architecture-standards/api/`

---

## 📝 更新履歴

- **2025-11-13**: 初版作成、8ファイルに分割（98.8 KB → 平均12.8 KB、削減率87%）

---

**ナビゲーションのヒント**:
- **トピック別検索**: 各ドキュメントの「主要トピック」セクションを参照
- **段階的学習**: 1→2→3→4→5→6→7→8の順に読むことを推奨
- **問題解決**: 特定の問題が発生した時は、該当ドキュメントの目次から直接ジャンプ
