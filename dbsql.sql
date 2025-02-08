DROP DATABASE IF EXISTS `carepoint`;
CREATE DATABASE `carepoint`;
USE `carepoint`;


CREATE TABLE users (
                       user_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                       auth_level SMALLINT(2) NOT NULL, -- 권한 레벨 (3=일반, 7=관리자)
                       social_login_status TINYINT(1) NOT NULL, -- 소셜 여부 (0=일반로그인, 1=소셜로그인)
                       userid VARCHAR(50) UNIQUE NOT NULL, -- 회원아이디
                       userpwd VARCHAR(150) NOT NULL, -- 회원비밀번호
                       username VARCHAR(50) NOT NULL, -- 회원이름
                       usernick VARCHAR(50) NOT NULL, -- 회원닉네임
                       phone VARCHAR(20) NOT NULL, -- 전화번호
                       email VARCHAR(100) UNIQUE NOT NULL, -- 이메일 (이름 수정)
                       join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 가입일
                       update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                       del_status TINYINT(1) DEFAULT 0 NOT NULL, -- 탈퇴 여부 (0=탈퇴 전, 1=탈퇴 후)
                       delDate DATETIME DEFAULT NULL -- 탈퇴일 (NULL 허용)
);

CREATE TABLE user_detail (
                             detail_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                             age INT NOT NULL, -- 나이
                             weight CHAR(5) NOT NULL, -- 몸무게
                             height CHAR(10) NOT NULL, -- 키
                             sick_type VARCHAR(10) NOT NULL, -- 질병종류(당뇨, 비만, 고혈압)
                             sick_detail VARCHAR(50) NOT NULL, -- 질병 상세종류
                             smoke TINYINT(1) NOT NULL, -- 흡연 여부 ( 0= 비흡연자 , 1= 흡연자)
                             exercise_cnt VARCHAR(20) NOT NULL, -- 운동빈도 (아예안함, 주1~2회, 주3~4회, 주 5회 이상)
                             drink TINYINT(1) NOT NULL, -- 음주 여부 ( 0= 비음주자 , 1= 음주자)
                             photo VARCHAR(100), -- 프로필사진
                             target_count INT, -- 주간달성횟수
                             reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                             update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                             user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                             CONSTRAINT fk_user_detail_user FOREIGN KEY (user_pk) REFERENCES users (user_pk)
);


CREATE TABLE board (
                       board_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                       board_type CHAR(1) NOT NULL, -- 게시판종류 (Q : Q&A, F : Free, N : Notice)
                       reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                       update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                       del_status TINYINT(1) DEFAULT 0 NOT NULL -- 삭제 여부 (0=삭제 전, 1=삭제 후)
);

CREATE TABLE article (
                         article_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                         title VARCHAR(100) NOT NULL, -- 제목
                         content TEXT NOT NULL, -- 내용
                         filename VARCHAR(200), -- 파일이름
                         recom INT DEFAULT 0, -- 추천수
                         viewcnt INT DEFAULT 0, -- 조회수
                         origin_num INT DEFAULT 0, -- 원글번호
                         level_ INT DEFAULT 0, -- 답변레벨
                         reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                         update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                         del_status TINYINT(1) DEFAULT 0 NOT NULL, -- 삭제 여부 (0=삭제 전, 1=삭제 후)
                         user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                         board_pk INT NOT NULL, -- 외래 키 (board 테이블의 board_pk 참조)
                         CONSTRAINT fk_article_users FOREIGN KEY (user_pk) REFERENCES users (user_pk), -- 외래 키 설정
                         CONSTRAINT fk_article_board FOREIGN KEY (board_pk) REFERENCES board (board_pk) -- 외래 키 설정
);

