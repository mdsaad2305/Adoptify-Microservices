package com.microservices.adoptify.pet_service.controller;

import com.microservices.adoptify.pet_service.configuration.JWTService;
import com.microservices.adoptify.pet_service.dto.PetAndUserDTO;
import com.microservices.adoptify.pet_service.model.Pet;
import com.microservices.adoptify.pet_service.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final JWTService jwtService;

    public PetController(PetService petService, JWTService jwtService) {
        this.petService = petService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<PetAndUserDTO>> getPets(@RequestHeader("Authorization") String token) {
        System.out.println(token);
        List<PetAndUserDTO> pets = petService.getAllPets(token);
        String trimmedToken = token.substring(7).trim();
        String userId = jwtService.extractUserId(trimmedToken);
        System.out.println(userId);
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetAndUserDTO> getPetById(@PathVariable("petId") Long petId, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(petService.getPetById(petId, token), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(petService.addPet(pet, token), HttpStatus.CREATED);
    }

}
