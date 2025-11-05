# 推奨拡張機能・ツール

**最終更新**: 2025-10-27  
**バージョン**: 1.0.0  
**対象**: 開発チーム全体

---

## 📖 概要

このドキュメントは、開発生産性を向上させるための推奨拡張機能・ツール・サービスを定義します。適切なツールセットの活用により、開発効率、コード品質、チーム協業が大幅に改善されます。

---

## 🎯 ツールカテゴリ

1. **IDE拡張機能** - エディタ機能の拡張
2. **AI開発支援** - コード生成・補完支援
3. **コラボレーション** - チーム協業ツール
4. **開発ユーティリティ** - 日常的な開発作業の支援
5. **品質・テスト** - コード品質管理
6. **DevOps** - デプロイ・運用支援

---

## 💻 IDE拡張機能

### Visual Studio Code 推奨拡張機能

#### 🔴 Tier 1: 必須

| 拡張機能 | ID | 用途 | 理由 |
|---------|-----|-----|-----|
| **Prettier** | `esbenp.prettier-vscode` | コードフォーマット | チーム統一のフォーマット |
| **ESLint** | `dbaeumer.vscode-eslint` | JavaScript/TypeScript静的解析 | コード品質の保証 |
| **GitLens** | `eamodio.gitlens` | Git拡張機能 | コード履歴の可視化 |
| **EditorConfig** | `editorconfig.editorconfig` | エディタ設定統一 | 設定の標準化 |
| **Error Lens** | `usernamehw.errorlens` | エラーインライン表示 | 問題の即座な発見 |
| **Path Intellisense** | `christian-kohler.path-intellisense` | パス補完 | ファイルパスの入力支援 |

#### 🟡 Tier 2: 強く推奨

| 拡張機能 | ID | 用途 | 理由 |
|---------|-----|-----|-----|
| **GitHub Copilot** | `github.copilot` | AI コード補完 | 開発速度の向上 |
| **Todo Tree** | `gruntfuggly.todo-tree` | TODOコメント管理 | タスク管理の可視化 |
| **Better Comments** | `aaron-bond.better-comments` | コメント装飾 | コメントの可読性向上 |
| **Bracket Pair Colorizer** | ビルトイン（v1.60+） | ブラケット可視化 | ネスト構造の理解 |
| **Import Cost** | `wix.vscode-import-cost` | importサイズ表示 | バンドルサイズ最適化 |
| **Code Spell Checker** | `streetsidesoftware.code-spell-checker` | スペルチェック | タイポの防止 |
| **Peacock** | `johnpapa.vscode-peacock` | ワークスペース色分け | マルチプロジェクト管理 |
| **Remote - SSH** | `ms-vscode-remote.remote-ssh` | SSH接続 | リモート開発環境 |

#### 🟢 Tier 3: 任意（用途に応じて）

| 拡張機能 | ID | 用途 | 理由 |
|---------|-----|-----|-----|
| **REST Client** | `humao.rest-client` | API テスト | Postmanの代替 |
| **Thunder Client** | `rangav.vscode-thunder-client` | API テスト | より高機能なAPIクライアント |
| **Live Share** | `ms-vsliveshare.vsliveshare` | リアルタイム共同編集 | ペアプログラミング |
| **Polacode** | `pnp.polacode` | コードスクリーンショット | ドキュメント作成 |
| **Bookmarks** | `alefragnani.bookmarks` | コードブックマーク | ナビゲーション支援 |

### 言語別拡張機能

#### TypeScript/JavaScript

| 拡張機能 | ID | 必須度 |
|---------|-----|--------|
| **TypeScript Hero** | `rbbit.typescript-hero` | 🟡 推奨 |
| **npm Intellisense** | `christian-kohler.npm-intellisense` | 🟡 推奨 |
| **ES7+ React/Redux/React-Native snippets** | `dsznajder.es7-react-js-snippets` | 🟢 任意 |
| **Auto Import** | `steoates.autoimport` | 🟢 任意 |

#### Python

| 拡張機能 | ID | 必須度 |
|---------|-----|--------|
| **Python** | `ms-python.python` | 🔴 必須 |
| **Pylance** | `ms-python.vscode-pylance` | 🔴 必須 |
| **Black Formatter** | `ms-python.black-formatter` | 🔴 必須 |
| **isort** | `ms-python.isort` | 🟡 推奨 |
| **Python Docstring Generator** | `njpwerner.autodocstring` | 🟡 推奨 |

#### Java

| 拡張機能 | ID | 必須度 |
|---------|-----|--------|
| **Java Extension Pack** | `vscjava.vscode-java-pack` | 🔴 必須 |
| **Checkstyle for Java** | `shengchen.vscode-checkstyle` | 🟡 推奨 |
| **Maven for Java** | `vscjava.vscode-maven` | 🟡 推奨 |

#### データベース

| 拡張機能 | ID | 必須度 |
|---------|-----|--------|
| **SQLTools** | `mtxr.sqltools` | 🟡 推奨 |
| **PostgreSQL** | `ckolkman.vscode-postgres` | 🟢 任意 |
| **MongoDB for VS Code** | `mongodb.mongodb-vscode` | 🟢 任意 |

