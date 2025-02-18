package com.aws.carepoint.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Data
public class Food { // 공공데이터 api용 domain
    private int foodPk;
    private LocalDate selectDate;
    private String foodType;
    private int userPk;
}
