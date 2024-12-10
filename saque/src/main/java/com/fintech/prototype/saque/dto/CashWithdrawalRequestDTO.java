package com.fintech.prototype.saque.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CashWithdrawalRequestDTO {

    private BigDecimal amount;

    private String password;

}
