package com.springboot.SoilDetection.controller;

import com.springboot.SoilDetection.model.SensorReading;
import com.springboot.SoilDetection.model.ReadingType;
import com.springboot.SoilDetection.repository.SensorReadingRepository;
import com.springboot.SoilDetection.repository.ReadingTypeRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final Jdbi jdbi;

    public SensorController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GetMapping
    public ResponseEntity<List<SensorReading>> getAllSensorReadings() {
        List<SensorReading> readings = jdbi.withExtension(SensorReadingRepository.class,
                SensorReadingRepository::findAll);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorReading> getSensorReadingById(@PathVariable Long id) {
        Optional<SensorReading> reading = jdbi.withExtension(SensorReadingRepository.class, dao -> dao.findById(id));
        return reading.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plant/{plantId}")
    public ResponseEntity<List<SensorReading>> getReadingsByPlantId(@PathVariable Long plantId) {
        List<SensorReading> readings = jdbi.withExtension(SensorReadingRepository.class,
                dao -> dao.findByPlantId(plantId));
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/plant/{plantId}/readings")
    public ResponseEntity<Map<String, List<SensorReading>>> getReadingsByPlantIdGroupedByType(
            @PathVariable Long plantId,
            @RequestParam(required = false) Integer limit) {

        List<ReadingType> readingTypes = jdbi.withExtension(ReadingTypeRepository.class,
                ReadingTypeRepository::findAll);

        Map<String, List<SensorReading>> groupedReadings = new HashMap<>();

        for (ReadingType type : readingTypes) {
            List<SensorReading> readings;

            if (limit != null) {
                readings = jdbi.withExtension(SensorReadingRepository.class,
                        dao -> dao.findLatestByPlantAndReadingType(plantId, type.getReadingTypeId(), limit));
            } else {
                readings = jdbi.withExtension(SensorReadingRepository.class,
                        dao -> dao.findByPlantAndReadingType(plantId, type.getReadingTypeId()));
            }

            readings.forEach(reading -> reading.setReadingType(type));

            groupedReadings.put(type.getReadingName(), readings);
        }

        return ResponseEntity.ok(groupedReadings);
    }

    @GetMapping("/plant/{plantId}/type/{readingTypeId}")
    public ResponseEntity<List<SensorReading>> getReadingsByPlantAndType(
            @PathVariable Long plantId,
            @PathVariable Long readingTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
            @RequestParam(required = false) Integer limit) {

        List<SensorReading> readings;

        if (startDate != null && endDate != null) {
            Timestamp startTimestamp = new Timestamp(startDate.getTime());
            Timestamp endTimestamp = new Timestamp(endDate.getTime());
            readings = jdbi.withExtension(SensorReadingRepository.class,
                    dao -> dao.findByPlantAndReadingTypeAndTimeRange(plantId, readingTypeId, startTimestamp,
                            endTimestamp));
        } else if (limit != null) {
            readings = jdbi.withExtension(SensorReadingRepository.class,
                    dao -> dao.findLatestByPlantAndReadingType(plantId, readingTypeId, limit));
        } else {
            readings = jdbi.withExtension(SensorReadingRepository.class,
                    dao -> dao.findByPlantAndReadingType(plantId, readingTypeId));
        }

        Optional<ReadingType> readingType = jdbi.withExtension(ReadingTypeRepository.class,
                dao -> dao.findById(readingTypeId));
        readingType.ifPresent(type -> readings.forEach(reading -> reading.setReadingType(type)));

        return ResponseEntity.ok(readings);
    }

    @PostMapping
    public ResponseEntity<SensorReading> createSensorReading(@RequestBody SensorReading sensorReading) {
        if (sensorReading.getReadingTimestamp() == null) {
            sensorReading.setReadingTimestamp(new Timestamp(System.currentTimeMillis()));
        }

        Long readingId = jdbi.withExtension(SensorReadingRepository.class,
                dao -> dao.insert(sensorReading));
        sensorReading.setReadingId(readingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorReading);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorReading> updateSensorReading(@PathVariable Long id,
            @RequestBody SensorReading sensorReading) {
        sensorReading.setReadingId(id);
        int rowsAffected = jdbi.withExtension(SensorReadingRepository.class,
                dao -> dao.update(sensorReading));

        if (rowsAffected > 0) {
            return ResponseEntity.ok(sensorReading);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensorReading(@PathVariable Long id) {
        int rowsAffected = jdbi.withExtension(SensorReadingRepository.class,
                dao -> dao.delete(id));

        if (rowsAffected > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "up");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
