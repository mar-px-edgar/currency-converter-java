package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ConvertRequest;
import org.example.dto.ExchangeResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRateClient client;
    private final CacheService cacheService;

    public double convert(ConvertRequest request) {
        ExchangeResponse response = cacheService.getRates(request.getFrom());

        if (response == null) {
            response = client.getRates(request.getFrom());
            cacheService.saveRates(request.getFrom(), response);
        }

        Double rate = response.getConversion_rates().get(request.getTo());

        if (rate == null) {
            throw new RuntimeException("Something went wrong");
        }

        return request.getAmount() * rate;
    }
}
