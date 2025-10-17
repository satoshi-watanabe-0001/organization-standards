# CSS/SCSS コーディング規約
## CSS/SCSS Coding Standards

**最終更新日**: 2024-10-09  
**バージョン**: 1.0.0  
**対象**: CSS3+, SCSS/Sass, PostCSS  
**適用範囲**: モダンブラウザ（ES6+対応）・レスポンシブWebアプリケーション

## 目的

このドキュメントは、CSS/SCSSを使用したスタイリング実装における具体的なコーディング規約を定義し、保守性・パフォーマンス・アクセシビリティを兼ね備えたスケーラブルなスタイルシートの作成を実現します。共通原則については`00-general-principles.md`を参照してください。

---

## 1. 基本原則・スタイルルール

### 1.1 基本フォーマット

#### **インデント・改行ルール**
```css
/* ✅ Good: 一貫したフォーマット */
.card {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.5rem;
  background-color: var(--color-surface);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
}

.card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.card__title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  line-height: var(--line-height-tight);
}

/* ❌ Bad: 不一貫なフォーマット */
.card{display:flex;flex-direction:column;gap:1rem;padding:1.5rem;}
.card__header{
display:flex;
align-items:center;
    justify-content: space-between;
margin-bottom:1rem;}
```

#### **プロパティ記述順序**
```css
/* ✅ Good: 論理的な順序での記述 */
.component {
  /* Position & Display */
  position: relative;
  display: flex;
  flex-direction: column;
  
  /* Box Model */
  width: 100%;
  max-width: 600px;
  height: auto;
  min-height: 200px;
  padding: 1rem 1.5rem;
  margin: 0 auto 2rem;
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius-md);
  
  /* Typography */
  font-family: var(--font-family-base);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-normal);
  line-height: var(--line-height-base);
  text-align: left;
  
  /* Visual */
  color: var(--color-text-primary);
  background-color: var(--color-surface);
  box-shadow: var(--shadow-sm);
  opacity: 1;
  
  /* Animation */
  transition: all 0.2s ease-in-out;
  transform: translateZ(0);
  
  /* Misc */
  cursor: pointer;
  user-select: none;
  z-index: 1;
}
```

#### **セレクタ記述ルール**
```css
/* ✅ Good: 明確で具体的なセレクタ */
/* 単一セレクタ */
.btn { }

/* 複数セレクタ（各行に分離） */
.btn,
.btn--primary,
.btn--secondary {
  /* 共通スタイル */
}

/* 擬似要素・擬似クラス */
.btn:hover,
.btn:focus-visible {
  /* インタラクション状態 */
}

.btn::before {
  /* 擬似要素 */
}

/* ❌ Bad: 複雑で保守困難なセレクタ */
div.container > ul li:nth-child(2n+1) a:not(.external):hover { }
#header .nav ul li a { }  /* IDセレクタ使用 */
.btn.btn--large.btn--primary { }  /* 過度な連結 */
```

### 1.2 コメント規約

```css
/* ✅ Good: 構造的で情報価値の高いコメント */

/**
 * Card Component
 * 
 * カード形式のコンテンツ表示用コンポーネント
 * - レスポンシブ対応（mobile-first）
 * - アクセシビリティ準拠
 * - テーマシステム対応
 * 
 * Usage:
 * <div class="card">
 *   <div class="card__header">...</div>
 *   <div class="card__body">...</div>
 * </div>
 */
.card {
  /* Layout foundation */
  display: flex;
  flex-direction: column;
  
  /* Spacing system integration */
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  
  /* Design token integration */
  background-color: var(--color-surface);
  border: var(--border-width-sm) solid var(--color-border);
  border-radius: var(--border-radius-md);
  
  /* Performance optimization */
  contain: layout style;
  will-change: transform;
}

/* State variations */
.card--elevated {
  /* Enhanced visual hierarchy */
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

/* Responsive behavior */
@media (min-width: 768px) {
  .card {
    /* Desktop enhancements */
    padding: var(--spacing-xl);
  }
}

/* ❌ Bad: 不適切で価値のないコメント */
.card {
  display: flex; /* flexを使用 */
  color: red; /* 赤色 */
  /* TODO: 後で修正 */
}
```

## 2. ファイル構成・アーキテクチャ

### 2.1 ITCSS（Inverted Triangle CSS）アーキテクチャ

```scss
// ✅ Good: スケーラブルなファイル構成
styles/
├── 01-settings/           // 変数・設定値
│   ├── _colors.scss
│   ├── _typography.scss
│   ├── _spacing.scss
│   └── _breakpoints.scss
├── 02-tools/             // Mixins・Functions
│   ├── _mixins.scss
│   ├── _functions.scss
│   └── _placeholders.scss
├── 03-generic/           // Reset・Normalize
│   ├── _reset.scss
│   └── _normalize.scss
├── 04-elements/          // HTML要素
│   ├── _typography.scss
│   ├── _forms.scss
│   └── _media.scss
├── 05-objects/           // レイアウトパターン
│   ├── _container.scss
│   ├── _grid.scss
│   └── _media.scss
├── 06-components/        // UIコンポーネント
│   ├── _button.scss
│   ├── _card.scss
│   ├── _navigation.scss
│   └── _modal.scss
├── 07-utilities/         // ユーティリティクラス
│   ├── _spacing.scss
│   ├── _display.scss
│   └── _text.scss
└── main.scss            // エントリーポイント
```

#### **メインエントリーファイル（main.scss）**
```scss
// ✅ Good: 論理的なインポート順序
@charset "UTF-8";

// 1. Settings - Variables, configs
@use "01-settings/colors" as *;
@use "01-settings/typography" as *;
@use "01-settings/spacing" as *;
@use "01-settings/breakpoints" as *;

// 2. Tools - Mixins, functions
@use "02-tools/mixins" as *;
@use "02-tools/functions" as *;

// 3. Generic - Reset, normalize
@use "03-generic/reset";
@use "03-generic/normalize";

// 4. Elements - HTML elements
@use "04-elements/typography";
@use "04-elements/forms";
@use "04-elements/media";

// 5. Objects - Layout patterns
@use "05-objects/container";
@use "05-objects/grid";
@use "05-objects/media";

// 6. Components - UI components
@use "06-components/button";
@use "06-components/card";
@use "06-components/navigation";
@use "06-components/modal";

// 7. Utilities - Helper classes
@use "07-utilities/spacing";
@use "07-utilities/display";
@use "07-utilities/text";
```

### 2.2 モダンCSS Reset

```css
/* ✅ Good: 2024年対応のモダンCSS Reset */

/*
 * Modern CSS Reset
 * Based on modern-css-reset by Andy Bell
 * Updated for 2024 standards
 */

/* Box sizing normalization */
*,
*::before,
*::after {
  box-sizing: border-box;
}

/* Remove default margin and padding */
* {
  margin: 0;
  padding: 0;
}

/* Improve media defaults */
img,
picture,
video,
canvas,
svg {
  display: block;
  max-width: 100%;
  height: auto;
}

/* Improve form defaults */
input,
button,
textarea,
select {
  font: inherit;
  color: inherit;
}

/* Remove button styling */
button {
  border: none;
  background-color: transparent;
  cursor: pointer;
}

/* Improve table defaults */
table {
  border-collapse: collapse;
  border-spacing: 0;
}

/* Remove list styling where it's not needed */
ul[role="list"],
ol[role="list"] {
  list-style: none;
}

/* Improve text rendering */
html {
  font-feature-settings: "kern" 1, "liga" 1, "calt" 1;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Root-level improvements */
html,
body {
  height: 100%;
}

body {
  line-height: 1.6;
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
}

/* Improve focus visibility */
:focus-visible {
  outline: 2px solid var(--color-focus, #005fcc);
  outline-offset: 2px;
}

/* Respect user preferences */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
    scroll-behavior: auto !important;
  }
}
```

## 3. 命名規則・BEM実装

### 3.1 BEM（Block Element Modifier）標準実装

```css
/* ✅ Good: 正確なBEM実装 */

/* Block - 独立したコンポーネント */
.card { }
.button { }
.navigation { }

/* Element - ブロックの構成要素 */
.card__header { }
.card__body { }
.card__footer { }
.button__icon { }
.button__text { }

/* Modifier - バリエーション・状態 */
.card--elevated { }
.card--compact { }
.button--primary { }
.button--large { }
.button--loading { }

/* 複合例 */
.card {
  display: flex;
  flex-direction: column;
  padding: var(--spacing-md);
  background-color: var(--color-surface);
  border-radius: var(--border-radius-md);
}

.card--elevated {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.card--compact {
  padding: var(--spacing-sm);
}

.card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
}

.card__title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.card__subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.card__body {
  flex: 1;
  color: var(--color-text-primary);
  line-height: var(--line-height-relaxed);
}

.card__footer {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--color-border-light);
}

/* ❌ Bad: BEM違反の例 */
.cardHeader { }  /* キャメルケース */
.card-header { }  /* ケバブケースの混在 */
.card__header__title { }  /* 多層element */
.card__header--large__title { }  /* element + modifier の複合 */
```

### 3.2 ステート管理

```css
/* ✅ Good: 状態管理の明確な分離 */

/* JavaScript状態クラス（is-*, has-*プレフィックス） */
.is-active { }
.is-loading { }
.is-disabled { }
.is-hidden { }
.has-error { }
.has-success { }

/* ARIA状態属性の活用 */
.button[aria-pressed="true"] {
  background-color: var(--color-primary-active);
  color: var(--color-primary-contrast);
}

.button[aria-disabled="true"] {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

.modal[aria-hidden="true"] {
  opacity: 0;
  visibility: hidden;
  transform: scale(0.95);
}

.modal[aria-hidden="false"] {
  opacity: 1;
  visibility: visible;
  transform: scale(1);
}

/* 複合状態の管理 */
.card {
  transition: all 0.2s ease-in-out;
}

.card.is-loading {
  opacity: 0.7;
  pointer-events: none;
  
  &::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 20px;
    height: 20px;
    margin: -10px 0 0 -10px;
    border: 2px solid var(--color-primary);
    border-radius: 50%;
    border-top-color: transparent;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
```

### 3.3 ユーティリティクラス設計

```css
/* ✅ Good: 一貫性のあるユーティリティクラス */

/* Spacing utilities - Design token based */
.u-margin-none { margin: 0 !important; }
.u-margin-xs { margin: var(--spacing-xs) !important; }
.u-margin-sm { margin: var(--spacing-sm) !important; }
.u-margin-md { margin: var(--spacing-md) !important; }
.u-margin-lg { margin: var(--spacing-lg) !important; }
.u-margin-xl { margin: var(--spacing-xl) !important; }

/* Directional spacing */
.u-margin-top-md { margin-top: var(--spacing-md) !important; }
.u-margin-bottom-lg { margin-bottom: var(--spacing-lg) !important; }
.u-margin-x-sm { margin-left: var(--spacing-sm) !important; margin-right: var(--spacing-sm) !important; }
.u-margin-y-md { margin-top: var(--spacing-md) !important; margin-bottom: var(--spacing-md) !important; }

/* Display utilities */
.u-display-block { display: block !important; }
.u-display-inline { display: inline !important; }
.u-display-inline-block { display: inline-block !important; }
.u-display-flex { display: flex !important; }
.u-display-grid { display: grid !important; }
.u-display-none { display: none !important; }

/* Responsive utilities */
@media (min-width: 768px) {
  .u-md-display-block { display: block !important; }
  .u-md-display-flex { display: flex !important; }
  .u-md-display-none { display: none !important; }
}

/* Text utilities */
.u-text-left { text-align: left !important; }
.u-text-center { text-align: center !important; }
.u-text-right { text-align: right !important; }

.u-text-xs { font-size: var(--font-size-xs) !important; }
.u-text-sm { font-size: var(--font-size-sm) !important; }
.u-text-base { font-size: var(--font-size-base) !important; }
.u-text-lg { font-size: var(--font-size-lg) !important; }
.u-text-xl { font-size: var(--font-size-xl) !important; }

.u-font-normal { font-weight: var(--font-weight-normal) !important; }
.u-font-medium { font-weight: var(--font-weight-medium) !important; }
.u-font-semibold { font-weight: var(--font-weight-semibold) !important; }
.u-font-bold { font-weight: var(--font-weight-bold) !important; }

/* Color utilities */
.u-text-primary { color: var(--color-text-primary) !important; }
.u-text-secondary { color: var(--color-text-secondary) !important; }
.u-text-success { color: var(--color-success) !important; }
.u-text-warning { color: var(--color-warning) !important; }
.u-text-error { color: var(--color-error) !important; }

.u-bg-primary { background-color: var(--color-primary) !important; }
.u-bg-surface { background-color: var(--color-surface) !important; }
.u-bg-success { background-color: var(--color-success-light) !important; }
```

## 4. レイアウトシステム（Grid/Flexbox）

### 4.1 CSS Grid活用戦略

```css
/* ✅ Good: モダンCSS Gridシステム */

/* Grid container foundation */
.grid {
  display: grid;
  gap: var(--spacing-md);
  width: 100%;
}

/* Responsive grid patterns */
.grid--auto-fit {
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
}

.grid--auto-fill {
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}

/* Named grid areas */
.layout-main {
  display: grid;
  min-height: 100vh;
  grid-template-areas:
    "header"
    "main"
    "sidebar"
    "footer";
  grid-template-rows: auto 1fr auto auto;
  gap: var(--spacing-lg);
}

@media (min-width: 768px) {
  .layout-main {
    grid-template-areas:
      "header header"
      "main sidebar"
      "footer footer";
    grid-template-columns: 1fr 300px;
    grid-template-rows: auto 1fr auto;
  }
}

@media (min-width: 1024px) {
  .layout-main {
    grid-template-areas:
      "header header header"
      "sidebar main aside"
      "footer footer footer";
    grid-template-columns: 250px 1fr 200px;
  }
}

.layout-header { grid-area: header; }
.layout-main-content { grid-area: main; }
.layout-sidebar { grid-area: sidebar; }
.layout-footer { grid-area: footer; }

/* Advanced grid patterns */
.grid--masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  grid-template-rows: masonry; /* When supported */
  gap: var(--spacing-md);
}

/* Fallback for masonry (when not supported) */
@supports not (grid-template-rows: masonry) {
  .grid--masonry {
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-md);
  }
  
  .grid--masonry > * {
    flex: 1 1 250px;
  }
}

/* Container Queries integration */
.card-grid {
  container-type: inline-size;
  display: grid;
  gap: var(--spacing-md);
  grid-template-columns: 1fr;
}

@container (min-width: 400px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@container (min-width: 600px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
```

