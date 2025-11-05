# ライセンス管理標準

**最終更新**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発チーム全体

---

## 📖 概要

このドキュメントは、オープンソースソフトウェア（OSS）およびサードパーティライブラリのライセンス管理標準を定義します。適切なライセンス管理により、法的リスクを回避し、コンプライアンスを維持します。

**目的**:
- ライセンス違反による法的リスクの回避
- ライセンス条件の遵守
- 知的財産権の保護
- 監査への対応準備

---

## 🎯 ライセンス分類

### ✅ Tier 1: 商用利用可・制限なし（承認済み）

これらのライセンスは、**事前承認なしで使用可能**です。

| ライセンス | 特徴 | 主な条件 |
|-----------|-----|---------|
| **MIT License** | 最も寛容 | 著作権表示のみ必須 |
| **Apache License 2.0** | 特許条項付き | 著作権表示・変更箇所の明示 |
| **BSD License (2-clause, 3-clause)** | 寛容 | 著作権表示のみ必須 |
| **ISC License** | MITに類似 | 著作権表示のみ必須 |
| **Python Software Foundation License** | Python関連 | 著作権表示のみ必須 |
| **CC0 (Public Domain)** | パブリックドメイン | 条件なし |

**使用例**:
- React (MIT)
- Express.js (MIT)
- TensorFlow (Apache 2.0)
- PostgreSQL (PostgreSQL License - BSD類似)

---

### ⚠️ Tier 2: コピーレフト（慎重使用・事前承認必要）

これらのライセンスは、**使用前にリーガルチームの承認が必要**です。

| ライセンス | 特徴 | 主な条件 | リスク |
|-----------|-----|---------|--------|
| **LGPL (Lesser GPL)** | 弱いコピーレフト | ライブラリとして使用なら変更箇所の公開のみ | ライブラリとして動的リンクなら低リスク |
| **MPL (Mozilla Public License)** | ファイル単位のコピーレフト | 変更したファイルのみ公開 | 中程度のリスク |
| **EPL (Eclipse Public License)** | 弱いコピーレフト | 変更箇所の公開 | 中程度のリスク |

**注意事項**:
- ソースコードの公開義務が発生する可能性
- 使用方法（静的リンク vs 動的リンク）により影響範囲が変わる

---

### ❌ Tier 3: 強いコピーレフト（原則使用禁止）

これらのライセンスは、**原則使用禁止**です。特別な理由がある場合のみ、経営層の承認が必要です。

| ライセンス | 特徴 | 主な条件 | リスク |
|-----------|-----|---------|--------|
| **GPL v2/v3** | 強いコピーレフト | **全ソースコードの公開義務** | **非常に高い** |
| **AGPL v3** | 最も強いコピーレフト | **ネットワーク経由でも公開義務** | **最高レベル** |

**禁止理由**:
- プロプライエタリコードの公開義務が発生
- ビジネスモデルへの重大な影響
- 知的財産権の喪失リスク

**例外的に使用可能なケース**:
- 完全に独立したツール（本番環境に含まれない）
- 内部ツール（外部に提供しない）

---

### 🔒 Tier 4: 商用禁止・独自ライセンス（個別審査必要）

| ライセンス | 特徴 | リスク |
|-----------|-----|--------|
| **CC BY-NC (非営利限定)** | 商用利用不可 | 商用プロジェクトで使用不可 |
| **Proprietary（独自ライセンス）** | ベンダー固有 | 個別に条件を確認必要 |

---

## 📋 ライセンス管理プロセス

### 1. 導入前チェック

#### ステップ1: ライセンス確認

```bash
# package.jsonの依存関係からライセンスを確認
npm install -g license-checker
license-checker --summary

# 特定パッケージのライセンス詳細
npm info <package-name> license
```

#### ステップ2: ライセンス分類

```markdown
□ Tier 1（承認済み）→ 使用可能
□ Tier 2（要承認）→ リーガルチームへ申請
□ Tier 3（使用禁止）→ 代替パッケージを検索
□ Tier 4（個別審査）→ 経営層へエスカレーション
```

#### ステップ3: 記録

```markdown
プロジェクトの`LICENSES.md`ファイルに記録:
- パッケージ名
- バージョン
- ライセンス種類
- 使用箇所
- 承認日（Tier 2の場合）
```

### 2. 継続的監視

#### CI/CDパイプラインでの自動チェック

```yaml
# .github/workflows/license-check.yml
name: License Check

on: [push, pull_request]

jobs:
  license-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: npm install -g license-checker
      - run: |
          license-checker --failOn 'GPL;AGPL;LGPL' --summary
```

#### Python向けライセンスチェック

```bash
pip install pip-licenses

# ライセンス一覧表示
pip-licenses --summary

# GPL系ライセンスを検出してエラー
pip-licenses --fail-on 'GPL;AGPL'
```

### 3. 定期監査

```markdown
□ 四半期ごとにライセンス棚卸し
□ 新規依存関係の追加をレビュー
□ 非推奨ライセンスの有無を確認
□ 監査レポートの作成・保管
```

---

## 🛠️ ツールとサービス

### ライセンスチェックツール

