# 検索技術スタック / Search Technology Stack

```yaml
version: "2.0.0"
last_updated: "2025-01-15"
status: "active"
owner: "Backend Architecture Team"
category: "technology-stack"
```

## 目次 / Table of Contents

1. [概要 / Overview](#概要--overview)
2. [検索エンジン / Search Engines](#検索エンジン--search-engines)
3. [全文検索 / Full-Text Search](#全文検索--full-text-search)
4. [ベクトル検索 / Vector Search](#ベクトル検索--vector-search)
5. [検索アーキテクチャパターン / Search Architecture Patterns](#検索アーキテクチャパターン--search-architecture-patterns)
6. [インデックス設計 / Index Design](#インデックス設計--index-design)
7. [クエリ最適化 / Query Optimization](#クエリ最適化--query-optimization)
8. [多言語検索 / Multilingual Search](#多言語検索--multilingual-search)
9. [検索分析 / Search Analytics](#検索分析--search-analytics)
10. [パフォーマンスチューニング / Performance Tuning](#パフォーマンスチューニング--performance-tuning)
11. [セキュリティ / Security](#セキュリティ--security)
12. [ベストプラクティス / Best Practices](#ベストプラクティス--best-practices)
13. [参考資料 / References](#参考資料--references)

---

## 概要 / Overview

このドキュメントは、組織全体で使用する検索技術スタックの標準を定義します。

### 目的 / Purpose

- 高速で関連性の高い検索体験の提供
- スケーラブルな検索インフラストラクチャの構築
- 多様な検索要件への対応
- 検索品質の継続的改善

### 適用範囲 / Scope

- 全文検索
- ファセット検索
- 地理空間検索
- ベクトル検索(セマンティック検索)
- オートコンプリート/サジェスト
- ログ検索と分析

### 検索の種類 / Types of Search

```yaml
search_types:
  full_text_search:
    description: "テキストベースの全文検索"
    use_cases:
      - "製品検索"
      - "コンテンツ検索"
      - "ドキュメント検索"
  
  faceted_search:
    description: "複数の属性による絞り込み検索"
    use_cases:
      - "Eコマース商品フィルタリング"
      - "不動産物件検索"
  
  geospatial_search:
    description: "位置情報ベースの検索"
    use_cases:
      - "近隣店舗検索"
      - "配達エリア検索"
  
  vector_search:
    description: "ベクトル類似度検索"
    use_cases:
      - "セマンティック検索"
      - "画像類似検索"
      - "レコメンデーション"
  
  autocomplete:
    description: "入力補完"
    use_cases:
      - "検索ボックスサジェスト"
      - "タグ入力補完"
```

---

## 検索エンジン / Search Engines

### Elasticsearch (標準 / Standard)

```yaml
search_engine:
  name: "Elasticsearch"
  version: "8.x"
  status: "standard"
  
  deployment_options:
    managed: "Amazon OpenSearch Service"
    self_hosted: "EKS / ECS"
  
  features:
    - "分散全文検索"
    - "リアルタイムインデックス"
    - "RESTful API"
    - "スケーラブルアーキテクチャ"
    - "豊富なクエリDSL"
    - "集計とアナリティクス"
  
  use_cases:
    - "アプリケーション検索"
    - "ログ分析"
    - "メトリクス可視化"
    - "セキュリティ分析"
  
  cluster_architecture:
    master_nodes: 3
    data_nodes: "3+ (ワークロード依存)"
    coordination_nodes: "2+ (オプション)"
    ingest_nodes: "2+ (大量データ投入時)"
```

**Elasticsearch設定例**:

```typescript
import { Client } from '@elastic/elasticsearch';

const client = new Client({
  node: process.env.ELASTICSEARCH_URL || 'https://localhost:9200',
  auth: {
    username: process.env.ELASTICSEARCH_USERNAME!,
    password: process.env.ELASTICSEARCH_PASSWORD!,
  },
  tls: {
    rejectUnauthorized: false, // 本番環境では true
  },
});

// インデックス作成
export async function createProductIndex(): Promise<void> {
  await client.indices.create({
    index: 'products',
    body: {
      settings: {
        number_of_shards: 3,
        number_of_replicas: 2,
        analysis: {
          analyzer: {
            custom_analyzer: {
              type: 'custom',
              tokenizer: 'standard',
              filter: ['lowercase', 'asciifolding', 'stop', 'snowball'],
            },
            autocomplete_analyzer: {
              type: 'custom',
              tokenizer: 'standard',
              filter: ['lowercase', 'edge_ngram'],
            },
          },
          filter: {
            edge_ngram: {
              type: 'edge_ngram',
              min_gram: 2,
              max_gram: 10,
            },
          },
        },
      },
      mappings: {
        properties: {
          name: {
            type: 'text',
            analyzer: 'custom_analyzer',
            fields: {
              keyword: { type: 'keyword' },
              autocomplete: {
                type: 'text',
                analyzer: 'autocomplete_analyzer',
                search_analyzer: 'standard',
              },
            },
          },
          description: {
            type: 'text',
            analyzer: 'custom_analyzer',
          },
          category: {
            type: 'keyword',
          },
          price: {
            type: 'float',
          },
          stock: {
            type: 'integer',
          },
          rating: {
            type: 'float',
          },
          tags: {
            type: 'keyword',
          },
          created_at: {
            type: 'date',
          },
          location: {
            type: 'geo_point',
          },
          features: {
            type: 'nested',
            properties: {
              name: { type: 'keyword' },
              value: { type: 'keyword' },
            },
          },
        },
      },
    },
  });
}

// ドキュメントインデックス
export async function indexProduct(product: any): Promise<void> {
  await client.index({
    index: 'products',
    id: product.id,
    document: product,
    refresh: 'wait_for', // テスト環境のみ、本番は false
  });
}

// バルクインデックス
export async function bulkIndexProducts(products: any[]): Promise<void> {
  const body = products.flatMap((product) => [
    { index: { _index: 'products', _id: product.id } },
    product,
  ]);

  const result = await client.bulk({ body, refresh: false });

  if (result.errors) {
    const erroredDocuments: any[] = [];
    result.items.forEach((action: any, i: number) => {
      const operation = Object.keys(action)[0];
      if (action[operation].error) {
        erroredDocuments.push({
          status: action[operation].status,
          error: action[operation].error,
          document: products[i],
        });
      }
    });
    console.error('Bulk indexing errors:', erroredDocuments);
  }
}

// 検索クエリ
export interface SearchParams {
  query?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  tags?: string[];
  location?: { lat: number; lon: number; distance: string };
  page?: number;
  size?: number;
  sort?: string;
}

export async function searchProducts(params: SearchParams): Promise<any> {
  const {
    query = '',
    category,
    minPrice,
    maxPrice,
    tags = [],
    location,
    page = 1,
    size = 20,
    sort = 'relevance',
  } = params;

  const must: any[] = [];
  const filter: any[] = [];

  // テキスト検索
  if (query) {
    must.push({
      multi_match: {
        query,
        fields: ['name^3', 'description^2', 'tags'],
        type: 'best_fields',
        fuzziness: 'AUTO',
        prefix_length: 2,
      },
    });
  }

  // カテゴリフィルター
  if (category) {
    filter.push({ term: { category } });
  }

  // 価格範囲フィルター
  if (minPrice !== undefined || maxPrice !== undefined) {
    filter.push({
      range: {
        price: {
          ...(minPrice !== undefined && { gte: minPrice }),
          ...(maxPrice !== undefined && { lte: maxPrice }),
        },
      },
    });
  }

  // タグフィルター
  if (tags.length > 0) {
    filter.push({
      terms: { tags },
    });
  }

  // 地理検索
  if (location) {
    filter.push({
      geo_distance: {
        distance: location.distance,
        location: {
          lat: location.lat,
          lon: location.lon,
        },
      },
    });
  }

  // ソート
  let sortConfig: any[] = [];
  switch (sort) {
    case 'price_asc':
      sortConfig = [{ price: 'asc' }];
      break;
    case 'price_desc':
      sortConfig = [{ price: 'desc' }];
      break;
    case 'rating':
      sortConfig = [{ rating: 'desc' }];
      break;
    case 'newest':
      sortConfig = [{ created_at: 'desc' }];
      break;
    default:
      sortConfig = ['_score'];
  }

  const response = await client.search({
    index: 'products',
    body: {
      query: {
        bool: {
          must: must.length > 0 ? must : [{ match_all: {} }],
          filter,
        },
      },
      sort: sortConfig,
      from: (page - 1) * size,
      size,
      aggs: {
        categories: {
          terms: { field: 'category', size: 20 },
        },
        price_ranges: {
          range: {
            field: 'price',
            ranges: [
              { to: 1000 },
              { from: 1000, to: 5000 },
              { from: 5000, to: 10000 },
              { from: 10000 },
            ],
          },
        },
        avg_rating: {
          avg: { field: 'rating' },
        },
      },
      highlight: {
        fields: {
          name: {},
          description: {},
        },
        pre_tags: ['<mark>'],
        post_tags: ['</mark>'],
      },
    },
  });

  return {
    total: response.hits.total,
    hits: response.hits.hits.map((hit: any) => ({
      ...hit._source,
      id: hit._id,
      score: hit._score,
      highlights: hit.highlight,
    })),
    aggregations: response.aggregations,
    page,
    size,
  };
}

// オートコンプリート
export async function autocomplete(prefix: string, size: number = 10): Promise<string[]> {
  const response = await client.search({
    index: 'products',
    body: {
      query: {
        match: {
          'name.autocomplete': {
            query: prefix,
            operator: 'and',
          },
        },
      },
      _source: ['name'],
      size,
    },
  });

  return response.hits.hits.map((hit: any) => hit._source.name);
}

// サジェスト
export async function suggest(query: string): Promise<any> {
  const response = await client.search({
    index: 'products',
    body: {
      suggest: {
        name_suggestion: {
          text: query,
          term: {
            field: 'name',
            suggest_mode: 'popular',
            min_word_length: 3,
          },
        },
        phrase_suggestion: {
          text: query,
          phrase: {
            field: 'name',
            size: 5,
            gram_size: 3,
            direct_generator: [
              {
                field: 'name',
                suggest_mode: 'always',
              },
            ],
          },
        },
      },
    },
  });

  return response.suggest;
}
```

### Amazon OpenSearch Service (推奨 / Recommended)

```yaml
search_engine:
  name: "Amazon OpenSearch Service"
  status: "recommended"
  
  advantages:
    - "フルマネージド"
    - "自動バックアップ"
    - "自動パッチ適用"
    - "AWS統合"
    - "セキュリティ機能"
  
  features:
    - "Elasticsearch / OpenSearch互換"
    - "Kibana / OpenSearch Dashboards"
    - "Fine-grained access control"
    - "暗号化 (at rest / in transit)"
  
  instance_types:
    - "t3.small.search (開発)"
    - "m6g.large.search (本番)"
    - "r6g.xlarge.search (メモリ集約)"
```

### Algolia (承認済み / Approved)

```yaml
search_engine:
  name: "Algolia"
  type: "Search-as-a-Service"
  status: "approved"
  
  use_cases:
    - "高速なフロントエンド検索"
    - "モバイルアプリ検索"
    - "Eコマース検索"
  
  features:
    - "サブミリ秒レスポンス"
    - "Typo-tolerance"
    - "ファセット検索"
    - "パーソナライゼーション"
    - "A/Bテスト"
    - "検索分析"
  
  pricing:
    model: "検索リクエスト数 + レコード数"
    consideration: "コスト注意"
```

**Algolia設定例**:

```typescript
import algoliasearch from 'algoliasearch';

const client = algoliasearch(
  process.env.ALGOLIA_APP_ID!,
  process.env.ALGOLIA_API_KEY!
);

const index = client.initIndex('products');

// インデックス設定
export async function configureAlgoliaIndex(): Promise<void> {
  await index.setSettings({
    searchableAttributes: [
      'name',
      'unordered(description)',
      'category',
      'tags',
    ],
    attributesForFaceting: [
      'filterOnly(category)',
      'filterOnly(brand)',
      'price',
      'rating',
    ],
    customRanking: ['desc(rating)', 'desc(popularity)'],
    ranking: [
      'typo',
      'geo',
      'words',
      'filters',
      'proximity',
      'attribute',
      'exact',
      'custom',
    ],
    typoTolerance: 'min',
    minWordSizefor1Typo: 4,
    minWordSizefor2Typos: 8,
    hitsPerPage: 20,
    maxValuesPerFacet: 100,
  });
}

// 検索
export async function searchAlgolia(query: string, filters?: any): Promise<any> {
  const results = await index.search(query, {
    filters: filters?.filters,
    facetFilters: filters?.facetFilters,
    numericFilters: filters?.numericFilters,
    page: filters?.page || 0,
    hitsPerPage: filters?.hitsPerPage || 20,
  });

  return results;
}
```

### Meilisearch (評価中 / Under Evaluation)

```yaml
search_engine:
  name: "Meilisearch"
  status: "under_evaluation"
  
  advantages:
    - "軽量で高速"
    - "簡単なセットアップ"
    - "Typo-tolerance"
    - "オープンソース"
  
  use_cases:
    - "中小規模アプリケーション"
    - "プロトタイピング"
```

---

## 全文検索 / Full-Text Search

### アナライザー設計 / Analyzer Design

```yaml
text_analysis:
  tokenization:
    standard: "単語単位で分割"
    ngram: "文字単位で分割"
    edge_ngram: "前方一致用"
  
  token_filters:
    lowercase: "小文字化"
    stop_words: "ストップワード除去"
    stemming: "語幹抽出"
    synonym: "同義語展開"
    asciifolding: "アクセント記号除去"
  
  custom_analyzers:
    japanese:
      tokenizer: "kuromoji_tokenizer"
      filters:
        - "kuromoji_baseform"
        - "kuromoji_part_of_speech"
        - "cjk_width"
        - "lowercase"
    
    english:
      tokenizer: "standard"
      filters:
        - "lowercase"
        - "stop"
        - "snowball"
```

**日本語アナライザー設定**:

```json
{
  "settings": {
    "analysis": {
      "tokenizer": {
        "kuromoji_custom": {
          "type": "kuromoji_tokenizer",
          "mode": "search",
          "discard_punctuation": true,
          "user_dictionary_rules": [
            "東京スカイツリー,東京 スカイツリー,トウキョウ スカイツリー,カスタム名詞"
          ]
        }
      },
      "analyzer": {
        "japanese_analyzer": {
          "type": "custom",
          "tokenizer": "kuromoji_custom",
          "filter": [
            "kuromoji_baseform",
            "kuromoji_part_of_speech",
            "ja_stop",
            "kuromoji_stemmer",
            "lowercase",
            "cjk_width"
          ]
        },
        "japanese_reading": {
          "type": "custom",
          "tokenizer": "kuromoji_tokenizer",
          "filter": ["kuromoji_readingform", "lowercase"]
        }
      },
      "filter": {
        "ja_stop": {
          "type": "stop",
          "stopwords": ["の", "に", "は", "を", "た", "が", "で", "て", "と", "し"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "japanese_analyzer",
        "fields": {
          "reading": {
            "type": "text",
            "analyzer": "japanese_reading"
          },
          "keyword": {
            "type": "keyword"
          }
        }
      }
    }
  }
}
```

### クエリタイプ / Query Types

```yaml
query_types:
  match:
    description: "標準の全文検索"
    use_case: "一般的な検索"
  
  multi_match:
    description: "複数フィールド検索"
    use_case: "タイトル、説明、タグ横断検索"
  
  bool:
    description: "複合クエリ"
    clauses:
      - "must: AND条件"
      - "should: OR条件"
      - "must_not: NOT条件"
      - "filter: スコアに影響しない条件"
  
  fuzzy:
    description: "あいまい検索"
    use_case: "タイポ対応"
  
  prefix:
    description: "前方一致検索"
    use_case: "オートコンプリート"
  
  wildcard:
    description: "ワイルドカード検索"
    use_case: "部分一致"
  
  phrase:
    description: "フレーズ検索"
    use_case: "完全一致フレーズ"
```

---

## ベクトル検索 / Vector Search

### セマンティック検索 / Semantic Search

```yaml
vector_search:
  description: "意味的な類似度に基づく検索"
  
  embedding_models:
    - name: "OpenAI Embeddings"
      dimensions: 1536
      use_case: "汎用的なテキスト埋め込み"
    
    - name: "Sentence Transformers"
      models: ["all-MiniLM-L6-v2", "paraphrase-multilingual"]
      use_case: "多言語対応"
    
    - name: "Amazon Titan Embeddings"
      dimensions: 1024
      use_case: "AWS統合"
  
  storage:
    elasticsearch: "dense_vector フィールド"
    pgvector: "PostgreSQL拡張"
    pinecone: "専用ベクトルDB"
    weaviate: "AI-Native DB"
```

**Elasticsearchベクトル検索**:

```typescript
// インデックスマッピング
await client.indices.create({
  index: 'semantic_search',
  body: {
    mappings: {
      properties: {
        title: { type: 'text' },
        content: { type: 'text' },
        title_vector: {
          type: 'dense_vector',
          dims: 1536,
          index: true,
          similarity: 'cosine',
        },
        content_vector: {
          type: 'dense_vector',
          dims: 1536,
          index: true,
          similarity: 'cosine',
        },
      },
    },
  },
});

// ベクトル生成 (OpenAI例)
import { OpenAI } from 'openai';

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });

async function generateEmbedding(text: string): Promise<number[]> {
  const response = await openai.embeddings.create({
    model: 'text-embedding-3-small',
    input: text,
  });
  return response.data[0].embedding;
}

// ドキュメントインデックス
export async function indexWithVector(doc: any): Promise<void> {
  const titleVector = await generateEmbedding(doc.title);
  const contentVector = await generateEmbedding(doc.content);

  await client.index({
    index: 'semantic_search',
    document: {
      ...doc,
      title_vector: titleVector,
      content_vector: contentVector,
    },
  });
}

// ベクトル検索
export async function semanticSearch(query: string, k: number = 10): Promise<any> {
  const queryVector = await generateEmbedding(query);

  const response = await client.search({
    index: 'semantic_search',
    body: {
      knn: {
        field: 'content_vector',
        query_vector: queryVector,
        k,
        num_candidates: 100,
      },
      _source: ['title', 'content'],
    },
  });

  return response.hits.hits;
}

// ハイブリッド検索 (キーワード + ベクトル)
export async function hybridSearch(query: string, k: number = 10): Promise<any> {
  const queryVector = await generateEmbedding(query);

  const response = await client.search({
    index: 'semantic_search',
    body: {
      query: {
        bool: {
          should: [
            {
              multi_match: {
                query,
                fields: ['title^2', 'content'],
              },
            },
          ],
        },
      },
      knn: {
        field: 'content_vector',
        query_vector: queryVector,
        k,
        num_candidates: 100,
        boost: 0.5,
      },
      size: k,
    },
  });

  return response.hits.hits;
}
```

### pgvector (PostgreSQL拡張)

```yaml
vector_search:
  name: "pgvector"
  status: "approved"
  
  use_cases:
    - "既存PostgreSQLとの統合"
    - "トランザクション要件"
    - "中小規模ベクトル検索"
  
  features:
    - "L2距離"
    - "内積"
    - "コサイン類似度"
    - "IVFFlat / HNSW インデックス"
```

**pgvector実装例**:

```sql
-- 拡張有効化
CREATE EXTENSION vector;

-- テーブル作成
CREATE TABLE documents (
  id SERIAL PRIMARY KEY,
  title TEXT,
  content TEXT,
  embedding vector(1536)
);

-- インデックス作成
CREATE INDEX ON documents USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
-- または HNSW (PostgreSQL 16+)
CREATE INDEX ON documents USING hnsw (embedding vector_cosine_ops);

-- ベクトル検索
SELECT id, title, 1 - (embedding <=> '[0.1, 0.2, ...]') AS similarity
FROM documents
ORDER BY embedding <=> '[0.1, 0.2, ...]'
LIMIT 10;
```

```typescript
import { Pool } from 'pg';

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
});

export async function vectorSearch(queryVector: number[], limit: number = 10): Promise<any[]> {
  const result = await pool.query(
    `
    SELECT id, title, content, 
           1 - (embedding <=> $1) AS similarity
    FROM documents
    ORDER BY embedding <=> $1
    LIMIT $2
    `,
    [JSON.stringify(queryVector), limit]
  );

  return result.rows;
}
```

---

## 検索アーキテクチャパターン / Search Architecture Patterns

### インデクサーパターン / Indexer Pattern

```yaml
indexer_pattern:
  description: "データベース変更を検索エンジンに同期"
  
  strategies:
    real_time:
      method: "Change Data Capture (CDC)"
      tools: ["Debezium", "AWS DMS"]
      latency: "秒単位"
    
    near_real_time:
      method: "イベント駆動"
      tools: ["Kafka", "SQS"]
      latency: "数秒"
    
    batch:
      method: "定期バッチ処理"
      tools: ["Cron", "AWS Batch"]
      latency: "分〜時間単位"
```

**イベント駆動インデクサー**:

```typescript
import { Consumer } from 'kafkajs';

export class SearchIndexer {
  private consumer: Consumer;

  constructor(consumer: Consumer) {
    this.consumer = consumer;
  }

  async start(): Promise<void> {
    await this.consumer.subscribe({ topics: ['product.created', 'product.updated', 'product.deleted'] });

    await this.consumer.run({
      eachMessage: async ({ topic, message }) => {
        const event = JSON.parse(message.value!.toString());

        try {
          switch (topic) {
            case 'product.created':
            case 'product.updated':
              await this.indexProduct(event.data);
              break;
            case 'product.deleted':
              await this.deleteProduct(event.data.id);
              break;
          }
        } catch (error) {
          console.error('Indexing failed:', error);
          // DLQへ送信
        }
      },
    });
  }

  private async indexProduct(product: any): Promise<void> {
    await client.index({
      index: 'products',
      id: product.id,
      document: {
        ...product,
        indexed_at: new Date().toISOString(),
      },
    });
  }

  private async deleteProduct(id: string): Promise<void> {
    await client.delete({
      index: 'products',
      id,
    });
  }
}
```

### キャッシング戦略 / Caching Strategy

```yaml
caching:
  query_cache:
    location: "Redis"
    ttl: "5分"
    use_case: "頻繁な同一クエリ"
  
  result_cache:
    location: "CDN / Redis"
    ttl: "1時間"
    use_case: "人気検索結果"
  
  aggregation_cache:
    location: "Redis"
    ttl: "15分"
    use_case: "ファセット集計"
```

**検索キャッシング実装**:

```typescript
import Redis from 'ioredis';
import crypto from 'crypto';

const redis = new Redis(process.env.REDIS_URL!);

export class CachedSearch {
  private cacheTTL = 300; // 5分

  async search(params: SearchParams): Promise<any> {
    const cacheKey = this.generateCacheKey(params);

    // キャッシュチェック
    const cached = await redis.get(cacheKey);
    if (cached) {
      console.log('Cache hit');
      return JSON.parse(cached);
    }

    // 検索実行
    const results = await searchProducts(params);

    // キャッシュ保存
    await redis.setex(cacheKey, this.cacheTTL, JSON.stringify(results));

    return results;
  }

  private generateCacheKey(params: SearchParams): string {
    const normalized = JSON.stringify(params, Object.keys(params).sort());
    const hash = crypto.createHash('sha256').update(normalized).digest('hex');
    return `search:${hash}`;
  }

  async invalidateCache(pattern: string = 'search:*'): Promise<void> {
    const keys = await redis.keys(pattern);
    if (keys.length > 0) {
      await redis.del(...keys);
    }
  }
}
```

---

## インデックス設計 / Index Design

### マッピング設計 / Mapping Design

```yaml
mapping_best_practices:
  field_types:
    text: "全文検索用"
    keyword: "完全一致、集計、ソート用"
    date: "日付範囲検索"
    numeric: "数値範囲検索、集計"
    geo_point: "地理検索"
    nested: "複雑なオブジェクト配列"
  
  multi_fields:
    description: "同じフィールドを複数の方法でインデックス"
    example:
      name:
        type: "text"
        fields:
          keyword: "keyword"
          autocomplete: "edge_ngram"
```

### インデックス管理 / Index Management

```yaml
index_management:
  lifecycle:
    hot: "アクティブな書き込み、高速検索"
    warm: "読み取りのみ、検索最適化"
    cold: "アーカイブ、低コストストレージ"
    delete: "自動削除"
  
  rollover:
    trigger: "サイズ or 時間ベース"
    policy: "30日 or 50GB"
  
  reindexing:
    use_case: "マッピング変更"
    strategy: "Zero-downtime reindex"
```

**Index Lifecycle Management (ILM)**:

```json
{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_age": "7d",
            "max_size": "50gb",
            "max_docs": 10000000
          },
          "set_priority": {
            "priority": 100
          }
        }
      },
      "warm": {
        "min_age": "30d",
        "actions": {
          "shrink": {
            "number_of_shards": 1
          },
          "forcemerge": {
            "max_num_segments": 1
          },
          "set_priority": {
            "priority": 50
          }
        }
      },
      "cold": {
        "min_age": "90d",
        "actions": {
          "freeze": {},
          "set_priority": {
            "priority": 0
          }
        }
      },
      "delete": {
        "min_age": "365d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

---

## クエリ最適化 / Query Optimization

### パフォーマンスチューニング / Performance Tuning

```yaml
optimization:
  query_optimization:
    - "不要なフィールドは_source除外"
    - "filter context使用 (キャッシュ可能)"
    - "bool queryの最適化"
    - "script使用の最小化"
  
  index_optimization:
    - "適切なシャード数"
    - "refresh_intervalの調整"
    - "force_merge定期実行"
  
  hardware_optimization:
    - "メモリ: ヒープサイズは物理メモリの50%以下"
    - "ディスク: SSD推奨"
    - "CPU: 検索ワークロードはCPU集約的"
```

**スロークエリ対策**:

```typescript
export class QueryOptimizer {
  // フィールド制限
  async searchWithMinimalFields(query: string): Promise<any> {
    return await client.search({
      index: 'products',
      body: {
        query: { match: { name: query } },
        _source: ['id', 'name', 'price'], // 必要なフィールドのみ
        size: 20,
      },
    });
  }

  // filter context活用
  async searchWithFilter(query: string, category: string): Promise<any> {
    return await client.search({
      index: 'products',
      body: {
        query: {
          bool: {
            must: [{ match: { name: query } }], // query context (スコアリング)
            filter: [{ term: { category } }],    // filter context (キャッシュ可能)
          },
        },
      },
    });
  }

  // クエリプロファイリング
  async profileQuery(query: any): Promise<any> {
    return await client.search({
      index: 'products',
      body: {
        profile: true,
        ...query,
      },
    });
  }
}
```

---

## 多言語検索 / Multilingual Search

### 多言語アナライザー / Multilingual Analyzers

```yaml
multilingual_search:
  strategies:
    separate_fields:
      description: "言語ごとに別フィールド"
      example:
        title_en: "English analyzer"
        title_ja: "Japanese analyzer"
        title_zh: "Chinese analyzer"
    
    language_detection:
      description: "自動言語検出"
      tools: ["langdetect", "AWS Comprehend"]
    
    universal_analyzer:
      description: "汎用アナライザー"
      use_case: "複数言語混在コンテンツ"
```

**多言語マッピング例**:

```json
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "fields": {
          "en": {
            "type": "text",
            "analyzer": "english"
          },
          "ja": {
            "type": "text",
            "analyzer": "kuromoji"
          },
          "zh": {
            "type": "text",
            "analyzer": "smartcn"
          },
          "ko": {
            "type": "text",
            "analyzer": "nori"
          }
        }
      }
    }
  }
}
```

```typescript
export async function multilingualSearch(query: string, language: string): Promise<any> {
  const fieldMap: Record<string, string> = {
    en: 'title.en',
    ja: 'title.ja',
    zh: 'title.zh',
    ko: 'title.ko',
  };

  const searchField = fieldMap[language] || 'title';

  return await client.search({
    index: 'products',
    body: {
      query: {
        match: {
          [searchField]: query,
        },
      },
    },
  });
}
```

---

## 検索分析 / Search Analytics

### 検索メトリクス / Search Metrics

```yaml
analytics:
  key_metrics:
    - "検索クエリ数"
    - "ゼロ結果クエリ率"
    - "クリックスルー率 (CTR)"
    - "平均レスポンスタイム"
    - "検索結果品質スコア"
  
  tracking:
    - "人気検索キーワード"
    - "検索結果クリック"
    - "検索後のコンバージョン"
    - "検索離脱率"
```

**検索ログ実装**:

```typescript
export class SearchAnalytics {
  async logSearch(userId: string, query: string, results: any): Promise<void> {
    const log = {
      timestamp: new Date(),
      userId,
      query,
      resultCount: results.total.value,
      hasResults: results.total.value > 0,
      responseTime: results.took,
      topResults: results.hits.slice(0, 3).map((hit: any) => hit._id),
    };

    // Elasticsearchにログ保存
    await client.index({
      index: 'search_logs',
      document: log,
    });

    // ゼロ結果の場合はアラート
    if (!log.hasResults) {
      await this.alertZeroResults(query);
    }
  }

  async logClick(userId: string, query: string, resultId: string, position: number): Promise<void> {
    await client.index({
      index: 'search_clicks',
      document: {
        timestamp: new Date(),
        userId,
        query,
        resultId,
        position,
      },
    });
  }

  private async alertZeroResults(query: string): Promise<void> {
    // SNS/Slackに通知
    console.log(`Zero results for query: ${query}`);
  }

  async getPopularQueries(days: number = 7): Promise<any> {
    return await client.search({
      index: 'search_logs',
      body: {
        query: {
          range: {
            timestamp: {
              gte: `now-${days}d/d`,
            },
          },
        },
        aggs: {
          popular_queries: {
            terms: {
              field: 'query.keyword',
              size: 100,
            },
          },
        },
        size: 0,
      },
    });
  }
}
```

---

## パフォーマンスチューニング / Performance Tuning

### スケーリング戦略 / Scaling Strategy

```yaml
scaling:
  vertical:
    description: "インスタンスサイズ拡大"
    use_case: "メモリ不足、CPU不足"
  
  horizontal:
    description: "ノード追加"
    use_case: "検索スループット向上"
    considerations:
      - "シャード数の再配分"
      - "レプリカ数の調整"
  
  shard_strategy:
    size: "10GB - 50GB / shard"
    count: "ノード数の倍数"
```

### モニタリング / Monitoring

```yaml
monitoring:
  cluster_health:
    - "ステータス (green/yellow/red)"
    - "ノード数"
    - "アクティブシャード数"
  
  performance_metrics:
    - "検索レイテンシー"
    - "インデックスレイテンシー"
    - "クエリスループット"
    - "CPU使用率"
    - "メモリ使用率"
    - "ディスクI/O"
  
  alerts:
    - "クラスターステータスがyellow/red"
    - "検索レイテンシー > 500ms"
    - "メモリ使用率 > 85%"
    - "ディスク使用率 > 80%"
```

---

## セキュリティ / Security

### アクセス制御 / Access Control

```yaml
security:
  authentication:
    - "IAM認証 (AWS)"
    - "APIキー"
    - "Basic認証"
    - "SAML / OpenID Connect"
  
  authorization:
    - "Role-Based Access Control (RBAC)"
    - "Document-level security"
    - "Field-level security"
  
  encryption:
    at_rest: "AWS KMS"
    in_transit: "TLS 1.2+"
```

### データマスキング / Data Masking

```typescript
export async function searchWithMasking(query: string, userRole: string): Promise<any> {
  const response = await client.search({
    index: 'sensitive_data',
    body: {
      query: { match: { content: query } },
    },
  });

  // ロールに応じてデータマスキング
  return response.hits.hits.map((hit: any) => {
    if (userRole !== 'admin') {
      hit._source.email = this.maskEmail(hit._source.email);
      hit._source.phone = this.maskPhone(hit._source.phone);
    }
    return hit;
  });
}

private maskEmail(email: string): string {
  const [local, domain] = email.split('@');
  return `${local.substring(0, 2)}***@${domain}`;
}

private maskPhone(phone: string): string {
  return phone.replace(/(\d{3})-(\d{4})-(\d{4})/, '$1-****-$3');
}
```

---

## ベストプラクティス / Best Practices

### 検索UX / Search User Experience

1. **オートコンプリート**
   - 3文字以上で表示
   - 最大10件の候補

2. **検索サジェスト**
   - タイポ訂正
   - 同義語展開

3. **ファセット検索**
   - カテゴリ、価格帯、ブランド等
   - 選択数の表示

4. **検索結果**
   - ハイライト表示
   - ソート機能
   - ページネーション

5. **ゼロ結果対応**
   - 関連クエリ提案
   - 人気商品表示

### パフォーマンス / Performance

1. **インデックス設計**
   - 適切なシャード数
   - レプリカ配置

2. **クエリ最適化**
   - filter context活用
   - 不要なフィールド除外

3. **キャッシング**
   - クエリキャッシュ
   - 結果キャッシュ

4. **モニタリング**
   - スロークエリ検出
   - リソース監視

---

## 参考資料 / References

### 公式ドキュメント / Official Documentation

- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Amazon OpenSearch Documentation](https://docs.aws.amazon.com/opensearch-service/)
- [Algolia Documentation](https://www.algolia.com/doc/)
- [pgvector Documentation](https://github.com/pgvector/pgvector)

### ベストプラクティス / Best Practices

- [Elasticsearch: The Definitive Guide](https://www.elastic.co/guide/en/elasticsearch/guide/current/index.html)
- [Search UI Patterns](https://ui-patterns.com/patterns/search)
- [Vector Search Best Practices](https://www.pinecone.io/learn/vector-search/)

---

## バージョン履歴 / Version History

| バージョン | 日付 | 変更内容 | 承認者 |
|----------|------|---------|--------|
| 2.0.0 | 2025-01-15 | 検索技術スタック標準策定、ベクトル検索追加 | Backend Architecture Team |
| 1.5.0 | 2024-12-01 | Algolia承認、pgvector追加 | CTO |
| 1.0.0 | 2024-10-01 | 初版リリース | Engineering Manager |

---

## 承認 / Approval

| 役割 | 氏名 | 承認日 | 署名 |
|------|------|--------|------|
| CTO | [Name] | 2025-01-15 | [Signature] |
| Backend Lead | [Name] | 2025-01-15 | [Signature] |
| Search Engineer | [Name] | 2025-01-15 | [Signature] |
| Data Scientist | [Name] | 2025-01-15 | [Signature] |

---

## お問い合わせ / Contact

**Backend Architecture Team**
- Email: backend-team@company.com
- Slack: #backend-architecture
- 担当者: [Tech Lead Name]

---
