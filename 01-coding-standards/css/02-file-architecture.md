# CSS/SCSS ファイル構成・アーキテクチャ

**このドキュメントについて**: CSS/SCSS コーディング規約 - ファイル構成・アーキテクチャ

---

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

