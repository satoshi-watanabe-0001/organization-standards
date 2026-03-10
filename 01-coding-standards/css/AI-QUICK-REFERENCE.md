# CSS/SCSS開発 - AI向けクイックリファレンス

**バージョン**: 1.1.0  
**最終更新**: 2025-11-15

## ✅ 必須チェック項目 TOP 25

### 命名・構造 (1-5)
1. ✓ BEM命名規則の使用 (.block__element--modifier)
2. ✓ セマンティックなクラス名 (見た目でなく役割)
3. ✓ ネスト深度は3階層まで
4. ✓ IDセレクタを避ける (詳細度の問題)
5. ✓ !importantを避ける

### レスポンシブ (6-10)
6. ✓ モバイルファースト設計
7. ✓ 相対単位の使用 (rem, em, %)
8. ✓ メディアクエリの適切な使用
9. ✓ Container Queriesの活用検討
10. ✓ Fluid Typographyの実装

### パフォーマンス (11-15)
11. ✓ 不要なセレクタの削除
12. ✓ Critical CSSの分離
13. ✓ フォント最適化 (font-display: swap)
14. ✓ CSS Bundle Size の監視
15. ✓ will-changeの適切な使用

### アクセシビリティ (16-20)
16. ✓ Focus Ringを削除しない
17. ✓ カラーコントラスト比 4.5:1以上
18. ✓ キーボードナビゲーション対応
19. ✓ prefers-reduced-motionの尊重
20. ✓ Screen Reader考慮

### コメント規約 (21-25) ✨ 2025-11-15追加

#### 21. ✓ 日本語コメントの記述

```css
/* ✅ OK: 日本語で「なぜ」を説明 */
/* NOTE: IE11サポートのため、flexboxのフォールバックを追加 */
.container {
  display: block;
  display: flex;
}

/* ❌ NG: 英語または「何を」の説明 */
/* Container styles */
.container {
  display: flex;
}
```
- **必須**: すべてのコメントを日本語で記述（CSS プロパティ名、技術用語を除く）
- **参照**: [css-inline-comment-examples.md](css-inline-comment-examples.md)

#### 22. ✓ WHY原則の遵守

```scss
// ✅ OK: なぜその実装をしているか説明
// NOTE: モバイルでのタップ領域確保のため、最小44pxに設定
.button {
  min-height: 44px;
  padding: 12px 24px;
}

// ❌ NG: 何をしているか説明
// ボタンのスタイル
.button {
  min-height: 44px;
  padding: 12px 24px;
}
```
- **必須**: 「WHAT」ではなく「WHY」を説明
- **参照**: [css-inline-comment-examples.md](css-inline-comment-examples.md)

#### 23. ✓ 複雑なスタイルへの詳細コメント

```scss
/**
 * レスポンシブグリッドレイアウト
 * 
 * デザイン意図:
 * - モバイル: 1カラム
 * - タブレット: 2カラム
 * - デスクトップ: 3カラム
 * 
 * 技術的理由:
 * - CSS Gridを使用（flexboxより列の高さ揃えが容易）
 * - gap プロパティで余白を統一管理
 * - minmax()で最小幅を確保しつつ、画面幅に応じて伸縮
 */
.grid-container {
  display: grid;
  gap: 1.5rem;
  
  /* モバイル: 1カラム */
  grid-template-columns: 1fr;
  
  /* タブレット: 2カラム */
  @media (min-width: 768px) {
    grid-template-columns: repeat(2, minmax(300px, 1fr));
  }
  
  /* デスクトップ: 3カラム */
  @media (min-width: 1024px) {
    grid-template-columns: repeat(3, minmax(300px, 1fr));
  }
}
```
- **必須**: 複雑なレイアウト、calc()計算、ブラウザハック、Z-index管理に詳細コメント
- **参照**: [css-inline-comment-examples.md](css-inline-comment-examples.md)

#### 24. ✓ マジックナンバーの説明

```scss
/* ✅ OK: マジックナンバーの理由を説明 */
.modal {
  /* NOTE: ヘッダー(60px) + フッター(80px) + 余白(40px) = 180pxを確保 */
  max-height: calc(100vh - 180px);
  
  /* NOTE: z-index 1000: モーダルレイヤー（ドロップダウン900より上位） */
  z-index: 1000;
}

/* ❌ NG: 理由なしのマジックナンバー */
.modal {
  max-height: calc(100vh - 180px);
  z-index: 1000;
}
```
- **必須**: calc()計算、z-index値、特定の数値（px、%等）の理由を記載
- **参照**: [css-inline-comment-examples.md](css-inline-comment-examples.md)

#### 25. ✓ TODO/FIXME/HACKの書式

```scss
// TODO: [山田太郎] [期限: 2025-12-31] 理由: ダークモード対応を追加
// FIXME: [佐藤花子] [期限: 2025-11-30] 理由: Safariで文字が欠ける問題を修正
// HACK: [鈴木一郎] 理由: Chrome 90のバグ回避のための暫定対応

.button {
  // HACK: [鈴木一郎] 理由: Chrome 90でボーダーが消える問題の回避
  border: 1px solid transparent;
  border-color: #333;
}
```
- **必須**: 担当者、期限、理由を記載
- **参照**: [css-inline-comment-examples.md](css-inline-comment-examples.md)

---

## 🚫 よくある間違い

### 間違い1: ID使用、過度なネスト

❌ **悪い例**:
```css
#header .nav ul li a {
  color: blue;
}
```

✅ **良い例**:
```css
.nav__link {
  color: blue;
}
```

### 間違い2: コメントなしのマジックナンバー

❌ **悪い例**:
```css
.element {
  top: 73px;
  z-index: 9999;
}
```

✅ **良い例**:
```css
.element {
  /* NOTE: ヘッダー高さ(60px) + 余白(13px) */
  top: 73px;
  
  /* NOTE: 最上位モーダルレイヤー（通常モーダル1000より上位） */
  z-index: 9999;
}
```

---

## 📋 コードレビューチェックリスト

### 基本チェック
- [ ] BEM命名規則に従っている
- [ ] セマンティックなクラス名を使用
- [ ] ネスト深度が3階層以下
- [ ] IDセレクタを使用していない
- [ ] !importantを使用していない

### レスポンシブ
- [ ] モバイルファースト設計
- [ ] 相対単位を使用
- [ ] メディアクエリが適切

### パフォーマンス
- [ ] 不要なセレクタがない
- [ ] Critical CSSを分離
- [ ] フォントが最適化されている

### アクセシビリティ
- [ ] Focus Ringが保持されている
- [ ] カラーコントラストが十分
- [ ] prefers-reduced-motionに対応

### コメント規約 ✨
- [ ] **すべてのコメントが日本語で記述されている**
- [ ] **コメントがWHY原則に従っている**
- [ ] **複雑なスタイルに詳細コメントがある**
- [ ] **マジックナンバーに説明がある**
- [ ] **TODO/FIXME/HACKに担当者・期限・理由がある**

---

## 🔗 詳細ドキュメントへのリンク

- [README.md](README.md) - CSS/SCSS規約の全体像
- [01-introduction-setup.md](01-introduction-setup.md) - 基本設定
- [02-naming-bem.md](02-naming-bem.md) - 命名規則とBEM
- [03-responsive-design.md](03-responsive-design.md) - レスポンシブデザイン
- [04-performance-optimization.md](04-performance-optimization.md) - パフォーマンス最適化
- [05-accessibility.md](05-accessibility.md) - アクセシビリティ
- **[css-inline-comment-examples.md](css-inline-comment-examples.md)** - コメント規約 ✨

---

詳細は各ドキュメントを参照してください。
