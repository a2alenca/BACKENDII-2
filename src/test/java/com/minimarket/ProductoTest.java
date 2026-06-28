package com.minimarket;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoRepository productoRepository;

    @BeforeEach
    public void setUp() {
        // Configuración del entorno
        Producto productoSimulado = new Producto();
        productoSimulado.setId(1L);
        productoSimulado.setNombre("Leche Entera");
        productoSimulado.setPrecio(1200.0);
        productoSimulado.setStock(50);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoSimulado));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoSimulado);
    }

    @Test
    public void modificarProducto_ComoAdmin_DeberiaPermitir() throws Exception {
        String productoJson = "{\"id\":1, \"nombre\":\"Leche Entera\", \"precio\":1200, \"stock\":50}";

        mockMvc.perform(put("/api/productos/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson))
                .andExpect(status().isOk());
    }

    @Test
    public void modificarProducto_ComoCliente_DeberiaDenegarAcceso() throws Exception {
        String productoJson = "{\"id\":1, \"nombre\":\"Leche Entera\", \"precio\":1200, \"stock\":50}";

        SecurityContextHolder.clearContext();

        mockMvc.perform(put("/api/productos/1")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.createEmptyContext()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson))
                .andExpect(status().is3xxRedirection()); // Valida la redirección
    }
}