### 4.2 Flexbox活用パターン

```css
/* ✅ Good: Flexboxの適切な使い分け */

/* Basic flex patterns */
.flex {
  display: flex;
}

.flex--column {
  flex-direction: column;
}

.flex--wrap {
  flex-wrap: wrap;
}

.flex--center {
  align-items: center;
  justify-content: center;
}

.flex--between {
  justify-content: space-between;
}

.flex--around {
  justify-content: space-around;
}

.flex--gap {
  gap: var(--spacing-md);
}

/* Component-specific flex usage */
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.navbar__brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  text-decoration: none;
}

.navbar__nav {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  list-style: none;
}

.navbar__actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* Card layout with flexbox */
.card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card__body {
  flex: 1; /* Grow to fill available space */
  padding: var(--spacing-md);
}

.card__actions {
  display: flex;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  margin-top: auto; /* Push to bottom */
  border-top: 1px solid var(--color-border-light);
}

/* Media object pattern */
.media {
  display: flex;
  gap: var(--spacing-md);
}

.media__figure {
  flex-shrink: 0;
}

.media__body {
  flex: 1;
  min-width: 0; /* Prevent text overflow */
}

/* Responsive flex behavior */
.flex-stack {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

@media (min-width: 768px) {
  .flex-stack {
    flex-direction: row;
    align-items: center;
  }
}
```

### 4.3 レイアウト判断基準

```css
/* ✅ Good: 適切なレイアウト技術の選択 */

/*
 * Grid vs Flexbox 使い分けガイドライン：
 * 
 * CSS Grid を使用する場合：
 * - 2次元レイアウト（行と列の両方）
 * - 複雑なレイアウトパターン
 * - グリッドベースのデザイン
 * - レスポンシブカードグリッド
 * 
 * Flexbox を使用する場合：
 * - 1次元レイアウト（行または列）
 * - コンポーネント内部のレイアウト
 * - 中央揃え・配置調整
 * - ナビゲーションバー
 */

/* 例1: カードグリッド（Grid が適している） */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

/* 例2: カード内部レイアウト（Flexbox が適している） */
.product-card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-card__content {
  flex: 1;
}

.product-card__actions {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: auto;
}

/* 例3: 複合レイアウト */
.dashboard {
  display: grid;
  grid-template-areas:
    "sidebar main"
    "sidebar footer";
  grid-template-columns: 250px 1fr;
  grid-template-rows: 1fr auto;
  gap: var(--spacing-lg);
  min-height: 100vh;
}

.sidebar {
  grid-area: sidebar;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.main-content {
  grid-area: main;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}
```

## 5. レスポンシブデザイン

### 5.1 モバイルファースト戦略

```css
/* ✅ Good: モバイルファースト実装 */

/* Base styles (mobile-first) */
.hero {
  padding: var(--spacing-lg);
  text-align: center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: var(--color-primary-contrast);
}

.hero__title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  line-height: var(--line-height-tight);
  margin-bottom: var(--spacing-md);
}

.hero__subtitle {
  font-size: var(--font-size-lg);
  opacity: 0.9;
  margin-bottom: var(--spacing-lg);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero__actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  align-items: center;
}

/* Tablet breakpoint (768px+) */
@media (min-width: 768px) {
  .hero {
    padding: var(--spacing-2xl) var(--spacing-lg);
  }
  
  .hero__title {
    font-size: var(--font-size-4xl);
  }
  
  .hero__subtitle {
    font-size: var(--font-size-xl);
  }
  
  .hero__actions {
    flex-direction: row;
    justify-content: center;
  }
}

/* Desktop breakpoint (1024px+) */
@media (min-width: 1024px) {
  .hero {
    padding: var(--spacing-3xl) var(--spacing-lg);
    text-align: left;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-2xl);
    align-items: center;
  }
  
  .hero__content {
    max-width: none;
  }
  
  .hero__title {
    font-size: var(--font-size-5xl);
  }
  
  .hero__actions {
    justify-content: flex-start;
  }
}

/* Large desktop breakpoint (1440px+) */
@media (min-width: 1440px) {
  .hero {
    padding: var(--spacing-4xl) var(--spacing-lg);
  }
  
  .hero__title {
    font-size: var(--font-size-6xl);
  }
}
```

### 5.2 Container Queries活用

```css
/* ✅ Good: Container Queriesによるコンポーネントベース設計 */

.card {
  container-type: inline-size;
  container-name: card;
  
  display: flex;
  flex-direction: column;
  padding: var(--spacing-md);
  background-color: var(--color-surface);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
}

.card__header {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-md);
}

.card__title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  line-height: var(--line-height-tight);
}

.card__meta {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* Container query: カードが300px以上の場合 */
@container card (min-width: 300px) {
  .card {
    padding: var(--spacing-lg);
  }
  
  .card__header {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
  
  .card__title {
    font-size: var(--font-size-xl);
  }
}

/* Container query: カードが500px以上の場合 */
@container card (min-width: 500px) {
  .card {
    flex-direction: row;
    gap: var(--spacing-lg);
  }
  
  .card__header {
    flex-direction: column;
    align-items: flex-start;
    min-width: 200px;
  }
  
  .card__body {
    flex: 1;
  }
}

/* 複数のコンテナクエリの組み合わせ */
.product-card {
  container-type: inline-size;
  container-name: product;
}

@container product (min-width: 250px) {
  .product-card__image {
    aspect-ratio: 16 / 9;
  }
  
  .product-card__title {
    font-size: var(--font-size-lg);
  }
}

@container product (min-width: 350px) {
  .product-card {
    display: grid;
    grid-template-columns: 120px 1fr;
    gap: var(--spacing-md);
  }
  
  .product-card__image {
    aspect-ratio: 1;
  }
}
```

### 5.3 Fluid Typography & Spacing

```css
/* ✅ Good: 流動的なタイポグラフィとスペーシング */

:root {
  /* Fluid typography using clamp() */
  --font-size-xs: clamp(0.75rem, 0.7rem + 0.25vw, 0.875rem);
  --font-size-sm: clamp(0.875rem, 0.8rem + 0.375vw, 1rem);
  --font-size-base: clamp(1rem, 0.9rem + 0.5vw, 1.125rem);
  --font-size-lg: clamp(1.125rem, 1rem + 0.625vw, 1.25rem);
  --font-size-xl: clamp(1.25rem, 1.1rem + 0.75vw, 1.5rem);
  --font-size-2xl: clamp(1.5rem, 1.3rem + 1vw, 1.875rem);
  --font-size-3xl: clamp(1.875rem, 1.6rem + 1.375vw, 2.25rem);
  --font-size-4xl: clamp(2.25rem, 1.9rem + 1.75vw, 3rem);
  --font-size-5xl: clamp(3rem, 2.5rem + 2.5vw, 4rem);
  --font-size-6xl: clamp(4rem, 3rem + 5vw, 6rem);
  
  /* Fluid spacing using clamp() */
  --spacing-xs: clamp(0.25rem, 0.2rem + 0.25vw, 0.375rem);
  --spacing-sm: clamp(0.5rem, 0.4rem + 0.5vw, 0.75rem);
  --spacing-md: clamp(1rem, 0.8rem + 1vw, 1.5rem);
  --spacing-lg: clamp(1.5rem, 1.2rem + 1.5vw, 2.25rem);
  --spacing-xl: clamp(2rem, 1.6rem + 2vw, 3rem);
  --spacing-2xl: clamp(3rem, 2.4rem + 3vw, 4.5rem);
  --spacing-3xl: clamp(4rem, 3.2rem + 4vw, 6rem);
  --spacing-4xl: clamp(6rem, 4.8rem + 6vw, 9rem);
  
  /* Fluid line heights */
  --line-height-tight: clamp(1.1, 1.05 + 0.25vw, 1.25);
  --line-height-base: clamp(1.4, 1.3 + 0.5vw, 1.6);
  --line-height-relaxed: clamp(1.6, 1.5 + 0.5vw, 1.8);
}

/* Responsive container with fluid spacing */
.container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding-left: var(--spacing-md);
  padding-right: var(--spacing-md);
}

/* Fluid grid gaps */
.grid {
  display: grid;
  gap: var(--spacing-md);
  grid-template-columns: repeat(auto-fit, minmax(min(300px, 100%), 1fr));
}

/* Intrinsic design patterns */
.stack {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.cluster {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  align-items: center;
}

.switcher {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.switcher > * {
  flex-grow: 1;
  flex-basis: calc((30rem - 100%) * 999);
}
```

## 6. SCSS/Sass規約

### 6.1 変数・デザイントークン体系

#### **セマンティック変数設計**
```scss
/* ✅ Good: 階層的で保守性の高いSCSS変数設計 */

// =================================
// Color System - Base Palette
// =================================

// Primary colors
$color-primary-50: #f0f9ff;
$color-primary-100: #e0f2fe;
$color-primary-200: #bae6fd;
$color-primary-300: #7dd3fc;
$color-primary-400: #38bdf8;
$color-primary-500: #0ea5e9;  // Base primary
$color-primary-600: #0284c7;
$color-primary-700: #0369a1;
$color-primary-800: #075985;
$color-primary-900: #0c4a6e;

// Neutral colors
$color-neutral-50: #fafafa;
$color-neutral-100: #f5f5f5;
$color-neutral-200: #e5e5e5;
$color-neutral-300: #d4d4d4;
$color-neutral-400: #a3a3a3;
$color-neutral-500: #737373;
$color-neutral-600: #525252;
$color-neutral-700: #404040;
$color-neutral-800: #262626;
$color-neutral-900: #171717;

// Semantic color tokens
$color-success: #10b981;
$color-warning: #f59e0b;
$color-error: #ef4444;
$color-info: #3b82f6;

// =================================
// Semantic Token Mapping
// =================================

// Theme-aware semantic tokens
$colors: (
  // Text colors
  text-primary: $color-neutral-900,
  text-secondary: $color-neutral-600,
  text-tertiary: $color-neutral-500,
  text-inverse: $color-neutral-50,
  text-link: $color-primary-600,
  text-link-hover: $color-primary-700,
  
  // Surface colors
  surface-primary: #ffffff,
  surface-secondary: $color-neutral-50,
  surface-tertiary: $color-neutral-100,
  surface-overlay: rgba(0, 0, 0, 0.5),
  
  // Border colors
  border-primary: $color-neutral-200,
  border-secondary: $color-neutral-300,
  border-focus: $color-primary-500,
  
  // Status colors
  status-success: $color-success,
  status-warning: $color-warning,
  status-error: $color-error,
  status-info: $color-info,
  status-success-bg: rgba($color-success, 0.1),
  status-warning-bg: rgba($color-warning, 0.1),
  status-error-bg: rgba($color-error, 0.1),
  status-info-bg: rgba($color-info, 0.1)
);

// Typography scale
$font-sizes: (
  xs: 0.75rem,    // 12px
  sm: 0.875rem,   // 14px
  base: 1rem,     // 16px
  lg: 1.125rem,   // 18px
  xl: 1.25rem,    // 20px
  2xl: 1.5rem,    // 24px
  3xl: 1.875rem,  // 30px
  4xl: 2.25rem,   // 36px
  5xl: 3rem,      // 48px
  6xl: 3.75rem,   // 60px
  7xl: 4.5rem,    // 72px
  8xl: 6rem,      // 96px
  9xl: 8rem       // 128px
);

// Font weights
$font-weights: (
  thin: 100,
  extralight: 200,
  light: 300,
  normal: 400,
  medium: 500,
  semibold: 600,
  bold: 700,
  extrabold: 800,
  black: 900
);

// Spacing scale (8px base unit)
$spacing: (
  0: 0,
  px: 1px,
  0-5: 0.125rem,  // 2px
  1: 0.25rem,     // 4px
  1-5: 0.375rem,  // 6px
  2: 0.5rem,      // 8px
  2-5: 0.625rem,  // 10px
  3: 0.75rem,     // 12px
  3-5: 0.875rem,  // 14px
  4: 1rem,        // 16px
  5: 1.25rem,     // 20px
  6: 1.5rem,      // 24px
  7: 1.75rem,     // 28px
  8: 2rem,        // 32px
  9: 2.25rem,     // 36px
  10: 2.5rem,     // 40px
  12: 3rem,       // 48px
  14: 3.5rem,     // 56px
  16: 4rem,       // 64px
  20: 5rem,       // 80px
  24: 6rem,       // 96px
  28: 7rem,       // 112px
  32: 8rem,       // 128px
  36: 9rem,       // 144px
  40: 10rem,      // 160px
  44: 11rem,      // 176px
  48: 12rem,      // 192px
  52: 13rem,      // 208px
  56: 14rem,      // 224px
  60: 15rem,      // 240px
  64: 16rem,      // 256px
  72: 18rem,      // 288px
  80: 20rem,      // 320px
  96: 24rem       // 384px
);

// Border radius scale
$border-radius: (
  none: 0,
  sm: 0.125rem,   // 2px
  base: 0.25rem,  // 4px
  md: 0.375rem,   // 6px
  lg: 0.5rem,     // 8px
  xl: 0.75rem,    // 12px
  2xl: 1rem,      // 16px
  3xl: 1.5rem,    // 24px
  full: 9999px
);

// Shadow system
$shadows: (
  xs: 0 1px 2px 0 rgba(0, 0, 0, 0.05),
  sm: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06),
  base: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06),
  md: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05),
  lg: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04),
  xl: 0 25px 50px -12px rgba(0, 0, 0, 0.25),
  2xl: 0 25px 50px -12px rgba(0, 0, 0, 0.25),
  inner: inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)
);

// Z-index layers
$z-index: (
  hide: -1,
  auto: auto,
  base: 0,
  docked: 10,
  dropdown: 1000,
  sticky: 1100,
  banner: 1200,
  overlay: 1300,
  modal: 1400,
  popover: 1500,
  skipLink: 1600,
  toast: 1700,
  tooltip: 1800
);

/* ❌ Bad: 平坦で保守困難な変数設計 */
$blue: #0ea5e9;
$light-blue: #38bdf8;
$dark-blue: #0369a1;
$red: #ef4444;
$green: #10b981;
$big-font: 2rem;
$small-font: 0.875rem;
$padding-big: 2rem;
$padding-small: 0.5rem;
```

