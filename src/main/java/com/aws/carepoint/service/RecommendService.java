package com.aws.carepoint.service;

import com.aws.carepoint.dto.RecommendDto;
import com.aws.carepoint.mapper.RecommendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendService {

    @Autowired
    private final RecommendMapper recommendMapper;

    @Transactional
    public RecommendDto toggleRecommend(int userPk, int articlePk) {
        // 기존 추천 기록 조회
        RecommendDto recommendDto = recommendMapper.findByUserAndBoard(userPk, articlePk);
        int newStatus = 0;

        System.out.println(recommendDto);

        if (recommendDto == null) {
            newStatus = 1;
            recommendMapper.insertRecommend(userPk, articlePk, newStatus);
        } else {
            newStatus = (recommendDto.getRecomStatus() == 1) ? 0 : 1;
            recommendMapper.updateRecommendStatus(userPk, articlePk, newStatus);
        }

        RecommendDto updatedRecommend = recommendMapper.findByUserAndBoard(userPk, articlePk);

        int count = recommendMapper.countRecommend(articlePk);

        RecommendDto result = new RecommendDto();
        result.setUserPk(userPk);
        result.setArticlePk(articlePk);
        result.setRecomStatus(newStatus);
        result.setCount(count);

        return result;
    }

    public int getRecommendCount(int articlePk) {
        int count = recommendMapper.countRecommend(articlePk);
        return count;
    }
}
