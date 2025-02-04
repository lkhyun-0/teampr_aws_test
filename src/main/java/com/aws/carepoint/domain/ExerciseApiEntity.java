package com.aws.carepoint.domain;

import lombok.Data;

@Data
public class ExerciseApiEntity {

    private Integer exerciseId;
    private String exerciseName;
    private Integer met;
}
