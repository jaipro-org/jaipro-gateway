package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.domain.specialist.SpecialistBiographyDto;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.BasePaginateResponseServiceRequestListDto;
import com.bindord.eureka.resourceserver.model.BasePaginateResponseSpecialistResultSearchDTO;
import com.bindord.eureka.resourceserver.model.BaseSearch;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/service-request")
@Slf4j
public class ServiceRequestController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Get list services request by customer id", responseCode = "200")
    @GetMapping(value = "/list/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<BasePaginateResponseServiceRequestListDto> listByCustomerId(@PathVariable UUID customerId, @Valid BaseSearch searchDto) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/service-request/list/{customerId}")
                        .queryParam("pageNumber", searchDto.getPageNumber())
                        .queryParam("pageSize", searchDto.getPageSize())
                        .build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BasePaginateResponseServiceRequestListDto.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}