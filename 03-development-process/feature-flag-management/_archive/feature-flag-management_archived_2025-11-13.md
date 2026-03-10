# フィーチャーフラグ管理

**バージョン**: 1.0.0  
**最終更新**: 2024-10-24  
**ドキュメント種別**: 開発プロセス標準  
**階層**: Tier 3 (任意)

---

## 概要

### 目的

フィーチャーフラグ（フィーチャートグルやフィーチャースイッチとも呼ばれる）は、新機能をユーザーに即座に公開することなく、コードをデプロイできるようにします。このドキュメントは、一般的な落とし穴や技術的負債を避けながら、フィーチャーフラグを効果的に実装、管理、保守するためのガイドラインを提供します。

### 利点

- **デプロイとリリースの分離**: 機能を公開せずに安全にコードをデプロイ
- **段階的ロールアウト**: 特定のユーザーセグメントへの機能公開制御
- **リスク軽減**: ロールバックなしで問題のある機能を迅速に無効化
- **A/Bテスト**: 実際のユーザーで異なる実装を比較
- **早期フィードバック**: ベータユーザーから早期フィードバックを取得
- **継続的デリバリー**: トランクベース開発の実現

---

## フィーチャーフラグの種類

### リリーストグル

開発中の未完成機能を隠すために使用される一時的なフラグ。

**特徴:**
- 短期間（数日から数週間）
- 機能開発中に使用
- 機能完成後に削除
- 低複雑度

**✅ 良い例:**
```typescript
// シンプルなリリーストグル
class ShoppingCartService {
  constructor(
    private featureFlags: FeatureFlagService,
    private legacyCartService: LegacyCartService,
    private newCartService: NewCartService
  ) {}
  
  async addItem(userId: string, item: CartItem): Promise<void> {
    const useNewCartService = await this.featureFlags.isEnabled(
      'new_cart_service',
      { userId }
    );
    
    if (useNewCartService) {
      return this.newCartService.addItem(userId, item);
    } else {
      return this.legacyCartService.addItem(userId, item);
    }
  }
}

// フラグ設定
const releaseFlags = {
  'new_cart_service': {
    enabled: false, // デフォルトで無効
    description: '新しいカートサービス実装を有効化',
    type: 'release',
    createdDate: '2024-10-01',
    plannedRemovalDate: '2024-11-01', // ロールアウト後に削除
    owner: 'cart-team'
  }
};
```

---

### 実験トグル

A/Bテストと実験に使用されるフラグ。

**特徴:**
- 中期間（数週間から数ヶ月）
- 仮説検証に使用
- 統計分析が必要
- 永続化または削除される可能性

**Example:**
```typescript
interface ExperimentConfig {
  flagName: string;
  variants: {
    control: { weight: number; config: any };
    treatment: { weight: number; config: any };
  };
  metrics: string[];
  duration: { start: Date; end: Date };
  successCriteria: string;
}

class ExperimentService {
  async getUserVariant(
    experimentName: string,
    userId: string
  ): Promise<'control' | 'treatment'> {
    const experiment = await this.getExperiment(experimentName);
    
    // Consistent user assignment based on hash
    const hash = this.hashUserId(userId, experimentName);
    const threshold = experiment.variants.control.weight / 100;
    
    return hash < threshold ? 'control' : 'treatment';
  }
  
  private hashUserId(userId: string, experimentName: string): number {
    // Deterministic hash function for consistent assignment
    const combined = `${userId}:${experimentName}`;
    let hash = 0;
    for (let i = 0; i < combined.length; i++) {
      const char = combined.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32-bit integer
    }
    return Math.abs(hash) / Math.pow(2, 31);
  }
}

// Checkout page experiment
class CheckoutController {
  async renderCheckoutPage(userId: string): Promise<CheckoutPageData> {
    const variant = await this.experimentService.getUserVariant(
      'checkout_optimization_v2',
      userId
    );
    
    if (variant === 'treatment') {
      return this.renderOptimizedCheckout(userId);
    } else {
      return this.renderStandardCheckout(userId);
    }
  }
}
```

---

### 運用トグル

システムの運用面を制御するために使用されるフラグ。

**特徴:**
- 長期間（数ヶ月から数年）
- システム動作制御に使用
- 頻繁に変更される可能性
- パフォーマンス調整によく使用

**Example:**
```typescript
class DatabaseService {
  constructor(
    private primaryDb: Database,
    private readReplicaDb: Database,
    private featureFlags: FeatureFlagService
  ) {}
  
  async executeQuery(query: string, params: any[]): Promise<any> {
    const useReadReplica = await this.featureFlags.isEnabled(
      'use_read_replica'
    );
    
    const maxRetries = await this.featureFlags.getNumericValue(
      'db_max_retries',
      3 // default
    );
    
    const timeoutMs = await this.featureFlags.getNumericValue(
      'db_timeout_ms',
      5000 // default
    );
    
    const database = useReadReplica ? this.readReplicaDb : this.primaryDb;
    
    return this.executeWithRetry(database, query, params, {
      maxRetries,
      timeoutMs
    });
  }
}

// Operational flag configurations
const operationalFlags = {
  'use_read_replica': {
    enabled: true,
    description: 'Route read queries to read replica',
    type: 'operational',
    modifiable: true // Can be changed at runtime
  },
  'db_max_retries': {
    value: 3,
    description: 'Maximum database connection retries',
    type: 'operational',
    constraints: { min: 1, max: 10 }
  },
  'rate_limit_requests_per_minute': {
    value: 1000,
    description: 'API rate limit per user per minute',
    type: 'operational',
    constraints: { min: 100, max: 10000 }
  }
};
```

---

### 権限トグル

ユーザー属性に基づいて機能へのアクセスを制御するために使用されるフラグ。

**特徴:**
- 長期間（数ヶ月から数年）
- ユーザー属性やサブスクリプション階層に基づく
- 複雑なロジックを伴う可能性
- しばしばビジネスロジックの一部

