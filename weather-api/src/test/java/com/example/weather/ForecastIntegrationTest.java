package com.example.weather;

import com.example.weather.model.ForecastResponse;
import com.example.weather.model.ForecastResponse.Period;
import com.example.weather.model.PointResponse;
import com.example.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void testForecastEndpoint_withMockedData() throws Exception {
        // Mock Periods
        Period day = new Period();
        day.setStartTime("2025-04-05T09:00:00-05:00");
        day.setShortForecast("Sunny");
        day.setTemperature(72);
        day.setWindSpeed("5 mph");
        day.setWindDirection("N");
        day.setDaytime(true);

        Period night = new Period();
        night.setStartTime("2025-04-05T21:00:00-05:00");
        night.setShortForecast("Cloudy");
        night.setTemperature(52);
        night.setWindSpeed("8 mph");
        night.setWindDirection("N");
        night.setDaytime(false);

        ForecastResponse forecastResponse = new ForecastResponse();
        ForecastResponse.Properties props = new ForecastResponse.Properties();
        props.setPeriods(List.of(day, night));
        forecastResponse.setProperties(props);

        PointResponse.Properties pointProps = new PointResponse.Properties();
        pointProps.setGridId("TUL");
        pointProps.setGridX(42);
        pointProps.setGridY(96);
        PointResponse pointResponse = new PointResponse();
        pointResponse.setProperties(pointProps);

        when(weatherService.getGridInfo(anyDouble(), anyDouble())).thenReturn(Mono.just(pointResponse));
        when(weatherService.getForecast(anyString(), anyInt(), anyInt())).thenReturn(Mono.just(forecastResponse));

        mockMvc.perform(get("/weather/forecast/36.244,-94.149?date=2025-04-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forecast").value("Day: Sunny - Night: Cloudy"))
                .andExpect(jsonPath("$.temperature.high").value(72))
                .andExpect(jsonPath("$.temperature.low").value(52))
                .andExpect(jsonPath("$.wind.direction").value("N"));
    }
}
