# rudeFriend API

rudeFriend API는 League of Legends 및 TFT 이용자들의 친구 매칭과 소통을 지원하는 Kotlin/Spring Boot 백엔드 서비스입니다. 사용자 인증, 게시글/친구 관리, 파일 업로드, 투표형 상호작용 등 커뮤니티 기능을 제공하며 Codex 에이전트가 코드/테스트/문서를 자동화하도록 설계되어 있습니다.

## 주요 기능
- **인증/인가**: JWT 기반 로그인, 토큰 재발급, 익명 사용자 비밀번호 검증 지원.
- **게시판 & 투표 시스템**: 게시글 CRUD, 이미지 업로드, 태그 필터링, 선택형 투표 항목 구성(`voteEnabled`, `voteItems`).
- **친구/유저 관리**: 게임 계정 연동 및 티어/랭크 정보, 회원 검색/수정 API 제공.
- **파일 관리**: S3 업로드/삭제 및 게시글별 파일 URL 관리.
- **자동화 에이전트 연동**: GitHub/Notion/Context7 MCP 서버와 연동해 테스트, 배포, 문서화를 자동화.

## 기술 스택
- Kotlin, Spring Boot 3, Spring Security, Spring Data JPA
- QueryDSL, Hibernate Validator, Swagger/OpenAPI 3
- AWS S3 (파일 업로드), H2/MySQL (프로파일별)
- Gradle (Kotlin DSL), Flyway 마이그레이션 스크립트

## 시작하기
1. **필수 조건**
   - JDK 21 이상 (`JAVA_HOME` 설정)
   - Gradle Wrapper 사용 (`./gradlew`)
   - 데이터베이스 및 AWS S3 자격 증명
2. **환경 변수**
   - `.env` 혹은 `application.yml`에 JWT, DB, AWS 설정을 입력합니다. 민감 정보는 저장소에 커밋하지 마세요.
3. **로컬 실행**
   ```bash
   ./gradlew bootRun
   ```
4. **테스트**
   ```bash
   ./gradlew test
   ```

## 주요 모듈 구조
- `controller/` : REST API 엔드포인트
- `service/` : 비즈니스 로직 & 트랜잭션
- `repository/` : JPA/QueryDSL 저장소
- `entity/` : 도메인 엔티티 및 DTO 매핑
- `config/` : Security/JWT/Swagger/S3 설정
- `test/` : 단위/통합 테스트

## 문서 & 협업
- API 명세: [Notion – rudeFriend API](https://www.notion.so/rudeFriend-API-2a9900af66c981fb87e1cb0198ae7235)
- 브랜치 전략: `develop` → 기능 브랜치 → PR
- Codex 에이전트: `api_agent`, `infra_agent`, `docs_agent`, `review_agent`

## 배포/운영
- Dockerfile 및 docker-compose로 컨테이너 실행을 지원합니다.
- CI/CD, 환경 변수, 배포 자동화는 `infra_agent`와 Context7 MCP가 담당합니다.

## 라이선스
프로젝트에는 별도 라이선스가 정의되지 않았습니다. 필요 시 LICENSE 파일을 추가하세요.
