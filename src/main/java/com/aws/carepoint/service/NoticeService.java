package com.aws.carepoint.service;

import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    @Autowired
    private final NoticeMapper noticeMapper;

    // 게시글 작성
    public void createNotice(NoticeDto notice) {
        noticeMapper.insertArticle(notice);
    }

}
