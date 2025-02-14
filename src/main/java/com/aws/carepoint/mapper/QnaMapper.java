package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.mapper.sql.QnaSqlProvider;
import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.dto.QnaDto;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface QnaMapper {

    @Select("SELECT a.*, u.usernick " +
            "FROM article a " +
            "JOIN users u ON a.user_pk = u.user_pk " +
            "WHERE a.board_pk = 1 " +
            "AND a.del_status = 0 " +
            "ORDER BY a.origin_num DESC, a.level_ ASC " +
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

    // 게시글 작성
    @Insert("INSERT INTO article (title, filename, content, user_pk, board_pk)" +
            "VALUES (#{title}, #{filename}, #{content}, #{userPk}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "articlePk")
    int insertArticle(QnaDto qna);

    // 게시글 작성 후 originNum 업데이트
    @Update("UPDATE article " +
            "SET origin_num = #{articlePk} " +
            "WHERE article_pk = #{articlePk} ")
    int updateOriginNum(int articlePk);

    // 게시글 삭제
    @UpdateProvider(type = QnaSqlProvider.class, method = "updateDelStatus")
    @ResultMap("qnaResultMap")
    int updateDelStatus(QnaDto qna);

    // 게시글 수정
    @Update("UPDATE article " +
            "SET content = #{content}, " +
            "title = #{title}, " +
            "update_date = NOW(), " +
            "filename = #{filename} " +
            "WHERE article_pk = #{articlePk} " +
            "AND user_pk = #{userPk}")
    @ResultMap("qnaResultMap")
    int updateQna(QnaDto qna);

    // 답변글 작성
    @Insert("INSERT INTO article (title, content, origin_num, level_, user_pk, board_pk) " +
            "VALUES (#{title}, #{content}, #{originNum}, 1, #{userPk}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "articlePk")
    @ResultMap("qnaResultMap")
    int insertQnaReply(QnaDto qna);

    @Select("SELECT COUNT(*) " +
            "FROM article " +
            "WHERE origin_num = #{originNum} " +
            "AND del_status = 0")
    int countReplies(int originNum);

    @Select("SELECT origin_num FROM article WHERE user_pk = #{userPk}")
    List<Integer> getUserOriginNums(int userPk);







    @Select("SELECT * " +
            "FROM article " +
            "WHERE board_pk = 3 " +
            "AND user_pk = #{userPk} " +
            "ORDER BY reg_date DESC " +
            "LIMIT 5")
    @ResultMap("qnaResultMap")
    List<QnaDto> getRecentQna(int userPk);
}



