## 11. Devin実行ガイドライン

### 11.1 AI開発エージェント対応

#### 11.1.1 Devinコードジェネレーション指針

**Good: Devinに最適化されたコード構造**
```python
# Devin AIエージェント対応コードジェネレーション指針
"""
Devin AIエージェント向けコードジェネレーションガイドライン

このファイルはDevin AIエージェントが効率的にコードを生成・修正できるように
設計されたベストプラクティスとパターン集です。

Key Principles for Devin Integration:
1. 明確で一貫したコード構造
2. 包括的なドキュメンテーション
3. 自動テスト可能な設計
4. モジュラーで再利用可能なコンポーネント
5. エラーハンドリングとログシステムの統合
"""

from typing import Dict, List, Any, Optional, Union, Callable, Protocol
from dataclasses import dataclass, field
from abc import ABC, abstractmethod
from enum import Enum
import logging
import json
from datetime import datetime
from pathlib import Path
import asyncio
from functools import wraps
import inspect

# === DEVIN向けコードジェネレーションパターン ===

class DevinCompatibleComponent(ABC):
    """
    Devin互換コンポーネントベースクラス
    
    このクラスを継承することで、Devinが理解しやすい
    一貫したインターフェースを持つコンポーネントを作成できます。
    """
    
    def __init__(self, name: str, config: Optional[Dict[str, Any]] = None):
        self.name = name
        self.config = config or {}
        self.logger = logging.getLogger(f"{self.__class__.__name__}.{name}")
        self._initialized = False
        self._metadata = self._generate_metadata()
    
    @abstractmethod
    def initialize(self) -> bool:
        """
        コンポーネントの初期化処理
        
        Returns:
            bool: 初期化成功時True
            
        Devin Note:
        - このメソッドは必ず実装してください
        - 初期化に必要なすべての処理をここに集約してください
        - エラー時はFalseを返し、ログに詳細を記録してください
        """
        pass
    
    @abstractmethod
    def process(self, input_data: Any) -> Any:
        """
        メイン処理ロジック
        
        Args:
            input_data: 入力データ
            
        Returns:
            処理結果
            
        Devin Note:
        - 入力データのバリデーションを必ず実装してください
        - 処理結果は一貫した形式で返してください
        - 例外は適切にキャッチし、意味のあるエラーメッセージを提供してください
        """
        pass
    
    def validate_input(self, input_data: Any) -> bool:
        """
        入力データのバリデーション
        
        Args:
            input_data: 検証するデータ
            
        Returns:
            bool: バリデーション成功時True
            
        Devin Note:
        - このメソッドをオーバーライドして入力検証ロジックを実装してください
        """
        return True
    
    def get_metadata(self) -> Dict[str, Any]:
        """
        コンポーネントのメタデータ取得
        
        Returns:
            Dict[str, Any]: コンポーネント情報
            
        Devin Note:
        - Devinはこのメタデータを使用してコンポーネントを理解します
        - 正確で詳細な情報を含めてください
        """
        return self._metadata
    
    def _generate_metadata(self) -> Dict[str, Any]:
        """メタデータ自動生成"""
        return {
            "component_name": self.name,
            "class_name": self.__class__.__name__,
            "version": "1.0.0",
            "description": self.__doc__ or "No description provided",
            "methods": self._get_public_methods(),
            "config_schema": self._get_config_schema(),
            "created_at": datetime.utcnow().isoformat()
        }
    
    def _get_public_methods(self) -> List[str]:
        """パブリックメソッド一覧取得"""
        return [method for method in dir(self) 
                if not method.startswith('_') and callable(getattr(self, method))]
    
    def _get_config_schema(self) -> Dict[str, str]:
        """設定スキーマ取得"""
        # サブクラスでオーバーライドして実装
        return {"config": "Generic configuration"}

# === 具体的な実装例 ===

class DataProcessor(DevinCompatibleComponent):
    """
    データ処理コンポーネント
    
    Devinが理解しやすいように設計されたデータ処理コンポーネント。
    各メソッドは明確な入力・出力とエラーハンドリングを持ちます。
    """
    
    def __init__(self, name: str = "data_processor", config: Optional[Dict[str, Any]] = None):
        default_config = {
            "batch_size": 1000,
            "max_retries": 3,
            "timeout_seconds": 30,
            "validation_enabled": True
        }
        final_config = {**default_config, **(config or {})}
        super().__init__(name, final_config)
        
        self.batch_size = self.config["batch_size"]
        self.max_retries = self.config["max_retries"]
        self.timeout_seconds = self.config["timeout_seconds"]
        self.validation_enabled = self.config["validation_enabled"]
    
    def initialize(self) -> bool:
        """
        データプロセッサーの初期化
        
        Returns:
            bool: 初期化成功敲True
        """
        try:
            self.logger.info(f"Initializing DataProcessor '{self.name}'")
            
            # 初期化処理をここに実装
            # 例: データベース接続、キャッシュ初期化など
            
            self._initialized = True
            self.logger.info("DataProcessor initialized successfully")
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to initialize DataProcessor: {e}")
            return False
    
    def process(self, input_data: Union[List[Dict], Dict]) -> Dict[str, Any]:
        """
        データ処理メインロジック
        
        Args:
            input_data: 処理するデータ（辞書または辞書のリスト）
            
        Returns:
            Dict[str, Any]: 処理結果
            {
                "success": bool,
                "processed_count": int,
                "results": List[Dict],
                "errors": List[str],
                "processing_time": float
            }
            
        Raises:
            ValueError: 入力データが無効な場合
            ProcessingError: 処理中にエラーが発生した場合
        """
        if not self._initialized:
            raise RuntimeError("DataProcessor not initialized. Call initialize() first.")
        
        start_time = datetime.utcnow()
        
        try:
            # 入力検証
            if not self.validate_input(input_data):
                raise ValueError("Invalid input data provided")
            
            # データ正規化
            normalized_data = self._normalize_input(input_data)
            
            # バッチ処理
            results = []
            errors = []
            
            for batch in self._create_batches(normalized_data):
                try:
                    batch_result = self._process_batch(batch)
                    results.extend(batch_result)
                except Exception as e:
                    error_msg = f"Batch processing failed: {str(e)}"
                    self.logger.error(error_msg)
                    errors.append(error_msg)
            
            # 結果組立
            processing_time = (datetime.utcnow() - start_time).total_seconds()
            
            result = {
                "success": len(errors) == 0,
                "processed_count": len(results),
                "results": results,
                "errors": errors,
                "processing_time": processing_time
            }
            
            self.logger.info(
                f"Processing completed: {len(results)} items processed, "
                f"{len(errors)} errors, {processing_time:.2f}s"
            )
            
            return result
            
        except Exception as e:
            self.logger.error(f"Data processing failed: {e}")
            raise ProcessingError(f"Failed to process data: {str(e)}") from e
    
    def validate_input(self, input_data: Any) -> bool:
        """
        入力データのバリデーション
        
        Args:
            input_data: 検証するデータ
            
        Returns:
            bool: バリデーション成功時True
        """
        if not self.validation_enabled:
            return True
        
        if input_data is None:
            self.logger.error("Input data is None")
            return False
        
        if isinstance(input_data, dict):
            return self._validate_dict(input_data)
        elif isinstance(input_data, list):
            return all(self._validate_dict(item) if isinstance(item, dict) else False 
                      for item in input_data)
        else:
            self.logger.error(f"Unsupported input type: {type(input_data)}")
            return False
    
    def _validate_dict(self, data: Dict) -> bool:
        """辞書データのバリデーション"""
        # 必要なフィールドのチェック
        required_fields = ["id", "data"]
        for field in required_fields:
            if field not in data:
                self.logger.error(f"Missing required field: {field}")
                return False
        
        return True
    
    def _normalize_input(self, input_data: Union[List[Dict], Dict]) -> List[Dict]:
        """入力データの正規化"""
        if isinstance(input_data, dict):
            return [input_data]
        return input_data
    
    def _create_batches(self, data: List[Dict]) -> List[List[Dict]]:
        """データをバッチに分割"""
        batches = []
        for i in range(0, len(data), self.batch_size):
            batch = data[i:i + self.batch_size]
            batches.append(batch)
        return batches
    
    def _process_batch(self, batch: List[Dict]) -> List[Dict]:
        """バッチ処理"""
        results = []
        for item in batch:
            # 具体的な処理ロジックをここに実装
            processed_item = {
                "id": item["id"],
                "processed_data": item["data"],
                "processed_at": datetime.utcnow().isoformat()
            }
            results.append(processed_item)
        
        return results
    
    def _get_config_schema(self) -> Dict[str, str]:
        """設定スキーマ定義"""
        return {
            "batch_size": "int: Number of items to process per batch (default: 1000)",
            "max_retries": "int: Maximum number of retry attempts (default: 3)",
            "timeout_seconds": "int: Processing timeout in seconds (default: 30)",
            "validation_enabled": "bool: Enable input validation (default: True)"
        }

# === カスタム例外クラス ===

class ProcessingError(Exception):
    """
    データ処理エラー
    
    Devin Note:
    カスタム例外クラスは具体的で意味のあるエラー情報を提供します。
    """
    pass

# === Devinコードジェネレーションヘルパー ===

class DevinCodeHelper:
    """
    Devin AIエージェント向けコードジェネレーションヘルパー
    
    Devinが効率的にコードを生成・修正できるようにサポートするユーティリティ。
    """
    
    @staticmethod
    def generate_component_template(component_name: str, component_type: str = "processor") -> str:
        """
        Devin用コンポーネントテンプレート生成
        
        Args:
            component_name: コンポーネント名
            component_type: コンポーネントタイプ
            
        Returns:
            str: 生成されたコードテンプレート
        """
        
        class_name = ''.join(word.capitalize() for word in component_name.split('_'))
        
        template = f'''
class {class_name}(DevinCompatibleComponent):
    """
    {component_name}コンポーネント
    
    TODO: Devin, please implement the following:
    1. Add specific configuration parameters
    2. Implement initialization logic
    3. Implement main processing logic
    4. Add input validation
    5. Add error handling
    6. Add logging
    """
    
    def __init__(self, config: Optional[Dict[str, Any]] = None):
        default_config = {{
            # TODO: Devin, add specific configuration parameters here
            "example_param": "default_value"
        }}
        final_config = {{**default_config, **(config or {})}}
        super().__init__("{component_name}", final_config)
        
        # TODO: Devin, initialize component-specific attributes here
    
    def initialize(self) -> bool:
        """
        {class_name}の初期化
        
        TODO: Devin, implement initialization logic:
        - Validate configuration
        - Initialize external connections
        - Set up internal state
        - Return True on success, False on failure
        """
        try:
            self.logger.info(f"Initializing {{self.name}}")
            
            # TODO: Devin, add initialization code here
            
            self._initialized = True
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to initialize {{self.name}}: {{e}}")
            return False
    
    def process(self, input_data: Any) -> Any:
        """
        メイン処理ロジック
        
        Args:
            input_data: 入力データ
            
        Returns:
            処理結果
            
        TODO: Devin, implement main processing logic:
        - Validate input using self.validate_input()
        - Process the data
        - Handle errors appropriately
        - Return structured results
        """
        if not self._initialized:
            raise RuntimeError(f"{{self.name}} not initialized. Call initialize() first.")
        
        try:
            # TODO: Devin, implement processing logic here
            if not self.validate_input(input_data):
                raise ValueError("Invalid input data")
            
            # Processing logic goes here
            result = input_data  # Placeholder
            
            return result
            
        except Exception as e:
            self.logger.error(f"Processing failed in {{self.name}}: {{e}}")
            raise
    
    def validate_input(self, input_data: Any) -> bool:
        """
        入力データのバリデーション
        
        TODO: Devin, implement input validation:
        - Check data types
        - Validate required fields
        - Check data ranges/constraints
        - Return True if valid, False otherwise
        """
        # TODO: Devin, add validation logic here
        return input_data is not None
    
    def _get_config_schema(self) -> Dict[str, str]:
        """設定スキーマ定義"""
        return {{
            # TODO: Devin, define configuration schema here
            "example_param": "str: Example parameter description"
        }}
'''
        return template
    
    @staticmethod
    def generate_test_template(component_name: str) -> str:
        """
        Devin用テストテンプレート生成
        
        Args:
            component_name: コンポーネント名
            
        Returns:
            str: 生成されたテストコードテンプレート
        """
        
        class_name = ''.join(word.capitalize() for word in component_name.split('_'))
        
        template = f'''
import pytest
from unittest.mock import Mock, patch
from {component_name} import {class_name}

class Test{class_name}:
    """
    {class_name}のテストクラス
    
    TODO: Devin, implement comprehensive tests:
    1. Test initialization
    2. Test main processing logic
    3. Test input validation
    4. Test error handling
    5. Test edge cases
    """
    
    def setup_method(self):
        """Test setup - runs before each test method"""
        self.config = {{
            # TODO: Devin, add test configuration here
        }}
        self.component = {class_name}(self.config)
    
    def test_initialization_success(self):
        """正常な初期化のテスト"""
        # TODO: Devin, implement initialization test
        result = self.component.initialize()
        assert result is True
        assert self.component._initialized is True
    
    def test_initialization_failure(self):
        """初期化失敗のテスト"""
        # TODO: Devin, implement failure case test
        pass
    
    def test_process_valid_input(self):
        """正常な入力での処理テスト"""
        # TODO: Devin, implement valid input test
        self.component.initialize()
        
        test_input = {{
            # TODO: Devin, add test input data
        }}
        
        result = self.component.process(test_input)
        
        # TODO: Devin, add result assertions
        assert result is not None
    
    def test_process_invalid_input(self):
        """無効な入力での処理テスト"""
        # TODO: Devin, implement invalid input test
        self.component.initialize()
        
        invalid_input = None  # TODO: Devin, define invalid input
        
        with pytest.raises(ValueError):
            self.component.process(invalid_input)
    
    def test_process_without_initialization(self):
        """未初期化状態での処理テスト"""
        test_input = {{}}  # TODO: Devin, add test input
        
        with pytest.raises(RuntimeError):
            self.component.process(test_input)
    
    def test_validate_input_valid(self):
        """正常な入力バリデーションテスト"""
        # TODO: Devin, implement validation test
        valid_input = {{}}  # TODO: Devin, define valid input
        result = self.component.validate_input(valid_input)
        assert result is True
    
    def test_validate_input_invalid(self):
        """無効な入力バリデーションテスト"""
        # TODO: Devin, implement validation test
        invalid_input = None
        result = self.component.validate_input(invalid_input)
        assert result is False
    
    def test_get_metadata(self):
        """メタデータ取得テスト"""
        metadata = self.component.get_metadata()
        
        assert "component_name" in metadata
        assert "class_name" in metadata
        assert "version" in metadata
        assert metadata["class_name"] == "{class_name}"
    
    # TODO: Devin, add more specific test methods based on component functionality
'''
        return template
    
    @staticmethod
    def create_component_files(component_name: str, output_dir: Path = Path(".")):
        """
        Devin用コンポーネントファイル一式生成
        
        Args:
            component_name: コンポーネント名
            output_dir: 出力ディレクトリ
        """
        output_dir = Path(output_dir)
        output_dir.mkdir(exist_ok=True)
        
        # コンポーネントファイル
        component_file = output_dir / f"{component_name}.py"
        component_code = DevinCodeHelper.generate_component_template(component_name)
        
        with open(component_file, 'w', encoding='utf-8') as f:
            f.write("# -*- coding: utf-8 -*-\n")
            f.write(f'"""\n{component_name} Component\n\nGenerated by DevinCodeHelper\nDate: {datetime.utcnow().isoformat()}\n"""\n')
            f.write(component_code)
        
        # テストファイル
        test_file = output_dir / f"test_{component_name}.py"
        test_code = DevinCodeHelper.generate_test_template(component_name)
        
        with open(test_file, 'w', encoding='utf-8') as f:
            f.write("# -*- coding: utf-8 -*-\n")
            f.write(f'"""\nTests for {component_name} Component\n\nGenerated by DevinCodeHelper\nDate: {datetime.utcnow().isoformat()}\n"""\n')
            f.write(test_code)
        
        print(f"Generated files for {component_name}:")
        print(f"  Component: {component_file}")
        print(f"  Tests: {test_file}")

# === 使用例 ===

if __name__ == "__main__":
    # Devin用コンポーネントの使用例
    
    print("=== Devin Compatible Component Example ===")
    
    # 1. データプロセッサーの作成と初期化
    processor = DataProcessor(
        name="example_processor",
        config={
            "batch_size": 100,
            "validation_enabled": True
        }
    )
    
    if processor.initialize():
        print("✓ DataProcessor initialized successfully")
        
        # 2. メタデータ表示
        metadata = processor.get_metadata()
        print(f"Component: {metadata['component_name']}")
        print(f"Class: {metadata['class_name']}")
        print(f"Methods: {', '.join(metadata['methods'])}")
        
        # 3. テストデータ処理
        test_data = [
            {"id": 1, "data": "test data 1"},
            {"id": 2, "data": "test data 2"}
        ]
        
        try:
            result = processor.process(test_data)
            print(f"✓ Processing completed: {result['processed_count']} items")
            
        except Exception as e:
            print(f"✗ Processing failed: {e}")
    
    else:
        print("✗ Failed to initialize DataProcessor")
    
    # 4. 新しいコンポーネントのテンプレート生成例
    print("\n=== Generating New Component Template ===")
    
    # コンポーネントテンプレート生成
    template = DevinCodeHelper.generate_component_template("email_sender")
    print("Generated component template (first 500 chars):")
    print(template[:500] + "...")
    
    # ファイル作成例（コメントアウト）
    # DevinCodeHelper.create_component_files("email_sender", Path("./generated"))
    
    print("\n=== Devin Instructions ===")
    print("""
    Devin AI Agent Instructions:
    
    1. Use DevinCompatibleComponent as base class for all new components
    2. Always implement initialize(), process(), and validate_input() methods
    3. Include comprehensive error handling and logging
    4. Follow the established patterns for configuration management
    5. Generate tests using the test template helper
    6. Document all methods with clear docstrings
    7. Use type hints for all parameters and return values
    8. Include TODO comments for areas that need implementation
    
    Example workflow:
    1. Create component using template: DevinCodeHelper.generate_component_template()
    2. Implement required methods following the patterns shown
    3. Generate tests using: DevinCodeHelper.generate_test_template()
    4. Test thoroughly before deployment
    
    This structure ensures consistent, maintainable, and testable code.
    """)
```

