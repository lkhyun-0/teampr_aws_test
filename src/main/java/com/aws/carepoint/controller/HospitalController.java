package com.aws.carepoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HospitalController {

    @RequestMapping("/hospital/plan")
    public String plan() {return "hospital/plan";}
}