**Example:**
```typescript
interface UserContext {
  userId: string;
  subscriptionTier: 'free' | 'premium' | 'enterprise';
  country: string;
  signupDate: Date;
  betaPrograms: string[];
}

class PermissionService {
  async canAccessFeature(
    featureName: string,
    userContext: UserContext
  ): Promise<boolean> {
    const flagConfig = await this.getPermissionFlag(featureName);
    
    // Check subscription tier requirements
    if (flagConfig.requiredTier) {
      const tierHierarchy = ['free', 'premium', 'enterprise'];
      const userTierIndex = tierHierarchy.indexOf(userContext.subscriptionTier);
      const requiredTierIndex = tierHierarchy.indexOf(flagConfig.requiredTier);
      
      if (userTierIndex < requiredTierIndex) {
        return false;
      }
    }
    
    // Check geographic restrictions
    if (flagConfig.allowedCountries && 
        !flagConfig.allowedCountries.includes(userContext.country)) {
      return false;
    }
    
    // Check beta program enrollment
    if (flagConfig.requiresBetaProgram && 
        !userContext.betaPrograms.includes(flagConfig.betaProgramName)) {
      return false;
    }
    
    // Check rollout percentage
    if (flagConfig.rolloutPercentage < 100) {
      const hash = this.hashUserId(userContext.userId, featureName);
      return hash < (flagConfig.rolloutPercentage / 100);
    }
    
    return flagConfig.enabled;
  }
}

// Advanced analytics feature
class AnalyticsController {
  async getAdvancedAnalytics(userContext: UserContext): Promise<AnalyticsData> {
    const canAccess = await this.permissionService.canAccessFeature(
      'advanced_analytics',
      userContext
    );
    
    if (!canAccess) {
      throw new ForbiddenError('Advanced analytics requires premium subscription');
    }
    
    return this.analyticsService.getAdvancedData(userContext.userId);
  }
}

// Permission flag configuration
const permissionFlags = {
  'advanced_analytics': {
    enabled: true,
    requiredTier: 'premium',
    allowedCountries: ['US', 'CA', 'GB', 'DE', 'JP'],
    rolloutPercentage: 100,
    description: 'Access to advanced analytics dashboard'
  },
  'beta_ai_features': {
    enabled: true,
    requiresBetaProgram: true,
    betaProgramName: 'ai_early_access',
    rolloutPercentage: 25, // 25% of enrolled users
    description: 'Beta AI-powered features'
  }
};
```

---

## フラグアーキテクチャパターン

### クライアント側 vs サーバー側評価

**Server-Side Evaluation (Recommended):**

```typescript
// Server-side flag evaluation service
class ServerSideFeatureFlagService {
  constructor(
    private flagStore: FlagStore,
    private userService: UserService,
    private analytics: AnalyticsService
  ) {}
  
  async isEnabled(
    flagName: string,
    userContext: UserContext
  ): Promise<boolean> {
    try {
      const flag = await this.flagStore.getFlag(flagName);
      
      if (!flag) {
        console.warn(`Flag ${flagName} not found, defaulting to false`);
        return false;
      }
      
      const result = await this.evaluateFlag(flag, userContext);
      
      // Track flag evaluation for analytics
      await this.analytics.trackFlagEvaluation({
        flagName,
        userId: userContext.userId,
        result,
        timestamp: new Date()
      });
      
      return result;
      
    } catch (error) {
      console.error(`Error evaluating flag ${flagName}:`, error);
      return false; // Fail safe
    }
  }
  
  private async evaluateFlag(
    flag: FeatureFlag,
    context: UserContext
  ): Promise<boolean> {
    // Check if flag is globally enabled
    if (!flag.enabled) return false;
    
    // Evaluate targeting rules
    for (const rule of flag.targetingRules) {
      if (await this.evaluateRule(rule, context)) {
        return rule.result;
      }
    }
    
    // Default fallback
    return flag.defaultResult;
  }
}

// API endpoint for flag evaluation
class FeatureFlagController {
  @Get('/flags/:flagName')
  async evaluateFlag(
    @Param('flagName') flagName: string,
    @CurrentUser() user: User
  ): Promise<{ enabled: boolean }> {
    const userContext = this.buildUserContext(user);
    const enabled = await this.flagService.isEnabled(flagName, userContext);
    
    return { enabled };
  }
  
  @Get('/flags/bulk')
  async evaluateMultipleFlags(
    @Query('flags') flagNames: string,
    @CurrentUser() user: User
  ): Promise<Record<string, boolean>> {
    const flags = flagNames.split(',');
    const userContext = this.buildUserContext(user);
    const results: Record<string, boolean> = {};
    
    await Promise.all(
      flags.map(async (flagName) => {
        results[flagName] = await this.flagService.isEnabled(
          flagName,
          userContext
        );
      })
    );
    
    return results;
  }
}
```

**Client-Side Evaluation:**

```typescript
// Client-side flag service (for non-sensitive flags only)
class ClientSideFeatureFlagService {
  private flagCache: Map<string, FeatureFlag> = new Map();
  
  constructor(private apiClient: ApiClient) {}
  
  async initialize(userId: string): Promise<void> {
    try {
      // Fetch all flags for user at startup
      const flags = await this.apiClient.get(`/flags/all?userId=${userId}`);
      
      flags.forEach(flag => {
        this.flagCache.set(flag.name, flag);
      });
      
      // Set up periodic refresh
      setInterval(() => this.refreshFlags(userId), 5 * 60 * 1000); // 5 minutes
      
    } catch (error) {
      console.warn('Failed to initialize feature flags:', error);
    }
  }
  
  isEnabled(flagName: string): boolean {
    const flag = this.flagCache.get(flagName);
    return flag?.enabled ?? false;
  }
  
  private async refreshFlags(userId: string): Promise<void> {
    try {
      const flags = await this.apiClient.get(`/flags/all?userId=${userId}`);
      flags.forEach(flag => this.flagCache.set(flag.name, flag));
    } catch (error) {
      console.warn('Failed to refresh feature flags:', error);
    }
  }
}

// React hook for feature flags
function useFeatureFlag(flagName: string): boolean {
  const [enabled, setEnabled] = useState(false);
  const flagService = useContext(FeatureFlagContext);
  
  useEffect(() => {
    const checkFlag = () => {
      setEnabled(flagService.isEnabled(flagName));
    };
    
    checkFlag();
    
    // Listen for flag updates
    const unsubscribe = flagService.subscribe(flagName, checkFlag);
    return unsubscribe;
  }, [flagName, flagService]);
  
  return enabled;
}

// Usage in React component
function AdvancedDashboard() {
  const showAdvancedFeatures = useFeatureFlag('advanced_dashboard_features');
  
  return (
    <div>
      <h1>Dashboard</h1>
      {showAdvancedFeatures && (
        <AdvancedFeaturesPanel />
      )}
      <StandardDashboardContent />
    </div>
  );
}
```

---

### フラグ階層と依存関係

