package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.Notification;
import com.bindord.eureka.resourceserver.model.NotificationDto;
import com.bindord.eureka.resourceserver.model.NotificationUpdateDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/notification")
@Slf4j
public class NotificationController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Get all notifications by userId",
            responseCode = "200")
    @GetMapping(value = "/profile/{profileType}/user/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<Notification> getAllNotificationByUserId(
            @PathVariable Integer profileType, @PathVariable UUID userId) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/notification/{profileType}/{userId}")
                        .build(profileType, userId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Notification.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Save notification",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Notification> saveNotification(@RequestBody NotificationDto notification) {
        return resourceServerClientConfiguration.init()
                .post()
                .uri("/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(notification), Notification.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Notification.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update notification",
            responseCode = "200")
    @PutMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Notification> update(@RequestBody NotificationUpdateDto notification) {
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(notification), NotificationUpdateDto.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Notification.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
