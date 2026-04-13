package com.goktug.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_best_sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBestSeller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "buyer_count")
    private Integer buyerCount;

    @Column(name = "rank")
    private Integer rank;
}
