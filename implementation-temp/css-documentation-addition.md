# CSS/SCSS ドキュメンテーション標準追加セクション

---

## X. ドキュメンテーション標準（Documentation Standards）

### X.1 CSS/SCSS コメント必須要件

#### **適用範囲**

**Level 1: 必須（品質ゲート）**
- すべてのCSSファイルのファイルヘッダー
- すべてのコンポーネント・モジュールの定義
- すべての複雑なレイアウト（Flexbox、Grid）
- すべてのブラウザ固有のハック・回避策

**Level 2: 強く推奨**
- 複雑なセレクタ（ネスト3階層以上、疑似クラス組み合わせ）
- レスポンシブデザインのブレークポイント
- アニメーション・トランジション
- カラーパレット・タイポグラフィ設定
- z-index の使用理由

**Level 3: 任意**
- 単純なスタイル（`color: red;` 等）
- 自己説明的な標準プロパティ

---

### X.2 CSS/SCSS コメント標準形式

#### **ファイルヘッダー（必須）**

```scss
/**
 * ユーザープロフィールコンポーネント
 * 
 * ユーザーのアバター、名前、ステータスを表示する再利用可能コンポーネント。
 * モバイル・タブレット・デスクトップのレスポンシブ対応。
 * 
 * @component UserProfile
 * @category Components
 * @responsive Mobile-first design
 * 
 * @example
 * <div class="user-profile">
 *   <img class="user-profile__avatar" src="..." alt="...">
 *   <div class="user-profile__info">
 *     <h3 class="user-profile__name">John Doe</h3>
 *     <span class="user-profile__status">Online</span>
 *   </div>
 * </div>
 */
```

**必須要素**:
- コンポーネント/ファイルの目的
- 適用範囲・使用場所

**推奨要素**:
- `@component`: コンポーネント名
- `@category`: カテゴリ分類
- `@responsive`: レスポンシブ対応状況
- `@example`: HTML使用例

---

#### **セクションヘッダー（推奨）**

```scss
/* ==========================================================================
   レイアウト: ユーザープロフィールカード
   ========================================================================== */

/* --------------------------------------------------------------------------
   サブセクション: カードコンテナ
   -------------------------------------------------------------------------- */
```

---

#### **コンポーネント定義（必須）**

```scss
/**
 * ユーザープロフィールカード
 * 
 * ユーザー情報を表示するカードコンポーネント。
 * BEM命名規則に従い、モディファイアで状態を管理。
 * 
 * Modifiers:
 *   --compact: コンパクト表示モード
 *   --featured: 注目ユーザー表示（金色ボーダー）
 * 
 * States:
 *   .is-loading: ローディング中の表示
 *   .is-offline: オフライン状態の表示
 * 
 * @see components/user-avatar.scss
 * @see components/status-badge.scss
 */
.user-profile {
  display: flex;
  padding: 1.5rem;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  /**
   * レスポンシブ: タブレット以上で横並びレイアウト
   * モバイルでは縦並び（デフォルト: flex-direction: column）
   */
  @media (min-width: 768px) {
    flex-direction: row;
    align-items: center;
  }
}
```

**必須要素**:
- コンポーネントの目的・役割
- モディファイア（BEM `--modifier`）の説明
- 状態クラス（`.is-*`）の説明

**推奨要素**:
- レスポンシブ動作の説明
- 関連コンポーネントへの参照

---

#### **複雑なレイアウト（必須）**

