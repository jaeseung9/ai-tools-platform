<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>AI Tools Platform – README</title>
  <style>
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
      line-height: 1.6;
      color: #111827;
      background: #f9fafb;
      margin: 0;
      padding: 24px;
    }
    .container {
      max-width: 920px;
      margin: 0 auto;
      background: #ffffff;
      padding: 32px 28px 40px;
      border-radius: 16px;
      box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
    }
    h1, h2, h3, h4 {
      margin-top: 1.6em;
      margin-bottom: 0.6em;
      font-weight: 700;
    }
    h1 { font-size: 26px; }
    h2 { font-size: 22px; }
    h3 { font-size: 18px; }
    h4 { font-size: 16px; }
    p {
      margin: 0.3em 0 0.6em;
    }
    hr {
      border: none;
      border-top: 1px solid #e5e7eb;
      margin: 24px 0;
    }
    .center {
      text-align: center;
    }
    .badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 999px;
      background: #eef2ff;
      color: #4338ca;
      font-size: 12px;
      margin-left: 4px;
    }
    ul {
      padding-left: 20px;
      margin: 4px 0 8px;
    }
    pre {
      background: #0b1120;
      color: #e5e7eb;
      padding: 12px 14px;
      border-radius: 8px;
      overflow-x: auto;
      font-size: 13px;
    }
    code {
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
      font-size: 0.9em;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin: 8px 0 12px;
      font-size: 14px;
    }
    th, td {
      border: 1px solid #e5e7eb;
      padding: 8px 10px;
      text-align: left;
      vertical-align: top;
    }
    th {
      background: #f3f4f6;
      font-weight: 600;
    }
    tbody tr:nth-child(even) {
      background: #fafafa;
    }
    a {
      color: #2563eb;
      text-decoration: none;
    }
    a:hover {
      text-decoration: underline;
    }
    .blockquote {
      padding: 8px 12px;
      border-left: 3px solid #e5e7eb;
      background: #f9fafb;
      font-size: 14px;
      margin: 8px 0 12px;
    }
    .small {
      font-size: 13px;
      color: #6b7280;
    }
    .toc-list li {
      margin: 2px 0;
    }
    .pill {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 999px;
      background: #eff6ff;
      color: #1d4ed8;
      font-size: 11px;
      margin-left: 4px;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1 id="top">🤖 AI Tools Platform</h1>
    <p class="center">
      Spring Boot + OAuth2 기반 통합 AI 도구 플랫폼<br />
      <b>Chat</b> · <b>Image Generation</b> · <b>Usage Dashboard</b>를 하나의 인터페이스로
    </p>

    <hr />

    <h2>🗓 개발 기간</h2>
    <div class="blockquote">
      2024.11 (3주)
    </div>

    <hr />

    <h2>📚 목차</h2>
    <ul class="toc-list">
      <li><a href="#about">1. 프로젝트 소개</a></li>
      <li><a href="#demo">2. 데모</a></li>
      <li><a href="#features">3. 주요 기능</a></li>
      <li><a href="#stack">4. 기술 스택</a></li>
      <li><a href="#architecture">5. 아키텍처</a></li>
      <li><a href="#api">6. API 명세</a></li>
      <li><a href="#database">7. 데이터베이스 설계</a></li>
      <li><a href="#run">8. 실행 방법</a></li>
      <li><a href="#insights">9. 개발 과정 &amp; 트러블슈팅</a></li>
    </ul>

    <hr />

    <h2 id="about">1) 프로젝트 소개</h2>
    <p>
      포트폴리오 메인 프로젝트로 제작한 <b>AI Tools Platform</b>입니다. 네이버/카카오 소셜 로그인을 통해 여러 AI API(Google Gemini, Stability AI)를 하나의 플랫폼에서 사용할 수 있으며, 사용 통계 대시보드를 통해 API 비용과 사용량을 실시간으로 추적할 수 있습니다.
    </p>

    <h3>🎯 개발 목표</h3>
    <ul>
      <li>여러 AI API를 통합한 <b>원스톱 플랫폼</b> 구축</li>
      <li>실무 수준의 인증/인가 시스템 구현 (OAuth2 + Custom Logic)</li>
      <li><b>API 비용 관리 및 사용량 추적 자동화</b></li>
      <li>클라우드 배포 완료 (Render)</li>
    </ul>

    <hr />

    <h2 id="demo">2) 데모</h2>

    <h3>🌐 배포 URL</h3>
    <div class="blockquote">
      <a href="https://ai-tools-platform-7air.onrender.com" target="_blank" rel="noreferrer">
        https://ai-tools-platform-7air.onrender.com
      </a>
      <br />
      <span class="small">⚠️ 첫 로딩 시 콜드 스타트로 인해 20~30초 지연될 수 있습니다.</span>
    </div>

    <h3>💻 주요 화면</h3>
    <ul>
      <li><b>로그인</b>: 네이버/카카오 소셜 로그인 + 이메일 회원가입/로그인</li>
      <li><b>Chat</b>: Gemini AI 대화 + 실시간 토큰 사용량 표시</li>
      <li><b>Image Generator</b>: Stability AI 이미지 생성 후 Base64 즉시 렌더링</li>
      <li><b>Dashboard</b>: 월별 사용 통계 &amp; 도구별 비용 분석</li>
    </ul>

    <hr />

    <h2 id="features">3) 🚀 주요 기능</h2>

    <table>
      <thead>
        <tr>
          <th>기능</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
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
      </tbody>
    </table>

    <hr />

    <h2 id="stack">4) 🧱 기술 스택</h2>

    <h3>Backend</h3>
    <ul>
      <li><b>Spring Boot 3.4.1</b></li>
      <li>Spring Security + OAuth2</li>
      <li>Spring Data JPA</li>
      <li>PostgreSQL</li>
      <li>Lombok / RestTemplate</li>
    </ul>

    <h3>Frontend</h3>
    <ul>
      <li>HTML5 / CSS3 / Vanilla JavaScript</li>
    </ul>

    <h3>API Integration</h3>
    <ul>
      <li><b>Google Gemini API</b> – 채팅</li>
      <li><b>Stability AI</b> – 이미지 생성</li>
    </ul>

    <h3>DevOps</h3>
    <ul>
      <li>Render – 클라우드 배포</li>
      <li>Docker – 이미지 구성</li>
      <li>GitHub Actions – CI/CD</li>
    </ul>

    <hr />

    <h2 id="architecture">5) 🏗️ 아키텍처</h2>

    <p class="center">
      <img
        src="https://github.com/user-attachments/assets/766870a9-093e-40b7-b55b-84955012edd3"
        alt="AI Tools Platform Architecture Diagram"
        style="max-width: 100%; border-radius: 12px; box-shadow: 0 6px 16px rgba(15,23,42,0.12);"
      />
    </p>

    <h3>💡 핵심 설계 원칙</h3>
    <ul>
      <li>Controller → Service → Repository 계층 분리</li>
      <li>DTO 패턴으로 계층 간 데이터 전달</li>
      <li>전역 예외 처리로 일관된 에러 응답</li>
      <li>Filter 체인으로 횡단 관심사 처리 (인증, Rate Limiting)</li>
    </ul>

    <hr />

    <h2 id="api">6) 📡 API 명세</h2>

    <h3>🔐 인증 API</h3>
    <table>
      <thead>
        <tr>
          <th>Method</th>
          <th>Endpoint</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>POST</td>
          <td><code>/api/auth/signup</code></td>
          <td>이메일 회원가입</td>
        </tr>
        <tr>
          <td>POST</td>
          <td><code>/api/auth/login</code></td>
          <td>이메일 로그인</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/oauth2/authorization/naver</code></td>
          <td>네이버 로그인</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/oauth2/authorization/kakao</code></td>
          <td>카카오 로그인</td>
        </tr>
      </tbody>
    </table>

    <h3>💬 채팅 API</h3>
    <table>
      <thead>
        <tr>
          <th>Method</th>
          <th>Endpoint</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>POST</td>
          <td><code>/api/chat/message</code></td>
          <td>메시지 전송</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/api/chat/history</code></td>
          <td>대화 기록 조회</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/api/chat/remaining-tokens</code></td>
          <td>잔여 토큰 조회</td>
        </tr>
      </tbody>
    </table>

    <h3>🎨 이미지 API</h3>
    <table>
      <thead>
        <tr>
          <th>Method</th>
          <th>Endpoint</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>POST</td>
          <td><code>/api/image/generate</code></td>
          <td>이미지 생성</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/api/image/history</code></td>
          <td>생성 기록 조회</td>
        </tr>
      </tbody>
    </table>

    <h3>📊 대시보드 API</h3>
    <table>
      <thead>
        <tr>
          <th>Method</th>
          <th>Endpoint</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>GET</td>
          <td><code>/api/dashboard/stats</code></td>
          <td>이번 달 통계</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/api/dashboard/usage-chart</code></td>
          <td>월별 차트 데이터</td>
        </tr>
        <tr>
          <td>GET</td>
          <td><code>/api/dashboard/cost-breakdown</code></td>
          <td>도구별 비용 비율</td>
        </tr>
      </tbody>
    </table>

    <hr />

    <h2 id="database">7) 🗄️ 데이터베이스 설계</h2>

    <h3>🎯 ERD 구조</h3>

    <h4><code>users</code></h4>
    <pre><code>id (PK)
