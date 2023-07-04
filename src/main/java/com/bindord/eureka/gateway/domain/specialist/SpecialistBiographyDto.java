package com.bindord.eureka.gateway.domain.specialist;

import com.bindord.eureka.resourceserver.model.Photo;
import com.bindord.eureka.resourceserver.model.SpecialistCvExperienceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SpecialistBiographyDto {

    private String about;

    private List<SpecialistCvExperienceDto> experiences;

    private List<Photo> gallery;
}