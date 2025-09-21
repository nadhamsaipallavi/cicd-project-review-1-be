package com.propertymanagement.service.impl;

import com.propertymanagement.exception.ResourceNotFoundException;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.PropertyType;
import com.propertymanagement.model.User;
import com.propertymanagement.repository.PropertyRepository;
import com.propertymanagement.service.PropertyService;
import com.propertymanagement.service.base.impl.BaseServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PropertyServiceImpl extends BaseServiceImpl<Property, PropertyRepository> implements PropertyService {

    public PropertyServiceImpl(PropertyRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Property createProperty(Property property, User landlord) {
        property.setLandlord(landlord);
        property.setAvailable(true);
        return repository.save(property);
    }

    @Override
    @Transactional
    public Property updateProperty(Long id, Property property) {
        Property existingProperty = findById(id);
        
        existingProperty.setTitle(property.getTitle());
        existingProperty.setDescription(property.getDescription());
        existingProperty.setAddress(property.getAddress());
        existingProperty.setPropertyType(property.getPropertyType());
        existingProperty.setTotalArea(property.getTotalArea());
        existingProperty.setNumberOfBedrooms(property.getNumberOfBedrooms());
        existingProperty.setNumberOfBathrooms(property.getNumberOfBathrooms());
        existingProperty.setMonthlyRent(property.getMonthlyRent());
        existingProperty.setSecurityDeposit(property.getSecurityDeposit());
        existingProperty.setAvailableFrom(property.getAvailableFrom());
        existingProperty.setAmenities(property.getAmenities());
        existingProperty.setImages(property.getImages());
        
        return repository.save(existingProperty);
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Property not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Page<Property> findAllAvailable(Pageable pageable) {
        return repository.findByAvailableTrue(pageable);
    }

    @Override
    public Page<Property> findByLandlord(User landlord, Pageable pageable) {
        return repository.findByLandlord(landlord, pageable);
    }

    @Override
    public Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable) {
        return repository.findByPropertyType(propertyType, pageable);
    }

    @Override
    public Page<Property> findByMonthlyRentBetween(BigDecimal minRent, BigDecimal maxRent, Pageable pageable) {
        return repository.findByMonthlyRentBetween(minRent, maxRent, pageable);
    }

    @Override
    public Page<Property> findByNumberOfBedrooms(Integer numberOfBedrooms, Pageable pageable) {
        return repository.findByNumberOfBedrooms(numberOfBedrooms, pageable);
    }

    @Override
    public Page<Property> searchProperties(
            PropertyType propertyType,
            BigDecimal maxRent,
            Integer minBedrooms,
            Pageable pageable) {
        
        if (propertyType != null && maxRent != null && minBedrooms != null) {
            return repository.findByAvailableTrueAndPropertyTypeAndMonthlyRentLessThanEqualAndNumberOfBedroomsGreaterThanEqual(
                    propertyType, maxRent, minBedrooms, pageable);
        } else if (propertyType != null && maxRent != null) {
            return repository.findByAvailableTrueAndPropertyTypeAndMonthlyRentLessThanEqual(
                    propertyType, maxRent, pageable);
        } else if (propertyType != null && minBedrooms != null) {
            return repository.findByAvailableTrueAndPropertyTypeAndNumberOfBedroomsGreaterThanEqual(
                    propertyType, minBedrooms, pageable);
        } else if (maxRent != null && minBedrooms != null) {
            return repository.findByAvailableTrueAndMonthlyRentLessThanEqualAndNumberOfBedroomsGreaterThanEqual(
                    maxRent, minBedrooms, pageable);
        } else if (propertyType != null) {
            return repository.findByAvailableTrueAndPropertyType(propertyType, pageable);
        } else if (maxRent != null) {
            return repository.findByAvailableTrueAndMonthlyRentLessThanEqual(maxRent, pageable);
        } else if (minBedrooms != null) {
            return repository.findByAvailableTrueAndNumberOfBedroomsGreaterThanEqual(minBedrooms, pageable);
        }
        
        return repository.findByAvailableTrue(pageable);
    }

    @Override
    public List<Property> findFeaturedProperties() {
        return repository.findByFeaturedTrueAndAvailableTrue();
    }

    @Override
    @Transactional
    public Property togglePropertyAvailability(Long id) {
        Property property = findById(id);
        property.setAvailable(!property.isAvailable());
        return repository.save(property);
    }

    @Override
    @Transactional
    public Property togglePropertyFeatured(Long id) {
        Property property = findById(id);
        property.setFeatured(!property.isFeatured());
        return repository.save(property);
    }

    @Override
    public boolean isPropertyAvailable(Long id) {
        Property property = findById(id);
        return property.isAvailable();
    }

    @Override
    public boolean isPropertyOwnedBy(Long propertyId, User landlord) {
        return repository.existsByIdAndLandlord(propertyId, landlord);
    }

    @Override
    public long countPropertiesByLandlord(User landlord) {
        return repository.countByLandlord(landlord);
    }

    @Override
    public long countAvailableProperties() {
        return repository.countByAvailableTrue();
    }
} 