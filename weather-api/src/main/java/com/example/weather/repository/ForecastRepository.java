package com.example.weather.repository;

import com.example.weather.entity.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForecastRepository extends JpaRepository<ForecastEntity, Long> {
}
