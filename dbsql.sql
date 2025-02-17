DROP DATABASE IF EXISTS `carepoint`;
CREATE DATABASE `carepoint`;
USE `carepoint`;

CREATE TABLE users
(
    user_pk             INT AUTO_INCREMENT PRIMARY KEY       NOT NULL,                    -- 기본키
    auth_level          SMALLINT(2)                          NOT NULL,                    -- 권한 레벨 (3=일반, 7=관리자)
    social_login_status TINYINT(1)                           NOT NULL,                    -- 소셜 여부 (0=일반로그인, 1=소셜로그인)
    userid              VARCHAR(50) UNIQUE                   NOT NULL,                    -- 회원아이디
    userpwd             VARCHAR(150)                         NOT NULL,                    -- 회원비밀번호
    username            VARCHAR(50)                          NOT NULL,                    -- 회원이름
    usernick            VARCHAR(50)                          NOT NULL,                    -- 회원닉네임
    phone               VARCHAR(20)                          NOT NULL,                    -- 전화번호
    email               VARCHAR(100) UNIQUE                  NOT NULL,                    -- 이메일 (이름 수정)
    join_date           TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL,                    -- 가입일
    update_date         TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_status          TINYINT(1) DEFAULT 0                 NOT NULL,                    -- 탈퇴 여부 (0=탈퇴 전, 1=탈퇴 후)
    delDate             DATETIME   DEFAULT NULL                                           -- 탈퇴일 (NULL 허용)
);

CREATE TABLE user_detail
(
    detail_pk          INT AUTO_INCREMENT PRIMARY KEY      NOT NULL,                    -- 기본키
    age                INT                                 NOT NULL,                    -- 나이
    weight             CHAR(5)                             NOT NULL,                    -- 몸무게
    height             CHAR(10)                            NOT NULL,                    -- 키
    sick_type          VARCHAR(10)                         NOT NULL,                    -- 질병종류(당뇨, 비만, 고혈압)
    sick_detail        VARCHAR(50)                         NOT NULL,                    -- 질병 상세종류
    smoke              TINYINT(1)                          NOT NULL,                    -- 흡연 여부 ( 0= 비흡연자 , 1= 흡연자)
    exercise_cnt       VARCHAR(20)                         NOT NULL,                    -- 운동빈도 (아예안함, 주1~2회, 주3~4회, 주 5회 이상)
    drink              TINYINT(1)                          NOT NULL,                    -- 음주 여부 ( 0= 비음주자 , 1= 음주자)
    gender             CHAR(1)                             NOT NULL,                    -- 성별
    photo              VARCHAR(100),                                                    -- 프로필사진
    target_count       INT,                                                             -- 주간달성횟수
    last_target_update DATE      DEFAULT NULL,
    reg_date           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,                    -- 작성일
    update_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    user_pk            INT                                 NOT NULL,                    -- 외래 키 (users 테이블의 user_pk 참조)
    CONSTRAINT fk_user_detail_user FOREIGN KEY (user_pk) REFERENCES users (user_pk)
);

CREATE TABLE board
(
    board_pk    INT AUTO_INCREMENT PRIMARY KEY       NOT NULL,                    -- 기본키
    board_type  CHAR(1)                              NOT NULL,                    -- 게시판종류 (Q : Q&A, F : Free, N : Notice)
    reg_date    TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL,                    -- 작성일
    update_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    del_status  TINYINT(1) DEFAULT 0                 NOT NULL                     -- 삭제 여부 (0=삭제 전, 1=삭제 후)
);

CREATE TABLE article
(
    article_pk  INT AUTO_INCREMENT PRIMARY KEY       NOT NULL,                     -- 기본키
    title       VARCHAR(100)                         NOT NULL,                     -- 제목
    content     TEXT                                 NOT NULL,                     -- 내용
    filename    VARCHAR(200),                                                      -- 추천수
    viewcnt     INT        DEFAULT 0,                                              -- 조회수
    origin_num  INT        DEFAULT 0,                                              -- 원글번호
    level_      INT        DEFAULT 0,                                              -- 답변레벨
    reg_date    TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL,                     -- 작성일
    update_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 수정일
    del_status  TINYINT(1) DEFAULT 0                 NOT NULL,                     -- 삭제 여부 (0=삭제 전, 1=삭제 후)
    user_pk     INT                                  NOT NULL,                     -- 외래 키 (users 테이블의 user_pk 참조)
    board_pk    INT                                  NOT NULL,                     -- 외래 키 (board 테이블의 board_pk 참조)
    CONSTRAINT fk_article_users FOREIGN KEY (user_pk) REFERENCES users (user_pk),  -- 외래 키 설정
    CONSTRAINT fk_article_board FOREIGN KEY (board_pk) REFERENCES board (board_pk) -- 외래 키 설정
);

