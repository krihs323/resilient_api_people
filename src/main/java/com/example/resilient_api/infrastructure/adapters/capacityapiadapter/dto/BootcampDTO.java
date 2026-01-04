package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class BootcampDTO {
    Long id;
    String name;
    String description;
    LocalDate launchDate;
    Integer durationWeeks;
}