oauth_id (UNIQUE) - 소셜 로그인 식별자
provider - naver/kakao/local
email (UNIQUE)
password
created_at</code></pre>

    <h4><code>chat_history</code></h4>
    <pre><code>id (PK)
user_id (FK → users)
user_message
ai_response
token_used
estimated_cost
created_at</code></pre>

    <h4><code>image_history</code></h4>
    <pre><code>id (PK)
user_id (FK → users)
prompt
image_url (Base64 Data URL)
image_size
estimated_cost
created_at</code></pre>

    <h4><code>api_usage_stats</code></h4>
    <pre><code>id (PK)
user_id (FK → users)
tool_type - CHAT / IMAGE
usage_count
total_cost
year_month - "2024-11"

UNIQUE(user_id, tool_type, year_month)</code></pre>

    <h3>📌 주요 인덱스</h3>
    <pre><code class="language-sql">-- 사용자별 히스토리 조회 최적화
idx_chat_user_created (user_id, created_at DESC)
idx_image_user_created (user_id, created_at DESC)
idx_stats_user_month (user_id, year_month)</code></pre>

    <hr />

    <h2 id="run">8) 💻 실행 방법</h2>

    <h3>로컬 실행</h3>
    <pre><code class="language-bash"># 1. 환경 변수 설정 (.env 파일 또는 시스템 환경 변수)
