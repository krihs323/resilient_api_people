package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.domain.spi.PersonPersistencePort;
import com.example.resilient_api.domain.usecase.PersonUseCase;
import com.example.resilient_api.domain.api.PersonServicePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.PersonPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.PersonEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.PersonBootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.PersonRepository;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.PersonBootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final PersonRepository personRepository;
        private final PersonEntityMapper personEntityMapper;
        private final PersonBootcampRepository bootcampCapacitiesRepository;
        private final PersonBootcampEntityMapper personBootcampEntityMapper;
        private final DatabaseClient databaseClient;

        @Bean
        public PersonPersistencePort personPersistencePort() {
                return new PersonPersistenceAdapter(personRepository, personEntityMapper, bootcampCapacitiesRepository, personBootcampEntityMapper, databaseClient);
        }

        @Bean
        public PersonServicePort personServicePort(PersonPersistencePort personPersistencePort, BootcampGateway bootcampGateway){
                return new PersonUseCase(personPersistencePort, bootcampGateway);
        }
}
