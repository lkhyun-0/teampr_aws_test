package com.aws.carepoint.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class QnaDto {
    private int articlePk;
    private String content;
    private String filename;
    private Integer recom;
    private Integer viewcnt;
    private Integer originNum;
    private Integer level;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private String userNick;
    private int boardPk;
    private String title;
    private String boardType;
    private Integer userPk;
    private MultipartFile attachfile;
}
