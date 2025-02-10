package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class RecommendDto {
    private int count; // 총 추천 횟수
    private Integer recomStatus;
    private int articlePk;
    private Integer userPk;
}
