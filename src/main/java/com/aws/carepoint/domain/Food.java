package com.aws.carepoint.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class Food {
    private int foodPk;
    private LocalDate selectDate;
    private String foodType;
    private int userPk;
}
