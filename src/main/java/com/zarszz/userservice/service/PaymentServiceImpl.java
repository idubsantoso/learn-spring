package com.zarszz.userservice.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import com.midtrans.service.MidtransSnapApi;
import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.domain.enumData.OrderStatus;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import com.zarszz.userservice.kernel.exception.AlreadyCreatedException;
import com.zarszz.userservice.kernel.exception.PaymentErrorException;
import com.zarszz.userservice.repository.PaymentRepository;
import com.zarszz.userservice.security.entity.AuthenticatedUser;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    MidtransSnapApi midtransSnapApi;

    @Autowired
    MidtransCoreApi midtransCoreApi;

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public Payment create(Long orderId) throws AlreadyCreatedException, MidtransError {
        if (paymentRepository.findByOrderId(orderId).isPresent())
            throw new AlreadyCreatedException("Order already created");
        var order = orderService.getById(orderId);
        var payment = new Payment();

        payment.setUser(authenticatedUser.getUser());
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTotal(order.getSubTotal());
        var paymentCode = "TRX" + RandomStringUtils.randomAlphanumeric(16).toUpperCase();
        payment.setPaymentCode(paymentCode);

        Map<String, Object> params = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", paymentCode);
        transactionDetails.put("gross_amount", payment.getTotal().toString());

        params.put("transaction_details", transactionDetails);

        var redirectUrl = midtransSnapApi.createTransactionRedirectUrl(params);

        payment.setRedirectUrl(redirectUrl);

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> get() {
        var isAdmin = authenticatedUser.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        return isAdmin ? paymentRepository.findAll() : paymentRepository.findByUserId(authenticatedUser.getUserId());
    }

    @Override
    public Payment getById(Long id) throws NoSuchElementException {
        return paymentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Payment not found"));
    }

    @Override
    public void update(Order order, Long id) throws NoSuchElementException {
        var payment = paymentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Payment not found"));
        payment.setTotal(order.getSubTotal());
        paymentRepository.save(payment);

    }

    @Override
    public void delete(Long id) throws NoSuchElementException {
        if (!paymentRepository.existsById(id))
            throw new NoSuchElementException("Payment not found");
        paymentRepository.deleteById(id);

    }

    @Override
    public void cancel(Long id) throws NoSuchElementException {
        var payment = paymentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Payment not found"));
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.getOrder().setStatus(OrderStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    @Override
    public void proceed(Map<String, Object> response) throws NoSuchElementException, MidtransError {
        System.out.println(response);
        if (!(response.isEmpty())) {
            //Get Order ID from notification body
            var orderId = response.containsKey("order_id") ? (String) response.get("order_id") : "";

            var payment = paymentRepository.findByPaymentCode(orderId).orElseThrow(() -> new NoSuchElementException("Payment not found"));
            if (payment.getStatus().equals(PaymentStatus.EXPIRED))
                throw new PaymentErrorException("Your payment is expired, please create someone new");
            if (payment.getStatus().equals(PaymentStatus.COMPLETED))
                throw new PaymentErrorException("Your payment is completed");

            // Get status transaction to api with order id
            var transactionResult = midtransCoreApi.checkTransaction(orderId);
            System.out.println(transactionResult);
            var transactionStatus = transactionResult.has("transaction_status") ? (String) transactionResult.get("transaction_status") : "";
            var fraudStatus = transactionResult.has("fraud_status") ? (String) transactionResult.get("fraud_status") : "";

            var notificationResponse = "Transaction notification received. Order ID: " + orderId + ". Transaction status: " + transactionStatus + ". Fraud status: " + fraudStatus;
            System.out.println(notificationResponse);

            switch (transactionStatus) {
                case "capture":
                    if (fraudStatus.equals("challenge")) {
                        payment.setStatus(PaymentStatus.PENDING);
                        throw new PaymentErrorException("Your payment is challenged");
                    } else if (fraudStatus.equals("accept")) {
                        payment.setStatus(PaymentStatus.COMPLETED);
                    }
                    break;
                case "cancel":
                case "deny":
                case "expire":
                    payment.setStatus(PaymentStatus.FAILED);
                    throw new PaymentErrorException("Your payment is failed");
                case "pending":
                    payment.setStatus(PaymentStatus.PENDING);
                    throw new PaymentErrorException("Your payment is pending");
            }
            paymentRepository.save(payment);
        }
    }
}
