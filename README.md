# 🩺 만성질환 환자를 위한 서비스, 케어포인트

----

![img.png](src/main/resources/static/images/img.png)

# 👋 프로젝트 소개 

----
- 케어포인트는 만성질환 환자들이 건강을 보다 편하고 효율적으로 관리 할 수 있도록 서비스를 제공합니다. 
- 사용자는 식단 관리부터 추천, 운동 목표를 설정하여 동기부여를 가질 수 있고, 병원 일정과 약 복용 관리, 알림 서비스 등을 제공 받을 수 있습니다. 

# 🎯 타겟 사용자 

----

- 만성질환을 앓고 있는 사용자
- 이미 만성질환 판정을 받은 사용자로 해당 질병을 꾸준히 기록, 관리가 필요한 사용자
- 만성질환이 의심되는 사용자
- 만성질환 판정을 받진 않았지만 자가진단체크리스트를 통하여 의심질환을 초기에 관리하고 싶은 사용자
- 만성질환 예방을 원하는 사용자



# 📅 개발 기간 

-----

- 2025.01.20 ~ 2024.02. 25

# 팀원 구성 

------

| 이경현                                                                          | 김이슬 | 이인아 | 김인서 |
|------------------------------------------------------------------------------|-|-|-|
| <img src="src/main/resources/static/images/경현.png" width="200" height="200"> | <img src="src/main/resources/static/images/이슬.png" width="200" height="200"> | <img src="src/main/resources/static/images/인아.png" width="200" height="200"> | <img src="src/main/resources/static/images/인서.png" width="200" height="200"> |
| [a01024705367@gmail.com](mailto:a01024705367@gmail.com)                      | [dltmf1045@gmail.com](mailto:dltmf1045@gmail.com) | [22na7592@gmail.com](mailto:22na7592@gmail.com) | [jjj99062633@gmail.com](mailto:jjj99062633@gmail.com) |



# 💻 기술 스택

----

- 개발환경 : Windows10, Linux, JDK 21, Spring Boot 3.3.4
- 개발도구 : GitHub, XAMPP, DBeaver, IntelliJ IDEA, SQLyog, VScode
- 개발언어 및 프레임워크 : Java, HTML, CSS, JavaScript, ThymeLeaf, Python, Spring Boot 3.3.4, Tailwind, jQuery, AJAX, Fast API
- DB : MySQL
- API : GOOGLE MAP API, Coolsms 문자 서비스 API, 카카오 로그인 API, 영양정보db API, openAi API, 운동정보


# ERD 구조

----


![img_1.png](src/main/resources/static/images/img_1.png)

# 🎨 figma

---

![img_2.png](src/main/resources/static/images/img_2.png)

<a href="https://www.figma.com/design/4Bwl2rMe00dvbafNZrW7QL/carepoint?node-id=0-1&p=f&t=OUwakinTflCVbA5T-0" target= "_blank">피그마 링크</a>


# 🙋‍♂️역할 분담

## 🐈 팀장 이경현

◆ 피그마 디자인

- 메인페이지
- 마이페이지
- 회원정보수정 팝업
- 회원가입 페이지
- 회원가입 약관동의 모달팝업
- 로그인 페이지
- 비밀번호 찾기 모달팝업
- 회원 상세정보 입력페이지
- 자가진단체크리스트 페이지
- 자가진단 결과 모달팝업

◆ 프론트엔드

- 메인페이지
- 마이페이지
- 회원정보수정 팝업
- 회원가입 페이지
- 회원가입 약관동의 모달팝업
- 로그인 페이지
- 비밀번호 찾기 모달팝업
- 회원 상세정보 입력페이지
- 자가진단체크리스트 페이지
- 자가진단 결과 모달팝업

◆ 백엔드

- 소셜로그인/회원가입
- 회원가입 수정 탈퇴
- 상세정보 입력
- 마이페이지 회원 상세정보 출력
- 마이페이지 회원 기록 그래프화
- 마이페이지 회원 작성 글목록(자유, 문의글)
- 일괄 문자전송
- 자가진단체크리스트 결과 출력


## 🦧 팀원 이인아 

◆ 피그마 디자인
- 운동 페이지 및 목표 작성 모달팝업 & 운동 종목 모달팝업
- 병원/약 일정 페이지 및 약 일정 상세 모달팝업 & 병원 일정 상세 모달팝업
- 자유 게시판(목록, 상세, 작성)
- Q&A 게시판(목록, 상세, 작성)
- 공지사항 게시판(목록, 상세, 작성)

◆ 프론트엔드

- 헤더풋터 
- 운동 메인 페이지
- 목표 작성 모달팝업
- 운동 종목 모달팝업
- 운동 일정 상세 모달팝업
- Q&A 게시판(목록, 상세, 작성)

◆ 백엔드

