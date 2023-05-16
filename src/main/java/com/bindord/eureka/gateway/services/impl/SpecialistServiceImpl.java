package com.bindord.eureka.gateway.services.impl;

import com.bindord.eureka.gateway.advice.CustomValidationException;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullUpdateDto;
import com.bindord.eureka.gateway.services.SpecialistService;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.Specialist;
import com.bindord.eureka.resourceserver.model.SpecialistCv;
import com.bindord.eureka.resourceserver.model.SpecialistCvDto;
import com.bindord.eureka.resourceserver.model.SpecialistUpdateDto;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                        Mono.zip(doUpdateSpecialist(id, specialist), doUpdateSpecialistCv(id, specialist))
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
                .bodyToMono(Boolean.class);
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
    private Mono<SpecialistCv> doUpdateSpecialistCv(UUID id, SpecialistFullUpdateDto specialist) {
        var specialistCVDto = convertSpecialistCVToDTO(specialist);

        return resourceServerClientConfiguration.init()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/{id}").build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(specialistCVDto, SpecialistCvDto.class)
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
}
