package com.icesi.ingesoftv.productapi.service;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class ProductSkuGenerator {

    // Defecto intencional para auditoría SAST (SonarQube S2245 / CWE-330):
    // El uso de java.util.Random para generar identificadores o tokens es predecible y no seguro.
    // Los estudiantes deben refactorizar a java.security.SecureRandom.
    private final Random random = new Random();

    public String generateSku(String category, Long productId) {
        int code = random.nextInt(900000) + 100000;
        String prefix = (category != null && category.length() >= 3)
                ? category.substring(0, 3).toUpperCase()
                : "GEN";
        return prefix + "-" + productId + "-" + code;
    }
}