#### **変数アクセス関数**
```scss
/* ✅ Good: 型安全で保守性の高いアクセス関数 */

// Color accessor function
@function color($name) {
  @if not map-has-key($colors, $name) {
    @error "Color '#{$name}' is not defined in $colors map. Available colors: #{map-keys($colors)}";
  }
  @return map-get($colors, $name);
}

// Font size accessor function
@function font-size($size) {
  @if not map-has-key($font-sizes, $size) {
    @error "Font size '#{$size}' is not defined in $font-sizes map. Available sizes: #{map-keys($font-sizes)}";
  }
  @return map-get($font-sizes, $size);
}

// Spacing accessor function
@function spacing($size) {
  @if not map-has-key($spacing, $size) {
    @error "Spacing '#{$size}' is not defined in $spacing map. Available values: #{map-keys($spacing)}";
  }
  @return map-get($spacing, $size);
}

// Border radius accessor function
@function radius($size) {
  @if not map-has-key($border-radius, $size) {
    @error "Border radius '#{$size}' is not defined in $border-radius map. Available values: #{map-keys($border-radius)}";
  }
  @return map-get($border-radius, $size);
}

// Shadow accessor function
@function shadow($size) {
  @if not map-has-key($shadows, $size) {
    @error "Shadow '#{$size}' is not defined in $shadows map. Available values: #{map-keys($shadows)}";
  }
  @return map-get($shadows, $size);
}

// Z-index accessor function
@function z($layer) {
  @if not map-has-key($z-index, $layer) {
    @error "Z-index layer '#{$layer}' is not defined in $z-index map. Available layers: #{map-keys($z-index)}";
  }
  @return map-get($z-index, $layer);
}

// 使用例
.button {
  padding: spacing(3) spacing(6);
  font-size: font-size(base);
  color: color(text-inverse);
  background-color: color(text-primary);
  border-radius: radius(md);
  box-shadow: shadow(sm);
  z-index: z(base);
  
  &:hover {
    box-shadow: shadow(md);
  }
}

.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: spacing(8);
  background-color: color(surface-primary);
  border-radius: radius(lg);
  box-shadow: shadow(xl);
  z-index: z(modal);
}
```

### 6.2 Mixin設計パターン

#### **レスポンシブMixin**
```scss
/* ✅ Good: 保守性の高いレスポンシブMixin */

// Breakpoint map
$breakpoints: (
  xs: 0,
  sm: 576px,
  md: 768px,
  lg: 992px,
  xl: 1200px,
  2xl: 1400px
);

// Media query mixin
@mixin media($breakpoint, $direction: 'up') {
  @if not map-has-key($breakpoints, $breakpoint) {
    @error "Breakpoint '#{$breakpoint}' is not defined. Available: #{map-keys($breakpoints)}";
  }
  
  $value: map-get($breakpoints, $breakpoint);
  
  @if $direction == 'up' {
    @if $value == 0 {
      @content;
    } @else {
      @media (min-width: $value) {
        @content;
      }
    }
  } @else if $direction == 'down' {
    @if $value == 0 {
      @error "Cannot use 'down' direction with breakpoint '#{$breakpoint}' (value: 0)";
    } @else {
      @media (max-width: #{$value - 1px}) {
        @content;
      }
    }
  } @else if $direction == 'only' {
    $next-breakpoint: null;
    $current-index: index(map-keys($breakpoints), $breakpoint);
    $next-index: $current-index + 1;
    
    @if $next-index <= length($breakpoints) {
      $next-key: nth(map-keys($breakpoints), $next-index);
      $next-breakpoint: map-get($breakpoints, $next-key);
    }
    
    @if $value == 0 and $next-breakpoint {
      @media (max-width: #{$next-breakpoint - 1px}) {
        @content;
      }
    } @else if $next-breakpoint {
      @media (min-width: $value) and (max-width: #{$next-breakpoint - 1px}) {
        @content;
      }
    } @else {
      @media (min-width: $value) {
        @content;
      }
    }
  } @else {
    @error "Invalid direction '#{$direction}'. Use 'up', 'down', or 'only'";
  }
}

// 使用例
.hero {
  padding: spacing(4);
  text-align: center;
  
  @include media(md) {
    padding: spacing(8);
    text-align: left;
  }
  
  @include media(lg) {
    padding: spacing(12);
  }
}

.sidebar {
  display: none;
  
  @include media(lg) {
    display: block;
  }
  
  @include media(xl, 'down') {
    width: 250px;
  }
}

.mobile-only {
  display: block;
  
  @include media(md) {
    display: none;
  }
}
```

#### **コンポーネントMixin**
```scss
/* ✅ Good: 再利用可能なコンポーネントMixin */

// Button variants mixin
@mixin button-variant($bg-color, $text-color: null, $border-color: null) {
  $calculated-text-color: $text-color or color-contrast($bg-color);
  $calculated-border-color: $border-color or $bg-color;
  
  background-color: $bg-color;
  color: $calculated-text-color;
  border: 1px solid $calculated-border-color;
  
  &:hover:not(:disabled) {
    background-color: darken($bg-color, 8%);
    border-color: darken($calculated-border-color, 8%);
  }
  
  &:active:not(:disabled) {
    background-color: darken($bg-color, 12%);
    border-color: darken($calculated-border-color, 12%);
    transform: translateY(1px);
  }
  
  &:focus-visible {
    outline: 2px solid transparentize($bg-color, 0.5);
    outline-offset: 2px;
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }
}

// Button size mixin
@mixin button-size($padding-y, $padding-x, $font-size, $border-radius: md) {
  padding: spacing($padding-y) spacing($padding-x);
  font-size: font-size($font-size);
  border-radius: radius($border-radius);
  
  // Icon spacing adjustment
  .icon {
    margin-right: spacing(2);
    
    &:only-child {
      margin-right: 0;
    }
  }
}

// Card elevation mixin
@mixin card-elevation($level: 1) {
  @if $level == 0 {
    box-shadow: none;
  } @else if $level == 1 {
    box-shadow: shadow(sm);
  } @else if $level == 2 {
    box-shadow: shadow(base);
  } @else if $level == 3 {
    box-shadow: shadow(md);
  } @else if $level == 4 {
    box-shadow: shadow(lg);
  } @else if $level == 5 {
    box-shadow: shadow(xl);
  } @else {
    @error "Invalid elevation level '#{$level}'. Use 0-5";
  }
  
  &:hover {
    @if $level < 5 {
      @include card-elevation($level + 1);
    }
  }
}

// Text truncation mixin
@mixin text-truncate($lines: 1) {
  @if $lines == 1 {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  } @else {
    display: -webkit-box;
    -webkit-line-clamp: $lines;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

// Visually hidden mixin (for accessibility)
@mixin visually-hidden {
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0, 0, 0, 0) !important;
  white-space: nowrap !important;
  border: 0 !important;
}

// 使用例
.btn {
  @include button-size(2, 4, base);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: map-get($font-weights, medium);
  line-height: 1.5;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  
  &--primary {
    @include button-variant(color(text-primary), color(text-inverse));
  }
  
  &--secondary {
    @include button-variant(color(surface-secondary), color(text-primary));
  }
  
  &--success {
    @include button-variant(color(status-success), #ffffff);
  }
  
  &--small {
    @include button-size(1, 3, sm);
  }
  
  &--large {
    @include button-size(3, 6, lg);
  }
}

.card {
  @include card-elevation(1);
  background-color: color(surface-primary);
  border-radius: radius(lg);
  padding: spacing(6);
  
  &--elevated {
    @include card-elevation(3);
  }
}

.text-clamp {
  &--1 {
    @include text-truncate(1);
  }
  
  &--2 {
    @include text-truncate(2);
  }
  
  &--3 {
    @include text-truncate(3);
  }
}

.sr-only {
  @include visually-hidden;
}
```

### 6.3 ネスト規則・セレクタ戦略

#### **適切なネスト深度**
```scss
/* ✅ Good: 3階層以内の適切なネスト */

.card {
  display: flex;
  flex-direction: column;
  padding: spacing(6);
  background-color: color(surface-primary);
  border-radius: radius(lg);
  box-shadow: shadow(sm);
  
  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: spacing(4);
    
    .title {
      font-size: font-size(xl);
      font-weight: map-get($font-weights, semibold);
      color: color(text-primary);
    }
    
    .actions {
      display: flex;
      gap: spacing(2);
    }
  }
  
  &__body {
    flex: 1;
    color: color(text-secondary);
    line-height: 1.6;
    
    p:not(:last-child) {
      margin-bottom: spacing(4);
    }
  }
  
  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: spacing(6);
    padding-top: spacing(4);
    border-top: 1px solid color(border-primary);
  }
  
  // State modifiers
  &--loading {
    opacity: 0.7;
    pointer-events: none;
    
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 20px;
      height: 20px;
      margin: -10px 0 0 -10px;
      border: 2px solid color(text-primary);
      border-radius: 50%;
      border-top-color: transparent;
      animation: spin 1s linear infinite;
    }
  }
  
  &--interactive {
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      box-shadow: shadow(md);
      transform: translateY(-2px);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
  
  // Responsive behavior
  @include media(md) {
    padding: spacing(8);
    
    &__header {
      margin-bottom: spacing(6);
      
      .title {
        font-size: font-size(2xl);
      }
    }
  }
}

/* ❌ Bad: 過度に深いネストと複雑なセレクタ */
.card {
  .header {
    .title {
      .text {
        .main {
          .content {
            font-size: 1.5rem; // 6階層のネスト
          }
        }
      }
    }
  }
  
  // 複雑すぎるセレクタ
  .content .section:nth-child(odd) .item:not(.disabled):hover .link {
    color: red;
  }
}
```

#### **擬似クラス・擬似要素の管理**
```scss
/* ✅ Good: 体系的な擬似クラス・擬似要素の使用 */

.button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: spacing(3) spacing(6);
  background-color: color(text-primary);
  color: color(text-inverse);
  border: none;
  border-radius: radius(md);
  font-size: font-size(base);
  font-weight: map-get($font-weights, medium);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
  
  // Pseudo-classes (interaction states)
  &:hover {
    background-color: lighten(color(text-primary), 10%);
    transform: translateY(-1px);
  }
  
  &:focus {
    outline: none;
  }
  
  &:focus-visible {
    outline: 2px solid transparentize(color(text-primary), 0.5);
    outline-offset: 2px;
  }
  
  &:active {
    transform: translateY(0);
    background-color: darken(color(text-primary), 5%);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
    
    &:hover {
      background-color: color(text-primary);
      transform: none;
    }
  }
  
  // Pseudo-elements (visual enhancements)
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.1) 50%, transparent 70%);
    border-radius: inherit;
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  
  &:hover::before {
    opacity: 1;
  }
  
  // Icon integration
  .icon {
    margin-right: spacing(2);
    
    &:last-child:not(:first-child) {
      margin-right: 0;
      margin-left: spacing(2);
    }
    
    &:only-child {
      margin: 0;
    }
  }
  
  // Loading state with pseudo-element
  &--loading {
    color: transparent;
    
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 16px;
      height: 16px;
      margin: -8px 0 0 -8px;
      border: 2px solid currentColor;
      border-radius: 50%;
      border-top-color: transparent;
      animation: spin 0.8s linear infinite;
    }
  }
}

// Animations
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// Form element styling
.form-input {
  display: block;
  width: 100%;
  padding: spacing(3) spacing(4);
  font-size: font-size(base);
  line-height: 1.5;
  color: color(text-primary);
  background-color: color(surface-primary);
  border: 1px solid color(border-primary);
  border-radius: radius(md);
  transition: all 0.2s ease;
  
  &::placeholder {
    color: color(text-tertiary);
    opacity: 1;
  }
  
  &:hover {
    border-color: color(border-secondary);
  }
  
  &:focus {
    outline: none;
    border-color: color(border-focus);
    box-shadow: 0 0 0 3px transparentize(color(border-focus), 0.8);
  }
  
  &:invalid {
    border-color: color(status-error);
    
    &:focus {
      box-shadow: 0 0 0 3px transparentize(color(status-error), 0.8);
    }
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background-color: color(surface-secondary);
  }
}
```

### 6.4 関数・演算・制御構造

#### **Sass関数の実装**
```scss
/* ✅ Good: 実用的なSass関数の実装 */

// Color manipulation functions
@function color-contrast($background-color, $light-color: #ffffff, $dark-color: #000000) {
  $lightness: lightness($background-color);
  
  @if $lightness > 50% {
    @return $dark-color;
  } @else {
    @return $light-color;
  }
}

@function alpha-blend($foreground, $background, $alpha) {
  @if not (type-of($alpha) == number and $alpha >= 0 and $alpha <= 1) {
    @error "Alpha value must be a number between 0 and 1. Received: #{$alpha}";
  }
  
  $red: red($foreground) * $alpha + red($background) * (1 - $alpha);
  $green: green($foreground) * $alpha + green($background) * (1 - $alpha);
  $blue: blue($foreground) * $alpha + blue($background) * (1 - $alpha);
  
  @return rgb($red, $green, $blue);
}

// Unit conversion functions
@function rem($pixels, $base-font-size: 16px) {
  @if unitless($pixels) {
    $pixels: $pixels * 1px;
  }
  
  @if unit($pixels) != 'px' {
    @error "rem() function expects pixel values. Received: #{$pixels}";
  }
  
  @return $pixels / $base-font-size * 1rem;
}

@function em($pixels, $context: 16px) {
  @if unitless($pixels) {
    $pixels: $pixels * 1px;
  }
  
  @if unitless($context) {
    $context: $context * 1px;
  }
  
  @return $pixels / $context * 1em;
}

// String manipulation functions
@function str-replace($string, $search, $replace: '') {
  $index: str-index($string, $search);
  
  @if $index {
    @return str-slice($string, 1, $index - 1) + $replace + str-replace(str-slice($string, $index + str-length($search)), $search, $replace);
  }
  
  @return $string;
}

@function url-encode($string) {
  $map: (
    "%": "%25",
    "<": "%3C",
    ">": "%3E",
    " ": "%20",
    "!": "%21",
    "*": "%2A",
    "'": "%27",
    '"': "%22",
    "(": "%28",
    ")": "%29",
    ";": "%3B",
    ":": "%3A",
    "@": "%40",
    "&": "%26",
    "=": "%3D",
    "+": "%2B",
    "$": "%24",
    ",": "%2C",
    "/": "%2F",
    "?": "%3F",
    "#": "%23",
    "[": "%5B",
    "]": "%5D"
  );
  
  $result: $string;
  
  @each $search, $replace in $map {
    $result: str-replace($result, $search, $replace);
  }
  
  @return $result;
}

// Math utility functions
@function pow($number, $exponent) {
  @if $exponent == 0 {
    @return 1;
  }
  
  $result: 1;
  
  @for $i from 1 through abs($exponent) {
    $result: $result * $number;
  }
  
  @if $exponent < 0 {
    @return 1 / $result;
  }
  
  @return $result;
}

@function strip-unit($number) {
  @if type-of($number) == 'number' and not unitless($number) {
    @return $number / ($number * 0 + 1);
  }
  
  @return $number;
}

// 使用例
.component {
  // Color contrast calculation
  $bg-color: color(text-primary);
  background-color: $bg-color;
  color: color-contrast($bg-color);
  
  // Unit conversion
  padding: rem(24px) rem(32px);
  margin: em(16px, 18px);
  
  // Alpha blending
  border-color: alpha-blend(color(text-primary), color(surface-primary), 0.2);
  
  &::before {
    // URL encoding for SVG data URI
    $svg: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>';
    background-image: url('data:image/svg+xml,#{url-encode($svg)}');
  }
}

// Responsive font size with mathematical calculation
.heading {
  font-size: rem(pow(1.2, 3) * 16px); // 1.2^3 * 16px = ~27.6px
  
  @include media(lg) {
    font-size: rem(pow(1.2, 4) * 16px); // 1.2^4 * 16px = ~33.2px
  }
}
```

