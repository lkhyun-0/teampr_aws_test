package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExerciseApiDto {

    private String exerciseName;
    private String metValue;

}
