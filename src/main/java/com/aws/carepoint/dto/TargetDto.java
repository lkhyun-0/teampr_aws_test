package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class TargetDto {

    private int targetPk;
    private int exerciseTarget;
    private int exerciseCount;
    private int valueTarget;
    private int valueCount;
    private int kcalTarget;
    private int kcalSum;
    private String startDate;
    private String endDate;
    private int userPk;

    // getter and setter methods
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
