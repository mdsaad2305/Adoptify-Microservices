package com.microservices.adoptify.adoption_service.service.impl;

import com.microservices.adoptify.adoption_service.client.PetClient;
import com.microservices.adoptify.adoption_service.client.UserClient;
import com.microservices.adoptify.adoption_service.configuration.JWTService;
import com.microservices.adoptify.adoption_service.dto.AdoptionDTO;
import com.microservices.adoptify.adoption_service.dto.PetAndUserDTO;
import com.microservices.adoptify.adoption_service.dto.UserDTO;
import com.microservices.adoptify.adoption_service.external.Pet;
import com.microservices.adoptify.adoption_service.model.Adoption;
import com.microservices.adoptify.adoption_service.model.Adoption.AdoptionStatus;
import com.microservices.adoptify.adoption_service.repository.AdoptionRepository;
import com.microservices.adoptify.adoption_service.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the AdoptionService interface.
 * This class handles the business logic for managing adoptions.
 */
@Service
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final UserClient userClient;
    private final PetClient petClient;
    private final JWTService jwtService;

    public AdoptionServiceImpl(AdoptionRepository adoptionRepository,
                               UserClient userClient,
                               PetClient petClient, JWTService jwtService) {
        this.adoptionRepository = adoptionRepository;
        this.userClient = userClient;
        this.petClient = petClient;
        this.jwtService = jwtService;
    }

    @Override
    public AdoptionDTO createAdoptionRequest(AdoptionDTO adoptionDTO, String token) {

        String trimmedToken = removeBearerFromToken(token);
        String UserIdString = jwtService.extractUserId(trimmedToken);

        Long UserId = Long.parseLong(UserIdString);
        UserDTO adopter = userClient.getUserById(UserId, token);

        PetAndUserDTO petAndOwner = petClient.getPetById(adoptionDTO.getPetId(), token);

        // Create a new Adoption entity
        Adoption adoption = new Adoption();
        adoption.setPetId(adoptionDTO.getPetId());
        adoption.setUserId(adoptionDTO.getAdopterId());
        adoption.setStatus(adoptionDTO.getStatus());
        adoption.setAdoptionDate(LocalDateTime.now());
        adoption.setReasonOfAdoption(adoptionDTO.getReasonOfAdoption());

        // Save the adoption record to the database
        Adoption savedAdoption = adoptionRepository.save(adoption);

        // Update the pet's status to reflect the adoption
        // For example, set status to ADOPTED
//        petAndOwner.setStatus(PetAndUserDTO.Status.ADOPTED);

//        Pet updatedPet = PetAndUserToPetDTO(petAndOwner);

//        petClient.updatePet(petAndOwner.getPetId(), updatedPet, token);

        // Map the saved Adoption entity and related data to AdoptionDTO
        AdoptionDTO responseDTO = new AdoptionDTO();
        responseDTO.setId(savedAdoption.getId());
        responseDTO.setPetId(savedAdoption.getPetId());
        responseDTO.setAdopterId(savedAdoption.getUserId());
        responseDTO.setOwnerUsername(petAndOwner.getUsername());
        responseDTO.setAdopterUsername(adopter.getUsername());
        responseDTO.setOwnerEmail(petAndOwner.getEmail());
        responseDTO.setAdopterEmail(adopter.getEmail());
        responseDTO.setOwnerPhone(petAndOwner.getPhoneNumber());
        responseDTO.setAdopterPhone(adopter.getPhoneNumber());
        responseDTO.setStatus(savedAdoption.getStatus());
        responseDTO.setAdoptionDate(savedAdoption.getAdoptionDate());
        responseDTO.setReasonOfAdoption(savedAdoption.getReasonOfAdoption());

        return responseDTO;
    }

    private String removeBearerFromToken(String token) {
        return token.substring(7).trim();
    }

    private Pet PetAndUserToPetDTO(PetAndUserDTO petAndUserDTO) {
        Pet updatedPet = new Pet();
        updatedPet.setPetId(petAndUserDTO.getPetId());
        updatedPet.setName(petAndUserDTO.getName());
        updatedPet.setType(petAndUserDTO.getType());
        updatedPet.setBreed(petAndUserDTO.getBreed());
        updatedPet.setAge(petAndUserDTO.getAge());
        updatedPet.setDescription(petAndUserDTO.getDescription());
        updatedPet.setLocation(petAndUserDTO.getLocation());
        if(petAndUserDTO.getStatus() == PetAndUserDTO.Status.ADOPTED) {
            updatedPet.setStatus(Pet.Status.ADOPTED);
        } else {
            updatedPet.setStatus(Pet.Status.AVAILABLE);
        }
        updatedPet.setUserId(updatedPet.getUserId());
        updatedPet.setPrimary_image(petAndUserDTO.getPrimary_image());
        updatedPet.setCreatedAt(petAndUserDTO.getCreatedAt());
        updatedPet.setUpdatedAt(petAndUserDTO.getUpdatedAt());

        return updatedPet;
    }

    /**
     * Retrieves all adoption records.
     *
     * @param token JWT token for authorization (passed to Feign clients)
     * @return List of AdoptionDTO containing all adoption information
     */
    @Override
    public List<AdoptionDTO> getAllAdoptions(String token) {
        // Fetch all adoption records from the repository
        List<Adoption> adoptions = adoptionRepository.findAll();

        // Map each Adoption entity to AdoptionDTO with related user and pet data
        return adoptions.stream().map(adoption -> {
            // Fetch adopter (user) details
            UserDTO adopter = userClient.getUserById(adoption.getUserId(), token);

            // Fetch pet and owner details
            PetAndUserDTO petAndOwner = petClient.getPetById(adoption.getPetId(), token);

            // Create and populate AdoptionDTO
            AdoptionDTO dto = new AdoptionDTO();
            dto.setId(adoption.getId());
            dto.setPetId(adoption.getPetId());
            dto.setAdopterId(adoption.getUserId());
            dto.setOwnerUsername(petAndOwner.getUsername());
            dto.setAdopterUsername(adopter.getUsername());
            dto.setOwnerEmail(petAndOwner.getEmail());
            dto.setAdopterEmail(adopter.getEmail());
            dto.setOwnerPhone(petAndOwner.getPhoneNumber());
            dto.setAdopterPhone(adopter.getPhoneNumber());
            dto.setStatus(adoption.getStatus());
            dto.setAdoptionDate(adoption.getAdoptionDate());
            dto.setReasonOfAdoption(adoption.getReasonOfAdoption());

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves a specific adoption record by its ID.
     *
     * @param id    Adoption record ID
     * @param token JWT token for authorization (passed to Feign clients)
     * @return AdoptionDTO containing the adoption information, or null if not found
     */
    @Override
    public AdoptionDTO getAdoptionById(Long id, String token) {
        // Fetch the adoption record from the repository
        Adoption adoption = adoptionRepository.findById(id).orElse(null);
        if (adoption == null) {
            return null; // Adoption not found
        }

        // Fetch adopter (user) details
        UserDTO adopter = userClient.getUserById(adoption.getUserId(), token);

        // Fetch pet and owner details
        PetAndUserDTO petAndOwner = petClient.getPetById(adoption.getPetId(), token);

        // Create and populate AdoptionDTO
        AdoptionDTO dto = new AdoptionDTO();
        dto.setId(adoption.getId());
        dto.setPetId(adoption.getPetId());
        dto.setAdopterId(adoption.getUserId());
        dto.setOwnerUsername(petAndOwner.getUsername());
        dto.setAdopterUsername(adopter.getUsername());
        dto.setOwnerEmail(petAndOwner.getEmail());
        dto.setAdopterEmail(adopter.getEmail());
        dto.setOwnerPhone(petAndOwner.getPhoneNumber());
        dto.setAdopterPhone(adopter.getPhoneNumber());
        dto.setStatus(adoption.getStatus());
        dto.setAdoptionDate(adoption.getAdoptionDate());
        dto.setReasonOfAdoption(adoption.getReasonOfAdoption());

        return dto;
    }

    /**
     * Deletes an adoption record by its ID.
     *
     * @param id    Adoption record ID
     * @param token JWT token for authorization (passed to Feign clients)
     */
    @Override
    public void deleteAdoption(Long id, String token) {
        // Fetch the adoption record from the repository
        Adoption adoption = adoptionRepository.findById(id).orElse(null);
        if (adoption != null) {
            // Fetch pet and owner details
            PetAndUserDTO petAndOwner = petClient.getPetById(adoption.getPetId(), token);

            // Revert pet status to AVAILABLE
            petAndOwner.setStatus(PetAndUserDTO.Status.AVAILABLE);

            Pet updatedPet = PetAndUserToPetDTO(petAndOwner);

            petClient.updatePet(petAndOwner.getPetId(), updatedPet, token);

            // Delete the adoption record from the repository
            adoptionRepository.delete(adoption);
        }
        // If adoption is not found, do nothing (as per no exception handling)
    }

    /**
     * Updates the status of an existing adoption record.
     *
     * @param id          Adoption record ID
     * @param adoptionDTO Adoption details containing the updated status
     * @param token       JWT token for authorization (passed to Feign clients)
     * @return AdoptionDTO containing the updated adoption information, or null if not found
     */
    @Override
    public AdoptionDTO updateAdoptionStatus(Long id, AdoptionDTO adoptionDTO, String token) {
        // Fetch the adoption record from the repository
        Adoption adoption = adoptionRepository.findById(id).orElse(null);
        if (adoption != null) {
            // Update the adoption status if provided
            if (adoptionDTO.getStatus() != null) {
                adoption.setStatus(adoptionDTO.getStatus());
            }

            // Update the reason for adoption if provided
            if (adoptionDTO.getReasonOfAdoption() != null) {
                adoption.setReasonOfAdoption(adoptionDTO.getReasonOfAdoption());
            }

            // Save the updated adoption record
            adoptionRepository.save(adoption);

            // Fetch pet and owner details
            PetAndUserDTO petAndOwner = petClient.getPetById(adoption.getPetId(), token);

            // Update pet status based on adoption status
            if (adoption.getStatus() == AdoptionStatus.APPROVED) {
                petAndOwner.setStatus(PetAndUserDTO.Status.ADOPTED);
            } else if (adoption.getStatus() == AdoptionStatus.REJECTED) {
                petAndOwner.setStatus(PetAndUserDTO.Status.AVAILABLE);
            }

            Pet updatedPet = PetAndUserToPetDTO(petAndOwner);

            petClient.updatePet(petAndOwner.getPetId(), updatedPet, token);

            // Fetch adopter (user) details
            UserDTO adopter = userClient.getUserById(adoption.getUserId(), token);

            // Create and populate AdoptionDTO
            AdoptionDTO dto = new AdoptionDTO();
            dto.setId(adoption.getId());
            dto.setPetId(adoption.getPetId());
            dto.setAdopterId(adoption.getUserId());
            dto.setOwnerUsername(petAndOwner.getUsername());
            dto.setAdopterUsername(adopter.getUsername());
            dto.setOwnerEmail(petAndOwner.getEmail());
            dto.setAdopterEmail(adopter.getEmail());
            dto.setOwnerPhone(petAndOwner.getPhoneNumber());
            dto.setAdopterPhone(adopter.getPhoneNumber());
            dto.setStatus(adoption.getStatus());
            dto.setAdoptionDate(adoption.getAdoptionDate());
            dto.setReasonOfAdoption(adoption.getReasonOfAdoption());

            return dto;
        }
        return null;
    }
}