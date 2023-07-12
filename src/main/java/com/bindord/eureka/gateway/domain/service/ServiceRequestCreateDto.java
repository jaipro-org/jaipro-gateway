package com.bindord.eureka.gateway.domain.service;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class ServiceRequestCreateDto {

    private Integer professionId;

    private Integer districtId;

    private String detail;

    private UUID customerId;
}