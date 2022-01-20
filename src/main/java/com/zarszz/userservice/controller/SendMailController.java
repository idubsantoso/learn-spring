package com.zarszz.userservice.controller;

import com.zarszz.userservice.requests.v1.email.SendSecretCodeFromEmail;
import com.zarszz.userservice.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/email/secret-code")
public class SendMailController {

    @Autowired
    EmailSenderService emailSenderService;

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody SendSecretCodeFromEmail sendMailHtmlDto) throws Exception {
        emailSenderService.sendMailWithInline(
                sendMailHtmlDto.getRecipientName(), sendMailHtmlDto.getRecipientEmail());
        return ResponseEntity.ok("Email Send Success !!");

    }

}
