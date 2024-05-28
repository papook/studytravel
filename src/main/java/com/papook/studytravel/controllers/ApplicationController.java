package com.papook.studytravel.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
