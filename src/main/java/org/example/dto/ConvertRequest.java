package org.example.dto;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class ConvertRequest {
    private String from;
    private String to;
    private double amount;
}
