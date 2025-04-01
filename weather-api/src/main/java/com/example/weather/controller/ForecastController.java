package com.example.weather.controller;

import com.example.weather.entity.ForecastEntity;
import com.example.weather.model.DailyForecast;
import com.example.weather.model.ForecastResponse;
import com.example.weather.service.WeatherService;
import com.example.weather.repository.ForecastRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/weather")
public class ForecastController {

    private final WeatherService weatherService;
    private final ForecastRepository forecastRepository;

    public ForecastController(WeatherService weatherService, ForecastRepository forecastRepository) {
        this.weatherService = weatherService;
        this.forecastRepository = forecastRepository;
    }

    @GetMapping("/forecast/{lat},{lon}")
    public Mono<Map<String, Object>> getForecast(
            @PathVariable double lat,
            @PathVariable double lon,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "false") boolean metric
    ) {
        double roundedLat = Math.round(lat * 1000.0) / 1000.0;
        double roundedLon = Math.round(lon * 1000.0) / 1000.0;
        LocalDate finalDate = (date != null) ? date : LocalDate.now();

        return weatherService.getGridInfo(roundedLat, roundedLon)
                .flatMap(point -> {
                    String office = point.getProperties().getGridId();
                    int gridX = point.getProperties().getGridX();
                    int gridY = point.getProperties().getGridY();

                    return weatherService.getForecast(office, gridX, gridY)
                            .map(forecast -> {
                                List<ForecastResponse.Period> periods = forecast.getProperties().getPeriods();
                                DailyForecast df = combineForecast(periods, finalDate, metric);

                                ForecastEntity entity = new ForecastEntity();
                                entity.setLat(roundedLat);
                                entity.setLon(roundedLon);
                                entity.setDate(finalDate.toString());
                                entity.setMetric(metric);
                                entity.setHighTemp(df.getHighTemp());
                                entity.setLowTemp(df.getLowTemp());
                                entity.setWindMin(df.getWindMin());
                                entity.setWindMax(df.getWindMax());
                                entity.setPop(df.getPop());
                                entity.setShortForecast(df.getShortForecast());
                                entity.setSavedAt(LocalDateTime.now());

                                forecastRepository.save(entity);

                                return Map.of(
                                        "latitude", roundedLat,
                                        "longitude", roundedLon,
                                        "date", df.getDate(),
                                        "forecast", df.getShortForecast().replaceAll("Day:\\s*-", "Day: -"),
                                        "pop", df.getPop(),
                                        "temperature", Map.of(
                                                "high", df.getHighTemp(),
                                                "low", df.getLowTemp(),
                                                "unit", metric ? "C" : "F"
                                        ),
                                        "wind", Map.of(
                                                "maxSpeed", df.getWindMax(),
                                                "minSpeed", df.getWindMin(),
                                                "direction", df.getWindDirection() != null ? df.getWindDirection() : "N/A",
                                                "unit", metric ? "kph" : "mph"
                                        )
                                );
                            });
                });
    }

    private DailyForecast combineForecast(List<ForecastResponse.Period> periods, LocalDate date, boolean metric) {
        DailyForecast df = new DailyForecast();
        df.setDate(date.toString());

        int high = Integer.MIN_VALUE;
        int low = Integer.MAX_VALUE;
        int windMin = Integer.MAX_VALUE;
        int windMax = Integer.MIN_VALUE;
        int maxPop = 0;
        StringBuilder dayBuilder = new StringBuilder();
        StringBuilder nightBuilder = new StringBuilder();
        String windDirection = "N/A";

        System.out.println("==== Forecast Debug Start ====");
        System.out.println("Target Date: " + date);

        
        for (ForecastResponse.Period p : periods) {
            if (p.getStartTime().startsWith(date.toString())) {
                OffsetDateTime startDateTime = OffsetDateTime.parse(p.getStartTime());
                LocalTime time = startDateTime.toLocalTime();

                boolean isDaytime = time.isAfter(LocalTime.of(5, 59)) && time.isBefore(LocalTime.of(18, 1)); // 6 AM to 6 PM

                if (isDaytime) {
                    if (!p.getShortForecast().isEmpty()) {
                        if (dayBuilder.length() > 0) dayBuilder.append(", ");
                        dayBuilder.append(p.getShortForecast());
                    }
                } else {
                    if (!p.getShortForecast().isEmpty()) {
                        if (nightBuilder.length() > 0) nightBuilder.append(", ");
                        nightBuilder.append(p.getShortForecast());
                    }
                }
             // Temperature
                int temp = p.getTemperature();
                if (metric) temp = (int) ((temp - 32) * 5.0 / 9);
                high = Math.max(high, temp);
                low = Math.min(low, temp);

                // Wind
                try {
                    String[] windParts = p.getWindSpeed().split(" ");
                    int min = Integer.parseInt(windParts[0]);
                    int max = min;
                    if (windParts.length >= 3 && windParts[1].equals("to")) {
                        max = Integer.parseInt(windParts[2]);
                    }
                    if (metric) {
                        min = (int) (min * 1.60934);
                        max = (int) (max * 1.60934);
                    }
                    windMin = Math.min(windMin, min);
                    windMax = Math.max(windMax, max);
                } catch (Exception ex) {
                    System.out.println("   ⚠ Error parsing windSpeed: " + p.getWindSpeed());
                }

                if (windDirection.equals("N/A") && p.getWindDirection() != null) {
                    windDirection = p.getWindDirection();
                }

                // Precipitation
                int pop = (p.getProbabilityOfPrecipitation() != null)
                        ? p.getProbabilityOfPrecipitation().getValue()
                        : 0;
                maxPop = Math.max(maxPop, pop);
            }
        }

        System.out.println("Day Forecast Collected: " + dayBuilder);
        System.out.println("Night Forecast Collected: " + nightBuilder);
        System.out.println("==== Forecast Debug End ====");

        df.setHighTemp(high == Integer.MIN_VALUE ? 0 : high);
        df.setLowTemp(low == Integer.MAX_VALUE ? 0 : low);
        df.setWindMin(windMin == Integer.MAX_VALUE ? 0 : windMin);
        df.setWindMax(windMax == Integer.MIN_VALUE ? 0 : windMax);
        df.setPop(maxPop);
        df.setWindDirection(windDirection);

        String[] parts = dayBuilder.toString().split(",", 2); // split into 2 parts only

        String dayPart = parts[0].trim();
        String nightPart = parts.length > 1 ? parts[1].trim() : nightBuilder.toString().trim();

        StringBuilder forecastBuilder = new StringBuilder();

        if (!dayPart.isEmpty()) {
            forecastBuilder.append("Day: ").append(dayPart);
        }

        if (!nightPart.isEmpty()) {
            if (forecastBuilder.length() > 0) forecastBuilder.append(" - ");
            forecastBuilder.append("Night: ").append(nightPart);
        }

        df.setShortForecast(forecastBuilder.toString());

//        df.setShortForecast("Day: " + dayBuilder + " - Night: " + nightBuilder);

        return df;
    }

}
