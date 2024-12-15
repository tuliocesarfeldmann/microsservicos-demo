package com.fintech.prototype.saque.repository;

import com.fintech.prototype.saque.dto.CommomDataDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<CommomDataDTO, String> {
}
