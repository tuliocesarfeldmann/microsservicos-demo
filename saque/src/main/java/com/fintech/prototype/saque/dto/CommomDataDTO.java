package com.fintech.prototype.saque.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@RedisHash("CommomData")
public class CommomDataDTO {
    @Id
    private String identifier;

    private String agency;

    private String account;

    private String document;

    private String name;

    private BigDecimal amount;

    private String password;

}
