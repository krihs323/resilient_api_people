package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.mapper;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BootcampGatewayMapper {

    BootcampDTO toDTO(Bootcamp bootcamp);
}