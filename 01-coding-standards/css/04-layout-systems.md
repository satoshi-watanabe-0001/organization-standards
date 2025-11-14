# CSS/SCSS レイアウトシステム

**このドキュメントについて**: CSS/SCSS コーディング規約 - レイアウトシステム

---

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

