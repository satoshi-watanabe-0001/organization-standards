## 1. 基本設定・ツール設定

### 1.1 推奨ツールチェーン

#### **必須ツール**
```txt
# requirements-dev.txt
# コード品質・フォーマット
black==23.9.1
isort==5.12.0
flake8==6.0.0
mypy==1.5.1
pylint==2.17.5

# テスト
pytest==7.4.2
pytest-cov==4.1.0
pytest-django==4.5.2
pytest-asyncio==0.21.1
factory-boy==3.3.0

# セキュリティ
bandit==1.7.5
safety==2.3.1

# 依存関係管理
pip-tools==7.3.0
pipenv==2023.9.8

# 開発支援
pre-commit==3.4.0
tox==4.11.3
```

#### **pyproject.toml設定**
```toml
[build-system]
requires = ["setuptools>=61.0", "wheel"]
build-backend = "setuptools.build_meta"

[project]
name = "your-project-name"
version = "0.1.0"
description = "Project description"
authors = [
    {name = "Your Name", email = "your.email@company.com"},
]
dependencies = [
    "django>=4.2,<5.0",
    "djangorestframework>=3.14.0",
    "fastapi>=0.100.0",
    "uvicorn>=0.23.0",
    "pydantic>=2.0.0",
    "sqlalchemy>=2.0.0",
    "celery>=5.3.0",
    "redis>=4.6.0",
    "psycopg2-binary>=2.9.0",
]
requires-python = ">=3.11"

[project.optional-dependencies]
dev = [
    "black",
    "isort",
    "flake8",
    "mypy",
    "pytest",
    "pytest-cov",
    "pre-commit",
]
ml = [
    "numpy>=1.24.0",
    "pandas>=2.0.0",
    "scikit-learn>=1.3.0",
    "tensorflow>=2.13.0",
    "torch>=2.0.0",
]

# Black設定
[tool.black]
line-length = 88
target-version = ['py311']
include = '\.pyi?$'
extend-exclude = '''
/(
  # directories
  \.eggs
  | \.git
  | \.mypy_cache
  | \.tox
  | \.venv
  | venv
  | _build
  | buck-out
  | build
  | dist
  | migrations
)/
'''

# isort設定
[tool.isort]
profile = "black"
multi_line_output = 3
line_length = 88
include_trailing_comma = true
force_grid_wrap = 0
use_parentheses = true
ensure_newline_before_comments = true
skip_gitignore = true
skip_glob = ["**/migrations/*.py"]

# MyPy設定
[tool.mypy]
python_version = "3.11"
check_untyped_defs = true
disallow_any_generics = true
disallow_incomplete_defs = true
disallow_untyped_defs = true
no_implicit_optional = true
warn_redundant_casts = true
warn_unused_ignores = true
warn_return_any = true
strict_equality = true
show_error_codes = true

[[tool.mypy.overrides]]
module = [
    "django.*",
    "rest_framework.*",
    "celery.*",
    "pytest.*",
]
ignore_missing_imports = true

# pytest設定
[tool.pytest.ini_options]
DJANGO_SETTINGS_MODULE = "config.settings.test"
python_files = ["tests.py", "test_*.py", "*_tests.py"]
python_classes = ["Test*"]
python_functions = ["test_*"]
addopts = [
    "--cov=src",
    "--cov-report=html",
    "--cov-report=term-missing",
    "--cov-fail-under=80",
    "--strict-markers",
    "--strict-config",
    "--disable-warnings",
]
markers = [
    "unit: Unit tests",
    "integration: Integration tests",
    "e2e: End-to-end tests",
    "slow: Slow running tests",
]

# Coverage設定
[tool.coverage.run]
source = ["src"]
omit = [
    "*/migrations/*",
    "*/venv/*",
    "*/tests/*",
    "manage.py",
    "*/settings/*",
    "*/wsgi.py",
    "*/asgi.py",
]

[tool.coverage.report]
exclude_lines = [
    "pragma: no cover",
    "def __repr__",
    "raise AssertionError",
    "raise NotImplementedError",
    "if __name__ == .__main__.:",
    "class .*\\(Protocol\\):",
    "@(abc\\.)?abstractmethod",
]
```

#### **.flake8設定**
```ini
[flake8]
max-line-length = 88
max-complexity = 10
select = E,W,F,C,N
ignore = 
    E203,  # whitespace before ':'
    E501,  # line too long (handled by black)
    W503,  # line break before binary operator
    F401,  # imported but unused (handled by isort)
exclude = 
    .git,
    __pycache__,
    .venv,
    venv,
    .tox,
    .eggs,
    *.egg,
    build,
    dist,
    .mypy_cache,
    migrations
per-file-ignores =
    __init__.py:F401
    settings/*.py:F405,F403
    tests/*.py:S101,S106
```

#### **pre-commit設定（.pre-commit-config.yaml）**
```yaml
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
      - id: trailing-whitespace
      - id: end-of-file-fixer
      - id: check-yaml
      - id: check-added-large-files
      - id: check-merge-conflict
      - id: debug-statements

  - repo: https://github.com/psf/black
    rev: 23.9.1
    hooks:
      - id: black
        language_version: python3.11

  - repo: https://github.com/pycqa/isort
    rev: 5.12.0
    hooks:
      - id: isort

  - repo: https://github.com/pycqa/flake8
    rev: 6.0.0
    hooks:
      - id: flake8

  - repo: https://github.com/pre-commit/mirrors-mypy
    rev: v1.5.1
    hooks:
      - id: mypy
        additional_dependencies: [types-all]

  - repo: https://github.com/pycqa/bandit
    rev: 1.7.5
    hooks:
      - id: bandit
        args: ['-c', 'pyproject.toml']
        additional_dependencies: ['bandit[toml]']

  - repo: https://github.com/python-poetry/poetry
    rev: 1.6.1
    hooks:
      - id: poetry-check
      - id: poetry-lock
        args: [--no-update]
```

#### **Bandit設定（セキュリティ）**
```toml
# pyproject.toml内に追加
[tool.bandit]
exclude_dirs = ["tests", "venv", ".venv", "migrations"]
skips = ["B101", "B601"]  # assert文、shell=True（テストで使用）
```

---

