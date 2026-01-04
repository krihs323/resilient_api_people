package com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.resilient_api.domain.model.PersonBootcamps;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.PersonBootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PersonBootcampEntityMapper {

    @Mapping(source = "idBootcamp", target = "idBootcamp")// dominio es target, entidad es fuente
    @Mapping(source = "idPerson", target = "idPerson")// dominio es target, entidad es fuente
    PersonBootcamps toModel(PersonBootcampEntity entity);
}