```typescript
interface FlagDependency {
  flagName: string;
  dependsOn: string[];
  conflictsWith: string[];
  parentFlag?: string;
}

class HierarchicalFlagService {
  private dependencies: Map<string, FlagDependency> = new Map();
  
  constructor(private baseFlagService: FeatureFlagService) {
    this.initializeDependencies();
  }
  
  async isEnabled(
    flagName: string,
    userContext: UserContext
  ): Promise<boolean> {
    // Check if flag has dependencies
    const dependency = this.dependencies.get(flagName);
    
    if (dependency) {
      // Check parent flag first
      if (dependency.parentFlag) {
        const parentEnabled = await this.baseFlagService.isEnabled(
          dependency.parentFlag,
          userContext
        );
        if (!parentEnabled) {
          return false; // Parent disabled, child must be disabled
        }
      }
      
      // Check dependencies
      for (const depFlag of dependency.dependsOn) {
        const depEnabled = await this.baseFlagService.isEnabled(
          depFlag,
          userContext
        );
        if (!depEnabled) {
          return false; // Required dependency not enabled
        }
      }
      
      // Check conflicts
      for (const conflictFlag of dependency.conflictsWith) {
        const conflictEnabled = await this.baseFlagService.isEnabled(
          conflictFlag,
          userContext
        );
        if (conflictEnabled) {
          return false; // Conflicting flag is enabled
        }
      }
    }
    
    // All dependencies satisfied, check the flag itself
    return this.baseFlagService.isEnabled(flagName, userContext);
  }
  
  private initializeDependencies(): void {
    // Example flag hierarchy: New checkout flow
    this.dependencies.set('new_checkout_flow', {
      flagName: 'new_checkout_flow',
      dependsOn: ['payment_service_v2', 'inventory_service_v2'],
      conflictsWith: ['legacy_checkout_flow'],
      parentFlag: 'checkout_experiments_enabled'
    });
    
    // Express checkout depends on new checkout flow
    this.dependencies.set('express_checkout', {
      flagName: 'express_checkout',
      dependsOn: ['new_checkout_flow'],
      conflictsWith: [],
      parentFlag: 'new_checkout_flow'
    });
    
    // Payment provider experiment
    this.dependencies.set('stripe_payment_experiment', {
      flagName: 'stripe_payment_experiment',
      dependsOn: ['payment_service_v2'],
      conflictsWith: ['paypal_payment_experiment'],
      parentFlag: 'payment_experiments_enabled'
    });
  }
}
```

---

## フラグ実装ベストプラクティス

### クリーンなフラグ統合

**✅ 良い例 - Strategy Pattern:**
```typescript
// Clean separation using strategy pattern
interface CheckoutStrategy {
  processCheckout(order: Order, payment: PaymentInfo): Promise<CheckoutResult>;
}

class LegacyCheckoutStrategy implements CheckoutStrategy {
  async processCheckout(order: Order, payment: PaymentInfo): Promise<CheckoutResult> {
    // Legacy implementation
    return this.legacyCheckoutService.process(order, payment);
  }
}

class NewCheckoutStrategy implements CheckoutStrategy {
  async processCheckout(order: Order, payment: PaymentInfo): Promise<CheckoutResult> {
    // New implementation
    return this.modernCheckoutService.process(order, payment);
  }
}

class CheckoutService {
  constructor(
    private featureFlags: FeatureFlagService,
    private legacyStrategy: LegacyCheckoutStrategy,
    private newStrategy: NewCheckoutStrategy
  ) {}
  
  async processCheckout(
    userId: string,
    order: Order,
    payment: PaymentInfo
  ): Promise<CheckoutResult> {
    const useNewCheckout = await this.featureFlags.isEnabled(
      'new_checkout_flow',
      { userId }
    );
    
    const strategy = useNewCheckout ? this.newStrategy : this.legacyStrategy;
    return strategy.processCheckout(order, payment);
  }
}
```

**❌ 悪い例 - Scattered Flag Checks:**
```typescript
// Scattered flag checks throughout code (avoid this!)
class CheckoutService {
  async processCheckout(
    userId: string,
    order: Order,
    payment: PaymentInfo
  ): Promise<CheckoutResult> {
    
    // Flag check 1
    const useNewValidation = await this.featureFlags.isEnabled(
      'new_validation',
      { userId }
    );
    
    if (useNewValidation) {
      await this.newValidationService.validate(order);
    } else {
      await this.legacyValidationService.validate(order);
    }
    
    // Flag check 2
    const useNewPricing = await this.featureFlags.isEnabled(
      'new_pricing_engine',
      { userId }
    );
    
    if (useNewPricing) {
      order.total = await this.newPricingService.calculate(order);
    } else {
      order.total = await this.legacyPricingService.calculate(order);
    }
    
    // Flag check 3
    const useNewPayment = await this.featureFlags.isEnabled(
      'new_payment_processor',
      { userId }
    );
    
    if (useNewPayment) {
      return this.newPaymentProcessor.process(payment);
    } else {
      return this.legacyPaymentProcessor.process(payment);
    }
  }
}
```

---

### フラグ評価の最適化

```typescript
// Batch flag evaluation for performance
class OptimizedFlagService {
  private cache: Map<string, CachedFlag> = new Map();
  
  async evaluateFlags(
    flagNames: string[],
    userContext: UserContext
  ): Promise<Map<string, boolean>> {
    const results = new Map<string, boolean>();
    const uncachedFlags: string[] = [];
    
    // Check cache first
    for (const flagName of flagNames) {
      const cached = this.cache.get(`${flagName}:${userContext.userId}`);
      if (cached && !this.isCacheExpired(cached)) {
        results.set(flagName, cached.enabled);
      } else {
        uncachedFlags.push(flagName);
      }
    }
    
    // Batch evaluate uncached flags
    if (uncachedFlags.length > 0) {
      const freshResults = await this.batchEvaluate(uncachedFlags, userContext);
      
      for (const [flagName, enabled] of freshResults) {
        results.set(flagName, enabled);
        this.updateCache(flagName, userContext.userId, enabled);
      }
    }
    
    return results;
  }
  
  private async batchEvaluate(
    flagNames: string[],
    userContext: UserContext
  ): Promise<Map<string, boolean>> {
    // Single database query for all flags
    const flags = await this.flagStore.getFlags(flagNames);
    const results = new Map<string, boolean>();
    
    for (const flag of flags) {
      const enabled = await this.evaluateFlag(flag, userContext);
      results.set(flag.name, enabled);
    }
    
    return results;
  }
}

// Flag evaluation middleware for web requests
class FlagMiddleware {
  constructor(private flagService: OptimizedFlagService) {}
  
  async middleware(req: Request, res: Response, next: NextFunction) {
    const user = req.user;
    if (!user) {
      return next();
    }
    
    // Pre-evaluate common flags for this request
    const commonFlags = [
      'new_ui_theme',
      'enhanced_search',
      'premium_features'
    ];
    
    const flagResults = await this.flagService.evaluateFlags(
      commonFlags,
      { userId: user.id, ...user.attributes }
    );
    
    // Attach to request for easy access
    req.flags = flagResults;
    next();
  }
}

// Usage in controller
class ProductController {
  async getProducts(req: Request): Promise<ProductResponse> {
    const useEnhancedSearch = req.flags?.get('enhanced_search') ?? false;
    
    if (useEnhancedSearch) {
      return this.enhancedSearchService.searchProducts(req.query);
    } else {
      return this.standardSearchService.searchProducts(req.query);
    }
  }
}
```

---

## フラグライフサイクル管理

### フラグ作成プロセス

