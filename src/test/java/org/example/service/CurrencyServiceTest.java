package org.example.service;

import org.example.dto.ConvertRequest;
import org.example.dto.ExchangeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private ExchangeRateClient client;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void convert_shouldUseCache_whenCacheHit() {
        ConvertRequest request = new ConvertRequest("USD", "EUR", 100);

        ExchangeResponse response = ExchangeResponse.builder()
                .base_code("USD")
                .conversion_rates(Map.of("EUR", 0.9))
                .build();

        when(cacheService.getRates("USD")).thenReturn(response);

        double result = currencyService.convert(request);

        assertEquals(90.0, result);

        verify(cacheService, times(1)).getRates("USD");
        verifyNoInteractions(client);
        verify(cacheService, never()).saveRates(any(), any());
    }

    @Test
    void convert_shouldCallApiAndCache_whenCacheMiss() {
        ConvertRequest request = new ConvertRequest("USD", "EUR", 100);

        ExchangeResponse response = ExchangeResponse.builder()
                .base_code("USD")
                .conversion_rates(Map.of("EUR", 0.9))
                .build();

        when(cacheService.getRates("USD")).thenReturn(null);
        when(client.getRates("USD")).thenReturn(response);

        double result = currencyService.convert(request);

        assertEquals(90.0, result);

        verify(cacheService, times(1)).getRates("USD");
        verify(client, times(1)).getRates("USD");
        verify(cacheService, times(1)).saveRates("USD", response);
    }

    @Test
    void convert_shouldThrowException_whenRateNotFound() {
        ConvertRequest request = new ConvertRequest("USD", "GBP", 100);

        ExchangeResponse response = ExchangeResponse.builder()
                .base_code("USD")
                .conversion_rates(Map.of("EUR", 0.9)) // GBP missing
                .build();

        when(cacheService.getRates("USD")).thenReturn(response);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> currencyService.convert(request));

        assertEquals("Something went wrong", exception.getMessage());

        verify(cacheService, times(1)).getRates("USD");
        verifyNoInteractions(client);
    }
}
