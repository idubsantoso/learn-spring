package com.zarszz.userservice.controller;

import com.midtrans.httpclient.error.MidtransError;
import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;
import com.zarszz.userservice.service.NotificationService;

import com.zarszz.userservice.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    NotificationService notificationService;

    @Autowired
    PaymentService paymentService;

    @PostMapping(path = "api/notifications/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendNotification(@RequestBody NotificationMessageDto notificationMessage) {
        String result = this.notificationService.sendNotificationRestTemplate(notificationMessage);
        if (result.equals(""))
            return ResponseEntity.badRequest().body("Operation Failed");
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "api/notifications/midtrans/payments/proceed", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> handleNotification(@RequestBody Map<String, Object> response) throws Exception {
        paymentService.proceed(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
