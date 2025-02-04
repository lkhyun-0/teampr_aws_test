DROP DATABASE IF EXISTS `carepoint`;
CREATE DATABASE `carepoint`;
USE `carepoint`;

CREATE TABLE exercise_API (
                              exercise_api_pk INT AUTO_INCREMENT PRIMARY KEY NOT NULL, -- 기본키
                              exercise_name VARCHAR(50) NOT NULL, -- 운동 종목
                              MET INT DEFAULT 0 NOT NULL -- MET 지수
);
