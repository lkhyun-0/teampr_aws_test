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

import java.lang.reflect.Array;
import java.util.ArrayList;
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
    public ArrayList<QnaDto> getQnaList(SearchCriteria scri) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("startPageNum", (scri.getPage()-1)*scri.getPerPageNum());
        result.put("searchType", scri.getSearchType());
        result.put("perPageNum", scri.getPerPageNum());

        ArrayList<QnaDto> qlist = qnaMapper.getQnaList(result);

        return qlist;
    }

    public int getQnaTotalCount(SearchCriteria scri) {
        int cnt = qnaMapper.getTotalQnaCount(scri);
        return cnt;
    }

    // 특정 게시글 상세 내용 가져오기
    public QnaDto getQnaDetail(int articlePk) {
        return qnaMapper.getQnaDetail(articlePk);
    }

    // 게시글 작성
    public int createQna(QnaDto qna) {
        int value = qnaMapper.insertArticle(qna);
        qnaMapper.updateOriginNum(qna.getArticlePk());
        return value;
    }

    // 게시글 삭제
    public int deleteQna(QnaDto qna) {
        int value = qnaMapper.updateDelStatus(qna);
        return value;
    }

    // 게시글 수정
    public int updateQna(QnaDto qna) {
        int value = qnaMapper.updateQna(qna);
        return value;
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
    public int createQnaReply(QnaDto qna) {
        int value = qnaMapper.insertQnaReply(qna);
        return value;
    }

    public List<Integer> getUserOriginNums(int userPk) {
        return qnaMapper.getUserOriginNums(userPk);
    }
}
