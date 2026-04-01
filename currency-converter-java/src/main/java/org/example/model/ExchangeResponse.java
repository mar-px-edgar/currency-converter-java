package org.example.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class ExchangeResponse {
    private Map<String, Double> rates;

}
