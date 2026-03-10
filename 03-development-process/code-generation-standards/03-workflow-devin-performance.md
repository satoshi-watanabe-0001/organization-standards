# コード生成標準

## 開発ワークフローとの統合

### ビルドパイプライン統合

```yaml
# .github/workflows/code-generation.yml
name: Code Generation and Validation

on:
  push:
    paths:
      - 'schemas/**'
      - 'templates/**'
      - 'generators/**'
  pull_request:
    paths:
      - 'schemas/**'
      - 'templates/**'
      - 'generators/**'

jobs:
  generate-and-validate:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
        
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
          
      - name: Install dependencies
        run: npm ci
        
      - name: Run code generation
        run: |
          npm run generate:clean
          npm run generate:all
          
      - name: Validate generated code
        run: |
          npm run lint:generated
          npm run test:generated
          
      - name: Check for uncommitted changes
        run: |
          if [ -n "$(git status --porcelain src/generated/)" ]; then
            echo "❌ Generated code is out of sync with schemas"
            echo "Please run 'npm run generate:all' and commit the changes"
            git status --porcelain src/generated/
            exit 1
          fi
          
      - name: Upload generation artifacts
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: generation-logs
          path: |
            .generator-cache/
            logs/generation.log
```

**Package.json Scripts:**
```json
{
  "scripts": {
    "generate:clean": "rimraf src/generated && mkdir -p src/generated",
    "generate:all": "npm run generate:entities && npm run generate:api && npm run generate:tests",
    "generate:entities": "node generators/entity-generator.js",
    "generate:api": "openapi-generator-cli generate -i openapi.yaml -g typescript-fetch -o src/generated/api",
    "generate:tests": "node generators/test-generator.js",
    "lint:generated": "eslint src/generated/**/*.ts --fix",
    "test:generated": "jest src/generated/**/*.test.ts",
    "validate:schemas": "ajv validate -s schemas/schema.json -d 'data/**/*.json'",
    "watch:generate": "nodemon --watch schemas --watch templates --exec 'npm run generate:all'"
  }
}
```

---

### IDE統合

**VS Code Extension Configuration:**
```json
// .vscode/settings.json
{
  "files.associations": {
    "*.hbs": "handlebars"
  },
  "emmet.includeLanguages": {
    "handlebars": "html"
  },
  "files.exclude": {
    "**/src/generated/**": false
  },
  "search.exclude": {
    "**/src/generated/**": true
  },
  "files.readonlyInclude": {
    "**/src/generated/**": true
  },
  "typescript.preferences.readonly": true,
  "eslint.workingDirectories": [
    "src/generated"
  ]
}
```

**VS Code Tasks:**
```json
// .vscode/tasks.json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Generate All Code",
      "type": "npm",
      "script": "generate:all",
      "group": {
        "kind": "build",
        "isDefault": true
      },
      "presentation": {
        "echo": true,
        "reveal": "always",
        "focus": false,
        "panel": "shared"
      }
    },
    {
      "label": "Watch and Generate",
      "type": "npm",
      "script": "watch:generate",
      "runOptions": {
        "runOn": "folderOpen"
      },
      "presentation": {
        "echo": false,
        "reveal": "silent",
        "focus": false,
        "panel": "shared"
      }
    }
  ]
}
```

---

## Devin AI実践例

### ジェネレーター開発

**Prompt 1: Create Custom Code Generator**
```
Create a custom code generator for this project with the following requirements:

Project Type: [Node.js/React/etc.]
Target: Generate [CRUD operations/API endpoints/components/etc.]

Requirements:
1. Template-based generation using Handlebars
2. Configuration via YAML files
3. Support for incremental generation
4. Generated code quality validation
5. Integration with build pipeline

Components needed:
- Generator engine with plugin architecture
- Template organization structure
- Configuration schema and validation
- CLI interface for generator commands
- Build script integration

Include:
- Template examples for common patterns
- Configuration file examples
- Usage documentation
- Quality validation rules
- CI/CD integration scripts

Focus on maintainability and extensibility.
```

**Prompt 2: Generate OpenAPI Client Code**
```
Generate a complete API client from this OpenAPI specification:

[Paste OpenAPI spec or provide URL]

Requirements:
1. TypeScript client with proper typing
2. HTTP client abstraction (fetch/axios)
3. Error handling and retry logic
4. Request/response interceptors
5. Authentication handling
6. Generated documentation

Generate:
- Client class with all endpoint methods
- TypeScript interfaces for all models
- Error handling classes
- Configuration options
- Usage examples
- Unit test templates

Ensure the generated code follows these standards:
- ESLint compliance
- Proper JSDoc documentation
- Consistent naming conventions
- Tree-shakable exports
```

### テンプレート作成

**Prompt 3: Create Entity Generation Templates**
```
Create Handlebars templates for generating database entities with the following features:

Database: [PostgreSQL/MongoDB/etc.]
ORM: [TypeORM/Prisma/Mongoose/etc.]

Templates needed:
1. Entity class template
2. Repository template
3. Service layer template
4. DTO (Data Transfer Object) template
5. Test template

Features for each template:
- Proper typing and decorators
- Validation rules
- Relationships handling
- Audit fields (createdAt, updatedAt)
- Soft delete support
- Custom field types

Input schema format:
```yaml
entities:
  - name: User
    tableName: users
    fields:
      - name: email
        type: string
        unique: true
        required: true
      - name: name
        type: string
        required: true
    relationships:
      - type: oneToMany
        entity: Post
        field: posts
