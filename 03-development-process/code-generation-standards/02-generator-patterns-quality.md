# コード生成標準

## ジェネレーター実装パターン

### プラグインベースジェネレーターアーキテクチャ

```typescript
interface GeneratorPlugin {
  name: string;
  version: string;
  generate(context: GenerationContext): Promise<GeneratedFile[]>;
  validate(config: any): ValidationResult;
}

interface GenerationContext {
  config: GeneratorConfig;
  inputSchema: any;
  outputDir: string;
  templateEngine: TemplateEngine;
  fileSystem: FileSystem;
  logger: Logger;
}

interface GeneratedFile {
  path: string;
  content: string;
  metadata: {
    generator: string;
    template: string;
    generatedAt: Date;
    sourceHash: string;
  };
}

class GeneratorEngine {
  private plugins: Map<string, GeneratorPlugin> = new Map();
  
  registerPlugin(plugin: GeneratorPlugin): void {
    this.plugins.set(plugin.name, plugin);
  }
  
  async generate(configPath: string): Promise<GenerationResult> {
    const config = await this.loadConfig(configPath);
    const context = await this.createContext(config);
    const results: GeneratedFile[] = [];
    
    for (const pluginConfig of config.plugins) {
      const plugin = this.plugins.get(pluginConfig.name);
      if (!plugin) {
        throw new Error(`Plugin ${pluginConfig.name} not found`);
      }
      
      // Validate plugin configuration
      const validation = plugin.validate(pluginConfig.config);
      if (!validation.valid) {
        throw new Error(`Invalid configuration for ${pluginConfig.name}: ${validation.errors.join(', ')}`);
      }
      
      // Generate files
      const pluginContext = { ...context, config: pluginConfig.config };
      const pluginResults = await plugin.generate(pluginContext);
      results.push(...pluginResults);
    }
    
    // Write generated files
    await this.writeFiles(results);
    
    return {
      generatedFiles: results,
      summary: this.generateSummary(results)
    };
  }
}

// Example plugin implementation
class EntityGeneratorPlugin implements GeneratorPlugin {
  name = 'entity-generator';
  version = '1.0.0';
  
  async generate(context: GenerationContext): Promise<GeneratedFile[]> {
    const { config, templateEngine, outputDir } = context;
    const files: GeneratedFile[] = [];
    
    for (const entityConfig of config.entities) {
      // Generate entity file
      const entityContent = await templateEngine.render('entity.hbs', {
        className: entityConfig.name,
        tableName: entityConfig.tableName,
        fields: entityConfig.fields,
        timestamp: new Date().toISOString()
      });
      
      files.push({
        path: `${outputDir}/entities/${entityConfig.name}.ts`,
        content: entityContent,
        metadata: {
          generator: this.name,
          template: 'entity.hbs',
          generatedAt: new Date(),
          sourceHash: this.calculateHash(entityConfig)
        }
      });
      
      // Generate repository file
      const repositoryContent = await templateEngine.render('repository.hbs', {
        className: entityConfig.name,
        entityName: entityConfig.name
      });
      
      files.push({
        path: `${outputDir}/repositories/${entityConfig.name}Repository.ts`,
        content: repositoryContent,
        metadata: {
          generator: this.name,
          template: 'repository.hbs',
          generatedAt: new Date(),
          sourceHash: this.calculateHash(entityConfig)
        }
      });
    }
    
    return files;
  }
  
  validate(config: any): ValidationResult {
    const errors: string[] = [];
    
    if (!config.entities || !Array.isArray(config.entities)) {
      errors.push('entities must be an array');
    }
    
    for (const entity of config.entities || []) {
      if (!entity.name) {
        errors.push('entity name is required');
      }
      if (!entity.tableName) {
        errors.push('entity tableName is required');
      }
      if (!entity.fields || !Array.isArray(entity.fields)) {
        errors.push('entity fields must be an array');
      }
    }
    
    return {
      valid: errors.length === 0,
      errors
    };
  }
  
  private calculateHash(data: any): string {
    return crypto.createHash('sha256')
      .update(JSON.stringify(data))
      .digest('hex');
  }
}
```

---

### 増分生成

