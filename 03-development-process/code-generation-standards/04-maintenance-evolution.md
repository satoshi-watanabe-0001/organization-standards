# コード生成標準

## 保守と進化

### テンプレートバージョニング

```typescript
interface TemplateVersion {
  version: string;
  compatibleSchemaVersions: string[];
  deprecatedAt?: Date;
  removedAt?: Date;
  migrationGuide?: string;
}

class VersionedTemplateManager {
  private templates: Map<string, Map<string, Template>> = new Map();
  
  registerTemplate(
    name: string,
    version: string,
    template: Template,
    metadata: TemplateVersion
  ): void {
    if (!this.templates.has(name)) {
      this.templates.set(name, new Map());
    }
    
    const templateVersions = this.templates.get(name)!;
    templateVersions.set(version, { ...template, metadata });
  }
  
  getTemplate(
    name: string,
    requestedVersion?: string,
    schemaVersion?: string
  ): Template {
    const templateVersions = this.templates.get(name);
    if (!templateVersions) {
      throw new Error(`Template ${name} not found`);
    }
    
    let selectedVersion: string;
    
    if (requestedVersion) {
      // Specific version requested
      if (!templateVersions.has(requestedVersion)) {
        throw new Error(`Template ${name} version ${requestedVersion} not found`);
      }
      selectedVersion = requestedVersion;
    } else {
      // Auto-select compatible version
      selectedVersion = this.selectCompatibleVersion(
        templateVersions,
        schemaVersion
      );
    }
    
    const template = templateVersions.get(selectedVersion)!;
    
    // Check for deprecation warnings
    if (template.metadata.deprecatedAt) {
      console.warn(
        `Template ${name}@${selectedVersion} is deprecated since ${template.metadata.deprecatedAt}`
      );
      
      if (template.metadata.migrationGuide) {
        console.warn(`Migration guide: ${template.metadata.migrationGuide}`);
      }
    }
    
    return template;
  }
  
  private selectCompatibleVersion(
    templateVersions: Map<string, Template>,
    schemaVersion?: string
  ): string {
    if (!schemaVersion) {
      // Return latest version
      const versions = Array.from(templateVersions.keys());
      return this.getLatestVersion(versions);
    }
    
    // Find compatible version
    for (const [version, template] of templateVersions) {
      if (template.metadata.compatibleSchemaVersions.includes(schemaVersion)) {
        return version;
      }
    }
    
    throw new Error(
      `No compatible template version found for schema version ${schemaVersion}`
    );
  }
  
  generateMigrationPlan(
    templateName: string,
    fromVersion: string,
    toVersion: string
  ): MigrationPlan {
    const fromTemplate = this.getTemplate(templateName, fromVersion);
    const toTemplate = this.getTemplate(templateName, toVersion);
    
    return {
      templateName,
      fromVersion,
      toVersion,
      changes: this.analyzeTemplateChanges(fromTemplate, toTemplate),
      breakingChanges: this.identifyBreakingChanges(fromTemplate, toTemplate),
      migrationSteps: this.generateMigrationSteps(fromTemplate, toTemplate),
      estimatedEffort: this.estimateMigrationEffort(fromTemplate, toTemplate)
    };
  }
}

// Usage example
const templateManager = new VersionedTemplateManager();

// Register template versions
templateManager.registerTemplate('entity', '1.0.0', entityTemplateV1, {
  version: '1.0.0',
  compatibleSchemaVersions: ['1.0.0', '1.1.0']
});

templateManager.registerTemplate('entity', '2.0.0', entityTemplateV2, {
  version: '2.0.0',
  compatibleSchemaVersions: ['2.0.0', '2.1.0'],
  migrationGuide: 'https://docs.company.com/templates/entity-v2-migration'
});

templateManager.registerTemplate('entity', '1.5.0', entityTemplateV15, {
  version: '1.5.0',
  compatibleSchemaVersions: ['1.0.0', '1.1.0', '1.2.0'],
  deprecatedAt: new Date('2024-12-01'),
  removedAt: new Date('2025-06-01'),
  migrationGuide: 'Migrate to v2.0.0 for continued support'
});
```

---

### ジェネレーター分析

