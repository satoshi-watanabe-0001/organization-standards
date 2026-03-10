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