CREATE TABLE recommend (
                           recom_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                           recom_status TINYINT(1) DEFAULT 0 NOT NULL, -- 추천 여부 ( 0= 추천 전, 1= 추천 후)
                           reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                           update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                           user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                           board_pk INT NOT NULL, -- 외래 키 (board 테이블의 board_pk 참조)
                           CONSTRAINT fk_recommend_users FOREIGN KEY (user_pk) REFERENCES users (user_pk), -- 외래 키 설정
                           CONSTRAINT fk_recommend_board FOREIGN KEY (board_pk) REFERENCES board (board_pk) -- 외래 키 설정
);

CREATE TABLE comments (
                          comment_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                          content TEXT NOT NULL, -- 댓글 내용
                          del_status TINYINT(1) DEFAULT 0 NOT NULL, -- 삭제 여부 (0=삭제 전, 1=삭제 후)
                          reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                          user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                          board_pk INT NOT NULL, -- 외래 키 (board 테이블의 board_pk 참조)
                          CONSTRAINT fk_comments_users FOREIGN KEY (user_pk) REFERENCES users (user_pk), -- 외래 키 설정
                          CONSTRAINT fk_comments_board FOREIGN KEY (board_pk) REFERENCES board (board_pk) -- 외래 키 설정
);

CREATE TABLE food (
                      food_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                      select_date DATE NOT NULL, -- 선택일
                      foodtype CHAR(1) NOT NULL, -- 식사종류 (B : 아침, L : 점심, D : 저녁)
                      user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                      CONSTRAINT fk_food_users FOREIGN KEY (user_pk) REFERENCES users (user_pk) -- 외래 키 설정
);

CREATE TABLE foodlist (
                          foodlist_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                          menu VARCHAR(100) NOT NULL, -- 음식메뉴
                          protein FLOAT, -- 단백질
                          carbohydrate FLOAT, -- 탄수화물
                          fat FLOAT, -- 지방
                          kcal INT NOT NULL, -- 칼로리
                          reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                          update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                          food_pk INT NOT NULL, -- 외래 키 (food 테이블의 food_pk 참조)
                          CONSTRAINT fk_foodlist_food FOREIGN KEY (food_pk) REFERENCES food (food_pk) -- 외래 키 설정
);

CREATE TABLE food_API (
                          food_api_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                          menu VARCHAR(100) NOT NULL, -- 음식메뉴
                          kcal INT NOT NULL, -- 칼로리
                          protein FLOAT, -- 단백질
                          carbohydrate FLOAT, -- 탄수화물
                          fat FLOAT -- 지방
);

CREATE TABLE graph (
                       graph_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                       weight INT DEFAULT 0, -- 체중
                       blood_press INT DEFAULT 0, -- 혈압
                       blood_sugar INT DEFAULT 0, -- 혈당
                       reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                       user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                       CONSTRAINT fk_graph_users FOREIGN KEY (user_pk) REFERENCES users (user_pk) -- 외래 키 설정
);

CREATE TABLE target (
                        target_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                        exercise_target INT DEFAULT 0, -- 운동 목표 횟수
                        exercise_count INT DEFAULT 0, -- 운동 횟수
                        value_target INT DEFAULT 0, -- 수치 목표 횟수
                        value_count INT DEFAULT 0, -- 수치 횟수
                        kcal_target INT DEFAULT 0, -- 칼로리 목표
                        kcal_sum INT DEFAULT 0, -- 칼로리 소모량'
                        user_pk INT NOT NULL -- 외래 키 (users 테이블의 user_pk 참조)
);

CREATE TABLE exercise (
                          exercise_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                          exercise_name VARCHAR(50) NOT NULL, -- 운동 종목
                          kcal INT DEFAULT 0, -- 칼로리
                          MET INT DEFAULT 0, -- MET 지수
                          value_status TINYINT(1) DEFAULT 0 NOT NULL, -- 수치기록 여부 ( 0= 수치기록 전, 1= 수치기록 후)
                          reg_date DATE NOT NULL, -- 작성일
                          `hour` INT DEFAULT 0, -- 시간
                          `minute` INT DEFAULT 0, -- 분
                          exercise_img VARCHAR(100) NOT NULL, -- 이미지
                          user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                          target_pk INT, -- 외래 키 (target 테이블의 target_pk 참조)
                          CONSTRAINT fk_exercise_users FOREIGN KEY (user_pk) REFERENCES users (user_pk), -- 외래 키 설정
                          CONSTRAINT fk_exercise_target FOREIGN KEY (target_pk) REFERENCES target (target_pk) -- 외래 키 설정
);

