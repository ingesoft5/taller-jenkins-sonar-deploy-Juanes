package com.icesi.ingesoftv.productapi;

import com.icesi.ingesoftv.productapi.model.Product;
import com.icesi.ingesoftv.productapi.repository.ProductRepository;
import com.icesi.ingesoftv.productapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("Auriculares Sony WH-1000XM5", "Cancelación de ruido activa", new BigDecimal("1450000"), 10, "Audio");
        sampleProduct.setId(1L);
    }

    @Test
    @DisplayName("Debe retornar la lista completa de productos")
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> products = productService.findAll();

        assertThat(products).isNotEmpty();
        assertThat(products.get(0).getName()).isEqualTo("Auriculares Sony WH-1000XM5");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar y retornar un nuevo producto")
    void testSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product saved = productService.save(sampleProduct);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    @DisplayName("Debe actualizar un producto existente")
    void testUpdateProduct() {
        Product updatedData = new Product("Auriculares Sony XM5", "Nueva descripción", new BigDecimal("1390000"), 15, "Audio");
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Optional<Product> result = productService.update(1L, updatedData);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Auriculares Sony XM5");
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    @DisplayName("Debe eliminar un producto si existe")
    void testDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        boolean deleted = productService.deleteById(1L);

        assertThat(deleted).isTrue();
        verify(productRepository, times(1)).deleteById(1L);
    }
}
