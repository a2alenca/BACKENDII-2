package com.minimarket;

import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VentaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaRepository ventaRepository;

    @BeforeEach
    public void setUp() {
        Venta ventaSimulada = new Venta();
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaSimulada);
    }

    @Test
    public void generarVenta_ComoCajero_DeberiaProcesar() throws Exception {
        String ventaJson = "{\"usuarioId\":1, \"detalles\":[]}";

        mockMvc.perform(post("/api/ventas")
                        .with(SecurityMockMvcRequestPostProcessors.user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson))
                .andExpect(status().isOk());
    }

    @Test
    public void generarVenta_ComoCliente_DeberiaDenegar() throws Exception {
        String ventaJson = "{\"usuarioId\":1, \"detalles\":[]}";

        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/api/ventas")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.createEmptyContext()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson))
                .andExpect(status().is3xxRedirection());
    }
}