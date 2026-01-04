package com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "person")
@Getter
@Setter
@RequiredArgsConstructor
public class PersonEntity {
    @Id
    private Long id;
    private String name;
    private String email;
    private Integer age;
}
