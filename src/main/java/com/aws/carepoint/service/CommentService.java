package com.aws.carepoint.service;

import com.aws.carepoint.dto.CommentDto;
import com.aws.carepoint.dto.RecommendDto;
import com.aws.carepoint.mapper.CommentMapper;
import com.aws.carepoint.mapper.RecommendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Autowired
    private final CommentMapper commentMapper;

    public List<CommentDto> getComment(int articlePk) {
        List<CommentDto> clist = commentMapper.getComment(articlePk);
        return clist;
    }

    public CommentDto addComment(CommentDto commentDto) {
        int result = commentMapper.addComment(commentDto);
        if (result > 0) {
            return commentMapper.getCommentDetail(commentDto.getCommentPk());
        }
        return null;
    }

    public CommentDto getCommentById(int commentPk) {
        return commentMapper.getCommentDetail(commentPk);
    }

    public int deleteComment(int commentPk) {
        return commentMapper.deleteComment(commentPk);
    }
}
