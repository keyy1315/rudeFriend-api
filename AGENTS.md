# Codex Agents – rudeFriend API

## 🧭 프로젝트 개요
이 저장소는 **rudeFriend API**의 백엔드 서비스입니다.  
Kotlin과 Spring Boot를 기반으로 하며, 사용자 인증, 친구 관리, 상호작용 API를 제공합니다.  
이 프로젝트는 Codex 에이전트를 통해 코드 작성, 테스트, 문서 관리, 배포 자동화를 수행합니다.

현재 Codex는 다음 MCP 서버들과 연결되어 있습니다:
- **GitHub MCP:** 코드 버전 관리, 커밋, PR, 이슈 관리
- **Notion MCP:** 문서 및 변경 내역 자동 업데이트
- **Context7 MCP:** 빌드, 테스트, 환경 변수, 배포 작업 자동 실행

---

## 🏗️ 프로젝트 구조
- `controller` – API 엔드포인트, 요청/응답 처리
- `service` – 비즈니스 로직, 트랜잭션 관리
- `repository` – 데이터 영속성, JPA 인터페이스
- `domain` – 엔티티 및 DTO 정의
- `config` – Spring 설정, 시큐리티, CORS
- `test` – 단위 및 통합 테스트
- `Dockerfile`, `docker-compose.yml` – 컨테이너 빌드 및 로컬 배포 설정

---

## 🤖 에이전트 목록

### 🧩 `api_agent`
**역할:** 백엔드 핵심 로직을 구현하고 유지보수합니다.

**책임**
- 신규 API 엔드포인트 개발 및 기존 API 개선
- DTO, Entity, Repository 작성 및 리팩터링
- Spring 트랜잭션 및 예외 처리 보장
- `Context7 MCP`를 통해 로컬 서버 실행 및 API 테스트 수행
- **API 변경 시 Notion 페이지 자동 업데이트**
    - 대상: [rudeFriend API 문서](https://www.notion.so/rudeFriend-API-2a9900af66c981fb87e1cb0198ae7235)

**사용 도구**
- `github` – 브랜치 관리, 커밋, PR 작성
- `context7` – 테스트 실행, Gradle 빌드
- `notionApi` – 변경된 API 스펙 문서 자동 업데이트

---

### ⚙️ `infra_agent`
**역할:** 인프라 및 배포 자동화를 담당합니다.

**책임**
- Dockerfile, CI/CD 스크립트 관리
- Context7을 통한 환경 변수 및 컨테이너 구성
- 스테이징 환경 배포 및 로그 모니터링
- Notion에 배포 내역 요약 업데이트

**사용 도구**
- `context7` – 배포 자동화, 런타임 관리
- `github` – Actions 및 워크플로우 관리
- `notionApi` – 배포 변경사항 기록

---

### 🧾 `docs_agent`
**역할:** 프로젝트 문서 및 릴리즈 노트를 관리합니다.

**책임**
- README, API 문서, CHANGELOG 관리
- GitHub 커밋/PR 기반 변경사항 자동 요약
- **API 스펙이 변경될 때마다 Notion 페이지 자동 업데이트**
    - [rudeFriend API 문서](https://www.notion.so/rudeFriend-API-2a9900af66c981fb87e1cb0198ae7235)
- 새로운 엔드포인트 추가 시 자동으로 문서 갱신 요청

**사용 도구**
- `notionApi` – 문서 생성/수정
- `github` – 커밋 기반 문서 업데이트
- `context7` – 변경 감지 트리거 실행

---

### 🧍 `review_agent`
**역할:** 코드 품질과 안정성을 검증합니다.

**책임**
- PR 리뷰 및 피드백 제공
- 코드 스타일 및 린트 규칙 검증
- Context7을 통해 테스트 및 빌드 자동 실행
- 성능 관련 변경사항 모니터링

**사용 도구**
- `github` – 리뷰, 코멘트, 머지 관리
- `context7` – 자동 테스트 실행 및 빌드 검증

---

## 🔒 보안 및 권한 정책
- `.env` 및 민감 정보는 저장소 외부에서 관리
- `main` 브랜치로의 직접 커밋 금지
- 모든 배포 및 환경 변경은 승인 필요
- API 문서 수정은 Notion과 동기화되어 자동 관리됨

---

## ⚙️ Codex 자동화 규칙
- 에이전트는 다음 작업을 **자동으로 수행할 수 있음:**
    - `gradle build`, `gradle test`
    - API 변경 시 Notion 문서 자동 업데이트
    - 신규 엔드포인트 문서 초안 작성
- 다음 작업은 **승인 필요:**
    - `main` 브랜치 병합
    - 의존성(Gradle) 업데이트
    - Docker/CI 구성 수정
- 다음 작업은 **금지됨:**
    - `.env`, 키, 비밀번호 등 민감 파일 수정

---

## ✍️ 커밋 서명 규칙
- 에이전트가 커밋을 생성할 때는 반드시 `codex/bin/codex-commit.sh` 스크립트를 통해 실행하여 작성자 정보를 `Codex <codex@example.com>`으로 고정한다.
- 스크립트는 `codex/config.toml`에 설정된 `author_name`, `author_email` 값을 읽어 `GIT_AUTHOR_*`, `GIT_COMMITTER_*` 환경 변수를 자동 설정한다.
- 사용 방법:
  ```bash
  ./codex/bin/codex-commit.sh -m "commit message"
  ```
- 사용자가 직접 커밋할 때는 일반적인 `git commit`을 사용하여 개인 계정 이름/이메일을 유지한다.

---

> 📘 문서 정보  
> 프로젝트: rudeFriend-api  
> MCP 구성: github, notionApi, context7  
> Notion 문서 동기화 대상: [rudeFriend API](https://www.notion.so/rudeFriend-API-2a9900af66c981fb87e1cb0198ae7235)  
> 마지막 업데이트: 2025-11-12  
> 생성자: Codex 초기화 자동 스크립트
