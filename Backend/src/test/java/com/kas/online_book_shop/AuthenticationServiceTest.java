package com.kas.online_book_shop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.dto.RegisterRequest;
import com.kas.online_book_shop.enums.Role;
import com.kas.online_book_shop.model.User;
import com.kas.online_book_shop.repository.UserRepository;
import com.kas.online_book_shop.service.AuthenticationService;
import com.kas.online_book_shop.service.JwtService;
import com.kas.online_book_shop.service.email.EmailService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthenticationServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationService authenticationService;

    @Test
    public void testRegister() throws MessagingException {
        RegisterRequest registerRequest = new RegisterRequest(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "Province",
                "District",
                "Ward",
                "1234567890",
                "123 Main St",
                Role.USER);

        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());

        when(jwtService.generateToken(any(User.class))).thenReturn("mocked_token");

        doNothing().when(emailService).sendActivationEmail(anyString(), anyString(), anyString());
        authenticationService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendActivationEmail(eq(registerRequest.email()), eq(registerRequest.fullName()),
                eq("mocked_token"));
    }

}
