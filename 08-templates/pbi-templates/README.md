# PBIテンプレート集

このディレクトリには、8つのPBIタイプ別のテンプレートが格納されています。

## 📁 テンプレート一覧

| ファイル名 | PBIタイプ | 用途 | Phase経路 |
|----------|---------|------|----------|
| [pbi-template-npi.md](./pbi-template-npi.md) | **NPI** | 新規プロジェクト立ち上げ | 0→1→2→3→4→5→6 |
| [pbi-template-nfd.md](./pbi-template-nfd.md) | **NFD** | 新規機能開発 | 0→2→3→4→5 |
| [pbi-template-enh.md](./pbi-template-enh.md) | **ENH** | 既存機能追加・改修 | 0→2→3→4→5 |
| [pbi-template-bug.md](./pbi-template-bug.md) | **BUG** | バグ修正 | (0簡易)→3→4 |
| [pbi-template-ref.md](./pbi-template-ref.md) | **REF** | リファクタリング | 0→3→4 |
| [pbi-template-arc.md](./pbi-template-arc.md) | **ARC** | 設計変更・アーキテクチャ改善 | 0→2→3→4→5 |
| [pbi-template-hot.md](./pbi-template-hot.md) | **HOT** | 緊急修正 | (0簡易)→3→(4簡易)→5 |
| [pbi-template-poc.md](./pbi-template-poc.md) | **POC** | 実験的機能 | (0簡易)→3 |

## 🚀 使い方

### 1. PBIタイプを判定
まず、作成するPBIがどのタイプに該当するか判定してください。

👉 判定方法: [PBI-TYPE-JUDGMENT-GUIDE.md](../../00-guides/PBI-TYPE-JUDGMENT-GUIDE.md)

### 2. テンプレートを選択
判定したタイプに対応するテンプレートファイルを開きます。

### 3. テンプレートをコピー
テンプレートの内容をコピーして、JIRAまたはテキストエディタに貼り付けます。

### 4. 情報を記入
プレースホルダー（`[...]`部分）を実際の情報で置き換えます。

### 5. レビュー
完成したPBIを以下のチェックリストで確認：

👉 チェックリスト: [PBI-CREATION-BEST-PRACTICES.md](../../00-guides/PBI-CREATION-BEST-PRACTICES.md#チェックリストレビュー前の確認事項)

## 📚 関連ドキュメント

- **[PBI-TEMPLATE-CATALOG.md](../../00-guides/PBI-TEMPLATE-CATALOG.md)**: テンプレートの詳細説明とJIRA連携
- **[PBI-CREATION-BEST-PRACTICES.md](../../00-guides/PBI-CREATION-BEST-PRACTICES.md)**: PBI作成のベストプラクティス
- **[PBI-TYPE-JUDGMENT-GUIDE.md](../../00-guides/PBI-TYPE-JUDGMENT-GUIDE.md)**: PBIタイプの判定方法

## ⚠️ 注意事項

- **必須セクションは削除しない**: 各テンプレートの必須セクションは削除せず、必ず記入してください
- **プレースホルダーを残さない**: `[...]`形式のプレースホルダーは全て実際の情報で置き換えてください
- **カスタマイズは推奨**: プロジェクトの特性に合わせてセクションを追加することは推奨されます

## 🔄 テンプレートの更新

テンプレートの改善提案がある場合は、Engineering Leadership Teamまでご連絡ください。

---

**Version**: 1.0.0  
**Last Updated**: 2025-11-17
