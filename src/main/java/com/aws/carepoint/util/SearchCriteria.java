package com.aws.carepoint.util;

import lombok.Data;

@Data
public class SearchCriteria extends Criteria {
    private String searchType;
    private String keyword;

    public SearchCriteria() {
        if (this.getPage() == 0) this.setPage(1); // 기본 페이지 1
        if (this.getPerPageNum() == 0) this.setPerPageNum(10); // 기본 페이지당 게시글 수 10
    }

    public int getPageStart() {
        return (this.getPage() - 1) * this.getPerPageNum();
    }
}