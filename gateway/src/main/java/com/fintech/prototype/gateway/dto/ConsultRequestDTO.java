package com.fintech.prototype.gateway.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConsultRequestDTO extends BaseConsultRequest {

    private String agency;

    private String account;

}
