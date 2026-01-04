package com.example.resilient_api.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    DATABASE_ERROR("500","Something went wrong in creation row", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501","Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    INVALID_EMAIL("403", "Invalid email, please verify", "email"),
    INVALID_MESSAGE_ID("404", "Invalid Message ID, please verify", "messageId"),
    UNSUPPORTED_OPERATION("501", "Method not supported, please try again", ""),
    PERSON_CREATED("201", "Person created successfully", ""),
    ADAPTER_RESPONSE_NOT_FOUND("404-0", "invalid email, please verify", ""),
    CAPACITY_WITH_OTHER_PERSONS("400", "Las capacidades del person estan asociadas a otros persons", ""),
    PERSON_ALREADY_EXISTS("400","El person ya está registrado." ,"" ),
    BOOTCAMPS_VALIDATION_DATE("400","Existen conflictos de fechas entre los bootcamps" ,"" ),
    PERSON_NAME_EMPTY("400","El nombre no debe ser vacio" ,"name" ),
    BBOOTCAMPS_DUPLICATE_IN_LIST("400","Bootcamps repetidos en la persona que intenta crear" ,"" );

    private final String code;
    private final String message;
    private final String param;
}