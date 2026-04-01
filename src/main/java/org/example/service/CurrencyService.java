package org.example.service;

import org.example.client.ExchangeRateClient;
import org.example.dto.ConvertRequest;
import org.example.model.ExchangeResponse;
import org.example.util.CacheService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {
    private final ExchangeRateClient client;
    private final CacheService cacheService;

    public CurrencyService(ExchangeRateClient client, CacheService cacheService) {
        this.client = client;
        this.cacheService = cacheService;
    }

    public double convert(ConvertRequest request) {
        ExchangeResponse response = cacheService.getRates(request.getFrom());

        if (response == null) {
            response = client.getRates(request.getFrom());
            cacheService.saveRates(request.getFrom(), response);
        }

        Double rate = response.getRates().get(request.getTo());

        if (rate == null) {
            throw new RuntimeException("Currency not supported");
        }

        return request.getAmount() * rate;
    }
}
