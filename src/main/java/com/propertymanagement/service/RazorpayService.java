package com.propertymanagement.service;

import org.json.JSONObject;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import java.math.BigDecimal;
import java.util.Map;

public interface RazorpayService {
    
    /**
     * Create a new Razorpay order
     * 
     * @param amount Amount in INR (will be converted to paise)
     * @param receiptId Reference receipt ID
     * @param notes Additional notes for the order
     * @return The created order
     * @throws RazorpayException If there was an error creating the order
     */
    Order createOrder(BigDecimal amount, String receiptId, Map<String, String> notes) throws RazorpayException;
    
    /**
     * Verify payment signature to confirm payment is valid
     * 
     * @param orderId Razorpay order ID
     * @param paymentId Razorpay payment ID
     * @param signature Razorpay signature
     * @return true if signature verification passes, false otherwise
     */
    boolean verifyPaymentSignature(String orderId, String paymentId, String signature);
    
    /**
     * Fetch payment details from Razorpay
     * 
     * @param paymentId Razorpay payment ID
     * @return Payment details as JSONObject
     * @throws RazorpayException If there was an error fetching the payment
     */
    JSONObject fetchPaymentById(String paymentId) throws RazorpayException;
    
    /**
     * Generate a receipt URL for a payment (invoice)
     * 
     * @param paymentId Razorpay payment ID
     * @return URL of the invoice/receipt
     * @throws RazorpayException If there was an error generating the receipt
     */
    String generateReceipt(String paymentId) throws RazorpayException;
} 