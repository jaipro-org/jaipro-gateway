package com.bindord.eureka.gateway.services;

import com.bindord.eureka.gateway.domain.specialist.SpecialistFullDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistFullUpdateDto;
import com.bindord.eureka.resourceserver.model.Specialist;
import com.bindord.eureka.auth.model.SpecialistPersist;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpecialistService {

    Mono<Specialist> update(UUID id, SpecialistFullUpdateDto specialist);

    Mono<SpecialistFullDto> findSpecialistFullInfoById(UUID uuid);
}
