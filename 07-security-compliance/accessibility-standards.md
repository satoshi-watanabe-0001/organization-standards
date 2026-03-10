# アクセシビリティ標準

**最終更新**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発チーム全体

---

## 📖 概要

このドキュメントは、**Webアクセシビリティ**の標準を定義します。すべてのユーザー（障害のある方を含む）が、製品・サービスを平等に利用できるよう、WCAG（Web Content Accessibility Guidelines）に基づいた実装を行います。

**目的**:
- すべてのユーザーに公平なアクセス機会を提供
- 法的コンプライアンスの遵守（ADA、Section 508、欧州アクセシビリティ法等）
- ユーザーエクスペリエンスの向上
- SEOの改善

---

## 🎯 WCAG 2.1準拠レベル

### レベルA（最低限）
- **必須**: 基本的なアクセシビリティ要件
- すべてのプロジェクトで達成必須

### レベルAA（推奨）
- **目標**: 中程度のアクセシビリティ要件
- ほとんどの公共サービス・企業サイトの標準
- **当組織の標準レベル**

### レベルAAA（最高）
- **理想**: 高度なアクセシビリティ要件
- 専門性の高いサービスで推奨

---

## 📏 WCAG 4つの原則（POUR）

### 1. 知覚可能（Perceivable）

ユーザーがコンテンツを知覚できること

#### 1.1 代替テキスト

**要件**: すべての非テキストコンテンツに代替テキストを提供

```html
<!-- ✅ 良い例 -->
<img src="chart.png" alt="2024年第1四半期の売上推移グラフ。1月から3月にかけて15%増加">

<button aria-label="メニューを開く">
  <span class="icon-menu"></span>
</button>

<!-- ❌ 悪い例 -->
<img src="chart.png" alt="画像">
<button><span class="icon-menu"></span></button>
```

**装飾画像の場合**:
```html
<img src="decorative.png" alt="" role="presentation">
<!-- または -->
<div style="background-image: url('decorative.png')" role="presentation"></div>
```

#### 1.2 時間依存メディア

**要件**: 音声・動画コンテンツに代替手段を提供

```html
<!-- 動画に字幕とトランスクリプトを提供 -->
<video controls>
  <source src="video.mp4" type="video/mp4">
  <track kind="captions" src="captions-ja.vtt" srclang="ja" label="日本語">
  <track kind="captions" src="captions-en.vtt" srclang="en" label="English">
</video>

<details>
  <summary>動画のトランスクリプト</summary>
  <p>この動画では...</p>
</details>
```

#### 1.3 適応可能

**要件**: 情報と構造を失わずに異なる方法で提示可能

```html
<!-- セマンティックHTML使用 -->
<article>
  <header>
    <h1>記事タイトル</h1>
    <p>投稿日: <time datetime="2024-10-27">2024年10月27日</time></p>
  </header>
  <main>
    <p>記事本文...</p>
  </main>
  <footer>
    <p>著者: 山田太郎</p>
  </footer>
</article>

<!-- ❌ 悪い例 -->
<div class="article">
  <div class="title">記事タイトル</div>
  <div class="date">2024年10月27日</div>
  <div class="content">記事本文...</div>
</div>
```

**フォームラベル**:
```html
<!-- ✅ 良い例 -->
<label for="email">メールアドレス</label>
<input type="email" id="email" name="email" required>

<!-- 複雑なフォーム -->
<fieldset>
  <legend>配送先住所</legend>
  <label for="postal-code">郵便番号</label>
  <input type="text" id="postal-code" name="postal-code">
  
  <label for="address">住所</label>
  <input type="text" id="address" name="address">
</fieldset>
```

#### 1.4 判別可能

**要件**: コンテンツを見やすく、聞きやすくする

**色のコントラスト比（WCAG AA）**:
- 通常テキスト: 4.5:1以上
- 大きなテキスト（18pt以上または14pt太字以上）: 3:1以上

```css
/* ✅ 良い例 - コントラスト比 7:1 */
.text {
  color: #000000;
  background-color: #FFFFFF;
}

/* ❌ 悪い例 - コントラスト比 2.5:1（不十分） */
.text-bad {
  color: #777777;
  background-color: #FFFFFF;
}
```

