package com.bindord.eureka.gateway.services.impl;

import com.bindord.eureka.gateway.advice.CustomValidationException;
import com.bindord.eureka.gateway.domain.OperationUserRecoverDto;
import com.bindord.eureka.gateway.services.UserDemandService;
import com.bindord.eureka.gateway.wsc.KeycloakClientConfiguration;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.keycloak.auth.model.UpdatePasswordDTO;
import com.bindord.eureka.resourceserver.model.RecoverPasswordDto;
import com.bindord.eureka.resourceserver.model.UserInfo;
import com.bindord.eureka.resourceserver.model.UserRecover;
import com.bindord.eureka.resourceserver.model.UserRecoverDto;
import com.bindord.eureka.resourceserver.model.UserRecoverUpdateDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

import static com.bindord.eureka.gateway.utils.Constants.ERROR_EMAIL_NOT_FOUND;
import static reactor.core.publisher.Mono.just;

@Service
@AllArgsConstructor
public class UserDemandServiceImpl implements UserDemandService {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;
    private final KeycloakClientConfiguration keycloakClientConfiguration;

    @Override
    public Mono<Void> demandForgetPassword(String email) {

        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/user-info/query").queryParam("email", email).build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserInfo.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> Mono.error(new CustomValidationException(ERROR_EMAIL_NOT_FOUND)))
                .flatMap(userInfo -> {

                    UserRecoverDto userRecoverDto = new UserRecoverDto();
                    userRecoverDto.setUserId(userInfo.getId());

                    return resourceServerClientConfiguration.init()
                            .post()
                            .uri("/user-recover")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .body(just(userRecoverDto), UserRecoverDto.class)
                            .retrieve()
                            .bodyToMono(UserRecover.class)
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(userRecover -> {
                                var recoverPasswordDto = new RecoverPasswordDto();
                                BeanUtils.copyProperties(userRecover, recoverPasswordDto);
                                recoverPasswordDto.setEmailReceiver(email);
                                return resourceServerClientConfiguration.init()
                                        .post()
                                        .uri("/mail/send/init-recover-password")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(just(recoverPasswordDto), RecoverPasswordDto.class)
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                        .subscribeOn(Schedulers.boundedElastic());
                            });
                });
    }

    @Override
    public Mono<Void> validateUserRecoverTicket(UUID id, UUID userId) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/user-recover/" + id.toString() + "/user/" + userId.toString())
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> executeUpdateUserPassword(OperationUserRecoverDto operationUserRecover) {
        return this.validateUserRecoverTicket(operationUserRecover.getUserRecoverId(), operationUserRecover.getUserId())
                .then(
                        this.updateUserPassword(operationUserRecover)
                                .zipWith(this.updateUserRecoverFlag(operationUserRecover)).then()
                );
    }

    private Mono<Void> updateUserPassword(OperationUserRecoverDto operationUserRecover) {
        var updatePass = new UpdatePasswordDTO();
        updatePass.setUserId(operationUserRecover.getUserId().toString());
        updatePass.setNwPassword(operationUserRecover.getNwPassword());
        return keycloakClientConfiguration.init()
                .put()
                .uri("/user/updatePassword/direct")
                .body(just(updatePass), UpdatePasswordDTO.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Void> updateUserRecoverFlag(OperationUserRecoverDto operationUserRecover) {
        var userRecoverUpdate = new UserRecoverUpdateDto();
        userRecoverUpdate.setId(operationUserRecover.getUserRecoverId());
        userRecoverUpdate.setFlagRecover(true);
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/user-recover")
                .body(just(userRecoverUpdate), UserRecoverUpdateDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
