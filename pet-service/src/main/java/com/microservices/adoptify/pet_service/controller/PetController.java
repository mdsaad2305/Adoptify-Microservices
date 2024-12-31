package com.microservices.adoptify.pet_service.controller;

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

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetAndUserDTO>> getPets(@RequestHeader("Authorization") String token) {
        System.out.println(token);
        List<PetAndUserDTO> pets = petService.getAllPets(token);
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetAndUserDTO> getPetById(@PathVariable("petId") Long petId, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(petService.getPetById(petId, token), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        return new ResponseEntity<>(petService.addPet(pet), HttpStatus.CREATED);
    }

}
