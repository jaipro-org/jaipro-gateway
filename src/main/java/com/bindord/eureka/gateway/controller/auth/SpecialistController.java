package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.auth.model.SpecialistPersist;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFiltersDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullUpdateDto;
import com.bindord.eureka.gateway.services.SpecialistService;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.Experience;
import com.bindord.eureka.resourceserver.model.Specialist;
import com.bindord.eureka.resourceserver.model.SpecialistExperienceUpdateDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/specialist")
@Slf4j
public class SpecialistController {

    private final AuthClientConfiguration authClientConfiguration;
    private final SpecialistService specialistService;
    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Persist a specialist",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Specialist> persistSpecialist(@Valid @RequestBody SpecialistPersist specialist) {
        return authClientConfiguration.init()
                .post()
                .uri("/specialist")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(specialist), SpecialistPersist.class)
                .retrieve()
                .bodyToMono(Specialist.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a specialist",
            responseCode = "200")
    @PutMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Specialist> updateSpecialist(@PathVariable UUID id, @Valid @RequestBody SpecialistFullUpdateDto specialist) {
        return specialistService.update(id, specialist);
    }

    @ApiResponse(description = "Update an experience in specialist CV",
            responseCode = "200")
    @PutMapping(value = "/{id}/experience",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> updateExperience(@PathVariable UUID id, @Valid @RequestBody SpecialistExperienceUpdateDto experienceUpdateDto) {
        return resourceServerClientConfiguration.init()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/{id}/experience").build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(experienceUpdateDto), SpecialistExperienceUpdateDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Delete an experience of specialist CV",
            responseCode = "200")
    @DeleteMapping(value = "/{id}/experience/{professionId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> deleteExperienceByIdAndProfessionId(@PathVariable UUID id, @PathVariable Integer professionId) {
        return resourceServerClientConfiguration.init()
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/{id}/experience/{professionId}")
                        .build(id, professionId))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic())
                .then(
                        resourceServerClientConfiguration.init()
                                .delete()
                                .uri(uriBuilder -> uriBuilder.path("/specialist-specialization/{id}/profession/{professionId}")
                                        .build(id, professionId))
                                .retrieve()
                                .bodyToMono(Void.class)
                                .subscribeOn(Schedulers.boundedElastic())
                );
    }

    @ApiResponse(description = "Persist an experience of specialist cv",
            responseCode = "200")
    @PostMapping(value = "/{id}/experience",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Experience> persistExperience(@PathVariable UUID id, @Valid @RequestBody Experience experience) {
        return resourceServerClientConfiguration.init()
                .post()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/{id}/experience").build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(experience), SpecialistExperienceUpdateDto.class)
                .retrieve()
                .bodyToMono(Experience.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Get full specialist info by id",
            responseCode = "200")
    @GetMapping(value = "/full/{specialistId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistFullDto> findSpecialistFullInfoById(@PathVariable UUID specialistId) {
        return specialistService.findSpecialistFullInfoById(specialistId);
    }

    @ApiResponse(description = "Get full filters for specialist list",
            responseCode = "200")
    @GetMapping(value = "/filters",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistFiltersDto> findSpecialistFullInfoById() {
        return specialistService.getSpecialistFilters();
    }
}