```markdown
## Feature Flag Creation Checklist

### Planning Phase
- [ ] Flag purpose clearly defined
- [ ] Flag type identified (release, experiment, operational, permission)
- [ ] Target audience specified
- [ ] Success criteria established
- [ ] Rollout plan created
- [ ] Removal plan scheduled

### Implementation Phase
- [ ] Flag naming convention followed
- [ ] Default value set appropriately (fail-safe)
- [ ] Targeting rules configured
- [ ] Analytics/metrics tracking enabled
- [ ] Documentation written
- [ ] Code review completed

### Testing Phase
- [ ] Flag behavior tested in all states
- [ ] Performance impact assessed
- [ ] Security implications reviewed
- [ ] Fallback behavior verified
- [ ] Integration tests updated

### Deployment Phase
- [ ] Flag deployed to staging environment
- [ ] Smoke tests passed
- [ ] Gradual rollout plan executed
- [ ] Monitoring alerts configured
- [ ] Team notified of rollout status
```

---

### フラグクリーンアップと削除

```typescript
// Automated flag cleanup service
class FlagCleanupService {
  constructor(
    private flagStore: FlagStore,
    private codeAnalyzer: CodeAnalyzer,
    private notificationService: NotificationService
  ) {}
  
  async analyzeObsoleteFlags(): Promise<ObsoleteFlag[]> {
    const allFlags = await this.flagStore.getAllFlags();
    const obsoleteFlags: ObsoleteFlag[] = [];
    
    for (const flag of allFlags) {
      const analysis = await this.analyzeFlagUsage(flag);
      
      if (this.shouldBeRemoved(flag, analysis)) {
        obsoleteFlags.push({
          flag,
          reason: this.getRemovalReason(flag, analysis),
          lastUsed: analysis.lastUsedDate,
          codeReferences: analysis.codeReferences
        });
      }
    }
    
    return obsoleteFlags;
  }
  
  private shouldBeRemoved(flag: FeatureFlag, analysis: FlagAnalysis): boolean {
    const now = new Date();
    const daysSinceCreation = (now.getTime() - flag.createdDate.getTime()) / (1000 * 60 * 60 * 24);
    
    // リリースフラグ older than 30 days with 100% rollout
    if (flag.type === 'release' && 
        daysSinceCreation > 30 && 
        flag.rolloutPercentage === 100) {
      return true;
    }
    
    // 実験フラグ past their end date
    if (flag.type === 'experiment' && 
        flag.experimentEndDate && 
        now > flag.experimentEndDate) {
      return true;
    }
    
    // Unused flags (no code references)
    if (analysis.codeReferences.length === 0 && daysSinceCreation > 7) {
      return true;
    }
    
    return false;
  }
  
  async generateCleanupPlan(): Promise<CleanupPlan> {
    const obsoleteFlags = await this.analyzeObsoleteFlags();
    
    return {
      flagsToRemove: obsoleteFlags,
      estimatedEffort: this.calculateCleanupEffort(obsoleteFlags),
      codeChangesRequired: this.identifyCodeChanges(obsoleteFlags),
      deploymentPlan: this.createDeploymentPlan(obsoleteFlags)
    };
  }
  
  async executeCleanup(plan: CleanupPlan): Promise<void> {
    for (const obsoleteFlag of plan.flagsToRemove) {
      // Create removal PR
      const prUrl = await this.createRemovalPR(obsoleteFlag);
      
      // Schedule flag disabling
      await this.scheduleRemoval(obsoleteFlag.flag, prUrl);
      
      // Notify stakeholders
      await this.notificationService.notifyFlagRemoval({
        flagName: obsoleteFlag.flag.name,
        owner: obsoleteFlag.flag.owner,
        prUrl,
        reason: obsoleteFlag.reason
      });
    }
  }
}

// Flag removal template
const FLAG_REMOVAL_TEMPLATE = `
# Remove Feature Flag: {{flagName}}

## Flag Information
- **Name**: {{flagName}}
- **Type**: {{flagType}}
- **Created**: {{createdDate}}
- **Owner**: {{owner}}

## Removal Reason
{{removalReason}}

## Changes Made
- [ ] Removed flag evaluation calls
- [ ] Kept {{winningVariant}} implementation
- [ ] Removed {{losingVariant}} implementation
- [ ] Updated tests
- [ ] Updated documentation

## Testing
- [ ] All existing tests pass
- [ ] Manual testing completed
- [ ] Performance impact verified

## Rollback Plan
If issues arise:
1. Revert this PR
2. Re-enable flag in admin panel
3. Investigate issues
4. Create new removal plan
`;
```

---

## 高度なフラグパターン

### 監視付き段階的ロールアウト

```typescript
class GradualRolloutService {
  constructor(
    private flagService: FeatureFlagService,
    private metricsService: MetricsService,
    private alertService: AlertService
  ) {}
  
  async executeGradualRollout(config: RolloutConfig): Promise<void> {
    const stages = this.calculateRolloutStages(config);
    
    for (const stage of stages) {
      console.log(`Starting rollout stage: ${stage.percentage}%`);
      
      // Update flag percentage
      await this.flagService.updateRolloutPercentage(
        config.flagName,
        stage.percentage
      );
      
      // Wait for stage duration
      await this.delay(stage.duration);
      
      // Check metrics and decide whether to continue
      const shouldContinue = await this.evaluateStageMetrics(
        config.flagName,
        stage,
        config.successCriteria
      );
      
      if (!shouldContinue) {
        console.log(`Rollout stopped at ${stage.percentage}% due to metrics`);
        await this.rollback(config.flagName);
        return;
      }
    }
    
    console.log('Gradual rollout completed successfully');
  }
  
  private async evaluateStageMetrics(
    flagName: string,
    stage: RolloutStage,
    criteria: SuccessCriteria
  ): Promise<boolean> {
    const metrics = await this.metricsService.getMetrics({
      flagName,
      timeRange: { start: stage.startTime, end: new Date() },
      segmentBy: 'flagVariant'
    });
    
    // Check error rate
    if (metrics.errorRate > criteria.maxErrorRate) {
      await this.alertService.sendAlert({
        type: 'FLAG_ROLLOUT_HIGH_ERROR_RATE',
        flagName,
        currentErrorRate: metrics.errorRate,
        threshold: criteria.maxErrorRate
      });
      return false;
    }
    
    // Check performance metrics
    if (metrics.avgResponseTime > criteria.maxResponseTime) {
      await this.alertService.sendAlert({
        type: 'FLAG_ROLLOUT_PERFORMANCE_DEGRADATION',
        flagName,
        currentResponseTime: metrics.avgResponseTime,
        threshold: criteria.maxResponseTime
      });
      return false;
    }
    
    // Check business metrics
    if (criteria.businessMetrics) {
      for (const [metricName, threshold] of Object.entries(criteria.businessMetrics)) {
        if (metrics.businessMetrics[metricName] < threshold) {
          await this.alertService.sendAlert({
            type: 'FLAG_ROLLOUT_BUSINESS_METRIC_BELOW_THRESHOLD',
            flagName,
            metricName,
            currentValue: metrics.businessMetrics[metricName],
            threshold
          });
          return false;
        }
      }
    }
    
    return true;
  }
}

// Rollout configuration
const rolloutConfig: RolloutConfig = {
  flagName: 'new_recommendation_engine',
  stages: [
    { percentage: 1, duration: '1h' },    // 1% for 1 hour
    { percentage: 5, duration: '2h' },    // 5% for 2 hours
    { percentage: 10, duration: '4h' },   // 10% for 4 hours
    { percentage: 25, duration: '8h' },   // 25% for 8 hours
    { percentage: 50, duration: '12h' },  // 50% for 12 hours
    { percentage: 100, duration: '0h' }   // 100% (complete)
  ],
  successCriteria: {
    maxErrorRate: 0.01,      // 1% error rate
    maxResponseTime: 500,    // 500ms response time
    businessMetrics: {
      'conversion_rate': 0.85,  // At least 85% of baseline
      'user_satisfaction': 4.0  // At least 4.0 rating
    }
  },
  rollbackOnFailure: true
};
```

