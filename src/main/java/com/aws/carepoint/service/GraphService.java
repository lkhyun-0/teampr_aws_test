package com.aws.carepoint.service;

import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.mapper.GraphMapper;
import org.springframework.stereotype.Service;
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
}
