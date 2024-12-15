package com.fintech.prototype.gateway.controller;

import com.fintech.prototype.gateway.dto.CashWithdrawalRequestDTO;
import com.fintech.prototype.gateway.service.CashWithdrawalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@Slf4j
public class CashWithdrawalController {

    @Autowired
    CashWithdrawalService service;

    @PostMapping("/{identifier}/cash-withdrawal")
    public ResponseEntity<?> transaction(
            @RequestBody @Valid CashWithdrawalRequestDTO cashWithdrawal,
            @PathVariable("identifier") String identifier
    ) {
        try {
            return ResponseEntity
                    .ok()
                    .body(service.cashWithdrawalSend(cashWithdrawal, identifier));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}
