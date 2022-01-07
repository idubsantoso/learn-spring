package com.zarszz.userservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${onesignal.app_key}")
    String OneSignalAppKey;

    @Value("${onesignal.url}")
    String OneSignalUrl;

    public String sendNotificationRestTemplate(NotificationMessageDto message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + OneSignalAppKey);

        HttpEntity<NotificationMessageDto> entity = new HttpEntity<NotificationMessageDto>(message, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(OneSignalUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.BAD_REQUEST)
            return "";
        return response.getBody();
    }

}