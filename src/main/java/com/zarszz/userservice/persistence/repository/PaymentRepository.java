package com.zarszz.userservice.persistence.repository;

import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);
    Optional<Payment> findByPaymentCode(String paymentCode);
    List<Payment> findAllByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(PaymentStatus paymentStatus, Date start, Date end);
}
