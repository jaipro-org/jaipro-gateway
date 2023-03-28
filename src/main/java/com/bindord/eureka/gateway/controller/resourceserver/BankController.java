package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.Bank;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/bank")
@Slf4j
public class BankController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Get all banks",
            responseCode = "200")
    @GetMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<Bank> getAllBank() throws InterruptedException {
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/bank")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Bank.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
