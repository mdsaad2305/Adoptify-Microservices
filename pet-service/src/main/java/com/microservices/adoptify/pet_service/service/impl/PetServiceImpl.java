package com.microservices.adoptify.pet_service.service.impl;


import com.microservices.adoptify.pet_service.clients.UserClient;
import com.microservices.adoptify.pet_service.configuration.JWTService;
import com.microservices.adoptify.pet_service.dto.PetAndUserDTO;
import com.microservices.adoptify.pet_service.external.User;
import com.microservices.adoptify.pet_service.mapper.UserAndPetToDTO;
import com.microservices.adoptify.pet_service.model.Pet;
import com.microservices.adoptify.pet_service.repository.PetRepository;
import com.microservices.adoptify.pet_service.service.PetService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserClient userClient;

    private final JWTService jwtService;

    public PetServiceImpl(PetRepository petRepository , UserClient userClient, JWTService jwtService) {
        this.petRepository = petRepository;
        this.userClient = userClient;
        this.jwtService = jwtService;
    }

    // Create or update a pet
    @Override
    public Pet addPet(Pet pet, String token) {
        String trimmedToken = removeBearerFromToken(token);
        String userIdString = jwtService.extractUserId(trimmedToken);
        try {
            Long userId = Long.parseLong(userIdString);
            pet.setUserId(userId);
            return petRepository.save(pet);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    // Get all pets
    @Override
    public List<PetAndUserDTO> getAllPets(String token) {
        List<Pet> petList = petRepository.findAll();
        return convertPetAndUserToDTO(petList, token);
    }

    private List<PetAndUserDTO> convertPetAndUserToDTO(List<Pet> pets, String token ) {
        List<PetAndUserDTO> petAndUserDTOs = new ArrayList<>();
        for(Pet pet : pets) {
            User user = userClient.getUserById(pet.getUserId(), token);
            PetAndUserDTO petAndUserDTO = UserAndPetToDTO.toDTO(pet, user);
            petAndUserDTOs.add(petAndUserDTO);
        }
        return petAndUserDTOs;
    }

    // Get a pet by ID
    @Override
    public PetAndUserDTO getPetById(Long id, String token) {
        Pet pet = petRepository.findById(id).orElse(null);
        return convertPetAndUserToDTO(pet, token);

    }

    private PetAndUserDTO convertPetAndUserToDTO(Pet pet, String token ) {
            User user = userClient.getUserById(pet.getUserId(), token);
            return UserAndPetToDTO.toDTO(pet, user);
    }

    // Delete a pet by ID
    @Override
    public boolean deletePet(Long id, String token) {
        String trimmedToken = removeBearerFromToken(token);
        String userIdString = jwtService.extractUserId(trimmedToken);
        try{
            Long userId = Long.parseLong(userIdString);
            Pet pet = petRepository.findById(id).orElse(null);
            if(pet != null) {
                if(pet.getUserId().equals(userId)) {
                    petRepository.delete(pet);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updatePet(long id, Pet updatePet, String token){
        Optional<Pet> optionalPet = petRepository.findById(id);
        if(optionalPet.isPresent()){
            Pet pet = optionalPet.get();
            if(updatePet.getName() != null){
                pet.setName(updatePet.getName());
            }
            if(updatePet.getType() != null){
                pet.setType(updatePet.getType());
            }
            if(updatePet.getBreed() != null){
                pet.setBreed(updatePet.getBreed());
            }
            if(updatePet.getDescription() != null){
                pet.setDescription(updatePet.getDescription());
            }
            if(updatePet.getLocation() != null){
                pet.setLocation(updatePet.getLocation());
            }
            if(updatePet.getAge() != null){
                pet.setAge(updatePet.getAge());
            }
            if(updatePet.getStatus() != null){
                pet.setStatus(updatePet.getStatus());
            }
            if(updatePet.getName() != null) {
                pet.setName(updatePet.getName());
            }
            return  true;
        }
        return  false;
    }

    private String removeBearerFromToken(String token) {
        return token.substring(7).trim();
    }
}
