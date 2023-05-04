package com.bindord.eureka.gateway.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Setter
@Getter
public class OperationUserRecoverDto {

    @NotNull
    private UUID userRecoverId;

    @NotNull
    private UUID userId;

    @Size(min = 8, max = 64)
    @NotEmpty
    private String nwPassword;

    @Size(min = 8, max = 64)
    @NotEmpty
    private String repeatNwPassword;
}
