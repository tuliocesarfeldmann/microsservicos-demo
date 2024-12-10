package com.fintech.prototype.consulta.repository;

import com.fintech.prototype.consulta.dto.CommomDataDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<CommomDataDTO, String> {
}
