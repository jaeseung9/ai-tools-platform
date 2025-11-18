🤖 AI Tools Platform
<p align="center">
  Spring Boot + OAuth2 기반 통합 AI 도구 플랫폼<br/>
  <b>Chat</b> · <b>Image Generation</b> · <b>Usage Dashboard</b>를 하나의 플랫폼에서
</p>

🗓 개발 기간
2024.11 (3주)

📚 목차

프로젝트 소개
데모
주요 기능
기술 스택
아키텍처
API 명세
데이터베이스 설계
실행 방법
개발 과정 & 트러블슈팅


1) 프로젝트 소개 <a id="about"></a>
포트폴리오 메인 프로젝트로 제작한 AI Tools Platform입니다.
네이버/카카오 소셜 로그인을 통해 여러 AI API(Google Gemini, Stability AI)를 하나의 플랫폼에서 사용할 수 있으며,
사용 통계 대시보드로 API 비용과 사용량을 실시간으로 추적할 수 있습니다.
🎯 개발 목표

여러 AI API를 통합한 원스톱 플랫폼 구축
실무 수준의 인증/인가 시스템 구현 (OAuth2)
API 비용 관리 및 사용량 추적 자동화
배포까지 완료하여 실제 서비스 가능한 수준 달성


2) 데모 <a id="demo"></a>
🌐 배포 URL: https://ai-tools-platform-7air.onrender.com
주요 화면

로그인: 네이버/카카오 소셜 로그인 + 이메일 회원가입/로그인
Chat: Gemini AI 대화 + 실시간 토큰 사용량 표시
Image Generator: Stability AI 이미지 생성 + Base64 즉시 표시
Dashboard: 월별 사용 통계 & 도구별 비용 분석


3) 🚀 주요 기능 <a id="features"></a>
기능설명소셜 로그인네이버/카카오 OAuth2 + 이메일 회원가입 통합 인증AI 채팅Google Gemini API 연동, 대화 기록 자동 저장, 토큰 추적이미지 생성Stability AI 텍스트→이미지 변환, Base64 즉시 표시사용 통계월별/도구별 사용량 집계, 실시간 비용 계산Rate Limiting일일 토큰 제한으로 비용 관리 (100 tokens/day)전역 예외 처리일관된 에러 응답 형식, 세분화된 예외 핸들링

4) 🧱 기술 스택 <a id="stack"></a>
Backend

Spring Boot 3.4.11 - 웹 애플리케이션 프레임워크
Spring Security + OAuth2 - 인증/인가 및 소셜 로그인
Spring Data JPA - ORM 및 데이터베이스 접근
PostgreSQL - 운영 데이터베이스

Frontend

HTML5 / CSS3 / Vanilla JavaScript - 순수 웹 기술

API Integration

Google Gemini API - AI 채팅
Stability AI - 이미지 생성

DevOps

Render - 클라우드 배포 플랫폼
Docker - 컨테이너화
GitHub - 버전 관리 및 자동 배포


5) 🏗️ 아키텍처 <a id="architecture"></a>
   <img width="508" height="775" alt="image" src="https://github.com/user-attachments/assets/766870a9-093e-40b7-b55b-84955012edd3" />

핵심 설계 원칙

Controller → Service → Repository 계층 분리
DTO 패턴으로 계층 간 데이터 전달
전역 예외 처리로 일관된 에러 응답
Filter 체인으로 횡단 관심사 처리 (인증, Rate Limiting)


6) 📡 API 명세 <a id="api"></a>
인증 API
MethodEndpoint설명POST/api/auth/signup이메일 회원가입POST/api/auth/login이메일 로그인GET/oauth2/authorization/naver네이버 로그인GET/oauth2/authorization/kakao카카오 로그인
채팅 API
MethodEndpoint설명POST/api/chat/message메시지 전송GET/api/chat/history대화 기록 조회GET/api/chat/remaining-tokens잔여 토큰 조회
이미지 API
MethodEndpoint설명POST/api/image/generate이미지 생성GET/api/image/history생성 기록 조회
대시보드 API
MethodEndpoint설명GET/api/dashboard/stats이번 달 통계GET/api/dashboard/usage-chart월별 차트 데이터GET/api/dashboard/cost-breakdown도구별 비용 비율

