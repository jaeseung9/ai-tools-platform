<h1 align="center"> AI Tools Platform</h1>
<p align="center">
  <b>여러 AI API를 하나의 플랫폼에서</b><br/>
  Spring Boot + OAuth2 기반 통합 AI 도구 플랫폼
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.11-brightgreen?logo=springboot"/>
  <img src="https://img.shields.io/badge/Java-17-orange?logo=java"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql"/>
  <img src="https://img.shields.io/badge/OAuth2-Naver%20%7C%20Kakao-03C75A"/>
</p>

---

## 📑 목차

1. [📌 프로젝트 소개](#-프로젝트-소개)
2. [🎯 개발 동기 및 목표](#-개발-동기-및-목표)
3. [🏆 주요 성과](#-주요-성과)
4. [🛠️ 기술 스택](#️-기술-스택)
5. [🏗️ 시스템 아키텍처 및 설계](#️-시스템-아키텍처-및-설계)
6. [🚀 핵심 기능 구현](#-핵심-기능-구현)
   - [OAuth2 소셜 로그인](#1-oauth2-소셜-로그인)
   - [Rate Limiting](#2-rate-limiting-비용-관리)
   - [API 사용 통계](#3-api-사용-통계)
7. [🔧 트러블슈팅](#-트러블슈팅)
   - [OAuth2 Provider별 응답 구조 차이](#1️⃣-oauth2-provider별-응답-구조-차이)
   - [Rate Limiting 동시성 문제](#2️⃣-rate-limiting-동시성-문제)
   - [JPA N+1 문제](#3️⃣-jpa-n1-문제)
   - [Stability AI 타임아웃](#4️⃣-stability-ai-타임아웃)
8. [💡 기술적 의사결정](#-기술적-의사결정)
9. [📊 성능 최적화](#-성능-최적화)
10. [🎓 프로젝트를 통해 배운 점](#-프로젝트를-통해-배운-점)
11. [💻 실행 방법](#-실행-방법)
12. [📧 Contact](#-contact)

---

## 📌 프로젝트 소개

**개발 기간**: 2024.11 (3주, 약 120시간)  
**배포 URL**: https://ai-tools-platform-7air.onrender.com  
**GitHub**: https://github.com/jaeseung9/ai-tools-platform

> 네이버/카카오 소셜 로그인을 통해 Google Gemini, Stability AI 등 여러 AI API를  
> 하나의 인터페이스에서 사용하고, 실시간으로 사용량과 비용을 추적할 수 있는 통합 플랫폼
> <img width="363" height="254" alt="image" src="https://github.com/user-attachments/assets/f86000e7-272e-4acb-81df-97a0f0f3afaa" />


---

## 🎯 개발 동기 및 목표

### Why?
- 여러 AI 서비스를 사용하면서 **비용 관리의 어려움** 경험
- 각 플랫폼마다 별도 로그인이 필요한 **불편함** 해소
- **실무 수준의 인증/인가 시스템** 구현 경험 필요

### What?
- OAuth2 기반 통합 인증 시스템
- AI API 통합 및 사용량 추적
- 비용 관리 자동화

---

## 🏆 주요 성과

| 지표 | 내용 |
|------|------|
| **인증 시스템** | OAuth2 + 이메일 통합 인증 |
| **API 통합** | 3개 (Gemini, Stability AI, OAuth) |
| **모니터링** | 실시간 사용량/비용 추적 |
| **비용 절감** | Rate Limiting으로 일일 사용량 제한 |
| **배포** | Docker + Render 자동 배포 |

---

## 🛠️ 기술 스택

**Backend**: Spring Boot 3.4.11, Spring Security + OAuth2, JPA, PostgreSQL  
**Frontend**: HTML5, CSS3, Vanilla JavaScript  
**DevOps**: Docker, Render  
**APIs**: Google Gemini, Stability AI

---

## 🏗️ 시스템 아키텍처 및 설계




### 전체 구조

<img width="1536" height="1024" alt="ChatGPT Image 2025년 11월 19일 오후 05_13_57" src="https://github.com/user-attachments/assets/b3d27192-b07f-4425-85a0-7d796f24da84" />


### 핵심 설계 원칙

**1. 계층 분리 (Layered Architecture)**
- Controller: 요청 검증 및 응답 반환
- Service: 핵심 비즈니스 로직 처리
- Repository: 데이터 접근 추상화

**2. 모듈화 설계**
- 각 AI 도구를 독립적인 모듈로 구성
- 새로운 도구 추가 시 기존 코드 영향 최소화

**3. 보안 우선**
- Filter Chain을 통한 다층 보안
- 비밀번호 BCrypt 암호화
- SQL Injection 방지 (JPA 사용)

---

## 🚀 핵심 기능 구현

### 1. OAuth2 소셜 로그인

**구현 전략**
- Spring Security OAuth2 Client 활용
- Provider별 응답 구조 차이를 추상화 계층에서 통합 처리
```java
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        // Provider별 사용자 정보 추출
        String oauthId = extractOAuthId(provider, attributes);
        String email = extractEmail(provider, attributes);
        
        // DB 조회 또는 신규 생성
        return userRepository.findByOauthId(oauthId)
                .orElseGet(() -> createNewUser(oauthId, provider, email));
    }
}
```

**포인트**
- Naver와 Kakao의 다른 응답 구조를 통일된 인터페이스로 처리
- 신규/기존 사용자 자동 구분 및 처리

---

### 2. Rate Limiting (비용 관리)

**구현 전략**
- Filter에서 요청 전 토큰 사용량 체크
- ConcurrentHashMap으로 동시성 처리
```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    private static final int DAILY_TOKEN_LIMIT = 100;
    private final Map dailyTokenUsage = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(...) {
        String key = userIdentifier + "_" + LocalDate.now();
        int usedTokens = dailyTokenUsage.getOrDefault(key, 0);
        
        if (usedTokens >= DAILY_TOKEN_LIMIT) {
            throw new RateLimitException("일일 토큰 제한 초과");
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**포인트**
- 사용자별 일일 토큰 사용량 추적
- 자정 자동 초기화로 간단한 관리

---

### 3. API 사용 통계

**설계 고민**
- 실시간 집계 vs 배치 집계
- 선택: 실시간 집계 (사용자 경험 우선)

**구현 방법**
- UNIQUE 제약으로 중복 방지
- 월별 집계 데이터 자동 생성/업데이트
```sql
CREATE TABLE api_usage_stats (
    user_id BIGINT,
    tool_type VARCHAR(20),    -- 'CHAT', 'IMAGE'
    usage_count INT,
    total_cost DECIMAL(10, 4),
    year_month VARCHAR(7),    -- '2024-11'
    UNIQUE(user_id, tool_type, year_month)
);
```

---

## 🔧 트러블슈팅

### 1️⃣ OAuth2 Provider별 응답 구조 차이

**문제**
```
Naver: { response: { id: "...", email: "..." } }
Kakao: { id: ..., kakao_account: { email: "..." } }
```
서로 다른 JSON 구조로 인한 파싱 오류 발생

**해결**
- Provider별 분기 처리 메서드 작성
- 추상화 계층에서 통일된 User 객체로 변환

**배운 점**
- 외부 API 통합 시 **추상화 계층의 중요성** 인식
- 확장 가능한 구조 설계의 필요성

---

### 2️⃣ Rate Limiting 동시성 문제

**문제**
- 여러 요청이 동시에 들어올 때 토큰 카운터가 부정확하게 증가
- Race Condition 발생

**시도한 방법**
1. 일반 HashMap 사용 → 동시성 문제 발생
2. synchronized 키워드만 사용 → 성능 저하

**최종 해결**
```java
public synchronized void addTokenUsage(String userIdentifier, int tokens) {
    String key = userIdentifier + "_" + LocalDate.now();
    // atomic operation
    dailyTokenUsage.merge(key, tokens, Integer::sum);
}
```

**배운 점**
- `ConcurrentHashMap`과 `synchronized`의 적절한 조합
- `merge()` 메서드의 원자성 보장
- 향후 Redis 도입 시 Lua Script 활용 가능성 학습

---

### 3️⃣ JPA N+1 문제

**문제**
```java
// ChatHistory 100개 조회 시
// User 조회 쿼리가 100번 추가 실행됨
List history = chatHistoryRepository.findByUserId(userId);
```

**해결**
```java
@Entity
public class ChatHistory {
    @ManyToOne(fetch = FetchType.LAZY)  // Eager → Lazy
    private User user;
}

// 인덱스 추가
CREATE INDEX idx_chat_user_created 
ON chat_history(user_id, created_at DESC);
```

**성과**
- 조회 속도 **약 3배 향상**
- 불필요한 DB 커넥션 감소

**배운 점**
- JPA의 Lazy Loading 전략 이해
- 인덱스 설계의 중요성
- 실제 쿼리 로그 확인 습관화

---

### 4️⃣ Stability AI 타임아웃

**문제**
- 이미지 생성에 15~30초 소요
- RestTemplate 기본 타임아웃(5초) 초과로 에러 발생

**해결**
```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
            .setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
}
```

**추가 개선**
- 프론트엔드에 로딩 스피너 추가
- 사용자에게 예상 소요 시간 안내

**배운 점**
- 외부 API 특성에 맞는 타임아웃 설정 필요
- 사용자 경험(UX) 고려의 중요성

---

## 💡 기술적 의사결정

### OAuth2 vs JWT
**선택: OAuth2 (세션 기반)**

| 항목 | 선택 이유 |
|------|----------|
| 개발 속도 | Spring Security 자동 처리 |
| 안정성 | Provider가 인증 책임 |
| 학습 곡선 | 기존 세션 방식에 익숙 |

**트레이드오프**
- 장점: 빠른 개발, 안정적인 인증
- 단점: 세션 메모리 사용, 확장성 제한
- 향후 개선: 대규모 트래픽 시 JWT 전환 고려

---

### RestTemplate vs WebClient
**선택: RestTemplate**

**이유**
- 동기 방식으로 로직 흐름 이해 쉬움
- 외부 API 호출 빈도 낮음 (비동기 필요성 낮음)
- 학습 곡선 고려 (프로젝트 기간 3주)

**배운 점**
- 상황에 맞는 기술 선택의 중요성
- 트레이드오프 고려한 의사결정

---

## 📊 성능 최적화

### 인덱스 설계
```sql
-- 사용자별 최신 채팅 조회 최적화
CREATE INDEX idx_chat_user_created 
ON chat_history(user_id, created_at DESC);

-- 월별 통계 조회 최적화
CREATE INDEX idx_stats_user_month 
ON api_usage_stats(user_id, year_month);
```

**효과**
- 히스토리 조회 속도 **3배 향상**
- 대시보드 로딩 시간 **1.5초 → 0.4초**

---

## 🎓 프로젝트를 통해 배운 점

### 기술적 성장
1. **OAuth2 인증 플로우 이해**
   - Authorization Code Grant 방식
   - Provider별 차이점과 통합 방법

2. **동시성 제어**
   - ConcurrentHashMap, synchronized 활용
   - Race Condition 해결 경험

3. **JPA 성능 최적화**
   - N+1 문제 인식 및 해결
   - Lazy Loading, 인덱스 설계

4. **외부 API 통합**
   - 타임아웃 처리
   - 에러 핸들링
   - 비용 추적 시스템 구축

### 소프트 스킬
1. **문제 해결 능력**
   - 발생한 문제를 로그와 디버깅으로 원인 파악
   - 여러 해결 방법을 시도하고 최적안 선택

2. **설계 능력**
   - 확장 가능한 모듈 구조 설계
   - 계층 분리를 통한 유지보수성 향상

3. **학습 능력**
   - 공식 문서 읽기 습관화
   - Stack Overflow, GitHub Issue 활용

---

## 💻 실행 방법
```bash
# 환경 변수 설정
export DB_URL="jdbc:postgresql://localhost:5432/aitools"
export GEMINI_API_KEY="your-key"
export STABILITY_API_KEY="your-key"
export NAVER_CLIENT_ID="your-id"
export NAVER_CLIENT_SECRET="your-secret"
export KAKAO_CLIENT_ID="your-id"

# 실행
./gradlew bootRun
```

---

## 📧 Contact

**서재승 (Seo Jae Seung)**  
📧 seojaeseung9@gmail.com  
🌐 https://seungcoding.tistory.com  
💻 https://github.com/jaeseung9

---

<p align="center">
  <a href="#-ai-tools-platform">
    <img src="https://img.shields.io/badge/⬆️-맨_위로_가기-blue?style=for-the-badge" alt="맨 위로"/>
  </a>
</p>