CREATE TABLE recommend
(
    recom_pk     INT AUTO_INCREMENT PRIMARY KEY       NOT NULL,                              -- 기본키
    recom_status TINYINT(1) DEFAULT 0                 NOT NULL,                              -- 추천 여부 ( 0= 추천 전, 1= 추천 후)
    reg_date     TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL,                              -- 작성일
    update_date  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,           -- 수정일
    user_pk      INT                                  NOT NULL,                              -- 외래 키 (users 테이블의 user_pk 참조)
    article_pk   INT                                  NOT NULL,                              -- 외래 키 (board 테이블의 board_pk 참조)
    CONSTRAINT fk_recommend_users FOREIGN KEY (user_pk) REFERENCES users (user_pk),          -- 외래 키 설정
    CONSTRAINT fk_recommend_article FOREIGN KEY (article_pk) REFERENCES article (article_pk) -- 외래 키 설정
);

CREATE TABLE comments
(
    comment_pk  INT AUTO_INCREMENT PRIMARY KEY       NOT NULL,                              -- 기본키
    content     TEXT                                 NOT NULL,                              -- 댓글 내용
    del_status  TINYINT(1) DEFAULT 0                 NOT NULL,                              -- 삭제 여부 (0=삭제 전, 1=삭제 후)
    reg_date    TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL,                              -- 작성일
    update_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,           -- 수정일
    user_pk     INT                                  NOT NULL,                              -- 외래 키 (users 테이블의 user_pk 참조)
    article_pk  INT                                  NOT NULL,                              -- 외래 키 (board 테이블의 board_pk 참조)
    CONSTRAINT fk_comments_users FOREIGN KEY (user_pk) REFERENCES users (user_pk),          -- 외래 키 설정
    CONSTRAINT fk_comments_article FOREIGN KEY (article_pk) REFERENCES article (article_pk) -- 외래 키 설정
);

CREATE TABLE food
(
    food_pk     INT AUTO_INCREMENT PRIMARY KEY NOT NULL,                      -- 기본키
    select_date DATE                           NOT NULL,                      -- 선택일
    foodtype    CHAR(1)                        NOT NULL,                      -- 식사종류 (B : 아침, L : 점심, D : 저녁)
    user_pk     INT                            NOT NULL,                      -- 외래 키 (users 테이블의 user_pk 참조)
    CONSTRAINT fk_food_users FOREIGN KEY (user_pk) REFERENCES users (user_pk) -- 외래 키 설정
);

CREATE TABLE foodlist
(
    foodlist_pk  INT AUTO_INCREMENT PRIMARY KEY      NOT NULL,                    -- 기본키
    menu         VARCHAR(100)                        NOT NULL,                    -- 음식메뉴
    protein      FLOAT,                                                           -- 단백질
    carbohydrate FLOAT,                                                           -- 탄수화물
    fat          FLOAT,                                                           -- 지방
    kcal         INT                                 NOT NULL,                    -- 칼로리
    amount       INT       DEFAULT 100               NOT NULL,                    -- 100g
    reg_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,                    -- 작성일
    update_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    food_pk      INT                                 NOT NULL,                    -- 외래 키 (food 테이블의 food_pk 참조)
    CONSTRAINT fk_foodlist_food FOREIGN KEY (food_pk) REFERENCES food (food_pk)   -- 외래 키 설정
);


CREATE TABLE target
(
    target_pk       INT AUTO_INCREMENT PRIMARY KEY NOT NULL,                    -- 기본키
    exercise_target INT DEFAULT 0,                                              -- 운동 목표 횟수
    exercise_count  INT DEFAULT 0,                                              -- 운동 횟수
    value_target    INT DEFAULT 0,                                              -- 수치 목표 횟수
    value_count     INT DEFAULT 0,                                              -- 수치 횟수
    kcal_target     INT DEFAULT 0,                                              -- 칼로리 목표
    kcal_sum        INT DEFAULT 0,                                              -- 칼로리 소모량
    start_date      DATE                           NOT NULL,                    -- 목표 시작 날짜
    end_date        DATE                           NOT NULL,                    -- 목표 종료 날짜
    user_pk         INT                            NOT NULL,                    -- 외래 키 (users 테이블의 user_pk 참조)
    CONSTRAINT fk_target_users FOREIGN KEY (user_pk) REFERENCES users (user_pk) -- 외래 키 설정
);