```typescript
interface GeneratorUsageAnalytics {
  templateUsage: Record<string, number>;
  generationFrequency: { date: string; count: number }[];
  errorPatterns: { error: string; count: number; lastSeen: Date }[];
  performanceMetrics: {
    averageGenerationTime: number;
    p95GenerationTime: number;
    memoryUsage: number;
  };
  userAdoption: {
    activeUsers: number;
    newUsers: number;
    churned: number;
  };
}

class GeneratorAnalyticsCollector {
  constructor(
    private metricsStore: MetricsStore,
    private eventBus: EventBus
  ) {
    this.setupEventListeners();
  }
  
  private setupEventListeners(): void {
    this.eventBus.on('generation:started', this.onGenerationStarted.bind(this));
    this.eventBus.on('generation:completed', this.onGenerationCompleted.bind(this));
    this.eventBus.on('generation:failed', this.onGenerationFailed.bind(this));
    this.eventBus.on('template:used', this.onTemplateUsed.bind(this));
  }
  
  private async onGenerationStarted(event: GenerationStartedEvent): Promise<void> {
    await this.metricsStore.recordEvent({
      type: 'generation_started',
      userId: event.userId,
      projectId: event.projectId,
      templateCount: event.templates.length,
      timestamp: new Date()
    });
  }
  
  private async onGenerationCompleted(event: GenerationCompletedEvent): Promise<void> {
    await this.metricsStore.recordEvent({
      type: 'generation_completed',
      userId: event.userId,
      projectId: event.projectId,
      duration: event.duration,
      filesGenerated: event.filesGenerated,
      templateCount: event.templateCount,
      timestamp: new Date()
    });
    
    // Track performance metrics
    await this.recordPerformanceMetrics(event);
  }
  
  private async onGenerationFailed(event: GenerationFailedEvent): Promise<void> {
    await this.metricsStore.recordEvent({
      type: 'generation_failed',
      userId: event.userId,
      projectId: event.projectId,
      error: event.error,
      templateName: event.templateName,
      timestamp: new Date()
    });
  }
  
  async generateUsageReport(
    startDate: Date,
    endDate: Date
  ): Promise<GeneratorUsageAnalytics> {
    const events = await this.metricsStore.getEvents({
      startDate,
      endDate,
      types: ['generation_started', 'generation_completed', 'generation_failed', 'template_used']
    });
    
    return {
      templateUsage: this.calculateTemplateUsage(events),
      generationFrequency: this.calculateGenerationFrequency(events),
      errorPatterns: this.analyzeErrorPatterns(events),
      performanceMetrics: this.calculatePerformanceMetrics(events),
      userAdoption: this.calculateUserAdoption(events)
    };
  }
  
  async generateRecommendations(): Promise<string[]> {
    const report = await this.generateUsageReport(
      new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), // Last 30 days
      new Date()
    );
    
    const recommendations: string[] = [];
    
    // Template usage recommendations
    const unusedTemplates = Object.entries(report.templateUsage)
      .filter(([_, count]) => count === 0)
      .map(([template, _]) => template);
      
    if (unusedTemplates.length > 0) {
      recommendations.push(
        `Consider removing unused templates: ${unusedTemplates.join(', ')}`
      );
    }
    
    // Performance recommendations
    if (report.performanceMetrics.averageGenerationTime > 10000) { // 10 seconds
      recommendations.push(
        'Generation performance is below target. Consider optimizing templates or caching.'
      );
    }
    
    // Error pattern recommendations
    const frequentErrors = report.errorPatterns
      .filter(error => error.count > 10)
      .sort((a, b) => b.count - a.count);
      
    if (frequentErrors.length > 0) {
      recommendations.push(
        `Address frequent errors: ${frequentErrors.slice(0, 3).map(e => e.error).join(', ')}`
      );
    }
    
    return recommendations;
  }
  
  private calculateTemplateUsage(events: AnalyticsEvent[]): Record<string, number> {
    const usage: Record<string, number> = {};
    
    events
      .filter(e => e.type === 'template_used')
      .forEach(event => {
        const templateName = event.templateName;
        usage[templateName] = (usage[templateName] || 0) + 1;
      });
      
    return usage;
  }
  
  private calculateGenerationFrequency(events: AnalyticsEvent[]): { date: string; count: number }[] {
    const frequency: Record<string, number> = {};
    
    events
      .filter(e => e.type === 'generation_completed')
      .forEach(event => {
        const date = event.timestamp.toISOString().split('T')[0];
        frequency[date] = (frequency[date] || 0) + 1;
      });
      
    return Object.entries(frequency)
      .map(([date, count]) => ({ date, count }))
      .sort((a, b) => a.date.localeCompare(b.date));
  }
}
```

