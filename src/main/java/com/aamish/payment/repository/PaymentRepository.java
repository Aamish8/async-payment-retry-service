package com.aamish.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aamish.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

	Optional<Payment> findByOrderId(String orderId);

}
