package com.aamish.payment.component;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aamish.payment.entity.Payment;
import com.aamish.payment.entity.PaymentStatus;
import com.aamish.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentWorker {
	private final RedisTemplate<String,Long>temp;
	private final PaymentService service;
     @Scheduled(fixedDelay=5000)
     public void processQueue() {
    	 Object paymentId=temp.opsForList().leftPop("PaymentQueue");
    	 if(paymentId!=null) {
    		 Payment payment=service.getPayment(Long.parseLong(paymentId.toString()));
    		 if(payment.getStatus()!=PaymentStatus.PENDING) {
    			 System.out.print("Skipping Payment "+payment.getId());
    			 return;
    		 }
    		 if(LocalDateTime.now().isBefore(payment.getScheduledAt())) {
    		     temp.opsForList().rightPush("PaymentQueue",payment.getId());
    		     return;
    		     }
    		 service.processPayment(payment);
    		 System.out.print("Processd payment : "+payment.getId());
    	 }
     }
     
}
