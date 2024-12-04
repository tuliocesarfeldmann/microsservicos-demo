package com.fintech.prototype.consulta.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponseDTO {

    private String error;

    private String details;

}
