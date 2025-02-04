package com.aws.carepoint.domain;

import lombok.Data;

public class ExerciseApiEntity {
    private Long id; // 자동 증가 ID
    private String exerciseName;
    private double metValue;

    public Long getId() {
        return id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public double getMetValue() {
        return metValue;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setMetValue(double metValue) {
        this.metValue = metValue;
    }
}
