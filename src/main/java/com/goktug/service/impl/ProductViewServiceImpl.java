package com.goktug.service.impl;

import com.goktug.dto.ProductViewEvent;
import com.goktug.models.ProductView;
import com.goktug.repository.ProductViewRepository;
import com.goktug.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductViewServiceImpl implements ProductViewService {

    private final ProductViewRepository productViewRepository;

    @Override
    public boolean isDuplicate(String messageId) {
        return productViewRepository.existsByMessageId(messageId);
    }

    @Override
    public void processViewEvent(ProductViewEvent event) {
        ProductView productView = new ProductView();
        productView.setEvent(event.getEvent());
        productView.setMessageId(event.getMessageid());
        productView.setUserId(event.getUserid());
        productView.setProductId(event.getProperties().getProductid());
        productView.setSource(event.getContext().getSource());
        productView.setViewDate(LocalDateTime.now());
        productView.setIsDeleted(false);

        productViewRepository.save(productView);
    }
}
