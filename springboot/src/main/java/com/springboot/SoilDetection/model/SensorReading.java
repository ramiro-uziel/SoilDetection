package com.springboot.SoilDetection.model;

import java.sql.Timestamp;

public class SensorReading {
    private Long readingId;
    private Long plantId;
    private Long readingTypeId;
    private Double readingValue;
    private Timestamp readingTimestamp;

    private Plant plant;
    private ReadingType readingType;

    public Long getReadingId() {
        return readingId;
    }

    public void setReadingId(Long readingId) {
        this.readingId = readingId;
    }

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public Long getReadingTypeId() {
        return readingTypeId;
    }

    public void setReadingTypeId(Long readingTypeId) {
        this.readingTypeId = readingTypeId;
    }

    public Double getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(Double readingValue) {
        this.readingValue = readingValue;
    }

    public Timestamp getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(Timestamp readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public ReadingType getReadingType() {
        return readingType;
    }

    public void setReadingType(ReadingType readingType) {
        this.readingType = readingType;
    }
}
