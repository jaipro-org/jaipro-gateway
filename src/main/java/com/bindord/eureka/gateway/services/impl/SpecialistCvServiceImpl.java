package com.bindord.eureka.gateway.services.impl;

import com.bindord.eureka.gateway.domain.specialist.SpecialistBiographyDto;
import com.bindord.eureka.gateway.services.SpecialistCvService;
import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.Photo;
import com.bindord.eureka.resourceserver.model.SpecialistCvExperienceDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpecialistCvServiceImpl implements SpecialistCvService {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @Override
    public Mono<SpecialistBiographyDto> getBiography(UUID specialistId) {

        Mono<String> aboutMono = getAbout(specialistId);
        Flux<SpecialistCvExperienceDto> experiencesFlux = getExperiences(specialistId);
        Flux<Photo> galleryFlux = getGallery(specialistId);

        return Flux
                .zip(aboutMono.flux(), experiencesFlux.collectList(), galleryFlux.collectList())
                .flatMap(tuple -> {
                   String about = tuple.getT1();
                    List<SpecialistCvExperienceDto> experiences = tuple.getT2();
                    List<Photo> gallery = tuple.getT3();

                    var specialistBiographyDto = new SpecialistBiographyDto();
                    specialistBiographyDto.setAbout(about);
                    specialistBiographyDto.setExperiences(experiences);
                    specialistBiographyDto.setGallery(gallery);

                    return Mono.just(specialistBiographyDto);
                })
                .single();
    }

    private Mono<String> getAbout(UUID specialistId){
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/about/{id}").build(specialistId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<SpecialistCvExperienceDto> getExperiences(UUID specialistId){
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/experience/{id}").build(specialistId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(SpecialistCvExperienceDto.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<Photo> getGallery(UUID specialistId){
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/specialist-cv/gallery/{id}").build(specialistId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Photo.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
