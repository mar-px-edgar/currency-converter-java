package org.example.client;

import org.example.model.ExchangeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;

@Service
public class ExchangeRateClient {

    private final RestTemplate restTemplate;
    public ExchangeRateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExchangeResponse getRates(String base) {
        String url = "https://v6.exchangerate-api.com/v6/6017ec15ad8e55f942601fd0/latest/" + base;
        return restTemplate.getForObject(url, ExchangeResponse.class);
    }
}



