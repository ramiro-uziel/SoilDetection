package com.springboot.SoilDetection.controller;

import com.springboot.SoilDetection.model.Sensor;
import com.springboot.SoilDetection.repository.SensorRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class SensorController {

    private final Jdbi jdbi;

    public SensorController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sensor> getSensorById(@PathVariable Long id) {
        Optional<Sensor> sensor = jdbi.withExtension(SensorRepository.class, dao -> dao.findById(id));
        return sensor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
