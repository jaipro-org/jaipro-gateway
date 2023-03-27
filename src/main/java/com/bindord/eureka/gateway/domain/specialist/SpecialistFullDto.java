package com.bindord.eureka.gateway.domain.specialist;

import com.bindord.eureka.resourceserver.model.Specialist;
import com.bindord.eureka.resourceserver.model.SpecialistCv;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SpecialistFullDto {

    private Specialist specialist;
    private SpecialistCv cv;
}