```scss
/**
 * グリッドレイアウト: ダッシュボード
 * 
 * CSS Grid による複雑な2次元レイアウト。
 * ヘッダー・サイドバー・メインコンテンツ・フッターを配置。
 * 
 * Grid Areas:
 *   - header: トップバー（全幅）
 *   - sidebar: ナビゲーション（固定幅: 250px）
 *   - main: メインコンテンツ（可変幅）
 *   - footer: フッター（全幅）
 * 
 * Layout:
 *   ┌─────────────────┐
 *   │     header      │
 *   ├────────┬────────┤
 *   │sidebar │  main  │
 *   ├────────┴────────┤
 *   │     footer      │
 *   └─────────────────┘
 * 
 * Responsive:
 *   - Desktop (1024px+): 上記レイアウト
 *   - Tablet (768px-1023px): サイドバー 200px
 *   - Mobile (<768px): サイドバー非表示、縦並び
 */
.dashboard-layout {
  display: grid;
  min-height: 100vh;
  
  /* グリッドテンプレート定義 */
  grid-template-areas:
    "header header"
    "sidebar main"
    "footer footer";
  
  /* カラム幅: 固定サイドバー + 可変メイン */
  grid-template-columns: 250px 1fr;
  
  /* 行高さ: 固定ヘッダー + 可変コンテンツ + 固定フッター */
  grid-template-rows: 60px 1fr 80px;
  
  /* グリッド間のギャップ */
  gap: 0;
}

/* ヘッダー領域 */
.dashboard-layout__header {
  grid-area: header;
  /* 他のスタイル... */
}

/* サイドバー領域 */
.dashboard-layout__sidebar {
  grid-area: sidebar;
  /* 他のスタイル... */
}

/* メインコンテンツ領域 */
.dashboard-layout__main {
  grid-area: main;
  /* 他のスタイル... */
}

/* フッター領域 */
.dashboard-layout__footer {
  grid-area: footer;
  /* 他のスタイル... */
}

/**
 * レスポンシブ: タブレット
 * サイドバー幅を200pxに縮小
 */
@media (max-width: 1023px) and (min-width: 768px) {
  .dashboard-layout {
    grid-template-columns: 200px 1fr;
  }
}

/**
 * レスポンシブ: モバイル
 * サイドバーを非表示にし、縦並びレイアウトに変更
 */
@media (max-width: 767px) {
  .dashboard-layout {
    grid-template-areas:
      "header"
      "main"
      "footer";
    grid-template-columns: 1fr;
    grid-template-rows: 60px 1fr 80px;
  }
  
  .dashboard-layout__sidebar {
    display: none;
  }
}
```

---

#### **デザインシステム: カラーパレット（必須）**

```scss
/**
 * カラーパレット
 * 
 * ブランドカラーとセマンティックカラーの定義。
 * アクセシビリティ基準（WCAG AA）を満たすコントラスト比を確保。
 */

/* --------------------------------------------------------------------------
   プライマリカラー
   -------------------------------------------------------------------------- */

/**
 * プライマリブルー
 * 用途: メインアクション、リンク、アクティブ状態
 * コントラスト比: 白背景で 4.5:1 (WCAG AA)
 */
$color-primary: #2563eb;

/**
 * プライマリブルー（ホバー）
 * インタラクション時の視覚的フィードバック
 */
$color-primary-hover: #1d4ed8;

/**
 * プライマリブルー（アクティブ）
 * クリック時の押下状態
 */
$color-primary-active: #1e40af;

/* --------------------------------------------------------------------------
   セマンティックカラー
   -------------------------------------------------------------------------- */

/**
 * 成功カラー（緑）
 * 用途: 成功メッセージ、完了状態、ポジティブアクション
 */
$color-success: #10b981;

/**
 * 警告カラー（黄）
 * 用途: 警告メッセージ、注意喚起
 * 注意: 黒文字（#000000）との使用を推奨（コントラスト確保）
 */
$color-warning: #f59e0b;

/**
 * エラーカラー（赤）
 * 用途: エラーメッセージ、削除アクション、危険な操作
 */
$color-error: #ef4444;

/**
 * 情報カラー（青）
 * 用途: 情報メッセージ、ヒント、補足説明
 */
$color-info: #3b82f6;
```

---

#### **複雑なアニメーション（推奨）**

