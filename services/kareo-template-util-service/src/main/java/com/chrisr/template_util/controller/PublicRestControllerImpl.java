package com.chrisr.template_util.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class PublicRestControllerImpl implements PublicRestController {

    @Override
    public ResponseEntity<String> status() {
        String responseMessage = "Application is up. Current time on server = " + LocalDateTime.now();
        return ResponseEntity.ok().body(responseMessage);
    }
}
