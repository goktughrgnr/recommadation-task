package com.goktug.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrowsingHistoryResponse {
    private Long userId;
    private List<String> products;
    private String type;
}