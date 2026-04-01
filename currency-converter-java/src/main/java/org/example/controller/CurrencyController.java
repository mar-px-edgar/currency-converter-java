package org.example.controller;

import org.example.dto.ConvertRequest;
import org.example.service.CurrencyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

@Controller
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/convert")
    public String convert(@ModelAttribute ConvertRequest request, Model model) throws Exception {
        double result = currencyService.convert(request);
        model.addAttribute("result", result);
        return "index";
    }
}
