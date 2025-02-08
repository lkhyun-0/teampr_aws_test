package com.aws.carepoint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FreeDto {
    private int articlePk;
    private String title;
    private String content;
    private String filename;
    private Integer recom;
    private Integer viewcnt;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private String boardType;
    private String userNick;
    private int boardPk;
    private Integer userPk;
}
