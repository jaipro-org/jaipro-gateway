package com.bindord.eureka.gateway.services;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserDemandService {

    Mono<Void> demandForgetPassword(String email);

    Mono<Void> validateUserRecoverTicket(UUID id, UUID userId);
}
