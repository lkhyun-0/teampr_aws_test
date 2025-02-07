package com.aws.carepoint.util;

import lombok.Data;

@Data
public class Criteria {
    private int page;  // 현재 페이지 번호
    private int perPageNum; // 한 페이지에 보여줄 게시글 수
}
