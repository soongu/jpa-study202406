package com.spring.jpastudy.chap05_manytomany.repository;

import com.spring.jpastudy.chap05_manytomany.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
