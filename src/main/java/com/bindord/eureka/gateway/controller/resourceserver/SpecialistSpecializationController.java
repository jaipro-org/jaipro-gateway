package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.SpecialistSpecialization;
import com.bindord.eureka.resourceserver.model.SpecialistSpecializationDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/specialist-specialization")
@Slf4j
public class SpecialistSpecializationController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Persist a Specialist's specialization",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistSpecialization> persistSpecialistSpecialization(@Valid @RequestBody SpecialistSpecializationDto SpecialistSpecialization) {
        return resourceServerClientConfiguration.init()
                .post()
                .uri("/specialist-specialization")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(SpecialistSpecialization), SpecialistSpecializationDto.class)
                .retrieve()
                .bodyToMono(SpecialistSpecialization.class)
                .subscribeOn(Schedulers.parallel());
    }

    @ApiResponse(description = "Persist many Specialist's specialization",
            responseCode = "200")
    @PostMapping(value = "/list",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<SpecialistSpecialization> persistManySpecialistSpecialization(@RequestBody List<@Valid SpecialistSpecializationDto> specialistSpecializations) {
        return resourceServerClientConfiguration.init()
                .post()
                .uri("/specialist-specialization/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.fromIterable(
                        specialistSpecializations
                ), new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .bodyToFlux(SpecialistSpecialization.class);
    }

    @ApiResponse(description = "Delete some specialistSpecializations associated to a specific profession",
            responseCode = "200")
    @PostMapping(value = "/delete/list",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<SpecialistSpecialization> deleteManySpecialistSpecialization(@RequestBody List<@Valid SpecialistSpecializationDto> specialistSpecializations) {
        return resourceServerClientConfiguration.init()
                .post()
                .uri("/specialist-specialization/delete/list")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.fromIterable(
                        specialistSpecializations
                ), new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .bodyToFlux(SpecialistSpecialization.class);
    }

    @ApiResponse(description = "Update Specialist's specialization",
            responseCode = "200")
    @PutMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistSpecialization> updateSpecialistSpecialization(@Valid @RequestBody SpecialistSpecializationDto SpecialistSpecialization) {
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/specialist-specialization")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(SpecialistSpecialization), SpecialistSpecializationDto.class)
                .retrieve()
                .bodyToMono(SpecialistSpecialization.class)
                .subscribeOn(Schedulers.parallel());
    }

    @ApiResponse(description = "Get all Specialist's specialization By SpecialistId",
            responseCode = "200")
    @GetMapping(value = "/specialist/{specialistId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<SpecialistSpecialization> findAllBySpecialistId(@PathVariable UUID specialistId) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist-specialization/list/{id}")
                        .build(specialistId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(SpecialistSpecialization.class);
    }
}
