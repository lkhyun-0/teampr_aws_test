package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PlanController {

    @RequestMapping("/plan/plan")
    public String plan() {return "plan/plan";}
}
