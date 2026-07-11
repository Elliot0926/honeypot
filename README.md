<div align="center">

# 🍯 Honeypot

**꿀 발린 매물은 지도 위에서 찾는다.**
부산 아파트 실거래가 지도 서비스

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-000000?style=flat-square)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
![Kakao Map](https://img.shields.io/badge/Kakao_Map-FFCD00?style=flat-square&logo=kakao&logoColor=black)

`KH아카데미 부산지원` · `세미 프로젝트` · 개인 프로젝트

</div>

---

## 🔍 왜 만들었나

외부 API를 직접 다뤄보는 경험이 필요했습니다. 마침 부동산은 누구나 한 번쯤 관심을 갖는 분야이기도 하고,
나중에 앱이나 웹 서비스를 제대로 만들어보기 전에 Open API 연동 경험을 미리 쌓아두고 싶어서
세미 프로젝트 주제로 골랐습니다.

기존 실거래가 서비스는 텍스트 목록 위주라 "이 동네가 요즘 오르는지 내리는지"를 감으로 잡기 어렵습니다.
Honeypot은 [아실(asil.kr)](https://asil.kr)의 UX를 참고해, 아파트 단지를 지도 위 마커로 띄우고
클릭 한 번으로 매매·전월세 실거래 내역과 가격 흐름을 볼 수 있게 만들었습니다.

- 📍 부산 14개 구·군 전체 커버
- 📅 2023.01 ~ 2026.05 실거래가 데이터
- 🧭 국토교통부 공동주택 실거래가 Open API + 카카오 로컬 API(좌표 변환)

---

## 🏘️ 지도 위에서 할 수 있는 것

| 기능 | 내용 |
|---|---|
| 단지 마커 | 카카오맵 기반 마커 + 클러스터링, 화면 범위(bounds) 내 단지만 동적 조회 |
| 실거래가 조회 | 단지 클릭 → 매매/전월세 목록 + Chart.js 가격 추이 차트 |
| 최근 거래현황 | 매매 + 전월세 통합 최신순 슬라이딩 패널 (최대 200건) |
| 즐겨찾기 | 관심 단지 등록/해제, 마이페이지에서 관리 |
| 데이터 수집 관리자 | 구·군 / 기간 단위 또는 **전체 일괄** 실거래가 수집 파이프라인 |

---

## 🧭 데이터가 흘러가는 길

```
[Browser] ── Kakao Map SDK / Chart.js
     │
     ▼
[Spring Boot (Thymeleaf)] ── MyBatis ──▶ [MySQL]
     │
     ├─ MainController    지도 조회 / 실거래가 조회 / 즐겨찾기
     ├─ MemberController  회원가입 / 로그인 / 마이페이지
     └─ AdminController    공공데이터 수집 파이프라인
              │
              ▼
   국토교통부 실거래가 Open API  /  카카오 로컬 API
```

Presentation(Thymeleaf) - Business(Service) - Data(MyBatis+MySQL) 3-tier 구조이며,
Docker Compose로 `backend` + `db` 컨테이너를 함께 기동해 AWS EC2에 배포했습니다.

---

## 🧰 기술 스택

| 구분 | 스택 |
|---|---|
| 언어 | ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black) |
| 백엔드 | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-000000?style=flat-square) |
| 프론트엔드 | ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white) ![Chart.js](https://img.shields.io/badge/Chart.js-FF6384?style=flat-square&logo=chartdotjs&logoColor=white) |
| DB | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white) |
| 외부 API | ![Kakao Map](https://img.shields.io/badge/Kakao_Map-FFCD00?style=flat-square&logo=kakao&logoColor=black) ![공공데이터포털](https://img.shields.io/badge/공공데이터포털-실거래가_API-003478?style=flat-square) |
| 인프라 | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) ![AWS](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonaws&logoColor=white) |
| IDE | ![Eclipse](https://img.shields.io/badge/Eclipse-2C2255?style=flat-square&logo=eclipseide&logoColor=white) ![VS Code](https://img.shields.io/badge/VS_Code-007ACC?style=flat-square&logo=visualstudiocode&logoColor=white) |

---

## 📍 화면 미리보기

<details>
<summary>🗺️ 메인 지도</summary>

카카오맵 기반 단지 마커 + 클러스터링, 최근 거래현황 슬라이딩 패널

</details>

<details>
<summary>🔑 로그인 · 회원가입</summary>

이메일 기반 회원가입 / 로그인 / 로그아웃 (세션 인증)

</details>

<details>
<summary>💰 단지 상세 · 실거래가</summary>

매매/전월세 실거래가 목록, Chart.js 가격 추이, 즐겨찾기 토글

</details>

<details>
<summary>🗂️ 마이페이지 · 관리자 페이지</summary>

즐겨찾기 관리 / 지역·기간별 실거래가 데이터 수집

</details>

---

## ▶️ 실행 방법

```bash
# Docker Compose (권장)
git clone https://github.com/<your-id>/honeypot.git
cd honeypot
docker compose up -d --build
# → http://localhost:8080

# 로컬 직접 실행 (MySQL 8.0 선행 필요)
./mvnw spring-boot:run
```

> ⚠️ DB 계정 정보와 카카오 Map App Key는 실제 배포 시 환경변수로 분리 권장

---

## 📁 프로젝트 구조

```
honeypot/
├── src/main/java/org/cloud/honeypot/
│   ├── controller/   # Main / Member / Admin
│   ├── service/      # Collect / Favorite / Member
│   ├── mapper/         # MyBatis Mapper 인터페이스
│   └── dto/             # AptComplex / SaleTransaction / RentTransaction / Member / Favorite
├── src/main/resources/
│   ├── mapper/          # MyBatis XML
│   └── templates/       # index, member/*, admin/*
├── Dockerfile
└── docker-compose.yml
```

---

## 🧪 트러블슈팅 로그

**"어느 날 갑자기 파일이 안 열려요."**

별도 백업 없이 단일 워크스페이스에서만 작업하던 중, 저장 중 비정상 종료로 추정되는 이슈로
일부 소스 파일이 손상되는 사고가 있었습니다.

**대응**: Eclipse **Local History** 기능으로 손상 이전 시점의 파일을 복구했습니다.

```
파일 우클릭 → Replace With → Local History...
→ 손상 이전 시점 선택 후 복원
```

이후 부산 14개 구 × 40개월치 실거래가를 API Rate Limit 안에서 안정적으로 수집하기 위해,
구·기간 단위로 나눠 배치 수집하는 기능(`/admin/collect/sale/all-region` 등)을 별도로 구현했습니다.

---

## 🏆 얻은 것

- 기획부터 배포까지 혼자 끝까지 완주한 경험
- 국토교통부 공공데이터 Open API + 카카오 로컬 API 연동 경험
- 대용량 데이터를 안전하게 나눠 수집하는 배치 설계 감각
- Local History로 사고를 되돌려본 실전 장애 대응 경험

## 🔮 다음 업데이트 할 예정 목록

- [ ] 모바일 앱으로 확장 제작
- [ ] 웹페이지 UI 개선 및 최신 트렌드 반영
- [ ] 지역별 평균가 · 변동률 통계 대시보드
- [ ] 매물 필터(면적 / 가격대 / 거래유형) 세분화
- [ ] 반응형 UI (모바일 대응)

---

## 🧑‍💻 만든 사람

| 이름 | 역할 |
|---|---|
| (김효준) | 기획 · 전체 개발 (Backend/Frontend/Infra) |
