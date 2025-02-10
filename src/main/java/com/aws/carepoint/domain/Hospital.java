package com.aws.carepoint.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class Hospital {
    private int hospitalPk;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String placeId;
}
