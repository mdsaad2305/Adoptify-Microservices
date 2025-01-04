package com.microservices.adoptify.pet_service.service;

import com.microservices.adoptify.pet_service.clients.UserClient;
import com.microservices.adoptify.pet_service.configuration.JWTService;
import com.microservices.adoptify.pet_service.model.Pet;
import com.microservices.adoptify.pet_service.repository.PetRepository;
import com.microservices.adoptify.pet_service.service.impl.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PetServiceImplTest {
    @Mock private PetRepository petRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private PetServiceImpl petServiceImpl;

    private Pet pet1;

    @BeforeEach
    void setup() throws Exception{
        MockitoAnnotations.openMocks(this);
        pet1 = new Pet("testPet1", "type1", "breed1", 1, "some1", "Somewhere1", Pet.Status.AVAILABLE, 1L, "SomeImage1");
    }

    @Test
    void addPet_ReturnsPetSuccessfully() {
        String token = "Bearer sometoken";
        Pet updatedPet1 = new Pet("testPet1", "type1", "breed1", 1, "some1", "Somewhere1", Pet.Status.AVAILABLE, 1L, "SomeImage1");
        updatedPet1.setUserId(1L);

        when(jwtService.extractUserId("sometoken")).thenReturn("1");
        when(petRepository.save(pet1)).thenReturn(updatedPet1);

        Pet savedPet = petServiceImpl.addPet(pet1, token);

        assertNotNull(savedPet);
        assertEquals(updatedPet1, savedPet);

        verify(jwtService, times(1)).extractUserId("sometoken");
        verify(petRepository, times(1)).save(pet1);

    }

}
