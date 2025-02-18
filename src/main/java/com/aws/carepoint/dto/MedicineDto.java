package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicineDto {
    private int medicinePk;
    private String medicineName;
    private int medicineType;
    private String color;
    private String selectTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ✅ JSON 날짜 변환 설정
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ✅ JSON 날짜 변환 설정
    private LocalDate endDate;

    private Integer userPk;
}

