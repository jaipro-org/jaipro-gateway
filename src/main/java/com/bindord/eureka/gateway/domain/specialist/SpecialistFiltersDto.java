package com.bindord.eureka.gateway.domain.specialist;

import com.bindord.eureka.resourceserver.model.District;
import com.bindord.eureka.resourceserver.model.Profession;
import com.bindord.eureka.resourceserver.model.Specialization;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SpecialistFiltersDto {

    private List<Specialization> categories;

    private List<Profession> specialties;

    private List<District> districts;
}
