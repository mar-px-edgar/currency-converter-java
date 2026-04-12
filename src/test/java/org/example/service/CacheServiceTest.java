package org.example.service;

import org.example.dto.ExchangeResponse;
import org.example.persistence.entity.ExchangeRateEntity;
import org.example.persistence.repository.CacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private CacheRepository repository;

    @InjectMocks
    private CacheService cacheService;

    @Test
    void getRates_shouldReturnResponse_whenCacheHit() {
        String base = "USD";

        ExchangeRateEntity entity = ExchangeRateEntity.builder()
                .baseCode(base)
                .conversionRates(Map.of("EUR", 0.9))
                .expiryTime(System.currentTimeMillis() + 10000)
                .build();

        when(repository.findByBaseCodeAndExpiryTimeGreaterThan(eq(base), anyLong()))
                .thenReturn(Optional.of(entity));

        ExchangeResponse result = cacheService.getRates(base);

        assertNotNull(result);
        assertEquals(base, result.getBase_code());
        assertEquals(entity.getConversionRates(), result.getConversion_rates());

        verify(repository, times(1))
                .findByBaseCodeAndExpiryTimeGreaterThan(eq(base), anyLong());
    }

    @Test
    void getRates_shouldReturnNull_whenCacheMiss() {
        String base = "USD";

        when(repository.findByBaseCodeAndExpiryTimeGreaterThan(eq(base), anyLong()))
                .thenReturn(Optional.empty());

        ExchangeResponse result = cacheService.getRates(base);

        assertNull(result);

        verify(repository, times(1))
                .findByBaseCodeAndExpiryTimeGreaterThan(eq(base), anyLong());
    }

    @Test
    void saveRates_shouldPersistEntity() {
        String base = "USD";

        ExchangeResponse response = ExchangeResponse.builder()
                .base_code(base)
                .conversion_rates(Map.of("EUR", 0.9))
                .build();

        cacheService.saveRates(base, response);

        verify(repository, times(1)).save(argThat(entity ->
                entity.getBaseCode().equals(base) &&
                        entity.getConversionRates().equals(response.getConversion_rates()) &&
                        entity.getExpiryTime() > System.currentTimeMillis()
        ));
    }

    @Test
    void cleanUp_shouldDeleteExpiredEntries() {
        cacheService.cleanUp();

        verify(repository, times(1))
                .deleteByExpiryTimeLessThan(anyLong());
    }
}