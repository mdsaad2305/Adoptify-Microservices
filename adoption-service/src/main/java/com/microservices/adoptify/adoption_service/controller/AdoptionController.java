package com.microservices.adoptify.adoption_service.controller;

import com.microservices.adoptify.adoption_service.dto.AdoptionDTO;
import com.microservices.adoptify.adoption_service.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @PostMapping
    public ResponseEntity<AdoptionDTO> createAdoption(
            @RequestBody AdoptionDTO adoptionDTO,
            @RequestHeader("Authorization") String token) {
        AdoptionDTO createdAdoption = adoptionService.createAdoptionRequest(adoptionDTO, token);
        return ResponseEntity.ok(createdAdoption);
    }

    @GetMapping
    public ResponseEntity<List<AdoptionDTO>> getAllAdoptions(
            @RequestHeader("Authorization") String token) {
        List<AdoptionDTO> adoptions = adoptionService.getAllAdoptions(token);
        return ResponseEntity.ok(adoptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptionDTO> getAdoptionById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        AdoptionDTO adoption = adoptionService.getAdoptionById(id, token);
        if (adoption != null) {
            return ResponseEntity.ok(adoption);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdoption(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        adoptionService.deleteAdoption(id, token);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdoptionDTO> updateAdoptionStatus(
            @PathVariable Long id,
            @RequestBody AdoptionDTO adoptionDTO,
            @RequestHeader("Authorization") String token) {
        AdoptionDTO updatedAdoption = adoptionService.updateAdoptionStatus(id, adoptionDTO, token);
        if (updatedAdoption != null) {
            return ResponseEntity.ok(updatedAdoption);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