```typescript
interface GenerationMetadata {
  sourceHash: string;
  generatedAt: Date;
  generator: string;
  template: string;
}

class IncrementalGenerator {
  private metadataStore: MetadataStore;
  
  constructor(metadataStore: MetadataStore) {
    this.metadataStore = metadataStore;
  }
  
  async generateIncremental(
    sources: SourceFile[],
    templates: Template[]
  ): Promise<GenerationResult> {
    const results: GeneratedFile[] = [];
    const skipped: string[] = [];
    
    for (const source of sources) {
      const currentHash = this.calculateSourceHash(source);
      const metadata = await this.metadataStore.getMetadata(source.path);
      
      // Skip if source hasn't changed
      if (metadata && metadata.sourceHash === currentHash) {
        skipped.push(source.path);
        continue;
      }
      
      // Generate new files
      const generatedFiles = await this.generateFromSource(source, templates);
      results.push(...generatedFiles);
      
      // Update metadata
      await this.metadataStore.updateMetadata(source.path, {
        sourceHash: currentHash,
        generatedAt: new Date(),
        generator: this.constructor.name,
        template: templates.map(t => t.name).join(',')
      });
    }
    
    return {
      generated: results,
      skipped,
      performance: {
        totalSources: sources.length,
        processed: results.length,
        skippedCount: skipped.length,
        timesSaved: `${((skipped.length / sources.length) * 100).toFixed(1)}%`
      }
    };
  }
  
  private calculateSourceHash(source: SourceFile): string {
    const content = JSON.stringify({
      content: source.content,
      lastModified: source.lastModified,
      dependencies: source.dependencies
    });
    
    return crypto.createHash('sha256').update(content).digest('hex');
  }
}

// Usage in build script
const generator = new IncrementalGenerator(
  new FileBasedMetadataStore('.generator-cache')
);

const sources = await loadSources('schemas/**/*.yaml');
const templates = await loadTemplates('templates/**/*.hbs');

const result = await generator.generateIncremental(sources, templates);

console.log(`Generated ${result.generated.length} files`);
console.log(`Skipped ${result.skipped.length} unchanged files`);
console.log(`Performance improvement: ${result.performance.timesSaved}`);
```

---

## 生成されたコードの品質保証

### 生成されたコードのテスト

```typescript
// Test generator for generated entities
class GeneratedCodeTester {
  async testGeneratedEntity(entityPath: string): Promise<TestResult> {
    const results: TestResult = {
      passed: 0,
      failed: 0,
      errors: []
    };
    
    try {
      // Test 1: Code compiles
      await this.testCompilation(entityPath);
      results.passed++;
      
      // Test 2: Entity can be instantiated
      const EntityClass = await import(entityPath);
      const instance = new EntityClass.default();
      results.passed++;
      
      // Test 3: TypeORM decorators are present
      const metadata = getMetadataArgsStorage();
      const entityMetadata = metadata.tables.find(
        table => table.target === EntityClass.default
      );
      
      if (!entityMetadata) {
        throw new Error('Entity metadata not found');
      }
      results.passed++;
      
      // Test 4: All expected columns are present
      const expectedColumns = this.getExpectedColumns(entityPath);
      const actualColumns = metadata.columns
        .filter(col => col.target === EntityClass.default)
        .map(col => col.propertyName);
        
      for (const expectedColumn of expectedColumns) {
        if (!actualColumns.includes(expectedColumn)) {
          throw new Error(`Missing expected column: ${expectedColumn}`);
        }
      }
      results.passed++;
      
    } catch (error) {
      results.failed++;
      results.errors.push(error.message);
    }
    
    return results;
  }
  
  async testAllGeneratedFiles(pattern: string): Promise<OverallTestResult> {
    const files = await glob(pattern);
    const results: TestResult[] = [];
    
    for (const file of files) {
      if (await this.isGeneratedFile(file)) {
        const result = await this.testGeneratedEntity(file);
        results.push(result);
      }
    }
    
    return {
      totalFiles: files.length,
      totalPassed: results.reduce((sum, r) => sum + r.passed, 0),
      totalFailed: results.reduce((sum, r) => sum + r.failed, 0),
      files: results
    };
  }
  
  private async isGeneratedFile(filePath: string): Promise<boolean> {
    const content = await fs.readFile(filePath, 'utf-8');
    return content.includes('@generated') || 
           content.includes('DO NOT EDIT') ||
           content.includes('automatically generated');
  }
}

// Integration with test suite
describe('Generated Code Quality', () => {
  const tester = new GeneratedCodeTester();
  
  it('should generate valid TypeScript entities', async () => {
    const result = await tester.testAllGeneratedFiles('src/generated/entities/**/*.ts');
    
    expect(result.totalFailed).toBe(0);
    expect(result.totalPassed).toBeGreaterThan(0);
    
    if (result.totalFailed > 0) {
      console.error('Generated code test failures:', result.files);
    }
  });
  
  it('should generate compilable code', async () => {
    const compileResult = await exec('npx tsc --noEmit --project tsconfig.json');
    expect(compileResult.exitCode).toBe(0);
  });
  
  it('should pass linting rules', async () => {
    const lintResult = await exec('npx eslint src/generated/**/*.ts');
    expect(lintResult.exitCode).toBe(0);
  });
});
```

