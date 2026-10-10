package com.icesi.ingesoftv.productapi.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.icesi.ingesoftv.productapi.model.Product;
import com.icesi.ingesoftv.productapi.repository.ProductRepository;

@Configuration
@Profile("!test")
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                    new Product("Laptop Dell XPS 15", "Laptop de alto rendimiento 32GB RAM", new BigDecimal("7500000"), 12, "Computadores"),
                    new Product("Monitor LG UltraWide 34\"", "Monitor IPS 144Hz para productividad", new BigDecimal("2300000"), 8, "Monitores"),
                    new Product("Teclado Mecánico Keychron K2", "Teclado inalámbrico switches red", new BigDecimal("480000"), 25, "Accesorios"),
                    new Product("Mouse Logitech MX Master 3S", "Mouse ergonómico para desarrollo", new BigDecimal("520000"), 18, "Accesorios")
                ));
            }
        };
    }
}
