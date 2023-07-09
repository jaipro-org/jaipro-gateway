package com.bindord.eureka.gateway.domain.specialist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class SpecialistFullUpdateDto {

    private String filePhoto;

    private String filePhotoExtension;

    private boolean flagUpdatePhoto;

    @NotBlank
    @Size(min = 2, max = 36)
    private String name;

    @NotBlank
    @Size(min = 2, max = 36)
    private String lastName;

    private String about;

    @Size(min = 2, max = Byte.MAX_VALUE)
    private String address;

    @Size(min = 9, max = 15)
    private String phone;

    @Size(min = 9, max = 15)
    private String secondaryPhone;
}