7) 🗄️ 데이터베이스 설계 <a id="database"></a>
ERD 구조
users
├─ id (PK)
├─ oauth_id (UNIQUE) - 소셜 로그인 식별자
├─ provider - naver/kakao/local
├─ email (UNIQUE)
├─ password - 일반 회원용
└─ created_at

chat_history
├─ id (PK)
├─ user_id (FK → users)
├─ user_message
├─ ai_response
├─ token_used
├─ estimated_cost
└─ created_at

image_history
├─ id (PK)
├─ user_id (FK → users)
├─ prompt
├─ image_url (Base64 Data URL)
├─ image_size
├─ estimated_cost
└─ created_at

api_usage_stats
├─ id (PK)
├─ user_id (FK → users)
├─ tool_type - CHAT/IMAGE/GRAMMAR
├─ usage_count
├─ total_cost
├─ year_month - "2024-11"
└─ UNIQUE(user_id, tool_type, year_month)
주요 인덱스
sql-- 사용자별 히스토리 조회 최적화
idx_chat_user_created (user_id, created_at DESC)
idx_image_user_created (user_id, created_at DESC)
idx_stats_user_month (user_id, year_month)

8) 💻 실행 방법 <a id="run"></a>
로컬 실행
bash# 1. 환경 변수 설정 (.env 파일 또는 시스템 환경 변수)
export DB_URL="jdbc:postgresql://localhost:5432/aitools"
export GEMINI_API_KEY="your-api-key"
export STABILITY_API_KEY="your-api-key"
export NAVER_CLIENT_ID="your-client-id"
export KAKAO_CLIENT_ID="your-client-id"

# 2. 빌드 및 실행
./gradlew bootRun
Docker 실행
bashdocker build -t ai-tools-platform .
docker run -p 8080:8080 \
  -e DB_URL="..." \
  -e GEMINI_API_KEY="..." \
  ai-tools-platform
필수 환경 변수
propertiesDB_URL                    # PostgreSQL 연결 URL
DB_USERNAME              # DB 사용자명
DB_PASSWORD              # DB 비밀번호
OAUTH_REDIRECT_BASE_URL  # OAuth2 리다이렉트 기본 URL
NAVER_CLIENT_ID          # 네이버 앱 클라이언트 ID
NAVER_CLIENT_SECRET      # 네이버 앱 시크릿
KAKAO_CLIENT_ID          # 카카오 REST API 키
KAKAO_CLIENT_SECRET      # 카카오 시크릿
GEMINI_API_KEY          # Google Gemini API 키
GEMINI_API_URL          # Gemini API 엔드포인트
STABILITY_API_KEY       # Stability AI API 키
STABILITY_API_URL       # Stability AI 엔드포인트

9) 🌱 개발 과정 & 트러블슈팅 <a id="insights"></a>
왜 이 프로젝트를 만들었나?
1. 실무 수준의 웹 개발 경험 필요
게임 개발(C++ WinAPI) 경험은 있었지만 웹 개발은 처음이었습니다. 실제 서비스처럼 동작하는 플랫폼을 만들고 싶었습니다.
2. AI API 통합 경험
ChatGPT, DALL-E 등 AI 도구를 자주 사용하면서 "이것들을 하나의 플랫폼에 모으면 어떨까?"라는 생각에서 시작했습니다.
3. 인증/인가 시스템 학습
JWT나 세션 같은 개념을 책으로만 보다가, 실제로 소셜 로그인을 구현하면서 Spring Security의 동작 원리를 깊이 이해하고 싶었습니다.

개발하면서 어려웠던 점
🔴 1. OAuth2 소셜 로그인 구현의 복잡성
문제 상황

네이버와 카카오의 응답 구조가 서로 달랐습니다
네이버는 response.id, 카카오는 id 형태로 사용자 정보 제공
로컬 개발과 배포 환경의 Redirect URI가 달라서 로그인이 실패

해결 과정

CustomOAuth2UserService에서 provider별로 분기 처리
extractOAuthId(), extractEmail() 메서드로 공통 인터페이스 추출
application.properties를 dev/prod로 분리하여 환경별 설정 관리

