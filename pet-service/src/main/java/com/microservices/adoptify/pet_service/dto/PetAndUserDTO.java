package com.microservices.adoptify.pet_service.dto;

import com.microservices.adoptify.pet_service.external.User;
import com.microservices.adoptify.pet_service.model.Pet;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class PetAndUserDTO {
    private Long petId;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String description;
    private String location;

    @Enumerated(EnumType.STRING)
    private Pet.Status status;
    private String primary_image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long userId;
    private String username;
    private String email;
    private String phoneNumber;

    public PetAndUserDTO(){

    }

    public PetAndUserDTO(String name, String type, String breed, Integer age, String description, String location, Pet.Status status, Long userId, String primary_image, String username, String email, String phoneNumber) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.description = description;
        this.location = location;
        this.status = status;
        this.userId = userId;
        this.primary_image = primary_image;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Pet.Status getStatus() {
        return status;
    }

    public void setStatus(Pet.Status status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPrimary_image() {
        return primary_image;
    }

    public void setPrimary_image(String primary_image) {
        this.primary_image = primary_image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // automatically set time for updatedAt when the row is updated
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public enum Status {
        AVAILABLE, ADOPTED
    }
}
