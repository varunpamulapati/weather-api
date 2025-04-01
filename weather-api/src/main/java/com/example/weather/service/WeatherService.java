package com.example.weather.service;

import com.example.weather.model.ForecastResponse;
import com.example.weather.model.PointResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private final WebClient weatherWebClient;

    public WeatherService(WebClient weatherWebClient) {
        this.weatherWebClient = weatherWebClient;
    }

    public Mono<PointResponse> getGridInfo(double lat, double lon) {
        return weatherWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/points/{lat},{lon}")
                        .build(lat, lon))
                .retrieve()
                .bodyToMono(PointResponse.class);
    }

    public Mono<ForecastResponse> getForecast(String office, int gridX, int gridY) {
        return weatherWebClient.get()
                .uri("/gridpoints/{office}/{gridX},{gridY}/forecast", office, gridX, gridY)
                .retrieve()
                .bodyToMono(ForecastResponse.class);
    }
}
