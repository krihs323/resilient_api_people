package com.example.resilient_api.domain.model;

import java.time.LocalDate;
public record Bootcamp(Long id, String name, String description, LocalDate launchDate, Integer durationWeeks) {
}