<h1 align="center">🤖 AI Tools Platform</h1>
<p align="center">
  Spring Boot + OAuth2 기반 통합 AI 도구 플랫폼<br/>
  <b>Chat</b> · <b>Image Generation</b> · <b>Usage Dashboard</b>를 하나의 인터페이스로
</p>

---

## 🗓 개발 기간
> 2024.11 (3주)

---

## 📚 목차
- [프로젝트 소개](#프로젝트-소개)
- [데모](#데모)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [API 명세](#api-명세)
- [데이터베이스 설계](#데이터베이스-설계)
- [실행 방법](#실행-방법)
- [개발 과정 & 트러블슈팅](#개발-과정--트러블슈팅)
- [Contact](#contact)

---

## 🎯 프로젝트 소개

> 포트폴리오 메인 프로젝트로 제작한 **AI Tools Platform**입니다.  
> 네이버/카카오 소셜 로그인을 통해 여러 AI API(Google Gemini, Stability AI)를 하나의 플랫폼에서 사용할 수 있으며,  
> 사용 통계 대시보드를 통해 API 비용과 사용량을 실시간으로 추적할 수 있습니다.

### 💡 개발 목표
- 여러 AI API를 통합한 **원스톱 플랫폼 구축**
- 실무 수준의 인증/인가 시스템 구현 (OAuth2)
- API 모니터링 및 비용 추적 시스템 구현
- 클라우드 배포 완료 (Render)

---

## 🌐 데모

🔗 **배포 URL**  
👉 https://ai-tools-platform-7air.onrender.com  
<sub>⚠️ 첫 로딩 시 콜드 스타트로 인해 20~30초 지연될 수 있습니다.</sub>

---

## 🚀 주요 기능

<table>
  <tr>
    <th>기능</th>
    <th>설명</th>
  </tr>
  <tr>
    <td><b>소셜 로그인</b></td>
    <td>네이버/카카오 OAuth2 + 이메일 회원가입 통합 인증</td>
  </tr>
  <tr>
    <td><b>AI 채팅</b></td>
    <td>Google Gemini API 연동, 대화 기록 자동 저장, 토큰 추적</td>
  </tr>
  <tr>
    <td><b>이미지 생성</b></td>
    <td>Stability AI 텍스트 → 이미지 변환, Base64 즉시 표시</td>
  </tr>
  <tr>
    <td><b>사용 통계</b></td>
    <td>월별/도구별 사용량 집계, 실시간 비용 계산</td>
  </tr>
  <tr>
    <td><b>Rate Limiting</b></td>
    <td>일일 토큰 제한으로 API 비용 관리 (100 tokens/day)</td>
  </tr>
  <tr>
    <td><b>전역 예외 처리</b></td>
    <td>일관된 에러 응답 형식, 세분화된 예외 핸들링</td>
  </tr>
</table>

---

## 🧱 기술 스택

**Backend**  
- Spring Boot 3.4.1  
- Spring Security + OAuth2  
- Spring Data JPA  
- PostgreSQL  
- Lombok / RestTemplate  

**Frontend**  
- HTML5 / CSS3 / Vanilla JavaScript

**AI API Integration**  
- Google Gemini API  
- Stability AI

**DevOps**  
- Render – 클라우드 배포  
- Docker – 컨테이너화  
- GitHub – 버전 관리 및 자동 빌드  

---

## 🏗️ 아키텍처

<p align="center">
  <img src="https://github.com/user-attachments/assets/766870a9-093e-40b7-b55b-84955012edd3" width="500" alt="architecture diagram"/>
</p>

### 핵심 설계 원칙
- Controller → Service → Repository 계층 분리  
- DTO 패턴으로 계층 간 데이터 전달  
- Filter 체인으로 횡단 관심사 처리 (인증, Rate Limiting)  
- 전역 예외 처리(ExceptionHandler)로 일관된 에러 응답 관리  

---

## 📡 API 명세

### 🔐 인증 API
| Method | Endpoint                      | 설명 |
|--------|-------------------------------|------|
| POST   | `/api/auth/signup`            | 이메일 회원가입 |
| POST   | `/api/auth/login`             | 이메일 로그인 |
| GET    | `/oauth2/authorization/naver` | 네이버 로그인 |
| GET    | `/oauth2/authorization/kakao` | 카카오 로그인 |

### 💬 채팅 API
| Method | Endpoint                | 설명 |
|--------|-------------------------|------|
| POST   | `/api/chat/message`     | 메시지 전송 |
| GET    | `/api/chat/history`     | 대화 기록 조회 |
| GET    | `/api/chat/remaining-tokens` | 잔여 토큰 조회 |

### 🎨 이미지 API
| Method | Endpoint                | 설명 |
|--------|-------------------------|------|
| POST   | `/api/image/generate`   | 이미지 생성 |
| GET    | `/api/image/history`    | 생성 기록 조회 |

---

## 🗄️ 데이터베이스 설계

### ERD 구조

**users**
id (PK)
oauth_id (UNIQUE)
provider - naver/kakao/local
email (UNIQUE)
password
created_at

코드 복사

**chat_history**
id (PK)
user_id (FK → users)
user_message
ai_response
token_used
estimated_cost
created_at

코드 복사

**image_history**
id (PK)
user_id (FK → users)
prompt
image_url (Base64)
image_size
estimated_cost
created_at

markdown
코드 복사

**api_usage_stats**
id (PK)
user_id (FK → users)
tool_type - CHAT / IMAGE
usage_count
total_cost
year_month - "2024-11"
UNIQUE(user_id, tool_type, year_month)

bash
코드 복사

---

## 💻 실행 방법

### 로컬 실행
```bash
# 1. 환경 변수 설정
export DB_URL="jdbc:postgresql://localhost:5432/aitools"
export DB_USERNAME="your-username"
export DB_PASSWORD="your-password"
export GEMINI_API_KEY="your-key"
export STABILITY_API_KEY="your-key"

# 2. 빌드 & 실행
./gradlew bootRun
Docker 실행
bash
코드 복사
docker build -t ai-tools-platform .
docker run -p 8080:8080 \
  -e DB_URL="..." \
  -e GEMINI_API_KEY="..." \
  ai-tools-platform
🌱 개발 과정 & 트러블슈팅
🔴 OAuth2 소셜 로그인 Provider별 응답 구조 차이
java
코드 복사
private String extractOAuthId(String provider, Map<String, Object> attributes) {
    if ("naver".equals(provider)) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return provider + "_" + response.get("id");
    } else if ("kakao".equals(provider)) {
        return provider + "_" + attributes.get("id");
    }
    throw new OAuth2AuthenticationException("Unsupported provider");
}
🔴 Rate Limiting 동시성 처리
java
코드 복사
public synchronized void addTokenUsage(String userIdentifier, int tokens) {
    String key = userIdentifier + "_" + LocalDate.now();
    dailyTokenUsage.merge(key, tokens, Integer::sum);
}
🔴 JPA N+1 문제 해결
java
코드 복사
@ManyToOne(fetch = FetchType.LAZY)
private User user;
📧 Contact
서재승 (Seo Jae Seung)
📧 Email: seojaeseung9@gmail.com
🌐 Blog: https://seungcoding.tistory.com
💻 GitHub: https://github.com/jaeseung9

<br/> <p align="center"> ⭐ 이 프로젝트가 유용했다면 Star를 눌러주세요! </p> ```
💡 추가 팁
GitHub에서는 <table>, <img>, <p align="center"> 같은 HTML 태그가 안전하게 작동해

이 버전은 인덱스 링크(목차)도 잘 되고, 테이블도 깨지지 않도록 구성했어

Markdown 스타일은 유지하되 HTML로 디자인을 보완했기 때문에 README용으로 최적화된 버전이야

필요하면 이 기반으로 PPT/포트폴리오 PDF도 변환 가능해.
