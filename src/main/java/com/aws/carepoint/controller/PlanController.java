package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/plan")
public class PlanController {

    @RequestMapping("/plan")
    public String plan() {return "plan/plan";}

    @RequestMapping("/hospital")
    public String hospital() {return "plan/hospital";}
}
