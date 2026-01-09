package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.PersonBootcamps;
import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.domain.spi.PersonPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonUseCaseTest {

    @Mock
    private PersonPersistencePort personPersistencePort;
    @Mock
    private BootcampGateway bootcampGateway;

    @InjectMocks
    private PersonUseCase personUseCase;

    private final String messageId = "trace-789";
    private Person personRequest;

    @BeforeEach
    void setUp() {
        // Persona que intenta inscribirse a dos bootcamps (IDs 101 y 102)
        List<PersonBootcamps> bootcampsRequest = List.of(
                new PersonBootcamps(1L, 11L, 100L),
                new PersonBootcamps(2L, 12L, 100L)
        );
        personRequest = new Person(100L, "Juan Perez", "juan@mail.com", 25, bootcampsRequest);
    }

    @Test
    @DisplayName("Should FAIL when bootcamps dates overlap")
    void registerPersonOverlapError() {
        // GIVEN: Dos bootcamps que se solapan
        // Bootcamp 1: Inicia hoy, dura 4 semanas
        Bootcamp b1 = new Bootcamp(11L, "Java", "Desc", LocalDate.now(), 4);
        // Bootcamp 2: Inicia en 1 semana (dentro del rango de b1), dura 2 semanas
        Bootcamp b2 = new Bootcamp(12L, "WebFlux", "Desc", LocalDate.now().plusWeeks(1), 2);

        when(personPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));
        when(bootcampGateway.getBootcampById(eq(11L), anyString())).thenReturn(Mono.just(b1));
        when(bootcampGateway.getBootcampById(eq(12L), anyString())).thenReturn(Mono.just(b2));
        when(personPersistencePort.save(personRequest)).thenReturn(Mono.just(personRequest));

        // WHEN
        Mono<Person> result = personUseCase.registerPerson(personRequest, messageId);

        // THEN
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.BOOTCAMPS_VALIDATION_DATE)
                .verify();

        // Verificamos que NUNCA se guardó la persona por el error de validación
        verify(personPersistencePort).save(any());
    }

    @Test
    @DisplayName("Should SUCCESS when bootcamps do not overlap")
    void registerPersonNoOverlapSuccess() {
        // GIVEN: Dos bootcamps en fechas distintas
        // Bootcamp 1: Inicia hoy, dura 2 semanas
        Bootcamp b1 = new Bootcamp(11L, "Java", "Desc", LocalDate.now(), 2);
        // Bootcamp 2: Inicia en 3 semanas (después de que termine b1)
        Bootcamp b2 = new Bootcamp(12L, "WebFlux", "Desc", LocalDate.now().plusWeeks(3), 2);

        when(personPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));
        when(bootcampGateway.getBootcampById(anyLong(), anyString()))
                .thenReturn(Mono.just(b1))
                .thenReturn(Mono.just(b2));
        when(personPersistencePort.save(any())).thenReturn(Mono.just(personRequest));
        when(personPersistencePort.saveBootcamps(anyLong(), any(), anyString())).thenReturn(Mono.just(true));

        // Mock para el reporte (Fire and Forget)
        when(bootcampGateway.savePersonReport(anyLong(), any(), anyString())).thenReturn(Mono.empty());

        // WHEN
        Mono<Person> result = personUseCase.registerPerson(personRequest, messageId);

        // THEN
        StepVerifier.create(result)
                .expectNext(personRequest)
                .verifyComplete();

        verify(personPersistencePort).save(any());
    }

    @Test
    @DisplayName("Should Error when bootcamps are duplicated")
    void registerPersonWhenBoortcamsAreDuplicated() {
        // GIVEN: Persona con bootcamps repetidos
        // Persona que intenta inscribirse a dos bootcamps (IDs 101 y 102)
        List<PersonBootcamps> bootcampRepeteaded = List.of(
                new PersonBootcamps(null, 11L, 100L),
                new PersonBootcamps(null, 11L, 100L)
        );
        Person person = new Person(100L, "Juan Perez", "juan@mail.com", 25, bootcampRepeteaded);

        when(personPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));
        when(personPersistencePort.save(any())).thenReturn(Mono.just(person));

        // WHEN
        Mono<Person> result = personUseCase.registerPerson(person, messageId);

        // THEN
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.BBOOTCAMPS_DUPLICATE_IN_LIST)
                .verify();

        verify(personPersistencePort).save(any());

    }
}