### 11.2 自動化可能な開発タスク

#### 11.2.1 Devin自動化タスクリスト

**Devinが効率的に実行できるタスク一覧**

```python
# Devin自動化タスク定義
from enum import Enum
from dataclasses import dataclass
from typing import List, Dict, Any, Optional

class TaskComplexity(Enum):
    """タスク複雑さレベル"""
    SIMPLE = "simple"          # 1-2時間で完了
    MODERATE = "moderate"      # 2-6時間で完了
    COMPLEX = "complex"        # 6-24時間で完了
    ADVANCED = "advanced"      # 1-3日で完了

class TaskCategory(Enum):
    """タスクカテゴリ"""
    CODE_GENERATION = "code_generation"
    TESTING = "testing"
    DOCUMENTATION = "documentation"
    REFACTORING = "refactoring"
    API_DEVELOPMENT = "api_development"
    DATABASE = "database"
    DEPLOYMENT = "deployment"
    MONITORING = "monitoring"
    MACHINE_LEARNING = "machine_learning"
    DATA_PROCESSING = "data_processing"

@dataclass
class DevinTask:
    """デDevin自動化タスク定義"""
    name: str
    description: str
    category: TaskCategory
    complexity: TaskComplexity
    prerequisites: List[str]
    deliverables: List[str]
    example_prompt: str
    estimated_time: str
    automation_level: str  # "fully", "mostly", "partially"

# === コード生成タスク ===
CODE_GENERATION_TASKS = [
    DevinTask(
        name="APIエンドポイント作成",
        description="FastAPIを使用したRESTful APIエンドポイントの作成",
        category=TaskCategory.API_DEVELOPMENT,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["FastAPI基本設定", "Pydanticモデル", "データベース接続"],
        deliverables=[
            "エンドポイントコード",
            "リクエスト/レスポンスモデル",
            "エラーハンドリング",
            "APIドキュメント作成"
        ],
        example_prompt="""
        Create a FastAPI endpoint for user management with the following requirements:
        - GET /users: List all users with pagination
        - GET /users/{user_id}: Get specific user
        - POST /users: Create new user
        - PUT /users/{user_id}: Update user
        - DELETE /users/{user_id}: Delete user
        
        Include proper error handling, input validation, and OpenAPI documentation.
        """,
        estimated_time="2-4 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="データベースモデル作成",
        description="SQLAlchemyを使用したデータベースモデルの作成",
        category=TaskCategory.DATABASE,
        complexity=TaskComplexity.SIMPLE,
        prerequisites=["データベーススキーマ設計", "SQLAlchemy設定"],
        deliverables=[
            "SQLAlchemyモデルクラス",
            "リレーション設定",
            "バリデーションルール",
            "マイグレーションファイル"
        ],
        example_prompt="""
        Create SQLAlchemy models for a blog system with:
        - User model (id, username, email, password_hash, created_at)
        - Post model (id, title, content, user_id, created_at, updated_at)
        - Comment model (id, content, post_id, user_id, created_at)
        
        Include proper relationships, indexes, and validation.
        """,
        estimated_time="1-2 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="データ処理パイプライン",
        description="Pandasを使用したデータ処理パイプラインの作成",
        category=TaskCategory.DATA_PROCESSING,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["データソース確認", "処理要件定義"],
        deliverables=[
            "データ読み込み関数",
            "データクリーニング処理",
            "特徴量エンジニアリング",
            "データ出力関数",
            "パフォーマンス最適化"
        ],
        example_prompt="""
        Create a data processing pipeline that:
        1. Reads CSV files from multiple sources
        2. Cleans and validates the data
        3. Performs feature engineering (create new columns, handle missing values)
        4. Aggregates data by time periods
        5. Exports results to database and CSV
        
        Include error handling, logging, and performance optimization.
        """,
        estimated_time="3-6 hours",
        automation_level="mostly"
    )
]

# === テスト作成タスク ===
TESTING_TASKS = [
    DevinTask(
        name="単体テスト作成",
        description="pytestを使用した包括的な単体テストの作成",
        category=TaskCategory.TESTING,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["テスト対象コード", "pytest設定"],
        deliverables=[
            "テストケース作成",
            "モックオブジェクト設定",
            "テストデータ作成",
            "カバレッジレポート",
            "CI/CD統合"
        ],
        example_prompt="""
        Create comprehensive unit tests for the UserService class with:
        - Test all public methods
        - Test error conditions and edge cases
        - Use mocks for database interactions
        - Achieve 90%+ code coverage
        - Include parameterized tests for multiple scenarios
        
        Follow pytest best practices and include proper fixtures.
        """,
        estimated_time="2-4 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="API結合テスト",
        description="FastAPIアプリケーションの結合テスト作成",
        category=TaskCategory.TESTING,
        complexity=TaskComplexity.COMPLEX,
        prerequisites=["APIエンドポイント", "テストデータベース"],
        deliverables=[
            "APIテストケース",
            "データベーステスト統合",
            "認証テスト",
            "エラーハンドリングテスト",
            "パフォーマンステスト"
        ],
        example_prompt="""
        Create integration tests for the blog API that:
        - Test complete user workflows (register, login, create post, comment)
        - Test authentication and authorization
        - Test database transactions and rollbacks
        - Test file upload functionality
        - Test rate limiting and security measures
        
        Use test database with proper setup/teardown.
        """,
        estimated_time="4-8 hours",
        automation_level="mostly"
    )
]

# === ドキュメント作成タスク ===
DOCUMENTATION_TASKS = [
    DevinTask(
        name="APIドキュメント作成",
        description="OpenAPI/Swaggerを使用したAPIドキュメントの作成",
        category=TaskCategory.DOCUMENTATION,
        complexity=TaskComplexity.SIMPLE,
        prerequisites=["APIエンドポイント完成"],
        deliverables=[
            "OpenAPIスキーマ",
            "API使用例",
            "エラーコード一覧",
            "認証ガイド",
            "Postmanコレクション"
        ],
        example_prompt="""
        Create comprehensive API documentation for the user management system:
        - Complete OpenAPI schema with examples
        - Request/response examples for each endpoint
        - Authentication guide with sample tokens
        - Error handling documentation
        - Rate limiting information
        - SDK generation setup
        """,
        estimated_time="2-3 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="コードドキュメント作成",
        description="Sphinxを使用したコードドキュメントの作成",
        category=TaskCategory.DOCUMENTATION,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["コードベース完成", "docstring作成済み"],
        deliverables=[
            "Sphinx設定",
            "APIリファレンス",
            "チュートリアル",
            "コード例集",
            "HTML/PDF出力"
        ],
        example_prompt="""
        Create code documentation using Sphinx that includes:
        - Automatic API reference from docstrings
        - User guide with examples
        - Installation and setup instructions
        - Contributing guidelines
        - Architecture overview
        - Generate both HTML and PDF versions
        """,
        estimated_time="3-5 hours",
        automation_level="mostly"
    )
]

# === デプロイメントタスク ===
DEPLOYMENT_TASKS = [
    DevinTask(
        name="Dockerコンテナ化",
        description="アプリケーションのDockerコンテナ化と最適化",
        category=TaskCategory.DEPLOYMENT,
        complexity=TaskComplexity.MODERATE,
        prerequisites=["アプリケーションコード", "依存関係一覧"],
        deliverables=[
            "最適化されたDockerfile",
            "docker-compose.yml",
            "環境別設定ファイル",
            "ヘルスチェック設定",
            "ビルドスクリプト"
        ],
        example_prompt="""
        Create Docker configuration for a FastAPI application:
        - Multi-stage build for optimization
        - Non-root user for security
        - Health checks
        - Environment-specific configurations
        - Docker Compose for local development
        - Build and deployment scripts
        """,
        estimated_time="2-4 hours",
        automation_level="fully"
    ),
    
    DevinTask(
        name="CI/CDパイプライン構築",
        description="GitHub Actionsを使用したCI/CDパイプラインの作成",
        category=TaskCategory.DEPLOYMENT,
        complexity=TaskComplexity.COMPLEX,
        prerequisites=["テスト環境", "Docker設定", "デプロイ先環境"],
        deliverables=[
            "GitHub Actionsワークフロー",
            "テスト自動化",
            "セキュリティスキャン",
            "自動デプロイ",
            "ロールバック機能"
        ],
        example_prompt="""
        Create a complete CI/CD pipeline that:
        - Runs tests on multiple Python versions
        - Performs security and code quality checks
        - Builds and pushes Docker images
        - Deploys to staging automatically
        - Deploys to production with manual approval
        - Includes rollback capabilities
        - Sends notifications on failures
        """,
        estimated_time="6-12 hours",
        automation_level="mostly"
    )
]

# === 機械学習タスク ===
ML_TASKS = [
    DevinTask(
        name="MLモデルパイプライン作成",
        description="scikit-learnを使用した機械学習パイプラインの作成",
        category=TaskCategory.MACHINE_LEARNING,
        complexity=TaskComplexity.COMPLEX,
        prerequisites=["データセット", "問題設定", "評価指標定義"],
        deliverables=[
            "データ前処理パイプライン",
            "特徴量エンジニアリング",
            "モデル訓練コード",
            "ハイパーパラメーターチューニング",
            "モデル評価レポート"
        ],
        example_prompt="""
        Create a machine learning pipeline for customer churn prediction:
        - Data preprocessing with missing value handling
        - Feature engineering and selection
        - Multiple model comparison (RF, XGBoost, LR)
        - Hyperparameter tuning with cross-validation
        - Model evaluation with comprehensive metrics
        - Feature importance analysis
        - Model serialization for deployment
        """,
        estimated_time="8-16 hours",
        automation_level="mostly"
    )
]

# === 全タスク統合 ===
ALL_DEVIN_TASKS = (
    CODE_GENERATION_TASKS + 
    TESTING_TASKS + 
    DOCUMENTATION_TASKS + 
    DEPLOYMENT_TASKS + 
    ML_TASKS
)

# === Devinタスクマネージャー ===
class DevinTaskManager:
    """デDevinタスク管理クラス"""
    
    def __init__(self):
        self.tasks = {task.name: task for task in ALL_DEVIN_TASKS}
    
    def get_tasks_by_category(self, category: TaskCategory) -> List[DevinTask]:
        """カテゴリ別タスク取得"""
        return [task for task in self.tasks.values() if task.category == category]
    
    def get_tasks_by_complexity(self, complexity: TaskComplexity) -> List[DevinTask]:
        """複雑さ別タスク取得"""
        return [task for task in self.tasks.values() if task.complexity == complexity]
    
    def get_fully_automated_tasks(self) -> List[DevinTask]:
        """完全自動化可能タスク取得"""
        return [task for task in self.tasks.values() if task.automation_level == "fully"]
    
    def suggest_task_order(self, task_names: List[str]) -> List[str]:
        """タスク実行順序推奨"""
        # 簡単な依存関係ベースのソート
        ordered_tasks = []
        remaining_tasks = task_names.copy()
        
        while remaining_tasks:
            for task_name in remaining_tasks.copy():
                task = self.tasks[task_name]
                # 依存タスクが完了しているかチェック
                dependencies_met = all(
                    dep in ordered_tasks or dep not in task_names 
                    for dep in task.prerequisites
                )
                
                if dependencies_met:
                    ordered_tasks.append(task_name)
                    remaining_tasks.remove(task_name)
        
        return ordered_tasks
    
    def generate_task_report(self) -> str:
        """タスクレポート生成"""
        report = ["=== Devin Automation Task Report ==="]
        
        # カテゴリ別統計
        for category in TaskCategory:
            tasks = self.get_tasks_by_category(category)
            if tasks:
                report.append(f"\n{category.value.title()} Tasks: {len(tasks)}")
                for task in tasks:
                    report.append(f"  - {task.name} ({task.complexity.value}, {task.automation_level})")
        
        # 複雑さ別統計
        report.append("\n=== Complexity Distribution ===")
        for complexity in TaskComplexity:
            tasks = self.get_tasks_by_complexity(complexity)
            report.append(f"{complexity.value.title()}: {len(tasks)} tasks")
        
        # 自動化レベル統計
        automation_stats = {}
        for task in self.tasks.values():
            level = task.automation_level
            automation_stats[level] = automation_stats.get(level, 0) + 1
        
        report.append("\n=== Automation Level Distribution ===")
        for level, count in automation_stats.items():
            report.append(f"{level.title()}: {count} tasks")
        
        return "\n".join(report)

# 使用例
if __name__ == "__main__":
    task_manager = DevinTaskManager()
    
    print(task_manager.generate_task_report())
    
    print("\n=== Fully Automated Tasks ===")
    fully_automated = task_manager.get_fully_automated_tasks()
    for task in fully_automated:
        print(f"- {task.name} ({task.estimated_time})")
    
    print("\n=== Example Task Prompt ===")
    api_task = task_manager.tasks["APIエンドポイント作成"]
    print(f"Task: {api_task.name}")
    print(f"Prompt: {api_task.example_prompt}")
```

---