java// provider에 따라 다른 속성 추출
private String extractOAuthId(String provider, Map<String, Object> attributes) {
    if ("naver".equals(provider)) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return provider + "_" + response.get("id");
    } else if ("kakao".equals(provider)) {
        return provider + "_" + attributes.get("id");
    }
    throw new OAuth2AuthenticationException("Unsupported provider");
}
배운 점

OAuth2는 프로토콜은 같지만 각 Provider마다 구현이 다름
환경별 설정 분리의 중요성 (dev/prod 프로파일 활용)


🔴 2. Rate Limiting 동시성 문제
문제 상황

여러 요청이 동시에 들어올 때 토큰 카운터가 부정확하게 증가
한 사용자가 100개 제한인데 105개까지 요청 성공하는 현상

해결 과정

ConcurrentHashMap 사용으로 기본 Thread-Safe 보장
synchronized 키워드로 카운터 증가 부분 동기화

javapublic synchronized void addTokenUsage(String userIdentifier, int tokens) {
    String key = userIdentifier + "_" + LocalDate.now();
    dailyTokenUsage.merge(key, tokens, Integer::sum);
}

자정마다 자동 리셋 로직 추가

javaprivate synchronized void resetIfNewDay() {
    LocalDate today = LocalDate.now();
    if (!today.equals(lastResetDate)) {
        dailyTokenUsage.clear();
        lastResetDate = today;
    }
}
배운 점

동시성 제어의 중요성과 synchronized의 용도
향후 Redis를 사용한 분산 환경 대응 필요성 인식


🔴 3. JPA N+1 문제
문제 상황

히스토리 조회 시 사용자 정보를 매번 개별 쿼리로 조회
100개 히스토리 → 101번의 쿼리 발생 (1 + 100)

해결 과정

인덱스 추가로 조회 속도 개선

java@Index(name = "idx_chat_user_created", 
       columnList = "user_id, created_at DESC")

@ManyToOne(fetch = FetchType.LAZY)로 지연 로딩 설정
필요한 경우에만 사용자 정보 조회하도록 DTO 변환

배운 점

JPA의 즉시 로딩 vs 지연 로딩 차이
인덱스 설계의 중요성 (복합 인덱스 활용)


🔴 4. 배포 환경 PostgreSQL 마이그레이션
문제 상황

로컬에서는 수업에서 학스한 MySQL을 사용해서 개발했는데, Render 배포 시 PostgreSQL 사용 하면서
테이블 자동 생성은 되는데 UNIQUE 제약조건 문법 오류 발생

해결 과정

application.properties에서 DB 설정 환경 변수화

propertiesspring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}

PostgreSQL 예약어인 year_month 컬럼명을 백틱으로 감싸기

java@Column(name = "`year_month`")
private String yearMonth;

spring.jpa.hibernate.ddl-auto=update로 자동 스키마 관리

배운 점

DB마다 예약어와 문법이 다름 (이식성 고려)
환경 변수를 활용한 설정 외부화 중요성


기술적 성과
✅ 보안

BCrypt 비밀번호 암호화
OAuth2 표준 인증 프로토콜 준수
Rate Limiting으로 API 남용 방지

✅ 성능

인덱스 최적화로 히스토리 조회 속도 개선
지연 로딩으로 불필요한 쿼리 제거

✅ 유지보수성

계층 분리 (Controller/Service/Repository)
DTO 패턴으로 계층 간 의존성 분리
전역 예외 처리로 일관된 에러 응답

✅ 사용자 경험

실시간 토큰 사용량 표시
로딩 애니메이션
직관적인 UI 디자인


향후 개선 계획

Redis 기반 Rate Limiting: 분산 환경 지원
JWT 토큰 인증: 무상태 API 서버로 전환
테스트 코드 작성: JUnit 단위/통합 테스트
Chart.js 그래프: 대시보드 시각화 완성
다크모드: 테마 전환 기능 추가


📧 Contact
Author: 서재승 (Seo Jae Seung)
Email: seojaeseung9@gmail.com
Blog: https://seungcoding.tistory.com/
GitHub: https://github.com/jaeseung9

<p align="center">
  ⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요!
</p>
