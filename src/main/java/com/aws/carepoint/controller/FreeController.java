package com.aws.carepoint.controller;

import com.aws.carepoint.dto.FreeDto;
import com.aws.carepoint.service.FreeService;
import com.aws.carepoint.util.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/free")
public class FreeController {

    @Autowired
    private FreeService freeService;

    @GetMapping("/freeList")
    public String freeList(
            @ModelAttribute("scri") SearchCriteria scri,
            Model model) {

        Map<String, Object> result = freeService.getFreeList(scri);
        model.addAllAttributes(result);

        return "free/freeList";
    }

    @GetMapping("/freeContents/{id}")
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

    @GetMapping("/freeWrite")
    public String freeWrite() {
        return "free/freeWrite";
    }

    @PostMapping("/freeWriteAction")
    public String freeWriteAction(
            @ModelAttribute FreeDto freeDto,
            RedirectAttributes rttr
    ) {
        int value = freeService.writeFree(freeDto);

        if (value > 0) {
            rttr.addFlashAttribute("msg", "게시글이 작성되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "게시글 작성이 안되었습니다.");
        }

        return "free/freeList";
    }

    @GetMapping("/freeModify")
    public String freeModify() {
        return "free/freeModify";
    }
}
