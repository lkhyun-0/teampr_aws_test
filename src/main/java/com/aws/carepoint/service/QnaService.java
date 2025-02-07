package com.aws.carepoint.service;

import com.aws.carepoint.util.PageMaker;
import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.mapper.QnaMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QnaService {

    @Autowired(required = false)
    private final PageMaker pageMaker;

    @Autowired
    private final QnaMapper qnaMapper;

    // 전체 게시글 개수 조회
    public Map<String, Object> getQnaList(SearchCriteria scri) {
        Map<String, Object> result = new HashMap<>();

        int totalCount = qnaMapper.getTotalQnaCount(scri);

        scri.setPerPageNum(12);

        pageMaker.setScri(scri);
        pageMaker.setTotalCount(totalCount);

        // ✅ 매 요청마다 새로운 리스트를 가져와야 함
        List<QnaDto> qnaList = qnaMapper.getQnaList(scri);

        // ✅ Thymeleaf에 데이터 전달
        result.put("qnaList", qnaList);
        result.put("pageMaker", pageMaker);

        return result;
    }

    // 특정 게시글 상세 내용 가져오기
    public QnaDto getQnaDetail(int articlePk) {
        return qnaMapper.getQnaDetail(articlePk);
    }

    // 게시글 작성
    public void createQna(QnaDto qna) {
        qnaMapper.insertArticle(qna);
        qnaMapper.updateOriginNum(qna.getArticlePk());
    }

    public int deleteQna(QnaDto qna) {
        int value = qnaMapper.updateDelStatus(qna);
        return value;
    }

    // 게시글 수정
    public void updateQna(QnaDto qna) {
        qnaMapper.updateQna(qna);
    }

    // 답변글 존재 여부
    public int hasQnaReply(QnaDto qna) {
        int value = qnaMapper.countReplies(qna.getOriginNum());
        if (value == 2) {
            return value;
        }
        return value;
    }

    // 답변글 작성
    public void createQnaReply(QnaDto qna) {
        qnaMapper.insertQnaReply(qna);
    }
}
