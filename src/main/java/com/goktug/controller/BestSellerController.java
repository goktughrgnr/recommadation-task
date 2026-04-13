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

    // Best seller verisini servis katmanindan alir
    private final BestSellerService bestSellerService;

    @Operation(summary = "Get personalized or general best sellers")
    @GetMapping("/best-sellers")
    // Kullaniciya ozel veya genel cok satanlari dondurur
    public ResponseEntity<BestSellerResponse> getBestSellers(@PathVariable Long userId) {
        return ResponseEntity.ok(bestSellerService.getBestSellers(userId));
    }
}

/**
 * @RequiredArgsConstructor: Lombok kütüphanesinden gelir.
 *                           'private final' olarak tanımladığın tüm değişkenler
 *                           (BestSellerService gibi) için
 *                           otomatik olarak bir Constructor (yapıcı metod)
 *                           oluşturur.
 *                           Spring bu sayede "BestSellerService" nesnesini
 *                           buraya enjekte eder (Dependency Injection).
 */

/**
 * @Operation: Swagger dokümantasyonu için bu işlemin ne yaptığını açıklar.
 */