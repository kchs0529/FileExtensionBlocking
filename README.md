# FileExtensionBlocking

Spring Boot + MySQL + jQuery 기반의 **파일 확장자 차단 서비스**

---

## 과제 요구사항

1. 고정 확장자
   - exe, sh 등 기본 확장자 리스트 제공
   - 기본 상태는 unCheck
   - check/uncheck 상태는 DB에 저장되며 새로고침 시 유지됨

2. 확장자 입력 제약
   - 최대 입력 길이 20자 제한

3. 커스텀 확장자
   - 추가 시 DB에 저장, 화면에 즉시 반영
   - 최대 200개까지 추가 가능
   - 각 확장자 옆 `X` 버튼으로 DB에서 삭제

---

## 기술 스택

* **Backend**

  * Java 17
  * Spring Boot 3.4.9
  * Spring Data JPA, Hibernate
  * MySQL
* **Frontend**

  * jQuery
  * Static HTML / CSS / JS
* **Build**

  * Maven

---
## 화면 
<img width="925" height="648" alt="image" src="https://github.com/user-attachments/assets/8f7c23b1-9572-48bd-852b-ecd625b52b69" />

---

## 주요 기능

### 고정 확장자 (FixedExtension)

* 서버 시작 시 초기 데이터 삽입 (`exe`, `bat`, `cmd`, `js` 등)
* 사용자가 **체크박스 on/off**로 차단 여부 변경
* REST API

  * `GET /api/fixed` : 고정 확장자 목록 조회
  * `PATCH /api/fixed` : `{name, checked}` 상태 변경

### 커스텀 확장자 (CustomExtension)

* 사용자가 입력한 확장자 추가/삭제 가능
* 최대 200개 제한
* 확장자 이름이 영어와 숫자 외 다른 문자를 포함할 경우 영어,숫자를 제외한 것은 무시하고 등록
* 확장자 이름이 한글, 특수문자만으로 이루어져 있을 경우 거부 (`확장자 이름이 올바르지 않습니다`)
* REST API

  * `GET /api/custom` : 커스텀 확장자 전체 목록 조회
  * `POST /api/custom` : 새 확장자 추가
    → 응답: `"success"`, `"중복된 확장자입니다"`, `"200개를 초과하였습니다"`, `"확장자 이름이 올바르지 않습니다"`
  * `DELETE /api/custom/{id}` : 단일 객체 삭제
  * `DELETE /api/custom/deleteAll` : 전체 삭제

### 업로드 차단

* `POST /api/upload` : 파일을 선택하여 확장자를 검사(실제 업로드 x)

---

## 프로젝트 구조

```
src
 ├─ main/java/com/flow/FileExtensionBlocking
 │   ├─ controller/{ExtensionController, UploadContriller}.java
 │   ├─ domain/{FixedExtension, CustomExtension}.java
 │   ├─ repo/{FixedExtensionRepo, CustomExtensionRepo}.java
 │   └─ service/ExtensionService.java
 └─ resources/static
     ├─ index.html
     ├─ css/style.css
     └─ js/app.js
```

---
## ☁️ 배포 방법 (Railway)

본 프로젝트는 **Railway**를 사용하여 배포했습니다.

1. **GitHub Repository 연결**
   - Railway 프로젝트를 생성하고, GitHub 레포 (`FileExtensionBlocking`)를 연결하여 CI/CD 파이프라인을 구성했습니다.
   - master 브랜치 push 시 자동 배포되도록 설정했습니다.

2. **MySQL 플러그인 추가**
   - Railway에서 MySQL 서비스 인스턴스를 추가했습니다.
   - Railway가 제공하는 `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` 환경변수를 Spring Boot `application.yml`에서 참조하도록 구성했습니다.

3. **환경 변수 설정**
   - `application.yml`은 로컬 실행 시에는 로컬 DB를,  
     Railway 배포 시에는 환경변수 기반 DB 연결을 사용하도록 설정했습니다.
   - 예시:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
         username: ${MYSQLUSER}
         password: ${MYSQLPASSWORD}
     ```

4. **배포 결과**
   - Railway가 자동으로 빌드/실행 (Java 21 런타임)
   - Public Networking → Railway에서 제공하는 도메인으로 접근 가능
   - **배포 URL: https://fileextensionblocking-production.up.railway.app/**

---

## 🚀 로컬 vs 배포 환경

- **로컬 개발**  
  - Java 17, 로컬 MySQL 사용  
  - `mvn spring-boot:run` 으로 실행

- **배포 환경 (Railway)**  
  - GitHub Actions 없이 Railway 자동 빌드/배포  
  - DB는 Railway MySQL 인스턴스 사용  
  - 환경변수 기반으로 동작

---

## 테스트 시나리오

* 고정 확장자 `exe` 체크 후 `.exe` 파일 업로드 → **차단**
* 커스텀 확장자 `zip` 추가 → `.zip` 파일 업로드 → **차단**
* 커스텀 확장자 삭제 → `.zip` 파일 업로드 → **허용**
* 200개 초과 시 → `"200개를 초과하였습니다"`
* 한글 입력 시 → `"확장자 이름이 올바르지 않습니다"`
* 영어 숫자 한글 특수문자 입력시 ex).exe1아} → '.','아','}' 무시하고 exe1만 입력

---

## 파일 업로드에 대한 설명

본 과제에서는 **실제 파일 저장/업로드 기능은 구현하지 않았습니다.**

이유는 다음과 같습니다:

1. **과제 요구사항 범위**  
   - 요구사항은 “파일 확장자를 검사하여 차단 여부를 판단”하는 것이 핵심이었고,  
     파일 자체 저장은 범위 외라고 판단했습니다.

2. **배포 환경 제약 (Railway)**  
   - Railway 무료 환경에서는 파일 시스템 저장소가 **휘발성**이어서,  
     업로드 파일을 저장해도 세션이 종료되면 사라집니다.  
   - 지속 저장이 필요하다면 별도의 Object Storage(AWS S3 등)가 필요합니다.

