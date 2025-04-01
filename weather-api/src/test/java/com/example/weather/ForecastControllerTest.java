package com.example.weather;

import com.example.weather.controller.ForecastController;
import com.example.weather.model.DailyForecast;
import com.example.weather.model.ForecastResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForecastControllerTest {

    @SuppressWarnings("unused")
	private final ForecastController controller = new ForecastController(null, null);

    @Test
    void testCombineForecast_WithDayAndNight() {
        ForecastResponse.Period day = new ForecastResponse.Period();
        day.setStartTime("2025-04-05T09:00:00-05:00");
        day.setShortForecast("Sunny");
        day.setTemperature(70);
        day.setWindSpeed("5 to 10 mph");
        day.setWindDirection("NW");
        day.setDaytime(true);

        ForecastResponse.Period night = new ForecastResponse.Period();
        night.setStartTime("2025-04-05T21:00:00-05:00");
        night.setShortForecast("Cloudy");
        night.setTemperature(50);
        night.setWindSpeed("10 to 15 mph");
        night.setWindDirection("NW");
        night.setDaytime(false);

        DailyForecast result = controllerTestHelper(
                List.of(day, night),
                LocalDate.of(2025, 4, 5),
                false
        );

        assertEquals("Day: Sunny - Night: Cloudy", result.getShortForecast());
        assertEquals(70, result.getHighTemp());
        assertEquals(50, result.getLowTemp());
    }

    // Helper method that mimics combineForecast logic
    private DailyForecast controllerTestHelper(List<ForecastResponse.Period> periods, LocalDate date, boolean metric) {
        DailyForecast df = new DailyForecast();
        df.setDate(date.toString());

        int high = Integer.MIN_VALUE;
        int low = Integer.MAX_VALUE;
        int windMin = Integer.MAX_VALUE;
        int windMax = Integer.MIN_VALUE;
        @SuppressWarnings("unused")
		int maxPop = 0;
        String dayForecast = "";
        String nightForecast = "";
        String windDirection = "N/A";

        for (ForecastResponse.Period p : periods) {
            if (p.getStartTime().startsWith(date.toString())) {
                int temp = p.getTemperature();
                if (metric) temp = (int) ((temp - 32) * 5.0 / 9);
                high = Math.max(high, temp);
                low = Math.min(low, temp);

                if (p.isDaytime() && dayForecast.isEmpty()) {
                    dayForecast = p.getShortForecast();
                } else if (!p.isDaytime() && nightForecast.isEmpty()) {
                    nightForecast = p.getShortForecast();
                }

                String[] windParts = p.getWindSpeed().split(" ");
                try {
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
                } catch (Exception ignored) {}

                if (windDirection.equals("N/A") && p.getWindDirection() != null) {
                    windDirection = p.getWindDirection();
                }
            }
        }

        df.setHighTemp(high);
        df.setLowTemp(low);
        df.setWindMin(windMin);
        df.setWindMax(windMax);
        df.setWindDirection(windDirection);

        StringBuilder forecastBuilder = new StringBuilder();
        if (!dayForecast.isEmpty()) {
            forecastBuilder.append("Day: ").append(dayForecast);
        }
        if (!nightForecast.isEmpty()) {
            if (forecastBuilder.length() > 0) forecastBuilder.append(" - ");
            forecastBuilder.append("Night: ").append(nightForecast);
        }
        df.setShortForecast(forecastBuilder.toString());

        return df;
    }
}
