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

    public void saveHospital(HospitalDto hospitalDto) {
        planMapper.saveHospital(hospitalDto);
        planMapper.saveHospitalPlan(hospitalDto);
    }

    public List<HospitalDto> getAllHospital(Integer userPk) {
        return planMapper.getAllHospital(userPk);
    }
}
