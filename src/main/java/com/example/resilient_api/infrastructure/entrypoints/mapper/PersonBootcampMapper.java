package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.PersonBootcamps;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.PersonBootcampDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PersonBootcampMapper {
    @Mapping(target = "idBootcamp", source = "idBootcamp")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idPerson", ignore = true)
    PersonBootcamps toPersonBootcamp(PersonBootcampDTO personBootcampDTO);
}