---

### コード品質検証

```typescript
interface CodeQualityRules {
  maxLineLength: number;
  maxComplexity: number;
  requireDocumentation: boolean;
  allowedImports: string[];
  forbiddenPatterns: RegExp[];
}

class GeneratedCodeValidator {
  constructor(private rules: CodeQualityRules) {}
  
  async validateFile(filePath: string): Promise<ValidationResult> {
    const content = await fs.readFile(filePath, 'utf-8');
    const issues: ValidationIssue[] = [];
    
    // Check line length
    const lines = content.split('\n');
    lines.forEach((line, index) => {
      if (line.length > this.rules.maxLineLength) {
        issues.push({
          type: 'line-length',
          line: index + 1,
          message: `Line exceeds ${this.rules.maxLineLength} characters`,
          severity: 'warning'
        });
      }
    });
    
    // Check for forbidden patterns
    this.rules.forbiddenPatterns.forEach(pattern => {
      const matches = content.match(pattern);
      if (matches) {
        issues.push({
          type: 'forbidden-pattern',
          message: `Forbidden pattern found: ${pattern}`,
          severity: 'error'
        });
      }
    });
    
    // Check imports
    const importMatches = content.match(/import .+ from ['"](.+)['"];?/g);
    if (importMatches) {
      importMatches.forEach(importStatement => {
        const importPath = importStatement.match(/from ['"](.+)['"];?/)?.[1];
        if (importPath && !this.isAllowedImport(importPath)) {
          issues.push({
            type: 'invalid-import',
            message: `Import not allowed: ${importPath}`,
            severity: 'error'
          });
        }
      });
    }
    
    // Check documentation
    if (this.rules.requireDocumentation) {
      const hasJSDoc = content.includes('/**');
      const hasClassDoc = /export class \w+/.test(content) && 
                         content.includes('@description');
      
      if (!hasJSDoc && !hasClassDoc) {
        issues.push({
          type: 'missing-documentation',
          message: 'Generated class lacks proper documentation',
          severity: 'warning'
        });
      }
    }
    
    return {
      valid: issues.filter(i => i.severity === 'error').length === 0,
      issues
    };
  }
  
  private isAllowedImport(importPath: string): boolean {
    return this.rules.allowedImports.some(allowed => 
      importPath.startsWith(allowed) || 
      importPath.match(new RegExp(allowed))
    );
  }
}

// Usage in generator
class QualityAwareGenerator {
  private validator: GeneratedCodeValidator;
  
  constructor() {
    this.validator = new GeneratedCodeValidator({
      maxLineLength: 120,
      maxComplexity: 10,
      requireDocumentation: true,
      allowedImports: [
        'typeorm',
        'class-validator',
        'class-transformer',
        '@app/',
        'lodash',
        'date-fns'
      ],
      forbiddenPatterns: [
        /console\.log/,  // No console.log in generated code
        /any/,           // No 'any' type
        /\/\/ TODO/      // No TODO comments
      ]
    });
  }
  
  async generateWithQualityCheck(
    template: string,
    data: any,
    outputPath: string
  ): Promise<void> {
    // Generate code
    const generatedContent = await this.templateEngine.render(template, data);
    
    // Write to temporary file for validation
    const tempPath = `${outputPath}.tmp`;
    await fs.writeFile(tempPath, generatedContent);
    
    // Validate quality
    const validation = await this.validator.validateFile(tempPath);
    
    if (!validation.valid) {
      const errors = validation.issues.filter(i => i.severity === 'error');
      throw new Error(`Generated code quality check failed: ${errors.map(e => e.message).join(', ')}`);
    }
    
    // Move to final location if validation passed
    await fs.move(tempPath, outputPath);
    
    // Log warnings
    const warnings = validation.issues.filter(i => i.severity === 'warning');
    if (warnings.length > 0) {
      console.warn(`Generated code warnings for ${outputPath}:`, warnings);
    }
  }
}
```

---

