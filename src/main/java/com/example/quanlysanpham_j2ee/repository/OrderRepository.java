package com.example.quanlysanpham_j2ee.repository;

import com.example.quanlysanpham_j2ee.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    List<Order> findAllByOrderByOrderDateDesc();
}
