package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.infrastructure.entrypoints.dto.PersonBootcampDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.PersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {PersonBootcampDTO.class})
public interface PersonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "personBootcampsList", target = "personBootcampsList")
    Person personDTOToPerson(PersonDTO personDTO);

}

