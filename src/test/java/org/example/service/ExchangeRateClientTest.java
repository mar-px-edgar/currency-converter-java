package org.example.service;

import org.example.dto.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeRateClient exchangeRateClient;

    @Value("${app.apiURL}")
    private String API_URL;

    @BeforeEach
    void setUp() {
        // Inject value for @Value field
        ReflectionTestUtils.setField(exchangeRateClient, "apiUrl", API_URL);
    }

    @Test
    void getRates_shouldReturnExchangeResponse_whenApiCallSuccessful() throws Exception {
        String base = "USD";
        String expectedUrl = API_URL + base;

        ExchangeResponse mockResponse = new ExchangeResponse();

        when(restTemplate.getForObject(expectedUrl, ExchangeResponse.class))
                .thenReturn(mockResponse);

        ExchangeResponse result = exchangeRateClient.getRates(base);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate, times(1))
                .getForObject(expectedUrl, ExchangeResponse.class);
    }

    @Test
    void getRates_shouldReturnNull_whenExceptionOccurs() throws Exception {
        String base = "USD";
        String expectedUrl = API_URL + base;

        when(restTemplate.getForObject(expectedUrl, ExchangeResponse.class))
                .thenThrow(new RuntimeException("API error"));

        ExchangeResponse result = exchangeRateClient.getRates(base);

        assertNull(result);
        verify(restTemplate, times(1))
                .getForObject(expectedUrl, ExchangeResponse.class);
    }
}
