package com.zarszz.userservice.utility.rabbitmq;

import com.google.gson.Gson;
import com.midtrans.service.MidtransSnapApi;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import com.zarszz.userservice.requests.v1.email.SendSecretCodeFromEmail;
import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;
import com.zarszz.userservice.persistence.service.EmailSenderService;
import com.zarszz.userservice.persistence.service.NotificationServiceImpl;
import com.zarszz.userservice.persistence.service.PaymentServiceImpl;
import com.zarszz.userservice.utility.rabbitmq.dto.SendTransactionStatusEmail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RabbitMQReceiver implements RabbitListenerConfigurer {

    @Autowired
    NotificationServiceImpl notificationService;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    PaymentServiceImpl paymentService;

    @Autowired
    MidtransSnapApi midtransSnapApi;

    @Autowired
    Gson gson;

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void receivedMessage(Message message) throws Exception {
        switch (message.getPurpose()) {
            case SEND_NOTIFICATION:
                log.info("Purpose : SEND_NOTIFICATION, received message : {}", message.getMessage());
                var notificationMessage = gson.fromJson(message.getMessage(), NotificationMessageDto.class);
                String result = this.notificationService.sendNotificationRestTemplate(notificationMessage);
                if (result.equals(""))
                    throw new Exception("Send notification failed");
                break;
            case SEND_EMAIL:
                log.info("Purpose : SEND_EMAIL, received message : {}", message.getMessage());
                var sendSecretCodeFromEmail = gson.fromJson(message.getMessage(), SendSecretCodeFromEmail.class);
                emailSenderService.sendMailWithInline(
                        sendSecretCodeFromEmail.getRecipientName(), sendSecretCodeFromEmail.getRecipientEmail());
                break;
            case SEND_TRANSACTION_STATUS_EMAIL:
                log.info("Purpose : SEND_TRANSACTION_STATUS_EMAIL, received message : {}", message.getMessage());
                var sendTransactionStatusEmail = gson.fromJson(message.getMessage(), SendTransactionStatusEmail.class);
                var paymentEmail = paymentService.getById(sendTransactionStatusEmail.getPaymentId());
                emailSenderService.sendTransactionStatusEmail(
                        sendTransactionStatusEmail.getRecipientEmail(),
                        sendTransactionStatusEmail.getState(), paymentEmail);
                break;
            case PROCESS_PAYMENT:
                log.info("Purpose : PROCESS_PAYMENT, received message : {}", message.getMessage());
                break;
            case CREATE_MIDTRANS_PAYMENT:
                log.info("Purpose : CREATE_MIDTRANS_PAYMENT, received message : {}", message.getMessage());
                var paymentCode = "TRX" + RandomStringUtils.randomAlphanumeric(16).toUpperCase();
                var paymentIdentity = gson.fromJson(message.getMessage(), HashMap.class);
                var payment = paymentService.getById(Long.valueOf((String) paymentIdentity.get("id")));
                payment.setPaymentCode(paymentCode);

                Map<String, Object> params = new HashMap<>();

                Map<String, String> transactionDetails = new HashMap<>();
                transactionDetails.put("order_id", paymentCode);
                transactionDetails.put("gross_amount", payment.getTotal().toString());

                params.put("transaction_details", transactionDetails);

                var redirectUrl = midtransSnapApi.createTransactionRedirectUrl(params);

                payment.setRedirectUrl(redirectUrl);

                paymentService.save(payment);

                emailSenderService.sendPaymentEmail(payment.getUser().getName(), "ngaco@email.com", payment);
        }
    }
}