**色だけに依存しない**:
```html
<!-- ❌ 悪い例 - 色のみで状態を表現 -->
<span style="color: red;">エラー</span>

<!-- ✅ 良い例 - アイコンとテキストで状態を表現 -->
<span class="error">
  <svg aria-hidden="true"><use href="#icon-error"></use></svg>
  エラー: メールアドレスの形式が正しくありません
</span>
```

---

### 2. 操作可能（Operable）

ユーザーがインターフェースを操作できること

#### 2.1 キーボードアクセシブル

**要件**: すべての機能をキーボードで操作可能

```typescript
// カスタムドロップダウンのキーボード対応
const Dropdown: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(0);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    switch (e.key) {
      case 'Enter':
      case ' ':
        setIsOpen(!isOpen);
        break;
      case 'Escape':
        setIsOpen(false);
        break;
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex((prev) => Math.min(prev + 1, items.length - 1));
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex((prev) => Math.max(prev - 1, 0));
        break;
    }
  };

  return (
    <div role="combobox" aria-expanded={isOpen} onKeyDown={handleKeyDown} tabIndex={0}>
      {/* ... */}
    </div>
  );
};
```

**フォーカス管理**:
```css
/* ✅ 良い例 - フォーカスインジケーターを明確に */
:focus {
  outline: 2px solid #0066CC;
  outline-offset: 2px;
}

/* ❌ 悪い例 - フォーカスを完全に削除 */
:focus {
  outline: none; /* 絶対に避ける */
}
```

#### 2.2 十分な時間

**要件**: ユーザーにコンテンツを読み、操作する十分な時間を提供

```typescript
// セッションタイムアウト警告
const SessionTimeout: React.FC = () => {
  const [timeLeft, setTimeLeft] = useState(300); // 5分

  useEffect(() => {
    if (timeLeft === 60) {
      // 1分前に警告を表示
      showWarning('セッションが1分後に切れます。延長しますか？');
    }
  }, [timeLeft]);

  return (
    <div role="alert" aria-live="polite">
      セッション残り時間: {timeLeft}秒
      <button onClick={extendSession}>延長する</button>
    </div>
  );
};
```

#### 2.3 発作の防止

**要件**: 発作を引き起こす可能性のあるコンテンツを避ける

```css
/* ❌ 危険 - 高速点滅（避ける） */
@keyframes dangerous-flash {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
.flashing {
  animation: dangerous-flash 0.1s infinite; /* 1秒間に10回以上の点滅は危険 */
}

/* ✅ 安全 - 緩やかなフェード */
@keyframes safe-fade {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}
.pulsing {
  animation: safe-fade 2s ease-in-out infinite;
}
```

#### 2.4 ナビゲーション可能

**要件**: ユーザーがコンテンツを見つけ、ナビゲートできる

```html
<!-- ✅ スキップリンク -->
<a href="#main-content" class="skip-link">メインコンテンツへスキップ</a>

<nav aria-label="メインナビゲーション">
  <ul>
    <li><a href="/">ホーム</a></li>
    <li><a href="/about">会社概要</a></li>
    <li><a href="/contact">お問い合わせ</a></li>
  </ul>
</nav>

<main id="main-content">
  <h1>ページタイトル</h1>
  <!-- コンテンツ -->
</main>
```

**パンくずリスト**:
```html
<nav aria-label="パンくずリスト">
  <ol>
    <li><a href="/">ホーム</a></li>
    <li><a href="/products">製品</a></li>
    <li aria-current="page">製品A</li>
  </ol>
</nav>
```

---

### 3. 理解可能（Understandable）

情報とインターフェースの操作方法が理解できること

#### 3.1 読みやすさ

**要件**: テキストを読みやすく、理解可能にする

```html
<!-- 言語の指定 -->
<html lang="ja">
  <body>
    <p>これは日本語のコンテンツです。</p>
    <p lang="en">This is English content.</p>
  </body>
</html>
```

#### 3.2 予測可能

**要件**: Webページは予測可能な方法で動作する

```typescript
// ❌ 悪い例 - フォーカス時に自動送信
<input onFocus={() => form.submit()} />

// ✅ 良い例 - 明示的なアクション
<form onSubmit={handleSubmit}>
  <input type="text" />
  <button type="submit">送信</button>
</form>
```

#### 3.3 入力支援

**要件**: ユーザーのミスを防ぎ、修正を支援

