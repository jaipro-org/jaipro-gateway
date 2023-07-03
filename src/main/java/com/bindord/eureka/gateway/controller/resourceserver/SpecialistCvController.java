package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.domain.specialist.SpecialistBiographyDto;
import com.bindord.eureka.gateway.domain.specialist.SpecialistGalleryUpdateDto;
import com.bindord.eureka.gateway.services.SpecialistCvService;
import com.bindord.eureka.gateway.services.SpecialistService;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.SpecialistCv;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/specialist-cv")
@Slf4j
public class SpecialistCvController {

    private final SpecialistCvService specialistCvService;
    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Update gallery of the specialist",
            responseCode = "200")
    @PostMapping(value = "/gallery",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Mono<SpecialistCv> updateGallery(@RequestPart List<FilePart> images,
                                            @RequestPart @Valid SpecialistGalleryUpdateDto specialistGallery) {
        MultipartBodyBuilder multipartBuilder = new MultipartBodyBuilder();
        images.forEach(
                ele -> multipartBuilder.part("images", ele)
        );

        multipartBuilder.part("specialistGallery", specialistGallery);
        return resourceServerClientConfiguration.initFormData()
                .put()
                .uri("/specialist-cv/gallery")
                .body(BodyInserters.fromMultipartData(
                        multipartBuilder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SpecialistCv.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Delete image from gallery of specialist cv",
            responseCode = "200")
    @DeleteMapping(value = "/gallery/{specialistId}/{imageName}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Mono<Void> deleteImageFromGallery(@PathVariable UUID specialistId, @PathVariable String imageName) {

        return resourceServerClientConfiguration.init()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/specialist-cv/gallery/{specialistId}/{imageName}")
                        .build(specialistId, imageName))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Get specialist biography by id", responseCode = "200")
    @GetMapping(value = "/biography/{specialistId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<SpecialistBiographyDto> findSpecialistBiographyById(@PathVariable UUID specialistId) {
        return specialistCvService.getBiography(specialistId);
    }
}