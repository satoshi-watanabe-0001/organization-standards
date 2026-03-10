# フィーチャーフラグ管理標準

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

