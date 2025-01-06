package com.microservices.adoptify.adoption_service.repository;

import com.microservices.adoptify.adoption_service.model.Adoption;
import com.microservices.adoptify.adoption_service.model.Adoption.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    List<Adoption> findByUserId(Long userId);
    List<Adoption> findByPetId(Long petId);
    List<Adoption> findByStatus(AdoptionStatus status);
}
