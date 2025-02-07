package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.ExerciseEntity;
import com.aws.carepoint.dto.GraphDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

@Mapper
public interface ExerciseMapper {

    @Insert("INSERT INTO exercise_Api (exercise_name, MET) VALUES (#{exerciseName}, #{metValue})")
    void saveExerciseApi(ExerciseEntity exercise);

    @Insert("INSERT INTO graph (blood_sugar, blood_press, weight, user_pk) " +
            "VALUES (#{bloodSugar}, #{bloodPress}, #{weight}, 2)")
    @Results(id = "graphResultMap", value = {
            @Result(property = "bloodSugar", column = "blood_sugar"),
            @Result(property = "bloodPress", column = "blood_press"),
            @Result(property = "userPk", column = "user_pk")
    })
    void insertGraph(GraphDto graphDto);

}