export DB_URL="jdbc:postgresql://localhost:5432/aitools"
export GEMINI_API_KEY="your-api-key"
export STABILITY_API_KEY="your-api-key"
export NAVER_CLIENT_ID="your-client-id"
export KAKAO_CLIENT_ID="your-client-id"

# 2. 빌드 및 실행
./gradlew bootRun</code></pre>

    <h3>Docker 실행</h3>
    <pre><code class="language-bash">docker build -t ai-tools-platform .
docker run -p 8080:8080 \
  -e DB_URL="..." \
  -e GEMINI_API_KEY="..." \
  ai-tools-platform</code></pre>

    <h3>필수 환경 변수</h3>
    <table>
      <thead>
        <tr>
          <th>Key</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><code>DB_URL</code></td>
          <td>PostgreSQL 연결 URL</td>
        </tr>
        <tr>
          <td><code>DB_USERNAME</code></td>
          <td>DB 사용자명</td>
        </tr>
        <tr>
          <td><code>DB_PASSWORD</code></td>
          <td>DB 비밀번호</td>
        </tr>
        <tr>
          <td><code>OAUTH_REDIRECT_BASE_URL</code></td>
          <td>OAuth2 기본 Redirect URL</td>
        </tr>
        <tr>
          <td><code>NAVER_CLIENT_ID</code></td>
          <td>네이버 앱 Client ID</td>
        </tr>
        <tr>
          <td><code>NAVER_CLIENT_SECRET</code></td>
          <td>네이버 앱 Secret</td>
        </tr>
        <tr>
          <td><code>KAKAO_CLIENT_ID</code></td>
          <td>카카오 REST API 키</td>
        </tr>
        <tr>
          <td><code>KAKAO_CLIENT_SECRET</code></td>
          <td>카카오 Secret</td>
        </tr>
        <tr>
          <td><code>GEMINI_API_KEY</code></td>
          <td>Google Gemini API 키</td>
        </tr>
        <tr>
          <td><code>STABILITY_API_KEY</code></td>
          <td>Stability AI API 키</td>
        </tr>
      </tbody>
    </table>

    <hr />

    <h2 id="insights">9) 🌱 개발 과정 &amp; 트러블슈팅</h2>

    <h3>왜 이 프로젝트를 만들었나?</h3>
    <ul>
      <li><b>실무 수준의 웹 개발 경험</b>을 쌓기 위해 – 게임 개발(C++ WinAPI) 경험은 있었지만 웹 개발은 처음이라, 실제 서비스처럼 동작하는 플랫폼을 만들고 싶었습니다.</li>
      <li><b>AI API 통합 경험</b> – ChatGPT, DALL-E 등 여러 AI 도구를 사용하다 보니, “이것들을 하나로 통합하면 어떨까?”라는 고민에서 시작했습니다.</li>
      <li><b>인증/인가 시스템 학습</b> – OAuth2와 Spring Security의 동작 원리를 실제로 구현해보고 싶었습니다.</li>
    </ul>

    <h3>주요 트러블슈팅</h3>

    <h4>🔴 1. OAuth2 소셜 로그인 구현의 복잡성</h4>
    <p class="small">네이버와 카카오의 응답 구조가 서로 달라, Provider별로 분기 처리가 필요했습니다.</p>
    <pre><code class="language-java">private String extractOAuthId(String provider, Map&lt;String, Object&gt; attributes) {
    if ("naver".equals(provider)) {
        Map&lt;String, Object&gt; response = (Map&lt;String, Object&gt;) attributes.get("response");
        return provider + "_" + response.get("id");
    } else if ("kakao".equals(provider)) {
        return provider + "_" + attributes.get("id");
    }
    throw new OAuth2AuthenticationException("Unsupported provider");
}</code></pre>

    <ul>
      <li><code>CustomOAuth2UserService</code>에서 provider별로 분기 처리</li>
      <li><code>extractOAuthId()</code>, <code>extractEmail()</code> 메서드로 공통 인터페이스 추출</li>
      <li><code>application.properties</code>를 dev/prod 프로파일로 분리해 환경별 설정 관리</li>
    </ul>

    <h4>🔴 2. Rate Limiting 동시성 문제</h4>
    <p class="small">동시에 여러 요청이 들어오면, 토큰 카운터가 부정확하게 증가하는 문제가 발생했습니다.</p>
    <pre><code class="language-java">public synchronized void addTokenUsage(String userIdentifier, int tokens) {
    String key = userIdentifier + "_" + LocalDate.now();
    dailyTokenUsage.merge(key, tokens, Integer::sum);
}</code></pre>
    <p class="small">자정마다 자동 리셋:</p>
    <pre><code class="language-java">private synchronized void resetIfNewDay() {
    LocalDate today = LocalDate.now();
    if (!today.equals(lastResetDate)) {
        dailyTokenUsage.clear();
        lastResetDate = today;
    }
}</code></pre>

    <ul>
      <li><code>ConcurrentHashMap</code> + <code>synchronized</code>로 Thread-Safe 보장</li>
      <li>자정 기준으로 Map 초기화</li>
      <li>향후 Redis 기반 분산 Rate Limiting으로 확장 예정</li>
    </ul>

    <h4>🔴 3. JPA N+1 문제</h4>
    <p class="small">히스토리 조회 시 사용자 정보를 매번 개별 쿼리로 조회하는 문제가 있었습니다.</p>
    <pre><code class="language-java">@ManyToOne(fetch = FetchType.LAZY)
