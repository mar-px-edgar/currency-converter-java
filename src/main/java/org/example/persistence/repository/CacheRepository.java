package org.example.persistence.repository;

import org.example.persistence.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheRepository extends JpaRepository<ExchangeRateEntity, String> {

    Optional<ExchangeRateEntity> findByBaseCodeAndExpiryTimeGreaterThan(String baseCode, long now);

    void deleteByExpiryTimeLessThan(long now);
}