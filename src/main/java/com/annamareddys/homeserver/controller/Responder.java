package com.annamareddys.homeserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response")

public class Responder{
    @GetMapping("/checkhealth")
    public String checkHealth(){
        return "All is well :)";
    }
}
