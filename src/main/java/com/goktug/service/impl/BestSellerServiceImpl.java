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

    private final ProductViewRepository productViewRepository;
    private final GeneralBestSellerRepository generalBestSellerRepository;
    private final CategoryBestSellerRepository categoryBestSellerRepository;

    @Override
    public BestSellerResponse getBestSellers(Long userId) {
        String formattedUserId = IdFormatter.formatUserId(userId);
        List<String> topCategories = productViewRepository.findTopCategoriesByUserId(formattedUserId);

        if (topCategories.isEmpty()) {
            return getGeneralBestSellers(userId);
        }

        return getPersonalizedBestSellers(userId, topCategories);
    }

    private BestSellerResponse getPersonalizedBestSellers(Long userId, List<String> topCategories) {
        List<CategoryBestSeller> bestSellers = categoryBestSellerRepository
                .findByCategoriesOrderByRank(topCategories);

        if (bestSellers.size() < 5) {
            return getGeneralBestSellers(userId);
        }

        List<String> products = bestSellers.stream()
                .limit(10)
                .map(CategoryBestSeller::getProductId)
                .toList();

        return new BestSellerResponse(userId, products, "personalized");
    }

    private BestSellerResponse getGeneralBestSellers(Long userId) {
        List<GeneralBestSeller> bestSellers = generalBestSellerRepository.findAllByOrderByRankAsc();

        List<String> products = bestSellers.size() < 5
                ? Collections.emptyList()
                : bestSellers.stream().limit(10).map(GeneralBestSeller::getProductId).toList();

        return new BestSellerResponse(userId, products, "non-personalized");
    }
}
