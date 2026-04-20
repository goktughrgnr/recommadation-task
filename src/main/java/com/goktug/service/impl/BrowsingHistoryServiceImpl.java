package com.goktug.service.impl;

import com.goktug.dto.response.BrowsingHistoryResponse;
import com.goktug.dto.response.DeleteResponse;
import com.goktug.exception.NotFoundException;
import com.goktug.models.ProductView;
import com.goktug.repository.ProductViewRepository;
import com.goktug.service.BrowsingHistoryService;
import com.goktug.util.IdFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrowsingHistoryServiceImpl implements BrowsingHistoryService {

    private final ProductViewRepository productViewRepository;
    private final IdFormatter idFormatter;

    @Override
    public BrowsingHistoryResponse getHistory(Long userId) {
        String formattedUserId = idFormatter.formatUserId(userId);
        List<ProductView> views = productViewRepository.findRecentViews(formattedUserId);

        List<String> products = views.stream().map(ProductView::getProductId).toList();
        return new BrowsingHistoryResponse(userId, products, "personalized");
    }

    @Override
    public DeleteResponse deleteFromHistory(Long userId, Long productId) {
        String formattedUserId = idFormatter.formatUserId(userId);
        String formattedProductId = idFormatter.formatProductId(productId);

        ProductView view = productViewRepository.findActiveView(formattedUserId, formattedProductId)
                .orElseThrow(() -> new NotFoundException("Record not found"));

        // soft-delete: fiziksel silmek yerine isDeleted=true
        view.setIsDeleted(true);
        productViewRepository.save(view);

        return new DeleteResponse("Product removed from browsing history", userId, productId);
    }
}
