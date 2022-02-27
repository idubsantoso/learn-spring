package com.zarszz.userservice.persistence.service;


import java.util.*;

import com.google.gson.Gson;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.domain.enumData.OrderStatus;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import com.zarszz.userservice.requests.v1.order.CreatePaymentDto;
import com.zarszz.userservice.utility.rabbitmq.RabbitMqSender;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.JobPurpose;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import com.zarszz.userservice.kernel.exception.AlreadyCreatedException;
import com.zarszz.userservice.kernel.exception.PaymentErrorException;
import com.zarszz.userservice.persistence.repository.PaymentRepository;
import com.zarszz.userservice.security.entity.AuthenticatedUser;

import com.zarszz.userservice.utility.rabbitmq.dto.SendTransactionStatusEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Autowired
    MidtransCoreApi midtransCoreApi;

    @Autowired
    RabbitMqSender rabbitMqSender;

    @Autowired
    Gson gson;

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public void create(Long orderId, CreatePaymentDto paymentDto) throws AlreadyCreatedException, MidtransError {
        if (paymentRepository.findByOrderId(orderId).isPresent())
            throw new AlreadyCreatedException("Order already created");
        var order = orderService.getById(orderId);
        var payment = new Payment();

        payment.setUser(authenticatedUser.getUser());
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTotal(order.getSubTotal());
        payment.setMethod(paymentDto.getPaymentMethod());
        var createdPayment = paymentRepository.save(payment);
        var message = new Message();
        var identity = new HashMap<>();
        identity.put("id", createdPayment.getId().toString());
        message.setPurpose(JobPurpose.CREATE_MIDTRANS_PAYMENT);
        message.setMessage(gson.toJson(identity));
        rabbitMqSender.send(message);
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
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
    public void proceed(Map<String, Object> response) throws NoSuchElementException, MidtransError, MessagingException {
        log.info(response.toString());
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
            log.info(transactionResult.toString());
            var transactionStatus = transactionResult.has("transaction_status") ? (String) transactionResult.get("transaction_status") : "";
            var fraudStatus = transactionResult.has("fraud_status") ? (String) transactionResult.get("fraud_status") : "";

            var notificationResponse = "Transaction notification received. Order ID: " + orderId + ". Transaction status: " + transactionStatus + ". Fraud status: " + fraudStatus;
            log.info(notificationResponse);

            var sendTransactionEmailMessage = new SendTransactionStatusEmail();
            sendTransactionEmailMessage.setPaymentId(payment.getId());
            sendTransactionEmailMessage.setRecipientEmail("ngaco@email.com");

            switch (transactionStatus) {
                case "capture":
                case "":
                case "settlement":
                    if (fraudStatus.equals("challenge")) {
                        payment.setStatus(PaymentStatus.PENDING);
                        sendTransactionEmailMessage.setState("PENDING");
                    } else if (fraudStatus.equals("accept")) {
                        sendTransactionEmailMessage.setState("BERHASIL");
                        payment.setStatus(PaymentStatus.COMPLETED);
                    }
                    break;
                case "cancel":
                case "deny":
                case "expire":
                    payment.setStatus(PaymentStatus.FAILED);
                    sendTransactionEmailMessage.setState("GAGAL");
                case "pending":
                    payment.setStatus(PaymentStatus.PENDING);
                    sendTransactionEmailMessage.setState("PENDING");
            }

            var message = new Message();
            message.setMessage(gson.toJson(sendTransactionEmailMessage));
            message.setPurpose(JobPurpose.SEND_TRANSACTION_STATUS_EMAIL);
            rabbitMqSender.send(message);
            payment.setPaymentDate(new Date());
            paymentRepository.save(payment);
        }
    }
}
