package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.ExerciseEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExerciseMapper {

    @Insert("INSERT INTO exercise_Api (exercise_name, MET) VALUES (#{exerciseName}, #{metValue})")
    void saveExerciseApi(ExerciseEntity exercise);

}