---

### 多変量テスト

```typescript
interface MultiVariateExperiment {
  name: string;
  variants: {
    [variantName: string]: {
      weight: number;
      config: Record<string, any>;
    };
  };
  trafficAllocation: number; // Percentage of users in experiment
  metrics: string[];
  startDate: Date;
  endDate: Date;
}

class MultiVariateTestingService {
  constructor(
    private userHashService: UserHashService,
    private metricsService: MetricsService
  ) {}
  
  getVariantForUser(
    experimentName: string,
    userId: string,
    experiment: MultiVariateExperiment
  ): string | null {
    // First, check if user is in experiment
    const allocationHash = this.userHashService.hash(userId, `${experimentName}_allocation`);
    if (allocationHash > experiment.trafficAllocation / 100) {
      return null; // User not in experiment
    }
    
    // Assign variant based on weighted distribution
    const variantHash = this.userHashService.hash(userId, `${experimentName}_variant`);
    let cumulativeWeight = 0;
    
    for (const [variantName, variant] of Object.entries(experiment.variants)) {
      cumulativeWeight += variant.weight;
      if (variantHash <= cumulativeWeight / 100) {
        return variantName;
      }
    }
    
    // Fallback to first variant
    return Object.keys(experiment.variants)[0];
  }
  
  async trackConversion(
    experimentName: string,
    userId: string,
    conversionType: string,
    value?: number
  ): Promise<void> {
    const variant = await this.getCurrentVariant(experimentName, userId);
    
    if (variant) {
      await this.metricsService.trackEvent({
        event: 'experiment_conversion',
        experimentName,
        variant,
        userId,
        conversionType,
        value,
        timestamp: new Date()
      });
    }
  }
}

// Example: Checkout page optimization experiment
const checkoutExperiment: MultiVariateExperiment = {
  name: 'checkout_optimization_q4_2024',
  variants: {
    'control': {
      weight: 25,
      config: {
        layout: 'standard',
        paymentMethods: ['card', 'paypal'],
        showTrustBadges: false
      }
    },
    'streamlined': {
      weight: 25,
      config: {
        layout: 'single_page',
        paymentMethods: ['card', 'paypal'],
        showTrustBadges: false
      }
    },
    'trust_enhanced': {
      weight: 25,
      config: {
        layout: 'standard',
        paymentMethods: ['card', 'paypal', 'apple_pay'],
        showTrustBadges: true
      }
    },
    'optimized': {
      weight: 25,
      config: {
        layout: 'single_page',
        paymentMethods: ['card', 'paypal', 'apple_pay', 'google_pay'],
        showTrustBadges: true
      }
    }
  },
  trafficAllocation: 50, // 50% of users in experiment
  metrics: ['conversion_rate', 'cart_abandonment', 'completion_time'],
  startDate: new Date('2024-11-01'),
  endDate: new Date('2024-11-30')
};

// Usage in checkout controller
class CheckoutController {
  async renderCheckoutPage(userId: string): Promise<CheckoutPageData> {
    const variant = this.multiVariateService.getVariantForUser(
      'checkout_optimization_q4_2024',
      userId,
      checkoutExperiment
    );
    
    if (!variant) {
      return this.renderStandardCheckout(); // User not in experiment
    }
    
    const config = checkoutExperiment.variants[variant].config;
    return this.renderCheckoutWithConfig(config, variant);
  }
  
  async processCheckout(userId: string, orderData: OrderData): Promise<void> {
    // Process the order
    const result = await this.orderService.processOrder(orderData);
    
    if (result.success) {
      // Track conversion for experiment
      await this.multiVariateService.trackConversion(
        'checkout_optimization_q4_2024',
        userId,
        'purchase_completed',
        orderData.total
      );
    }
  }
}
```

---

## Devin AI実践例

### フラグ実装

**Prompt 1: Implement Basic Feature Flag System**
```
Create a feature flag system for this application with the following requirements:

1. Support for different flag types (release, experiment, operational, permission)
2. Server-side flag evaluation with caching
3. REST API for flag management
4. Database schema for storing flag configurations
5. Integration with existing authentication system

Technical Stack: [Node.js/Python/Java/etc.]
Database: [PostgreSQL/MongoDB/etc.]

Include:
- Database migrations/schema
- Service layer with proper error handling
- API endpoints with input validation
- Caching layer for performance
- Configuration management
- Basic admin interface (optional)

Focus on clean architecture and testability.
```

**Prompt 2: Add Feature Flags to Existing Feature**
```
Add feature flags to control the rollout of this new feature:

[Paste existing code that needs feature flagging]

Requirements:
1. Wrap the new functionality with a feature flag
2. Maintain backward compatibility with existing behavior
3. Use strategy pattern to avoid scattered conditionals
4. Add proper error handling and fallbacks
5. Include unit tests for both flag states

Flag name: {{flag_name}}
Flag type: {{release/experiment/operational/permission}}
Default state: {{enabled/disabled}}

Show before/after code with clean integration patterns.
```

### 段階的ロールアウト実装

**Prompt 3: Create Gradual Rollout System**
```
Implement a gradual rollout system with automated monitoring:

1. Percentage-based rollout with configurable stages
2. Real-time metrics monitoring during rollout
3. Automatic rollback on metric thresholds breach
4. Support for different rollout strategies (percentage, user segments, geographic)
5. Integration with monitoring/alerting systems

Features needed:
- Rollout configuration management
- Metrics collection and analysis
- Alert system integration
- Rollback mechanisms
- Dashboard for rollout monitoring
- Audit logging

Include configuration examples and usage patterns for common scenarios.
```

**Prompt 4: Implement A/B Testing Framework**
```
Create an A/B testing framework on top of the feature flag system:

Requirements:
1. Multi-variant experiment support
2. Statistical significance calculation
3. User assignment consistency
4. Conversion tracking
5. Experiment results analysis
6. Integration with analytics platforms

Components needed:
- Experiment configuration management
- User bucketing algorithm
- Metrics tracking service
- Statistical analysis functions
- Results dashboard
- Experiment lifecycle management

Include examples for common A/B test scenarios (UI changes, algorithm changes, pricing tests).
```

