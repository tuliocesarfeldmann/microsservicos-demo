package com.fintech.prototype.saque.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponseDTO {

    private String error;

    private String details;

}
