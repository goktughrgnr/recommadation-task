package com.goktug.repository;

import com.goktug.models.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, String> {

    boolean existsByMessageId(String messageId);

    @Query("SELECT pv FROM ProductView pv WHERE pv.userId = :userId AND pv.isDeleted = false ORDER BY pv.viewDate DESC LIMIT 10")
    List<ProductView> findRecentViews(@Param("userId") String userId);

    @Query("SELECT pv FROM ProductView pv WHERE pv.userId = :userId AND pv.productId = :productId AND pv.isDeleted = false")
    Optional<ProductView> findActiveView(@Param("userId") String userId, @Param("productId") String productId);

    boolean existsByUserIdAndIsDeletedFalse(String userId);

    // Kullanıcının son 20 view'ından en çok baktığı 3 kategoriyi çek
    @Query("SELECT p.categoryId FROM ProductView pv " +
           "JOIN Product p ON pv.productId = p.productId " +
           "WHERE pv.userId = :userId AND pv.isDeleted = false AND p.isDeleted = false " +
           "GROUP BY p.categoryId " +
           "ORDER BY COUNT(p.categoryId) DESC " +
           "LIMIT 3")
    List<String> findTopCategories(@Param("userId") String userId);

    // Kullanıcının en çok baktığı 3 kategoriyi bulmak için
    @Query("SELECT p.categoryId FROM ProductView pv JOIN Product p ON pv.productId = p.productId " +
           "WHERE pv.userId = :userId AND pv.isDeleted = false AND p.isDeleted = false " +
           "GROUP BY p.categoryId ORDER BY COUNT(p.categoryId) DESC LIMIT 3")
    List<String> findTopCategoriesByUserId(@Param("userId") String userId);
}