private User user;</code></pre>
    <ul>
      <li>지연 로딩(<code>LAZY</code>)으로 설정해 필요할 때만 연관 엔티티 로딩</li>
      <li>DTO 변환 시 필요한 필드만 추출</li>
      <li>복합 인덱스 추가로 조회 최적화</li>
    </ul>

    <h4>🔴 4. 배포 환경 PostgreSQL 마이그레이션</h4>
    <p class="small">
      로컬에서는 MySQL로 개발했지만, Render 배포 시 PostgreSQL로 전환하면서 예약어 충돌 및 제약조건 문제를 겪었습니다.
    </p>
    <pre><code class="language-properties">spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}</code></pre>
    <pre><code class="language-java">@Column(name = "`year_month`")
private String yearMonth;</code></pre>

    <ul>
      <li>환경 변수 기반 DB 설정으로 로컬/배포 환경 분리</li>
      <li>PostgreSQL 예약어 컬럼명을 백틱으로 감싸 해결</li>
      <li><code>spring.jpa.hibernate.ddl-auto=update</code>로 스키마 자동 관리</li>
    </ul>

    <h3>🏁 기술적 성과</h3>
    <ul>
      <li><b>보안</b>: Bcrypt 비밀번호 암호화, OAuth2 표준 인증, Rate Limiting</li>
      <li><b>성능</b>: 인덱스 최적화, 지연 로딩으로 쿼리 수 절감</li>
      <li><b>유지보수성</b>: 계층 분리, DTO 패턴, 전역 예외 처리</li>
      <li><b>사용자 경험</b>: 실시간 토큰 사용량 표시, 직관적인 UI</li>
    </ul>

    <h3>🔮 향후 개선 계획</h3>
    <ul>
      <li>Redis 기반 Rate Limiting: 분산 환경 지원</li>
      <li>JWT 기반 인증: 무상태 API 서버로 전환</li>
      <li>JUnit 단위/통합 테스트 코드 작성</li>
      <li>Chart.js 기반 대시보드 시각화</li>
      <li>다크 모드 테마 지원</li>
    </ul>

    <hr />

    <h2>📧 Contact</h2>
    <p>
      <b>서재승 (Seo Jae Seung)</b><br />
      📧 Email: <a href="mailto:seojaeseung9@gmail.com">seojaeseung9@gmail.com</a><br />
      🌐 Blog: <a href="https://seungcoding.tistory.com" target="_blank" rel="noreferrer">https://seungcoding.tistory.com</a><br />
      💻 GitHub: <a href="https://github.com/jaeseung9" target="_blank" rel="noreferrer">https://github.com/jaeseung9</a>
    </p>

    <p class="center" style="margin-top: 24px;">
      ⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요!
    </p>

    <p class="center small">
      <a href="#top">맨 위로 ↑</a>
    </p>
  </div>
</body>
</html>
