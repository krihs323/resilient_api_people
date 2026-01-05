package com.example.resilient_api.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class UpdatePersonReportRequestDTO {

    private Long idBootcamp;
    private PersonDTO person;


}
