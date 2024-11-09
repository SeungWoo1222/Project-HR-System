# HR 솔루션 웹 애플리케이션 - HaruHaru
## 프로젝트 설명
정회산은 취업 준비를 위한 포트폴리오 제작을 목표로 하였고, 진승우와 협업하여 HR 시스템과 업무 관리 기능을 결합한 프로젝트를 개발하게 되었습니다. 
이 프로젝트는 회산의 기획과 설계를 중심으로, 승우가 다양한 기술을 실습하며 실무 역량을 쌓아가는 경험을 통해 두 사람 모두 실질적인 성장을 이루는 것을 목표로 하고 있습니다.

**기능 소개를 제외한 프로젝트 개요와 상세 정보는 [노션 문서](https://awake-reaction-86b.notion.site/HaruHaru-Project-with-160975df7b7b4aa483d2d3598cbd7ee2?pvs=4)를 참고해 주세요. 자세한 내용은 링크에서 확인하실 수 있습니다.**

## 프로젝트 기능 소개 
## 정회산 담당 기능
### 1. 방문자 회원가입 및 로그인
#### 방문자 회원가입 : 방문자 회원가입을 통해 손쉽게 사원 계정을 생성할 수 있습니다.
#### 로그인 : 사원 아이디와 비밀번호을 통해 로그인할 수 있습니다.
- 아이디를 기억하는 옵션을 통해 더 편리하게 로그인할 수 있습니다.

![join-and-login](https://github.com/user-attachments/assets/5f8d7dfb-acaa-422c-aa30-aa883ace94e6)
#### 보안을 위해 처음 로그인하시는 경우 비밀번호 변경 창이 자동으로 표시됩니다.
![first-password-change](https://github.com/user-attachments/assets/e3e1b685-0253-47bd-9764-46b881cf683e)
### 2. 근태 관리
#### 출근 퇴근 기록
- 출퇴근 창을 통해 출퇴근 시간과 이번 주 근무 시간을 간편하게 확인하고, 출퇴근 기록을 손쉽게 등록할 수 있습니다. 
- 정규 출근 시간 이후에 출근할 경우 자동으로 지각 처리됩니다. 
- 정규 퇴근 시간 이전에 퇴근할 경우 자동으로 조퇴 처리됩니다. 
- 정규 퇴근 시간 이후에 퇴근할 경우 자동으로 초과근무가 적용되어 처리됩니다.
- 영업일 오전 9시에 사원이 휴가 상태일 경우 자동으로 휴가 처리됩니다.
- 반차일에는 정규 출근 시간이 조정되어 지각 및 조퇴 처리에서 제외됩니다.

![commute](https://github.com/user-attachments/assets/a80a0df7-0a22-442f-85ee-3682b578a960)

- 영업일 오후 6시에 미출근 상태인 경우 자동으로 결근 처리됩니다.

<img width="45%" alt="스크린샷 2024-11-06 오후 6 45 33" src="https://github.com/user-attachments/assets/219a435f-240e-4e82-a3a2-cfe40d335b3e">
<img width="45%" alt="스크린샷 2024-11-06 오후 6 45 56" src="https://github.com/user-attachments/assets/fe135cea-993c-4c7a-a8dc-b05912203119">
<img width="1166" alt="스크린샷 2024-11-06 오후 6 29 58" src="https://github.com/user-attachments/assets/260caeef-e1f9-41d9-aa05-547c1d3529c5">

#### 조퇴 기록 : 조퇴창을 통해 조퇴 사유를 등록 후 조퇴할수 있습니다. 
![early-leave](https://github.com/user-attachments/assets/87b8d230-a828-47dd-9ca5-0776ca6534cf)
#### 나의 근태 현황을 월별로 조회할 수 있으며, 일별 근태 기록을 클릭하여 해당 일의 상세 정보를 확인할 수 있습니다.
<img width="1278" alt="스크린샷 2024-11-06 오후 2 26 41" src="https://github.com/user-attachments/assets/bd2ce81f-b35f-4e66-a6a8-63a4612f47da"/>

#### 오늘의 근태 현황(인사부서 권한 필요), 부서 관리 - 오늘의 근태 현황(관리자 권한 필요)
- 오늘의 근태 현황을 통계표와 도넛 차트를 통해 조회할 수 있습니다.
- 정상 출근, 근태 불량(지각, 조퇴, 결근), 출장 및 휴가 사원을 자세하게 조회할 수 있습니다.

#### 근태 목록(인사부서 권한 필요), 부서 관리 - 근태 관리(관리자 권한 필요)
- 모든 근태 기록을 조회할 수 있습니다.
- 부서별, 근태 상태별, 월별, 검색어를 통한 필터링 기능을 제공합니다.
- 부서 관리 - 근태 관리에서 부서원들의 근태를 조회할 수 있습니다.

![attendance-list](https://github.com/user-attachments/assets/21c6b694-f394-41f2-b70e-1221641c4b72)

- 행을 클릭하면 근태 기록의 상세 조회 및 수정이 가능합니다.
- 정규 퇴근 시간 이후로 퇴근 시간을 수정 시 자동으로 초과근무가 적용되어 처리됩니다.

![attendance-detail-update](https://github.com/user-attachments/assets/950d0243-3c20-4614-950e-95d49689fbc0)

#### 초과근무 목록(인사부서 권한 필요)
- 모든 초과근무 기록을 조회할 수 있으며, 기록을 수정 및 삭제할 수 있습니다.
- 부서별, 월별, 검색어를 통한 필터링 기능을 제공합니다.

![overtime-update](https://github.com/user-attachments/assets/6266cabd-093a-4d7a-91b3-d7999ce5d407)

#### 공휴일 관리(인사부서 권한 필요)
- 모든 공휴일을 조회할 수 있으며, 인사부서 관리자 권한이 있다면 등록, 수정, 삭제할 수 있습니다.
- 공휴일이 등록되면 모든 사원의 캘린더에 자동으로 표시됩니다.

![holiday](https://github.com/user-attachments/assets/27540968-d953-43cf-9790-81a637ae6c59)

### 3. 사원 관리(인사부서 권한 필요)
#### 사원 목록 조회
- 모든 사원 정보를 조회할 수 있습니다.
- 사원 행을 선택하면 사원의 상세 조회 및 추가 작업을 진행할 수 있습니다.
- 부서별, 월별, 재직 상태, 검색어를 통한 필터링 기능을 제공합니다.
- 사원 정보를 상세조회하면 사원 정보 수정, 퇴사 정보 수정(재직상태가 퇴사일 경우), 재직 상태 변경, 승진 처리, 계정 잠금/해제 등 작업을 할 수 있습니다.

![employee-list](https://github.com/user-attachments/assets/b71c49ba-6cfa-4b07-8b5c-94ade3780fd8)

#### 사원 정보 추가 작업
- 사원의 재직 상태를 변경할 수 있습니다.

![status-update](https://github.com/user-attachments/assets/f314fd57-e5e7-4cc8-83f7-d1984b8e9e43)

- 사원의 직급을 한 단계 승진 처리할 수 있습니다.

![promotion](https://github.com/user-attachments/assets/129a4eee-c869-4de1-b27b-54198a43ebb8)

- 사원의 계정을 잠금 또는 잠금해제 할 수 있습니다.

![account-lock](https://github.com/user-attachments/assets/0a1a3cf6-2e59-41d3-a026-75bab8f62412)
![account-unlock](https://github.com/user-attachments/assets/211245af-052d-44d9-b5c7-a75b7d80fd16)

#### 사원 정보 수정 : 사원 정보를 수정할 수 있습니다.
<img width="1264" alt="스크린샷 2024-11-06 오후 7 17 10" src="https://github.com/user-attachments/assets/9ca4b6e9-980d-4ac7-84af-2e87311a193b">

#### 사원 등록 : 신규 사원의 정보를 등록할 수 있습니다.
![employee-register](https://github.com/user-attachments/assets/febc71bc-4fc0-498a-8fe3-87f97efef2c5)

#### 퇴사 사원 관리
- 퇴사 예정인 사원, 퇴사 처리된 사원, 퇴사 후 1년이 경과된 사원을 조회할 수 있습니다. 
- 퇴사 예정 사원을 퇴사처리 할 수 있습니다.
- 퇴사 후 1년이 경과된 사원의 정보를 영구 삭제할 수 있습니다.

![resignation](https://github.com/user-attachments/assets/adb626cb-fdf5-451d-814d-a260d2308752)

#### 퇴사 정보 수정 : 사원의 퇴사 정보와 첨부된 문서를 수정할 수 있습니다.
<img width="1285" alt="스크린샷 2024-11-06 오후 11 11 23" src="https://github.com/user-attachments/assets/66973fee-30ca-43a8-9a2e-22fe0f94183c">

#### 내 정보 - 내 정보 관리
- 내 정보를 조회할 수 있으며 내 정보를 수정할 수 있습니다.

![myinfo](https://github.com/user-attachments/assets/703ee518-d3f2-4888-8c1e-54da9e44c6d6)

- 내 비밀번호의 강도와 마지막 비밀번호 변경 날짜를 조회할 수 있으며 비밀번호를 변경할 수 있습니다.
- 스프링 시큐리티의 암호화 기능을 활용하여 비밀번호 변경 시 보안을 강화하고, 사용자 계정을 안전하게 보호합니다.
- 비밀번호를 변경한 지 3개월이 경과 후 로그인 시 비밀번호 변경창을 표시합니다.

![password](https://github.com/user-attachments/assets/34740f00-a970-4f37-b21a-61fdf90acb56)

### 4. 휴가 관리
#### 휴가 신청
- 본인 휴가를 신청할 수 있습니다.
- 휴가를 연차로 신청하면 주말과 공휴일은 자동으로 계산하여 연차에서 제외합니다.
- 반차 신청 시 0.5 연차로 자동 계산됩니다.
- 연차와 반차를 제외하곤 연차를 사용하지 않습니다.
- 연차가 없다면 연차 휴가를 사용할 수 없습니다.

![vacation-request](https://github.com/user-attachments/assets/c296dc9c-f2b6-402c-b18c-38775ed6283e)

#### 휴가 내역
- 나의 휴가 내역과 잔여 연차를 조회할 수 있습니다.
- 날짜 필터링 기능을 제공합니다.

<img width="1082" alt="스크린샷 2024-11-07 오전 12 04 22" src="https://github.com/user-attachments/assets/3f376f9e-ea0a-44bc-bae9-f4ddfb8528d4">

#### 휴가 목록(인사부서 권한 필요)
- 모든 사원의 휴가 내역을 조회할 수 있습니다.
- 각 행을 선택하면 사원의 자세한 휴가 신청을 조회할 수 있으며 수정 및 삭제할 수 있습니다.
- 부서별, 승인상태, 날짜, 검색어 필터링을 제공합니다.

<img width="1273" alt="스크린샷 2024-11-07 오전 12 14 20" src="https://github.com/user-attachments/assets/fed70dc3-2dda-4e65-9ecb-a558dd48d86b">

#### 휴가 등록(인사부서 권한 필요)
- 휴가 신청 기능을 포함합니다.
- 타인 휴가를 등록할 수 있습니다.

![vacation-request](https://github.com/user-attachments/assets/6a36eef9-cf33-46f7-8e68-112b42ec9307)

#### 부서 관리 - 휴가 관리(관리자 권한 필요)
- 부서원의 휴가 내역을 조회할 수 있습니다.
- 각 행을 선택하면 사원의 자세한 휴가 신청을 조회할 수 있으며 수정, 삭제, 승인, 거절 작업을 진행할 수 있습니다.
- 연차 휴가라면 승인처리 시 해당 사원의 연차가 차감됩니다.
- 승인상태, 날짜 필터링을 제공합니다.

![vacation-department](https://github.com/user-attachments/assets/b4794c9c-4a6a-4510-90c8-f840d8e2f247)

### 5. 급여 관리
#### 급여정보 목록
- 모든 사원의 급여정보들과 급여 정보 미등록 사원들을 조회할 수 있습니다.
- 사용 여부, 검색어 필터링을 제공합니다.

![salary-list](https://github.com/user-attachments/assets/2228af66-4804-4bb8-aaf6-076341dab124)

#### 급여정보 등록
- 사원의 급여 정보를 등록할 수 있습니다.
- 급여정보 목록에서 급여정보가 미등록된 특정 사원의 급여 정보를 등록할 수 있습니다.
- 급여정보 등록 시 사원의 급여가 존재한다면 현재 급여정보를 정지 후 새로 등록할 수 있습니다.

![salary-add](https://github.com/user-attachments/assets/658c47c7-36a1-40e0-96db-3d95d1983af0)

#### 급여 지급 내역
- 모든 사원의 급여 지급 내역을 조회할 수 있습니다.
- 부서별, 월별 검색어 필터링을 제공합니다.
- 급여가 미지급된 사원들의 선택 버튼이 활성화 되고 지급된 사원들은 비활성화됩니다.<br>선택 버튼이 활성화된 사원들에겐 급여를 지급할 수 있습니다.

#### 급여명세서 목록
- 모든 사원의 급여명세서를 조회할 수 있습니다.
- 월별, 부서별, 검색어 필터링을 제공합니다.

#### 급여 및 공제 비율
- 급여와 공제의 비율을 조회하고 수정할 수 있습니다.

#### 내 급여 관리
- 내 급여 정보 내역을 확일할 수 있으며 현재 급여 정보를 수정할 수 있습니다.
- 나의 급여명세서 기록을 확인할 수 있습니다.

### 6. 설문 조사

### 7. 알림 수신함

## 진승우 담당 기능