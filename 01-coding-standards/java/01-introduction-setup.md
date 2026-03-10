# Java Coding Standards - Introduction & Setup

**説明**: 本規約の目的、基本設定、ツールチェーン、開発環境のセットアップ

**主要トピック**:
- 規約の目的とスコープ
- 推奨ツールチェーン（Java 17+, Spring Boot 3.0+）
- Gradle設定
- Checkstyle、SpotBugs、JaCoCo設定

---

# Java コーディング規約
## Java Coding Standards

**最終更新日**: 2024-10-15  
**バージョン**: 1.0.0  
**対象**: Java 17+, Spring Boot 3.0+, Maven/Gradle  
**適用範囲**: エンタープライズアプリケーション・マイクロサービス・バックエンドAPI

## 目的

このドキュメントは、Javaプロジェクトにおける具体的なコーディング規約を定義し、エンタープライズ環境での保守性・拡張性・セキュリティを重視した高品質なコードを実現します。共通原則については`00-general-principles.md`を参照してください。

---


## 1. 基本設定・ツール設定

### 1.1 推奨ツールチェーン

#### **必須ツール**
```xml
<!-- Maven依存関係（pom.xml） -->
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.1.0</spring-boot.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.target>
</properties>

<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### **Gradle設定（build.gradle）**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.0'
    id 'io.spring.dependency-management' version '1.1.0'
    id 'checkstyle'
    id 'jacoco'
    id 'org.sonarqube' version '4.0.0.2929'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.mockito:mockito-junit-jupiter'
}
```

#### **Checkstyle設定（checkstyle.xml）**
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="severity" value="error"/>
    <property name="fileExtensions" value="java, properties, xml"/>
    
    <!-- ファイルレベルチェック -->
    <module name="FileTabCharacter">
        <property name="eachLine" value="true"/>
    </module>
    
    <module name="TreeWalker">
        <!-- 命名規則 -->
        <module name="ConstantName"/>
        <module name="LocalFinalVariableName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="MethodName"/>
        <module name="PackageName"/>
        <module name="ParameterName"/>
        <module name="StaticVariableName"/>
        <module name="TypeName"/>
        
        <!-- インポート -->
        <module name="AvoidStarImport"/>
        <module name="IllegalImport"/>
        <module name="RedundantImport"/>
        <module name="UnusedImports"/>
        
        <!-- サイズ制限 -->
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <module name="MethodLength">
            <property name="max" value="50"/>
        </module>
        <module name="ParameterNumber">
            <property name="max" value="5"/>
        </module>
        
        <!-- 空白・フォーマット -->
        <module name="EmptyForIteratorPad"/>
        <module name="GenericWhitespace"/>
        <module name="MethodParamPad"/>
        <module name="NoWhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="OperatorWrap"/>
        <module name="ParenPad"/>
        <module name="TypecastParenPad"/>
        <module name="WhitespaceAfter"/>
        <module name="WhitespaceAround"/>
        
        <!-- 修飾子 -->
        <module name="ModifierOrder"/>
        <module name="RedundantModifier"/>
        
        <!-- ブロック -->
        <module name="AvoidNestedBlocks"/>
        <module name="EmptyBlock"/>
        <module name="LeftCurly"/>
        <module name="NeedBraces"/>
        <module name="RightCurly"/>
        
        <!-- 一般的なコーディング問題 -->
        <module name="EmptyStatement"/>
        <module name="EqualsHashCode"/>
        <module name="HiddenField"/>
        <module name="IllegalInstantiation"/>
        <module name="InnerAssignment"/>
        <module name="MagicNumber"/>
        <module name="MissingSwitchDefault"/>
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>
        
        <!-- クラス設計 -->
        <module name="DesignForExtension"/>
        <module name="FinalClass"/>
        <module name="HideUtilityClassConstructor"/>
        <module name="InterfaceIsType"/>
        <module name="VisibilityModifier"/>
    </module>
</module>
```

#### **JaCoCo設定（Gradleに追加）**
```gradle
jacoco {
    toolVersion = "0.8.8"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80%以上のカバレッジを要求
            }
        }
    }
}
```

---


