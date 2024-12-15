package com.fintech.prototype.gateway.controller;

import com.fintech.prototype.gateway.dto.ConsultRequestDTO;
import com.fintech.prototype.gateway.dto.ConsultResponseDTO;
import com.fintech.prototype.gateway.service.ConsultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@Slf4j
public class ConsultController {

    @Autowired
    ConsultService service;

    @PostMapping(value = "/consult")
    public ResponseEntity<?> consult(@RequestBody @Valid ConsultRequestDTO consult) {
        try {

            ConsultResponseDTO response = service.consultSend(consult);

            return ResponseEntity
                    .ok()
                    .body(response);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}
