package com.goktug.service.impl;

import com.goktug.service.BatchService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchServiceImpl implements BatchService {

    // Native SQL sorgularini calistirmak icin kullanilir
    private final EntityManager entityManager;

    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000) // 1 saatte bir
    // ETL: once eski tabloyu temizler, sonra yeni best seller verisini doldurur
    public void runBestSellerETL() {
        log.info("ETL started at {}", LocalDateTime.now());

        // Her calismada sifirdan dogru siralama uretmek icin tablolari temizle
        entityManager.createNativeQuery("TRUNCATE general_best_sellers RESTART IDENTITY").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE category_best_sellers RESTART IDENTITY").executeUpdate();

        // Genel best seller: tum kullanicilarda en cok farkli aliciya ulasan ilk 10
        // urun
        entityManager.createNativeQuery("""
                    INSERT INTO general_best_sellers (product_id, buyer_count, rank)
                    SELECT product_id, buyer_count, rank FROM (
                        SELECT oi.product_id,
                               COUNT(DISTINCT o.user_id) AS buyer_count,
                               ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT o.user_id) DESC) AS rank
                        FROM orders o
                        JOIN order_items oi ON o.order_id = oi.order_id
                        GROUP BY oi.product_id
                    ) ranked WHERE rank <= 10
                """).executeUpdate();

        // Kategori bazli best seller: her kategori icin en cok aliciya ulasan ilk 10
        // urun
        entityManager
                .createNativeQuery(
                        """
                                    INSERT INTO category_best_sellers (product_id, category_id, buyer_count, rank)
                                    SELECT product_id, category_id, buyer_count, rank FROM (
                                        SELECT oi.product_id,
                                               p.category_id,
                                               COUNT(DISTINCT o.user_id) AS buyer_count,
                                               ROW_NUMBER() OVER (PARTITION BY p.category_id ORDER BY COUNT(DISTINCT o.user_id) DESC) AS rank
                                        FROM orders o
                                        JOIN order_items oi ON o.order_id = oi.order_id
                                        JOIN products p ON oi.product_id = p.product_id
                                        GROUP BY oi.product_id, p.category_id
                                    ) ranked WHERE rank <= 10
                                """)
                .executeUpdate();

        log.info("ETL completed at {}", LocalDateTime.now());
    }
}