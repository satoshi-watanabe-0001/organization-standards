# Organization Standards準拠 マルチステージDockerfile
# Java 17 + Spring Boot 3.2 アプリケーション用

# ====================================
# ステージ1: ビルドステージ
# ====================================
FROM eclipse-temurin:17-jdk as builder

# 作業ディレクトリ設定
WORKDIR /app

# Gradleラッパーと設定ファイルをコピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 実行権限付与
RUN chmod +x gradlew

# 依存関係の事前ダウンロード（キャッシュ活用）
RUN ./gradlew dependencies --no-daemon || true

# ソースコードをコピー
COPY src src
COPY config config

# アプリケーションのビルド（テストスキップ）
RUN ./gradlew bootJar --no-daemon -x test

# JARファイルの場所を確認
RUN ls -lh build/libs/

# ====================================
# ステージ2: 実行ステージ
# ====================================
FROM eclipse-temurin:17-jre-alpine

# メタデータ
LABEL maintainer="Organization Development Team"
LABEL description="Spring Boot API Template"
LABEL version="1.0.0"

# 非rootユーザーの作成（セキュリティベストプラクティス）
RUN addgroup -g 1000 appgroup && \
    adduser -D -u 1000 -G appgroup appuser

# 作業ディレクトリ設定
WORKDIR /app

# ビルドステージからJARファイルをコピー
COPY --from=builder /app/build/libs/*.jar app.jar

# 所有者を非rootユーザーに変更
RUN chown -R appuser:appgroup /app

# 非rootユーザーに切り替え
USER appuser

# ヘルスチェック設定（Spring Boot Actuator使用）
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# ポート公開
EXPOSE 8080

# JVMオプション設定
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Spring Bootプロファイル（デフォルトはprod）
ENV SPRING_PROFILES_ACTIVE=prod

# アプリケーション起動
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
