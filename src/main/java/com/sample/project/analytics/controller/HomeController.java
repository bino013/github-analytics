package com.sample.project.analytics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author arvin on 8/16/20
 **/
@Controller
public class HomeController {

    @GetMapping("/index")
    public String index() {
        return "index.html";
    }

}



