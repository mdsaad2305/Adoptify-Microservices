package com.microservices.adoptify.adoption_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long petId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private AdoptionStatus status;

    private LocalDateTime adoptionDate;

    private String reasonOfAdoption;

    public enum AdoptionStatus {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED
    }
}
