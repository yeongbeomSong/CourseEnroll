# 수강신청 시스템

대학 수강신청을 위한 풀스택 웹 애플리케이션입니다.  
학생·교수·관리자 역할에 따라 강의 조회/신청, 강의 관리, 학과·사용자 관리 기능을 제공합니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Frontend** | React 19, Vite 7, React Router, React Query, Tailwind CSS, Lucide React |
| **Backend** | Java 21, Spring Boot 3.4, Spring Security, JPA, JWT |
| **DB** | H2 (파일 DB, 개발용) |
| **대기열** | Redis (수강신청 정원 마감 시 대기열 등록·자동 배정) |

---

## 프로젝트 구조

```
project/
├── frontend/          # React (Vite) 프론트엔드
│   ├── src/
│   │   ├── components/   # 레이아웃, ProtectedRoute 등
│   │   ├── contexts/     # AuthContext
│   │   ├── lib/          # API 클라이언트
│   │   └── pages/        # common, student, professor, admin 페이지
│   └── package.json
├── backend/           # Spring Boot 백엔드
│   ├── src/main/java/org/example/backend/
│   │   ├── domain/       # user, course, department, registration 등
│   │   └── global/       # config, jwt, exception
│   └── build.gradle
├── .env.example       # 환경 변수 예시
├── .gitignore
└── README.md
```

---

## 사전 요구 사항

- **Node.js** 18+ (프론트엔드)
- **Java** 21 (백엔드)
- **Gradle** (또는 JDK에 포함된 wrapper 사용)
- **Redis** (대기열 기능 사용 시, 기본 localhost:6379)

---

## 환경 설정

### 1. 프론트엔드

`frontend` 폴더에 `.env` 파일을 만들고 백엔드 API 주소를 넣습니다.

```bash
# frontend/.env
VITE_API_URL=http://localhost:8080/api
```

백엔드가 다른 주소/포트라면 위 값을 수정하면 됩니다.

### 2. 백엔드

H2 DB와 JWT 설정은 `backend/src/main/resources/application.properties`에 있습니다.  
로컬에서만 바꾸고 싶다면 `application-local.properties`를 만들고 `.gitignore`에 포함되어 있으므로 커밋되지 않습니다.

---

## 실행 방법

### 백엔드 실행

```bash
cd backend
./gradlew bootRun
# Windows: gradlew.bat bootRun
```

- 서버: **http://localhost:8080**
- H2 콘솔: **http://localhost:8080/h2-console**  
  (JDBC URL: `jdbc:h2:file:./data/coursedb`, 사용자: `sa`, 비밀번호: 비움)

### 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

- 접속: **http://localhost:5173**

---

## 테스트 계정 (시드 데이터)

모든 계정 비밀번호: **`password`**

| 역할 | 로그인 ID | 비고 |
|------|-----------|------|
| 관리자 | `admin` | 학과/사용자/강의 모니터링 |
| 교수 | `prof01` ~ `prof50` | 강의 등록·수정·수강생 명단 |
| 학생 | `s00001` ~ `s00600` | 강의 목록·신청·시간표 |

---

## 주요 기능

- **공통**: 로그인, 회원가입(학생/교수), 403/404 페이지
- **학생**: 강의 목록·필터·신청, **정원 마감 시 대기열 신청**, 대기 순번 조회·포기, 내 수강 시간표, 신청 취소, 마이페이지
- **교수**: 내 강의 대시보드, 강의 등록/수정, 수강생 명단 조회
- **관리자**: 학과 CRUD, 사용자 검색·삭제, 전체 강의 모니터링

---

## 빌드

```bash
# 프론트엔드 (정적 파일 생성)
cd frontend && npm run build

# 백엔드 (JAR 생성)
cd backend && ./gradlew build
```

---

## 라이선스

이 프로젝트는 수강/포트폴리오용으로 작성되었습니다.