CREATE TABLE exercise
(
    exercise_pk   INT AUTO_INCREMENT PRIMARY KEY NOT NULL,                              -- 기본키
    exercise_name VARCHAR(50)                    NOT NULL,                              -- 운동 종목
    kcal          INT   DEFAULT 0,                                                      -- 칼로리
    MET           FLOAT DEFAULT 0,                                                      -- MET 지수
    reg_date      DATE                           NOT NULL,                              -- 작성일
    `hour`        INT   DEFAULT 0,                                                      -- 시간
    `minute`      INT   DEFAULT 0,                                                      -- 분
    user_pk       INT                            NOT NULL,                              -- 외래 키 (users 테이블의 user_pk 참조)
    target_pk     INT,                                                                  -- 외래 키 (target 테이블의 target_pk 참조)
    CONSTRAINT fk_exercise_users FOREIGN KEY (user_pk) REFERENCES users (user_pk),      -- 외래 키 설정
    CONSTRAINT fk_exercise_target FOREIGN KEY (target_pk) REFERENCES target (target_pk) -- 외래 키 설정
);

CREATE TABLE exercise_API
(
    exercise_api_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
    exercise_name   VARCHAR(50)                    NOT NULL, -- 운동 종목
    MET             FLOAT DEFAULT 0                NOT NULL, -- MET 지수
    exercise_img    VARCHAR(100)                   NULL      -- 이미지
);

CREATE TABLE graph
(
    graph_pk    INT AUTO_INCREMENT PRIMARY KEY NOT NULL,                             -- 기본키
    weight      INT DEFAULT 0,                                                       -- 체중
    blood_press INT DEFAULT 0,                                                       -- 혈압
    blood_sugar INT DEFAULT 0,                                                       -- 혈당
    reg_date    DATE                           NOT NULL,                             -- 작성일
    user_pk     INT                            NOT NULL,                             -- 외래 키 (users 테이블의 user_pk 참조)
    target_pk   INT,                                                                 -- 외래 키 (target 테이블의 target_pk 참조)
    CONSTRAINT fk_graph_users FOREIGN KEY (user_pk) REFERENCES users (user_pk),      -- 외래 키 설정
    CONSTRAINT fk_graph_target FOREIGN KEY (target_pk) REFERENCES target (target_pk) -- 외래 키 설정
);

CREATE TABLE medicine
(
    medicine_pk   INT AUTO_INCREMENT PRIMARY KEY      NOT NULL,                    -- 기본키
    medicine_name VARCHAR(50)                         NOT NULL,                    -- 약 이름
    medicine_type SMALLINT(2)                         NOT NULL,                    -- 약 유형 (1 = 경구약, 2 = 주사)
    reg_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,                    -- 작성일
    update_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    user_pk       INT                                 NOT NULL,                    -- 외래 키 (users 테이블의 user_pk 참조)
    CONSTRAINT fk_medicine_users FOREIGN KEY (user_pk) REFERENCES users (user_pk)  -- 외래 키 설정
);

CREATE TABLE medicine_plan
(
    medicine_plan_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL,                                        -- 기본키
    start_date       DATE                           NOT NULL,                                        -- 시작일
    end_date         DATE                           NOT NULL,                                        -- 선택일
    select_time      TIME                           NOT NULL,                                        -- 선택시간
    color            VARCHAR(15)                    NOT NULL,
    medicine_pk      INT                            NOT NULL,                                        -- 외래 키 (medicine 테이블의 medicine_pk 참조)
    CONSTRAINT fk_medicine_plan_medicine FOREIGN KEY (medicine_pk) REFERENCES medicine (medicine_pk) -- 외래 키 설정
);

CREATE TABLE hospital
(
    hospital_pk   INT AUTO_INCREMENT PRIMARY KEY      NOT NULL,                    -- 기본키
    hospital_name VARCHAR(50)                         NOT NULL,                    -- 병원 이름
    latitude      VARCHAR(20),                                                     -- 위도(x)
    longitude     VARCHAR(20),                                                     -- 경도(y)
    address       TEXT,                                                            -- 병원 주소
    reg_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,                    -- 작성일
    update_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    user_pk       INT                                 NOT NULL,                    -- 외래 키 (users 테이블의 user_pk 참조)
    CONSTRAINT fk_hospital_users FOREIGN KEY (user_pk) REFERENCES users (user_pk)  -- 외래 키 설정
);

CREATE TABLE hospital_plan
(
    hospital_plan_pk INT AUTO_INCREMENT PRIMARY KEY      NOT NULL,                                   -- 기본키
    select_date      DATE                                NOT NULL,                                   -- 선택일
    select_time      TIME                                NOT NULL,                                   -- 분
    reg_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,                                   -- 작성일
    update_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,                -- 수정일
    hospital_pk      INT                                 NOT NULL,                                   -- 외래 키 (hospital 테이블의 hospital_pk 참조)
    CONSTRAINT fk_hospital_plan_hospital FOREIGN KEY (hospital_pk) REFERENCES hospital (hospital_pk) -- 외래 키 설정
);

CREATE TABLE hospital_api
(
    hospital_api_pk INT AUTO_INCREMENT PRIMARY KEY,
    hospital_name   VARCHAR(255) NOT NULL,
    address         VARCHAR(500),
    latitude        DECIMAL(10, 8),
    longitude       DECIMAL(11, 8),
    place_id        VARCHAR(255) UNIQUE
);

