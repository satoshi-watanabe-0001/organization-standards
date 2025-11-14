# TypeScript/JavaScript コーディング規約
## TypeScript/JavaScript Coding Standards

**最終更新日**: 2024-10-09  
**バージョン**: 1.0.0  
**対象**: TypeScript 5.0+, JavaScript ES2022+  
**適用範囲**: フロントエンド（React/Next.js）・バックエンド（Node.js/NestJS）

## 目的

このドキュメントは、TypeScript/JavaScriptプロジェクトにおける具体的なコーディング規約を定義し、フロントエンド・バックエンド両方の開発で一貫した高品質なコードを実現します。共通原則については`00-general-principles.md`を参照してください。

---

## 1. 基本設定・ツール設定

### 1.1 推奨ツールチェーン

#### **必須ツール**
```json
{
  "typescript": "^5.0.0",
  "eslint": "^8.50.0",
  "@typescript-eslint/eslint-plugin": "^6.0.0",
  "@typescript-eslint/parser": "^6.0.0",
  "prettier": "^3.0.0",
  "husky": "^8.0.0",
  "lint-staged": "^14.0.0"
}
```

#### **ESLint設定（.eslintrc.json）**
```json
{
  "extends": [
    "eslint:recommended",
    "@typescript-eslint/recommended",
    "@typescript-eslint/recommended-requiring-type-checking",
    "prettier"
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "ecmaVersion": 2022,
    "sourceType": "module",
    "project": "./tsconfig.json"
  },
  "plugins": ["@typescript-eslint"],
  "rules": {
    "@typescript-eslint/no-unused-vars": ["error", { "argsIgnorePattern": "^_" }],
    "@typescript-eslint/explicit-function-return-type": "warn",
    "@typescript-eslint/no-explicit-any": "error",
    "@typescript-eslint/prefer-nullish-coalescing": "error",
    "@typescript-eslint/prefer-optional-chain": "error",
    "prefer-const": "error",
    "no-var": "error",
    "eol-last": "error",
    "comma-dangle": ["error", "es5"]
  }
}
```

#### **Prettier設定（.prettierrc）**
```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false,
  "bracketSpacing": true,
  "arrowParens": "avoid"
}
```

#### **TypeScript設定（tsconfig.json）**
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "allowJs": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "noUncheckedIndexedAccess": true,
    "forceConsistentCasingInFileNames": true,
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "incremental": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["next-env.d.ts", "**/*.ts", "**/*.tsx"],
  "exclude": ["node_modules"]
}
```

**Devin指示**: プロジェクト初期化時は上記設定を必ず適用し、strictモードを有効にせよ

### 1.2 Git設定

#### **Husky + lint-staged設定**
```json
// package.json
{
  "lint-staged": {
    "*.{ts,tsx,js,jsx}": [
      "eslint --fix",
      "prettier --write"
    ],
    "*.{json,css,md}": [
      "prettier --write"
    ]
  }
}
```

#### **コミットメッセージ規約**
```
<type>(<scope>): <subject>

types: feat, fix, docs, style, refactor, test, chore
例: feat(auth): add JWT token validation
例: fix(api): resolve user profile update issue
```

**Devin指示**: コミット前に必ずlint・format・type-checkを実行せよ

---
