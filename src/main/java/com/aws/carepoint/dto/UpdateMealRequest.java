package com.aws.carepoint.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateMealRequest {
    private int userPk; // 유저 ID
    private String selectDate; // 수정할 날짜
    private String foodType; // 아침(B), 점심(L), 저녁(D)
    private List<FoodDto> foodList; // 수정할 음식 리스트
}
