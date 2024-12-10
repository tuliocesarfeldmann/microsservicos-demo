package com.fintech.prototype.consulta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultRequestDTO {

    private String agency;

    private String account;

}
