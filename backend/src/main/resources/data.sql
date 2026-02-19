-- ============================================================
-- 시드 데이터 (모든 계정 테스트 비밀번호: password)
-- ============================================================
-- BCrypt hash for "password"

-- 1) 관리자 1명
INSERT INTO users (username, password, name, role) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '시스템관리자', 'ROLE_ADMIN');

-- 2) 교수 50명 (username: prof01~prof50)
INSERT INTO users (username, password, name, role)
SELECT 'prof' || LPAD(CAST(X AS VARCHAR), 2, '0'), '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '교수' || LPAD(CAST(X AS VARCHAR), 2, '0'), 'ROLE_PROFESSOR'
FROM SYSTEM_RANGE(1, 50);

-- 3) 학생 600명 (username: s00001 ~ s00600)
INSERT INTO users (username, password, name, role)
SELECT 's' || LPAD(CAST(X AS VARCHAR), 5, '0'), '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '학생' || LPAD(CAST(X AS VARCHAR), 4, '0'), 'ROLE_STUDENT'
FROM SYSTEM_RANGE(1, 600);

-- 4) 학과 20개
INSERT INTO departments (name, dept_code, college) VALUES ('컴퓨터공학과', 'CS', '공과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('소프트웨어학과', 'SW', '공과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('전자공학과', 'EE', '공과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('기계공학과', 'ME', '공과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('산업공학과', 'IE', '공과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('경영학과', 'BA', '경상대학');
INSERT INTO departments (name, dept_code, college) VALUES ('경제학과', 'EC', '경상대학');
INSERT INTO departments (name, dept_code, college) VALUES ('국제통상학과', 'IT', '경상대학');
INSERT INTO departments (name, dept_code, college) VALUES ('법학과', 'LA', '법과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('행정학과', 'PA', '사회과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('심리학과', 'PS', '사회과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('국어국문학과', 'KL', '인문대학');
INSERT INTO departments (name, dept_code, college) VALUES ('영어영문학과', 'EL', '인문대학');
INSERT INTO departments (name, dept_code, college) VALUES ('수학과', 'MA', '자연과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('물리학과', 'PH', '자연과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('화학과', 'CH', '자연과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('생명과학과', 'LS', '자연과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('건축학과', 'AR', '공과대학');
INSERT INTO departments (name, dept_code, college) VALUES ('미디어커뮤니케이션학과', 'MC', '사회과학대학');
INSERT INTO departments (name, dept_code, college) VALUES ('간호학과', 'NU', '보건대학');

-- 5) 교수 프로필 50명 (user_id 2~51, department_id 1~20 순환)
INSERT INTO professors (user_id, department_id, professor_code, position)
SELECT 1 + X, (X % 20) + 1, 'P' || LPAD(CAST(X + 1 AS VARCHAR), 3, '0'), '교수'
FROM SYSTEM_RANGE(0, 49);

-- 6) 학생 프로필 600명 (user_id 52~651, department_id 1~20 순환, 학번 20240001~20240600)
INSERT INTO students (user_id, department_id, student_number, grade, max_credits, current_credits)
SELECT 51 + X, (X % 20) + 1, '2024' || LPAD(CAST(X AS VARCHAR), 4, '0'), (X % 4) + 1, 21, 0
FROM SYSTEM_RANGE(1, 600);

-- 7) 강의 180개 (교수 1~50, 학과 1~20, 이수구분/학점/정원/대상학년/시간 다양)
INSERT INTO courses (professor_id, department_id, course_code, title, category, credit, capacity, current_enrollment, target_grade, schedule)
SELECT
    (X % 50) + 1,
    (X % 20) + 1,
    'C' || LPAD(CAST(X + 1 AS VARCHAR), 3, '0'),
    '강의' || (X + 1),
    CASE (X % 3) WHEN 0 THEN 'MAJOR_REQUIRED' WHEN 1 THEN 'MAJOR_SELECT' ELSE 'GENERAL' END,
    2 + (X % 2),
    30 + (X % 51),
    0,
    (X % 4) + 1,
    CASE (X % 10)
        WHEN 0 THEN '월 09:00-11:00'
        WHEN 1 THEN '화 10:00-12:00'
        WHEN 2 THEN '수 14:00-16:00'
        WHEN 3 THEN '목 15:00-17:00'
        WHEN 4 THEN '금 11:00-13:00'
        WHEN 5 THEN '월 13:00-15:00'
        WHEN 6 THEN '화 14:00-16:00'
        WHEN 7 THEN '수 10:00-12:00'
        WHEN 8 THEN '목 09:00-11:00'
        ELSE '금 14:00-16:00'
    END
FROM SYSTEM_RANGE(0, 179);

-- 8) 수강신청 3600건 (학생 1~600명이 각 6개 강의씩, course_id 1~180 분산)
INSERT INTO registrations (student_id, course_id, created_at)
SELECT 1 + (X / 6), 1 + (X % 180), CURRENT_TIMESTAMP
FROM SYSTEM_RANGE(0, 3599);

-- 9) 강의별 현재 수강 인원 반영
UPDATE courses c SET current_enrollment = (SELECT COUNT(*) FROM registrations r WHERE r.course_id = c.id);

-- 10) 학생별 현재 신청 학점 반영 (각 학생이 6과목, 과목당 2~3학점 가정)
UPDATE students s SET current_credits = (
    SELECT COALESCE(SUM(c.credit), 0)
    FROM registrations r
    JOIN courses c ON r.course_id = c.id
    WHERE r.student_id = s.id
);