```html
<!-- エラーメッセージの例 -->
<form>
  <label for="email">メールアドレス<span aria-label="必須">*</span></label>
  <input 
    type="email" 
    id="email" 
    aria-required="true"
    aria-invalid="true"
    aria-describedby="email-error"
  >
  <span id="email-error" role="alert">
    エラー: 有効なメールアドレスを入力してください
  </span>

  <button type="submit">送信</button>
</form>
```

**入力補助**:
```html
<!-- HTML5オートコンプリート -->
<form>
  <label for="name">お名前</label>
  <input type="text" id="name" autocomplete="name">

  <label for="email">メールアドレス</label>
  <input type="email" id="email" autocomplete="email">

  <label for="tel">電話番号</label>
  <input type="tel" id="tel" autocomplete="tel">
</form>
```

---

### 4. 堅牢性（Robust）

様々な技術で解釈できるコンテンツ

#### 4.1 互換性

**要件**: 支援技術を含む様々なユーザーエージェントと互換性を持つ

```html
<!-- ARIA属性の適切な使用 -->
<nav role="navigation" aria-label="サイトナビゲーション">
  <ul>
    <li><a href="/">ホーム</a></li>
    <li><a href="/products" aria-current="page">製品</a></li>
  </ul>
</nav>

<!-- モーダルダイアログ -->
<div 
  role="dialog" 
  aria-labelledby="dialog-title"
  aria-describedby="dialog-desc"
  aria-modal="true"
>
  <h2 id="dialog-title">確認</h2>
  <p id="dialog-desc">本当に削除しますか？</p>
  <button>削除</button>
  <button>キャンセル</button>
</div>
```

---

## 🔧 実装チェックリスト

### HTML/CSS

```markdown
□ セマンティックHTML要素を使用（<header>, <nav>, <main>, <footer>等）
□ すべての<img>にalt属性を設定
□ フォーム要素に<label>を関連付け
□ コントラスト比をWCAG AAレベルで確保（4.5:1以上）
□ フォーカスインジケーターを明確に表示
□ レスポンシブデザイン（200%ズームでも利用可能）
```

### JavaScript/TypeScript

```markdown
□ キーボード操作のサポート（Enter, Space, Arrow, Escape等）
□ フォーカス管理（モーダル開閉時等）
□ ARIA属性の適切な使用（role, aria-label等）
□ スクリーンリーダー向けのライブリージョン（aria-live）
□ エラーメッセージの適切な通知
```

### コンテンツ

```markdown
□ 明確で簡潔な見出し構造（<h1>～<h6>）
□ リンクテキストが目的地を明確に説明
□ エラーメッセージが具体的で修正方法を提示
□ 動画に字幕とトランスクリプトを提供
□ 複雑なグラフや図表に説明文を提供
```

---

## 🧪 テスト方法

### 自動テストツール

```bash
# axe-core（Jest統合）
npm install --save-dev jest-axe

# テスト例
import { axe, toHaveNoViolations } from 'jest-axe';

expect.extend(toHaveNoViolations);

test('should have no accessibility violations', async () => {
  const { container } = render(<MyComponent />);
  const results = await axe(container);
  expect(results).toHaveNoViolations();
});
```

### 手動テスト

```markdown
1. キーボードのみで操作（Tabキーでナビゲーション）
2. スクリーンリーダーでテスト（NVDA、JAWS、VoiceOver等）
3. ブラウザズーム200%で表示確認
4. 色覚シミュレーターでコントラスト確認
5. 自動再生動画・音声の確認
```

### 推奨テストツール

| ツール | 用途 | URL |
|-------|-----|-----|
| **axe DevTools** | ブラウザ拡張機能 | https://www.deque.com/axe/devtools/ |
| **Lighthouse** | Chrome DevTools | ビルトイン |
| **WAVE** | アクセシビリティ評価 | https://wave.webaim.org/ |
| **Pa11y** | CLIツール | https://pa11y.org/ |
| **Contrast Checker** | コントラスト比チェック | https://webaim.org/resources/contrastchecker/ |

---

## 📚 参考リソース

- **WCAG 2.1**: https://www.w3.org/TR/WCAG21/
- **WAI-ARIA**: https://www.w3.org/WAI/ARIA/
- **MDN Accessibility**: https://developer.mozilla.org/en-US/docs/Web/Accessibility
- **A11y Project**: https://www.a11yproject.com/

---

## 🔄 更新履歴

### v1.0.0 (2025-10-27)
- 初版作成
- WCAG 2.1 AAレベルを標準として定義
- 実装例とチェックリストを追加
- テスト方法とツールを整理
