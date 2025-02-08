package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.util.SearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface FreeMapper {

    @Select("""
            SELECT count(*)
            FROM article
            WHERE board_pk = 2
            AND del_status = 0
            AND (#{keyword} IS NULL OR title LIKE CONCAT('%', #{keyword}, '%'))
            """)
    int getTotalFreeCount(SearchCriteria scri);

    @Select("""
                SELECT a.*, u.usernick
                FROM article a
                JOIN users u ON a.user_pk = u.user_pk
                WHERE a.board_pk = 2
                AND a.del_status = 0
                AND (#{keyword} IS NULL OR a.title LIKE CONCAT('%', #{keyword}, '%'))
                ORDER BY a.article_pk DESC
                LIMIT #{pageStart}, #{perPageNum}
            """)
    @Results(id = "freeResultMap", value = {
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "boardPk", column = "board_pk"),
            @Result(property = "userPk", column = "user_pk"),
            @Result(property = "userNick", column = "usernick")
    })
    List<FreeDto> getFreeList(SearchCriteria scri);

    @Select("""
            SELECT a.*, u.usernick
            FROM article a
            JOIN users u ON a.user_pk = u.user_pk
            AND a.article_pk = #{articlePk}
            """)
    @ResultMap("freeResultMap")
    FreeDto getFreeContent(int articlePk);
}
