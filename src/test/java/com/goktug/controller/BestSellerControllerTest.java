package com.goktug.controller;

import com.goktug.dto.response.BestSellerResponse;
import com.goktug.service.BestSellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BestSellerController.class)
class BestSellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BestSellerService bestSellerService;

    // SENARYO 3: Kullanıcıya özel yeterli veri varsa "personalized" döner
    // Yeterli kategori verisi yoksa sistem genel listeye düşer → "non-personalized" döner
    @Test
    void getBestSellers_whenNoPersonalData_returnsNonPersonalized() throws Exception {
        // ARRANGE: Servis bu kullanıcı için genel listeyi döndürsün (fallback)
        BestSellerResponse response = new BestSellerResponse(
                120L, List.of("gen1", "gen2", "gen3", "gen4", "gen5"), "non-personalized");
        when(bestSellerService.getBestSellers(120L)).thenReturn(response);

        // ACT + ASSERT
        mockMvc.perform(get("/api/v1/users/120/best-sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(120))
                .andExpect(jsonPath("$.type").value("non-personalized"))
                .andExpect(jsonPath("$.products.length()").value(5));
    }
}