- Q&A 게시판 CRUD
- 운동 페이지 목표 저장
- 운동 페이지 목표 저장 시 해당 주에 기록되어있던 운동+수치 기록 반영
- 운동 페이지 수치값 저장
- 운동 페이지 데이터가 있는 날 수치 입력 시 기존 데이터 수정
- 운동 페이지 달성한 주간 목표 수 출력
- 운동 페이지 운동 기록한 횟수 출력
- 운동 페이지 운동명+MET지수 외부 API DB 저장
- 운동 페이지 운동 정보 저장
- 운동 페이지 저장된 운동 정보 가져와서 FullCalendar에 운동 이벤트 추가
- 운동 페이지 FullCalendar 운동 이벤트 클릭시 운동 정보 상세 모달 띄우고 모달에 있는 기록 삭제 버튼 클릭 시 운동 정보 삭제

## 🌿 팀원 김인서

◆ 프론트엔드

- 자유게시판 목록, 작성, 상세보기, 수정
- 근처 병원 찾기
- 병원/약 일정
- 병원/약 상세보기 팝업

◆ 백엔드

- 자유게시판 CRUD
- 자유게시판 목록
- 자유게시판 댓글 및 추천
- 병원/약 일정 페이지 일정작성
- 풀캘린더에 실시간 일정추가
- 일정 상세보기
- 일정 삭제
- 병원 위치 지도 표시
- 약 일정 원하는 색 지정
- 최근 일정 등록
- 내 위치 기반 주변 병원찾기
- 검색된 병원 찾기
- 병원 목록

◆ ERD
- ERD 17섹션 전체 담당 

## 🐹 팀원 김이슬

케어포인트 브랜드 로고 디자인 및 대표 컬러 선정

◆ 피그마 디자인
- 식단 기록
- 식단 목록
- 내 식단 분석
- 식단 검색 & 작성
- 식단 상세 및 수정
- 식단 추천 목표 
- 식단 추천 결과
- 가까운 병원 찾기

◆ 프론트엔드

- 식단 기록 페이지
- 식단 목록 페이지
- 내 식단 분석 페이지
- 식단 검색 & 작성 페이지
- 식단 상세 및 수정 페이지
- 식단 추천 목표 페이지
- 식단 추천 결과 페이지
- 로딩 애니메이션

◆ 백엔드

- 식단 아침, 점심, 저녁 작성
- 식단 수정 및 삭제
- 날짜별 식단 조회, 작성 여부에 따라 이모티콘 표시
- 식단 상세 내용 & 하루 식단 총 칼로리 계산
- 공공 API 연동하여 음식 데이터 검색
- 주간 영양 통계 분석 및 피드백 데이터 제공
- OpenAI API 활용한 식단 추천
- 공지사항 게시판(목록, 상세, 작성)

# ✅ 페이지별 기능

----

## 1. 메인 화면 

![img_3.png](src/main/resources/static/images/img_3.png)

- 메인페이지는 케어포인트 서비스를 쉽게 이용할 수 있도록 이미지별로 링크를 넣어 편하게 갈 수 있도록 설정 했습니다. 
- 대표 이미지에 마우스를 올리면 페이지별 안내를 간단하게 표시 했습니다.

![img_27.png](src/main/resources/static/images/img_27.png)

- 오른쪽 상단에서도 카테고리로 서비스를 한 눈에 볼 수 있습니다. 

## 2. 회원가입 

![img_4.png](src/main/resources/static/images/img_4.png)
- 회원가입 진행 전, 개인정보 수집과 이메일 수신 등의 약관 동의를 받습니다.

![img_5.png](src/main/resources/static/images/img_5.png)
- 기본 정보 입력후 회원가입을 합니다.

![img_45.png](src/main/resources/static/images/img_45.png)

- 회원가입 후 상세정보를 입력합니다.

## 3. 로그인 
![img_6.png](src/main/resources/static/images/img_6.png)

- 아이디와 비밀번호로 로그인을 합니다.

![img_7.png](src/main/resources/static/images/img_7.png)

- 카카오 계정으로도 로그인을 할 수 있습니다.

## 4. 비밀번호 찾기 
![img_23.png](src/main/resources/static/images/img_23.png)
- 가입 당시 작성했던 이름과 아이디, 전화번호로 비밀번호를 찾을 수 있습니다. 
 <br>임시 비밀번호는 등록한 전화번호로 sms 발송합니다.

## 5. 마이페이지

![img_8.png](src/main/resources/static/images/img_8.png)
- 마이페이지에서는 회원가입 당시에 작성했던 정보가 나타납니다. 
<br>왼쪽 동그라미를 클릭하면 내 프로필 사진을 등록할 수 있습니다. 

- 내가 작성한 최신글 5개와 운동 페이지에서 입력한 수치가 그래프로 나타납니다.

## 6. 회원정보 수정

![img_24.png](src/main/resources/static/images/img_24.png)

- 마이페이지에서 회원정보를 수정할 수 있습니다. 

## 7. 운동 메인페이지 

![img_26.png](src/main/resources/static/images/img_26.png)

- 메인페이지 기본 화면입니다. 

![img_44.png](src/main/resources/static/images/img_44.png)

