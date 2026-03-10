# TypeScript/JavaScript コーディング規約

## 2. 言語仕様・構文規約

### 2.1 変数宣言

#### **基本原則**
```typescript
// ✅ Good: const優先、letは再代入が必要な場合のみ
const userName = 'John Doe';
const userList = ['Alice', 'Bob'];
let counter = 0; // 再代入が必要な場合のみ

// ❌ Bad: varは使用禁止
var globalVar = 'never use var';
```

#### **分割代入の活用**
```typescript
// ✅ Good: オブジェクト分割代入
const { name, email, age } = user;
const { data, loading, error } = useApiCall();

// ✅ Good: 配列分割代入
const [first, second, ...rest] = items;
const [isOpen, setIsOpen] = useState(false);

// ✅ Good: デフォルト値の指定
const { name = 'Anonymous', age = 0 } = user;
```

#### **テンプレートリテラル**
```typescript
// ✅ Good: テンプレートリテラル使用
const message = `Hello, ${userName}! You have ${count} messages.`;
const apiUrl = `${BASE_URL}/users/${userId}/profile`;

// ❌ Bad: 文字列連結
const message = 'Hello, ' + userName + '! You have ' + count + ' messages.';
```

**Devin指示**: const優先、分割代入を積極活用、テンプレートリテラルで文字列構築せよ

### 2.2 関数定義

#### **関数宣言 vs アロー関数**
```typescript
// ✅ Good: 純粋関数・ユーティリティ関数はアロー関数
const calculateTotal = (items: Item[]): number => {
  return items.reduce((sum, item) => sum + item.price, 0);
};

// ✅ Good: 関数式も可能
const processData = async (data: RawData): Promise<ProcessedData> => {
  const validated = await validateData(data);
  return transformData(validated);
};

// ✅ Good: トップレベル関数・複雑な関数は関数宣言
function complexBusinessLogic(
  input: ComplexInput
): Promise<ComplexOutput> {
  // 複雑な処理...
}

// ✅ Good: メソッドは通常の関数記法
class UserService {
  async findById(id: string): Promise<User | null> {
    return this.repository.findOne(id);
  }
}
```

#### **高階関数・関数型プログラミング**
```typescript
// ✅ Good: map, filter, reduce を積極活用
const activeUsers = users.filter(user => user.status === 'active');
const userNames = users.map(user => user.name);
const totalRevenue = orders.reduce((sum, order) => sum + order.total, 0);

// ✅ Good: チェイン処理
const result = data
  .filter(item => item.isValid)
  .map(item => ({ ...item, processed: true }))
  .sort((a, b) => a.createdAt.getTime() - b.createdAt.getTime());

// ❌ Bad: forループの過度な使用
const activeUsers = [];
for (let i = 0; i < users.length; i++) {
  if (users[i].status === 'active') {
    activeUsers.push(users[i]);
  }
}
```

**Devin指示**: アロー関数を基本とし、関数型プログラミングを積極活用せよ

### 2.3 非同期処理

#### **async/await優先**
```typescript
// ✅ Good: async/await使用
const fetchUserProfile = async (userId: string): Promise<UserProfile> => {
  try {
    const user = await userService.findById(userId);
    const profile = await profileService.getProfile(user.id);
    return profile;
  } catch (error) {
    logger.error('Failed to fetch user profile', { userId, error });
    throw new UserProfileFetchError('Failed to fetch profile', error);
  }
};

// ✅ Good: 並列処理
const fetchUserData = async (userId: string) => {
  const [user, posts, followers] = await Promise.all([
    userService.findById(userId),
    postService.getByUserId(userId),
    followerService.getFollowers(userId),
  ]);
  
  return { user, posts, followers };
};

// ❌ Bad: Promiseチェイン（複雑な場合）
const fetchUserProfile = (userId: string) => {
  return userService.findById(userId)
    .then(user => profileService.getProfile(user.id))
    .then(profile => profile)
    .catch(error => {
      logger.error('Failed to fetch user profile', { userId, error });
      throw error;
    });
};
```

#### **エラーハンドリング**
```typescript
// ✅ Good: 詳細なエラーハンドリング
const processPayment = async (paymentData: PaymentData): Promise<PaymentResult> => {
  try {
    const validated = await validatePaymentData(paymentData);
    const result = await paymentGateway.process(validated);
    
    await auditLogger.log('payment_processed', {
      userId: paymentData.userId,
      amount: paymentData.amount,
      transactionId: result.transactionId,
    });
    
    return result;
  } catch (error) {
    if (error instanceof ValidationError) {
      throw new PaymentValidationError(error.message, error);
    }
    if (error instanceof NetworkError) {
      throw new PaymentNetworkError('Payment gateway unavailable', error);
    }
    
    logger.error('Unexpected payment error', {
      userId: paymentData.userId,
      error: error.message,
      stack: error.stack,
    });
    throw new PaymentProcessingError('Payment processing failed', error);
  }
};
```

**Devin指示**: async/awaitを優先し、並列処理はPromise.allを活用、詳細なエラーハンドリングを実装せよ

---
