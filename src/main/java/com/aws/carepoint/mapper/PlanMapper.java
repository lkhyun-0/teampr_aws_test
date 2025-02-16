package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.HospitalDto;
import com.aws.carepoint.dto.MedicineDto;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PlanMapper {

    // 병원
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

    @Select("""
            SELECT COUNT(*)
            FROM hospital_plan
            WHERE select_date = #{selectDate}
            """)
    boolean checkExistingPlan(LocalDate selectDate);

    @Delete("""
            DELETE FROM hospital_plan 
            WHERE hospital_pk = #{hospitalPk} AND hospital_pk 
            IN (SELECT hospital_pk FROM hospital WHERE user_pk = #{userPk})""")
    int deleteHospitalPlan(@Param("hospitalPk") int hospitalPk, @Param("userPk") int userPk);

    @Delete("""
            DELETE FROM hospital 
            WHERE hospital_pk = #{hospitalPk} AND user_pk = #{userPk}""")
    int deleteHospital(@Param("hospitalPk") int hospitalPk, @Param("userPk") int userPk);

    // 약
    @Insert("""
            INSERT INTO medicine(medicine_name, medicine_type, user_pk)
            VALUES (#{medicineName}, #{medicineType}, #{userPk})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "medicinePk")
    void saveMedicine(MedicineDto medicineDto);

    @Insert("""
            INSERT INTO medicine_plan(color, start_date, end_date,select_time, medicine_pk)
            VALUES (#{color}, #{startDate}, #{endDate}, #{selectTime}, #{medicinePk})
            """)
    void saveMedicine_Plan(MedicineDto medicineDto);

    @Select("SELECT * FROM medicine WHERE user_pk = #{userPk} ORDER BY medicine_pk DESC LIMIT 1")
    @Results(id = "medicineResultMap", value = {
            @Result(property = "medicinePk", column = "medicine_pk"),
            @Result(property = "medicineName", column = "medicine_name"),
            @Result(property = "medicineType", column = "medicine_type"),
            @Result(property = "selectTime", column = "select_time"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "userPk", column = "user_pk"),
    })
    MedicineDto getLastInsertedMedicine(@Param("userPk") Integer userPk);

    @Select("""
            SELECT m.* , mp.* FROM medicine m
            JOIN medicine_plan mp ON m.medicine_pk = mp.medicine_pk
            ORDER BY reg_date DESC;
            """)
    @ResultMap("medicineResultMap")
    List<MedicineDto> getAllMedicine(Integer userPk);

    @Select("""
            SELECT *
            FROM medicine
            WHERE medicine_name = #{medicineName}
            ORDER BY reg_date DESC
            LIMIT 1
            """)
    @ResultMap("medicineResultMap")
    MedicineDto getMedicineRecent(String medicineName);


    @Select("""
            SELECT m.* , mp.*
            FROM medicine m 
            JOIN medicine_plan mp ON m.medicine_pk = mp.medicine_pk
            WHERE #{selectDate} BETWEEN mp.start_date AND mp.end_date
            """)
    @ResultMap("medicineResultMap")
    List<MedicineDto> getMedicineDetail(@Param("selectDate") String selectDate);

    @Delete("""
            <script>
                DELETE FROM medicine_plan
                WHERE medicine_pk IN
                <foreach item='id' collection='medicinePkList' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
                AND medicine_pk IN
                (SELECT medicine_pk FROM medicine WHERE user_pk = #{userPk})
            </script>
        """)
    int deleteMedicinePlan(@Param("medicinePkList") List<Integer> medicinePkList, @Param("userPk") Integer userPk);


    @Delete("""
            <script>
            DELETE FROM medicine
            WHERE medicine_pk IN
            <foreach item='id' collection='medicinePkList' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            AND user_pk = #{userPk}
            </script>
            """)
    int deleteMedicine(@Param("medicinePkList") List<Integer> medicinePkList, @Param("userPk") Integer userPk);

}
