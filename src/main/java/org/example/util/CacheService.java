package org.example.util;

import lombok.RequiredArgsConstructor;
import org.example.util.entity.ExchangeRateEntity;
import org.example.model.ExchangeResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository repository;

    private static final long TTL = 3600000; // 1 hour

    public ExchangeResponse getRates(String base) {
        Optional<ExchangeRateEntity> entry =
                repository.findByBaseCodeAndExpiryTimeGreaterThan(base, System.currentTimeMillis());

        if (entry.isPresent()) {
            //log("DB Cache hit");

            ExchangeRateEntity entity = entry.get();

            return ExchangeResponse.builder()
                    .base_code(entity.getBaseCode())
                    .conversion_rates(entity.getConversionRates())
                    .build();
        }

        //log("Cache miss");
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
        //log("Cleaning expired cache");
        repository.deleteByExpiryTimeLessThan(System.currentTimeMillis());
    }
}