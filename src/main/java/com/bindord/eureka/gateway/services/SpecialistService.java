package com.bindord.eureka.gateway.services;

import com.bindord.eureka.gateway.domain.specialist.SpecialistFiltersDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullUpdateDto;
import com.bindord.eureka.resourceserver.model.Specialist;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpecialistService {

    Mono<SpecialistFullDto> update(UUID id, SpecialistFullUpdateDto specialist);

    Mono<SpecialistFullDto> findSpecialistFullInfoById(UUID uuid);

    Mono<SpecialistFiltersDto> getSpecialistFilters();
}