package com.zarszz.userservice.controller;

import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;
import com.zarszz.userservice.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @PostMapping(path = "api/notifications/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendNotification(@RequestBody NotificationMessageDto notificationMessage) {
        String result = this.notificationService.sendNotificationRestTemplate(notificationMessage);
        if (result.equals(""))
            return ResponseEntity.badRequest().body("Operation Failed");
        return ResponseEntity.ok(result);
    }
}
