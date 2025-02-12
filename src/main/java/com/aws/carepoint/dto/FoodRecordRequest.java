package com.aws.carepoint.dto;

import lombok.Data;
import java.util.List;

@Data
public class FoodRecordRequest { // 식단 기록 dto
    private String selectDate;
    private String foodType;
    private int userPk;
    private List<FoodDto> foodList;
}
