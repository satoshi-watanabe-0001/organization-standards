# CSS/SCSS 命名規則・BEM実装

**このドキュメントについて**: CSS/SCSS コーディング規約 - 命名規則・BEM実装

---

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