### フラグ管理とクリーンアップ

**Prompt 5: Build Flag Lifecycle Management**
```
Create a comprehensive flag lifecycle management system:

1. Automated flag usage detection in codebase
2. Flag age and status tracking
3. Cleanup recommendations and automation
4. Dependency analysis between flags
5. Impact assessment for flag removal

Tools needed:
- Static code analysis for flag usage
- Database queries for flag metadata
- Automated cleanup suggestions
- Pull request generation for flag removal
- Documentation generation

Include scripts for:
- Finding obsolete flags
- Generating cleanup reports
- Creating removal pull requests
- Updating documentation
```

**Prompt 6: Create Flag Governance Dashboard**
```
Build a comprehensive dashboard for feature flag governance:

Features:
1. Flag inventory with metadata (owner, age, type, usage)
2. Flag health metrics (performance impact, error rates)
3. Cleanup recommendations and scheduling
4. Flag dependency visualization
5. Team/project flag usage analytics
6. Compliance and audit reporting

Dashboard sections:
- Overview/summary page
- Flag catalog with search/filtering
- Health monitoring and alerts
- Cleanup management
- Analytics and reporting
- Admin configuration

Include both technical metrics and business KPIs visualization.
```

### 統合パターン

**Prompt 7: Integrate with CI/CD Pipeline**
```
Create CI/CD integration for feature flag management:

1. Flag validation in pull requests
2. Automated flag deployment
3. Integration testing with different flag states
4. Flag rollout automation post-deployment
5. Monitoring and alerting setup

CI/CD Platform: [GitHub Actions/GitLab CI/Jenkins/etc.]

Components:
- Pre-commit hooks for flag validation
- Build pipeline integration
- Deployment automation
- Post-deployment verification
- Rollback procedures

Include workflow files and scripts for common deployment scenarios.
```

**Prompt 8: Implement Flag Security and Compliance**
```
Add security and compliance features to the feature flag system:

1. Role-based access control for flag management
2. Audit logging for all flag changes
3. Approval workflows for sensitive flags
4. Encryption for sensitive flag data
5. Compliance reporting (SOX, GDPR, etc.)

Security requirements:
- Authentication and authorization
- Data encryption at rest and in transit
- Audit trail maintenance
- Access control policies
- Sensitive data protection

Include compliance documentation and security best practices.
```

### パフォーマンス最適化

**Prompt 9: Optimize Flag Evaluation Performance**
```
Optimize the feature flag system for high-performance scenarios:

Current Performance Issues: [describe current bottlenecks]
Traffic Scale: [requests per second, user base size]

Optimizations needed:
1. Efficient caching strategies (local, distributed)
2. Batch flag evaluation
3. Async flag updates
4. Database query optimization
5. CDN integration for global distribution

Focus areas:
- Reduce flag evaluation latency to <1ms
- Support 100k+ requests per second
- Minimize memory footprint
- Optimize database queries
- Implement circuit breakers

Provide benchmarking scripts and performance testing strategies.
```

**Prompt 10: Create Flag Analytics and Reporting**
```
Build comprehensive analytics and reporting for feature flags:

1. Flag performance metrics (evaluation time, cache hit rates)
2. Business impact analysis (conversion rates, user engagement)
3. Technical health monitoring (error rates, system impact)
4. Usage analytics (which flags are used most, by whom)
5. ROI calculation for experiments

Reports needed:
- Daily/weekly flag health reports
- Experiment results summaries
- Performance impact analysis
- Usage trends and patterns
- Cleanup recommendations

Include both automated reporting and interactive dashboards.
Integration with existing analytics platforms (Google Analytics, Mixpanel, etc.).
```

---

## 監視と分析

### フラグパフォーマンスメトリクス

```typescript
interface FlagMetrics {
  flagName: string;
  evaluationCount: number;
  evaluationLatency: {
    p50: number;
    p95: number;
    p99: number;
    max: number;
  };
  cacheHitRate: number;
  errorRate: number;
  lastEvaluated: Date;
  userSegments: {
    segment: string;
    enabled: number;
    disabled: number;
  }[];
}

class FlagMetricsCollector {
  constructor(
    private metricsStore: MetricsStore,
    private alertService: AlertService
  ) {}
  
  async recordFlagEvaluation(
    flagName: string,
    userId: string,
    result: boolean,
    evaluationTime: number,
    cacheHit: boolean
  ): Promise<void> {
    const metrics = {
      flagName,
      userId,
      result,
      evaluationTime,
      cacheHit,
      timestamp: new Date()
    };
    
    await this.metricsStore.record(metrics);
    
    // Check for performance issues
    if (evaluationTime > 100) { // 100ms threshold
      await this.alertService.sendAlert({
        type: 'SLOW_FLAG_EVALUATION',
        flagName,
        evaluationTime,
        threshold: 100
      });
    }
  }
  
  async generateDailyReport(): Promise<FlagHealthReport> {
    const yesterday = new Date(Date.now() - 24 * 60 * 60 * 1000);
    const allFlags = await this.getAllActiveFlags();
    const report: FlagHealthReport = {
      date: yesterday,
      flagMetrics: [],
      overallHealth: 'healthy',
      alerts: []
    };
    
    for (const flagName of allFlags) {
      const metrics = await this.calculateFlagMetrics(flagName, yesterday);
      report.flagMetrics.push(metrics);
      
      // Check health thresholds
      if (metrics.errorRate > 0.01) { // 1% error rate
        report.alerts.push({
          severity: 'warning',
          message: `High error rate for flag ${flagName}: ${metrics.errorRate * 100}%`
        });
      }
      
      if (metrics.evaluationLatency.p95 > 50) { // 50ms P95
        report.alerts.push({
          severity: 'warning',
          message: `High latency for flag ${flagName}: ${metrics.evaluationLatency.p95}ms P95`
        });
      }
    }
    
    return report;
  }
}
```

---

### ビジネスインパクト追跡

