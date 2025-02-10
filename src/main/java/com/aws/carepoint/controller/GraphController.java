package com.aws.carepoint.controller;

import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/graph")
public class GraphController {
    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/{userPk}")
    public List<GraphDto> getGraphData(@PathVariable int userPk) {
        return graphService.getGraphData(userPk);
    }

    // 그래프 저장
    @PostMapping("/saveGraph")
    public ResponseEntity<String> saveGraph(@RequestBody GraphDto graphDto) {
        graphService.saveGraph(graphDto);
        return ResponseEntity.ok("오늘의 수치가 저장되었습니다.");
    }

    // ✅ 오늘 데이터가 있는지 확인하는 API
    @GetMapping("/has-today-graph")
    public ResponseEntity<Boolean> hasTodayGraphData(@RequestParam int userPk) {
        boolean hasData = graphService.hasTodayGraphData(userPk);
        return ResponseEntity.ok(hasData);
    }
}

