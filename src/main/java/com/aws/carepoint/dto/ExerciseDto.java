package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ExerciseDto {
    @JsonProperty("운동명")
    private String exerciseName;

    @JsonProperty("MET 계수")
    private String metValue;

    public String getExerciseName() {
        return exerciseName;
    }

    public String getMetValue() {
        return metValue;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setMetValue(String metValue) {
        this.metValue = metValue;
    }
}