```typescript
class ExperimentAnalyzer {
  constructor(
    private metricsService: MetricsService,
    private statisticsService: StatisticsService
  ) {}
  
  async analyzeExperiment(
    experimentName: string,
    timeRange: { start: Date; end: Date }
  ): Promise<ExperimentResults> {
    const variants = await this.getExperimentVariants(experimentName);
    const results: VariantResults[] = [];
    
    for (const variant of variants) {
      const metrics = await this.metricsService.getVariantMetrics(
        experimentName,
        variant,
        timeRange
      );
      
      results.push({
        variant,
        users: metrics.uniqueUsers,
        conversions: metrics.conversions,
        conversionRate: metrics.conversions / metrics.uniqueUsers,
        revenue: metrics.totalRevenue,
        revenuePerUser: metrics.totalRevenue / metrics.uniqueUsers,
        confidence: await this.statisticsService.calculateConfidence(
          metrics,
          this.getControlMetrics(results)
        )
      });
    }
    
    return {
      experimentName,
      timeRange,
      variants: results,
      winner: this.determineWinner(results),
      statisticalSignificance: this.calculateSignificance(results)
    };
  }
  
  private determineWinner(results: VariantResults[]): string {
    const control = results.find(r => r.variant === 'control');
    if (!control) return 'inconclusive';
    
    const bestVariant = results
      .filter(r => r.variant !== 'control')
      .reduce((best, current) => 
        current.conversionRate > best.conversionRate ? current : best
      );
    
    if (bestVariant.conversionRate > control.conversionRate && 
        bestVariant.confidence > 0.95) {
      return bestVariant.variant;
    }
    
    return 'inconclusive';
  }
}

// Automated experiment reporting
class ExperimentReporter {
  async generateWeeklyReport(): Promise<void> {
    const activeExperiments = await this.getActiveExperiments();
    const report = {
      reportDate: new Date(),
      experiments: []
    };
    
    for (const experiment of activeExperiments) {
      const analysis = await this.experimentAnalyzer.analyzeExperiment(
        experiment.name,
        { start: this.getWeekStart(), end: new Date() }
      );
      
      report.experiments.push({
        name: experiment.name,
        status: this.determineExperimentStatus(analysis),
        results: analysis,
        recommendations: this.generateRecommendations(analysis)
      });
    }
    
    await this.sendReportToStakeholders(report);
  }
  
  private generateRecommendations(analysis: ExperimentResults): string[] {
    const recommendations: string[] = [];
    
    if (analysis.winner !== 'inconclusive') {
      recommendations.push(
        `Consider rolling out ${analysis.winner} variant to all users`
      );
    }
    
    if (analysis.statisticalSignificance < 0.95) {
      recommendations.push(
        'Continue experiment to reach statistical significance'
      );
    }
    
    const lowPerformingVariants = analysis.variants.filter(
      v => v.conversionRate < 0.05 // 5% conversion rate threshold
    );
    
    if (lowPerformingVariants.length > 0) {
      recommendations.push(
        `Consider stopping low-performing variants: ${lowPerformingVariants.map(v => v.variant).join(', ')}`
      );
    }
    
    return recommendations;
  }
}
```

---

## セキュリティとコンプライアンス

### アクセス制御と権限

```typescript
enum FlagPermission {
  READ = 'flag:read',
  WRITE = 'flag:write',
  DELETE = 'flag:delete',
  ADMIN = 'flag:admin'
}

interface FlagAccessControl {
  flagName: string;
  permissions: {
    [userId: string]: FlagPermission[];
  };
  teamPermissions: {
    [teamId: string]: FlagPermission[];
  };
  publicRead: boolean;
}

class FlagSecurityService {
  constructor(
    private userService: UserService,
    private auditLogger: AuditLogger
  ) {}
  
  async checkPermission(
    userId: string,
    flagName: string,
    permission: FlagPermission
  ): Promise<boolean> {
    const user = await this.userService.getUser(userId);
    const flagAcl = await this.getFlagAccessControl(flagName);
    
    // Check direct user permissions
    const userPermissions = flagAcl.permissions[userId] || [];
    if (userPermissions.includes(permission) || userPermissions.includes(FlagPermission.ADMIN)) {
      return true;
    }
    
    // Check team permissions
    for (const teamId of user.teams) {
      const teamPermissions = flagAcl.teamPermissions[teamId] || [];
      if (teamPermissions.includes(permission) || teamPermissions.includes(FlagPermission.ADMIN)) {
        return true;
      }
    }
    
    // Check public read access
    if (permission === FlagPermission.READ && flagAcl.publicRead) {
      return true;
    }
    
    // Log access denial
    await this.auditLogger.log({
      event: 'ACCESS_DENIED',
      userId,
      flagName,
      permission,
      timestamp: new Date()
    });
    
    return false;
  }
  
  async updateFlag(
    userId: string,
    flagName: string,
    updates: Partial<FeatureFlag>
  ): Promise<void> {
    // Check write permission
    const hasPermission = await this.checkPermission(
      userId,
      flagName,
      FlagPermission.WRITE
    );
    
    if (!hasPermission) {
      throw new UnauthorizedError('Insufficient permissions to update flag');
    }
    
    const originalFlag = await this.flagStore.getFlag(flagName);
    await this.flagStore.updateFlag(flagName, updates);
    
    // Audit log the change
    await this.auditLogger.log({
      event: 'FLAG_UPDATED',
      userId,
      flagName,
      changes: this.calculateChanges(originalFlag, updates),
      timestamp: new Date()
    });
  }
}

// Role-based access control middleware
class FlagAuthMiddleware {
  constructor(private securityService: FlagSecurityService) {}
  
  requirePermission(permission: FlagPermission) {
    return async (req: Request, res: Response, next: NextFunction) => {
      const userId = req.user?.id;
      const flagName = req.params.flagName;
      
      if (!userId) {
        return res.status(401).json({ error: 'Authentication required' });
      }
      
      const hasPermission = await this.securityService.checkPermission(
        userId,
        flagName,
        permission
      );
      
      if (!hasPermission) {
        return res.status(403).json({ error: 'Insufficient permissions' });
      }
      
      next();
    };
  }
}

// Usage in routes
app.get('/api/flags/:flagName', 
  authenticate,
  flagAuthMiddleware.requirePermission(FlagPermission.READ),
  async (req, res) => {
    const flag = await flagService.getFlag(req.params.flagName);
    res.json(flag);
  }
);

app.put('/api/flags/:flagName',
  authenticate,
  flagAuthMiddleware.requirePermission(FlagPermission.WRITE),
  async (req, res) => {
    await flagSecurityService.updateFlag(
      req.user.id,
      req.params.flagName,
      req.body
    );
    res.json({ success: true });
  }
);
```

---

### 監査ログとコンプライアンス

