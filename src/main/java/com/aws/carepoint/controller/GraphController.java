package com.aws.carepoint.controller;


import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.service.GraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/graph")
    public class GraphController {
        private final GraphService graphService;

        public GraphController(GraphService graphService) {
            this.graphService = graphService;
        }

    @GetMapping("/{userPk}")
    public ResponseEntity<?> getGraphData(@PathVariable("userPk") int userPk) {
        List<GraphDto> graphs = graphService.getGraphData(userPk);

        if (graphs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content 응답
        }

        return ResponseEntity.ok(graphs);
    }


    // 그래프 저장
    @PostMapping("/saveGraph")
    public String saveGraph(@RequestBody(required = false) GraphDto graphDto) {
        graphService.saveGraph(graphDto);
        return "redirect:/exercise/exerciseMain";
    }

    // 그래프 저장
    @PostMapping("/updateGraph")
    public String updateGraph(@RequestBody(required = false) GraphDto graphDto) {
        graphService.updateGraph(graphDto);
        return "redirect:/exercise/exerciseMain";
    }

    // ✅ 오늘 데이터가 있는지 확인하는 API
    @GetMapping("/has-today-graph")
    public ResponseEntity<Boolean> hasTodayGraphData(@RequestParam("userPk") int userPk) {
        boolean hasData = graphService.hasTodayGraphData(userPk);
        return ResponseEntity.ok(hasData);
    }
}

