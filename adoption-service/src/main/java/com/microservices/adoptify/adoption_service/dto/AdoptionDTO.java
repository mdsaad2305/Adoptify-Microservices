package com.microservices.adoptify.adoption_service.dto;

import com.microservices.adoptify.adoption_service.model.Adoption.AdoptionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdoptionDTO {
    private Long id;
    private Long petId;
    private Long AdopterId;
    private String ownerUsername;
    private String adopterUsername;
    private String ownerEmail;
    private String adopterEmail;
    private String ownerPhone;
    private String adopterPhone;
    private AdoptionStatus status;
    private LocalDateTime adoptionDate;
    private String reasonOfAdoption;
}