#### **制御構造の活用**
```scss
/* ✅ Good: 効果的な制御構造の使用 */

// Theme generation with loops and conditions
$themes: (
  light: (
    text-primary: #1a1a1a,
    text-secondary: #6b7280,
    surface-primary: #ffffff,
    surface-secondary: #f9fafb,
    border-primary: #e5e7eb
  ),
  dark: (
    text-primary: #f9fafb,
    text-secondary: #9ca3af,
    surface-primary: #1f2937,
    surface-secondary: #374151,
    border-primary: #4b5563
  )
);

@each $theme-name, $theme-colors in $themes {
  .theme-#{$theme-name} {
    @each $property, $value in $theme-colors {
      --color-#{$property}: #{$value};
    }
  }
}

// Button variant generation
$button-variants: (
  primary: color(text-primary),
  secondary: color(surface-secondary),
  success: color(status-success),
  warning: color(status-warning),
  error: color(status-error)
);

@each $variant, $color in $button-variants {
  .btn--#{$variant} {
    @include button-variant($color);
  }
}

// Spacing utility generation
@each $name, $value in $spacing {
  // Margin utilities
  .m-#{$name} { margin: $value !important; }
  .mt-#{$name} { margin-top: $value !important; }
  .mr-#{$name} { margin-right: $value !important; }
  .mb-#{$name} { margin-bottom: $value !important; }
  .ml-#{$name} { margin-left: $value !important; }
  .mx-#{$name} {
    margin-left: $value !important;
    margin-right: $value !important;
  }
  .my-#{$name} {
    margin-top: $value !important;
    margin-bottom: $value !important;
  }
  
  // Padding utilities
  .p-#{$name} { padding: $value !important; }
  .pt-#{$name} { padding-top: $value !important; }
  .pr-#{$name} { padding-right: $value !important; }
  .pb-#{$name} { padding-bottom: $value !important; }
  .pl-#{$name} { padding-left: $value !important; }
  .px-#{$name} {
    padding-left: $value !important;
    padding-right: $value !important;
  }
  .py-#{$name} {
    padding-top: $value !important;
    padding-bottom: $value !important;
  }
}

// Responsive utility generation
@each $breakpoint-name, $breakpoint-value in $breakpoints {
  @if $breakpoint-value > 0 {
    @include media($breakpoint-name) {
      // Display utilities
      .#{$breakpoint-name}\:block { display: block !important; }
      .#{$breakpoint-name}\:flex { display: flex !important; }
      .#{$breakpoint-name}\:grid { display: grid !important; }
      .#{$breakpoint-name}\:hidden { display: none !important; }
      
      // Text alignment
      .#{$breakpoint-name}\:text-left { text-align: left !important; }
      .#{$breakpoint-name}\:text-center { text-align: center !important; }
      .#{$breakpoint-name}\:text-right { text-align: right !important; }
    }
  }
}

// Complex conditional logic
@mixin button-style($variant: primary, $size: base, $full-width: false) {
  // Base styles
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: radius(md);
  font-weight: map-get($font-weights, medium);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
  
  // Full width handling
  @if $full-width {
    width: 100%;
  }
  
  // Size variations
  @if $size == xs {
    @include button-size(1, 2, xs, sm);
  } @else if $size == sm {
    @include button-size(1-5, 3, sm);
  } @else if $size == base {
    @include button-size(2-5, 4, base);
  } @else if $size == lg {
    @include button-size(3, 6, lg);
  } @else if $size == xl {
    @include button-size(4, 8, xl, lg);
  } @else {
    @error "Invalid button size '#{$size}'. Available sizes: xs, sm, base, lg, xl";
  }
  
  // Variant handling
  @if $variant == primary {
    @include button-variant(color(text-primary), color(text-inverse));
  } @else if $variant == secondary {
    @include button-variant(color(surface-secondary), color(text-primary));
  } @else if $variant == outline {
    background-color: transparent;
    color: color(text-primary);
    border: 1px solid color(border-primary);
    
    &:hover {
      background-color: color(surface-secondary);
    }
  } @else if $variant == ghost {
    background-color: transparent;
    color: color(text-primary);
    border: 1px solid transparent;
    
    &:hover {
      background-color: color(surface-secondary);
    }
  } @else {
    @error "Invalid button variant '#{$variant}'. Available variants: primary, secondary, outline, ghost";
  }
}

// Usage
.btn {
  @include button-style();
  
  &--secondary {
    @include button-style(secondary);
  }
  
  &--large {
    @include button-style($size: lg);
  }
  
  &--full {
    @include button-style($full-width: true);
  }
  
  &--outline-sm {
    @include button-style(outline, sm);
  }
}
```

## 7. モダンCSS機能

### 7.1 CSS Custom Properties (CSS Variables)

#### **Dynamic Theme System**
```css
/* ✅ Good: 動的テーマシステムの実装 */

:root {
  /* Light theme (default) */
  --color-scheme: light;
  
  /* Base color palette */
  --color-primary-h: 210;
  --color-primary-s: 100%;
  --color-primary-l: 45%;
  
  /* Semantic color tokens using HSL */
  --color-primary: hsl(var(--color-primary-h), var(--color-primary-s), var(--color-primary-l));
  --color-primary-light: hsl(var(--color-primary-h), var(--color-primary-s), calc(var(--color-primary-l) + 10%));
  --color-primary-dark: hsl(var(--color-primary-h), var(--color-primary-s), calc(var(--color-primary-l) - 10%));
  
  /* Context-aware text colors */
  --color-text-primary: hsl(0, 0%, 5%);
  --color-text-secondary: hsl(0, 0%, 40%);
  --color-text-tertiary: hsl(0, 0%, 60%);
  --color-text-inverse: hsl(0, 0%, 95%);
  
  /* Surface colors */
  --color-surface-primary: hsl(0, 0%, 100%);
  --color-surface-secondary: hsl(0, 0%, 98%);
  --color-surface-tertiary: hsl(0, 0%, 95%);
  
  /* Interactive states */
  --color-border: hsl(0, 0%, 85%);
  --color-border-hover: hsl(0, 0%, 70%);
  --color-border-focus: var(--color-primary);
  
  /* Spacing system with mathematical relationships */
  --spacing-unit: 0.25rem; /* 4px base unit */
  --spacing-xs: calc(var(--spacing-unit) * 1);   /* 4px */
  --spacing-sm: calc(var(--spacing-unit) * 2);   /* 8px */
  --spacing-md: calc(var(--spacing-unit) * 4);   /* 16px */
  --spacing-lg: calc(var(--spacing-unit) * 6);   /* 24px */
  --spacing-xl: calc(var(--spacing-unit) * 8);   /* 32px */
  --spacing-2xl: calc(var(--spacing-unit) * 12); /* 48px */
  --spacing-3xl: calc(var(--spacing-unit) * 16); /* 64px */
  
  /* Typography scale using modular scale */
  --font-scale: 1.25; /* Major third */
  --font-size-base: 1rem;
  --font-size-sm: calc(var(--font-size-base) / var(--font-scale));
  --font-size-xs: calc(var(--font-size-sm) / var(--font-scale));
  --font-size-lg: calc(var(--font-size-base) * var(--font-scale));
  --font-size-xl: calc(var(--font-size-lg) * var(--font-scale));
  --font-size-2xl: calc(var(--font-size-xl) * var(--font-scale));
  --font-size-3xl: calc(var(--font-size-2xl) * var(--font-scale));
  
  /* Animation timing */
  --duration-fast: 150ms;
  --duration-base: 250ms;
  --duration-slow: 350ms;
  --ease-out: cubic-bezier(0.25, 0.46, 0.45, 0.94);
  --ease-in: cubic-bezier(0.55, 0.06, 0.68, 0.19);
  --ease-in-out: cubic-bezier(0.42, 0, 0.58, 1);
  
  /* Layout constraints */
  --container-max-width: 1200px;
  --sidebar-width: 280px;
  --header-height: 64px;
  
  /* Z-index system */
  --z-dropdown: 1000;
  --z-sticky: 1100;
  --z-overlay: 1200;
  --z-modal: 1300;
  --z-tooltip: 1400;
}
```

### 7.2 CSS Cascade Layers (@layer)

#### **レイヤー戦略とアーキテクチャ**
```css
/* ✅ Good: 明確なレイヤー戦略による保守性向上 */

/* Layer declaration (order matters) */
@layer reset, base, layout, components, utilities, overrides;

/* Reset layer - Lowest specificity */
@layer reset {
  *,
  *::before,
  *::after {
    box-sizing: border-box;
  }
  
  * {
    margin: 0;
    padding: 0;
  }
  
  html {
    color-scheme: var(--color-scheme, light);
    font-feature-settings: "kern" 1, "liga" 1, "calt" 1;
    text-rendering: optimizeLegibility;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }
  
  body {
    font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
    line-height: 1.6;
    color: var(--color-text-primary);
    background-color: var(--color-surface-primary);
  }
}

/* Components layer - UI components */
@layer components {
  .btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: var(--spacing-xs);
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--font-size-base);
    font-weight: 500;
    line-height: 1;
    border: 1px solid transparent;
    border-radius: var(--spacing-xs);
    cursor: pointer;
    transition: all var(--duration-fast) var(--ease-out);
    
    &--primary {
      background-color: var(--color-primary);
      color: var(--color-text-inverse);
      
      &:hover {
        background-color: var(--color-primary-dark);
      }
    }
  }
}

/* Utilities layer - Helper classes */ 
@layer utilities {
  .text-center { text-align: center !important; }
  .font-bold { font-weight: 700 !important; }
  .hidden { display: none !important; }
}
```

### 7.3 CSS Subgrid & Advanced Grid

#### **Subgrid Implementation Patterns**
```css
/* ✅ Good: Subgridを活用した複雑なレイアウト */

/* Main grid container */
.article-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: var(--spacing-lg);
  margin: var(--spacing-2xl) 0;
}

/* Article card with subgrid */
.article-card {
  grid-column: span 12;
  
  /* Create subgrid for internal layout */
  display: grid;
  grid-template-columns: subgrid;
  grid-template-rows: auto auto 1fr auto;
  gap: var(--spacing-md);
  
  padding: var(--spacing-lg);
  background-color: var(--color-surface-primary);
  border: 1px solid var(--color-border);
  border-radius: var(--spacing-md);
  
  @container (min-width: 768px) {
    grid-column: span 6;
  }
  
  @container (min-width: 1024px) {
    grid-column: span 4;
  }
}

/* Featured article spanning full width */
.article-card--featured {
  grid-column: 1 / -1;
  
  display: grid;
  grid-template-columns: subgrid;
  
  @container (min-width: 768px) {
    grid-template-areas: "image content";
    align-items: center;
  }
}

.article-card__image {
  grid-column: 1 / -1;
  aspect-ratio: 16 / 9;
  border-radius: var(--spacing-xs);
  overflow: hidden;
  
  .article-card--featured & {
    @container (min-width: 768px) {
      grid-area: image;
      grid-column: span 5;
    }
  }
}

.article-card__content {
  grid-column: 1 / -1;
  
  .article-card--featured & {
    @container (min-width: 768px) {
      grid-area: content;
      grid-column: span 7;
    }
  }
}

/* Fallback for browsers without subgrid support */
@supports not (grid-template-columns: subgrid) {
  .article-card {
    display: flex;
    flex-direction: column;
  }
  
  .article-card--featured {
    @container (min-width: 768px) {
      display: flex;
      flex-direction: row;
      gap: var(--spacing-lg);
    }
  }
}
```
## 8. パフォーマンス最適化

### 8.1 Critical CSS & レンダリング最適化

#### **Critical CSS 抽出戦略**
```css
/* ✅ Good: Critical CSS の戦略的分離 */

/* =================================
   CRITICAL CSS - Above the fold
   =================================
   このセクションはファーストビューに必要なスタイルのみを含む
*/

/* Reset essentials */
*,
*::before,
*::after {
  box-sizing: border-box;
}

* {
  margin: 0;
  padding: 0;
}

html {
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
  line-height: 1.6;
  color: #1a1a1a;
  background-color: #ffffff;
}

body {
  min-height: 100vh;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
}

/* Critical layout components only */
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  background-color: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  backdrop-filter: blur(8px);
}

.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
  padding: 3rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  line-height: 1;
  color: #ffffff;
  background-color: #111827;
  border: none;
  border-radius: 0.5rem;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.15s ease;
}

/* Loading states to prevent CLS */
.loading-skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
```

