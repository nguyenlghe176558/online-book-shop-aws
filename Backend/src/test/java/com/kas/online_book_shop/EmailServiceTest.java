package com.kas.online_book_shop;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.kas.online_book_shop.service.email.EmailService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;
    @Autowired
    private JavaMailSender javaMailSender;

    private GreenMail testSmtp;

    @BeforeEach
    public void setUp() {
        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.start();
        ReflectionTestUtils.setField(emailService, "javaMailSender", javaMailSender);
    }

    @AfterEach
    public void tearDown() {
        testSmtp.stop();
    }

    @Test
    public void testSendActivationEmail()
            throws MessagingException, IOException, jakarta.mail.MessagingException, InterruptedException {
        String to = "test@example.com";
        String fullName = "John Doe";
        String token = "testToken";

        emailService.sendActivationEmail(to, fullName, token);

        //TimeUnit.SECONDS.sleep(2);
        Thread.sleep(2000);

        MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];
        assertEquals("Kích hoạt tài khoản SachTrucTuyen", receivedMessage.getSubject());
        assertEquals("sachtructuyen123@gmail.com", receivedMessage.getFrom()[0].toString());
        assertEquals(to, receivedMessage.getAllRecipients()[0].toString());

        String content = GreenMailUtil.getBody(receivedMessage);
        assertTrue(content.contains("Kích hoạt tài khoản"));
        assertTrue(content.contains("https://sachtructuyen.shop/activation/testToken"));
        // Add more assertions as needed to validate the email content.
    }

    @Test
    public void testSendResetPasswordEmail()
            throws MessagingException, IOException, jakarta.mail.MessagingException, InterruptedException {
        String to = "test@example.com";
        String fullName = "John Doe";
        String token = "testToken";

        emailService.sendResetPasswordEmail(to, fullName, token);
        TimeUnit.SECONDS.sleep(2);

        MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];
        assertEquals("Đặt lại mật khẩu SachTrucTuyen", receivedMessage.getSubject());
        assertEquals("sachtructuyen123@gmail.com", receivedMessage.getFrom()[0].toString());
        assertEquals(to, receivedMessage.getAllRecipients()[0].toString());

        String content = GreenMailUtil.getBody(receivedMessage);
        assertTrue(content.contains("Đặt lại mật khẩu"));
        assertTrue(content.contains("https://sachtructuyen.shop/reset-password/testToken"));
        // Add more assertions as needed to validate the email content.
    }
}