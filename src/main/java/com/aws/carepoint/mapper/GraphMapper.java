package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.GraphDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface GraphMapper {
    @Select("SELECT graph_pk, weight, blood_press, blood_sugar, reg_date, user_pk FROM graph WHERE user_pk = #{userPk} ORDER BY reg_date")
    List<GraphDto> getGraphData(int userPk);
}

