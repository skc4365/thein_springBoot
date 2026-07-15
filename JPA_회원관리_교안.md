# [교안] Spring Boot 3 + JPA 회원 관리 시스템 구축 가이드

본 교안은 **JDK 21**, **Spring Boot 3.3.x**, **Gradle**, **PostgreSQL** 환경에서 **Spring Data JPA**를 활용하여 표준 회원 관리(CRUD) 기능을 구현하는 흐름을 설명합니다. 

이 과정을 통해 JPA의 영속성 컨텍스트 동작 원리, 변경 감지(Dirty Checking), 소프트 딜리트(Soft Delete) 구현 기법 및 DTO 설계 등의 현대적인 백엔드 설계 프랙티스를 학습합니다.

---

## 1. 학습 목표
- Spring Boot 3 환경에서 PostgreSQL 데이터베이스와의 JPA 연동 방식을 이해합니다.
- JPA Entity 설계 기법(식별자 전략, 공통 Auditing, Enum 매핑)을 실습합니다.
- 데이터 수정 시 영속성 컨텍스트의 **변경 감지(Dirty Checking)** 메커니즘을 경험합니다.
- API 통신 시 엔티티 대신 **DTO(Data Transfer Object)**를 왜 사용해야 하는지 배우고 구현합니다.
- JUnit 5 테스트 작성을 통해 비즈니스 로직 및 JPA 쿼리 로그를 직접 검증합니다.

---

## 2. 실습 프로젝트 구조

```text
jpa-member-project/
├── build.gradle
├── settings.gradle
└── src/
    ├── main/
    │   ├── java/com/example/jpa/member/
    │   │   ├── JpaMemberApplication.java      # 메인 실행 클래스 (@EnableJpaAuditing)
    │   │   ├── controller/
    │   │   │   └── MemberController.java      # REST API 컨트롤러
    │   │   ├── domain/
    │   │   │   ├── BaseEntity.java            # 등록일, 수정일 공통 엔티티
    │   │   │   ├── Member.java                # 회원 핵심 엔티티
    │   │   │   ├── MemberRole.java            # 권한 Enum (USER, ADMIN)
    │   │   │   └── MemberStatus.java          # 회원 상태 Enum (ACTIVE, DELETED)
    │   │   ├── dto/
    │   │   │   ├── MemberCreateRequest.java   # 가입 요청 DTO (검증 포함)
    │   │   │   ├── MemberUpdateRequest.java   # 수정 요청 DTO
    │   │   │   └── MemberResponse.java        # API 응답 DTO
    │   │   ├── exception/
    │   │   │   ├── ErrorCode.java             # 커스텀 에러 정의 Enum
    │   │   │   ├── CustomException.java       # 커스텀 예외 클래스
    │   │   │   ├── ErrorResponse.java         # API 에러 반환 DTO
    │   │   │   └── GlobalExceptionHandler.java# 예외 일괄 제어 컨트롤러 어드바이스
    │   │   ├── repository/
    │   │   │   └── MemberRepository.java      # Spring Data JPA 리포지토리
    │   │   └── service/
    │   │       └── MemberService.java         # 비즈니스 로직 레이어 (@Transactional)
    │   └── resources/
    │       └── application.yml                # 운영 환경 데이터베이스 및 JPA 설정
    └── test/
        ├── java/com/example/jpa/member/
        │   └── service/
        │       └── MemberServiceTest.java     # 비즈니스 로직 JUnit 테스트
        └── resources/
            └── application.yml                # 테스트 전용 H2 데이터베이스 설정
```

---

## 3. 개발 환경 설정

### 3.1 `build.gradle` 구성
의존성 설정 시 최신 **Spring Boot 3.5.16** 및 **JDK 21** 호환을 유지하며, 데이터 검증(`validation`)과 성능 로깅을 위한 라이브러리를 포함합니다.

