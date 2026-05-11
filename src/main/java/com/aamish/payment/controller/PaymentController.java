package com.aamish.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aamish.payment.entity.Payment;
import com.aamish.payment.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
     @Autowired
     private PaymentService service;
     @PostMapping
     public Payment create(@RequestBody Payment payment) throws InterruptedException {
    	 return service.createPayment(payment);
     }
     @GetMapping("/{id}")
     public Payment get(@PathVariable long id) {
    	 return service.getPayment(id);
     }
}
