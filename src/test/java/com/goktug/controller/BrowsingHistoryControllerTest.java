package com.goktug.controller;

import com.goktug.dto.response.BrowsingHistoryResponse;
import com.goktug.dto.response.DeleteResponse;
import com.goktug.service.BrowsingHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BrowsingHistoryController.class)
class BrowsingHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrowsingHistoryService browsingHistoryService;

    // SENARYO 1: Kullanıcının geçmişi varsa doğru ürünleri döndürür mü?
    @Test
    void getHistory_returnsProductsForUser() throws Exception {
        // ARRANGE: Servis bu kullanıcı için 2 ürün döndürsün
        BrowsingHistoryResponse response = new BrowsingHistoryResponse(
                120L, List.of("product-101", "product-102"), "personalized");
        when(browsingHistoryService.getHistory(120L)).thenReturn(response);

        // ACT + ASSERT
        mockMvc.perform(get("/api/v1/users/120/browsing-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(120))
                .andExpect(jsonPath("$.type").value("personalized"))
                .andExpect(jsonPath("$.products[0]").value("product-101"))
                .andExpect(jsonPath("$.products[1]").value("product-102"));
    }

    // SENARYO 2: Kayıt yoksa servis RuntimeException fırlatır → 404 dönmeli
    @Test
    void deleteFromHistory_whenNotFound_returns404() throws Exception {
        // ARRANGE: Servis "kayıt yok" diyerek exception fırlatsın
        when(browsingHistoryService.deleteFromHistory(120L, 999L))
                .thenThrow(new RuntimeException("Record not found"));

        // ACT + ASSERT
        mockMvc.perform(delete("/api/v1/users/120/browsing-history/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Record not found"));
    }
}
