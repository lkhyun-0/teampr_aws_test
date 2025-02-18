package com.aws.carepoint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private int commentPk;
    private String content;
    private String userNick;
    private LocalDateTime regDate;
    private int count; // 총 댓글 갯수
    private int articlePk;
    private Integer userPk;
}
