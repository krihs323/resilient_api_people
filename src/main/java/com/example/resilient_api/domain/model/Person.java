package com.example.resilient_api.domain.model;

import java.util.List;

public record Person(Long id, String name, String email, Integer age, List<PersonBootcamps> personBootcampsList) {
}