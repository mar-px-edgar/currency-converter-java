package org.example.util;

import org.example.model.ExchangeResponse;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CacheService {
    private final Map<String, ExchangeResponse> cache = new HashMap<>();

    public ExchangeResponse getRates(String base) {
        return cache.get(base);
    }

    public void saveRates(String base, ExchangeResponse response) {
        cache.put(base, response);
    }
}
