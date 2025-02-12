package com.aws.carepoint.controller;

import com.aws.carepoint.domain.Hospital;
import com.aws.carepoint.mapper.HospitalMapper;
import com.aws.carepoint.service.HospitalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hospital")
public class HospitalController {
    private final HospitalService hospitalService;
    private final HospitalMapper hospitalMapper;

    public HospitalController(HospitalService hospitalService, HospitalMapper hospitalMapper) {
        this.hospitalService = hospitalService;
        this.hospitalMapper = hospitalMapper;

    }

    // DB에서 병원 검색
    @GetMapping("/search")
    public List<Hospital> searchHospitals(@RequestParam("name") String name) {
        List<Hospital> hospitals = hospitalMapper.findByName(name);

        // DB에 병원이 없으면 Google API에서 가져와서 저장 후 다시 검색
        if (hospitals.isEmpty()) {
            hospitalService.fetchAndSaveHospitals(name);
            hospitals = hospitalMapper.findByName(name);

            return hospitals;
        }
        return hospitals;
    }
}