- 목표 설정을 통해 주간 목표를 설정할 수 있습니다.

![img_10.png](src/main/resources/static/images/img_10.png)
- 운동 종류와 시간을 선택하여 캘린더에 등록할 수 있습니다.

![img_11.png](src/main/resources/static/images/img_11.png)

- 캘린더에서 해당 날짜를 누르면 상세내용을 볼 수 있습니다. 

![img_9.png](src/main/resources/static/images/img_9.png)

- 운동 기록 횟수나 달성한 주간 목표 수를 통해 사용자에게 성취감과 동기부여를 제공합니다.

## 8. 식단 기록 

![img_12.png](src/main/resources/static/images/img_12.png)

- 아침, 점심, 저녁을 선택하여 식단을 등록할 수 있습니다. 

![img_13.png](src/main/resources/static/images/img_13.png)

- 공공데이터 영양정보 api를 통해 음식을 검색하고 기록할 수 있습니다.

## 9. 식단 목록 & 상세

![img_14.png](src/main/resources/static/images/img_14.png)

- 사용자가 등록한 식단 목록을 이모티콘으로 표시하여 알려줍니다.

![img_15.png](src/main/resources/static/images/img_15.png)

- 상세 내용에서 하루 식단 총 칼로리를 볼 수 있고 수정, 삭제가 가능합니다.

## 10. 내 식단 분석

![img_16.png](src/main/resources/static/images/img_16.png)

- 회원의 식단 데이터를 주간 단위로 분석하여 칼로리, 탄수화물, 단백질, 지방 섭취량을 그래프로 시각화하여 나타냅니다.
- 성별 & 연령별 적정 섭취량을 반영하여 피드백을 제공합니다. 

## 11. 식단 추천

![img_17.png](src/main/resources/static/images/img_17.png)

- AI 기반 사용자 목표(체중 감량, 근육 증가 등)에 따른 맞춤형 식단을 추천합니다.

![img_18.png](src/main/resources/static/images/img_18.png)

- 아침, 점심, 저녁 1,900 kcal 기준으로 식단과 영양정보를 제공합니다. (다이어트 1,300 kcal)
- 식단 추가를 눌러 더 다양한 식단을 추천 받을 수 있습니다. 

## 12. 병원/약 일정관리 

![img_20.png](src/main/resources/static/images/img_20.png)

- 병원 일정에서 내원 날짜, 시간을 작성하고, 구글 맵 api를 연동하여 위치 등록을 할 수 있습니다. 
- 최근 등록한 일정 정보를 자동으로 가져와 표시할 수 있습니다.
- 약 복용 일정은 원하는 색상을 골라 캘린더 커스터마이징할 수 있습니다.

![img_19.png](src/main/resources/static/images/img_19.png)

- 캘린더를 통해 내가 등록한 병원 일정과 약 복용 기록을 확인할 수 있습니다. 

![img_21.png](src/main/resources/static/images/img_21.png)
![img_22.png](src/main/resources/static/images/img_22.png)

- 캘린더 해당 날짜를 클릭하면 병원일정과 약 복용 일정을 상세하게 볼 수 있습니다.

## 13. 질병 자가진단
![img_29.png](src/main/resources/static/images/img_29.png)
![img_31.png](src/main/resources/static/images/img_31.png)

- 당뇨, 고혈압을 자가 진단할 수 있는 체크리스트를 제공하여 n개 이상일 경우 가까운 병원에서 검진 추천하고 있습니다.
  
![img_46.png](src/main/resources/static/images/img_46.png)

- 비만은 몸무게와 체중을 입력하면 결과를 바로 확인할 수 있습니다. 

## 14. 가까운 병원 찾기

![img_34.png](src/main/resources/static/images/img_34.png)

- 구글 맵 API를 이용하여 검색한 장소에 3km이내에 있는 병원들을 리스트로 보여줍니다. 
- 병원 마커를 클릭하면 해당 병원의 정보를 볼 수 있습니다. 

## 15. 데일리 문자 알림

![img.png](src/main/resources/static/images/문자알림.png)

- 매일 오후 7시 30분에 회원들에게 문자 알림을 보내서 회원의 꾸준한 서비스 이용을 유도합니다.  

## 16. 자유게시판

![img_37.png](src/main/resources/static/images/img_37.png)

- 자유게시판 목록을 일반적인 게시판 페이지네이션 형식으로 보여줍니다.

![img_38.png](src/main/resources/static/images/img_38.png)

- 상세내용에서는 댓글 기능과 추천기능을 제공합니다.

## 17. Q&A 게시판

![img_47.png](src/main/resources/static/images/img_47.png)

- 궁금한 점이 있을 때 Q&A 게시판에서 질문할 수 있습니다. 
- 내용은 작성자와 관리자만 볼 수 있습니다.

## 18. 공지사항 게시판

![img_43.png](src/main/resources/static/images/img_43.png)

- 운영자가 작성한 공지사항의 내용을 확인할 수 있습니다.

