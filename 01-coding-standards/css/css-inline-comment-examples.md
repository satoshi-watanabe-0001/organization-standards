# CSS/SCSS インラインコメント実装例

## 📖 概要

本ドキュメントは、CSS/SCSS固有のインラインコメント記述の具体例を提供します。

**共通原則**: [00-inline-comment-standards.md](../00-inline-comment-standards.md)を必ず参照してください。

---

## 🎯 CSS/SCSS特有のコメント場面

### 1. マジックナンバー（数値の根拠）

```css
/* ❌ 悪い例: 数値の意味が不明 */
.button {
  padding: 12px 24px;
  border-radius: 4px;
}

/* ✅ 良い例: 数値の選定理由を説明 */
.button {
  /* パディングの設定根拠:
     縦12px: タッチターゲットの最小サイズ44pxを確保するため（iOS HIG準拠）
     横24px: 視覚的なバランスを取るため（縦の2倍）*/
  padding: 12px 24px;
  
  /* 角丸4px: デザインシステムの標準値
     理由: ブランドガイドラインで定義された統一値 */
  border-radius: 4px;
}
```

---

### 2. !important の使用理由

```css
/* ❌ 悪い例: !important を理由なく使用 */
.button {
  color: red !important;
}

/* ✅ 良い例: !important の必要性を説明 */
.button {
  /* !important を使用する理由:
     サードパーティライブラリ（Bootstrap）のスタイルを上書きするため
     ライブラリの詳細度が非常に高く（.btn.btn-primary.btn-lg）、
     通常のセレクタでは上書きできない
     
     TODO: 将来的にはライブラリをカスタムビルドして、!important を削除 */
  color: red !important;
}
```

---

### 3. z-index の管理

```css
/* ❌ 悪い例: z-index の値が恣意的 */
.header { z-index: 999; }
.modal { z-index: 9999; }
.tooltip { z-index: 99999; }

/* ✅ 良い例: z-index の階層構造を明示 */
/* === z-index 階層管理 ===
 * レベル0 (1-99): 通常コンテンツ
 * レベル1 (100-199): ドロップダウン、ポップオーバー
 * レベル2 (200-299): 固定ヘッダー、サイドバー
 * レベル3 (300-399): モーダルオーバーレイ
 * レベル4 (400-499): ツールチップ、トースト通知
 * レベル5 (500+): デバッグ用オーバーレイ
 */

.header {
  /* レベル2: 固定ヘッダー
     理由: ページスクロール時も常に表示する必要がある */
  z-index: 200;
}

.modal-overlay {
  /* レベル3: モーダルオーバーレイ
     理由: ヘッダーより上に表示し、背景をブロックする */
  z-index: 300;
}

.tooltip {
  /* レベル4: ツールチップ
     理由: モーダル内でも表示される必要がある */
  z-index: 400;
}
```

---

### 4. ブラウザハック・ベンダープレフィックス

```css
/* ❌ 悪い例: ハックの理由が不明 */
.button {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

/* ✅ 良い例: ベンダープレフィックスの必要性を説明 */
.button {
  /* Flexbox のベンダープレフィックス
     理由: IE11 サポートのため（-ms-flexbox）
     対象ブラウザ: IE11（2025年末まで企業向けサポート継続）
     
     Autoprefixer で自動付与されるため、手動で記述不要
     TODO: IE11サポート終了後（2026年1月）に削除 */
  display: -webkit-box;  /* Safari 8以前 */
  display: -ms-flexbox;  /* IE11 */
  display: flex;         /* 標準 */
}
```

---

## 🎨 レスポンシブデザインのコメント

### 1. ブレークポイントの定義

```scss
/* ❌ 悪い例: ブレークポイントの根拠が不明 */
@media (max-width: 768px) {
  .container { padding: 10px; }
}

/* ✅ 良い例: ブレークポイントの選定理由を説明 */
/* === ブレークポイント定義 ===
 * 
 * Mobile: 〜575px
 * - 対象デバイス: iPhone SE, 小型スマートフォン
 * - ビューポート: 320px〜575px
 * 
 * Tablet: 576px〜991px
 * - 対象デバイス: iPad Mini, タブレット端末
 * - ビューポート: 576px〜991px
 * 
 * Desktop: 992px〜
 * - 対象デバイス: PC、大型タブレット
 * - ビューポート: 992px以上
 * 
 * ブレークポイントの根拠:
 * - Bootstrap 5 の標準値に準拠（チーム内の統一基準）
 * - Google Analytics のデバイス統計に基づく最適化
 */

// Mobile（〜575px）
@media (max-width: 575px) {
  .container {
    /* モバイルでは左右余白を最小化
       理由: 画面幅が限られるため、コンテンツ領域を最大化 */
    padding: 10px;
  }
}

// Tablet（576px〜991px）
@media (min-width: 576px) and (max-width: 991px) {
  .container {
    /* タブレットでは適度な余白を確保
       理由: 読みやすさとタッチ操作性のバランスを取る */
    padding: 20px;
  }
}

// Desktop（992px〜）
@media (min-width: 992px) {
  .container {
    /* デスクトップでは余白を十分に確保
       理由: 大画面での視認性向上、コンテンツの散漫化を防ぐ */
    padding: 40px;
    max-width: 1200px;  /* 最大幅制限で可読性を維持 */
  }
}
```

---

## 🔧 パフォーマンス最適化のコメント

### 1. アニメーションの最適化

