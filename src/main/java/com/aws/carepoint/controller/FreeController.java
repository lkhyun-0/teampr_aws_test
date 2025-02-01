package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FreeController {

    @RequestMapping("/free/freeList")
    public String freeList() {return "free/freeList";}

    @RequestMapping("/free/freeContents")
    public String freeContents() {return "free/freeContents";}

    @RequestMapping("/free/freeWrite")
    public String freeWrite() {return "free/freeWrite";}
}