---

## チェックリスト

### コード生成セットアップチェックリスト

**Project Setup / プロジェクトセットアップ:**
- [ ] Code generation directory structure established
- [ ] Template organization defined
- [ ] Generator configuration files created
- [ ] Build scripts integrated
- [ ] Version control strategy decided (.gitignore configured)

**Template Development / テンプレート開発:**
- [ ] Template engine selected and configured
- [ ] Common template patterns established
- [ ] Template versioning strategy implemented
- [ ] Helper functions library created
- [ ] Template testing framework setup

**Quality Assurance / 品質保証:**
- [ ] Generated code validation rules defined
- [ ] Automated testing for generated code implemented
- [ ] Code quality gates configured
- [ ] Performance benchmarks established
- [ ] Error handling and logging implemented

**Integration / 統合:**
- [ ] CI/CD pipeline integration completed
- [ ] IDE/editor support configured
- [ ] Development workflow documented
- [ ] Team training conducted
- [ ] Monitoring and analytics setup

---

### 生成されたコード品質チェックリスト

**Code Structure / コード構造:**
- [ ] Generated code follows project conventions
- [ ] Proper imports and exports
- [ ] Consistent naming patterns
- [ ] Appropriate modularization
- [ ] Clear separation of concerns

**Documentation / ドキュメンテーション:**
- [ ] Generated files clearly marked
- [ ] Proper JSDoc/comments included
- [ ] Type annotations present (TypeScript)
- [ ] Usage examples provided
- [ ] API documentation generated

**Testing / テスト:**
- [ ] Generated code compiles without errors
- [ ] Passes all linting rules
- [ ] Unit tests exist and pass
- [ ] Integration tests cover generated code
- [ ] Performance tests validate efficiency

**Maintainability / 保守性:**
- [ ] Generated code is readable
- [ ] No hardcoded values (configurable)
- [ ] Error handling implemented
- [ ] Logging and debugging support
- [ ] Update/regeneration process documented

---

### ジェネレーター保守チェックリスト

**Regular Maintenance / 定期保守:**
- [ ] Template versions reviewed and updated
- [ ] Deprecated templates identified
- [ ] Generator performance monitored
- [ ] Error patterns analyzed
- [ ] Usage analytics reviewed

**Quality Improvement / 品質改善:**
- [ ] Generated code quality trends analyzed
- [ ] User feedback collected and addressed
- [ ] Template optimization opportunities identified
- [ ] New template patterns documented
- [ ] Best practices updated

**Evolution Management / 進化管理:**
- [ ] Schema changes impact assessed
- [ ] Breaking changes communicated
- [ ] Migration guides provided
- [ ] Backward compatibility maintained
- [ ] Future roadmap planned

---

## 参考資料

### 主要リソース

- Martin Fowlerの「Code Generation Patterns」
- OpenAPI Generator - 多言語コード生成
- Handlebars.js - テンプレートエンジン
- AST Explorer - 抽象構文木の可視化
- Yeoman - コード足場ツール

### ツールとライブラリ

**Code Generation Tools:**
- OpenAPI Generator: https://openapi-generator.tech/
- Plop: https://plopjs.com/
- Hygen: https://hygen.io/
- GraphQL Code Generator: https://graphql-code-generator.com/

**Template Engines:**
- Handlebars: https://handlebarsjs.com/
- Mustache: https://mustache.github.io/
- EJS: https://ejs.co/
- Nunjucks: https://mozilla.github.io/nunjucks/

### 関連標準
- [03-development-process/README.md](../README.md)
- [01-coding-standards/README.md](../../01-coding-standards/README.md)
- [04-quality-standards/README.md](../../04-quality-standards/README.md)

---

## 変更履歴

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0.0 | 2024-10-24 | Initial version | Development Team |

---

**Document Owner / ドキュメント所有者**: Development Team  
**Review Cycle / レビューサイクル**: Quarterly / 四半期ごと  
**Next Review Date / 次回レビュー日**: 2025-01-24