package com.papook.studytravel.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.papook.studytravel.Constants;

@RestController
@RequestMapping(Constants.API_BASE)
public class ApplicationController {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
