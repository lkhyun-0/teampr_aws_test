package com.aws.carepoint.controller;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.service.FreeService;
import com.aws.carepoint.util.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/free")
public class FreeController {

    @Autowired
    private FreeService freeService;

    @RequestMapping("/freeList")
    public String freeList(
            @ModelAttribute("scri") SearchCriteria scri,
            Model model) {

        Map<String, Object> result = freeService.getFreeList(scri);
        model.addAllAttributes(result);

        return "free/freeList";
    }

    @RequestMapping("/freeContents/{id}")
    public String freeContents(
            @PathVariable("id") int articlePk,
            Model model) {

        int value = freeService.addviewcnt(articlePk);

        if (value > 0) {
            FreeDto free = freeService.getFreeContent(articlePk);
            model.addAttribute("free", free);
        } else {
            return "free/freeList";
        }

        return "free/freeContents";
    }

    @RequestMapping("/freeWrite")
    public String freeWrite() {
        return "free/freeWrite";
    }

    @RequestMapping("/freeModify")
    public String freeModify() {
        return "free/freeModify";
    }
}
