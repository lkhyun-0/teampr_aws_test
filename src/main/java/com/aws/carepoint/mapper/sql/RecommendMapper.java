package com.aws.carepoint.mapper.sql;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.dto.RecommendDto;
import com.aws.carepoint.util.SearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface RecommendMapper {

    @Select("""
            SELECT *
            FROM recommend
            WHERE user_pk = #{userPk} AND article_pk = #{articlePk}
            """)
    RecommendDto findByUserAndBoard(@Param("userPk") int userPk, @Param("articlePk") int articlePk);

    @Insert("""
            INSERT INTO recommend(recom_status, user_pk, article_pk)
            VALUES(#{newStatus}, #{userPk}, #{articlePk})
            """)
    void insertRecommend(@Param("userPk") int userPk, @Param("articlePk") int articlePk, @Param("newStatus") int newStatus);

    @Update("""
            UPDATE recommend
            SET recom_status = #{newStatus}
            WHERE user_pk = #{userPk} AND article_pk = #{articlePk}
            """)
    void updateRecommendStatus(@Param("userPk") int userPk, @Param("articlePk") int articlePk, @Param("newStatus") int newStatus);

    @Select("""
            SELECT count(*)
            FROM recommend
            WHERE article_pk = #{articlePk}
            """)
    int countRecommend(@Param("articlePk") int articlePk);
}
