package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.SpecialistBankAccount;
import com.bindord.eureka.resourceserver.model.SpecialistBankAccountDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/specialist-bank-account")
@Slf4j
public class SpecialistBankAccountController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Persist a Specialist's bank account",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistBankAccount> persistSpecialistBankAccount(@Valid @RequestBody SpecialistBankAccountDto SpecialistBankAccount) {
        return resourceServerClientConfiguration.init()
                .post()
                .uri("/specialist-bank-account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(SpecialistBankAccount), SpecialistBankAccountDto.class)
                .retrieve()
                .bodyToMono(SpecialistBankAccount.class)
                .subscribeOn(Schedulers.parallel());
    }

    @ApiResponse(description = "Update Specialist's bank account",
            responseCode = "200")
    @PutMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistBankAccount> updateSpecialistBankAccount(@Valid @RequestBody SpecialistBankAccountDto SpecialistBankAccount) {
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/specialist-bank-account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(SpecialistBankAccount), SpecialistBankAccountDto.class)
                .retrieve()
                .bodyToMono(SpecialistBankAccount.class)
                .subscribeOn(Schedulers.parallel());
    }

    @ApiResponse(description = "Get all Specialist's bank account By SpecialistId",
            responseCode = "200")
    @GetMapping(value = "/specialist/{specialistId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<SpecialistBankAccount> findAllBySpecialistId(@PathVariable UUID specialistId) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist-bank-account/list/{id}")
                        .build(specialistId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(SpecialistBankAccount.class);
    }

    @ApiResponse(description = "Delete Specialist's bank account By Id",
            responseCode = "200")
    @DeleteMapping(value = "/specialist-bank-account/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> deleteBankAccountById(@PathVariable UUID id) {
        return resourceServerClientConfiguration.init()
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/specialist-bank-account/{id}")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
