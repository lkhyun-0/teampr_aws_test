package com.aws.carepoint.dto;


import lombok.Data;

@Data
public class WeeklyFoodStatsDto { // 식단분석 dto
    private String selectDate;
    private int totalCalories;
    private float totalProtein;
    private float totalCarbohydrates;
    private float totalFat;
}
