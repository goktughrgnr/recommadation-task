package com.goktug.repository;

import com.goktug.models.CategoryBestSeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryBestSellerRepository extends JpaRepository<CategoryBestSeller, Long> {

    @Query("SELECT cb FROM CategoryBestSeller cb WHERE cb.categoryId IN :categories ORDER BY cb.rank ASC, cb.categoryId ASC")
    List<CategoryBestSeller> findByCategoriesOrderByRank(@Param("categories") List<String> categories);
}