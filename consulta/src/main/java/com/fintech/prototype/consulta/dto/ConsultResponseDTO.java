package com.fintech.prototype.consulta.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConsultResponseDTO {

    private String document;

    private String name;

}
