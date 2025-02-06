package com.aws.carepoint.mapper;

import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.dto.QnaDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QnaMapper {

    @Select("SELECT a.*, u.usernick " +
            "FROM article a " +
            "JOIN users u ON a.user_pk = u.user_pk " +
            "WHERE a.board_pk = 1 " +
            "AND a.del_status = 0 " +
            "ORDER BY a.article_pk DESC " +
            "LIMIT #{pageStart}, #{perPageNum} ")
    @Results(id = "qnaResultMap", value = {
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "originNum", column = "origin_num"),
            @Result(property = "level", column = "level_"),
            @Result(property = "regDate", column = "reg_date"),
            @Result(property = "updateDate", column = "update_date"),
            @Result(property = "boardPk", column = "board_pk"),
            @Result(property = "userPk", column = "user_pk"),
            @Result(property = "userNick", column = "usernick")
    })
    List<QnaDto> getQnaList(SearchCriteria scri);

    // 총 게시글 개수 조회 (검색 조건 포함)
    @Select("SELECT COUNT(*) " +
            "FROM article " +
            "WHERE board_pk = 1 " +
            "AND del_status = 0")
    int getTotalQnaCount(SearchCriteria scri);

    // 상세보기 (article_pk 기준으로 조회)
    @Select("SELECT a.*, u.usernick " +
            "FROM article a " +
            "JOIN users u ON a.user_pk = u.user_pk " +
            "WHERE a.article_pk = #{articlePk}")
    @ResultMap("qnaResultMap")
    QnaDto getQnaDetail(int articlePk);

    // 게시글 수정
    @Update("UPDATE article " +
            "SET content = #{content}, " +
            "title = #{title}, " +
            "update_date = NOW(), " +
            "filename = #{filename} " +
            "WHERE article_pk = #{articlePk} " +
            "AND user_pk = #{userPk}")
    @ResultMap("qnaResultMap")
    void updateQna(QnaDto qna);

    /*@Insert("INSERT INTO board (board_type, title, user_pk) " +
            "VALUES ('Q', #{title}, 2)")
    void insertBoard(QnaDto qna);

    @Select("SELECT MAX(board_pk) AS max_board_pk " +
            "FROM board " +
            "WHERE board_type = 'Q'")
    Long maxBoardPk();

    @Insert("INSERT INTO article (content, filename, origin_num, level_, board_pk)" +
            "VALUES (#{content}, #{filename}, 0, 0, #{boardPk})")
    void insertArticle(QnaDto qna);*/
}

