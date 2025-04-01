package com.example.weather.model;

import java.util.List;

public class ForecastResponse {
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static class Properties {
        private List<Period> periods;

        public List<Period> getPeriods() {
            return periods;
        }

        public void setPeriods(List<Period> periods) {
            this.periods = periods;
        }
    }

    public static class Period {
        private String name;
        private String startTime;
        private String endTime;
        private int temperature;
        private String temperatureUnit;
        private String windSpeed;
        private String shortForecast;
        private boolean isDaytime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public String getTemperatureUnit() {
            return temperatureUnit;
        }

        public void setTemperatureUnit(String temperatureUnit) {
            this.temperatureUnit = temperatureUnit;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(String windSpeed) {
            this.windSpeed = windSpeed;
        }

        public String getShortForecast() {
            return shortForecast;
        }

        public void setShortForecast(String shortForecast) {
            this.shortForecast = shortForecast;
        }

        public boolean isDaytime() {
            return isDaytime;
        }

        public void setDaytime(boolean daytime) {
            isDaytime = daytime;
        }
        private String windDirection;
        // other fields...

        public String getWindDirection() {
            return windDirection;
        }

        public void setWindDirection(String windDirection) {
            this.windDirection = windDirection;
        }

        private ProbabilityOfPrecipitation probabilityOfPrecipitation;

        public ProbabilityOfPrecipitation getProbabilityOfPrecipitation() {
            return probabilityOfPrecipitation;
        }

        public void setProbabilityOfPrecipitation(ProbabilityOfPrecipitation probabilityOfPrecipitation) {
            this.probabilityOfPrecipitation = probabilityOfPrecipitation;
        }

        public static class ProbabilityOfPrecipitation {
            private Integer value;

            public Integer getValue() {
                return value != null ? value : 0;
            }

            public void setValue(Integer value) {
                this.value = value;
            }
        }

    }
}
