package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class GraphDto {
    private int graphPk;
    private int weight;
    private int bloodPress;
    private int bloodSugar;
    private int userPk;
    private String regDate;
}