```groovy
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.5.16'
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JPA & DB
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'org.postgresql:postgresql'

    // Web & Validation
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Utility & Tooling
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Test (JUnit 5 & Mockito)
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    testImplementation 'com.h2database:h2' // 로컬 테스트용 인메모리 DB
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### 3.2 데이터베이스 연결 설정 (`application.yml`)
로컬 환경에 가동 중인 **PostgreSQL** 인스턴스와 연동하고, Hibernate가 생성하는 DDL 쿼리 및 실행 SQL을 포맷에 맞추어 콘솔에 상세 출력하도록 로깅 레벨을 세팅합니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: '1234'
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update # 엔티티 변화를 테이블 구조에 자동으로 누적 반영
    show-sql: true     # System.out 표준 출력으로 SQL 표시
    properties:
      hibernate:
        format_sql: true          # SQL 들여쓰기 가독성 활성화
        highlight_sql: true       # 콘솔 SQL에 컬러 강조 적용 (개발 편의성)
        default_batch_fetch_size: 100 # N+1 문제를 방지하기 위해 batch fetch 활성화
        use_sql_comments: true    # 주석에 어떤 JPQL 혹은 메서드가 실행되었는지 로깅

logging:
  level:
    org:
      hibernate:
        SQL: DEBUG               # JPA 생성 SQL 전체 로깅
        orm:
          jdbc:
            bind: TRACE          # [Hibernate 6] ? 바인딩된 실제 파라미터 값 표시
```

---

## 4. JPA 핵심 디자인 패러다임 이해

### 4.1 영속성 컨텍스트 (Persistence Context)
영속성 컨텍스트는 "엔티티를 영구 저장하는 환경"을 뜻하며, 애플리케이션과 데이터베이스 사이에서 캐시 및 쿼리 제어를 담당하는 가상의 계층입니다.
* **1차 캐시**: 하나의 트랜잭션 내에서 조회 시, DB를 먼저 찌르지 않고 1차 캐시에 올려둔 객체를 재사용하여 효율성을 극대화합니다.
* **쓰기 지연 (Transactional Write-Behind)**: `save()` 실행 시 즉시 Insert SQL을 DB에 쏘지 않고, 쓰기 지연 SQL 저장소에 쌓아 두었다가 트랜잭션이 **커밋(commit)되는 시점**에 배치 형태로 전송합니다.
* **변경 감지 (Dirty Checking)**: 엔티티를 영속화(조회 혹은 등록)하면 그 당시의 상태 스냅샷을 1차 캐시에 남겨둡니다. 트랜잭션 종료 시점에 원본 스냅샷과 현재 엔티티의 차이점을 파악해 변경 사항이 발견되면 **자동으로 Update SQL을 DB에 발행**합니다. 따라서 별도의 `update()` 혹은 `save()` 메서드를 수동 호출할 필요가 없습니다.

### 4.2 API 스펙과 Entity의 분리 (DTO)
JPA 개발에서 가장 흔히 벌어지는 실수 중 하나는 컨트롤러가 엔티티(`Member.java`)를 클라이언트에 그대로 노출하거나 파라미터로 직접 내려받는 일입니다.
* **이유 1. 엔티티 변경 시 API 스펙 깨짐**: 엔티티의 필드명 변경이 클라이언트 API JSON 키 값의 강제 변경으로 직접 이어져 시스템 불안정을 유발합니다.
* **이유 2. 불필요한 정보 유출**: 비밀번호, 결제 정보 등 보안상 외부 노출을 제한해야 할 컬럼들이 노출될 우려가 존재합니다.
* **이유 3. 무한 루프 발생**: 양방향 매핑(예: Member <-> Order) 설정 시 직렬화(Serialization) 과정에서 순환 참조로 인해 StackOverflowError가 터집니다.
* **대결책**: 화면 및 통신 목적에 알맞은 Request DTO, Response DTO 클래스를 무조건 분리 설계하는 아키텍처적 습관을 갖추어야 합니다.

---

## 5. 단계별 코드 구현 및 설명