#### **CSS Containment & Performance**
```css
/* ✅ Good: CSS Containmentでレンダリング最適化 */

.card {
  /* Isolate layout calculations */
  contain: layout style;
  
  /* Improve compositing performance */
  will-change: transform;
  transform: translateZ(0);
  
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
  background-color: #ffffff;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-2px) translateZ(0);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Size containment for fixed-size components */
.avatar {
  contain: size layout;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

/* Virtual scrolling container */
.virtual-list {
  contain: strict;
  height: 400px;
  overflow: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
}

.performance-grid-item {
  contain: layout style;
  content-visibility: auto;
  contain-intrinsic-size: 0 200px;
}
```

### 8.2 フォント最適化 & リソースヒント

#### **フォント読み込み戦略**
```css
/* ✅ Good: パフォーマンスを意識したフォント読み込み */

@font-face {
  font-family: 'Inter';
  src: url('/fonts/Inter-var.woff2') format('woff2-variations');
  font-weight: 100 900;
  font-style: normal;
  font-display: swap; /* Improve font loading performance */
  unicode-range: U+0000-00FF, U+0131, U+0152-0153;
}

:root {
  --font-family-primary: 'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
  --font-family-mono: 'SF Mono', Monaco, 'Roboto Mono', monospace;
}

body {
  font-family: var(--font-family-primary);
  font-feature-settings: 'kern' 1, 'liga' 1, 'calt' 1;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Size-specific font optimizations */
.text-sm {
  font-size: 0.875rem;
  font-variant-ligatures: none;
  font-feature-settings: 'kern' 1;
}

.text-display {
  font-size: clamp(2rem, 5vw, 4rem);
  font-weight: 700;
  line-height: 1.1;
  font-feature-settings: 'kern' 1, 'liga' 1, 'dlig' 1;
  text-rendering: geometricPrecision;
}

.code {
  font-family: var(--font-family-mono);
  font-feature-settings: 'zero' 1, 'ss01' 1;
  font-variant-numeric: tabular-nums;
  white-space: pre;
  tab-size: 2;
}
```

### 8.3 CSS Bundle Size & Tree Shaking

#### **Unused CSS 最適化**
```css
/* ✅ Good: Tree-shaking friendly CSS architecture */

/* Utility-first approach */
.u-text-center { text-align: center; }
.u-text-left { text-align: left; }
.u-font-bold { font-weight: 700; }
.u-text-lg { font-size: 1.125rem; }

/* Component-scoped styles */
.c-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  border: 1px solid transparent;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
}

.c-button--primary {
  background-color: #3b82f6;
  color: #ffffff;
}

/* Performance monitoring CSS */
.prevent-cls {
  min-height: 200px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

.prevent-cls.loaded {
  min-height: auto;
  background: none;
  animation: none;
}

/* Memory-efficient animations */
.efficient-animation {
  transform: translateX(0);
  transition: transform 0.2s ease;
}

.efficient-animation:hover {
  transform: translateX(10px);
}
```

## 9. アクセシビリティ

### 9.1 Focus Management & キーボードナビゲーション

#### **Focus Ring & Visual Indicators**
```css
/* ✅ Good: アクセシブルなフォーカス管理 */

/* Modern focus ring system */
:root {
  --focus-ring-color: #005fcc;
  --focus-ring-width: 2px;
  --focus-ring-offset: 2px;
  --focus-ring-style: solid;
  --focus-ring-opacity: 1;
}

/* Global focus-visible implementation */
:focus {
  outline: none; /* Remove default outline */
}

:focus-visible {
  outline: var(--focus-ring-width) var(--focus-ring-style) var(--focus-ring-color);
  outline-offset: var(--focus-ring-offset);
  border-radius: 2px;
}

/* Interactive elements with enhanced focus */
button,
[role="button"],
input,
select,
textarea,
a {
  position: relative;
  transition: all 0.2s ease;
}

button:focus-visible,
[role="button"]:focus-visible {
  outline: var(--focus-ring-width) var(--focus-ring-style) var(--focus-ring-color);
  outline-offset: var(--focus-ring-offset);
  box-shadow: 0 0 0 calc(var(--focus-ring-width) + var(--focus-ring-offset)) 
              rgba(0, 95, 204, 0.2);
}

/* Form elements with consistent focus treatment */
input:focus-visible,
select:focus-visible,
textarea:focus-visible {
  border-color: var(--focus-ring-color);
  box-shadow: 0 0 0 var(--focus-ring-width) rgba(0, 95, 204, 0.2);
  outline: none;
}

/* Links with underline focus indicator */
a:focus-visible {
  outline: var(--focus-ring-width) var(--focus-ring-style) var(--focus-ring-color);
  outline-offset: var(--focus-ring-offset);
  text-decoration: underline;
  text-decoration-thickness: 2px;
  text-underline-offset: 4px;
}

/* Skip link implementation */
.skip-link {
  position: absolute;
  top: -40px;
  left: 6px;
  background: var(--focus-ring-color);
  color: white;
  padding: 8px 12px;
  font-size: 14px;
  text-decoration: none;
  border-radius: 4px;
  z-index: 9999;
  transition: top 0.3s ease;
}

.skip-link:focus {
  top: 6px;
  outline: 2px solid white;
  outline-offset: 2px;
}

/* Focus trap for modal dialogs */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal[aria-hidden="true"] {
  display: none;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.modal-content:focus {
  outline: none;
}

/* Focus within for maintaining focus */
.focus-within-container:focus-within {
  box-shadow: 0 0 0 2px var(--focus-ring-color);
  border-radius: 4px;
}
```

#### **Keyboard Navigation Patterns**
```css
/* ✅ Good: キーボードナビゲーション対応 */

/* Tab order optimization */
.tab-container {
  display: flex;
  border-bottom: 1px solid #d1d5db;
}

.tab-button {
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  color: #6b7280;
  font-weight: 500;
  transition: all 0.2s ease;
}

.tab-button:hover {
  color: #374151;
  background-color: #f9fafb;
}

.tab-button:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: -2px;
  color: #374151;
}

.tab-button[aria-selected="true"] {
  color: #1f2937;
  border-bottom-color: var(--focus-ring-color);
  background-color: #ffffff;
}

/* Dropdown menu with arrow key navigation */
.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-toggle {
  padding: 0.5rem 1rem;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.dropdown-toggle:focus-visible {
  border-color: var(--focus-ring-color);
  box-shadow: 0 0 0 2px rgba(0, 95, 204, 0.2);
  outline: none;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  z-index: 100;
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.2s ease;
}

.dropdown-menu[aria-expanded="true"] {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  color: #374151;
  transition: background-color 0.2s ease;
}

.dropdown-item:hover,
.dropdown-item:focus {
  background-color: #f3f4f6;
  outline: none;
}

.dropdown-item:focus-visible {
  background-color: #e5e7eb;
  outline: 2px solid var(--focus-ring-color);
  outline-offset: -2px;
}

/* Breadcrumb navigation */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 0;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.breadcrumb-link {
  color: #6b7280;
  text-decoration: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.breadcrumb-link:hover {
  color: #374151;
  background-color: #f9fafb;
}

.breadcrumb-link:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: 2px;
  color: #374151;
}

.breadcrumb-separator {
  color: #9ca3af;
  font-size: 0.875rem;
  user-select: none;
}

/* Current page indicator */
.breadcrumb-current {
  color: #1f2937;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
}
```

### 9.2 Color Contrast & Visual Accessibility

#### **High Contrast Mode Support**
```css
/* ✅ Good: カラーコントラストとビジュアルアクセシビリティ */

/* Base color system with WCAG AA compliance */
:root {
  /* Text colors with 4.5:1 contrast ratio minimum */
  --color-text-primary: #111827;     /* 16.25:1 on white */
  --color-text-secondary: #4b5563;   /* 7.22:1 on white */
  --color-text-tertiary: #6b7280;    /* 5.49:1 on white */
  --color-text-inverse: #f9fafb;     /* 17.67:1 on dark */
  
  /* Interactive colors with sufficient contrast */
  --color-link: #1d4ed8;             /* 6.24:1 on white */
  --color-link-hover: #1e40af;       /* 7.48:1 on white */
  --color-link-visited: #7c3aed;     /* 5.61:1 on white */
  
  /* Status colors meeting WCAG requirements */
  --color-success: #059669;          /* 4.89:1 on white */
  --color-warning: #d97706;          /* 4.58:1 on white */
  --color-error: #dc2626;            /* 5.25:1 on white */
  --color-info: #2563eb;             /* 6.05:1 on white */
  
  /* Background colors */
  --color-bg-primary: #ffffff;
  --color-bg-secondary: #f9fafb;
  --color-bg-tertiary: #f3f4f6;
}

/* High contrast mode overrides */
@media (prefers-contrast: high) {
  :root {
    --color-text-primary: #000000;
    --color-text-secondary: #000000;
    --color-text-tertiary: #000000;
    --color-bg-primary: #ffffff;
    --color-bg-secondary: #ffffff;
    --color-bg-tertiary: #ffffff;
    
    --color-link: #0000ee;
    --color-link-hover: #0000aa;
    --color-link-visited: #800080;
    
    --color-success: #008000;
    --color-warning: #ff8c00;
    --color-error: #ff0000;
    --color-info: #0000ff;
  }
  
  /* Enhanced borders and outlines */
  .card,
  .button,
  .form-input {
    border-width: 2px;
    border-color: #000000;
  }
  
  /* Remove subtle shadows and backgrounds */
  .card {
    box-shadow: none;
    background-color: #ffffff;
  }
  
  /* Ensure all interactive elements are clearly defined */
  button,
  [role="button"],
  input,
  select,
  textarea {
    border: 2px solid #000000;
    background-color: #ffffff;
  }
}

/* Dark theme with proper contrast ratios */
@media (prefers-color-scheme: dark) {
  :root {
    --color-text-primary: #f9fafb;    /* 17.67:1 on #111827 */
    --color-text-secondary: #d1d5db;  /* 10.89:1 on #111827 */
    --color-text-tertiary: #9ca3af;   /* 6.37:1 on #111827 */
    --color-text-inverse: #111827;
    
    --color-bg-primary: #111827;
    --color-bg-secondary: #1f2937;
    --color-bg-tertiary: #374151;
    
    --color-link: #60a5fa;            /* 5.12:1 on #111827 */
    --color-link-hover: #93c5fd;      /* 7.04:1 on #111827 */
    --color-link-visited: #c084fc;    /* 4.75:1 on #111827 */
    
    --color-success: #10b981;         /* 5.32:1 on #111827 */
    --color-warning: #f59e0b;         /* 8.41:1 on #111827 */
    --color-error: #f87171;           /* 5.96:1 on #111827 */
    --color-info: #60a5fa;            /* 5.12:1 on #111827 */
  }
}

/* Forced colors mode (Windows High Contrast) */
@media (forced-colors: active) {
  :root {
    --color-text-primary: CanvasText;
    --color-text-secondary: CanvasText;
    --color-bg-primary: Canvas;
    --color-link: LinkText;
    --color-link-visited: VisitedText;
    --focus-ring-color: Highlight;
  }
  
  .card {
    border: 1px solid CanvasText;
    background-color: Canvas;
    box-shadow: none;
  }
  
  .button {
    border: 1px solid ButtonBorder;
    background-color: ButtonFace;
    color: ButtonText;
  }
  
  .button:hover {
    background-color: Highlight;
    color: HighlightText;
    border-color: HighlightText;
  }
  
  /* Ensure focus indicators are visible */
  :focus-visible {
    outline: 2px solid Highlight;
    outline-offset: 2px;
  }
}
```

#### **Color-blind Friendly Design**
```css
/* ✅ Good: 色覚障害に配慮したデザイン */

/* Status indicators with multiple visual cues */
.status-indicator {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;
}

/* Success state: Green + checkmark */
.status-indicator--success {
  background-color: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.status-indicator--success::before {
  content: "✓";
  font-weight: bold;
}

/* Warning state: Orange + exclamation */
.status-indicator--warning {
  background-color: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
}

.status-indicator--warning::before {
  content: "!";
  font-weight: bold;
}

/* Error state: Red + X mark */
.status-indicator--error {
  background-color: #fecaca;
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.status-indicator--error::before {
  content: "✕";
  font-weight: bold;
}

/* Info state: Blue + i mark */
.status-indicator--info {
  background-color: #dbeafe;
  color: #1e40af;
  border: 1px solid #93c5fd;
}

.status-indicator--info::before {
  content: "i";
  font-weight: bold;
  font-style: italic;
}

/* Chart colors with patterns for accessibility */
.chart-bar {
  transition: opacity 0.2s ease;
}

.chart-bar--pattern-1 {
  fill: #1f77b4;
  stroke: #ffffff;
  stroke-width: 1;
}

.chart-bar--pattern-2 {
  fill: #ff7f0e;
  stroke-dasharray: 3,3;
  stroke: #ffffff;
  stroke-width: 1;
}

.chart-bar--pattern-3 {
  fill: #2ca02c;
  stroke-dasharray: 5,2,2,2;
  stroke: #ffffff;
  stroke-width: 1;
}

.chart-bar--pattern-4 {
  fill: #d62728;
  stroke-dasharray: 1,1;
  stroke: #ffffff;
  stroke-width: 1;
}

/* Form validation with multiple indicators */
.form-field {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.form-input {
  display: block;
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

/* Error state with color, icon, and text */
.form-field--error .form-label {
  color: var(--color-error);
}

.form-field--error .form-input {
  border-color: var(--color-error);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23dc2626'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 18L18 6M6 6l12 12'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem 1rem;
  padding-right: 2.5rem;
}

.form-field--error .form-input:focus {
  border-color: var(--color-error);
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.form-error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: var(--color-error);
}

.form-error-message::before {
  content: "⚠";
  font-weight: bold;
}

/* Success state */
.form-field--success .form-input {
  border-color: var(--color-success);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23059669'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M5 13l4 4L19 7'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem 1rem;
  padding-right: 2.5rem;
}
```

### 9.3 Screen Reader & ARIA Support

