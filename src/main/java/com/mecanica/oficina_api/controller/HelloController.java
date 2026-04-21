package com.mecanica.oficina_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    
    @RequestMapping("/ola")
    public String olaMundo() {
        return "Olá, Mundo!";
    }
}
