package com.goktug.util;

import org.springframework.stereotype.Component;

@Component
public class IdFormatter {

    public String formatUserId(Long userId) {
        return "user-" + userId;
    }

    public String formatProductId(Long productId) {
        return "product-" + productId;
    }
}
