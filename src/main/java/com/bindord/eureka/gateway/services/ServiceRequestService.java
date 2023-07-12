package com.bindord.eureka.gateway.services;

import com.bindord.eureka.gateway.domain.service.ServiceRequestDto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ServiceRequestService {

    Mono<Void> create(List<FilePart> images, ServiceRequestDto createDto);
}