### 5.1 공통 Auditing을 위한 `BaseEntity`
모든 테이블에 공통적으로 들어가는 생성 시간 및 최종 수정 시간을 일괄 관리하기 위해 `@MappedSuperclass`를 선언한 추상 클래스를 구축합니다.

```java
package com.example.jpa.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 엔티티가 해당 클래스를 상속할 경우, 속성들을 테이블 매핑 정보로 포함시킵니다.
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간을 감지하는 이벤트 리스너를 결합합니다.
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt; // 최초 등록일시

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 최종 수정일시
}
```

### 5.2 회원 엔티티 (`Member.java`)
데이터 무결성을 보호하기 위해 기본 생성자는 `PROTECTED`로 접근을 통제하고, 무분별한 Setter의 배제 대신 의미 있는 이름의 비즈니스 메서드로 도메인의 행동을 정의합니다.

```java
package com.example.jpa.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙 준수 및 무분별한 객체 생성을 막기 위해 protected 사용
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL의 SERIAL/IDENTITY 규칙을 따릅니다.
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING) // 숫자로 기록되는 ORDINAL은 순서 변경 시 치명적인 버그가 생기므로 STRING을 고정 사용합니다.
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Builder
    public Member(String email, String name, MemberRole role, MemberStatus status) {
        this.email = email;
        this.name = name;
        this.role = role != null ? role : MemberRole.USER;
        this.status = status != null ? status : MemberStatus.ACTIVE;
    }

    // --- 엔티티 내부 비즈니스 로직 (JPA Dirty Checking 대상) ---
    
    public void update(String name, MemberRole role) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (role != null) {
            this.role = role;
        }
    }

    public void withdraw() {
        this.status = MemberStatus.DELETED; // 소프트 딜리트 상태 처리
    }
}
```

### 5.3 서비스 계층 (`MemberService.java`)
비즈니스 트랜잭션 단위를 묶는 중요한 역할을 하는 클래스입니다. 트랜잭션 어노테이션(`@Transactional`)의 사용 방식을 면밀히 고찰합니다.

```java
package com.example.jpa.member.service;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberStatus;
import com.example.jpa.member.dto.MemberCreateRequest;
import com.example.jpa.member.dto.MemberResponse;
import com.example.jpa.member.dto.MemberUpdateRequest;
import com.example.jpa.member.exception.CustomException;
import com.example.jpa.member.exception.ErrorCode;
import com.example.jpa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // 1. 기본 설정을 읽기 전용으로 두어 성능 최적화 및 더티체킹 감지 리소스 절약
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입 (Insert)
     */
    @Transactional // 2. 등록/수정/삭제 등 상태 변조 기능엔 반드시 쓰기 트랜잭션 명시
    public Long join(MemberCreateRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = request.toEntity();
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    /**
     * 회원 단건 조회
     */
    public MemberResponse findMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return MemberResponse.from(member);
    }

    /**
     * 회원 전체 조회
     */
    public List<MemberResponse> findAllMembers() {
        return memberRepository.findAll().stream()
                .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 회원 정보 수정
     * 중요: Dirty Checking에 의해 member.update()만 호출해도 트랜잭션 종료 시 UPDATE 쿼리가 날아갑니다.
     */
    @Transactional
    public void updateMember(Long id, MemberUpdateRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 별도의 memberRepository.save() 호출 없음! 영속화된 객체의 데이터 수정 시 더티체킹이 자동으로 발생함.
        member.update(request.getName(), request.getRole());
    }

    /**
     * 회원 탈퇴 (소프트 딜리트)
     */
    @Transactional
    public void withdrawMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        member.withdraw(); // 엔티티 상태를 DELETED로 변경하여 소프트 딜리트 구현
    }
}
```

---

## 6. API 테스트 가이드
프로젝트 구동 후 Postman, Insomnia 혹은 cURL을 사용하여 REST API 동작 상태를 테스트합니다.

