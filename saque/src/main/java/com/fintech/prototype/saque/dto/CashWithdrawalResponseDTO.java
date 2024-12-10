package com.fintech.prototype.saque.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class CashWithdrawalResponseDTO {

    private String status;

}
