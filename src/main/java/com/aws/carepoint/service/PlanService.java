package com.aws.carepoint.service;

import com.aws.carepoint.dto.HospitalDto;
import com.aws.carepoint.mapper.PlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    @Autowired
    private final PlanMapper planMapper;

    public HospitalDto saveHospital(HospitalDto hospitalDto) {
        planMapper.saveHospital(hospitalDto);
        planMapper.saveHospitalPlan(hospitalDto);

        return planMapper.getLastInsertedHospital(hospitalDto.getUserPk());
    }

    public List<HospitalDto> getAllHospital(Integer userPk) {
        return planMapper.getAllHospital(userPk);
    }

    public HospitalDto getHospitalDetail(int hospitalPk, String selectDate) {
        return planMapper.getHospitalDetail(hospitalPk, selectDate);
    }

    public HospitalDto getHospitalRecent(String hospitalName) {
        return planMapper.getHospitalRecent(hospitalName);
    }
}
