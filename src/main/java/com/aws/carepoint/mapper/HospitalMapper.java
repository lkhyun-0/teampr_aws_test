package com.aws.carepoint.mapper;
import com.aws.carepoint.domain.Hospital;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HospitalMapper {

    @Insert("""
            INSERT INTO hospital_api (hospital_name, address, latitude, longitude, place_id)
            VALUES (#{name}, #{address}, #{latitude}, #{longitude}, #{placeId})
            ON DUPLICATE KEY UPDATE
            hospital_name = VALUES(hospital_name), address = VALUES(address)
            """)
    void insertHospital(Hospital hospital);

    @Select("""
            SELECT * 
            FROM hospital_api 
            WHERE place_id = #{placeId}
            """)
    @Results(id="hospitalApi", value = {
            @Result(property = "hospitalPk", column = "hospital_api_pk"),
            @Result(property = "name", column = "hospital_name"),
            @Result(property = "placeId", column = "place_id"),
    })
    Hospital findByPlaceId(@Param("placeId") String placeId);

    @Select("""
            SELECT * 
            FROM hospital_api 
            WHERE hospital_name LIKE CONCAT('%', #{name}, '%')
            """)
    @ResultMap("hospitalApi")
    List<Hospital> findByName(@Param("name") String name);
}
