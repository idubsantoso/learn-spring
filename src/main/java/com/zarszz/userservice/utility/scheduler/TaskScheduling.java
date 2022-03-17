package com.zarszz.userservice.utility.scheduler;

import com.google.gson.Gson;
import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.JobPurpose;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import com.zarszz.userservice.persistence.repository.PaymentRepository;
import com.zarszz.userservice.utility.rabbitmq.RabbitMqSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;

@Component
@Slf4j
public class TaskScheduling {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    RabbitMqSender rabbitMqSender;

    @Autowired
    Gson gson;

    @Scheduled(cron = "${spring.payment.scheduling}")
    @Transactional
    public void checkValidPaymentScheduling() {
        log.info(">>>>>>>>>>>> {} | check and send valid payment notification task begin", TaskScheduling.class.getSimpleName());

        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -3);

        Calendar end = Calendar.getInstance();

        var payments = paymentRepository.findAllByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                PaymentStatus.PENDING, start.getTime(), end.getTime()
        );

        for (var payment: payments) {
            if (payment.getStatus().equals(PaymentStatus.PENDING)) {
                // Send email notification to pay
                var message = new Message();
                var identity = new HashMap<>();

                identity.put("id", payment.getId().toString());
                message.setPurpose(JobPurpose.SEND_DO_PAYMENT_EMAIL);
                message.setMessage(gson.toJson(identity));

                rabbitMqSender.send(message);
            }
        }

        log.info("<<<<<<<<<<<< {} | check and send valid payment notification task end", TaskScheduling.class.getSimpleName());
    }

    @Scheduled(cron = "${spring.payment.scheduling}")
    @Transactional
    public void checkAndSetExpiredPayment() {
        log.info(">>>>>>>>>>>> {} | check and set expired payment notification task begin", TaskScheduling.class.getSimpleName());
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -30);

        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_MONTH, -4);

        var payments = paymentRepository.findAllByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                PaymentStatus.PENDING, start.getTime(), end.getTime()
        );

        int countForceExpiredPayment = 0;

        for (var payment: payments) {
            if (payment.getStatus().equals(PaymentStatus.PENDING)) {
                // Send email notification to inform payment is expired
                var message = new Message();
                var identity = new HashMap<>();

                identity.put("id", payment.getId().toString());
                message.setPurpose(JobPurpose.SEND_INFORM_PAYMENT_EXPIRED);
                message.setMessage(gson.toJson(identity));

                rabbitMqSender.send(message);
                countForceExpiredPayment += 1;
            }
        }

        log.info("<<<<<<<<<<<< {} | forcing set {} payments to expired", TaskScheduling.class.getSimpleName(), countForceExpiredPayment);
        log.info("<<<<<<<<<<<< {} | check and set expired payment notification task end", TaskScheduling.class.getSimpleName());
    }
}
