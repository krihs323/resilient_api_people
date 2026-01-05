package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.Person;
import reactor.core.publisher.Mono;

public interface BootcampGateway {

    Mono<Bootcamp> getBootcampById(Long idBootcamp, String messageId);
    Mono<Void> savePersonReport(Long idBootcamp, Person savedPerson, String messageId);
}
