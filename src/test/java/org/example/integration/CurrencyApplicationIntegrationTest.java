package org.example.integration;

import org.example.dto.ConvertRequest;
import org.example.dto.ExchangeResponse;
import org.example.service.ExchangeRateClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private ExchangeRateClient exchangeRateClient;

    @Test
    void convert_shouldFetchFromApi_thenCache_thenReuseCache() {
        // Arrange
        ConvertRequest request = new ConvertRequest("USD", "EUR", 100);

        ExchangeResponse mockResponse = ExchangeResponse.builder()
                .base_code("USD")
                .conversion_rates(Map.of("EUR", 0.9))
                .build();

        when(exchangeRateClient.getRates("USD"))
                .thenReturn(mockResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ConvertRequest> entity =
                new HttpEntity<>(request, headers);

        // Act 1: First call (cache miss → API call)
        ResponseEntity<Double> response1 =
                restTemplate.postForEntity("/convert", entity, Double.class);

        // Act 2: Second call (cache hit → NO API call)
        ResponseEntity<Double> response2 =
                restTemplate.postForEntity("/convert", entity, Double.class);

        // Assert responses
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(90.0, response1.getBody());

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(90.0, response2.getBody());

        // 🔥 KEY ASSERTION: API called only once → proves caching works
        verify(exchangeRateClient, times(1)).getRates("USD");
    }
}