package com.example.recipesapp;

import com.example.recipesapp.controller.UnitController;
import com.example.recipesapp.entity.Unit;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.UnitService;
import com.example.recipesapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UnitController.class)
@Import(TestSecurityConfig.class)
public class UnitControllerTest {
    @MockBean
    private UnitService unitService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetAllUnits() throws Exception {
        when(unitService.findAllUnits()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/unit/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testAddUnit() throws Exception {
        Unit unit = new Unit();
        unit.setId(1);
        unit.setUnit("Kilogram");

        when(unitService.saveUnit(any(Unit.class))).thenReturn(unit);

        mockMvc.perform(post("/api/unit/new")
                        .contentType("application/json")
                        .content("{\"unit\":\"Kilogram\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.unit").value("Kilogram"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetUnit() throws Exception {
        Unit unit = new Unit();
        unit.setId(1);
        unit.setUnit("Kilogram");

        when(unitService.getUnitWithId(1)).thenReturn(unit);

        mockMvc.perform(get("/api/unit/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.unit").value("Kilogram"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testDeleteUnitNotAdmin() throws Exception {
        Unit unit = new Unit();
        unit.setId(1);
        unit.setUnit("Kilogram");

        when(unitService.getUnitWithId(1)).thenReturn(unit);

        mockMvc.perform(delete("/api/unit/1"))
                .andExpect(status().isForbidden());

        verify(unitService, times(0)).deleteUnit(unit);
    }

}
