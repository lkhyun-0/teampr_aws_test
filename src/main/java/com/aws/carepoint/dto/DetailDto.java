package com.aws.carepoint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailDto {
    private int detailPk;
    @NotNull(message = "나이는 필수 입력값입니다.")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    private int age;
    @NotNull(message = "체중은 필수 입력값입니다.")  // ✅ null 허용 안 하지만 빈 문자열 허용
    private String weight;
    @NotNull(message = "신장은 필수 입력값입니다.") // ✅ null 허용 안 하지만 빈 문자열 허용
    private String height;
    @NotNull(message = "성별은 필수 입력값입니다.") // ✅ null 허용 안 하지만 빈 문자열 허용
    private String gender;
    @NotBlank(message = "질병 정보는 필수 입력값입니다.") // ✅ 필수 입력
    private String sickType;
    @NotBlank(message = "질병의 정도 정보는 필수 입력값입니다.") // ✅ 필수 입력
    private String sickDetail;
    private int smoke;
    @NotBlank(message = "운동 빈도의 정보는 필수 입력값입니다.") // ✅ 필수 입력
    private String exerciseCnt;
    private int drink;
    private String photo;
    private int targetCount;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private int userPk;
}

