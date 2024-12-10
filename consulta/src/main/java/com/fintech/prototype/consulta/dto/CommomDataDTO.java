package com.fintech.prototype.consulta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("CommomData")
public class CommomDataDTO {
    @Id
    private String identifier;

    private String agency;

    private String account;

    private String document;

    private String name;

}
