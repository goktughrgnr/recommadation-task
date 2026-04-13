package com.goktug.service;

import com.goktug.dto.response.BrowsingHistoryResponse;
import com.goktug.dto.response.DeleteResponse;

public interface BrowsingHistoryService {
    BrowsingHistoryResponse getHistory(Long userId);
    DeleteResponse deleteFromHistory(Long userId, Long productId);
}