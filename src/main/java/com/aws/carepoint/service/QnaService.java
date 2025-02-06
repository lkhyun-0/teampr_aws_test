package com.aws.carepoint.service;

import com.aws.carepoint.util.PageMaker;
import com.aws.carepoint.util.SearchCriteria;
import com.aws.carepoint.dto.QnaDto;
import com.aws.carepoint.mapper.QnaMapper;
import lombok.RequiredArgsConstructor;
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

    public Map<String, Object> getQnaList(SearchCriteria scri) {
        Map<String, Object> result = new HashMap<>();

        // ✅ 전체 게시글 개수 조회
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

    public QnaDto getQnaDetail(int articlePk) {
        // 게시글 상세 내용 가져오기
        return qnaMapper.getQnaDetail(articlePk);
    }

    // 게시글 수정
    public void updateQna(QnaDto qna) {
        qnaMapper.updateQna(qna);
    }

    /*// 게시글 작성
    @Transactional
    public void createQna(QnaDto qna) {
        // 1. board 테이블에 데이터 삽입
        qnaMapper.insertBoard(qna);  // INSERT 실행

        // 2. 방금 삽입된 board_pk 가져오기 (MyBatis에서 자동으로 세팅됨)
        Long maxBoardPk = qnaMapper.maxBoardPk();
        qna.setBoardPk(maxBoardPk);

        // 3. article 테이블에 데이터 삽입
        qnaMapper.insertArticle(qna);  // INSERT 실행
    }*/
}
