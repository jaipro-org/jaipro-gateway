package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.domain.service.ServiceRequestDto;
import com.bindord.eureka.gateway.services.ServiceRequestService;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.BasePaginateResponseServiceRequestListDto;
import com.bindord.eureka.resourceserver.model.BaseSearch;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/service-request")
@Slf4j
public class ServiceRequestController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;
    private final ServiceRequestService service;

    @ApiResponse(description = "create services request by customer id", responseCode = "200")
    @PostMapping(value = "",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Mono<Void> create(@RequestPart List<FilePart> images, @RequestPart @Valid ServiceRequestDto createDto){
        return service.create(images, createDto);
    }

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