CREATE TABLE exercise_API (
                              exercise_api_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                              exercise_name VARCHAR(50) NOT NULL, -- 운동 종목
                              MET INT DEFAULT 0 NOT NULL, -- MET 지수
                              exercise_img VARCHAR(100) NOT NULL -- 이미지
);

CREATE TABLE medicine (
                          medicine_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                          medicine_name VARCHAR(50) NOT NULL, -- 약 이름
                          medicine_type SMALLINT(2) NOT NULL, -- 약 유형 (1 = 경구약, 2 = 주사)
                          start_date DATE NOT NULL, -- 시작일
                          end_date DATE NOT NULL, -- 끝일
                          reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                          update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                          user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                          CONSTRAINT fk_medicine_users FOREIGN KEY (user_pk) REFERENCES users (user_pk) -- 외래 키 설정
);

CREATE TABLE medicine_plan (
                               medicine_plan_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                               `hour` INT DEFAULT 0, -- 시간
                               `minute` INT DEFAULT 0, -- 분
                               medicine_pk INT NOT NULL, -- 외래 키 (medicine 테이블의 medicine_pk 참조)
                               CONSTRAINT fk_medicine_plan_medicine FOREIGN KEY (medicine_pk) REFERENCES medicine (medicine_pk) -- 외래 키 설정
);

CREATE TABLE hospital (
                          hospital_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                          check_status TINYINT(1) DEFAULT 0 NOT NULL, -- 즐겨찾기 등록 여부 ( 0= 등록 전, 1= 등록 후)
                          hospital_name VARCHAR(50) NOT NULL, -- 병원 이름
                          latitude VARCHAR(20), -- 위도(x)
                          longitude VARCHAR(20), -- 경도(y)
                          adress TEXT, -- 병원 주소
                          reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                          update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                          user_pk INT NOT NULL, -- 외래 키 (users 테이블의 user_pk 참조)
                          CONSTRAINT fk_hospital_users FOREIGN KEY (user_pk) REFERENCES users (user_pk) -- 외래 키 설정
);

CREATE TABLE hospital_plan (
                               hospital_plan_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                               select_date DATE NOT NULL, -- 선택일
                               `hour` INT DEFAULT 0, -- 시간
                               `minute` INT DEFAULT 0, -- 분
                               reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 작성일
                               update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                               hospital_pk INT NOT NULL, -- 외래 키 (hospital 테이블의 hospital_pk 참조)
                               CONSTRAINT fk_hospital_plan_hospital FOREIGN KEY (hospital_pk) REFERENCES hospital (hospital_pk) -- 외래 키 설정
);



INSERT INTO users (auth_level, social_login_status, userid, userpwd, username, usernick, phone, email)
VALUES (3, 0, 'testuser', 'testpassword', '홍길동', '길동이', '010-1234-5678', 'testuser@example.com');


SELECT * FROM users;

SELECT * FROM food;
SELECT * FROM foodlist;

DELETE FROM foodlist
WHERE food_pk IN (
    SELECT food_pk FROM food
    WHERE select_date = '2025-02-07' AND foodtype = 'L'
);


DELETE FROM food
WHERE select_date = '2025-02-07' AND foodtype = 'L';

SET FOREIGN_KEY_CHECKS = 0; -- 외래 키 제약 해제
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS board;
SET FOREIGN_KEY_CHECKS = 1; -- 외래 키 제약 다시 활성화
