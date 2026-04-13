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

    // Product view kayitlarini okumak/guncellemek icin
    private final ProductViewRepository productViewRepository;

    @Override
    // Kullanicinin son gezdigi urunleri dondurur
    public BrowsingHistoryResponse getHistory(Long userId) {
        String formattedUserId = IdFormatter.formatUserId(userId);
        List<ProductView> views = productViewRepository.findRecentViews(formattedUserId);

        // Yeterli veri yoksa bos liste, varsa urun id listesi dondur
        List<String> products = views.size() < 5
                ? Collections.emptyList()
                : views.stream().map(ProductView::getProductId).toList();

        return new BrowsingHistoryResponse(userId, products, "personalized");
    }

    @Override
    // Gecmisten urun silme: kaydi fiziksel silmek yerine soft-delete yapar
    public DeleteResponse deleteFromHistory(Long userId, Long productId) {
        String formattedUserId = IdFormatter.formatUserId(userId);
        String formattedProductId = IdFormatter.formatProductId(productId);

        // Aktif kayit bulunamazsa hata firlat
        ProductView view = productViewRepository.findActiveView(formattedUserId, formattedProductId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // Soft delete: kaydi silmek yerine isDeleted=true yap
        view.setIsDeleted(true);
        productViewRepository.save(view);

        return new DeleteResponse("Product removed from browsing history", userId, productId);
    }
}