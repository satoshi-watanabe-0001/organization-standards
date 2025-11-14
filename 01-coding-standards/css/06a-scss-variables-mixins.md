# CSS/SCSS SCSS: 変数・Mixin

**このドキュメントについて**: CSS/SCSS コーディング規約 - SCSS: 変数・Mixin

---

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

