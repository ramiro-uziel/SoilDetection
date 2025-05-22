package com.springboot.SoilDetection.model;

public class ReadingType {
    private Long readingTypeId;
    private String readingName;
    private String unitOfMeasure;
    private Double minHealthyValue;
    private Double maxHealthyValue;
    private String description;

    public Long getReadingTypeId() {
        return readingTypeId;
    }

    public void setReadingTypeId(Long readingTypeId) {
        this.readingTypeId = readingTypeId;
    }

    public String getReadingName() {
        return readingName;
    }

    public void setReadingName(String readingName) {
        this.readingName = readingName;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Double getMinHealthyValue() {
        return minHealthyValue;
    }

    public void setMinHealthyValue(Double minHealthyValue) {
        this.minHealthyValue = minHealthyValue;
    }

    public Double getMaxHealthyValue() {
        return maxHealthyValue;
    }

    public void setMaxHealthyValue(Double maxHealthyValue) {
        this.maxHealthyValue = maxHealthyValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
