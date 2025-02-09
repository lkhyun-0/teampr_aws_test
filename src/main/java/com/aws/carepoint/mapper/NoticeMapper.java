package com.aws.carepoint.mapper;

import com.aws.carepoint.dto.NoticeDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface NoticeMapper {



    // 게시글 작성
    @Insert("INSERT INTO article (title, content, user_pk, board_pk)" +
            "VALUES (#{title}, #{content}, 1, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "articlePk")
    public int insertArticle(NoticeDto notice);
}
