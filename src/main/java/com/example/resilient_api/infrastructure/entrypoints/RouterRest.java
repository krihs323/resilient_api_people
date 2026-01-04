package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.handler.PersonHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;


@Configuration
public class RouterRest {


    @Bean
    public RouterFunction<ServerResponse> routerFunction(PersonHandlerImpl personHandler) {
        return route()
                .POST("/person",
                        personHandler::createPerson,
                        ops -> ops.beanClass(PersonHandlerImpl.class).beanMethod("createPerson"))
                .build();
    }
}