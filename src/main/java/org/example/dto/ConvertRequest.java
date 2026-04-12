package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ConvertRequest {
    private String from;
    private String to;
    private double amount;
}
