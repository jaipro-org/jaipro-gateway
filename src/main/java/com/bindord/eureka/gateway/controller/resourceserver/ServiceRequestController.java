package com.bindord.eureka.gateway.controller.resourceserver;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/service-request")
@Slf4j
public class ServiceRequestController {


}