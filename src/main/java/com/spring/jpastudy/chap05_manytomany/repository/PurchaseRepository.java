package com.spring.jpastudy.chap05_manytomany.repository;

import com.spring.jpastudy.chap05_manytomany.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {


}
