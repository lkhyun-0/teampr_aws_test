package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ExerciseApiResponse {
    @JsonProperty("currentCount")
    private int currentCount;

    @JsonProperty("data")
    private List<ExerciseDto> data;

    public int getCurrentCount() {
        return currentCount;
    }

    public List<ExerciseDto> getData() {
        return data;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setData(List<ExerciseDto> data) {
        this.data = data;
    }
}


