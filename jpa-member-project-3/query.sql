-- ==========================================
-- JPA 회원관리 실습용 PostgreSQL SQL 쿼리 모음
-- ==========================================

-- 1. 테이블 조회 (전체 및 회원 상태별)
-- 1.1 전체 회원 테이블 조회
SELECT * FROM members;

-- 1.2 활성화된(ACTIVE) 회원만 조회 (조회 API 및 서비스 기능 검증용)
SELECT * FROM members WHERE status = 'ACTIVE';

-- 1.3 탈퇴된(DELETED) 회원 조회 (소프트 딜리트 확인용)
SELECT * FROM members WHERE status = 'DELETED';

-- 2. 회원 등록 (INSERT)
-- JPA의 GenerationType.IDENTITY 전략에 따라 id(pk)는 serial로 자동 발급되므로 제외하고 삽입합니다.
-- created_at, updated_at은 JPA Auditing에 의해 자동으로 등록되지만, SQL 수동 검증용으로 기록해 둡니다.
INSERT INTO members (email, name, role, status, created_at, updated_at) 
VALUES ('sql_test@example.com', 'SQL테스터', 'USER', 'ACTIVE', NOW(), NOW());

-- 3. 회원 단건 조회
SELECT * FROM members WHERE id = 1;

-- 4. 회원 정보 수정 (UPDATE - Dirty Checking 대체 수동 쿼리)
-- 특정 회원의 이름 및 역할을 수정합니다.
UPDATE members 
SET name = '이름수정본', role = 'ADMIN', updated_at = NOW() 
WHERE id = 1 AND status = 'ACTIVE';

-- 5. 회원 탈퇴 (소프트 딜리트 - Soft Delete)
-- 데이터를 물리적으로 DELETE하지 않고 status를 DELETED로 변경하여 보존합니다.
UPDATE members 
SET status = 'DELETED', updated_at = NOW() 
WHERE id = 1;

-- 6. 회원 물리 삭제 (하드 딜리트 - 필요시 사용)
DELETE FROM members WHERE id = 1;

-- 7. 테이블 초기화 (테스트용)
-- DROP TABLE members;
