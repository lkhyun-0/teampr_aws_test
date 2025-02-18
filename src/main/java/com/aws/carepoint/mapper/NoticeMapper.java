package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.util.SearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeMapper {

    @Select("SELECT a.*, u.usernick " +
            "FROM article a " +
            "JOIN users u ON a.user_pk = u.user_pk " +
            "WHERE a.board_pk = 1 " +
            "AND a.del_status = 0 " +
            "ORDER BY a.article_pk DESC " +
            "LIMIT #{pageStart}, #{perPageNum} ")
    @Results(id = "noticeResultMap", value = {
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "boardPk", column = "board_pk"),
            @Result(property = "userPk", column = "user_pk"),
    })
    List<NoticeDto> getNoticeList(SearchCriteria scri);

    @Select("SELECT auth_level FROM users WHERE user_pk = #{userPk}")
    Integer getAuthLevelByUserPk(int userPk);


    // 총 게시글 개수 조회 (검색 조건 포함)
    @Select("SELECT COUNT(*) " +
            "FROM article " +
            "WHERE board_pk = 1 " +
            "AND del_status = 0")
    int getTotalNoticeCount(SearchCriteria scri);

    // 게시글 작성
    @Insert("""
            INSERT INTO article (title, content, filename, user_pk, board_pk)
            VALUES (#{title}, #{content}, #{filename}, #{userPk}, 1)
            """)
    int insertArticle(NoticeDto notice);


    // 상세보기 (article_pk 기준으로 조회)
    @Select("SELECT a.*, u.usernick " +
            "FROM article a " +
            "JOIN users u ON a.user_pk = u.user_pk " +
            "WHERE a.article_pk = #{articlePk}")
    @ResultMap("noticeResultMap")
    NoticeDto getNoticeDetail(int articlePk);


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
    int updateNotice(NoticeDto notice);



    @Update("""
            UPDATE article
            SET del_status = 1
            WHERE article_pk = #{articlePk}
            """)
    int deleteNotice(int articlePk);



}


