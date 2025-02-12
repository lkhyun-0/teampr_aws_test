package com.aws.carepoint.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class NoticeDto {
    private int articlePk;
    private String content;
    private String filename;
    private Integer viewcnt;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private int boardPk;
    private String title;
    private String boardType;
    private Integer userPk;
    private MultipartFile attachfile;
}