### 6.1 회원 가입
* **Endpoint**: `POST http://localhost:8080/api/members`
* **Content-Type**: `application/json`
* **Request Body**:
  ```json
  {
    "email": "student@example.com",
    "name": "홍길동",
    "role": "USER"
  }
  ```
* **Response (Created)**: `1` (생성된 회원의 PK 아이디값 반환)

### 6.2 회원 조회
* **Endpoint**: `GET http://localhost:8080/api/members/1`
* **Response (OK)**:
  ```json
  {
    "id": 1,
    "email": "student@example.com",
    "name": "홍길동",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2026-07-10T10:45:00",
    "updatedAt": "2026-07-10T10:45:00"
  }
  ```

### 6.3 회원 정보 수정 (Dirty Checking)
* **Endpoint**: `PUT http://localhost:8080/api/members/1`
* **Request Body**:
  ```json
  {
    "name": "홍길동수정",
    "role": "ADMIN"
  }
  ```
* **Response (OK)**: 빈 Body

### 6.4 회원 탈퇴 (Soft Delete)
* **Endpoint**: `DELETE http://localhost:8080/api/members/1`
* **Response (No Content)**: 빈 Body
* **검증**: 탈퇴 이후 회원 조회(`GET /api/members/1`)를 보낼 경우 `404 Not Found`와 함께 `M-001: 존재하지 않는 회원입니다.` 표준 에러 응답을 수신하게 됩니다.

---

## 7. 주요 학습 질문 (Quiz)
1. **Q. 왜 JPA 수정 프로세스에는 repository.save() 메서드를 컨트롤러/서비스에서 명시적으로 호출할 필요가 없을까요?**
   * **A**: 스프링의 `@Transactional` 범위 내에서 엔티티가 조회되면, 이 엔티티는 영속성 컨텍스트 내에 1차 캐시되고 원본 상태(스냅샷)가 기억됩니다. 메서드가 종료되어 트랜잭션이 커밋될 때 Hibernate는 스냅샷과 수정된 엔티티의 차이를 체크하여 바뀐 데이터가 있다면 UPDATE 쿼리를 임시 저장소에 생성하고 데이터베이스로 플러시(Flush)하기 때문입니다.
2. **Q. `@Enumerated(EnumType.ORDINAL)`을 현업 프로젝트에서 금지하고 `@Enumerated(EnumType.STRING)`을 강제해야 하는 이유는 무엇일까요?**
   * **A**: ORDINAL을 쓸 경우 DB에는 Enum의 순서 정수값(0, 1, 2...)이 기록됩니다. 이후 신규 상태나 역할이 Enum 클래스 중간에 신설되어 순서가 뒤틀리면, 이미 DB에 들어간 이전 정수값 데이터들의 논리적 의미가 왜곡되는 극도로 위험한 데이터 정합성 장애가 발생합니다. STRING 방식을 사용하여 DB에 "USER", "ADMIN" 문자열 자체를 보존해야 복구 및 순서 변경 시 안전합니다.

---

## 8. 트러블슈팅 가이드
* **윈도우(Windows) CLI 빌드 시 `ClassNotFoundException` 문제**:
  * **원인**: 프로젝트 경로 내에 한글 디렉토리명(`교안` 등)이 포함된 경우, 윈도우 환경에서 JVM/Gradle이 클래스패스 목록을 파싱하는 과정에서 인코딩 불일치로 컴파일된 테스트 클래스를 찾지 못하고 `ClassNotFoundException`을 일으킬 수 있습니다.
  * **해결방법**:
    1. 프로젝트 폴더를 순수 영문 경로(예: `C:\workspace\jpa-member-project`)로 복사/이동시킨 뒤 CLI 빌드(`.\gradlew test`)를 수행합니다.
    2. IntelliJ IDEA, Eclipse/STS 등의 IDE에서 Gradle Project로 Import한 후, IDE 내부 컴파일러(Build/Run by IDE)를 사용해 구동 또는 테스트를 가동하면 한글 경로 인코딩 문제 없이 정상적으로 컴파일 및 실행이 완료됩니다.

