package com.goktug.controller;

import com.goktug.dto.response.BrowsingHistoryResponse;
import com.goktug.dto.response.DeleteResponse;
import com.goktug.service.BrowsingHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}")
@RequiredArgsConstructor
@Tag(name = "Browsing History")
public class BrowsingHistoryController {

    private final BrowsingHistoryService browsingHistoryService;

    @Operation(summary = "Get last 10 viewed products")
    @GetMapping("/browsing-history")
    public ResponseEntity<BrowsingHistoryResponse> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(browsingHistoryService.getHistory(userId));
    }

    @Operation(summary = "Delete a product from browsing history")
    @DeleteMapping("/browsing-history/{productId}")
    public ResponseEntity<DeleteResponse> deleteFromHistory(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(browsingHistoryService.deleteFromHistory(userId, productId));
    }

}