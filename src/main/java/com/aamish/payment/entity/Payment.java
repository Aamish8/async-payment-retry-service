package com.aamish.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Payment {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
   private long id;
   private double amount;
   @Column(unique=true)
   private String orderId;
   private int retryCount;
   @Enumerated(EnumType.STRING)
   private PaymentStatus status;
   private LocalDateTime scheduledAt;
}
