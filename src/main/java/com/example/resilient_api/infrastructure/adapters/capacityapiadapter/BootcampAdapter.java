package com.example.resilient_api.infrastructure.adapters.capacityapiadapter;

import com.example.resilient_api.domain.constants.Messages;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.exceptions.TechnicalException;
import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.CapacityBootcampSaveResult;
import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.CapacityApiProperties;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.util.Constants;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampAdapter implements BootcampGateway {

    private final WebClient webClient;
    private final CapacityApiProperties capacityApiProperties;
    private final Retry retry;
    private final Bulkhead bulkhead;

    @Value("${bootcamp-api}")
    private String bootcampPath;

    @Override
    //@CircuitBreaker(name = "capacityApiValidator", fallbackMethod = "fallback")
    public Mono<Bootcamp> getBootcampById(Long idBootcamp, String messageId) {
        log.info("Starting email validation for email: {} with messageId: {}", idBootcamp, messageId);
        return webClient.get()
                    .uri(bootcampPath + "bootcamp/"+idBootcamp)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(Messages.MSJ_HEADER.getValue(), messageId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.ADAPTER_RESPONSE_NOT_FOUND))
                    .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.INTERNAL_ERROR_IN_ADAPTERS))
                    .bodyToMono(Bootcamp.class)
                    .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_MESSAGE_ID)))
                    .doOnNext(response -> log.info("Received API Bootcamp response for messageId: {}: {}", messageId, response))
                     //.transformDeferred(RetryOperator.of(retry))
                    //.transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
                    .timeout(Duration.ofSeconds(15)) // Aumentamos el tiempo de espera a 15 segundos
                    .doOnError(e -> log.error("Error occurred in get bootcamp for messageId: {}", messageId, e));
    }


    public Mono<CapacityBootcampSaveResult> fallback(Throwable t) {
        return Mono.defer(() ->
                Mono.justOrEmpty(t instanceof TimeoutException
                                ? new CapacityBootcampSaveResult("UNKOWN", "0.0") // Respuesta por timeout
                                : null)
                        .switchIfEmpty(Mono.error(t))  // Si no es timeout, lanza el error
        );
    }

    private Mono<Throwable> buildErrorResponse(ClientResponse response, TechnicalMessage technicalMessage) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty(Constants.NO_ADITIONAL_ERROR_DETAILS)
                .flatMap(errorBody -> {
                    log.error(Constants.STRING_ERROR_BODY_DATA, errorBody);
                    return Mono.error(
                            response.statusCode().is5xxServerError() ?
                                    new TechnicalException(technicalMessage):
                                    new BusinessException(technicalMessage));
                });
    }


}
