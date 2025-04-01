package com.example.weather;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "Weather API is up!";
    }
}