```css
/* ❌ 悪い例: アニメーションプロパティの選定理由が不明 */
.button {
  transition: all 0.3s;
}

/* ✅ 良い例: 最適化の意図を説明 */
.button {
  /* アニメーション最適化:
     all ではなく、変化するプロパティのみを指定
     理由: 
     - all を使うと、すべてのプロパティ変化を監視するためパフォーマンスが悪化
     - background-color と transform のみがアニメーションするため、明示的に指定
     
     transform を使用する理由:
     - GPU アクセラレーションが有効になり、60fps を維持
     - left/top を使うとレイアウト再計算が発生し、30fps に低下
     
     duration 0.3s の根拠:
     - ユーザーの認知に必要な最小時間（200ms）より少し長め
     - 0.5s 以上だと「遅い」と感じられるため、0.3s を採用 */
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.button:hover {
  /* transform を使用（left/top ではなく）
     理由: GPU レイヤーで処理され、再描画が発生しない */
  transform: translateY(-2px);
}
```

---

### 2. リフロー・リペイント対策

```css
/* ✅ 良い例: リフロー回避の説明 */
.tooltip {
  /* position: absolute を使用する理由:
     - 通常フローから外すことで、表示/非表示時のリフローを防ぐ
     - ツールチップは頻繁に表示/非表示が切り替わるため、
       リフローが発生すると全体のパフォーマンスが悪化する
     
     will-change の使用:
     - ブラウザに事前に変化を通知し、最適化を促す
     - opacity と transform のみ指定（GPU レイヤーに昇格）*/
  position: absolute;
  will-change: opacity, transform;
  
  /* opacity でフェードイン/アウト
     理由: visibility や display と異なり、リペイントのみで済む */
  opacity: 0;
  transition: opacity 0.2s ease;
}

.tooltip.visible {
  opacity: 1;
}
```

---

## 🧩 SCSS特有のコメント

### 1. Mixin の使用

```scss
/* ❌ 悪い例: Mixin の目的が不明 */
@mixin button-style {
  padding: 10px 20px;
  border-radius: 4px;
}

/* ✅ 良い例: Mixin の目的と使用場面を説明 */
/**
 * ボタンの基本スタイル Mixin
 * 
 * 目的:
 * - プライマリ、セカンダリ、危険ボタンなど、
 *   複数のボタンバリアントで共通するスタイルを一元管理
 * 
 * 使用場面:
 * - .btn-primary, .btn-secondary, .btn-danger で使用
 * 
 * パラメータなしの理由:
 * - 基本スタイルは固定値（デザインシステム準拠）
 * - 色やサイズのバリエーションは、呼び出し側で個別に指定
 */
@mixin button-base {
  /* タッチターゲット最小サイズを確保（iOS HIG準拠） */
  padding: 12px 24px;
  
  /* デザインシステムの標準角丸 */
  border-radius: 4px;
  
  /* フォント設定 */
  font-size: 16px;
  font-weight: 600;
  
  /* アクセシビリティ対応 */
  cursor: pointer;
  user-select: none;
  
  /* アニメーション（パフォーマンス最適化済み） */
  transition: background-color 0.2s ease, transform 0.1s ease;
}
```

---

### 2. 変数の定義

```scss
/* ❌ 悪い例: 変数の意味が不明 */
$color-1: #007bff;
$color-2: #6c757d;

/* ✅ 良い例: 変数の役割と選定理由を説明 */
/**
 * === カラーパレット定義 ===
 * 
 * ブランドカラーの選定根拠:
 * - プライマリカラー: 企業ロゴの青色（#007bff）
 * - セカンダリカラー: プライマリとの対比を考慮したグレー
 * - アクセントカラー: CTA（行動喚起）用の目立つオレンジ
 * 
 * アクセシビリティ検証済み:
 * - すべての色の組み合わせで WCAG AA 基準を満たす
 * - コントラスト比 4.5:1 以上（プライマリ/白: 5.2:1）
 */

/* プライマリカラー: ブランドの主要色 */
$color-primary: #007bff;

/* セカンダリカラー: サブ要素、無効状態の表現 */
$color-secondary: #6c757d;

/* 成功色: 完了メッセージ、成功状態 */
$color-success: #28a745;

/* 警告色: 注意喚起、確認が必要な状態 */
$color-warning: #ffc107;

/* 危険色: エラーメッセージ、削除ボタン */
$color-danger: #dc3545;

/* グレースケール */
$color-text-primary: #212529;    // 本文テキスト
$color-text-secondary: #6c757d;  // 補足テキスト
$color-border: #dee2e6;          // 境界線
$color-background: #f8f9fa;      // 背景色
```

---

## 🚫 避けるべきパターン

### 1. コードの繰り返し

```css
/* ❌ 悪い例: CSSをそのまま日本語に翻訳しただけ */
/* 幅を100%にする */
.container { width: 100%; }

/* ✅ 良い例: WHYを説明 */
.container {
  /* コンテナを親要素の幅いっぱいに広げる
     理由: レスポンシブデザインで、画面幅に応じて自動調整するため */
  width: 100%;
}
```

---

## ✅ レビューチェックリスト

CSS/SCSSコードレビュー時に確認:

- [ ] マジックナンバーに根拠が記載されている
- [ ] !importantの使用理由が説明されている
- [ ] z-indexの階層構造が明確である
- [ ] ブレークポイントの選定理由がある
- [ ] アニメーションのパフォーマンス最適化が説明されている
- [ ] すべてのコメントが日本語で記述されている

---

## 🔗 関連ドキュメント

- [00-inline-comment-standards.md](../00-inline-comment-standards.md) - 共通原則
- [css-coding-standards.md](css-coding-standards.md) - CSS/SCSS全体のコーディング規約
