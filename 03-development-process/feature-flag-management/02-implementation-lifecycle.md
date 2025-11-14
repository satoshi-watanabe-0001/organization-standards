# フィーチャーフラグ管理標準

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

