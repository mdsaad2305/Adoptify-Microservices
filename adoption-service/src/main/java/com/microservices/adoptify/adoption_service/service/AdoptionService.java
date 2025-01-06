package com.microservices.adoptify.adoption_service.service;

import com.microservices.adoptify.adoption_service.dto.AdoptionDTO;
import com.microservices.adoptify.adoption_service.model.Adoption;

import java.util.List;

public interface AdoptionService {
    AdoptionDTO createAdoptionRequest(AdoptionDTO adoptionDTO, String token);
    List<AdoptionDTO> getAllAdoptions(String token);
    AdoptionDTO getAdoptionById(Long id, String token);
    void deleteAdoption(Long id, String token);
    AdoptionDTO updateAdoptionStatus(Long id, AdoptionDTO adoptionDTO, String token);
}
