package com.zarszz.userservice.service;

import com.zarszz.userservice.requests.v1.message.NotificationMessageDto;

public interface NotificationService {
    public String sendNotificationRestTemplate(NotificationMessageDto message);
}