| ツール | 言語 | 機能 | URL |
|-------|-----|-----|-----|
| **license-checker** | Node.js | ライセンス一覧・チェック | https://www.npmjs.com/package/license-checker |
| **FOSSA** | 多言語 | 商用ライセンス管理SaaS | https://fossa.com |
| **Black Duck** | 多言語 | エンタープライズ向け | https://www.blackducksoftware.com |
| **Snyk** | 多言語 | セキュリティ+ライセンス | https://snyk.io |
| **pip-licenses** | Python | Pythonパッケージ | https://pypi.org/project/pip-licenses/ |
| **go-licenses** | Go | Goモジュール | https://github.com/google/go-licenses |

### ライセンス情報データベース

- **SPDX**: https://spdx.org/licenses/
- **OSI Approved Licenses**: https://opensource.org/licenses
- **ChooseALicense.com**: https://choosealicense.com/

---

## 📄 ライセンス表示義務

### NOTICES ファイル

プロジェクトルートに`NOTICES.md`または`THIRD-PARTY-LICENSES.md`を配置:

```markdown
# Third-Party Licenses

このプロジェクトは以下のオープンソースソフトウェアを使用しています。

## React (MIT License)

Copyright (c) Meta Platforms, Inc. and affiliates.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...

## Express (MIT License)

Copyright (c) 2009-2014 TJ Holowaychuk <tj@vision-media.ca>
Copyright (c) 2013-2014 Roman Shtylman <shtylman+expressjs@gmail.com>
Copyright (c) 2014-2015 Douglas Christopher Wilson <doug@somethingdoug.com>

Permission is hereby granted...
```

### 自動生成スクリプト

#### Node.js

```bash
# license-reportを使用
npm install -g license-report

license-report --output=markdown > THIRD-PARTY-LICENSES.md
```

#### Python

```bash
pip install pip-licenses

pip-licenses --format=markdown --output-file=THIRD-PARTY-LICENSES.md
```

---

## 🚨 よくあるライセンス違反

### 1. 著作権表示の欠落

**違反例**:
```javascript
// MITライセンスのコードをコピーしたが、著作権表示を削除
function someFunction() {
  // ...
}
```

**正しい例**:
```javascript
/**
 * Copyright (c) 2023 Original Author
 * Licensed under the MIT License
 * https://opensource.org/licenses/MIT
 */
function someFunction() {
  // ...
}
```

### 2. GPL汚染

**違反例**:
```javascript
// GPLライセンスのライブラリを直接組み込み
import { gplFunction } from 'gpl-library';

// 自社プロプライエタリコードと混在
export class ProprietaryClass {
  useGPL() {
    return gplFunction(); // ❌ 全体がGPLに汚染される
  }
}
```

**回避策**:
```javascript
// GPL代替ライブラリを使用
import { mitFunction } from 'mit-alternative-library';

export class ProprietaryClass {
  useMIT() {
    return mitFunction(); // ✅ 問題なし
  }
}
```

### 3. ライセンス非表示

**違反例**:
- Webアプリケーションに第三者ライセンス表示なし
- モバイルアプリに「ライセンス」メニューなし

**正しい例**:
```html
<!-- Webアプリケーション -->
<footer>
  <a href="/licenses">Third-Party Licenses</a>
</footer>
```

---

## 📊 ライセンス互換性マトリクス

| 使用したい | MITを含む | Apacheを含む | LGPLを含む | GPLを含む |
|-----------|----------|-------------|-----------|----------|
| **MIT** | ✅ | ✅ | ⚠️ 要確認 | ❌ |
| **Apache** | ✅ | ✅ | ⚠️ 要確認 | ❌ |
| **LGPL** | ✅ | ✅ | ✅ | ⚠️ 動的リンクのみ |
| **GPL** | ✅ | ⚠️ v3のみ互換 | ✅ | ✅ |
| **プロプライエタリ** | ✅ | ✅ | ⚠️ 動的リンクのみ | ❌ |

---

## 🔄 申請フォーマット

### Tier 2ライセンス使用申請

```markdown
## ライセンス使用申請

**申請日**: 2024-10-27  
**申請者**: 山田太郎  
**プロジェクト名**: ProjectX

### パッケージ情報
- **名前**: some-lgpl-library
- **バージョン**: 3.2.1
- **ライセンス**: LGPL v2.1
- **URL**: https://github.com/example/some-lgpl-library

### 使用目的
- データ可視化機能の実装に使用

### リスク評価
- 動的リンクのみで使用（静的リンクなし）
- ソースコード公開義務: なし
- 変更予定: なし

### 代替案検討
- MIT/Apacheライセンスの代替ライブラリを調査済み
- 機能要件を満たすライブラリが存在しないため、LGPLライブラリの使用を希望

### 承認者サイン
- リーガルチーム: __________
- 技術リーダー: __________
```

---

## 📚 参考リソース

- **Open Source Initiative (OSI)**: https://opensource.org/
- **GNU Licenses**: https://www.gnu.org/licenses/
- **Creative Commons**: https://creativecommons.org/
- **SPDX License List**: https://spdx.org/licenses/
- **TLDRLegal**: https://tldrlegal.com/

---

## 🔄 更新履歴

### v1.0.0 (2025-10-27)
- 初版作成
- ライセンス4段階分類を定義
- ライセンス管理プロセスを整備
- CI/CD統合例を追加
- ライセンス互換性マトリクスを作成
