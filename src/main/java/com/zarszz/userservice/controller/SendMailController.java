package com.zarszz.userservice.controller;

import com.google.gson.Gson;
import com.zarszz.userservice.utility.rabbitmq.RabbitMqSender;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.JobPurpose;
import com.zarszz.userservice.kernel.configs.rabbitmq.dto.Message;
import com.zarszz.userservice.requests.v1.email.SendSecretCodeFromEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/email/secret-code")
public class SendMailController {
    @Autowired
    Gson gson;

    @Autowired
    RabbitMqSender rabbitMqSender;

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody SendSecretCodeFromEmail sendMailHtmlDto) throws Exception {
        var message = gson.toJson(sendMailHtmlDto);
        var rabbitMessage = new Message();
        rabbitMessage.setPurpose(JobPurpose.SEND_EMAIL);
        rabbitMessage.setMessage(message);
        rabbitMqSender.send(rabbitMessage);
        return ResponseEntity.ok("Email Send Success !!");

    }

}
