# TypeScript/JavaScript コーディング規約

## 5. React固有規約（フロントエンド）

### 5.1 コンポーネント設計

#### **関数コンポーネント・Hooks**
```typescript
// ✅ Good: 関数コンポーネント + TypeScript
interface UserProfileProps {
  userId: string;
  onUserUpdate?: (user: User) => void;
  className?: string;
}

const UserProfile: React.FC<UserProfileProps> = ({ 
  userId, 
  onUserUpdate,
  className 
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        setLoading(true);
        const userData = await userService.findById(userId);
        setUser(userData);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load user');
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [userId]);

  const handleUserUpdate = useCallback(async (updatedData: Partial<User>) => {
    if (!user) return;
    
    try {
      const updatedUser = await userService.update(user.id, updatedData);
      setUser(updatedUser);
      onUserUpdate?.(updatedUser);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update user');
    }
  }, [user, onUserUpdate]);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;
  if (!user) return <div>User not found</div>;

  return (
    <div className={className}>
      <h2>{user.name}</h2>
      <p>{user.email}</p>
      <UserEditForm user={user} onSubmit={handleUserUpdate} />
    </div>
  );
};

export default UserProfile;
```

#### **カスタムHooks**
```typescript
// ✅ Good: カスタムHooks
interface UseApiStateOptions<T> {
  initialData?: T;
  onSuccess?: (data: T) => void;
  onError?: (error: Error) => void;
}

const useApiState = <T>(
  apiCall: () => Promise<T>,
  dependencies: React.DependencyList = [],
  options: UseApiStateOptions<T> = {}
) => {
  const [data, setData] = useState<T | null>(options.initialData || null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const execute = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await apiCall();
      setData(result);
      options.onSuccess?.(result);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Unknown error');
      setError(error);
      options.onError?.(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [apiCall, options]);

  useEffect(() => {
    execute();
  }, dependencies);

  return {
    data,
    loading,
    error,
    refetch: execute,
  };
};

// 使用例
const UserList: React.FC = () => {
  const { data: users, loading, error, refetch } = useApiState(
    () => userService.getAll(),
    [],
    {
      onError: (error) => toast.error(error.message),
    }
  );

  return (
    <div>
      <button onClick={refetch}>Refresh</button>
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage message={error.message} />}
      {users?.map(user => <UserCard key={user.id} user={user} />)}
    </div>
  );
};
```

#### **状態管理**
```typescript
// ✅ Good: Zustand使用例
interface UserStore {
  currentUser: User | null;
  users: User[];
  loading: boolean;
  error: string | null;
  
  // Actions
  setCurrentUser: (user: User | null) => void;
  fetchUsers: () => Promise<void>;
  updateUser: (id: string, updates: Partial<User>) => Promise<void>;
  clearError: () => void;
}

const useUserStore = create<UserStore>((set, get) => ({
  currentUser: null,
  users: [],
  loading: false,
  error: null,

  setCurrentUser: (user) => set({ currentUser: user }),

  fetchUsers: async () => {
    set({ loading: true, error: null });
    try {
      const users = await userService.getAll();
      set({ users, loading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Failed to fetch users',
        loading: false 
      });
    }
  },

  updateUser: async (id, updates) => {
    try {
      const updatedUser = await userService.update(id, updates);
      const { users, currentUser } = get();
      
      set({
        users: users.map(user => user.id === id ? updatedUser : user),
        currentUser: currentUser?.id === id ? updatedUser : currentUser,
      });
    } catch (error) {
      set({ error: error instanceof Error ? error.message : 'Failed to update user' });
    }
  },

  clearError: () => set({ error: null }),
}));
```

**Devin指示**: 関数コンポーネント、TypeScript props、カスタムHooks、適切な状態管理を実装せよ

### 5.2 Next.js固有パターン

#### **ページコンポーネント**
```typescript
// ✅ Good: Next.js App Router
// app/users/[id]/page.tsx
interface UserPageProps {
  params: { id: string };
  searchParams: { [key: string]: string | string[] | undefined };
}

const UserPage: React.FC<UserPageProps> = async ({ params }) => {
  // Server Component
  const user = await userService.findById(params.id);
  
  if (!user) {
    notFound();
  }

  return (
    <div>
      <h1>{user.name}</h1>
      <UserProfileClient user={user} />
    </div>
  );
};

export default UserPage;

// ✅ Good: Client Component
'use client';

interface UserProfileClientProps {
  user: User;
}

const UserProfileClient: React.FC<UserProfileClientProps> = ({ user }) => {
  const [isEditing, setIsEditing] = useState(false);
  
  return (
    <div>
      {isEditing ? (
        <UserEditForm 
          user={user} 
          onSave={() => setIsEditing(false)}
          onCancel={() => setIsEditing(false)}
        />
      ) : (
        <UserDisplay 
          user={user} 
          onEdit={() => setIsEditing(true)}
        />
      )}
    </div>
  );
};
```

#### **API Routes**
```typescript
// ✅ Good: API Route Handler
// app/api/users/route.ts
import { NextRequest, NextResponse } from 'next/server';

interface CreateUserRequestBody {
  name: string;
  email: string;
  role?: UserRole;
}

export async function GET(request: NextRequest) {
  try {
    const searchParams = request.nextUrl.searchParams;
    const page = parseInt(searchParams.get('page') || '1');
    const limit = parseInt(searchParams.get('limit') || '10');
    
    const users = await userService.getAll({ page, limit });
    
    return NextResponse.json({
      success: true,
      data: users,
    });
  } catch (error) {
    logger.error('Failed to fetch users', { error });
    return NextResponse.json(
      { success: false, error: 'Failed to fetch users' },
      { status: 500 }
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const body: CreateUserRequestBody = await request.json();
    
    // バリデーション
    if (!body.name || !body.email) {
      return NextResponse.json(
        { success: false, error: 'Name and email are required' },
        { status: 400 }
      );
    }
    
    const user = await userService.create(body);
    
    return NextResponse.json({
      success: true,
      data: user,
    }, { status: 201 });
  } catch (error) {
    if (error instanceof ValidationError) {
      return NextResponse.json(
        { success: false, error: error.message },
        { status: 400 }
      );
    }
    
    logger.error('Failed to create user', { error });
    return NextResponse.json(
      { success: false, error: 'Failed to create user' },
      { status: 500 }
    );
  }
}
```

**Devin指示**: App Router、Server/Client Component適切分離、型安全なAPI Routesを実装せよ

---
