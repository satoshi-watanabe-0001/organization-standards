# CSS/SCSS コーディング規約 (CSS/SCSS Coding Standards)

**バージョン**: 2.0.0  
**最終更新日**: 2025-11-13  
**対象**: CSS3+, SCSS/Sass, PostCSS  
**適用範囲**: モダンブラウザ・レスポンシブWebアプリケーション

---

## 📋 このディレクトリについて

CSS/SCSS開発における組織標準をまとめたドキュメント群です。元々一つの大きなファイル(138.2 KB)でしたが、AIと人間の両方にとって使いやすいように、トピック別に分割しました。

---

## 📚 ドキュメント一覧

### 基礎編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [01-basics-and-style-rules.md](./01-basics-and-style-rules.md) | 4.3 KB | **基本原則・スタイルルール**<br>• インデント・改行ルール<br>• プロパティ記述順序<br>• セレクタ記述ルール<br>• コメント規約 |
| [02-file-architecture.md](./02-file-architecture.md) | 4.0 KB | **ファイル構成・アーキテクチャ**<br>• ITCSS (Inverted Triangle CSS)<br>• モダンCSS Reset |
| [03-naming-bem.md](./03-naming-bem.md) | 5.6 KB | **命名規則・BEM実装**<br>• BEM (Block Element Modifier)<br>• ステート管理<br>• ユーティリティクラス設計 |

### レイアウト・レスポンシブ編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [04-layout-systems.md](./04-layout-systems.md) | 5.5 KB | **レイアウトシステム**<br>• CSS Grid活用戦略<br>• Flexbox活用パターン<br>• レイアウト判断基準 |
| [05-responsive-design.md](./05-responsive-design.md) | 5.8 KB | **レスポンシブデザイン**<br>• モバイルファースト戦略<br>• Container Queries<br>• Fluid Typography & Spacing |

### SCSS/Sass編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [06a-scss-variables-mixins.md](./06a-scss-variables-mixins.md) | 12.4 KB | **SCSS: 変数・Mixin**<br>• セマンティック変数設計<br>• デザイントークン体系<br>• レスポンシブMixin<br>• コンポーネントMixin |
| [06b-scss-nesting-functions.md](./06b-scss-nesting-functions.md) | 13.4 KB | **SCSS: ネスト・関数**<br>• 適切なネスト深度<br>• 擬似クラス・擬似要素管理<br>• Sass関数実装<br>• 制御構造活用 |

### モダンCSS・パフォーマンス編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [07-modern-css-features.md](./07-modern-css-features.md) | 6.4 KB | **モダンCSS機能**<br>• CSS Custom Properties<br>• CSS Cascade Layers (@layer)<br>• CSS Subgrid |
| [08-performance-optimization.md](./08-performance-optimization.md) | 5.6 KB | **パフォーマンス最適化**<br>• Critical CSS & レンダリング最適化<br>• フォント最適化<br>• Bundle Size & Tree Shaking |

### アクセシビリティ編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [09a-accessibility-focus-keyboard.md](./09a-accessibility-focus-keyboard.md) | 5.6 KB | **アクセシビリティ: フォーカス**<br>• Focus Ring & Visual Indicators<br>• Keyboard Navigation Patterns |
| [09b-accessibility-color-screenreader.md](./09b-accessibility-color-screenreader.md) | 12.6 KB | **アクセシビリティ: カラー・SR**<br>• High Contrast Mode Support<br>• Color-blind Friendly Design<br>• Screen Reader & ARIA Support |

### ツール・自動化編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [10a-tooling-stylelint.md](./10a-tooling-stylelint.md) | 11.0 KB | **ツール: Stylelint**<br>• 完全なStylelint設定<br>• package.jsonスクリプト |
| [10b-tooling-postcss-build.md](./10b-tooling-postcss-build.md) | 16.3 KB | **ツール: PostCSS・ビルド**<br>• PostCSS設定<br>• Tailwind CSS Integration<br>• Vite/Webpack設定 |

### 実践・ドキュメント編

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [11-devin-guidelines-resources.md](./11-devin-guidelines-resources.md) | 16.5 KB | **Devin実行ガイド**<br>• AI実装時の具体的指針<br>• コード生成パターン<br>• トラブルシューティング<br>• 継続的改善プロセス |
| [12-documentation-standards.md](./12-documentation-standards.md) | 15.5 KB | **ドキュメンテーション標準**<br>• CSS/SCSSコメント必須要件<br>• コメント標準形式<br>• Stylelint自動チェック<br>• コードレビューチェックリスト |

### AI活用向けサマリー

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [AI-QUICK-REFERENCE.md](./AI-QUICK-REFERENCE.md) | 作成予定 | **AI向けクイックリファレンス**<br>• 必須チェック項目<br>• よくある間違い<br>• パフォーマンスチェックリスト |

### コメント規約編（2025-11-14追加）✨

| ファイル | サイズ | 内容 |
|---------|--------|------|
| [css-inline-comment-examples.md](./css-inline-comment-examples.md) | 6.5 KB | **CSS/SCSS固有のインラインコメント実装例**<br>• マジックナンバー、!important、z-index<br>• レスポンシブブレークポイント<br>• アニメーション最適化<br>• SCSS Mixin、変数定義 |

**共通原則も参照**: [00-inline-comment-standards.md](../00-inline-comment-standards.md)

---

## 🎯 状況別ガイド

### 新規コンポーネント開発

1. [03-naming-bem.md](./03-naming-bem.md) - BEM命名規則
2. [04-layout-systems.md](./04-layout-systems.md) - レイアウト選択
3. [06a-scss-variables-mixins.md](./06a-scss-variables-mixins.md) - デザイントークン使用

### レスポンシブ対応

1. [05-responsive-design.md](./05-responsive-design.md) - モバイルファースト戦略
2. [06a-scss-variables-mixins.md](./06a-scss-variables-mixins.md) - レスポンシブMixin

### パフォーマンス改善

1. [08-performance-optimization.md](./08-performance-optimization.md) - 最適化手法
2. [07-modern-css-features.md](./07-modern-css-features.md) - CSS Containment

### アクセシビリティ対応

1. [09a-accessibility-focus-keyboard.md](./09a-accessibility-focus-keyboard.md) - フォーカス管理
2. [09b-accessibility-color-screenreader.md](./09b-accessibility-color-screenreader.md) - カラーコントラスト

---

## 💡 ベストプラクティス

### CSS開発の黄金律

1. **BEM命名** - 一貫した命名規則
2. **モバイルファースト** - 最小から拡張
3. **デザイントークン** - 変数で一元管理
4. **セマンティックHTML** - 適切なタグ選択
5. **アクセシビリティ** - すべてのユーザーに配慮

---

## 🔗 関連ドキュメント

- [../README.md](../README.md) - コーディング標準全体
- [../../02-architecture-standards/frontend-architecture.md](../../02-architecture-standards/frontend-architecture.md)

---

## 📝 更新履歴

| 日付 | バージョン | 変更内容 |
|------|-----------|---------|
| 2025-11-13 | 2.0.0 | 大規模リファクタリング: 1ファイル(138.2KB)から15ファイルに分割 |
| 2024-10-09 | 1.0.0 | 初版作成 |

---

**このREADMEについて**: CSS/SCSSコーディング規約ドキュメント群のナビゲーションハブです。
