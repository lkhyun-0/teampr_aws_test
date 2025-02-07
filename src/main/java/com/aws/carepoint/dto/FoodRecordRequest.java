package com.aws.carepoint.dto;

import lombok.Data;
import java.util.List;

@Data
public class FoodRecordRequest {
    private String selectDate;
    private String foodType;
    private int userPk;
    private List<FoodDto> foodList;
}
