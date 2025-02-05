package com.aws.carepoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailDto {
    private int detail_pk;
    private int age;
    private String weight;
    private String height;
    private String sick_type;
    private String sick_detail;
    private int smoke;
    private String exercise_cnt;
    private int drink;
    private String photo;
    private  int target_count;
    private String reg_date;
    private String update_date;
    private int user_pk;
}