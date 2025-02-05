package com.aws.carepoint.mapper;

import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.dto.QnaDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QnaMapper {

    @Select("SELECT a.*, b.*, u.usernick \n" +
            "FROM article a\n" +
            "JOIN board b ON a.board_pk = b.board_pk\n" +
            "JOIN users u ON b.user_pk = u.user_pk " +
            "WHERE b.board_type = 'Q'\n" +
            "AND b.del_status = 0\n" +
            "ORDER BY a.article_pk DESC\n" +
            "LIMIT #{pageStart}, #{perPageNum}")
    @Results(id = "qnaResultMap", value = {
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "content", column = "content"),
            @Result(property = "filename", column = "filename"),
            @Result(property = "recom", column = "recom"),
            @Result(property = "viewcnt", column = "viewcnt"),
            @Result(property = "originNum", column = "origin_num"),
            @Result(property = "level", column = "level_"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "boardPk", column = "board_pk"),
            @Result(property = "title", column = "title"),
            @Result(property = "boardType", column = "board_type"),
            @Result(property = "userPk", column = "user_pk"),
            @Result(property = "userNick", column = "usernick")
    })
    List<QnaDto> getQnaList(SearchCriteria scri);

    // 총 게시글 개수 조회 (검색 조건 포함)
    @Select("SELECT COUNT(*) " +
            "FROM article a " +
            "JOIN board b ON a.board_pk = b.board_pk " +
            "WHERE b.board_type = 'Q' " +
            "AND b.del_status = 0 ")
    int getTotalQnaCount(SearchCriteria scri);

    // 상세보기 (article_pk 기준으로 조회)
    @Select("SELECT a.article_pk, a.content, a.viewcnt, a.reg_date, " +
            "b.title, b.user_pk, u.usernick " +
            "FROM article a " +
            "JOIN board b ON a.board_pk = b.board_pk " +
            "JOIN users u ON b.user_pk = u.user_pk " +
            "WHERE a.article_pk = #{articlePk}")
    @ResultMap("qnaResultMap")
    QnaDto getQnaDetail(int articlePk);

    // 게시글 수정
    @Update("UPDATE article a " +
            "JOIN board b ON a.board_pk = b.board_pk " +
            "SET a.content = #{content}, " +
            "b.title = #{title}, " +
            "a.update_date = NOW(), " +
            "a.filename = #{filename} " +
            "WHERE a.article_pk = #{articlePk} " +
            "AND b.user_pk = #{userPk}")
    @ResultMap("qnaResultMap")
    void updateQna(QnaDto qna);
}
