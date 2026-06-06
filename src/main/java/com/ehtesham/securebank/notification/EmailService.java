package com.ehtesham.securebank.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("SecureBank - Password Reset OTP");
        message.setText(
                "Your OTP for password reset is: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "SecureBank Security Team"
        );
        mailSender.send(message);
    }
}