package com.aws.carepoint.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class FoodList {
    private int foodListPk;
    private String menu;
    private float protein;
    private float carbohydrate;
    private float fat;
    private int kcal;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private int foodPk;

    // JSON 변환을 위한 포맷 지정
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate selectDate;
    private String foodType; // 아침(B), 점심(L), 저녁(D) 구분
    private int userPk;
}