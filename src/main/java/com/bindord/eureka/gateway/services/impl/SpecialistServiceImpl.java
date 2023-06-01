package com.bindord.eureka.gateway.services.impl;

import com.bindord.eureka.gateway.advice.CustomValidationException;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFiltersDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullUpdateDto;
import com.bindord.eureka.gateway.services.SpecialistService;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.District;
import com.bindord.eureka.resourceserver.model.Profession;
import com.bindord.eureka.resourceserver.model.Specialist;
import com.bindord.eureka.resourceserver.model.SpecialistCv;
import com.bindord.eureka.resourceserver.model.SpecialistCvDto;
import com.bindord.eureka.resourceserver.model.SpecialistUpdateDto;
import com.bindord.eureka.resourceserver.model.Specialization;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpecialistServiceImpl implements SpecialistService {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @Override
    public Mono<Specialist> update(UUID id, SpecialistFullUpdateDto specialist) {
        return this
                .doValidateIfSpecialistExits(id)
                .flatMap(exist -> exist ?
                                    Mono.empty() :
                                    Mono.error(new CustomValidationException("Specialist not found")))
                .then(
                        Mono.zip(doUpdateSpecialist(id, specialist), doUpdateSpecialistCvOnlyPresentation(id, specialist))
                                .flatMap(t -> Mono.just(t.getT1()))
                );
    }

    @Override
    public Mono<SpecialistFullDto> findSpecialistFullInfoById(UUID id) {
        var speId = id.toString();
        return Mono.zip(findSpecialistById(speId), findSpecialisCvtById(speId))
                .map(
                        tuple -> new SpecialistFullDto(tuple.getT1(), tuple.getT2())
                );
    }

    @Override
    public Mono<SpecialistFiltersDto> getSpecialistFilters() {
        return Flux
                .zip(getAllSpecialization(), getAllDistrict(), getAllProfession())
                .map(tuple -> {
                    var specialistFiltersDto = new SpecialistFiltersDto();
                    specialistFiltersDto.setSpecialities(tuple.getT1());
                    specialistFiltersDto.setDistricts(tuple.getT2());
                    specialistFiltersDto.setProfessions(tuple.getT3());

                    return specialistFiltersDto;
                })
                .single();
    }

    @SneakyThrows
    private Mono<Boolean> doValidateIfSpecialistExits(UUID id) {
        if (id == null) {
            return Mono.just(false);
        }

        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist/{id}")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Specialist.class).flatMap(spe->Mono.just(true));
    }

    @SneakyThrows
    private Mono<Specialist> doUpdateSpecialist(UUID id, SpecialistFullUpdateDto specialist) {
        Mono<SpecialistUpdateDto> specialistDto = convertSpecialistToDTO(specialist);

        return resourceServerClientConfiguration.init()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/specialist/{id}").build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(specialistDto, SpecialistUpdateDto.class)
                .retrieve()
                .bodyToMono(Specialist.class);
    }

    @SneakyThrows
    private Mono<SpecialistCv> doUpdateSpecialistCvOnlyPresentation(UUID id, SpecialistFullUpdateDto specialist) {

        return resourceServerClientConfiguration.init()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/{id}/presentation").build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(specialist), SpecialistFullUpdateDto.class)
                .retrieve()
                .bodyToMono(SpecialistCv.class);

    }

    private Mono<SpecialistUpdateDto> convertSpecialistToDTO(SpecialistFullUpdateDto specialist) {
        SpecialistUpdateDto specialistDto = new SpecialistUpdateDto();
        BeanUtils.copyProperties(specialist, specialistDto);

        return Mono.just(specialistDto);
    }

    private Mono<SpecialistCvDto> convertSpecialistCVToDTO(SpecialistFullUpdateDto specialist) {
        SpecialistCvDto specialistCvDto = new SpecialistCvDto();
        BeanUtils.copyProperties(specialist, specialistCvDto);

        return Mono.just(specialistCvDto);
    }

    private Mono<Specialist> findSpecialistById(String id) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist/{id}")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Specialist.class);
    }

    private Mono<SpecialistCv> findSpecialisCvtById(String id) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/{id}")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SpecialistCv.class);
    }

    private Flux<List<Specialization>> getAllSpecialization(){
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/specialization")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Specialization.class)
                .subscribeOn(Schedulers.boundedElastic())
                .collectList()
                .flux();
    }

    private Flux<List<District>> getAllDistrict(){
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/district")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(District.class)
                .subscribeOn(Schedulers.boundedElastic())
                .collectList()
                .flux();
    }

    private Flux<List<Profession>> getAllProfession(){
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/profession")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Profession.class)
                .subscribeOn(Schedulers.boundedElastic())
                .collectList()
                .flux();
    }
}