```typescript
interface AuditEvent {
  eventId: string;
  eventType: string;
  timestamp: Date;
  userId: string;
  flagName?: string;
  details: Record<string, any>;
  ipAddress: string;
  userAgent: string;
}

class ComplianceAuditLogger {
  constructor(
    private auditStore: AuditStore,
    private encryptionService: EncryptionService
  ) {}
  
  async logFlagChange(event: {
    userId: string;
    flagName: string;
    changeType: 'created' | 'updated' | 'deleted';
    oldValue?: any;
    newValue?: any;
    reason?: string;
  }): Promise<void> {
    const auditEvent: AuditEvent = {
      eventId: crypto.randomUUID(),
      eventType: 'FLAG_CHANGE',
      timestamp: new Date(),
      userId: event.userId,
      flagName: event.flagName,
      details: {
        changeType: event.changeType,
        oldValue: event.oldValue ? await this.encryptSensitiveData(event.oldValue) : null,
        newValue: event.newValue ? await this.encryptSensitiveData(event.newValue) : null,
        reason: event.reason
      },
      ipAddress: this.getCurrentRequestIP(),
      userAgent: this.getCurrentRequestUserAgent()
    };
    
    await this.auditStore.store(auditEvent);
    
    // Send to compliance monitoring system
    await this.sendToComplianceSystem(auditEvent);
  }
  
  async generateComplianceReport(
    startDate: Date,
    endDate: Date
  ): Promise<ComplianceReport> {
    const events = await this.auditStore.getEvents({
      startDate,
      endDate,
      eventTypes: ['FLAG_CHANGE', 'ACCESS_DENIED', 'BULK_UPDATE']
    });
    
    return {
      reportPeriod: { start: startDate, end: endDate },
      totalEvents: events.length,
      eventsByType: this.groupEventsByType(events),
      flagChanges: this.analyzeFlagChanges(events),
      accessViolations: this.analyzeAccessViolations(events),
      complianceStatus: this.assessComplianceStatus(events),
      recommendations: this.generateComplianceRecommendations(events)
    };
  }
  
  private async encryptSensitiveData(data: any): Promise<string> {
    // Encrypt sensitive information for compliance
    const sensitiveFields = ['email', 'userId', 'personalInfo'];
    const sanitized = { ...data };
    
    for (const field of sensitiveFields) {
      if (sanitized[field]) {
        sanitized[field] = await this.encryptionService.encrypt(sanitized[field]);
      }
    }
    
    return JSON.stringify(sanitized);
  }
}

// GDPR compliance features
class GDPRComplianceService {
  async handleDataSubjectRequest(
    requestType: 'access' | 'deletion' | 'portability',
    userId: string
  ): Promise<void> {
    switch (requestType) {
      case 'access':
        return this.generateUserDataReport(userId);
      case 'deletion':
        return this.deleteUserData(userId);
      case 'portability':
        return this.exportUserData(userId);
    }
  }
  
  private async generateUserDataReport(userId: string): Promise<UserDataReport> {
    const flagEvaluations = await this.auditStore.getUserFlagEvaluations(userId);
    const experimentParticipation = await this.getExperimentParticipation(userId);
    
    return {
      userId,
      dataCollected: {
        flagEvaluations: flagEvaluations.length,
        experimentsParticipated: experimentParticipation.length,
        lastActivity: this.getLastActivityDate(flagEvaluations)
      },
      retentionPeriod: '2 years',
      dataProcessingPurpose: 'Feature flag evaluation and A/B testing',
      rightsInformation: {
        canRequestDeletion: true,
        canRequestPortability: true,
        canOptOut: true
      }
    };
  }
  
  private async deleteUserData(userId: string): Promise<void> {
    // Anonymize rather than delete to maintain experiment integrity
    await this.auditStore.anonymizeUserData(userId);
    await this.flagEvaluationStore.anonymizeUserData(userId);
    await this.experimentResultsStore.anonymizeUserData(userId);
    
    // Log the deletion for compliance
    await this.auditLogger.log({
      event: 'USER_DATA_DELETED',
      userId: 'ANONYMIZED',
      originalUserId: await this.encryptionService.encrypt(userId),
      timestamp: new Date()
    });
  }
}
```

---

## チェックリスト

### フィーチャーフラグ実装チェックリスト

**Planning and Design / 計画と設計:**
- [ ] Flag purpose and type clearly defined
- [ ] Target audience and rollout plan established
- [ ] Success metrics and criteria identified
- [ ] Fallback behavior designed
- [ ] Removal timeline planned
- [ ] Security and compliance requirements reviewed

**Implementation / 実装:**
- [ ] Flag naming convention followed
- [ ] Clean integration pattern used (avoid scattered conditionals)
- [ ] Proper error handling and fallbacks implemented
- [ ] Performance impact minimized
- [ ] Code review completed
- [ ] Documentation updated

**Testing / テスト:**
- [ ] Both flag states tested (enabled/disabled)
- [ ] Edge cases covered
- [ ] Performance impact measured
- [ ] Integration tests updated
- [ ] Load testing performed (if applicable)

**Deployment and Monitoring / デプロイメントと監視:**
- [ ] Flag deployed with safe defaults
- [ ] Monitoring and alerting configured
- [ ] Gradual rollout plan executed
- [ ] Metrics collection enabled
- [ ] Team notifications configured

**Maintenance and Cleanup / メンテナンスとクリーンアップ:**
- [ ] Flag usage tracked and monitored
- [ ] Regular cleanup reviews scheduled
- [ ] Removal process documented
- [ ] Technical debt prevented

---

### フラグセキュリティチェックリスト

**Access Control / アクセス制御:**
- [ ] Role-based permissions implemented
- [ ] Flag ownership assigned
- [ ] Team access properly configured
- [ ] Admin access restricted
- [ ] Public access controlled

**Data Protection / データ保護:**
- [ ] Sensitive flag data encrypted
- [ ] Audit logging enabled
- [ ] Data retention policies defined
- [ ] GDPR compliance addressed
- [ ] Cross-border data transfer considered

**Operational Security / 運用セキュリティ:**
- [ ] Flag evaluation secured (server-side preferred)
- [ ] API endpoints authenticated
- [ ] Rate limiting implemented
- [ ] Input validation applied
- [ ] Error messages sanitized

---

### フラグクリーンアップチェックリスト

**Pre-Cleanup Assessment / クリーンアップ前評価:**
- [ ] Flag usage analyzed in codebase
- [ ] Business impact assessed
- [ ] Stakeholder approval obtained
- [ ] Rollback plan prepared
- [ ] Dependencies identified

**Code Changes / コード変更:**
- [ ] Winning variant implementation kept
- [ ] Losing variant code removed
- [ ] Flag evaluation calls removed
- [ ] Tests updated
- [ ] Documentation updated

**Deployment and Verification / デプロイメントと検証:**
- [ ] Changes deployed safely
- [ ] Functionality verified
- [ ] Performance impact checked
- [ ] No regressions introduced
- [ ] Monitoring confirms stability

**Post-Cleanup / クリーンアップ後:**
- [ ] Flag configuration removed
- [ ] Database cleanup performed
- [ ] Analytics data archived
- [ ] Team notified
- [ ] Lessons learned documented

---

## 参考資料

### 主要リソース

- Martin Fowlerの「Feature Toggles」
- LaunchDarklyの「Feature Flags Best Practices」
- Unleash - オープンソースフィーチャーフラグプラットフォーム
- Microsoftの「Feature Toggle Strategies」
- Statsigの「A/B Testing and Feature Flags」

### ツールとプラットフォーム
- LaunchDarkly: https://launchdarkly.com/
- Unleash: https://unleash.github.io/
- Flagsmith: https://flagsmith.com/
- Split: https://split.io/
- ConfigCat: https://configcat.com/

### 関連標準
- [03-development-process/README.md](../README.md)
- [03-development-process/release-management.md](./release-management.md)
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