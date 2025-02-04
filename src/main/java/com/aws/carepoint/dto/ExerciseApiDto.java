package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class ExerciseApiDto {
    private String exerciseName;
    private Integer met;

    // Getter/Setter (Lombok @Data 써도 가능)
}
