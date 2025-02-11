package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HospitalDto {
    private int hospitalPk;
    private String hospitalName;
    private String latitude;
    private String longitude;
    private String address;
    private String selectTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ✅ JSON 날짜 변환 설정
    private LocalDate selectDate;

    private Integer userPk;
}

