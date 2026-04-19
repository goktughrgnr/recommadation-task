package com.goktug.controller;

import com.goktug.dto.response.BestSellerResponse;
import com.goktug.service.BestSellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}")
@RequiredArgsConstructor
@Tag(name = "Best Sellers")
public class BestSellerController {

    private final BestSellerService bestSellerService;

    @Operation(summary = "Get personalized or general best sellers")
    @GetMapping("/best-sellers")
    public ResponseEntity<BestSellerResponse> getBestSellers(@PathVariable Long userId) {
        return ResponseEntity.ok(bestSellerService.getBestSellers(userId));
    }
}