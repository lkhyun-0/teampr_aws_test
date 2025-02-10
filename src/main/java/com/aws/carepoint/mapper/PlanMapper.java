package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.HospitalDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanMapper {

    @Insert("""
            INSERT INTO hospital(hospital_name, latitude, longitude, address, user_pk)
            VALUES (#{hospitalName}, #{latitude}, #{longitude}, #{address}, #{userPk})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "hospitalPk")
    void saveHospital(HospitalDto hospitalDto);

    @Insert("""
            INSERT INTO hospital_plan(select_date, select_time, hospital_pk)
            VALUES (#{selectDate}, #{selectTime}, #{hospitalPk})
            """)
    void saveHospitalPlan(HospitalDto hospitalDto);

    @Select("""
            SELECT b.* , bp.select_date, bp.select_time FROM hospital b
            JOIN hospital_plan bp ON b.hospital_pk = bp.hospital_pk
            """)
    @Results(id="hospitalResultMap", value = {
            @Result(property = "hospitalPk", column = "hospital_pk"),
            @Result(property = "hospitalName", column = "hospital_name"),
            @Result(property = "selectTime", column = "select_time"),
            @Result(property = "selectDate", column = "select_date"),
    })
    List<HospitalDto> getAllHospital(Integer userPk);
}
