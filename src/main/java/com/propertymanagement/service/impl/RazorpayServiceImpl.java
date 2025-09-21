package com.propertymanagement.service.impl;

import com.propertymanagement.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayServiceImpl.class);
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    @Override
    public Order createOrder(BigDecimal amount, String receiptId, Map<String, String> notes) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            // Razorpay expects amount in paise (1 INR = 100 paise)
            // Use setScale to ensure proper rounding
            long amountInPaise = amount.multiply(new BigDecimal("100")).setScale(0, java.math.RoundingMode.HALF_UP).longValue();
            
            // For test mode, Razorpay has maximum transaction limits
            // Typically 500000 paise (5000 INR) in test mode
            // This can be removed in production with real API keys
            if (amountInPaise > 500000) {
                logger.warn("Amount exceeds Razorpay test mode limit. Capping at 5000 INR for testing purposes.");
                amountInPaise = 500000; // Cap at 5000 INR (500000 paise)
            }
            
            logger.info("Creating Razorpay order for amount: {} INR ({} paise)", amount, amountInPaise);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);
            
            if (notes != null && !notes.isEmpty()) {
                JSONObject notesObj = new JSONObject();
                for (Map.Entry<String, String> entry : notes.entrySet()) {
                    notesObj.put(entry.getKey(), entry.getValue());
                }
                orderRequest.put("notes", notesObj);
            }
            
            logger.info("Creating Razorpay order for amount: {} INR, Receipt ID: {}", amount, receiptId);
            Order order = razorpayClient.orders.create(orderRequest);
            logger.info("Razorpay order created successfully: {}", order.get("id").toString());
            
            return order;
        } catch (RazorpayException e) {
            logger.error("Error creating Razorpay order: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);
            
            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);
            
            if (isValid) {
                logger.info("Payment signature verified successfully for payment ID: {}", paymentId);
            } else {
                logger.warn("Payment signature verification failed for payment ID: {}", paymentId);
            }
            
            return isValid;
        } catch (RazorpayException e) {
            logger.error("Error verifying payment signature: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public JSONObject fetchPaymentById(String paymentId) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            Payment payment = razorpayClient.payments.fetch(paymentId);
            return payment.toJson();
        } catch (RazorpayException e) {
            logger.error("Error fetching payment details: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public String generateReceipt(String paymentId) throws RazorpayException {
        try {
            // For now, just return a link to Razorpay dashboard's invoice page
            // In a real-world scenario, you would use Razorpay's Invoice API
            // or generate a custom invoice and store it on your server/cloud storage
            
            // Example receipt URL - you'd need to customize this based on Razorpay's actual URL format
            return "https://dashboard.razorpay.com/app/payments/" + paymentId;
        } catch (Exception e) {
            logger.error("Error generating receipt: {}", e.getMessage(), e);
            throw new RazorpayException(e.getMessage());
        }
    }
} 