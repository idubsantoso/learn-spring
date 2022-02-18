package com.zarszz.userservice.controller;

import com.google.gson.Gson;
import com.midtrans.httpclient.error.MidtransError;
import com.zarszz.userservice.domain.User;
import com.zarszz.userservice.kernel.configs.rabbitmq.RabbitMqSender;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.JobPurpose;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;
import com.zarszz.userservice.service.NotificationService;

import com.zarszz.userservice.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@CrossOrigin
@Controller
public class NotificationController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    RabbitMqSender rabbitMqSender;

    @Autowired
    Gson gson;

    @PostMapping(path = "api/notifications/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendNotification(@RequestBody NotificationMessageDto notificationMessage) {
        var message = gson.toJson(notificationMessage);
        var rabbitMessage = new Message();
        rabbitMessage.setPurpose(JobPurpose.SEND_NOTIFICATION);
        rabbitMessage.setMessage(message);
        rabbitMqSender.send(rabbitMessage);
        return ResponseEntity.ok("ok");
    }

    @PostMapping(path = "api/notifications/midtrans/payments/proceed", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> handleNotification(@RequestBody Map<String, Object> response) throws Exception {
        paymentService.proceed(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "api/notifications/rabbitmq", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendRabbitNotification(@RequestBody User user)  {
        var userMessage = gson.toJson(user);
        var message = new Message();
        message.setPurpose(JobPurpose.SEND_NOTIFICATION);
        message.setMessage(userMessage);

        rabbitMqSender.send(message);
        return ResponseEntity.ok("ok");
    }
}
