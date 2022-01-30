package com.zarszz.userservice.security.entity;

import com.zarszz.userservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    private String username;
    private Long userId;
    private User user;
}
