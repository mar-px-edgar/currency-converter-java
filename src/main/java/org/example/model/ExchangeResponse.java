package org.example.model;

import lombok.*;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeResponse {
    private String base_code;
    private Map<String, Double> conversion_rates;
}