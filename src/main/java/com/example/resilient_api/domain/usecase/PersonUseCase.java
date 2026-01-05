package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.domain.spi.PersonPersistencePort;
import com.example.resilient_api.domain.api.PersonServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.*;

@Slf4j
public class PersonUseCase implements PersonServicePort {

    private final PersonPersistencePort personPersistencePort;
    private final BootcampGateway bootcampGateway;

    public PersonUseCase(PersonPersistencePort personPersistencePort, BootcampGateway bootcampGateway) {
        this.personPersistencePort = personPersistencePort;
        this.bootcampGateway = bootcampGateway;
    }

    @Override
    public Mono<Person> registerPerson(Person person, String messageId) {
        return personPersistencePort.existByName(person.name())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.PERSON_ALREADY_EXISTS));
                    }
                    return validateDuplicate(person.personBootcampsList());
                })
                // Usamos then() para limpiar el flujo y pasar a la siguiente tarea lógica
                .then(
                        Flux.fromIterable(person.personBootcampsList())
                                // Ejecutamos las peticiones en paralelo de forma controlada
                                .flatMap(pr -> bootcampGateway.getBootcampById(pr.idBootcamp(), messageId))
                                .collectList()
                                .flatMap(list -> {
                                    if (haySolapamiento(list)) {
                                        return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMPS_VALIDATION_DATE));
                                    }
                                    return Mono.just(true);
                                })
                )
                // Cambiamos al guardado de la persona
                .then(personPersistencePort.save(person))
                .flatMap(savedPerson ->
                        saveBootcamps(savedPerson.id(), person, messageId)
                                .doOnSuccess(v ->
                                    // Por cada bootcamp inscrito, enviamos la señal al reporte de manera que no sea bloqueante
                                    person.personBootcampsList().forEach(pb ->
                                            bootcampGateway.savePersonReport(pb.idBootcamp(), savedPerson, messageId)
                                                    .subscribeOn(Schedulers.boundedElastic())
                                                    .subscribe(
                                                        success -> log.info("Report updated successfully"),
                                                        error -> log.error("Error saving report: {}", error.getMessage())
                                                    )
                                    )
                                )
                                .then(Mono.just(savedPerson))
                )
                // Log de error global para identificar exactamente qué parte falla
                .doOnError(e -> log.error("Error en registro para messageId {}: {}", messageId, e.getMessage()));
    }

    private boolean haySolapamiento(List<Bootcamp> bootcamps) {
        for (int i = 0; i < bootcamps.size(); i++) {
            for (int j = i + 1; j < bootcamps.size(); j++) {
                if (checkConflict(bootcamps.get(i), bootcamps.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkConflict(Bootcamp b1, Bootcamp b2) {
        LocalDate start1 = b1.launchDate();
        LocalDate end1 = start1.plusWeeks(b1.durationWeeks());

        LocalDate start2 = b2.launchDate();
        LocalDate end2 = start2.plusWeeks(b2.durationWeeks());

        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private Mono<Boolean> saveBootcamps(Long idPerson, Person person, String messageId) {
        return personPersistencePort.saveBootcamps(idPerson, person, messageId)
                .filter(isSaved -> isSaved)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.DATABASE_ERROR)));
    }

    private Mono<Boolean> validateDuplicate(List<PersonBootcamps> personBootcamps) {
        Set<PersonBootcamps> uniqueCapacities = new HashSet<>(personBootcamps);
        if (personBootcamps.size() != uniqueCapacities.size()) {
            return Mono.error(new BusinessException(TechnicalMessage.BBOOTCAMPS_DUPLICATE_IN_LIST));
        } else {
            return Mono.just(Boolean.FALSE);
        }
    }



}
