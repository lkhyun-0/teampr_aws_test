package com.aws.carepoint.dto;

import lombok.Data;
import java.sql.Timestamp; // 변경

@Data
public class GraphDto {
    private int graphPk;
    private int weight;
    private int bloodPress;
    private int bloodSugar;
    private Timestamp regDate; // 변경됨
    private int userPk;
}