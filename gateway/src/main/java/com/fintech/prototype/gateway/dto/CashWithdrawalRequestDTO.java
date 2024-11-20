package com.fintech.prototype.gateway.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CashWithdrawalRequestDTO {

    private BigDecimal amount;

    private String password;

}
