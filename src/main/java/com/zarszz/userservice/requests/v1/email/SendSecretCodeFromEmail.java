package com.zarszz.userservice.requests.v1.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSecretCodeFromEmail {
    private String recipientName;
    private String recipientEmail;
}
