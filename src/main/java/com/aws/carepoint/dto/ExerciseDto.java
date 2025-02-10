package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class ExerciseDto {
    private int exercisePk;
    private String exerciseName;
    private int kcal;
    private double metValue;
    private int valueStatus;
    private String regDate;
    private int hour;
    private int minute;
    private int userPk;
    private int targetPk;
}
