package com.bindord.eureka.gateway.services;

import com.bindord.eureka.gateway.domain.specialist.SpecialistBiographyDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpecialistCvService {

    Mono<SpecialistBiographyDto> getBiography(UUID specialistId);
}
