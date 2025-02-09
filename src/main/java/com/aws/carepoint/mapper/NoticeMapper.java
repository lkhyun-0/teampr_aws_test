package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.mapper.sql.QnaSqlProvider;
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
            "ORDER BY a.origin_num DESC, a.level_ ASC " +
            "LIMIT #{pageStart}, #{perPageNum} ")
    @Results(id = "noticeResultMap", value = {
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "boardPk", column = "board_pk"),
            @Result(property = "userPk", column = "user_pk"),
    })
    List<NoticeDto> getNoticeList(SearchCriteria scri);


    // 총 게시글 개수 조회 (검색 조건 포함)
    @Select("SELECT COUNT(*) " +
            "FROM article " +
            "WHERE board_pk = 1 " +
            "AND del_status = 0")
    int getTotalNoticeCount(SearchCriteria scri);

    // 게시글 작성
    @Insert("INSERT INTO article (title, content, user_pk, board_pk)" +
            "VALUES (#{title}, #{content}, 1, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "articlePk")
    public int insertArticle(NoticeDto notice);


    // 상세보기 (article_pk 기준으로 조회)
    @Select("SELECT a.*, u.usernick " +
            "FROM article a " +
            "JOIN users u ON a.user_pk = u.user_pk " +
            "WHERE a.article_pk = #{articlePk}")
    @ResultMap("noticeResultMap")
    NoticeDto getNoticeDetail(int articlePk);


    // 게시글 수정
    @Update("UPDATE article " +
            "SET content = #{content}, " +
            "title = #{title}, " +
            "update_date = NOW(), " +
            "filename = #{filename} " +
            "WHERE article_pk = #{articlePk} " +
            "AND user_pk = #{userPk}")
    @ResultMap("noticeResultMap")
    void updateNotice(NoticeDto notice);


    // 게시글 삭제
    @ResultMap("noticeResultMap")
    int updateDelStatus(NoticeDto notice);

}


