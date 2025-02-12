package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class FoodListDto { //클라이언트와 데이터를 주고받을 DTO
    private String selectDate;
    private int breakfast;  // 1 = 식사 기록 있음, 0 = 없음
    private int lunch;
    private int dinner;
}
