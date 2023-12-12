package com.giggagit.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giggagit.demo.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
