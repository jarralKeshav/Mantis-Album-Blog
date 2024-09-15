package com.bishnah.springrestdemo.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class HomeController {

    @GetMapping("/api/v1")
    public String demo() {
        return "Hello World form Keshav Jrall";
    }
}   