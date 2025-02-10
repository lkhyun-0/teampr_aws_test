package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.CommentDto;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface CommentMapper {

    @Select("""
            SELECT c.*, u.usernick
            FROM comments c 
            JOIN users u ON c.user_pk = u.user_pk
            WHERE c.article_pk = #{articlePk} AND c.del_status = 0
            """)
    @Results(id = "commentResultMap", value = {
            @Result(property = "commentPk", column = "comment_pk"),
            @Result(property = "userNick", column = "usernick"),
            @Result(property = "regDate", column = "reg_Date"),
            @Result(property = "articlePk", column = "article_pk"),
            @Result(property = "userPk", column = "user_pk"),
    })
    List<CommentDto> getComment(int articlePk);

    @Insert("""
            INSERT INTO comments(content, user_pk, article_pk)
            VALUES (#{content}, #{userPk}, #{articlePk})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "commentPk")
    int addComment(CommentDto commentDto);

    @Select("""
            SELECT c.*, u.usernick
            FROM comments c
            JOIN users u ON c.user_pk = u.user_pk
            WHERE c.comment_pk = #{commentPk}
            """)
    @ResultMap("commentResultMap")
    CommentDto getCommentDetail(int commentPk);

    @Update("""
            UPDATE comments
            SET del_status = 1
            WHERE comment_pk = #{commentPk}
            """)
    int deleteComment(int commentPk);
}
