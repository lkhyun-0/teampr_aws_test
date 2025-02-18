package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.dto.QnaDto;
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
    FreeDto getFreeDetail(int articlePk);

    @Update("""
            UPDATE article
            SET viewcnt = viewcnt+1
            WHERE article_pk = #{articlePk}
            """)
    int addviewcnt(int articlePk);

    @Insert("""
            INSERT INTO article (title, content, filename, user_pk, board_pk)
            VALUES (#{title}, #{content}, #{filename}, #{userPk}, 2)
            """)
    int writeFree(FreeDto freeDto);

    // 동적으로 처리하기 위해서
    @Update("""
                <script>
                    UPDATE article
                    <set>
                        title = #{title},
                        content = #{content},
                        filename = COALESCE(#{filename}, 'null')
                    </set>
                    WHERE article_pk = #{articlePk}
                </script>
            """)
    int modifyFree(FreeDto freeDto);

    @Update("""
            UPDATE article
            SET del_status = 1
            WHERE article_pk = #{articlePk}
            """)
    int deleteArticle(int articlePk);

    @Select("SELECT * " +
            "FROM article " +
            "WHERE board_pk = 2 " +
            "AND user_pk = #{userPk} " +
            "ORDER BY reg_date DESC " +
            "LIMIT 5")
    @ResultMap("freeResultMap")
    List<FreeDto> getRecentFree(int userPk);
}
