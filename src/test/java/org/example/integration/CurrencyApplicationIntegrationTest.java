package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ConvertRequest;
import org.example.dto.ExchangeResponse;
import org.example.service.ExchangeRateClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class CurrencyApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateClient exchangeRateClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
     void convert_shouldFetchFromApi_thenCache_thenReuseCache() throws Exception{
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


        // Act 1: First call (cache miss → API call)
        ResultActions result1 =
                mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Act 2: Second call (cache hit → NO API call)
        ResultActions result2 =
                mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert responses

        result1
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("90.0"));
        result2
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("90.0"));

        verify(exchangeRateClient, times(1)).getRates("USD");
    }
}