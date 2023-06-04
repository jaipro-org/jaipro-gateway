package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.auth.model.Customer;
import com.bindord.eureka.auth.model.CustomerPersist;
import com.bindord.eureka.auth.model.UserPasswordDTO;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.CustomerInformationDto;
import com.bindord.eureka.resourceserver.model.CustomerInformationUpdateDto;
import com.bindord.eureka.resourceserver.model.CustomerLocationUpdateDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/customer")
@Slf4j
public class CustomerController {

    private final AuthClientConfiguration authClientConfiguration;
    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Persist a customer",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Customer> persistCustomer(@Valid @RequestBody CustomerPersist customer) {
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

    @ApiResponse(description = "Update a customer information",
            responseCode = "200")
    @PutMapping(value = "/updateInformation",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> updateInformation(@Valid @RequestBody CustomerInformationUpdateDto customer) {
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/customer/updateAbout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerInformationUpdateDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a customer information",
            responseCode = "200")
    @PutMapping(value = "/updateLocation",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> updateLocation(@Valid @RequestBody CustomerLocationUpdateDto customer) {
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/customer/updateLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerLocationUpdateDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a customer photo",
            responseCode = "200")
    @PostMapping(value = "/updatePhoto",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<Void> updatePhoto(@RequestPart(name = "file", required = false) FilePart file, @RequestPart("id") String id, @RequestPart("extension") String extension){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file);
        builder.part("id", id);
        builder.part("extension", extension);

        return resourceServerClientConfiguration
                .initFormData()
                .post()
                .uri("/customer/updatePhoto")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a customer information",
            responseCode = "200")
    @PutMapping(value = "/updatePassword",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Void> updatePassword(@Valid @RequestBody UserPasswordDTO userPassword) {
        return authClientConfiguration.init()
                .put()
                .uri("/customer/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userPassword), UserPasswordDTO.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Get customer full information",
            responseCode = "200")
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<CustomerInformationDto> getCustomerInformation(@PathVariable UUID id) {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/customer/{id}/information")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CustomerInformationDto.class);
    }
}
