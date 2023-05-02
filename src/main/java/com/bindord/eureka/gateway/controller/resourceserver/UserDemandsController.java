package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.services.UserDemandService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}
