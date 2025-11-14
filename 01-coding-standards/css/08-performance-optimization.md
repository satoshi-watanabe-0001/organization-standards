# CSS/SCSS パフォーマンス最適化

**このドキュメントについて**: CSS/SCSS コーディング規約 - パフォーマンス最適化

---

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

