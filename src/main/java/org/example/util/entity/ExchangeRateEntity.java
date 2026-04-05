package org.example.util.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "exchange_rates")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateEntity {

    @Id
    private String baseCode;

    @ElementCollection
    @CollectionTable(name = "conversion_rates", joinColumns = @JoinColumn(name = "base_code"))
    @MapKeyColumn(name = "currency")
    @Column(name = "rate")
    private Map<String, Double> conversionRates;

    private long expiryTime;
}