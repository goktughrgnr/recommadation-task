package com.goktug.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "general_best_sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralBestSeller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "buyer_count")
    private Integer buyerCount;

    @Column(name = "rank")
    private Integer rank;
}
