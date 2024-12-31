package com.microservices.adoptify.pet_service.service.impl;


import com.microservices.adoptify.pet_service.clients.UserClient;
import com.microservices.adoptify.pet_service.dto.PetAndUserDTO;
import com.microservices.adoptify.pet_service.external.User;
import com.microservices.adoptify.pet_service.mapper.UserAndPetToDTO;
import com.microservices.adoptify.pet_service.model.Pet;
import com.microservices.adoptify.pet_service.repository.PetRepository;
import com.microservices.adoptify.pet_service.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserClient userClient;

    public PetServiceImpl(PetRepository petRepository , UserClient userClient) {
        this.petRepository = petRepository;
        this.userClient = userClient;
    }

    // Create or update a pet
    @Override
    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
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
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    public boolean updatePet(long id, Pet updatePet){
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
}