#### DevOps

| 拡張機能 | ID | 必須度 |
|---------|-----|--------|
| **Docker** | `ms-azuretools.vscode-docker` | 🟡 推奨 |
| **Kubernetes** | `ms-kubernetes-tools.vscode-kubernetes-tools` | 🟡 推奨 |
| **YAML** | `redhat.vscode-yaml` | 🟡 推奨 |
| **Terraform** | `hashicorp.terraform` | 🟢 任意 |

---

## 🤖 AI開発支援ツール

### コード生成・補完

| ツール | 用途 | 必須度 | 特徴 |
|-------|-----|--------|-----|
| **GitHub Copilot** | コード補完 | 🟡 推奨 | 最も人気のあるAI補完 |
| **Cursor** | AIエディタ | 🟢 任意 | VSCode互換のAIネイティブエディタ |
| **Tabnine** | コード補完 | 🟢 任意 | プライベートモデル対応 |
| **Amazon CodeWhisperer** | コード補完 | 🟢 任意 | AWS統合 |
| **Devin** | 自律開発AI | 🟢 任意 | フルスタック開発支援 |

### ドキュメント生成

| ツール | 用途 | 必須度 |
|-------|-----|--------|
| **GitHub Copilot for Docs** | ドキュメント生成 | 🟢 任意 |
| **Mintlify** | API ドキュメント自動生成 | 🟢 任意 |
| **Swimm** | コードドキュメント同期 | 🟢 任意 |

---

## 👥 コラボレーションツール

### コミュニケーション

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Slack** | チャット | 🔴 必須 | https://slack.com |
| **Microsoft Teams** | チャット・会議 | 🟡 推奨 | https://teams.microsoft.com |
| **Discord** | コミュニティ | 🟢 任意 | https://discord.com |

### プロジェクト管理

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Jira** | タスク管理 | 🟡 推奨 | https://www.atlassian.com/software/jira |
| **Linear** | タスク管理 | 🟢 任意 | https://linear.app |
| **GitHub Projects** | タスク管理 | 🟡 推奨 | https://github.com/features/issues |
| **Notion** | ドキュメント・Wiki | 🟡 推奨 | https://www.notion.so |
| **Confluence** | ドキュメント管理 | 🟢 任意 | https://www.atlassian.com/software/confluence |

### コードレビュー

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **GitHub** | バージョン管理・レビュー | 🔴 必須 | https://github.com |
| **GitLab** | DevOpsプラットフォーム | 🟢 任意 | https://gitlab.com |
| **Bitbucket** | バージョン管理 | 🟢 任意 | https://bitbucket.org |

---

## 🛠️ 開発ユーティリティ

### ターミナル・シェル

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **iTerm2** (macOS) | ターミナルエミュレータ | 🟡 推奨 | https://iterm2.com |
| **Windows Terminal** | ターミナル | 🟡 推奨 | https://aka.ms/terminal |
| **Oh My Zsh** | Zshフレームワーク | 🟡 推奨 | https://ohmyz.sh |
| **Starship** | シェルプロンプト | 🟢 任意 | https://starship.rs |

### API開発・テスト

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Postman** | APIテスト | 🟡 推奨 | https://www.postman.com |
| **Insomnia** | APIテスト | 🟢 任意 | https://insomnia.rest |
| **Bruno** | オープンソースAPIクライアント | 🟢 任意 | https://www.usebruno.com |

### データベース管理

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **DBeaver** | 汎用DBクライアント | 🟡 推奨 | https://dbeaver.io |
| **TablePlus** | 軽量DBクライアント | 🟢 任意 | https://tableplus.com |
| **pgAdmin** | PostgreSQL管理 | 🟢 任意 | https://www.pgadmin.org |
| **MongoDB Compass** | MongoDB管理 | 🟢 任意 | https://www.mongodb.com/products/compass |

### デザイン・プロトタイピング

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Figma** | UIデザイン | 🟡 推奨 | https://www.figma.com |
| **Sketch** (macOS) | UIデザイン | 🟢 任意 | https://www.sketch.com |
| **Adobe XD** | UIデザイン | 🟢 任意 | https://www.adobe.com/products/xd.html |

---

## 🧪 品質・テストツール

### テスト自動化

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Jest** | JavaScriptテスト | 🔴 必須 | https://jestjs.io |
| **Pytest** | Pythonテスト | 🔴 必須 | https://pytest.org |
| **Cypress** | E2Eテスト | 🟡 推奨 | https://www.cypress.io |
| **Playwright** | E2Eテスト | 🟡 推奨 | https://playwright.dev |
| **Selenium** | E2Eテスト | 🟢 任意 | https://www.selenium.dev |

### コード品質

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **SonarQube** | 静的解析 | 🟡 推奨 | https://www.sonarsource.com/products/sonarqube/ |
| **CodeClimate** | 品質分析 | 🟢 任意 | https://codeclimate.com |
| **Snyk** | セキュリティスキャン | 🟡 推奨 | https://snyk.io |

