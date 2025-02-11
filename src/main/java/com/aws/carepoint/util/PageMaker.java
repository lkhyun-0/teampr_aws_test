package com.aws.carepoint.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PageMaker {
    private int totalCount; // 전체 게시글 수
    private int startPage;  // 현재 화면에서 보이는 시작 페이지 번호
    private int endPage;    // 현재 화면에서 보이는 끝 페이지 번호
    private boolean prev;   // 이전 페이지 버튼 활성화 여부
    private boolean next;   // 다음 페이지 버튼 활성화 여부
    private int displayPageNum = 10; // 한 화면에 보여줄 페이지 개수
    private SearchCriteria scri;



    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        calcData();
    }

    private void calcData() {
        endPage = (int) (Math.ceil(scri.getPage() / (double) displayPageNum) * displayPageNum);
        startPage = (endPage - displayPageNum) + 1;

        int tempEndPage = (int) (Math.ceil(totalCount / (double) scri.getPerPageNum()));

        if (endPage > tempEndPage) {
            endPage = tempEndPage;
        }

        prev = startPage > 1;
        next = endPage * scri.getPerPageNum() < totalCount;
    }
}