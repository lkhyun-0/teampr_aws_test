package com.aws.carepoint.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeEntity {
    private Long articlePk;
    private String content;
    private String filename;
    private Integer viewcnt;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private Long boardPk;
}
