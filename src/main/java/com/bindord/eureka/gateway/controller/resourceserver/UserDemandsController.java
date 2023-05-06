package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.advice.CustomValidationException;
import com.bindord.eureka.gateway.domain.OperationUserRecoverDto;
import com.bindord.eureka.gateway.services.UserDemandService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/user-demands")
@Slf4j
public class UserDemandsController {

    private final UserDemandService userDemandService;

    @ApiResponse(description = "Demand operation forgot password",
            responseCode = "200")
    @PostMapping(value = "/forgot-password",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> initDemandForgotPassword(@RequestParam String email) {
        return userDemandService.demandForgetPassword(email);
    }

    @ApiResponse(description = "Demand operation validate user recover ticket",
            responseCode = "200")
    @GetMapping(value = "/validate/user-recover-ticket/{id}/user/{userId}")
    public Mono<Void> validateUserRecoverTicket(@PathVariable UUID id, @PathVariable UUID userId) {
        return userDemandService.validateUserRecoverTicket(id, userId);
    }

    @ApiResponse(description = "Demand operation to update forgotten password",
            responseCode = "200")
    @PostMapping(value = "/forgot-password/change",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> doChangePassword(@Valid @RequestBody OperationUserRecoverDto userRecoverDto) {
        var validNwPass = userRecoverDto.getNwPassword().equals(userRecoverDto.getRepeatNwPassword());
        if (!validNwPass) {
            return Mono.error(new CustomValidationException("Passwords no coinciden, validar nuevamente."));
        }
        return Mono.empty();
    }
}
