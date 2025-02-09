package com.aws.carepoint.controller;


import com.aws.carepoint.dto.GraphDto;
import com.aws.carepoint.service.GraphService;
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
    }

