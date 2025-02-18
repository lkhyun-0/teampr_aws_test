package com.aws.carepoint.service;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.dto.NoticeDto;
import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.mapper.NoticeMapper;
import com.aws.carepoint.util.PageMaker;
import com.aws.carepoint.util.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeService {

    @Autowired(required = false)
    private final PageMaker pageMaker;

    @Autowired
    private final NoticeMapper noticeMapper;

    // 게시글 작성
    public int createNotice(NoticeDto noticeDto) {
        return noticeMapper.insertArticle(noticeDto);
    }


    // 특정 게시글 상세 내용 가져오기
    public NoticeDto getNoticeDetail(int articlePk) {

        NoticeDto notice = noticeMapper.getNoticeDetail(articlePk);

        return notice;
    }


    // 전체 게시글 개수 조회
    public Map<String, Object> getNoticeList(SearchCriteria scri) {
        Map<String, Object> result = new HashMap<>();

        int totalCount = noticeMapper.getTotalNoticeCount(scri);

        scri.setPerPageNum(12);

        pageMaker.setScri(scri);
        pageMaker.setTotalCount(totalCount);

        // 매 요청마다 새로운 리스트를 가져와야 함
        List<NoticeDto> noticeList = noticeMapper.getNoticeList(scri);

        // Thymeleaf에 데이터 전달
        result.put("noticeList", noticeList);
        result.put("pageMaker", pageMaker);

        return result;
    }

    // 사용자 권한 조회
    public Integer getUserAuthLevel(int userPk) {
        return noticeMapper.getAuthLevelByUserPk(userPk);
    }
    

    // 삭제
    public int deleteNotice(int articlePk) {
        return noticeMapper.deleteNotice(articlePk);
    }

    // 게시글 수정
    public int updateNotice(NoticeDto noticeDto) {
        return noticeMapper.updateNotice(noticeDto);
    }




}
