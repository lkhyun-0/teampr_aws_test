package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class FoodListDto {
    private String selectDate;
    private int breakfast;  // 1 = 식사 기록 있음, 0 = 없음
    private int lunch;
    private int dinner;
}
