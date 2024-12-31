package com.microservices.adoptify.pet_service.service;

import com.microservices.adoptify.pet_service.dto.PetAndUserDTO;
import com.microservices.adoptify.pet_service.model.Pet;

import java.util.List;
import java.util.Optional;

public interface PetService {
    // Create or update a pet
    Pet addPet(Pet pet);

    // Get all pets
    List<PetAndUserDTO> getAllPets(String token);

    // Get a pet by ID
    PetAndUserDTO getPetById(Long id, String token);

    // Delete a pet by ID
    void deletePet(Long id);
    boolean updatePet(long id, Pet pet);
}