```scss
/**
 * フェードインアニメーション
 * 
 * 要素を透明から不透明に変化させる。
 * ページ読み込み時やモーダル表示時に使用。
 * 
 * Duration: 300ms
 * Easing: ease-in-out（加速→減速）
 * 
 * @example
 * .modal {
 *   animation: fadeIn 300ms ease-in-out;
 * }
 */
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/**
 * スライドインアニメーション（左から）
 * 
 * サイドバーやドロワーメニューの表示時に使用。
 * GPU アクセラレーションを活用（transform使用）。
 * 
 * Duration: 250ms
 * Easing: cubic-bezier（カスタムイージング）
 * 
 * Performance Note:
 *   - transform を使用してレイアウトの再計算を回避
 *   - will-change プロパティで最適化を促進
 */
@keyframes slideInLeft {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(0);
  }
}

.sidebar {
  animation: slideInLeft 250ms cubic-bezier(0.4, 0, 0.2, 1);
  
  /* GPU アクセラレーション最適化 */
  will-change: transform;
}
```

---

#### **ブラウザハック・回避策（必須）**

```scss
/**
 * Internet Explorer 11 対応
 * 
 * IE11 は CSS Grid の一部機能をサポートしていないため、
 * Flexbox によるフォールバックレイアウトを提供。
 * 
 * @deprecated IE11 サポート終了予定: 2025年12月
 */
@media all and (-ms-high-contrast: none), (-ms-high-contrast: active) {
  .dashboard-layout {
    display: flex;
    flex-direction: column;
  }
  
  .dashboard-layout__sidebar {
    width: 250px;
    flex-shrink: 0;
  }
}

/**
 * Safari バグ回避: Flexbox の overflow 問題
 * 
 * Safari では Flexbox アイテムが min-width: auto の影響で
 * overflow: hidden が効かない問題がある。
 * min-width: 0 を明示的に設定することで解決。
 * 
 * @see https://bugs.webkit.org/show_bug.cgi?id=123456
 */
.flex-item {
  min-width: 0;  /* Safari バグ回避 */
  overflow: hidden;
}
```

---

#### **z-index 管理（推奨）**

```scss
/**
 * z-index レイヤー管理
 * 
 * 重なり順を体系的に管理するための定数定義。
 * 段階的な値で予測可能性を確保。
 * 
 * Layers (下から上):
 *   1. base (0): 通常コンテンツ
 *   2. dropdown (100): ドロップダウンメニュー
 *   3. sticky (200): 固定ヘッダー・フッター
 *   4. modal-backdrop (300): モーダル背景
 *   5. modal (400): モーダルコンテンツ
 *   6. popover (500): ポップオーバー・ツールチップ
 *   7. toast (600): トースト通知
 */

$z-index-base: 0;
$z-index-dropdown: 100;
$z-index-sticky: 200;
$z-index-modal-backdrop: 300;
$z-index-modal: 400;
$z-index-popover: 500;
$z-index-toast: 600;

/* 使用例 */
.modal-backdrop {
  z-index: $z-index-modal-backdrop;  /* 300 */
  position: fixed;
  /* ... */
}

.modal {
  z-index: $z-index-modal;  /* 400（背景より上） */
  position: fixed;
  /* ... */
}
```

---

### X.3 Stylelint による自動チェック

#### **推奨 Stylelint 設定（.stylelintrc.json）**

```json
{
  "extends": [
    "stylelint-config-standard",
    "stylelint-config-sass-guidelines"
  ],
  "plugins": [
    "stylelint-scss"
  ],
  "rules": {
    "comment-empty-line-before": [
      "always",
      {
        "except": ["first-nested"],
        "ignore": ["stylelint-commands"]
      }
    ],
    "comment-whitespace-inside": "always",
    "scss/comment-no-empty": true,
    "max-nesting-depth": 3,
    "selector-max-id": 0,
    "selector-class-pattern": "^[a-z][a-z0-9]*(-[a-z0-9]+)*(__[a-z0-9]+(-[a-z0-9]+)*)?(--[a-z0-9]+(-[a-z0-9]+)*)?$"
  }
}
```

#### **インストール**

```bash
npm install --save-dev stylelint stylelint-config-standard stylelint-scss
```

---

