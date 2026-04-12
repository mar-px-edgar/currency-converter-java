package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ConvertRequest;
import org.example.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void convert_shouldReturnResult_whenRequestIsValid() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(currencyController).build();

        ConvertRequest request = new ConvertRequest("USD", "EUR", 100);
        double expectedResult = 90.0;

        when(currencyService.convert(any(ConvertRequest.class)))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedResult)));

        verify(currencyService, times(1)).convert(any(ConvertRequest.class));
    }

    @Test
    void convert_shouldReturn500_whenServiceThrowsException() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(currencyController).build();

        ConvertRequest request = new ConvertRequest("USD", "EUR", 100);

        when(currencyService.convert(any(ConvertRequest.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(currencyService, times(1)).convert(any(ConvertRequest.class));
    }
}
