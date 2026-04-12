package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistence.entity.ExchangeRateEntity;
import org.example.dto.ExchangeResponse;
import org.example.persistence.repository.CacheRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository repository;

    private static final long TTL = 3600000; // 1 hour

    public ExchangeResponse getRates(String base) {
        Optional<ExchangeRateEntity> entry =
                repository.findByBaseCodeAndExpiryTimeGreaterThan(base, System.currentTimeMillis());

        if (entry.isPresent()) {
            log.info("DB Cache hit");

            ExchangeRateEntity entity = entry.get();

            return ExchangeResponse.builder()
                    .base_code(entity.getBaseCode())
                    .conversion_rates(entity.getConversionRates())
                    .build();
        }

        log.info("Cache miss");
        return null;
    }

    public void saveRates(String base, ExchangeResponse response) {

        ExchangeRateEntity entity = ExchangeRateEntity.builder()
                .baseCode(base)
                .conversionRates(response.getConversion_rates())
                .expiryTime(System.currentTimeMillis() + TTL)
                .build();

        repository.save(entity);
    }

    //Scheduled cleanup every 72 hrs
    @Scheduled(fixedRate = 259200000)
    public void cleanUp() {
        log.info("Cleaning expired cache");
        repository.deleteByExpiryTimeLessThan(System.currentTimeMillis());
    }
}