#### **Semantic HTML & ARIA Labels**
```css
/* ✅ Good: スクリーンリーダー対応とARIA実装 */

/* Visually hidden but screen reader accessible */
.sr-only {
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0, 0, 0, 0) !important;
  white-space: nowrap !important;
  border: 0 !important;
}

/* Focus-on for screen reader only content */
.sr-only:focus {
  position: static !important;
  width: auto !important;
  height: auto !important;
  padding: inherit !important;
  margin: inherit !important;
  overflow: visible !important;
  clip: auto !important;
  white-space: normal !important;
}

/* Loading states with proper announcements */
.loading-spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Button with loading state */
.button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #3b82f6;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.button:hover:not(:disabled) {
  background-color: #2563eb;
}

.button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.button[aria-busy="true"] {
  pointer-events: none;
}

.button[aria-busy="true"] .loading-spinner {
  display: inline-block;
}

.button:not([aria-busy="true"]) .loading-spinner {
  display: none;
}

/* Modal dialog with proper focus management */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-overlay[aria-hidden="false"] {
  opacity: 1;
  visibility: visible;
}

.modal-dialog {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  transform: scale(0.95);
  transition: transform 0.3s ease;
}

.modal-overlay[aria-hidden="false"] .modal-dialog {
  transform: scale(1);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 1.5rem 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  padding: 0;
  background: none;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.modal-close:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: 2px;
}

/* Progress indicators with proper labeling */
.progress-container {
  margin: 1rem 0;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.progress-bar {
  width: 100%;
  height: 0.5rem;
  background-color: #e5e7eb;
  border-radius: 9999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #3b82f6;
  border-radius: 9999px;
  transition: width 0.3s ease;
  transform-origin: left center;
}

/* Tooltip with proper ARIA implementation */
.tooltip-trigger {
  position: relative;
  display: inline-block;
  cursor: help;
}

.tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-bottom: 0.5rem;
  padding: 0.5rem 0.75rem;
  background-color: #1f2937;
  color: #ffffff;
  font-size: 0.875rem;
  border-radius: 6px;
  white-space: nowrap;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  pointer-events: none;
}

.tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 4px solid transparent;
  border-top-color: #1f2937;
}

.tooltip-trigger:hover .tooltip,
.tooltip-trigger:focus .tooltip,
.tooltip[aria-hidden="false"] {
  opacity: 1;
  visibility: visible;
}

/* Breadcrumb navigation with proper semantics */
.breadcrumb-nav {
  padding: 1rem 0;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.breadcrumb-link {
  color: #6b7280;
  text-decoration: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.breadcrumb-link:hover {
  color: #374151;
  background-color: #f9fafb;
}

.breadcrumb-link:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: 2px;
}

.breadcrumb-separator {
  color: #9ca3af;
  font-size: 0.875rem;
  user-select: none;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
}
```

## 10. ツール設定・自動化

### 10.1 Stylelint 設定

#### **完全な Stylelint 設定ファイル**
```json
{
  "extends": [
    "stylelint-config-standard",
    "stylelint-config-recess-order",
    "stylelint-config-prettier"
  ],
  "plugins": [
    "stylelint-scss",
    "stylelint-order",
    "stylelint-selector-bem-pattern",
    "stylelint-declaration-strict-value",
    "stylelint-high-performance-animation"
  ],
  "rules": {
    "/* ===== 基本規則 ===== */": null,
    "indentation": 2,
    "string-quotes": "single",
    "no-duplicate-selectors": true,
    "color-hex-case": "lower",
    "color-hex-length": "short",
    "color-named": "never",
    "selector-combinator-space-after": "always",
    "selector-attribute-quotes": "always",
    "selector-attribute-operator-space-before": "never",
    "selector-attribute-operator-space-after": "never",
    "selector-attribute-brackets-space-inside": "never",
    "declaration-block-trailing-semicolon": "always",
    "declaration-colon-space-before": "never",
    "declaration-colon-space-after": "always",
    "number-leading-zero": "always",
    "function-url-quotes": "always",
    "font-weight-notation": "numeric",
    "comment-word-disallowed-list": ["todo", "fixme"],
    
    "/* ===== パフォーマンス関連 ===== */": null,
    "plugin/no-low-performance-animation-properties": [
      true,
      {
        "ignore": "paint-properties"
      }
    ],
    "declaration-no-important": [
      true,
      {
        "ignore": ["keyframes"]
      }
    ],
    "selector-max-id": 0,
    "selector-max-universal": 1,
    "selector-max-type": 2,
    "selector-max-compound-selectors": 4,
    "selector-max-specificity": "0,3,2",
    
    "/* ===== SCSS 関連 ===== */": null,
    "scss/at-extend-no-missing-placeholder": true,
    "scss/at-function-pattern": "^[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/at-import-no-partial-leading-underscore": true,
    "scss/at-import-partial-extension-blacklist": ["scss"],
    "scss/at-mixin-pattern": "^[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/at-rule-no-unknown": true,
    "scss/dollar-variable-colon-space-after": "always",
    "scss/dollar-variable-colon-space-before": "never",
    "scss/dollar-variable-pattern": "^[_]?[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/percent-placeholder-pattern": "^[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/selector-no-redundant-nesting-selector": true,
    
    "/* ===== BEM パターン ===== */": null,
    "plugin/selector-bem-pattern": {
      "preset": "bem",
      "componentName": "[A-Z]+",
      "componentSelectors": {
        "initial": "^\\.{componentName}(?:-[a-z]+)*$",
        "combined": "^\\.combined-{componentName}-[a-z]+$"
      },
      "utilitySelectors": "^\\.u-[a-z]+$"
    },
    
    "/* ===== デザインシステム制約 ===== */": null,
    "scale-unlimited/declaration-strict-value": [
      ["/color$/", "z-index", "font-size", "font-weight"],
      {
        "ignoreKeywords": {
          "color": ["currentColor", "transparent", "inherit"],
          "z-index": ["auto"],
          "font-size": ["inherit", "initial"],
          "font-weight": ["inherit", "initial"]
        },
        "ignoreFunctions": false,
        "disableFix": true,
        "expandShorthand": true
      }
    ],
    
    "/* ===== プロパティ順序 ===== */": null,
    "order/properties-order": [
      [
        {
          "groupName": "positioning",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "position",
            "top",
            "right",
            "bottom",
            "left",
            "z-index"
          ]
        },
        {
          "groupName": "display",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "display",
            "flex",
            "flex-grow",
            "flex-shrink",
            "flex-basis",
            "flex-direction",
            "flex-wrap",
            "justify-content",
            "align-content",
            "align-items",
            "align-self",
            "order",
            "grid",
            "grid-area",
            "grid-template",
            "grid-template-areas",
            "grid-template-rows",
            "grid-template-columns",
            "grid-row",
            "grid-row-start",
            "grid-row-end",
            "grid-column",
            "grid-column-start",
            "grid-column-end",
            "grid-auto-rows",
            "grid-auto-columns",
            "grid-auto-flow",
            "grid-gap",
            "grid-row-gap",
            "grid-column-gap",
            "gap",
            "row-gap",
            "column-gap"
          ]
        },
        {
          "groupName": "box-model",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "width",
            "min-width",
            "max-width",
            "height",
            "min-height",
            "max-height",
            "padding",
            "padding-top",
            "padding-right",
            "padding-bottom",
            "padding-left",
            "margin",
            "margin-top",
            "margin-right",
            "margin-bottom",
            "margin-left",
            "border",
            "border-spacing",
            "border-collapse",
            "border-width",
            "border-style",
            "border-color",
            "border-top",
            "border-right",
            "border-bottom",
            "border-left",
            "border-radius",
            "outline",
            "outline-width",
            "outline-style",
            "outline-color",
            "outline-offset",
            "box-sizing"
          ]
        },
        {
          "groupName": "typography",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "font",
            "font-family",
            "font-size",
            "font-weight",
            "font-style",
            "font-variant",
            "font-size-adjust",
            "font-stretch",
            "font-effect",
            "font-emphasize",
            "font-emphasize-position",
            "font-emphasize-style",
            "font-smooth",
            "line-height",
            "letter-spacing",
            "word-spacing",
            "color",
            "text-align",
            "text-align-last",
            "text-emphasis",
            "text-emphasis-color",
            "text-emphasis-style",
            "text-emphasis-position",
            "text-decoration",
            "text-indent",
            "text-justify",
            "text-outline",
            "text-overflow",
            "text-overflow-ellipsis",
            "text-overflow-mode",
            "text-shadow",
            "text-transform",
            "text-wrap",
            "word-wrap",
            "word-break"
          ]
        },
        {
          "groupName": "visual",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "background",
            "background-color",
            "background-image",
            "background-repeat",
            "background-attachment",
            "background-position",
            "background-position-x",
            "background-position-y",
            "background-clip",
            "background-origin",
            "background-size",
            "box-decoration-break",
            "box-shadow",
            "opacity",
            "filter",
            "list-style",
            "list-style-position",
            "list-style-type",
            "list-style-image"
          ]
        },
        {
          "groupName": "animation",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "transition",
            "transition-delay",
            "transition-timing-function",
            "transition-duration",
            "transition-property",
            "transform",
            "transform-origin",
            "animation",
            "animation-name",
            "animation-duration",
            "animation-play-state",
            "animation-timing-function",
            "animation-delay",
            "animation-iteration-count",
            "animation-direction",
            "animation-fill-mode"
          ]
        },
        {
          "groupName": "misc",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "appearance",
            "clip",
            "clip-path",
            "counter-reset",
            "counter-increment",
            "resize",
            "user-select",
            "nav-index",
            "nav-up",
            "nav-right",
            "nav-down",
            "nav-left",
            "tab-size",
            "hyphens",
            "pointer-events",
            "will-change",
            "contain",
            "content-visibility",
            "cursor",
            "overflow",
            "overflow-x",
            "overflow-y",
            "overflow-scrolling",
            "overscroll-behavior",
            "overscroll-behavior-x",
            "overscroll-behavior-y"
          ]
        }
      ],
      {
        "unspecified": "bottomAlphabetical"
      }
    ],
    
    "/* ===== 例外とカスタム規則 ===== */": null,
    "at-rule-no-unknown": [
      true,
      {
        "ignoreAtRules": [
          "tailwind",
          "apply",
          "variants",
          "responsive",
          "screen",
          "layer",
          "container"
        ]
      }
    ],
    "function-no-unknown": [
      true,
      {
        "ignoreFunctions": ["theme", "screen"]
      }
    ],
    "selector-pseudo-class-no-unknown": [
      true,
      {
        "ignorePseudoClasses": ["global", "local", "export"]
      }
    ]
  },
  "ignoreFiles": [
    "**/*.js",
    "**/*.jsx",
    "**/*.ts",
    "**/*.tsx",
    "**/node_modules/**",
    "**/dist/**",
    "**/build/**",
    "**/.next/**",
    "**/coverage/**"
  ]
}
```

#### **package.json スクリプト設定**
```json
{
  "scripts": {
    "css:lint": "stylelint \"**/*.{css,scss}\" --cache --cache-location .cache/stylelint",
    "css:lint:fix": "stylelint \"**/*.{css,scss}\" --fix --cache",
    "css:format": "prettier --write \"**/*.{css,scss}\"",
    "css:check": "npm run css:lint && npm run css:format -- --check",
    "css:fix": "npm run css:lint:fix && npm run css:format",
    "css:watch": "stylelint \"**/*.{css,scss}\" --watch --cache"
  },
  "devDependencies": {
    "stylelint": "^15.10.0",
    "stylelint-config-standard": "^34.0.0",
    "stylelint-config-recess-order": "^4.3.0",
    "stylelint-config-prettier": "^9.0.5",
    "stylelint-scss": "^5.1.0",
    "stylelint-order": "^6.0.3",
    "stylelint-selector-bem-pattern": "^3.0.1",
    "stylelint-declaration-strict-value": "^1.10.4",
    "stylelint-high-performance-animation": "^1.8.0",
    "prettier": "^3.0.0"
  }
}
```

### 10.2 PostCSS 設定

#### **postcss.config.js**
```javascript
/* ✅ Good: 本番対応 PostCSS 設定 */

const isProduction = process.env.NODE_ENV === 'production';

module.exports = {
  plugins: [
    // Modern CSS features
    require('postcss-preset-env')({
      stage: 2,
      features: {
        'custom-properties': false, // Keep CSS variables as-is
        'nesting-rules': true,
        'custom-media-queries': true,
        'media-query-ranges': true,
        'logical-properties-and-values': true,
        'color-functional-notation': true,
        'lab-function': true,
        'oklab-function': true,
        'color-mix': true,
        'trigonometric-functions': true,
        'exponential-functions': true,
      },
      browsers: [
        '> 1%',
        'last 2 versions',
        'Firefox ESR',
        'not dead',
        'not IE 11',
      ],
      autoprefixer: {
        flexbox: 'no-2009',
        grid: 'autoplace',
      },
    }),
    
    // Import processing
    require('postcss-import')({
      plugins: [
        require('stylelint')({
          fix: true,
        }),
      ],
    }),
    
    // CSS nesting (for non-Sass projects)
    require('postcss-nesting'),
    
    // Custom properties enhancements
    require('postcss-custom-properties')({
      preserve: true,
      exportTo: './src/styles/design-tokens.json',
    }),
    
    // Media query optimization
    require('postcss-sort-media-queries')({
      sort: 'desktop-first',
    }),
    
    // CSS optimization for production
    ...(isProduction
      ? [
          // Critical CSS extraction
          require('@fullhuman/postcss-purgecss')({
            content: [
              './src/**/*.html',
              './src/**/*.vue',
              './src/**/*.js',
              './src/**/*.jsx',
              './src/**/*.ts',
              './src/**/*.tsx',
            ],
            defaultExtractor: content => {
              // Extract class names including those with special characters
              const broadMatches = content.match(/[^<>"'`\\s]*[^<>"'`\\s:]/g) || [];
              const innerMatches = content.match(/[^<>"'`\\s.()]*[^<>"'`\\s.():]/g) || [];
              return broadMatches.concat(innerMatches);
            },
            safelist: {
              standard: [
                /^(html|body|#root|#__next)/,
                /^focus-visible$/,
                /^data-/,
                /^aria-/,
                /^sr-only$/,
              ],
              deep: [
                /modal/,
                /dropdown/,
                /tooltip/,
                /loading/,
                /error/,
                /success/,
                /warning/,
                /info/,
              ],
              greedy: [
                /^swiper/,
                /^aos/,
                /^hljs/,
              ],
            },
            // Skip purging for component libraries
            skippedContentGlobs: ['node_modules/**'],
          }),
          
          // CSS minification
          require('cssnano')({
            preset: [
              'advanced',
              {
                cssDeclarationSorter: false, // Handled by stylelint
                discardComments: {
                  removeAll: true,
                },
                reduceIdents: {
                  keyframes: false, // Preserve animation names
                },
                zindex: false, // Don't optimize z-index values
                colormin: {
                  // Preserve CSS custom property references
                  ignore: ['var()'],
                },
              },
            ],
          }),
        ]
      : []),
      
    // Development helpers
    ...(!isProduction
      ? [
          // CSS debugging
          require('postcss-reporter')({
            clearReportedMessages: true,
            throwError: true,
          }),
        ]
      : []),
  ],
};
```

#### **Tailwind CSS Integration**
```javascript
/* ✅ Good: Tailwind CSS + PostCSS 統合設定 */

