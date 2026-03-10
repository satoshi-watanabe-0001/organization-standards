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

