package com.springboot.SoilDetection.controller;

import com.springboot.SoilDetection.model.ReadingType;
import com.springboot.SoilDetection.repository.ReadingTypeRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reading-types")
public class ReadingTypeController {

    private final Jdbi jdbi;

    public ReadingTypeController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GetMapping
    public ResponseEntity<List<ReadingType>> getAllReadingTypes() {
        List<ReadingType> readingTypes = jdbi.withExtension(ReadingTypeRepository.class,
                ReadingTypeRepository::findAll);
        return ResponseEntity.ok(readingTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadingType> getReadingTypeById(@PathVariable("id") Long id) {
        Optional<ReadingType> readingType = jdbi.withExtension(ReadingTypeRepository.class, dao -> dao.findById(id));
        return readingType.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ReadingType> getReadingTypeByName(@PathVariable("name") String name) {
        Optional<ReadingType> readingType = jdbi.withExtension(ReadingTypeRepository.class,
                dao -> dao.findByName(name));
        return readingType.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReadingType> createReadingType(@RequestBody ReadingType readingType) {
        Long readingTypeId = jdbi.withExtension(ReadingTypeRepository.class, dao -> dao.insert(readingType));
        readingType.setReadingTypeId(readingTypeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(readingType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadingType> updateReadingType(@PathVariable("id") Long id,
            @RequestBody ReadingType readingType) {
        readingType.setReadingTypeId(id);
        int rowsAffected = jdbi.withExtension(ReadingTypeRepository.class, dao -> dao.update(readingType));
        if (rowsAffected > 0) {
            return ResponseEntity.ok(readingType);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReadingType(@PathVariable("id") Long id) {
        int rowsAffected = jdbi.withExtension(ReadingTypeRepository.class, dao -> dao.delete(id));
        if (rowsAffected > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
