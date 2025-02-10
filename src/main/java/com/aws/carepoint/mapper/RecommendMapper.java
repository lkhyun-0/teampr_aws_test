package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.RecommendDto;
import org.apache.ibatis.annotations.*;


@Mapper
public interface RecommendMapper {

    @Select("""
            SELECT *
            FROM recommend
            WHERE user_pk = #{userPk} AND article_pk = #{articlePk}
            """)
    @Results(id = "recommedMap", value = {
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "recomStatus", column = "recom_status"),
            @Result(property = "userPk", column = "user_pk"),
    })
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
            WHERE article_pk = #{articlePk} AND recom_status = 1
            """)
    int countRecommend(@Param("articlePk") int articlePk);
}
