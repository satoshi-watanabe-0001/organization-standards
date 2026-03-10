# CSS/SCSS SCSS: ネスト・関数

**このドキュメントについて**: CSS/SCSS コーディング規約 - SCSS: ネスト・関数

---

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

