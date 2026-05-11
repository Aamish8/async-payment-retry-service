package com.aamish.payment.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aamish.payment.entity.Payment;
import com.aamish.payment.entity.PaymentStatus;
import com.aamish.payment.repository.PaymentRepository;

@Service
public class PaymentService {
      @Autowired
      private PaymentRepository repo;
      @Autowired
      private RedisTemplate <String,Long>template;
      private static final int MAX_RETRY=2;
	public Payment createPayment(Payment pay) throws InterruptedException {
		Optional<Payment> p=repo.findByOrderId(pay.getOrderId());
		if(p.isPresent()) {
			System.out.print("Duplicate Payment Detected");
			return p.get();
		}
		Payment payment=new Payment();
		payment.setOrderId(pay.getOrderId());
		payment.setAmount(pay.getAmount());
		payment.setStatus(PaymentStatus.PENDING);
		payment.setRetryCount(0);
		payment.setScheduledAt(LocalDateTime.now().plusSeconds(5));
	     Payment save=repo.save(payment);
	     
	     template.opsForList().rightPush("PaymentQueue",save.getId());
	     return save;
	}
	public Payment processPayment(Payment payment) {
		boolean success=Math.random()>0.9;
		if(success)payment.setStatus(PaymentStatus.SUCCESS);
		else {
			if(payment.getRetryCount()<MAX_RETRY) {
				payment.setRetryCount(payment.getRetryCount()+1);
				int delay=(int)Math.pow(2,payment.getRetryCount());
				payment.setScheduledAt(LocalDateTime.now().plusSeconds(delay));
				payment.setStatus(PaymentStatus.PENDING);
				template.opsForList().rightPush("PaymentQueue",payment.getId());
				System.out.print("Payment Rescheduled for : "+payment.getId());
			}
			else {
				payment.setStatus(PaymentStatus.FAILED);
				template.opsForList().rightPush("deadLetterQueue",payment.getId());
				System.out.print("Payment moved to DLQ : "+payment.getId());
			}
		}
		return repo.save(payment);
		
	}
	public Payment getPayment(long id) {
		return repo.findById(id).orElseThrow();
	}
    
}
