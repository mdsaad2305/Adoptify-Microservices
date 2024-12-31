package com.microservices.adoptify.pet_service.repository;

import com.microservices.adoptify.pet_service.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
