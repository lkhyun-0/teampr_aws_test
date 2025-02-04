package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.ExerciseApiEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExerciseMapper {

    @Insert("INSERT INTO exercise_Api (exercise_name, MET) VALUES (#{exerciseName}, #{metValue})")
    void insertExercise(ExerciseApiEntity exercise);

}