======= users 관리자데이터 =======
INSERT INTO users (auth_level, social_login_status, userid, userpwd, username, usernick, phone, email)
VALUES (7, 0, '22na', '1234', '이인아', '22na', '010-1111-1111', '22na@naver.com');

INSERT INTO users (auth_level, social_login_status, userid, userpwd, username, usernick, phone, email)
VALUES (3, 0, 'hong', '1234', '홍길동', 'hong', '010-2222-2222', 'hong@naver.com');

INSERT INTO users (auth_level, social_login_status, userid, userpwd, username, usernick, phone, email)
VALUES (3, 0, 'pupu', '1234', '김푸푸', 'pupu', '010-3333-3333', 'pupu@gmail.com');

INSERT INTO users (auth_level, social_login_status, userid, userpwd, username, usernick, phone, email)
VALUES (3, 0, 'mimi', '1234', '최미미', 'mimi', '010-4444-4444', 'mimi@gmail.com');

SELECT *
FROM users;

======= board 시작데이터 =======
INSERT INTO board (board_type)
VALUES ('N');
INSERT INTO board (board_type)
VALUES ('F');
INSERT INTO board (board_type)
VALUES ('Q');

SELECT *
FROM board;

DELETE
FROM board
ALTER TABLE board
    AUTO_INCREMENT = 1;

========= article 프로시저 =========
DELIMITER $$

CREATE PROCEDURE InsertArticleProc(
    IN PARAM_NAME VARCHAR(20) -- IN 키워드 추가 (SQLyog 호환)
)
BEGIN
    DECLARE i INT DEFAULT 1; -- 초기값 설정 방법 변경

    WHILE i <= 300
        DO
            INSERT INTO article (title, content, user_pk, board_pk)
            VALUES (CONCAT(PARAM_NAME, i), CONCAT('게시글 내용', i), 2, 2);

            SET i = i + 1;
        END WHILE;
END $$

DELIMITER ;

========= 프로시저 호출 =========
CALL InsertArticleProc('게시글 제목');

SELECT *
FROM article;



========= 운동 목표 트리거 =========
DELIMITER //

CREATE TRIGGER trg_update_exercise_target
    AFTER INSERT
    ON target
    FOR EACH ROW
BEGIN
    -- 목표가 추가될 때, 기존에 등록된 운동 데이터의 target_pk를 업데이트
    UPDATE exercise
    SET target_pk = NEW.target_pk
    WHERE user_pk = NEW.user_pk
      AND reg_date BETWEEN NEW.start_date AND NEW.end_date
      AND target_pk IS NULL;
END //

DELIMITER ;

========= 목표 카운트 이벤트 =========
SET GLOBAL event_scheduler = ON;

DELIMITER //

CREATE EVENT update_exercise_count_event
    ON SCHEDULE EVERY 5 SECOND
    DO
    BEGIN
        -- 목표 설정 기간 내의 운동 횟수 업데이트
        UPDATE target t
        SET exercise_count = (SELECT COUNT(*)
                              FROM exercise e
                              WHERE e.user_pk = t.user_pk
                                AND e.reg_date BETWEEN t.start_date AND t.end_date
                                AND e.target_pk = t.target_pk),
            kcal_sum       = (SELECT COALESCE(SUM(e.kcal), 0)
                              FROM exercise e
                              WHERE e.user_pk = t.user_pk
                                AND e.reg_date BETWEEN t.start_date AND t.end_date
                                AND e.target_pk = t.target_pk);
    END //

DELIMITER ;

========= 그래프 목표 트리거 =========
DELIMITER //

CREATE TRIGGER trg_update_graph_target
    AFTER INSERT
    ON target
    FOR EACH ROW
BEGIN
    -- 목표가 추가될 때, 기존에 등록된 그래프 데이터의 target_pk를 업데이트
    UPDATE graph
    SET target_pk = NEW.target_pk
    WHERE user_pk = NEW.user_pk
      AND DATE(reg_date) BETWEEN NEW.start_date AND NEW.end_date
      AND target_pk IS NULL;
END //

DELIMITER ;

========= 목표 카운트 이벤트 =========
SET GLOBAL event_scheduler = ON;

DELIMITER //

CREATE EVENT update_graph_count_event
    ON SCHEDULE EVERY 5 SECOND
    DO
    BEGIN
        -- 목표 설정 기간 내의 그래프 기록 횟수 업데이트
        UPDATE target t
        SET value_count = (SELECT COUNT(*)
                           FROM graph g
                           WHERE g.user_pk = t.user_pk
                             AND DATE(g.reg_date) BETWEEN t.start_date AND t.end_date
                             AND g.target_pk = t.target_pk);
    END //

DELIMITER ;
