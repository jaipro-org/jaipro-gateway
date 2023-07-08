package com.bindord.eureka.gateway.services.impl;

import com.bindord.eureka.auth.model.Customer;
import com.bindord.eureka.auth.model.CustomerPersist;
import com.bindord.eureka.gateway.domain.service.ServiceRequestDto;
import com.bindord.eureka.gateway.services.ServiceRequestService;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final AuthClientConfiguration authClientConfiguration;
    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @Override
    public Mono<Void> create(List<FilePart> images, ServiceRequestDto createDto) {

        if(createDto.getCustomerId() == null)
            return createCustomer(createDto).flatMap(qCus -> {
                createDto.setCustomerId(qCus.getId());
                return createServiceRequest(images, createDto);
            });

        return createServiceRequest(images, createDto);
    }

    private Mono<Customer> createCustomer(ServiceRequestDto createDto){
        var customer = castCustomer(createDto);

        return authClientConfiguration.init()
                .post()
                .uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerPersist.class)
                .retrieve()
                .bodyToMono(Customer.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Void> createServiceRequest(List<FilePart> images, ServiceRequestDto createDto){
        var serviceRequest = castServiceRequestCreateDto(createDto);

        MultipartBodyBuilder multipartBuilder = new MultipartBodyBuilder();
        images.forEach(ele -> multipartBuilder.part("images", ele) );
        multipartBuilder.part("serviceRequest", serviceRequest);

        return resourceServerClientConfiguration
                .initFormData()
                .post()
                .uri("/service-request")
                .body(BodyInserters.fromMultipartData(multipartBuilder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ServiceRequestCreateDto castServiceRequestCreateDto(ServiceRequestDto createDto){
        var serviceRequest = new ServiceRequestCreateDto();
        serviceRequest.setProfessionId(createDto.getProfessionId());
        serviceRequest.setDistrictId(createDto.getDistrictId());
        serviceRequest.setDetail(createDto.getDetail());
        serviceRequest.setCustomerId(createDto.getCustomerId());

        return serviceRequest;
    }

    private CustomerPersist castCustomer(ServiceRequestDto createDto){
        CustomerPersist customer = new CustomerPersist();
        customer.name(createDto.getName());
        customer.lastName(createDto.getLastName());
        customer.email(createDto.getEmail());
        customer.password(createDto.getPassword());
        customer.setGender(1);

        return customer;
    }
}