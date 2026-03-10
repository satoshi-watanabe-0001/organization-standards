# TypeScript/JavaScript コーディング規約

## 7. テスト規約

### 7.1 単体テスト（Jest）

#### **テスト構造・命名**
```typescript
// ✅ Good: テストファイル構造
// src/services/__tests__/user.service.test.ts
import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { UsersService } from '../users.service';
import { User } from '../entities/user.entity';
import { CreateUserDto } from '../dto/create-user.dto';

describe('UsersService', () => {
  let service: UsersService;
  let repository: Repository<User>;
  let emailService: EmailService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UsersService,
        {
          provide: getRepositoryToken(User),
          useValue: {
            find: jest.fn(),
            findOne: jest.fn(),
            create: jest.fn(),
            save: jest.fn(),
            createQueryBuilder: jest.fn(),
          },
        },
        {
          provide: EmailService,
          useValue: {
            sendWelcomeEmail: jest.fn(),
          },
        },
        {
          provide: AuditService,
          useValue: {
            log: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<UsersService>(UsersService);
    repository = module.get<Repository<User>>(getRepositoryToken(User));
    emailService = module.get<EmailService>(EmailService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('create', () => {
    const createUserDto: CreateUserDto = {
      name: 'John Doe',
      email: 'john@example.com',
      role: UserRole.USER,
    };

    it('should create a user successfully', async () => {
      // Arrange
      const mockUser: User = {
        id: 'user-123',
        ...createUserDto,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      jest.spyOn(repository, 'findOne').mockResolvedValue(null);
      jest.spyOn(repository, 'create').mockReturnValue(mockUser);
      jest.spyOn(repository, 'save').mockResolvedValue(mockUser);
      jest.spyOn(emailService, 'sendWelcomeEmail').mockResolvedValue(undefined);

      // Act
      const result = await service.create(createUserDto);

      // Assert
      expect(result).toEqual(mockUser);
      expect(repository.findOne).toHaveBeenCalledWith({
        where: { email: createUserDto.email },
      });
      expect(repository.create).toHaveBeenCalledWith(
        expect.objectContaining({
          name: createUserDto.name,
          email: createUserDto.email,
          role: createUserDto.role,
        })
      );
      expect(repository.save).toHaveBeenCalledWith(mockUser);
      expect(emailService.sendWelcomeEmail).toHaveBeenCalledWith(
        mockUser.email,
        mockUser.name
      );
    });

    it('should throw ConflictException when user already exists', async () => {
      // Arrange
      const existingUser: User = {
        id: 'existing-user',
        ...createUserDto,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      jest.spyOn(repository, 'findOne').mockResolvedValue(existingUser);

      // Act & Assert
      await expect(service.create(createUserDto)).rejects.toThrow(
        ConflictException
      );
      expect(repository.findOne).toHaveBeenCalledWith({
        where: { email: createUserDto.email },
      });
      expect(repository.create).not.toHaveBeenCalled();
      expect(repository.save).not.toHaveBeenCalled();
    });

    it('should handle email service failure gracefully', async () => {
      // Arrange
      const mockUser: User = {
        id: 'user-123',
        ...createUserDto,
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      jest.spyOn(repository, 'findOne').mockResolvedValue(null);
      jest.spyOn(repository, 'create').mockReturnValue(mockUser);
      jest.spyOn(repository, 'save').mockResolvedValue(mockUser);
      jest.spyOn(emailService, 'sendWelcomeEmail').mockRejectedValue(
        new Error('Email service unavailable')
      );

      // Act
      const result = await service.create(createUserDto);

      // Assert
      expect(result).toEqual(mockUser);
      // メール送信エラーはサービス作成を阻害しない
    });
  });

  describe('findAll', () => {
    it('should return paginated users', async () => {
      // Arrange
      const mockUsers: User[] = [
        { id: '1', name: 'User 1', email: 'user1@example.com', role: UserRole.USER, createdAt: new Date(), updatedAt: new Date() },
        { id: '2', name: 'User 2', email: 'user2@example.com', role: UserRole.USER, createdAt: new Date(), updatedAt: new Date() },
      ];

      const queryBuilder = {
        take: jest.fn().mockReturnThis(),
        skip: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        getManyAndCount: jest.fn().mockResolvedValue([mockUsers, 2]),
      };

      jest.spyOn(repository, 'createQueryBuilder').mockReturnValue(queryBuilder as any);

      const query = { page: 1, limit: 10 };

      // Act
      const result = await service.findAll(query);

      // Assert
      expect(result.users).toEqual(mockUsers);
      expect(result.pagination).toEqual({
        page: 1,
        limit: 10,
        total: 2,
        totalPages: 1,
      });
      expect(queryBuilder.take).toHaveBeenCalledWith(10);
      expect(queryBuilder.skip).toHaveBeenCalledWith(0);
    });
  });
});
```

#### **React コンポーネントテスト**
```typescript
// ✅ Good: React Testing Library
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserProfile } from '../UserProfile';
import { userService } from '../../services/user.service';

// Mock external dependencies
jest.mock('../../services/user.service');
const mockUserService = userService as jest.Mocked<typeof userService>;

describe('UserProfile', () => {
  const mockUser: User = {
    id: 'user-123',
    name: 'John Doe',
    email: 'john@example.com',
    role: UserRole.USER,
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should display user information when loaded', async () => {
    // Arrange
    mockUserService.findById.mockResolvedValue(mockUser);

    // Act
    render(<UserProfile userId="user-123" />);

    // Assert
    expect(screen.getByText('Loading...')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('john@example.com')).toBeInTheDocument();
    });

    expect(mockUserService.findById).toHaveBeenCalledWith('user-123');
  });

  it('should display error message when user fetch fails', async () => {
    // Arrange
    mockUserService.findById.mockRejectedValue(new Error('User not found'));

    // Act
    render(<UserProfile userId="user-123" />);

    // Assert
    await waitFor(() => {
      expect(screen.getByText('User not found')).toBeInTheDocument();
    });
  });

  it('should handle user update', async () => {
    // Arrange
    const onUserUpdate = jest.fn();
    const updatedUser = { ...mockUser, name: 'Jane Doe' };
    
    mockUserService.findById.mockResolvedValue(mockUser);
    mockUserService.update.mockResolvedValue(updatedUser);

    // Act
    render(<UserProfile userId="user-123" onUserUpdate={onUserUpdate} />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });

    const editButton = screen.getByRole('button', { name: /edit/i });
    await userEvent.click(editButton);

    const nameInput = screen.getByLabelText(/name/i);
    await userEvent.clear(nameInput);
    await userEvent.type(nameInput, 'Jane Doe');

    const saveButton = screen.getByRole('button', { name: /save/i });
    await userEvent.click(saveButton);

    // Assert
    await waitFor(() => {
      expect(mockUserService.update).toHaveBeenCalledWith('user-123', {
        name: 'Jane Doe',
      });
      expect(onUserUpdate).toHaveBeenCalledWith(updatedUser);
    });
  });
});
```

**Devin指示**: AAA パターン、適切なモック、非同期処理テスト、ユーザーインタラクションテストを実装せよ

---
