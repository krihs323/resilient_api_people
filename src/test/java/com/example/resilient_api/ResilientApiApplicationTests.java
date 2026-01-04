package com.example.resilient_api;

import com.example.resilient_api.domain.api.PersonServicePort;
import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.PersonBootcamps;
import com.example.resilient_api.infrastructure.entrypoints.dto.PersonBootcampDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.PersonDTO;
import com.example.resilient_api.infrastructure.entrypoints.handler.PersonHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.mapper.PersonMapper;
import com.example.resilient_api.infrastructure.validation.ObjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import static com.example.resilient_api.infrastructure.adapters.capacityapiadapter.util.Constants.X_MESSAGE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResilientApiApplicationTests {

    @Mock
    private PersonServicePort personServicePort;
    @Mock
    private PersonMapper personMapper;
    @Mock
    private ObjectValidator objectValidator;

    @InjectMocks
    private PersonHandlerImpl personHandler;

    private PersonDTO personDTO;
    private final String MESSAGE_ID = "test-uuid";

    @BeforeEach
    void setUp() {
        List<PersonBootcampDTO> personBootcampDTOS = List.of(
                new PersonBootcampDTO(1L),
                new PersonBootcampDTO(2L),
                new PersonBootcampDTO(3L)
        );
        personDTO = new PersonDTO();
        personDTO.setName("Backend Java");
        personDTO.setId(1L);
        personDTO.setName("cristian");
        personDTO.setEmail("cristian@hotmail.com");
        personDTO.setPersonBootcampsList(personBootcampDTOS);
    }

    @Test
    void createBootcampSuccess() {
        // GIVEN
        MockServerRequest request = MockServerRequest.builder()
                .header(X_MESSAGE_ID, MESSAGE_ID)
                .body(Mono.just(personDTO));

        List<PersonBootcamps> bootcampTechnologies = List.of(
                new PersonBootcamps(1L, 1L, 100L),
                new PersonBootcamps(2L, 1L, 101L),
                new PersonBootcamps(3L, 1L, 102L)
        );

        //doNothing().when(objectValidator).validate(any());
        when(personServicePort.registerPerson(any(), anyString()))
                .thenReturn(Mono.just(new Person(1L, "cristian", "cristian@hotmail.com", 10,  bootcampTechnologies)));

        // WHEN
        Mono<ServerResponse> responseMono = personHandler.createPerson(request);

        // THEN
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();
    }


}
