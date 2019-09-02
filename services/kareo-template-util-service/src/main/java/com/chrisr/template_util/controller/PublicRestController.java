package com.chrisr.template_util.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/public")
public interface PublicRestController {

    @GetMapping("/health")
    ResponseEntity<String> status();
}
