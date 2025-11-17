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

## 라인 엔딩 & Git 설정
WSL이나 Windows 환경에서 애플리케이션을 실행하거나 IDE가 파일을 저장하면 LF(Line Feed)가 CRLF(Carriage Return + Line Feed)로 자동 변환되어 모든 Kotlin/Java 파일이 변경된 것으로 표시될 수 있습니다. 다음 설정을 적용해 불필요한 `git status` 변화를 막을 수 있습니다.

1. `.gitattributes`에 `* text=auto eol=lf` 또는 `*.kt text eol=lf` 같은 규칙을 추가해 모든 소스 파일을 LF로 강제합니다.
2. 로컬 Git 설정을 `git config --global core.autocrlf input`으로 맞추면 체크아웃 시 LF를 유지합니다.
3. Windows의 NTFS 마운트(`/mnt/c`) 대신 WSL 기본 파일시스템에 클론하면 CRLF 변환 가능성이 줄어듭니다.

위 설정을 적용한 뒤 `git add --renormalize .`를 실행하면 이미 CRLF로 바뀐 파일들을 한 번에 되돌릴 수 있습니다. 설정을 변경하기 전에는 반드시 현재 작업 중인 변경 사항을 별도 브랜치나 스태시로 보관하세요.

## 게시글 투표 사용법
1. **투표 활성화 게시글 작성**  
   게시글 생성 시 `voteEnabled`를 `true`로 두고 최소 2개 이상의 `voteItems` 배열을 전달합니다.
   ```json
   {
     "title": "오늘의 듀오 타입은?",
     "content": "솔랭/자유랭 중 골라주세요.",
     "voteEnabled": true,
     "voteItems": ["솔로 랭크", "자유 랭크"]
   }
   ```
2. **투표 요청**  
   `POST /boards/{boardId}/vote`에 `VoteRequest` 본문을 전송합니다. 로그인 사용자는 `Authorization` 헤더를 포함하고, 익명 사용자는 비밀번호 검증 후 호출할 수 있습니다.
   ```bash
   curl -X POST https://api.example.com/boards/{boardId}/vote \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <JWT>" \
     -d '{"voteItem": "솔로 랭크"}'
   ```
3. **응답 형식**  
   `VoteResultResponse`에는 사용자가 선택한 항목(`selectedItem`), 전체 항목별 집계(`voteCounts`), 총 투표 수(`totalVotes`)가 포함돼 있어 바로 UI에 반영할 수 있습니다.
   ```json
   {
     "message": "투표가 반영되었습니다.",
     "data": {
       "boardId": "0e6c8371-5dd9-4b06-9d0f-9d45c9fdc6c1",
       "selectedItem": "솔로 랭크",
       "voteCounts": {
         "솔로 랭크": 8,
         "자유 랭크": 3
       },
       "totalVotes": 11
     }
   }
   ```

## 배포/운영
- Dockerfile 및 docker-compose로 컨테이너 실행을 지원합니다.
- CI/CD, 환경 변수, 배포 자동화는 `infra_agent`와 Context7 MCP가 담당합니다.

## 라이선스
프로젝트에는 별도 라이선스가 정의되지 않았습니다. 필요 시 LICENSE 파일을 추가하세요.
