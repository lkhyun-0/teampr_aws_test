package com.aws.carepoint.mapper;

import com.aws.carepoint.domain.ExerciseApiEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExerciseApiMapper {
    void insertExercise(ExerciseApiEntity exercise);
    // 필요 시 여러 메소드들 (select, update, etc.)
}
