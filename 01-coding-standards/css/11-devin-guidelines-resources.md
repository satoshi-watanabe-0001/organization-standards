# CSS/SCSS Devin実行ガイド・リソース

**このドキュメントについて**: CSS/SCSS コーディング規約 - Devin実行ガイド・リソース

---

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
# CSS/SCSS ドキュメンテーション標準追加セクション

---

