package com.aws.carepoint.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QnaEntity {
    private Long articlePk;
    private String content;
    private String filename;
    private Integer recom;
    private Integer viewcnt;
    private Integer originNum;
    private Integer level;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private Long boardPk;
}
