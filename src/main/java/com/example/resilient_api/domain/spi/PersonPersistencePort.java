package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonPersistencePort {
    Mono<Person> save(Person person);
    Mono<Boolean> existByName(String name);
    Mono<Boolean> saveBootcamps(Long idPerson, Person person, String messageId);
}
