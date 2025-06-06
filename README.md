![madness](banner.svg)

# api.madness

## 개요

채팅 웹 어플리케이션 **Madness** 백엔드 서버

## 주요 기능

- **JWT 기반 인증 및 인가**
  - 사용자 로그인 시 발급되는 Access Token/Refresh Token을 통해 인증 처리
  - Spring Security와 JWT를 결합하여 권한(Role)에 따른 API 접근 제어
- **웹소켓 채팅 기능**
  - STOMP 프로토콜을 이용한 메시지 송수신
  - 실시간 채팅 룸 관리 및 메시지 브로드캐스트

## 기술 스택

- **언어 & 프레임워크**
  - Java 17, Spring Boot 3.4.2
  - Spring Security
  - Spring Web, Spring WebSocket
  - Spring Data JPA
  - QueryDSL
- **데이터베이스**
  - PostgreSQL(Production)
  - H2(Development)
- **빌드 도구 & 의존성 관리**
  - Gradle
- **JWT 라이브러리**
  - io.jsonwebtoken (JJWT)
- **메시징 & 웹소켓**
  - Spring Messaging (STOMP, SockJS)
- **테스트**
  - JUnit 5, Mockito
- **배포 & 인프라**
  - Cloudtype
  - GitHub Actions (CI/CD)

## 환경 변수

### Local(Development)

```bash
DEV_DB_URL=
DEV_DB_USERNAME=
DEV_DB_PASSWORD=
DEV_JWT_SECRET=
DEV_JWT_EXP_MS=
DEV_JWT_REFRESH_EXP_SEC=
```

### Production

```bash
SPRING_PROFILES_ACTIVE=prod
MD_JWT_EXP_MS=
MD_JWT_REFRESH_EXP_SEC=
MD_JWT_SECRET=
MD_PASSWORD=
MD_URL=
MD_USERNAME=
```

## 도메인

[api.madn.es](https://api.madn.es)

## Contact

Email: nera4936@gmail.com
