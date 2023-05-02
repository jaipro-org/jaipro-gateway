package com.bindord.eureka.gateway.services;

import reactor.core.publisher.Mono;

public interface UserDemandService {

    Mono<Void> demandForgetPassword(String email);
}
