package com.propertymanagement.dto;

import com.propertymanagement.dto.base.BaseDTO;
import com.propertymanagement.model.ListingType;
import com.propertymanagement.model.PropertyType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PropertyDTO extends BaseDTO {
    
    private String title;
    
    private String description;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private PropertyType propertyType;
    
    private ListingType listingType;
    
    private Double totalArea;
    
    private Integer numberOfBedrooms;
    
    private Integer numberOfBathrooms;
    
    private BigDecimal monthlyRent;
    
    private BigDecimal salePrice;
    
    private BigDecimal securityDeposit;
    
    private LocalDate availableFrom;
    
    private boolean available;
    
    private boolean featured;
    
    private List<String> amenities;
    
    private List<String> images;
    
    private Long landlordId;
    
    private String landlordName;
    
    // Explicit getters and setters to avoid Lombok issues
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public PropertyType getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
    
    public ListingType getListingType() {
        return listingType;
    }
    
    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }
    
    public Double getTotalArea() {
        return totalArea;
    }
    
    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }
    
    public Integer getNumberOfBedrooms() {
        return numberOfBedrooms;
    }
    
    public void setNumberOfBedrooms(Integer numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }
    
    public Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }
    
    public void setNumberOfBathrooms(Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }
    
    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }
    
    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
    
    public BigDecimal getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public LocalDate getAvailableFrom() {
        return availableFrom;
    }
    
    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public boolean isFeatured() {
        return featured;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public List<String> getAmenities() {
        return amenities;
    }
    
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public Long getLandlordId() {
        return landlordId;
    }
    
    public void setLandlordId(Long landlordId) {
        this.landlordId = landlordId;
    }
    
    public String getLandlordName() {
        return landlordName;
    }
    
    public void setLandlordName(String landlordName) {
        this.landlordName = landlordName;
    }
} 