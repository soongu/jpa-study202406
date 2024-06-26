package com.spring.jpastudy.chap05_manytomany.repository;

import com.spring.jpastudy.chap05_manytomany.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
}
