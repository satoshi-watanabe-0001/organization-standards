# TypeScript/JavaScript コーディング規約

## 6. Node.js固有規約（バックエンド）

### 6.1 NestJS推奨パターン

#### **Controller設計**
```typescript
// ✅ Good: NestJS Controller
import { 
  Controller, 
  Get, 
  Post, 
  Put, 
  Delete, 
  Body, 
  Param, 
  Query,
  UseGuards,
  HttpStatus,
  HttpException
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';

@ApiTags('users')
@Controller('users')
@UseGuards(JwtAuthGuard)
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get()
  @ApiOperation({ summary: 'Get all users' })
  @ApiResponse({ status: 200, description: 'Users retrieved successfully' })
  async findAll(
    @Query() query: GetUsersQueryDto
  ): Promise<PaginatedResponse<UserDto>> {
    try {
      const result = await this.usersService.findAll(query);
      return {
        success: true,
        data: result.users.map(user => this.mapToDto(user)),
        pagination: result.pagination,
      };
    } catch (error) {
      throw new HttpException(
        'Failed to retrieve users',
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Post()
  @ApiOperation({ summary: 'Create a new user' })
  @ApiResponse({ status: 201, description: 'User created successfully' })
  async create(@Body() createUserDto: CreateUserDto): Promise<ApiResponse<UserDto>> {
    try {
      const user = await this.usersService.create(createUserDto);
      return {
        success: true,
        data: this.mapToDto(user),
        message: 'User created successfully',
      };
    } catch (error) {
      if (error instanceof ValidationError) {
        throw new HttpException(error.message, HttpStatus.BAD_REQUEST);
      }
      throw new HttpException(
        'Failed to create user',
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update user' })
  async update(
    @Param('id') id: string,
    @Body() updateUserDto: UpdateUserDto
  ): Promise<ApiResponse<UserDto>> {
    const user = await this.usersService.update(id, updateUserDto);
    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }
    
    return {
      success: true,
      data: this.mapToDto(user),
      message: 'User updated successfully',
    };
  }

  private mapToDto(user: User): UserDto {
    return {
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role,
      createdAt: user.createdAt.toISOString(),
      updatedAt: user.updatedAt.toISOString(),
    };
  }
}
```

#### **Service設計**
```typescript
// ✅ Good: NestJS Service
import { Injectable, Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

@Injectable()
export class UsersService {
  private readonly logger = new Logger(UsersService.name);

  constructor(
    @InjectRepository(User)
    private usersRepository: Repository<User>,
    private emailService: EmailService,
    private auditService: AuditService
  ) {}

  async findAll(query: GetUsersQueryDto): Promise<{
    users: User[];
    pagination: PaginationInfo;
  }> {
    const { page = 1, limit = 10, role, search } = query;
    const skip = (page - 1) * limit;

    const queryBuilder = this.usersRepository
      .createQueryBuilder('user')
      .take(limit)
      .skip(skip);

    if (role) {
      queryBuilder.andWhere('user.role = :role', { role });
    }

    if (search) {
      queryBuilder.andWhere(
        '(user.name ILIKE :search OR user.email ILIKE :search)',
        { search: `%${search}%` }
      );
    }

    const [users, total] = await queryBuilder.getManyAndCount();

    return {
      users,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    };
  }

  async create(createUserDto: CreateUserDto): Promise<User> {
    const existingUser = await this.usersRepository.findOne({
      where: { email: createUserDto.email },
    });

    if (existingUser) {
      throw new ConflictException('User with this email already exists');
    }

    const user = this.usersRepository.create({
      ...createUserDto,
      id: generateId(),
      createdAt: new Date(),
      updatedAt: new Date(),
    });

    const savedUser = await this.usersRepository.save(user);

    // 非同期処理（ウェルカムメール送信）
    this.emailService.sendWelcomeEmail(savedUser.email, savedUser.name)
      .catch(error => {
        this.logger.error('Failed to send welcome email', {
          userId: savedUser.id,
          email: savedUser.email,
          error: error.message,
        });
      });

    // 監査ログ
    await this.auditService.log('user_created', {
      userId: savedUser.id,
      email: savedUser.email,
      createdBy: 'system', // 実際は認証情報から取得
    });

    this.logger.log(`User created: ${savedUser.id}`);
    return savedUser;
  }

  async update(id: string, updateUserDto: UpdateUserDto): Promise<User | null> {
    const user = await this.usersRepository.findOne({ where: { id } });
    if (!user) {
      return null;
    }

    const updatedUser = await this.usersRepository.save({
      ...user,
      ...updateUserDto,
      updatedAt: new Date(),
    });

    await this.auditService.log('user_updated', {
      userId: id,
      changes: updateUserDto,
      updatedBy: 'system', // 実際は認証情報から取得
    });

    this.logger.log(`User updated: ${id}`);
    return updatedUser;
  }
}
```

#### **DTO・バリデーション**
```typescript
// ✅ Good: DTO with validation
import { 
  IsString, 
  IsEmail, 
  IsOptional, 
  IsEnum, 
  MinLength,
  MaxLength,
  IsNumber,
  Min,
  Max
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class CreateUserDto {
  @ApiProperty({ description: 'User name', example: 'John Doe' })
  @IsString()
  @MinLength(2)
  @MaxLength(100)
  name: string;

  @ApiProperty({ description: 'User email', example: 'john@example.com' })
  @IsEmail()
  email: string;

  @ApiPropertyOptional({ description: 'User role', enum: UserRole })
  @IsOptional()
  @IsEnum(UserRole)
  role?: UserRole;
}

export class UpdateUserDto {
  @ApiPropertyOptional()
  @IsOptional()
  @IsString()
  @MinLength(2)
  @MaxLength(100)
  name?: string;

  @ApiPropertyOptional()
  @IsOptional()
  @IsEmail()
  email?: string;
}

export class GetUsersQueryDto {
  @ApiPropertyOptional({ description: 'Page number', example: 1 })
  @IsOptional()
  @IsNumber()
  @Min(1)
  page?: number;

  @ApiPropertyOptional({ description: 'Items per page', example: 10 })
  @IsOptional()
  @IsNumber()
  @Min(1)
  @Max(100)
  limit?: number;

  @ApiPropertyOptional({ description: 'Filter by role', enum: UserRole })
  @IsOptional()
  @IsEnum(UserRole)
  role?: UserRole;

  @ApiPropertyOptional({ description: 'Search term' })
  @IsOptional()
  @IsString()
  @MaxLength(100)
  search?: string;
}

export class UserDto {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;

  @ApiProperty()
  email: string;

  @ApiProperty({ enum: UserRole })
  role: UserRole;

  @ApiProperty()
  createdAt: string;

  @ApiProperty()
  updatedAt: string;
}
```

**Devin指示**: NestJS装飾子、依存性注入、DTO/バリデーション、Swagger文書化を必ず実装せよ

---