```

Include helper functions for common transformations (camelCase, PascalCase, pluralization).
```

**Prompt 4: Build React Component Generator**
```
Create a React component generator that produces components following our design system:

Component Types:
- Functional components with hooks
- TypeScript interfaces
- Styled-components integration
- Storybook stories
- Unit tests

Input configuration:
```yaml
components:
  - name: UserCard
    type: display
    props:
      - name: user
        type: User
        required: true
      - name: onEdit
        type: function
        optional: true
    features:
      - responsive
      - accessible
      - themeable
```

Generated files for each component:
- Component.tsx (main component)
- Component.styles.ts (styled-components)
- Component.stories.tsx (Storybook)
- Component.test.tsx (Jest/RTL tests)
- index.ts (barrel export)

Include proper TypeScript typing, accessibility attributes, and responsive design patterns.
```

### 品質保証

**Prompt 5: Create Generated Code Validator**
```
Build a comprehensive validator for generated code quality:

Validation Rules:
1. Code compilation (TypeScript/JavaScript)
2. Linting compliance (ESLint rules)
3. Code style consistency
4. Import/export correctness
5. Documentation completeness
6. Test coverage requirements
7. Performance considerations

Features:
- Configurable rule sets
- Multiple output formats (JSON, XML, HTML report)
- Integration with CI/CD pipelines
- Custom rule definitions
- Severity levels (error, warning, info)

Validation categories:
- Syntax and compilation
- Code quality metrics
- Security vulnerabilities
- Performance anti-patterns
- Documentation standards
- Test quality

Include configuration examples and integration scripts for popular CI/CD platforms.
```

**Prompt 6: Generate Test Cases for Generated Code**
```
Create a test generator that produces comprehensive test suites for generated code:

Test Types:
1. Unit tests for individual methods
2. Integration tests for API endpoints
3. Contract tests for interfaces
4. Property-based tests for data validation
5. Performance tests for critical paths

Testing Frameworks: [Jest/Vitest/Mocha + Chai/etc.]

For each generated entity/component, create:
- Basic functionality tests
- Edge case and error handling tests
- Mock data generators
- Test utilities and helpers
- Performance benchmark tests

Test generation should be based on the same schemas/configs used for main code generation.
Include realistic test data generation and proper mocking strategies.

Focus on high coverage and meaningful test scenarios rather than just structural tests.
```

### CI/CD統合

**Prompt 7: Setup Generation Pipeline**
```
Create a complete CI/CD pipeline for code generation with the following requirements:

Pipeline Stages:
1. Schema validation
2. Template compilation
3. Code generation
4. Quality validation
5. Test execution
6. Documentation generation
7. Artifact publishing

CI/CD Platform: [GitHub Actions/GitLab CI/Jenkins/etc.]

Features needed:
- Incremental generation (only changed schemas)
- Parallel processing for large codebases
- Caching for performance
- Error reporting and notifications
- Artifact management
- Multi-environment deployment

Include:
- Pipeline configuration files
- Quality gates and approval processes
- Rollback mechanisms
- Performance monitoring
- Integration with existing workflows

Ensure the pipeline is robust, fast, and provides clear feedback to developers.
```

**Prompt 8: Create Generator Monitoring Dashboard**
```
Build a monitoring dashboard for code generation processes:

Metrics to track:
1. Generation frequency and success rates
2. Template usage statistics
3. Generated code quality trends
4. Build time impacts
5. Developer adoption metrics
6. Error patterns and failures

Dashboard Features:
- Real-time generation status
- Historical trend analysis
- Quality metrics visualization
- Performance impact tracking
- Alert configuration
- Team/project breakdown

Technical Requirements:
- Web-based dashboard (React/Vue/etc.)
- API for metrics collection
- Database for historical data
- Integration with existing monitoring tools
- Export capabilities for reports

Include sample data and deployment instructions.
```

### 高度な生成パターン

**Prompt 9: Implement Multi-Target Generator**
```
Create a generator that can produce code for multiple platforms/languages from a single source:

Source: Domain model definitions (JSON Schema/GraphQL/etc.)
Targets:
- TypeScript interfaces
- Python dataclasses
- Java POJOs
- C# DTOs
- SQL DDL scripts
- JSON schemas
- GraphQL schemas

Features:
- Shared validation logic across platforms
- Consistent naming conventions
- Type mapping between languages
- Custom annotations/decorators per platform
- Dependency management

Architecture:
- Abstract syntax tree (AST) based generation
- Platform-specific code emitters
- Shared template library
- Plugin system for extensibility

Include examples showing how the same domain model generates consistent code across all target platforms.
```

