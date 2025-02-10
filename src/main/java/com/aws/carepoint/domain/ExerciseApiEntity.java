package com.aws.carepoint.domain;

import lombok.Data;

@Data
public class ExerciseApiEntity {
    private Long id; // 자동 증가 ID
    private String exerciseName;
    private double metValue;
}
