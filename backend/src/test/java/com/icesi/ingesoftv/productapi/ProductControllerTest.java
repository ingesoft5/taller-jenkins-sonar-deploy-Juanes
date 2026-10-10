package com.icesi.ingesoftv.productapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icesi.ingesoftv.productapi.controller.ProductController;
import com.icesi.ingesoftv.productapi.model.Product;
import com.icesi.ingesoftv.productapi.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("GET /api/products debe retornar 200 y la lista de productos")
    void testGetAllProducts() throws Exception {
        Product p = new Product("iPad Air M2", "Tablet Apple 128GB", new BigDecimal("3200000"), 5, "Tablets");
        p.setId(1L);

        when(productService.findAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("iPad Air M2"))
                .andExpect(jsonPath("$[0].category").value("Tablets"));
    }

    @Test
    @DisplayName("POST /api/products debe retornar 201 Created al crear un producto")
    void testCreateProduct() throws Exception {
        Product p = new Product("iPad Air M2", "Tablet Apple 128GB", new BigDecimal("3200000"), 5, "Tablets");
        p.setId(1L);

        when(productService.save(any(Product.class))).thenReturn(p);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPad Air M2"));
    }
}
