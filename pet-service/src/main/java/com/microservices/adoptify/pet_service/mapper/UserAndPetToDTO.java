package com.microservices.adoptify.pet_service.mapper;

import com.microservices.adoptify.pet_service.dto.PetAndUserDTO;
import com.microservices.adoptify.pet_service.external.User;
import com.microservices.adoptify.pet_service.model.Pet;

public class UserAndPetToDTO {

    public static PetAndUserDTO toDTO(Pet pet, User user) {
        PetAndUserDTO petDTO = new PetAndUserDTO();
        petDTO.setPetId(pet.getPetId());
        petDTO.setUserId(user.getUserId());
        petDTO.setName(pet.getName());
        petDTO.setType(pet.getType());
        petDTO.setBreed(pet.getBreed());
        petDTO.setAge(pet.getAge());
        petDTO.setDescription(pet.getDescription());
        petDTO.setLocation(pet.getLocation());
        petDTO.setCreatedAt(pet.getCreatedAt());
        petDTO.setUpdatedAt(pet.getUpdatedAt());
        petDTO.setPrimary_image(pet.getPrimary_image());
        petDTO.setStatus(pet.getStatus());
        petDTO.setUsername(user.getUsername());
        petDTO.setEmail(user.getEmail());
        petDTO.setPhoneNumber(user.getPhoneNumber());

        return petDTO;
    }
}
