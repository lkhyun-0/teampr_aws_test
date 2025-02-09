package com.aws.carepoint.service;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.mapper.FreeMapper;
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
public class FreeService {

    @Autowired
    private final FreeMapper freeMapper;

    @Autowired(required = false)
    private final PageMaker pageMaker;

    public Map<String, Object> getFreeList(SearchCriteria scri) {
        Map<String, Object> result = new HashMap<>();

        int totalCount = freeMapper.getTotalFreeCount(scri);

        scri.setPerPageNum(12);

        pageMaker.setScri(scri);
        pageMaker.setTotalCount(totalCount);

        List<FreeDto> FreeList = freeMapper.getFreeList(scri);

        result.put("freeList", FreeList);
        result.put("pageMaker", pageMaker);

        return result;
    }

    public FreeDto getFreeContent(int articlePk) {

        FreeDto free = freeMapper.getFreeContent(articlePk);

        return free;
    }

    public int addviewcnt(int articlePk) {
        int value =  freeMapper.addviewcnt(articlePk);

        return value;
    }
}
