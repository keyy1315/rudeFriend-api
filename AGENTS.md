# Codex Agents – rudeFriend API

## 🧭 프로젝트 개요

이 저장소는 **rudeFriend API**의 백엔드 서비스입니다.
Kotlin과 Spring Boot 기반이며, 인증, 친구 관리, 상호작용 기능을 제공합니다.
Codex 에이전트를 통해 코드 작성, 테스트, 문서 관리, 배포 자동화를 수행합니다.

현재 Codex는 다음 MCP 서버들과 연결되어 있습니다:

* **GitHub MCP:** 코드 버전 관리, 커밋, PR, 이슈 관리
* **Notion MCP:** 문서 및 변경 내역 자동 업데이트
* **Context7 MCP:** 빌드, 테스트, 환경 변수, 배포 작업 자동 실행

---

## 🏗️ 프로젝트 구조

* `controller` – API 엔드포인트, 요청/응답 처리
* `service` – 비즈니스 로직, 트랜잭션 관리
* `repository` – 데이터 영속성, JPA 인터페이스
* `domain` – 엔티티 및 DTO 정의
* `config` – Spring 설정, 시큐리티, CORS
* `test` – 단위 및 통합 테스트
* `Dockerfile`, `docker-compose.yml` – 컨테이너 빌드 및 로컬 배포 설정

---

## 🤖 에이전트 목록

### 🧩 `api_agent`

**역할:** 백엔드 핵심 로직을 구현하고 유지보수합니다.

**책임**

* 신규 API 엔드포인트 개발 및 기존 API 개선
* DTO, Entity, Repository 작성 및 리팩터링
* Spring 트랜잭션 및 예외 처리 보장
* `Context7 MCP`를 통해 로컬 서버 실행 및 API 테스트 수행

**사용 도구**

* `github` – 브랜치 관리, 커밋, PR 작성
* `context7` – 테스트 실행, Gradle 빌드
* `notionApi` – 변경된 API 스펙 문서 자동 업데이트

---

### ⚙️ `infra_agent`

**역할:** 인프라 및 배포 자동화를 담당합니다.

**책임**

* Dockerfile, CI/CD 스크립트 관리
* Context7을 통한 환경 변수 및 컨테이너 구성
* 스테이징 배포 및 로그 모니터링
* Notion에 배포 내역 요약 업데이트

**사용 도구**

* `context7` – 배포 자동화, 런타임 관리
* `github` – Actions 및 워크플로우 관리
* `notionApi` – 배포 변경사항 기록

---

### 🧾 `docs_agent`

**역할:** 프로젝트 문서 및 릴리즈 노트를 관리합니다.

**책임**

* README, API 문서, CHANGELOG 관리
* GitHub 커밋/PR 기반 변경사항 자동 요약
* **API 스펙 변경 시 Notion 문서 자동 업데이트**

**사용 도구**

* `notionApi` – 문서 생성/수정
* `github` – 커밋 기반 문서 업데이트
* `context7` – 변경 감지 트리거 실행

---

### 🧍 `review_agent`

**역할:** 코드 품질과 안정성을 검증합니다.

**책임**

* PR 리뷰 및 피드백 제공
* 코드 스타일 및 린트 규칙 검증
* Context7을 통해 테스트 및 빌드 자동 실행
* 성능 관련 변경사항 모니터링

**사용 도구**

* `github` – 리뷰, 코멘트, 머지 관리
* `context7` – 자동 테스트 실행 및 빌드 검증

---

## 🔒 보안 및 권한 정책

* `.env` 및 민감 정보는 저장소 외부에서 관리
* `main` 브랜치로 직접 커밋 금지
* 모든 배포 및 환경 변경은 승인 필요
* API 문서 수정은 Notion과 동기화되어 자동 관리됨

---

## ⚙️ Codex 자동화 규칙

* `gradle build`, `gradle test` 자동 수행
* API 변경 시 Notion 문서 자동 업데이트
* 신규 엔드포인트 문서 초안 자동 생성

**승인 필요 작업:**

* `main` 병합
* Gradle 의존성 업데이트
* Docker/CI 구성 변경

**금지:**

* `.env`, 비밀번호, 키 등 민감 정보 수정

---

## ✍️ 커밋 서명 규칙

* 에이전트 커밋은 반드시 `codex/bin/codex-commit.sh` 스크립트를 사용한다.
* 작성자 정보는 `keyy-codex <keyy1315@naver.com>`으로 고정된다.
* 사용자는 일반 `git commit`으로 개인 계정 정보 유지 가능.

---

# 🧾 커밋 컨벤션 (Commit Convention)

모든 커밋 메시지는 **한국어**로 작성하며 다음 규칙을 따른다.

## ✔️ 형식

```
type: 변경 사항 요약 (50자 이내)

- 상세 변경 내용 (선택)
- 변경 이유, 영향도 설명 (선택)
```

## ✔️ 허용되는 type 목록

* **feat**: 새로운 기능 추가
* **fix**: 버그 수정
* **refactor**: 코드 개선 (기능 변화 없음)
* **docs**: 문서 변경
* **test**: 테스트 코드 추가/수정
* **infra**: 인프라, CI/CD, 스크립트 변경
* **style**: 코드 포맷팅, 불필요한 개행 등
* **chore**: 빌드 설정, 환경 구성 변경

## ✔️ 예시

```
feat: 친구 요청 API 추가

- 친구 요청 생성 엔드포인트 추가
- 검증 로직 및 예외 케이스 핸들링 포함
```

---

# 🔀 PR 컨벤션 (Pull Request Convention)

모든 PR은 다음 규칙을 따른다.

## ✔️ 제목 형식

* 반드시 **대괄호 Prefix** 사용

```
[PREFIX] 주요 변경 요약 (50자 이내)
```

### Prefix 종류

* **[API]** API 개발/수정
* **[FIX]** 버그 수정
* **[REFACTOR]** 리팩터링
* **[TEST]** 테스트 추가/수정
* **[INFRA]** 인프라/배포/CI
* **[DOCS]** 문서 업데이트

## ✔️ 본문 구성

* 신규 기능 요약
* 수정된 기능 상세 설명
* 테스트 내용/진행 결과
* 리팩터링한 경우 개선 포인트
* 필요 시 Bullet Point 활용

---

# 🧩 코딩 컨벤션 (Coding Convention)

Kotlin + Spring Boot 프로젝트 공통 규칙.

## ✔️ 일반 규칙

* 함수/클래스 이름은 **명확하고 직관적**으로 작성
* 불필요한 abbreviation(축약어) 금지
* Nullable 타입은 반드시 명확한 null 발생 가능성을 고려해 작성

## ✔️ Kotlin 스타일 규칙

* `val` 우선 사용, 필요한 경우에만 `var`
* 함수는 작은 단위로 분리하여 한 가지 역할만 수행
* Extension function 적극 활용
* 데이터 클래스는 불변성을 원칙으로 설계

## ✔️ Spring 컨벤션

* Controller는 입력 검증 + 서비스 호출만 담당
* Service는 트랜잭션과 비즈니스 로직을 담당
* Repository는 DB 접근만 담당
* 예외는 도메인 기반 커스텀 예외 사용

---

> 📘 문서 정보
> 프로젝트: rudeFriend-api
> MCP 구성: github, notionApi, context7
> Notion 문서 동기화 대상: rudeFriend API
> 마지막 업데이트: 2025-11-17