### X.4 コードレビューチェックリスト

**レビュアーは以下を確認**:

#### **必須項目**
- [ ] ファイルヘッダーが存在するか
- [ ] コンポーネント定義にコメントがあるか
- [ ] 複雑なレイアウト（Grid、Flexbox）に説明があるか
- [ ] ブラウザハックに理由が明記されているか

#### **品質項目**
- [ ] 「何を」だけでなく「なぜ」が説明されているか
- [ ] レスポンシブの動作が明記されているか
- [ ] カラーパレット・変数に用途が説明されているか
- [ ] z-index の使用理由が明確か
- [ ] アニメーションのパフォーマンス考慮が記載されているか

#### **BEM 準拠**
- [ ] ブロック・エレメント・モディファイアが明確か
- [ ] 命名規則に従っているか

---

### X.5 ベストプラクティス

#### **✅ Good Examples**

```scss
/**
 * ボタンコンポーネント
 * 
 * 再利用可能なボタンスタイル。
 * プライマリ・セカンダリ・デンジャーの3種類のバリエーション。
 * 
 * Modifiers:
 *   --primary: プライマリアクション（青）
 *   --secondary: セカンダリアクション（グレー）
 *   --danger: 危険なアクション（赤）
 * 
 * States:
 *   :hover: ホバー時の濃色表示
 *   :active: クリック時の押下効果
 *   :disabled: 無効状態のグレーアウト
 * 
 * Accessibility:
 *   - 最小タッチターゲット: 44x44px (WCAG AA)
 *   - フォーカス表示: 2px outline
 */
.button {
  /* 基本スタイル */
  display: inline-flex;
  align-items: center;
  justify-content: center;
  
  /* サイズ: 最小タッチターゲット確保 */
  min-height: 44px;
  padding: 0.75rem 1.5rem;
  
  /* タイポグラフィ */
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.5;
  
  /* ボーダー・角丸 */
  border: none;
  border-radius: 6px;
  
  /* トランジション: スムーズなインタラクション */
  transition: all 150ms ease-in-out;
  
  /* アクセシビリティ: フォーカス表示 */
  &:focus-visible {
    outline: 2px solid $color-primary;
    outline-offset: 2px;
  }
}

/**
 * プライマリボタンモディファイア
 * メインアクション用（例: 保存、送信）
 */
.button--primary {
  background-color: $color-primary;
  color: #ffffff;
  
  &:hover {
    background-color: $color-primary-hover;
  }
  
  &:active {
    background-color: $color-primary-active;
  }
}
```

#### **❌ Bad Examples**

```scss
/* ❌ コメントなし */
.btn {
  padding: 10px;
  background: blue;
}

/* ❌ 自明な内容の繰り返し */
.button {
  /* ボタンの色を青にする */
  color: blue;
}

/* ❌ ブラウザハックの理由不明 */
.box {
  display: flex;
  min-width: 0;  /* なぜ必要か不明 */
}

/* ❌ 複雑なレイアウトに説明なし */
.grid {
  display: grid;
  grid-template-areas:
    "a a b"
    "c d d";
  grid-template-columns: 1fr 2fr 1fr;
}
```

---

### X.6 まとめ

**必須ルール**:
1. すべてのCSSファイルにファイルヘッダー
2. すべてのコンポーネント定義にコメント
3. 複雑なレイアウト（Grid、Flexbox）に説明
4. ブラウザハック・回避策に理由を明記
5. Stylelint による自動チェック

**推奨プラクティス**:
1. カラーパレット・変数に用途説明
2. レスポンシブ動作の明記
3. z-index 使用理由の説明
4. アニメーションのパフォーマンス考慮
5. アクセシビリティ対応の記載
6. BEM 命名規則の遵守

**コードレビューで不合格となる条件**:
- コンポーネントにコメントがない
- 複雑なレイアウトに説明がない
- ブラウザハックの理由が不明
- Stylelint ルール違反

---

**このセクションを `css-styling-standards.md` の末尾に追加してください。**