// tailwind.config.js
const colors = require('tailwindcss/colors');
const plugin = require('tailwindcss/plugin');

module.exports = {
  content: [
    './src/**/*.{html,js,ts,jsx,tsx,vue,svelte}',
    './components/**/*.{js,ts,jsx,tsx,vue,svelte}',
    './pages/**/*.{js,ts,jsx,tsx,vue,svelte}',
    './app/**/*.{js,ts,jsx,tsx,vue,svelte}',
  ],
  
  theme: {
    extend: {
      // Design system integration
      colors: {
        primary: {
          50: 'rgb(239 246 255)',
          100: 'rgb(219 234 254)',
          200: 'rgb(191 219 254)',
          300: 'rgb(147 197 253)',
          400: 'rgb(96 165 250)',
          500: 'rgb(59 130 246)',
          600: 'rgb(37 99 235)',
          700: 'rgb(29 78 216)',
          800: 'rgb(30 64 175)',
          900: 'rgb(30 58 138)',
          950: 'rgb(23 37 84)',
        },
        gray: colors.slate,
        success: colors.emerald,
        warning: colors.amber,
        error: colors.red,
      },
      
      fontFamily: {
        sans: [
          'Inter',
          'system-ui',
          '-apple-system',
          'BlinkMacSystemFont',
          '"Segoe UI"',
          'Roboto',
          '"Helvetica Neue"',
          'Arial',
          '"Noto Sans"',
          'sans-serif',
          '"Apple Color Emoji"',
          '"Segoe UI Emoji"',
          '"Segoe UI Symbol"',
          '"Noto Color Emoji"',
        ],
        mono: [
          '"SF Mono"',
          'Monaco',
          'Inconsolata',
          '"Roboto Mono"',
          '"Source Code Pro"',
          'Consolas',
          '"Liberation Mono"',
          '"Menlo"',
          'monospace',
        ],
      },
      
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
        '128': '32rem',
      },
      
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-in': 'slideIn 0.3s ease-out',
        'bounce-slow': 'bounce 2s infinite',
        'pulse-slow': 'pulse 3s infinite',
      },
      
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideIn: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
      
      // Container queries support
      screens: {
        'xs': '475px',
        '3xl': '1920px',
      },
      
      // Custom utilities
      typography: (theme) => ({
        DEFAULT: {
          css: {
            color: theme('colors.gray.700'),
            lineHeight: theme('lineHeight.relaxed'),
            fontSize: theme('fontSize.base'),
            fontFamily: theme('fontFamily.sans').join(', '),
          },
        },
      }),
    },
  },
  
  plugins: [
    // Typography plugin
    require('@tailwindcss/typography'),
    
    // Forms plugin
    require('@tailwindcss/forms')({
      strategy: 'class',
    }),
    
    // Container queries
    require('@tailwindcss/container-queries'),
    
    // Aspect ratio
    require('@tailwindcss/aspect-ratio'),
    
    // Custom component plugin
    plugin(function({ addComponents, addUtilities, theme }) {
      // Add component classes
      addComponents({
        '.btn': {
          padding: `${theme('spacing.2')} ${theme('spacing.4')}`,
          borderRadius: theme('borderRadius.md'),
          fontWeight: theme('fontWeight.medium'),
          display: 'inline-flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'all 0.2s ease',
          cursor: 'pointer',
          border: 'none',
          textDecoration: 'none',
          
          '&:focus-visible': {
            outline: `2px solid ${theme('colors.blue.500')}`,
            outlineOffset: '2px',
          },
          
          '&:disabled': {
            opacity: '0.6',
            cursor: 'not-allowed',
          },
        },
        
        '.btn-primary': {
          backgroundColor: theme('colors.primary.600'),
          color: theme('colors.white'),
          
          '&:hover:not(:disabled)': {
            backgroundColor: theme('colors.primary.700'),
          },
        },
        
        '.btn-secondary': {
          backgroundColor: theme('colors.gray.100'),
          color: theme('colors.gray.900'),
          
          '&:hover:not(:disabled)': {
            backgroundColor: theme('colors.gray.200'),
          },
        },
      });
      
      // Add utility classes
      addUtilities({
        '.text-balance': {
          textWrap: 'balance',
        },
        '.text-pretty': {
          textWrap: 'pretty',
        },
        '.scrollbar-none': {
          scrollbarWidth: 'none',
          '&::-webkit-scrollbar': {
            display: 'none',
          },
        },
      });
    }),
    
    // Accessibility plugin
    plugin(function({ addBase, theme }) {
      addBase({
        ':focus-visible': {
          outline: `2px solid ${theme('colors.blue.500')}`,
          outlineOffset: '2px',
          borderRadius: theme('borderRadius.sm'),
        },
        
        // Screen reader only
        '.sr-only': {
          position: 'absolute',
          width: '1px',
          height: '1px',
          padding: '0',
          margin: '-1px',
          overflow: 'hidden',
          clip: 'rect(0, 0, 0, 0)',
          whiteSpace: 'nowrap',
          border: '0',
        },
        
        // Respect user preferences
        '@media (prefers-reduced-motion: reduce)': {
          '*': {
            animationDuration: '0.01ms !important',
            animationIterationCount: '1 !important',
            transitionDuration: '0.01ms !important',
          },
        },
      });
    }),
  ],
  
  // Dark mode configuration
  darkMode: 'class',
  
  // Prefix for avoiding conflicts
  prefix: '',
  
  // Important strategy
  important: false,
  
  // Separator for variants
  separator: ':',
  
  // Core plugins to disable
  corePlugins: {
    preflight: true,
    container: false, // Using custom container
  },
};
```

### 10.3 ビルドツール統合

#### **Vite 設定例**
```javascript
/* ✅ Good: Vite + CSS 最適化設定 */

import { defineConfig } from 'vite';
import { resolve } from 'path';

export default defineConfig({
  css: {
    // PostCSS configuration
    postcss: './postcss.config.js',
    
    // CSS modules configuration
    modules: {
      localsConvention: 'camelCaseOnly',
      generateScopedName: '[name]__[local]___[hash:base64:5]',
    },
    
    // Preprocessor options
    preprocessorOptions: {
      scss: {
        // Make design tokens available globally
        additionalData: `
          @use "/src/styles/design-tokens" as *;
          @use "/src/styles/mixins" as *;
        `,
        
        // Modern Sass API
        api: 'modern-compiler',
        
        // Include paths
        includePaths: ['node_modules', 'src/styles'],
        
        // Silence deprecation warnings
        silenceDeprecations: ['legacy-js-api'],
      },
    },
    
    // Development CSS source maps
    devSourcemap: true,
  },
  
  build: {
    // CSS optimization
    css: {
      // Enable CSS code splitting
      codeSplit: true,
      
      // Inline CSS threshold (4KB)
      inlineLimit: 4096,
    },
    
    rollupOptions: {
      output: {
        // Separate CSS chunks by entry
        assetFileNames: (assetInfo) => {
          if (assetInfo.name.endsWith('.css')) {
            return 'css/[name]-[hash][extname]';
          }
          return 'assets/[name]-[hash][extname]';
        },
      },
    },
    
    // Enable CSS minification
    cssMinify: 'esbuild',
    
    // Source maps for production debugging
    sourcemap: process.env.NODE_ENV === 'development',
  },
  
  // Development server configuration
  server: {
    // HMR for CSS
    hmr: {
      overlay: true,
    },
  },
  
  // Plugin configuration
  plugins: [
    // CSS analysis plugin
    {
      name: 'css-analyzer',
      buildStart() {
        console.log('🎨 CSS build started');
      },
      generateBundle(options, bundle) {
        let totalCSSSize = 0;
        Object.keys(bundle).forEach(fileName => {
          if (fileName.endsWith('.css')) {
            const cssAsset = bundle[fileName];
            totalCSSSize += cssAsset.source.length;
            console.log(`📄 ${fileName}: ${(cssAsset.source.length / 1024).toFixed(2)}KB`);
          }
        });
        console.log(`📦 Total CSS size: ${(totalCSSSize / 1024).toFixed(2)}KB`);
      },
    },
  ],
  
  resolve: {
    alias: {
      '@styles': resolve(__dirname, 'src/styles'),
      '@components': resolve(__dirname, 'src/components'),
    },
  },
});
```

#### **Webpack 設定例**
```javascript
/* ✅ Good: Webpack + CSS 最適化設定 */

const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const PurgeCSSPlugin = require('purgecss-webpack-plugin');
const glob = require('glob');

const isProduction = process.env.NODE_ENV === 'production';

