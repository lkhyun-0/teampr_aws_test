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

    @Select("SELECT * FROM hospital WHERE user_pk = #{userPk} ORDER BY hospital_pk DESC LIMIT 1")
    @ResultMap("hospitalResultMap")
    HospitalDto getLastInsertedHospital(@Param("userPk") Integer userPk);

    @Select("""
            SELECT b.* , bp.select_date, bp.select_time FROM hospital b
            JOIN hospital_plan bp ON b.hospital_pk = bp.hospital_pk
            ORDER BY reg_date DESC;
            """)
    @Results(id = "hospitalResultMap", value = {
            @Result(property = "hospitalPk", column = "hospital_pk"),
            @Result(property = "hospitalName", column = "hospital_name"),
            @Result(property = "selectTime", column = "select_time"),
            @Result(property = "selectDate", column = "select_date"),
    })
    List<HospitalDto> getAllHospital(Integer userPk);

    @Select("""
            SELECT b.* ,bp.select_date, bp.select_time FROM hospital b
            JOIN hospital_plan bp ON b.hospital_pk = bp.hospital_pk
            WHERE b.hospital_pk = #{hospitalPk} AND bp.select_date = #{selectDate}
            """)
    @ResultMap("hospitalResultMap")
    HospitalDto getHospitalDetail(@Param("hospitalPk") int hospitalPk,@Param("selectDate") String selectDate);

    @Select("""
            SELECT *
            FROM hospital
            WHERE hospital_name = #{hospitalName}
            ORDER BY reg_date DESC
            LIMIT 1
            """)
    @ResultMap("hospitalResultMap")
    HospitalDto getHospitalRecent(String hospitalName);
}
