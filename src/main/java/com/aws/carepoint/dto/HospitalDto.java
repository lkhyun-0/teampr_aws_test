package com.aws.carepoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class HospitalDto {
    private int hospitalPk;
    private String hospitalName;  // ✅ (프론트엔드 "name"과 일치)
    private String latitude;
    private String longitude;

    private String address;  // ✅ "adress" → "address"로 수정
    private String selectTime;  // ✅ (프론트엔드 "time"과 일치)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ✅ JSON 날짜 변환 설정
    private Date selectDate;  // ✅ (프론트엔드 "date"와 일치)

    private Integer userPk;
}

