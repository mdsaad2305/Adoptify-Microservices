package com.microservices.adoptify.adoption_service.client;

import com.microservices.adoptify.adoption_service.dto.PetAndUserDTO;
import com.microservices.adoptify.adoption_service.external.Pet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PET-SERVICE")
public interface PetClient {

    @GetMapping("/api/pets/{id}")
    PetAndUserDTO getPetById(@PathVariable("id") Long id, @RequestHeader("Authorization") String token);

    @PatchMapping("/api/pets/{id}")
    void updatePet(@PathVariable("id") Long id, Pet updatedPet, @RequestHeader("Authorization") String token);
}
