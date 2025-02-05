package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class FoodDTO {
    private String menu;
    private int kcal; // 🔥 int로 변경 (칼로리는 정수)
    private float protein; // 🔥 float로 변경 (소수 가능)
    private float fat;
    private float carbohydrate;

}
