package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonServicePort {
    Mono<Person> registerPerson(Person person, String messageId);
}
