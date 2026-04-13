package com.goktug.repository;

import com.goktug.models.GeneralBestSeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralBestSellerRepository extends JpaRepository<GeneralBestSeller, Long> {
    List<GeneralBestSeller> findAllByOrderByRankAsc();
}