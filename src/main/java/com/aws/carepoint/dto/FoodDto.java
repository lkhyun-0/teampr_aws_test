package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class FoodDto { //클라이언트와 데이터를 주고받을 DTO
    private String menu;
    private int kcal; // int로 변경 (칼로리는 정수)
    private float protein; // float로 변경 (소수 가능)
    private float fat;
    private float carbohydrate;

}
