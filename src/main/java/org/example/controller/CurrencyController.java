package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.example.dto.ConvertRequest;
import org.example.service.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @PostMapping("/convert")
    public ResponseEntity<Double> convert(@RequestBody ConvertRequest request) {
        Double result = currencyService.convert(request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
