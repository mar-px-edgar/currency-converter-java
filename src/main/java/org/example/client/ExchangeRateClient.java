package org.example.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.ApiException;
import org.example.model.ExchangeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateClient {
    @Value("${app.apiURL}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ExchangeResponse getRates(String base) throws ApiException {
        String url = apiUrl + base;

        try {
            return restTemplate.getForObject(url, ExchangeResponse.class);

        } catch (Exception e){
            log.info("Api exception:", e);
        }
        return null;
    }
}