module.exports = {
  module: {
    rules: [
      // CSS/SCSS processing
      {
        test: /\.(css|scss)$/,
        use: [
          // Extract CSS in production
          isProduction ? MiniCssExtractPlugin.loader : 'style-loader',
          
          // CSS loader with modules support
          {
            loader: 'css-loader',
            options: {
              modules: {
                auto: /\.module\.(css|scss)$/,
                localIdentName: isProduction
                  ? '[hash:base64:8]'
                  : '[name]__[local]__[hash:base64:5]',
              },
              sourceMap: !isProduction,
              importLoaders: 2,
            },
          },
          
          // PostCSS processing
          {
            loader: 'postcss-loader',
            options: {
              sourceMap: !isProduction,
              postcssOptions: {
                config: path.resolve(__dirname, 'postcss.config.js'),
              },
            },
          },
          
          // Sass processing
          {
            loader: 'sass-loader',
            options: {
              sourceMap: !isProduction,
              implementation: require('sass'),
              additionalData: `
                @use "@/styles/design-tokens" as *;
                @use "@/styles/mixins" as *;
              `,
              sassOptions: {
                includePaths: [
                  path.resolve(__dirname, 'src/styles'),
                  path.resolve(__dirname, 'node_modules'),
                ],
                outputStyle: 'expanded',
              },
            },
          },
        ],
      },
    ],
  },
  
  plugins: [
    // Extract CSS to separate files
    ...(isProduction ? [
      new MiniCssExtractPlugin({
        filename: 'css/[name].[contenthash:8].css',
        chunkFilename: 'css/[name].[contenthash:8].chunk.css',
        ignoreOrder: false,
      }),
      
      // Remove unused CSS
      new PurgeCSSPlugin({
        paths: glob.sync(`${path.join(__dirname, 'src')}/**/*`, {
          nodir: true,
          ignore: ['**/node_modules/**'],
        }),
        
        // Safelist important classes
        safelist: {
          standard: [
            /^(html|body|#root|#__next)/,
            /^focus-visible$/,
            /^data-/,
            /^aria-/,
          ],
          deep: [/modal/, /dropdown/, /tooltip/],
          greedy: [/^swiper/, /^aos/],
        },
        
        // Custom extractors
        defaultExtractor: content => {
          const broadMatches = content.match(/[^<>"'`\\s]*[^<>"'`\\s:]/g) || [];
          const innerMatches = content.match(/[^<>"'`\\s.()]*[^<>"'`\\s.():]/g) || [];
          return broadMatches.concat(innerMatches);
        },
      }),
    ] : []),
  ],
  
  optimization: {
    minimizer: [
      // CSS minification
      ...(isProduction ? [
        new CssMinimizerPlugin({
          minimizerOptions: {
            preset: [
              'advanced',
              {
                discardComments: { removeAll: true },
                cssDeclarationSorter: false,
                reduceIdents: { keyframes: false },
                zindex: false,
              },
            ],
          },
        }),
      ] : []),
    ],
  },
  
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@styles': path.resolve(__dirname, 'src/styles'),
    },
  },
  
  // Development configuration
  devServer: {
    hot: true,
    liveReload: true,
  },
};
```

## 11. Devin実行ガイドライン

### 11.1 AI実装時の具体的指針

#### **CSS/SCSS開発のワークフロー**
```markdown
/* ✅ Good: Devin向けCSS開発ワークフロー */

## Phase 1: プロジェクト初期化
1. **設計システム構築**
   - デザイントークン定義（カラー、タイポグラフィ、スペーシング）
   - SCSS変数とCSS Custom Propertiesの統合設計
   - ブレークポイント戦略の決定（mobile-first推奨）

2. **アーキテクチャ設定**
   - ITCSS（Inverted Triangle CSS）階層構造の実装
   - @layer directive を使用した詳細度管理
   - ファイル命名規則とディレクトリ構造の確立

3. **ツールチェーン設定**
   - Stylelint設定ファイルの実装
   - PostCSS pipeline構築
   - ビルドツール（Vite/Webpack）CSS最適化設定

## Phase 2: コンポーネント開発
1. **BEM命名規則の厳密適用**
   - Block: .component-name
   - Element: .component-name__element
   - Modifier: .component-name--modifier

2. **SCSS Mixin・Function活用**
   - 再利用可能なMixin定義
   - 型安全な関数実装
   - デザイントークンアクセス関数

3. **レスポンシブ実装**
   - Container Queries優先（適用可能な場合）
   - Fluid Typography（clamp()使用）
   - モバイルファースト Media Queries

## Phase 3: 最適化・品質保証
1. **パフォーマンス最適化**
   - Critical CSS抽出
   - CSS Containment適用
   - 不要CSS除去（PurgeCSS）

2. **アクセシビリティ確保**
   - Focus management implementation
   - Color contrast validation (WCAG AA準拠)
   - Screen reader対応

3. **品質チェック**
   - Stylelint自動修正実行
   - CSS bundle size monitoring
   - Cross-browser testing
```

#### **実装時の必須チェックリスト**
```markdown
/* ✅ Devin実装チェックリスト */

## 🎨 Design System Integration
- [ ] CSS Custom Properties for theming
- [ ] Consistent spacing scale (8px base unit)
- [ ] Typography scale implementation
- [ ] Color system with semantic tokens
- [ ] Z-index management system

## 📱 Responsive Design
- [ ] Mobile-first approach
- [ ] Container Queries where applicable
- [ ] Fluid typography with clamp()
- [ ] Flexible grid systems
- [ ] Touch-friendly interactive elements (min 44px)

## ♿ Accessibility
- [ ] Focus-visible implementation
- [ ] Color contrast verification (4.5:1 minimum)
- [ ] Screen reader compatibility
- [ ] Keyboard navigation support
- [ ] Reduced motion preferences

## ⚡ Performance
- [ ] Critical CSS identified and inlined
- [ ] CSS Containment applied
- [ ] Animation performance optimized
- [ ] Bundle size under target limits
- [ ] Unused CSS removed

## 🔧 Code Quality
- [ ] BEM naming convention
- [ ] SCSS best practices
- [ ] Proper nesting depth (max 3 levels)
- [ ] Consistent property ordering
- [ ] No duplicate selectors

## 🛠️ Tooling
- [ ] Stylelint configuration
- [ ] PostCSS optimization
- [ ] Build tool integration
- [ ] Development workflow setup
- [ ] Documentation generation
```

### 11.2 コード生成パターン

#### **SCSS Component Template**
```scss
/* ✅ Devin用 SCSSコンポーネントテンプレート */

// =================================
// Component: [COMPONENT_NAME]
// =================================
// Description: [COMPONENT_DESCRIPTION]
// Dependencies: [LIST_DEPENDENCIES]
// Usage: [USAGE_EXAMPLES]

@use '../design-tokens' as *;
@use '../mixins' as *;

// Component variables
$component-defaults: (
  padding: spacing(4),
  border-radius: radius(md),
  background: color(surface-primary),
  transition: duration(base) ease(out)
) !default;

// Main component block
.component-name {
  // Position & Display
  position: relative;
  display: flex;
  flex-direction: column;
  
  // Box Model
  padding: map-get($component-defaults, padding);
  border-radius: map-get($component-defaults, border-radius);
  
  // Visual
  background-color: map-get($component-defaults, background);
  
  // Animation
  transition: all map-get($component-defaults, transition);
  
  // Performance
  contain: layout style;
  
  // Elements
  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: spacing(3);
    
    .title {
      @include typography('heading', 'lg');
      color: color(text-primary);
    }
    
    .actions {
      display: flex;
      gap: spacing(2);
    }
  }
  
  &__body {
    flex: 1;
    @include typography('body', 'base');
    color: color(text-secondary);
    line-height: line-height(relaxed);
  }
  
  &__footer {
    margin-top: spacing(4);
    padding-top: spacing(3);
    border-top: 1px solid color(border-light);
  }
  
  // Modifiers
  &--compact {
    padding: spacing(2);
    
    .component-name__header {
      margin-bottom: spacing(2);
    }
  }
  
  &--elevated {
    @include elevation(2);
    
    &:hover {
      @include elevation(3);
    }
  }
  
  // States
  &.is-loading {
    opacity: 0.7;
    pointer-events: none;
    
    &::after {
      @include loading-spinner(center);
    }
  }
  
  &.has-error {
    border-color: color(status-error);
    background-color: color(status-error-bg);
  }
  
  // Responsive behavior
  @include media(md) {
    padding: spacing(6);
    
    &__header {
      margin-bottom: spacing(4);
    }
  }
  
  // Dark theme support
  @media (prefers-color-scheme: dark) {
    background-color: color(surface-dark);
    color: color(text-dark);
  }
  
  // Accessibility
  &:focus-within {
    outline: 2px solid color(focus);
    outline-offset: 2px;
  }
  
  // Print styles
  @media print {
    background: white !important;
    color: black !important;
    box-shadow: none !important;
  }
}

// Contextual variations
.sidebar .component-name {
  &__header .title {
    @include typography('heading', 'md');
  }
}

.modal .component-name {
  max-height: 80vh;
  overflow-y: auto;
}
```

#### **CSS Utility Generation Pattern**
```scss
/* ✅ Devin用 ユーティリティ生成パターン */

// =================================
// Utility Generator Mixins
// =================================

// Spacing utilities generator
@mixin generate-spacing-utilities($properties: (margin, padding), $breakpoints: $breakpoints) {
  @each $property in $properties {
    $prefix: str-slice($property, 1, 1); // m or p
    
    // Generate base utilities
    @each $name, $value in $spacing {
      .u-#{$prefix}-#{$name} { #{$property}: $value !important; }
      .u-#{$prefix}t-#{$name} { #{$property}-top: $value !important; }
      .u-#{$prefix}r-#{$name} { #{$property}-right: $value !important; }
      .u-#{$prefix}b-#{$name} { #{$property}-bottom: $value !important; }
      .u-#{$prefix}l-#{$name} { #{$property}-left: $value !important; }
      .u-#{$prefix}x-#{$name} { 
        #{$property}-left: $value !important;
        #{$property}-right: $value !important;
      }
      .u-#{$prefix}y-#{$name} { 
        #{$property}-top: $value !important;
        #{$property}-bottom: $value !important;
      }
    }
    
    // Generate responsive utilities
    @each $breakpoint-name, $breakpoint-value in $breakpoints {
      @if $breakpoint-value > 0 {
        @include media($breakpoint-name) {
          @each $name, $value in $spacing {
            .u-#{$breakpoint-name}\:#{$prefix}-#{$name} { #{$property}: $value !important; }
            .u-#{$breakpoint-name}\:#{$prefix}t-#{$name} { #{$property}-top: $value !important; }
            .u-#{$breakpoint-name}\:#{$prefix}r-#{$name} { #{$property}-right: $value !important; }
            .u-#{$breakpoint-name}\:#{$prefix}b-#{$name} { #{$property}-bottom: $value !important; }
            .u-#{$breakpoint-name}\:#{$prefix}l-#{$name} { #{$property}-left: $value !important; }
          }
        }
      }
    }
  }
}

// Typography utilities generator
@mixin generate-typography-utilities($breakpoints: $breakpoints) {
  // Font sizes
  @each $name, $value in $font-sizes {
    .u-text-#{$name} {
      font-size: $value !important;
      line-height: map-get($line-heights, $name, line-height(base)) !important;
    }
  }
  
  // Font weights
  @each $name, $value in $font-weights {
    .u-font-#{$name} { font-weight: $value !important; }
  }
  
  // Text alignment
  $alignments: (left, center, right, justify);
  @each $alignment in $alignments {
    .u-text-#{$alignment} { text-align: $alignment !important; }
    
    // Responsive text alignment
    @each $breakpoint-name, $breakpoint-value in $breakpoints {
      @if $breakpoint-value > 0 {
        @include media($breakpoint-name) {
          .u-#{$breakpoint-name}\:text-#{$alignment} { 
            text-align: $alignment !important; 
          }
        }
      }
    }
  }
}

// Color utilities generator
@mixin generate-color-utilities($colors: $colors) {
  @each $name, $value in $colors {
    .u-text-#{$name} { color: $value !important; }
    .u-bg-#{$name} { background-color: $value !important; }
    .u-border-#{$name} { border-color: $value !important; }
  }
}

// Generate all utilities
@include generate-spacing-utilities();
@include generate-typography-utilities();
@include generate-color-utilities();
```

### 11.3 トラブルシューティング

#### **よくある問題と解決方法**
```markdown
/* ✅ Devin向け トラブルシューティングガイド */

## 🚨 Common Issues & Solutions

### 1. Specificity Problems
**問題**: CSS規則が期待通りに適用されない
**原因**: セレクタの詳細度が不適切
**解決方法**:
```scss
// ❌ Bad: High specificity
#content .sidebar ul.nav li a.active { }

// ✅ Good: Lower specificity with @layer
@layer components {
  .nav-link.is-active { }
}

// ✅ Good: BEM approach
.nav__link--active { }
```

### 2. Layout Shift Issues
**問題**: コンテンツ読み込み時のレイアウトシフト
**原因**: 画像・フォントのサイズ予約不足
**解決方法**:
```css
/* Prevent CLS with aspect ratios */
.image-container {
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

/* Reserve font space */
.text-content {
  font-display: swap;
  min-height: 1.5em; /* Reserve line height */
}

/* Skeleton loading states */
.loading-skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}
```

### 3. Performance Issues
**問題**: CSS bundle size過大
**原因**: 未使用CSS、非効率なセレクタ
**解決方法**:
```javascript
// PurgeCSS configuration
module.exports = {
  content: ['./src/**/*.{html,js,jsx,ts,tsx}'],
  safelist: {
    standard: [/^focus-visible$/, /^data-/, /^aria-/],
    deep: [/modal/, /dropdown/],
    greedy: [/^swiper/]
  }
}
```

### 4. Browser Compatibility
**問題**: モダンCSS機能の非対応
**原因**: ブラウザサポート不足
**解決方法**:
```css
/* Feature queries for graceful degradation */
.grid-container {
  display: flex;
  flex-wrap: wrap;
}

@supports (display: grid) {
  .grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}

/* Subgrid fallback */
@supports not (grid-template-columns: subgrid) {
  .card-grid {
    display: flex;
    flex-direction: column;
  }
}
```

### 5. Accessibility Issues
**問題**: フォーカス管理・色彩対比不足
**原因**: a11y要件の見落とし
**解決方法**:
```css
/* Enhanced focus management */
:focus-visible {
  outline: 2px solid var(--focus-color);
  outline-offset: 2px;
}

/* Color contrast validation */
:root {
  --text-primary: #111827;    /* 16.25:1 on white */
  --text-secondary: #4b5563;  /* 7.22:1 on white */
  --link-color: #1d4ed8;      /* 6.24:1 on white */
}

/* High contrast mode */
@media (prefers-contrast: high) {
  :root {
    --text-primary: #000000;
    --text-secondary: #000000;
    --link-color: #0000ee;
  }
}
```

## 🔧 Debugging Techniques

### 1. CSS Grid Inspector
```css
/* Temporary grid visualization */
.debug-grid {
  background-image: 
    linear-gradient(rgba(255, 0, 0, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 0, 0, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
}
```

### 2. Performance Profiling
```css
/* Identify expensive operations */
.perf-expensive {
  outline: 2px solid red;
  outline-offset: -2px;
}

/* Mark reflow-causing elements */
.perf-reflow {
  outline: 2px solid orange;
}
```

### 3. Responsive Testing
```scss
/* Breakpoint indicators (development only) */
@if $env == 'development' {
  body::before {
    content: 'XS';
    position: fixed;
    top: 0;
    right: 0;
    padding: 0.5rem;
    background: red;
    color: white;
    z-index: 9999;
    
    @include media(sm) { content: 'SM'; background: orange; }
    @include media(md) { content: 'MD'; background: yellow; }
    @include media(lg) { content: 'LG'; background: green; }
    @include media(xl) { content: 'XL'; background: blue; }
  }
}
```
```

### 11.4 継続的改善プロセス

#### **Code Review Checklist**
```markdown
/* ✅ Devin CSS Code Review Checklist */

## 📋 Review Criteria

### Architecture & Organization
- [ ] Follows ITCSS layered architecture
- [ ] Proper @layer usage for specificity management
- [ ] Consistent file naming and organization
- [ ] Appropriate SCSS partials structure

### Code Quality
- [ ] BEM naming convention adherence
- [ ] Proper nesting depth (≤3 levels)
- [ ] Consistent property ordering
- [ ] No duplicate or conflicting rules
- [ ] Meaningful variable and mixin names

### Performance Optimization
- [ ] CSS Containment where applicable
- [ ] Efficient selectors (avoid complex combinators)
- [ ] Minimal specificity required
- [ ] No !important overuse
- [ ] Optimized animations (transform/opacity only)

### Responsive Design
- [ ] Mobile-first implementation
- [ ] Container Queries utilization
- [ ] Fluid typography with clamp()
- [ ] Appropriate breakpoint strategy
- [ ] Touch-friendly interaction areas

### Accessibility Compliance
- [ ] WCAG AA color contrast ratios
- [ ] Focus-visible implementation
- [ ] Screen reader compatibility
- [ ] Keyboard navigation support
- [ ] Motion preference respect

### Browser Compatibility
- [ ] Feature queries for new CSS features
- [ ] Appropriate fallbacks provided
- [ ] Vendor prefix handling (via autoprefixer)
- [ ] Progressive enhancement approach

## 🔄 Improvement Process

### Weekly Performance Audit
1. **Bundle Size Analysis**
   - CSS file size monitoring
   - Unused rule identification
   - Critical CSS optimization

2. **Runtime Performance**
   - Paint/layout thrashing detection
   - Animation performance profiling
   - Containment effectiveness review

3. **Accessibility Testing**
   - Automated a11y tool runs
   - Manual keyboard navigation
   - Screen reader compatibility

### Monthly Architecture Review
1. **Design System Evolution**
   - Token usage analysis
   - Component API consistency
   - Cross-project standardization

2. **Technical Debt Assessment**
   - Legacy code identification
   - Refactoring opportunities
   - Tooling updates needed

3. **Documentation Maintenance**
   - Style guide updates
   - Example code refresh
   - Best practice evolution
```

---

## 📚 参考リソース・関連ドキュメント

### 仕様・標準
- [CSS Specifications (W3C)](https://www.w3.org/Style/CSS/)
- [MDN CSS Reference](https://developer.mozilla.org/en-US/docs/Web/CSS)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)

### 設計手法・アーキテクチャ
- [BEM Methodology](https://bem.info/)
- [ITCSS Architecture](https://itcss.io/)
- [CSS Architecture for Design Systems](https://bradfrost.com/blog/post/css-architecture-for-design-systems/)

### パフォーマンス・最適化
- [CSS Containment Specification](https://www.w3.org/TR/css-contain-1/)
- [Critical CSS Tools](https://github.com/addyosmani/critical)
- [Web Performance Working Group](https://www.w3.org/webperf/)

### ツール・自動化
- [Stylelint Rules](https://stylelint.io/user-guide/rules/list)
- [PostCSS Plugins](https://github.com/postcss/postcss/blob/main/docs/plugins.md)
- [Sass Documentation](https://sass-lang.com/documentation)

---

**文書バージョン**: 1.0.0  
**最終更新**: 2024-10-15  
**次回レビュー予定**: 2024-11-15  
**メンテナー**: Development Team

**このドキュメントは組織のCSS/SCSS開発標準を定義し、Devin（AI開発者）による実装時の具体的指針を提供します。継続的な改善と更新により、最新のベストプラクティスを反映していきます。**
