package org.example.dto;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@RequiredArgsConstructor
public class ConvertRequest {
    private String from;
    private String to;
    private double amount;
}
