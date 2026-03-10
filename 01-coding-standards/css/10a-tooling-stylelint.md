# CSS/SCSS ツール: Stylelint

**このドキュメントについて**: CSS/SCSS コーディング規約 - ツール: Stylelint

---

## 10. ツール設定・自動化

### 10.1 Stylelint 設定

#### **完全な Stylelint 設定ファイル**
```json
{
  "extends": [
    "stylelint-config-standard",
    "stylelint-config-recess-order",
    "stylelint-config-prettier"
  ],
  "plugins": [
    "stylelint-scss",
    "stylelint-order",
    "stylelint-selector-bem-pattern",
    "stylelint-declaration-strict-value",
    "stylelint-high-performance-animation"
  ],
  "rules": {
    "/* ===== 基本規則 ===== */": null,
    "indentation": 2,
    "string-quotes": "single",
    "no-duplicate-selectors": true,
    "color-hex-case": "lower",
    "color-hex-length": "short",
    "color-named": "never",
    "selector-combinator-space-after": "always",
    "selector-attribute-quotes": "always",
    "selector-attribute-operator-space-before": "never",
    "selector-attribute-operator-space-after": "never",
    "selector-attribute-brackets-space-inside": "never",
    "declaration-block-trailing-semicolon": "always",
    "declaration-colon-space-before": "never",
    "declaration-colon-space-after": "always",
    "number-leading-zero": "always",
    "function-url-quotes": "always",
    "font-weight-notation": "numeric",
    "comment-word-disallowed-list": ["todo", "fixme"],
    
    "/* ===== パフォーマンス関連 ===== */": null,
    "plugin/no-low-performance-animation-properties": [
      true,
      {
        "ignore": "paint-properties"
      }
    ],
    "declaration-no-important": [
      true,
      {
        "ignore": ["keyframes"]
      }
    ],
    "selector-max-id": 0,
    "selector-max-universal": 1,
    "selector-max-type": 2,
    "selector-max-compound-selectors": 4,
    "selector-max-specificity": "0,3,2",
    
    "/* ===== SCSS 関連 ===== */": null,
    "scss/at-extend-no-missing-placeholder": true,
    "scss/at-function-pattern": "^[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/at-import-no-partial-leading-underscore": true,
    "scss/at-import-partial-extension-blacklist": ["scss"],
    "scss/at-mixin-pattern": "^[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/at-rule-no-unknown": true,
    "scss/dollar-variable-colon-space-after": "always",
    "scss/dollar-variable-colon-space-before": "never",
    "scss/dollar-variable-pattern": "^[_]?[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/percent-placeholder-pattern": "^[a-z]+([a-z0-9-]+[a-z0-9]+)?$",
    "scss/selector-no-redundant-nesting-selector": true,
    
    "/* ===== BEM パターン ===== */": null,
    "plugin/selector-bem-pattern": {
      "preset": "bem",
      "componentName": "[A-Z]+",
      "componentSelectors": {
        "initial": "^\\.{componentName}(?:-[a-z]+)*$",
        "combined": "^\\.combined-{componentName}-[a-z]+$"
      },
      "utilitySelectors": "^\\.u-[a-z]+$"
    },
    
    "/* ===== デザインシステム制約 ===== */": null,
    "scale-unlimited/declaration-strict-value": [
      ["/color$/", "z-index", "font-size", "font-weight"],
      {
        "ignoreKeywords": {
          "color": ["currentColor", "transparent", "inherit"],
          "z-index": ["auto"],
          "font-size": ["inherit", "initial"],
          "font-weight": ["inherit", "initial"]
        },
        "ignoreFunctions": false,
        "disableFix": true,
        "expandShorthand": true
      }
    ],
    
    "/* ===== プロパティ順序 ===== */": null,
    "order/properties-order": [
      [
        {
          "groupName": "positioning",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "position",
            "top",
            "right",
            "bottom",
            "left",
            "z-index"
          ]
        },
        {
          "groupName": "display",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "display",
            "flex",
            "flex-grow",
            "flex-shrink",
            "flex-basis",
            "flex-direction",
            "flex-wrap",
            "justify-content",
            "align-content",
            "align-items",
            "align-self",
            "order",
            "grid",
            "grid-area",
            "grid-template",
            "grid-template-areas",
            "grid-template-rows",
            "grid-template-columns",
            "grid-row",
            "grid-row-start",
            "grid-row-end",
            "grid-column",
            "grid-column-start",
            "grid-column-end",
            "grid-auto-rows",
            "grid-auto-columns",
            "grid-auto-flow",
            "grid-gap",
            "grid-row-gap",
            "grid-column-gap",
            "gap",
            "row-gap",
            "column-gap"
          ]
        },
        {
          "groupName": "box-model",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "width",
            "min-width",
            "max-width",
            "height",
            "min-height",
            "max-height",
            "padding",
            "padding-top",
            "padding-right",
            "padding-bottom",
            "padding-left",
            "margin",
            "margin-top",
            "margin-right",
            "margin-bottom",
            "margin-left",
            "border",
            "border-spacing",
            "border-collapse",
            "border-width",
            "border-style",
            "border-color",
            "border-top",
            "border-right",
            "border-bottom",
            "border-left",
            "border-radius",
            "outline",
            "outline-width",
            "outline-style",
            "outline-color",
            "outline-offset",
            "box-sizing"
          ]
        },
        {
          "groupName": "typography",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "font",
            "font-family",
            "font-size",
            "font-weight",
            "font-style",
            "font-variant",
            "font-size-adjust",
            "font-stretch",
            "font-effect",
            "font-emphasize",
            "font-emphasize-position",
            "font-emphasize-style",
            "font-smooth",
            "line-height",
            "letter-spacing",
            "word-spacing",
            "color",
            "text-align",
            "text-align-last",
            "text-emphasis",
            "text-emphasis-color",
            "text-emphasis-style",
            "text-emphasis-position",
            "text-decoration",
            "text-indent",
            "text-justify",
            "text-outline",
            "text-overflow",
            "text-overflow-ellipsis",
            "text-overflow-mode",
            "text-shadow",
            "text-transform",
            "text-wrap",
            "word-wrap",
            "word-break"
          ]
        },
        {
          "groupName": "visual",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "background",
            "background-color",
            "background-image",
            "background-repeat",
            "background-attachment",
            "background-position",
            "background-position-x",
            "background-position-y",
            "background-clip",
            "background-origin",
            "background-size",
            "box-decoration-break",
            "box-shadow",
            "opacity",
            "filter",
            "list-style",
            "list-style-position",
            "list-style-type",
            "list-style-image"
          ]
        },
        {
          "groupName": "animation",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "transition",
            "transition-delay",
            "transition-timing-function",
            "transition-duration",
            "transition-property",
            "transform",
            "transform-origin",
            "animation",
            "animation-name",
            "animation-duration",
            "animation-play-state",
            "animation-timing-function",
            "animation-delay",
            "animation-iteration-count",
            "animation-direction",
            "animation-fill-mode"
          ]
        },
        {
          "groupName": "misc",
          "emptyLineBefore": "always",
          "noEmptyLineBetween": true,
          "properties": [
            "appearance",
            "clip",
            "clip-path",
            "counter-reset",
            "counter-increment",
            "resize",
            "user-select",
            "nav-index",
            "nav-up",
            "nav-right",
            "nav-down",
            "nav-left",
            "tab-size",
            "hyphens",
            "pointer-events",
            "will-change",
            "contain",
            "content-visibility",
            "cursor",
            "overflow",
            "overflow-x",
            "overflow-y",
            "overflow-scrolling",
            "overscroll-behavior",
            "overscroll-behavior-x",
            "overscroll-behavior-y"
          ]
        }
      ],
      {
        "unspecified": "bottomAlphabetical"
      }
    ],
    
    "/* ===== 例外とカスタム規則 ===== */": null,
    "at-rule-no-unknown": [
      true,
      {
        "ignoreAtRules": [
          "tailwind",
          "apply",
          "variants",
          "responsive",
          "screen",
          "layer",
          "container"
        ]
      }
    ],
    "function-no-unknown": [
      true,
      {
        "ignoreFunctions": ["theme", "screen"]
      }
    ],
    "selector-pseudo-class-no-unknown": [
      true,
      {
        "ignorePseudoClasses": ["global", "local", "export"]
      }
    ]
  },
  "ignoreFiles": [
    "**/*.js",
    "**/*.jsx",
    "**/*.ts",
    "**/*.tsx",
    "**/node_modules/**",
    "**/dist/**",
    "**/build/**",
    "**/.next/**",
    "**/coverage/**"
  ]
}
```

#### **package.json スクリプト設定**
```json
{
  "scripts": {
    "css:lint": "stylelint \"**/*.{css,scss}\" --cache --cache-location .cache/stylelint",
    "css:lint:fix": "stylelint \"**/*.{css,scss}\" --fix --cache",
    "css:format": "prettier --write \"**/*.{css,scss}\"",
    "css:check": "npm run css:lint && npm run css:format -- --check",
    "css:fix": "npm run css:lint:fix && npm run css:format",
    "css:watch": "stylelint \"**/*.{css,scss}\" --watch --cache"
  },
  "devDependencies": {
    "stylelint": "^15.10.0",
    "stylelint-config-standard": "^34.0.0",
    "stylelint-config-recess-order": "^4.3.0",
    "stylelint-config-prettier": "^9.0.5",
    "stylelint-scss": "^5.1.0",
    "stylelint-order": "^6.0.3",
    "stylelint-selector-bem-pattern": "^3.0.1",
    "stylelint-declaration-strict-value": "^1.10.4",
    "stylelint-high-performance-animation": "^1.8.0",
    "prettier": "^3.0.0"
  }
}
```

### 10.2 PostCSS 設定

#### **postcss.config.js**
