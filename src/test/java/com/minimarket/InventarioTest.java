package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.repository.InventarioRepository;
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
public class InventarioTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioRepository inventarioRepository;

    @BeforeEach
    public void setUp() {
        Inventario inventarioSimulado = new Inventario();
        inventarioSimulado.setId(1L);
        inventarioSimulado.setCantidad(20);
        inventarioSimulado.setTipoMovimiento("ENTRADA");

        // Simulamos registro
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioSimulado));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioSimulado);
    }

    @Test
    public void registrarMovimiento_ConPermiso_DeberiaGuardar() throws Exception {
        String movimientoJson = "{\"id\":1, \"productoId\":1, \"cantidad\":20, \"tipoMovimiento\":\"ENTRADA\"}";

        mockMvc.perform(put("/api/inventario/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movimientoJson))
                .andExpect(status().isOk());
    }

    @Test
    public void registrarMovimiento_SinAutenticacion_DeberiaExigirLogin() throws Exception {
        String movimientoJson = "{\"id\":1, \"productoId\":1, \"cantidad\":20, \"tipoMovimiento\":\"ENTRADA\"}";

        SecurityContextHolder.clearContext();

        mockMvc.perform(put("/api/inventario/1")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.createEmptyContext()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movimientoJson))
                .andExpect(status().is3xxRedirection()); // Valida la redirección
    }
}