package com.goktug.service.impl;

import com.goktug.dto.response.BestSellerResponse;
import com.goktug.models.CategoryBestSeller;
import com.goktug.models.GeneralBestSeller;
import com.goktug.repository.CategoryBestSellerRepository;
import com.goktug.repository.GeneralBestSellerRepository;
import com.goktug.repository.ProductViewRepository;
import com.goktug.service.BestSellerService;
import com.goktug.util.IdFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BestSellerServiceImpl implements BestSellerService {

    // Kullanici goruntuleme verisinden ilgi kategorilerini bulmak icin
    private final ProductViewRepository productViewRepository;
    // Genel best seller listesini okumak icin
    private final GeneralBestSellerRepository generalBestSellerRepository;
    // Kategori bazli best seller listesini okumak icin
    private final CategoryBestSellerRepository categoryBestSellerRepository;

    @Override
    // Ana akis: once kisisellestirilmis dene, veri yetersizse genele dus
    public BestSellerResponse getBestSellers(Long userId) {
        String formattedUserId = IdFormatter.formatUserId(userId);
        List<String> topCategories = productViewRepository.findTopCategoriesByUserId(formattedUserId);

        // Kullanici icin kategori sinyali yoksa direkt genel listeyi dondur
        if (topCategories.isEmpty()) {
            return getGeneralBestSellers(userId);
        }

        return getPersonalizedBestSellers(userId, topCategories);
    }

    // Kullanicinin en cok ilgilendigi kategorilerden urun onerisi uretir
    private BestSellerResponse getPersonalizedBestSellers(Long userId, List<String> topCategories) {
        List<CategoryBestSeller> bestSellers = categoryBestSellerRepository
                .findByCategoriesOrderByRank(topCategories);

        // Sonuc cok azsa kaliteyi korumak icin genel listeye geri don
        if (bestSellers.size() < 5) {
            return getGeneralBestSellers(userId);
        }

        // Ilk 10 urunu productId listesine cevir
        List<String> products = bestSellers.stream()
                .limit(10)
                .map(CategoryBestSeller::getProductId)
                .toList();

        return new BestSellerResponse(userId, products, "personalized");
    }

    // Tum kullanicilar icin ortak best seller listesini uretir
    private BestSellerResponse getGeneralBestSellers(Long userId) {
        List<GeneralBestSeller> bestSellers = generalBestSellerRepository.findAllByOrderByRankAsc();

        // Veri 5'ten azsa bos don, aksi halde ilk 10 urunu ver
        List<String> products = bestSellers.size() < 5
                ? Collections.emptyList()
                : bestSellers.stream().limit(10).map(GeneralBestSeller::getProductId).toList();

        return new BestSellerResponse(userId, products, "non-personalized");
    }
}