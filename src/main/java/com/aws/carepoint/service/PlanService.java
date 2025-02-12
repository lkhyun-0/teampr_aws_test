package com.aws.carepoint.service;

import com.aws.carepoint.dto.HospitalDto;
import com.aws.carepoint.dto.MedicineDto;
import com.aws.carepoint.mapper.PlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public boolean checkExistingPlan(LocalDate selectDate) {
        return planMapper.checkExistingPlan(selectDate);
    }

    public boolean deleteHospital(int hospitalPk, Integer userPk) {
        int deletedPlans = planMapper.deleteHospitalPlan(hospitalPk, userPk);
        int deletedHospital = planMapper.deleteHospital(hospitalPk, userPk);

        return (deletedPlans > 0 || deletedHospital > 0);
    }

    public MedicineDto saveMedicine(MedicineDto medicineDto) {
        planMapper.saveMedicine(medicineDto);
        planMapper.saveMedicine_Plan(medicineDto);

        return planMapper.getLastInsertedMedicine(medicineDto.getUserPk());
    }

    public List<MedicineDto> getAllMedicine(Integer userPk) {
        return planMapper.getAllMedicine(userPk);
    }

    public MedicineDto getMedicineRecent(String medicineName) {
        return planMapper.getMedicineRecent(medicineName);
    }

    public List<MedicineDto> getMedicineDetail(String selectDate) {
        return planMapper.getMedicineDetail(selectDate);
    }

    public boolean deleteMedicine(List<Integer> medicinePkList, Integer userPk) {
        int deletedPlans = planMapper.deleteMedicinePlan(medicinePkList, userPk);
        int deleteMedicine = planMapper.deleteMedicine(medicinePkList, userPk);

        return (deletedPlans > 0 || deleteMedicine > 0);
    }
}
