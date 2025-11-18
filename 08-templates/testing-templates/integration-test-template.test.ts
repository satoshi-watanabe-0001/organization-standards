/**
 * 統合テストテンプレート (TypeScript)
 * Integration Test Template for TypeScript/Node.js Projects
 * 
 * 使用方法:
 * 1. このファイルをコピーして tests/integration/ 配下に配置
 * 2. feature-name.test.ts のようにリネーム
 * 3. テスト対象に応じて実装を追加
 */

import request from 'supertest';
import { app } from '../../src/app';
import { DataSource } from 'typeorm';
// import { Resource } from '../../src/entities/Resource';

describe('Resource API Integration Tests', () => {
  let dataSource: DataSource;

  beforeAll(async () => {
    // TestContainersで作成されたDataSourceを取得
    dataSource = (global as any).__TEST_DATASOURCE__;
  });

  afterEach(async () => {
    // 各テスト後にデータをクリア
    // await dataSource.getRepository(Resource).clear();
  });

  // ==========================================
  // API Endpoint Tests
  // ==========================================

  describe('POST /api/resources', () => {
    it('should create a new resource successfully', async () => {
      // Arrange
      const payload = {
        name: 'Test Resource',
        description: 'Test Description'
      };

      // Act
      const response = await request(app)
        .post('/api/resources')
        .send(payload)
        .expect(201);

      // Assert
      expect(response.body.name).toBe('Test Resource');
      expect(response.body.id).toBeDefined();

      // データベース検証
      // const resourceRepo = dataSource.getRepository(Resource);
      // const resource = await resourceRepo.findOne({ where: { id: response.body.id } });
      // expect(resource).not.toBeNull();
    });

    it('should return 400 for validation error', async () => {
      // Arrange
      const payload = {
        name: '', // 空文字（バリデーションエラー）
      };

      // Act & Assert
      const response = await request(app)
        .post('/api/resources')
        .send(payload)
        .expect(400);

      expect(response.body.message || response.body.error).toBeDefined();
    });
  });

  describe('GET /api/resources/:id', () => {
    it('should retrieve resource by id', async () => {
      // Arrange: テストデータ作成
      // const resourceRepo = dataSource.getRepository(Resource);
      // const resource = resourceRepo.create({
      //   name: 'Get Test',
      //   description: 'Test Description'
      // });
      // await resourceRepo.save(resource);

      // Act
      // const response = await request(app)
      //   .get(`/api/resources/${resource.id}`)
      //   .expect(200);

      // Assert
      // expect(response.body.id).toBe(resource.id);
      // expect(response.body.name).toBe('Get Test');
    });

    it('should return 404 for non-existent resource', async () => {
      // Act & Assert
      await request(app)
        .get('/api/resources/99999')
        .expect(404);
    });
  });

  describe('PUT /api/resources/:id', () => {
    it('should update resource successfully', async () => {
      // Arrange: テストデータ作成
      // const resourceRepo = dataSource.getRepository(Resource);
      // const resource = resourceRepo.create({
      //   name: 'Original Name',
      //   description: 'Original Description'
      // });
      // await resourceRepo.save(resource);

      // const payload = {
      //   name: 'Updated Name',
      //   description: 'Updated Description'
      // };

      // Act
      // const response = await request(app)
      //   .put(`/api/resources/${resource.id}`)
      //   .send(payload)
      //   .expect(200);

      // Assert
      // expect(response.body.name).toBe('Updated Name');
      
      // データベース検証
      // const updated = await resourceRepo.findOne({ where: { id: resource.id } });
      // expect(updated!.name).toBe('Updated Name');
    });
  });

  describe('DELETE /api/resources/:id', () => {
    it('should delete resource successfully', async () => {
      // Arrange: テストデータ作成
      // const resourceRepo = dataSource.getRepository(Resource);
      // const resource = resourceRepo.create({
      //   name: 'To Delete',
      //   description: 'Will be deleted'
      // });
      // await resourceRepo.save(resource);

      // Act
      // await request(app)
      //   .delete(`/api/resources/${resource.id}`)
      //   .expect(204);

      // Assert: データベース検証（削除されていることを確認）
      // const deleted = await resourceRepo.findOne({ where: { id: resource.id } });
      // expect(deleted).toBeNull();
    });
  });

  // ==========================================
  // Database Operations Tests
  // ==========================================

  describe('CRUD Operations', () => {
    it('should perform complete CRUD cycle', async () => {
      // const resourceRepo = dataSource.getRepository(Resource);

      // Create
      // const resource = resourceRepo.create({
      //   name: 'CRUD Test',
      //   description: 'Test'
      // });
      // await resourceRepo.save(resource);
      // expect(resource.id).toBeDefined();

      // Read
      // const retrieved = await resourceRepo.findOne({ where: { id: resource.id } });
      // expect(retrieved).not.toBeNull();
      // expect(retrieved!.name).toBe('CRUD Test');

      // Update
      // retrieved!.name = 'Updated';
      // await resourceRepo.save(retrieved!);
      // const updated = await resourceRepo.findOne({ where: { id: resource.id } });
      // expect(updated!.name).toBe('Updated');

      // Delete
      // await resourceRepo.remove(updated!);
      // const deleted = await resourceRepo.findOne({ where: { id: resource.id } });
      // expect(deleted).toBeNull();
    });
  });

  // ==========================================
  // Transaction Tests
  // ==========================================

  describe('Transaction Tests', () => {
    it('should commit transaction successfully', async () => {
      // Arrange
      // const payload = {
      //   resource: {
      //     name: 'Main Resource'
      //   },
      //   relatedData: {
      //     info: 'Related Information'
      //   }
      // };

      // Act
      // const response = await request(app)
      //   .post('/api/resources/with-related')
      //   .send(payload)
      //   .expect(201);

      // Assert: 両方のテーブルにデータが存在
      // const resourceRepo = dataSource.getRepository(Resource);
      // const resource = await resourceRepo.findOne({ 
      //   where: { name: 'Main Resource' },
      //   relations: ['relatedData']
      // });
      // expect(resource).not.toBeNull();
      // expect(resource!.relatedData).toBeDefined();
    });

    it('should rollback transaction on error', async () => {
      // Arrange
      // const payload = {
      //   resource: {
      //     name: 'Test Resource'
      //   },
      //   relatedData: {
      //     info: null // NOT NULL制約違反
      //   }
      // };

      // Act
      // await request(app)
      //   .post('/api/resources/with-related')
      //   .send(payload)
      //   .expect(400);

      // Assert: リソースも作成されていない（ロールバック成功）
      // const resourceRepo = dataSource.getRepository(Resource);
      // const resource = await resourceRepo.findOne({ where: { name: 'Test Resource' } });
      // expect(resource).toBeNull();
    });
  });

  // ==========================================
  // Authentication & Authorization Tests
  // ==========================================

  describe('Authentication Tests', () => {
    it.skip('should return 401 for unauthorized access', async () => {
      // await request(app)
      //   .get('/api/protected/resources')
      //   .expect(401);
    });

    it.skip('should allow access with valid token', async () => {
      // const token = 'valid_jwt_token';
      
      // await request(app)
      //   .get('/api/protected/resources')
      //   .set('Authorization', `Bearer ${token}`)
      //   .expect(200);
    });
  });
});
