package com.example.ChatApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    JavaMailSender jms;

    public String sendOtpEmail(String toemail, String otp) {
        SimpleMailMessage sms = new SimpleMailMessage();
        sms.setTo(toemail);
        sms.setSubject("Email Verification OTP");
        sms.setText("Your OTP for verification is: " + otp);
        jms.send(sms);
        return "OTP sent successfully to " + toemail;
    }
}
