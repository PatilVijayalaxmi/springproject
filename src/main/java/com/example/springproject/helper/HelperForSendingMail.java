package com.example.springproject.helper;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.springproject.dto.MyUSer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class HelperForSendingMail {
@Autowired
JavaMailSender mailsender;//interface

public void sendEmail(MyUSer myUSer){
MimeMessage mimeMessage=mailsender.createMimeMessage();
MimeMessageHelper helper=new MimeMessageHelper(mimeMessage);
try{
helper.setFrom("vijayalaxmipatil7505@gmail.com","Student management");
helper.setTo(myUSer.getEmail());
helper.setSubject("OTP verification");
helper.setText("Hello, "+myUSer.getName()+"Your OTP is :" +myUSer.getOtp());
}
catch(MessagingException | UnsupportedEncodingException e){
e.printStackTrace();
}
mailsender.send(mimeMessage);
}
}
