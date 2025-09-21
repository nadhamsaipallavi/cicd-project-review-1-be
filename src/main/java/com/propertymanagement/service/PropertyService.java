package com.propertymanagement.service;

import com.propertymanagement.model.Property;
import com.propertymanagement.model.PropertyType;
import com.propertymanagement.model.User;
import com.propertymanagement.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService extends BaseService<Property> {
    
    Property createProperty(Property property, User landlord);
    
    Property updateProperty(Long id, Property property);
    
    void deleteProperty(Long id);
    
    Page<Property> findAllAvailable(Pageable pageable);
    
    Page<Property> findByLandlord(User landlord, Pageable pageable);
    
    Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);
    
    Page<Property> findByMonthlyRentBetween(BigDecimal minRent, BigDecimal maxRent, Pageable pageable);
    
    Page<Property> findByNumberOfBedrooms(Integer numberOfBedrooms, Pageable pageable);
    
    Page<Property> searchProperties(
            PropertyType propertyType,
            BigDecimal maxRent,
            Integer minBedrooms,
            Pageable pageable);
    
    List<Property> findFeaturedProperties();
    
    Property togglePropertyAvailability(Long id);
    
    Property togglePropertyFeatured(Long id);
    
    boolean isPropertyAvailable(Long id);
    
    boolean isPropertyOwnedBy(Long propertyId, User landlord);
    
    long countPropertiesByLandlord(User landlord);
    
    long countAvailableProperties();
} 