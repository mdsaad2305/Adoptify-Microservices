package com.microservices.adoptify.pet_service.service.impl;


import com.microservices.adoptify.pet_service.model.Pet;
import com.microservices.adoptify.pet_service.repository.PetRepository;
import com.microservices.adoptify.pet_service.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetRepository petRepository;

    // Create or update a pet
    @Override
    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
    }

    // Get all pets
    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    // Get a pet by ID
    @Override
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
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
