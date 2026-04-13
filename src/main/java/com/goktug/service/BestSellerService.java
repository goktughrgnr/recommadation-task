package com.goktug.service;

import com.goktug.dto.response.BestSellerResponse;

public interface BestSellerService {
    BestSellerResponse getBestSellers(Long userId);
}
