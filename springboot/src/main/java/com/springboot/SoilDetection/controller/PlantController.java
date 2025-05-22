package com.springboot.SoilDetection.controller;

import com.springboot.SoilDetection.model.Plant;
import com.springboot.SoilDetection.repository.PlantRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final Jdbi jdbi;

    public PlantController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GetMapping
    public ResponseEntity<List<Plant>> getAllPlants() {
        List<Plant> plants = jdbi.withExtension(PlantRepository.class, PlantRepository::findAll);
        return ResponseEntity.ok(plants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable("id") Long id) {
        Optional<Plant> plant = jdbi.withExtension(PlantRepository.class, dao -> dao.findById(id));
        return plant.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Plant> createPlant(@RequestBody Plant plant) {
        Long plantId = jdbi.withExtension(PlantRepository.class, dao -> dao.insert(plant));
        plant.setPlantId(plantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(plant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable("id") Long id, @RequestBody Plant plant) {
        plant.setPlantId(id);
        int rowsAffected = jdbi.withExtension(PlantRepository.class, dao -> dao.update(plant));
        if (rowsAffected > 0) {
            return ResponseEntity.ok(plant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable("id") Long id) {
        int rowsAffected = jdbi.withExtension(PlantRepository.class, dao -> dao.delete(id));
        if (rowsAffected > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
