package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ExerciseDto {
    @JsonProperty("운동명")
    private String exerciseName;

    @JsonProperty("MET 계수")
    private String metValue;

}
