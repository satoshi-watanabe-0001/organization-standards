package com.organization.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Boot アプリケーションのメインクラス.
 * 
 * <p>このクラスはアプリケーションのエントリーポイントとして機能し、
 * Spring Bootの自動設定を有効化します。
 * 
 * <p>主な機能:
 * <ul>
 *   <li>Spring Boot自動設定の有効化</li>
 *   <li>JPA監査機能の有効化（作成日時、更新日時の自動記録）</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableJpaAuditing  // JPA監査機能を有効化（BaseEntityの自動フィールド更新のため）
public class Application {

    /**
     * アプリケーションのエントリーポイント.
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
