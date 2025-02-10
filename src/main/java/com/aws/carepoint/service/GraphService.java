package com.aws.carepoint.service;

import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.mapper.GraphMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GraphService {
    private final GraphMapper graphMapper;

    public GraphService(GraphMapper graphMapper) {
        this.graphMapper = graphMapper;
    }

    public List<GraphDto> getGraphData(int userPk) {
        return graphMapper.getGraphData(userPk);
    }

    // 오늘의 수치 저장
    @Transactional
    public void saveGraph(GraphDto graphDto) {
        graphMapper.insertGraph(graphDto);
        graphMapper.updateValueCount(graphDto.getUserPk(), graphDto.getRegDate()); // target 테이블 업데이트
        graphMapper.updateWeightValue(graphDto.getWeight(), graphDto.getUserPk());
    }

    public boolean hasTodayGraphData(int userPk) {
        return graphMapper.hasTodayGraphData(userPk) > 0;
    }
}
