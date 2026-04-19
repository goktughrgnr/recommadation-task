package com.goktug.service.impl;

import com.goktug.dto.response.BrowsingHistoryResponse;
import com.goktug.dto.response.DeleteResponse;
import com.goktug.models.ProductView;
import com.goktug.repository.ProductViewRepository;
import com.goktug.service.BrowsingHistoryService;
import com.goktug.util.IdFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrowsingHistoryServiceImpl implements BrowsingHistoryService {

    private final ProductViewRepository productViewRepository;

    @Override
    public BrowsingHistoryResponse getHistory(Long userId) {
        String formattedUserId = IdFormatter.formatUserId(userId);
        List<ProductView> views = productViewRepository.findRecentViews(formattedUserId);

        List<String> products = views.size() < 5
                ? Collections.emptyList()
                : views.stream().map(ProductView::getProductId).toList();

        return new BrowsingHistoryResponse(userId, products, "personalized");
    }

    @Override
    public DeleteResponse deleteFromHistory(Long userId, Long productId) {
        String formattedUserId = IdFormatter.formatUserId(userId);
        String formattedProductId = IdFormatter.formatProductId(productId);

        ProductView view = productViewRepository.findActiveView(formattedUserId, formattedProductId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // soft-delete: fiziksel silmek yerine isDeleted=true
        view.setIsDeleted(true);
        productViewRepository.save(view);

        return new DeleteResponse("Product removed from browsing history", userId, productId);
    }
}
