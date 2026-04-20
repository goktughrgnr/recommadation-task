package com.goktug.service;

import com.goktug.dto.ProductViewEvent;

public interface ProductViewService {
    void processViewEvent(ProductViewEvent event);
    boolean isDuplicate(String messageId);
}
