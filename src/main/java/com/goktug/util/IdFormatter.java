package com.goktug.util;

public class IdFormatter {

    public static String formatUserId(Long userId) {
        return "user-" + userId;
    }

    public static String formatProductId(Long productId) {
        return "product-" + productId;
    }
}