**Prompt 10: Build Schema Evolution Generator**
```
Create a generator that handles schema evolution and migration:

Capabilities:
1. Detect schema changes (additions, deletions, modifications)
2. Generate migration scripts (database, API, client code)
3. Create compatibility layers for breaking changes
4. Generate deprecation warnings
5. Update documentation automatically

Migration Types:
- Database schema migrations
- API version compatibility
- Client code updates
- Configuration file updates
- Test data migrations

Features:
- Backwards compatibility analysis
- Breaking change detection
- Migration strategy recommendations
- Automated testing for migrations
- Rollback script generation

Input: Before/after schemas
Output: Complete migration package with scripts, documentation, and tests

Focus on enterprise-grade schema evolution with minimal downtime and maximum compatibility.
```

---

## パフォーマンスと最適化

### 生成パフォーマンス

```typescript
class PerformanceOptimizedGenerator {
  private templateCache: Map<string, CompiledTemplate> = new Map();
  private schemaCache: Map<string, ParsedSchema> = new Map();
  
  constructor(
    private concurrency: number = 4,
    private cacheEnabled: boolean = true
  ) {}
  
  async generateBatch(
    sources: GenerationSource[],
    options: GenerationOptions
  ): Promise<BatchGenerationResult> {
    const startTime = Date.now();
    
    // Pre-compile templates
    await this.precompileTemplates(options.templates);
    
    // Process in batches for memory efficiency
    const batches = this.createBatches(sources, this.concurrency);
    const results: GeneratedFile[] = [];
    
    for (const batch of batches) {
      const batchResults = await Promise.all(
        batch.map(source => this.generateFromSource(source, options))
      );
      results.push(...batchResults.flat());
      
      // Optional: Force garbage collection between batches
      if (global.gc) {
        global.gc();
      }
    }
    
    const endTime = Date.now();
    
    return {
      files: results,
      performance: {
        totalTime: endTime - startTime,
        filesPerSecond: results.length / ((endTime - startTime) / 1000),
        cacheHitRate: this.calculateCacheHitRate(),
        memoryUsage: process.memoryUsage()
      }
    };
  }
  
  private async precompileTemplates(templates: TemplateConfig[]): Promise<void> {
    if (!this.cacheEnabled) return;
    
    const compilationPromises = templates.map(async template => {
      if (!this.templateCache.has(template.path)) {
        const compiled = await this.compileTemplate(template);
        this.templateCache.set(template.path, compiled);
      }
    });
    
    await Promise.all(compilationPromises);
  }
  
  private createBatches<T>(items: T[], batchSize: number): T[][] {
    const batches: T[][] = [];
    for (let i = 0; i < items.length; i += batchSize) {
      batches.push(items.slice(i, i + batchSize));
    }
    return batches;
  }
}

// Performance monitoring
class GenerationPerformanceMonitor {
  private metrics: PerformanceMetric[] = [];
  
  async measureGeneration<T>(
    operation: string,
    fn: () => Promise<T>
  ): Promise<T> {
    const startTime = process.hrtime.bigint();
    const startMemory = process.memoryUsage();
    
    try {
      const result = await fn();
      
      const endTime = process.hrtime.bigint();
      const endMemory = process.memoryUsage();
      const duration = Number(endTime - startTime) / 1_000_000; // Convert to milliseconds
      
      this.metrics.push({
        operation,
        duration,
        memoryDelta: {
          rss: endMemory.rss - startMemory.rss,
          heapUsed: endMemory.heapUsed - startMemory.heapUsed,
          heapTotal: endMemory.heapTotal - startMemory.heapTotal
        },
        timestamp: new Date()
      });
      
      return result;
      
    } catch (error) {
      const endTime = process.hrtime.bigint();
      const duration = Number(endTime - startTime) / 1_000_000;
      
      this.metrics.push({
        operation,
        duration,
        error: error.message,
        timestamp: new Date()
      });
      
      throw error;
    }
  }
  
  getPerformanceReport(): PerformanceReport {
    const grouped = this.groupMetricsByOperation();
    
    return {
      summary: {
        totalOperations: this.metrics.length,
        averageDuration: this.calculateAverageDuration(),
        slowestOperation: this.findSlowestOperation(),
        memoryTrend: this.calculateMemoryTrend()
      },
      operationBreakdown: grouped,
      recommendations: this.generateRecommendations(grouped)
    };
  }
  
  private generateRecommendations(
    grouped: Record<string, PerformanceMetric[]>
  ): string[] {
    const recommendations: string[] = [];
    
    Object.entries(grouped).forEach(([operation, metrics]) => {
      const avgDuration = metrics.reduce((sum, m) => sum + m.duration, 0) / metrics.length;
      const avgMemory = metrics.reduce((sum, m) => sum + (m.memoryDelta?.heapUsed || 0), 0) / metrics.length;
      
      if (avgDuration > 1000) { // > 1 second
        recommendations.push(`Consider optimizing ${operation}: average duration ${avgDuration.toFixed(2)}ms`);
      }
      
      if (avgMemory > 50 * 1024 * 1024) { // > 50MB
        recommendations.push(`High memory usage in ${operation}: average ${(avgMemory / 1024 / 1024).toFixed(2)}MB`);
      }
    });
    
    return recommendations;
  }
}
```

---

