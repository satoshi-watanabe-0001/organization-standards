# CSS/SCSS レスポンシブデザイン

**このドキュメントについて**: CSS/SCSS コーディング規約 - レスポンシブデザイン

---

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

