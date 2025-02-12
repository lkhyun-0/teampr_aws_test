package com.aws.carepoint.dto;

import lombok.Data;

@Data
public class FoodDto { //클라이언트와 데이터를 주고받을 DTO
    private Integer foodListPk; // 기존 데이터면 foodListPk가 있음, 새로운 데이터면 null
    private String menu;
    private int kcal; // int로 변경 (칼로리는 정수)
    private float protein; // float로 변경 (소수 가능)
    private float fat;
    private float carbohydrate;
    private int amount;  //추가 (사용자가 입력한 g 단위)
}


