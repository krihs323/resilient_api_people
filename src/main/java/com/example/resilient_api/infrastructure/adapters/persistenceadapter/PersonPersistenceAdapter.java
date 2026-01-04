package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.PersonBootcamps;
import com.example.resilient_api.domain.spi.PersonPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.PersonBootcampEntity;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.PersonBootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.PersonEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.PersonRepository;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.PersonBootcampRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class PersonPersistenceAdapter implements PersonPersistencePort {
    private final PersonRepository personRepository;
    private final PersonEntityMapper personEntityMapper;

    private final PersonBootcampRepository personBootcampRepository;
    private final PersonBootcampEntityMapper personBootcampEntityMapper;

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Person> save(Person person) {
        return personRepository
                .save(personEntityMapper.toEntity(person))
                .map(personEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return personRepository.findByName(name)
                .map(personEntityMapper::toModel)
                .map(bootcamp -> true)  // Si encuentra la persona, devuelve true
                .defaultIfEmpty(false);  // Si no encuentra, devuelve false
    }

    @Override
    public Mono<Boolean> saveBootcamps(Long idPerson, Person person, String messageId) {
        List<PersonBootcampEntity> details = new ArrayList<>();
        for (PersonBootcamps req : person.personBootcampsList()) {
            PersonBootcampEntity detail = new PersonBootcampEntity();
            detail.setIdPerson(idPerson);
            detail.setIdBootcamp(req.idBootcamp());
            details.add(detail);
        }
        return personBootcampRepository
                .saveAll(details)
                .then(Mono.just(true))
                .onErrorReturn(false);
    }


}
