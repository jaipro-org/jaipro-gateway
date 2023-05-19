package com.bindord.eureka.gateway.domain.specialist;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class SpecialistGalleryUpdateDto {

    @NotNull
    private UUID specialistId;

    private List<String> fileIdsToRemove;
}
