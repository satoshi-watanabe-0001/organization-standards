# CSS/SCSS モダンCSS機能

**このドキュメントについて**: CSS/SCSS コーディング規約 - モダンCSS機能

---

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