---

## 🚀 DevOpsツール

### コンテナ・オーケストレーション

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Docker** | コンテナ化 | 🔴 必須 | https://www.docker.com |
| **Kubernetes** | オーケストレーション | 🟡 推奨 | https://kubernetes.io |
| **Helm** | Kubernetesパッケージ管理 | 🟡 推奨 | https://helm.sh |
| **k9s** | Kubernetes CLI | 🟢 任意 | https://k9scli.io |

### CI/CD

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **GitHub Actions** | CI/CD | 🔴 必須 | https://github.com/features/actions |
| **GitLab CI/CD** | CI/CD | 🟢 任意 | https://docs.gitlab.com/ee/ci/ |
| **Jenkins** | CI/CD | 🟢 任意 | https://www.jenkins.io |
| **CircleCI** | CI/CD | 🟢 任意 | https://circleci.com |

### 監視・ログ

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **Prometheus** | メトリクス収集 | 🟡 推奨 | https://prometheus.io |
| **Grafana** | メトリクス可視化 | 🟡 推奨 | https://grafana.com |
| **ELK Stack** | ログ管理 | 🟡 推奨 | https://www.elastic.co/elastic-stack |
| **Datadog** | APM | 🟢 任意 | https://www.datadoghq.com |
| **New Relic** | APM | 🟢 任意 | https://newrelic.com |

---

## 📦 パッケージマネージャー

| ツール | 用途 | 必須度 | URL |
|-------|-----|--------|-----|
| **npm** | Node.jsパッケージ管理 | 🔴 必須 | https://www.npmjs.com |
| **pnpm** | 高速Node.jsパッケージ管理 | 🟡 推奨 | https://pnpm.io |
| **Poetry** | Pythonパッケージ管理 | 🟡 推奨 | https://python-poetry.org |
| **Maven** | Javaビルドツール | 🟡 推奨 | https://maven.apache.org |
| **Gradle** | Javaビルドツール | 🟡 推奨 | https://gradle.org |

---

## 🔧 セットアップスクリプト

### 一括インストールスクリプト（macOS）

```bash
#!/bin/bash
# install-dev-tools.sh

# Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 必須ツール
brew install git node python docker kubectl

# 推奨ツール
brew install --cask visual-studio-code iterm2 postman docker figma

# VSCode拡張機能
code --install-extension esbenp.prettier-vscode
code --install-extension dbaeumer.vscode-eslint
code --install-extension eamodio.gitlens
code --install-extension editorconfig.editorconfig
code --install-extension usernamehw.errorlens
code --install-extension github.copilot

# Python
pip install pylint black flake8 isort pytest

# Node.js
npm install -g typescript prettier eslint
```

### 一括インストールスクリプト（Windows）

```powershell
# install-dev-tools.ps1

# Chocolatey
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 必須ツール
choco install -y git nodejs python docker-desktop vscode

# VSCode拡張機能
code --install-extension esbenp.prettier-vscode
code --install-extension dbaeumer.vscode-eslint
code --install-extension eamodio.gitlens
code --install-extension editorconfig.editorconfig
code --install-extension usernamehw.errorlens
```

---

## 📊 拡張機能管理

### extensions.txt（VSCode用）

```
esbenp.prettier-vscode
dbaeumer.vscode-eslint
eamodio.gitlens
editorconfig.editorconfig
usernamehw.errorlens
christian-kohler.path-intellisense
github.copilot
gruntfuggly.todo-tree
aaron-bond.better-comments
streetsidesoftware.code-spell-checker
ms-python.python
ms-python.vscode-pylance
ms-python.black-formatter
ms-azuretools.vscode-docker
ms-kubernetes-tools.vscode-kubernetes-tools
```

### 一括インストール

```bash
cat extensions.txt | xargs -L 1 code --install-extension
```

---

## 🔍 ツール選定基準

### 評価項目

1. **必須度**: プロジェクトでの必要性
2. **学習コスト**: 習得の容易さ
3. **コミュニティサポート**: 情報量・アップデート頻度
4. **統合性**: 既存ツールとの連携
5. **コスト**: ライセンス費用

### 新規ツール導入プロセス

1. **提案**: チームメンバーが新規ツールを提案
2. **評価**: 評価項目に基づいて検討
3. **試用**: 小規模プロジェクトでの試験導入
4. **承認**: チームリーダーによる承認
5. **標準化**: このドキュメントへの追加

---

## 📚 参考リソース

- **VSCode Marketplace**: https://marketplace.visualstudio.com/vscode
- **Awesome Lists**: https://github.com/sindresorhus/awesome
- **Stack Overflow Developer Survey**: https://survey.stackoverflow.co/

---

## 🔄 更新履歴

### v1.0.0 (2025-10-27)
- 初版作成
- VSCode拡張機能リストを定義
- AI開発支援ツールを追加
- DevOpsツールリストを追加
- 一括インストールスクリプトを追加
