# 🌤️ Weather Forecast API (Weather.gov)

A Spring Boot reactive REST API that integrates with the [Weather.gov API](https://www.weather.gov/documentation/services-web-api) to fetch and serve daily weather forecasts based on latitude and longitude.

---

## 🚀 Features

- Get weather forecast by coordinates and date
- Handles temperature, wind speed, direction, and precipitation
- Supports unit conversion (imperial/metric)
- Stores forecast history in H2 in-memory DB
- Reactive stack using **Spring WebFlux**
- Integration tested with mocked Weather.gov responses

---

## 📦 Technologies Used

- Java 17+
- Spring Boot 3+
- Spring WebFlux
- H2 In-Memory Database
- JUnit 5, Mockito
- Maven

---

## 📌 Endpoint

### `GET /weather/forecast/{lat},{lon}?date={yyyy-MM-dd}&metric={true|false}`

| Param     | Type    | Required | Description                        |
|-----------|---------|----------|------------------------------------|
| `lat`     | Double  | ✅       | Latitude of location               |
| `lon`     | Double  | ✅       | Longitude of location              |
| `date`    | Date    | ❌       | Forecast date (defaults to today)  |
| `metric`  | Boolean | ❌       | Metric units (C, kph) if true      |

#### 🔄 Example:

```
GET /weather/forecast/36.244,-94.149?date=2025-04-01&metric=true
```

---

## ✅ Sample JSON Response

```json
{
  "latitude": 36.244,
  "longitude": -94.149,
  "date": "2025-04-01",
  "forecast": "Day: Mostly Sunny - Night: Showers And Thunderstorms Likely",
  "pop": 70,
  "temperature": {
    "high": 22,
    "low": 17,
    "unit": "C"
  },
  "wind": {
    "minSpeed": 10,
    "maxSpeed": 25,
    "direction": "SW",
    "unit": "kph"
  }
}
```

---

## 🧪 Testing

### ✅ Run Integration Tests

```bash
mvn test
```

- `WeatherServiceTest`: Unit test for forecast fetching logic
- `ForecastControllerTest`: Full integration test using `WebTestClient` and `MockWebServer`

---

## 💾 H2 DB Console

Access via: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)  
- JDBC URL: `jdbc:h2:mem:testdb`  
- User: `sa`  
- Password: *(leave empty)*

---

## 🏁 Run the App

### 🔧 Prerequisites
- Java 17+
- Maven

### ▶️ Start Spring Boot app

```bash
mvn spring-boot:run
```

---

## 📂 Project Structure

```
├── controller         → REST Controller
├── service            → Weather service logic
├── model              → DTOs for Forecast API
├── entity             → JPA entity
├── repository         → Spring Data repo
├── test               → Unit + integration tests
```

---

## 📝 License

MIT License. Built for demonstration purposes.

---

## ✨ Author

**Your Name**  
[GitHub Profile](https://github.com/your-profile)
