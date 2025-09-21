package com.propertymanagement.dto;

public class NewCardDTO {
    private String cardNumber;
    private String cardName;
    private int expiryMonth;
    private int expiryYear;
    private String cvv;
    private boolean isDefault = true;
    
    public NewCardDTO() {
    }
    
    public NewCardDTO(String cardNumber, String cardName, int expiryMonth, int expiryYear, String cvv, boolean isDefault) {
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
        this.isDefault = isDefault;
    }
    
    public static NewCardDTOBuilder builder() {
        return new NewCardDTOBuilder();
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getCardName() {
        return cardName;
    }
    
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    
    public int getExpiryMonth() {
        return expiryMonth;
    }
    
    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }
    
    public int getExpiryYear() {
        return expiryYear;
    }
    
    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public static class NewCardDTOBuilder {
        private String cardNumber;
        private String cardName;
        private int expiryMonth;
        private int expiryYear;
        private String cvv;
        private boolean isDefault = true;
        
        NewCardDTOBuilder() {
        }
        
        public NewCardDTOBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }
        
        public NewCardDTOBuilder cardName(String cardName) {
            this.cardName = cardName;
            return this;
        }
        
        public NewCardDTOBuilder expiryMonth(int expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }
        
        public NewCardDTOBuilder expiryYear(int expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }
        
        public NewCardDTOBuilder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }
        
        public NewCardDTOBuilder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }
        
        public NewCardDTO build() {
            return new NewCardDTO(cardNumber, cardName, expiryMonth, expiryYear, cvv, isDefault);
        }
    }
} 