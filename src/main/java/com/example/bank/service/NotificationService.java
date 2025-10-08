package com.example.bank.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class NotificationService {

    private final String fromEmail = "remainderbotagent@gmail.com";  
    private final String password = "wqhg ibsd azfz zjno";          


    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
    }


    private void sendEmail(String toEmail, String subject, String body) {
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(fromEmail, "MyBank"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendDepositNotification(String name, String accountNumber, String email, String amount) {
        String subject = "Deposit Successful - Account " + accountNumber;
        String body = String.format(
                "Dear %s,\n\nYour account %s has been credited with %s.\n\nThank you for banking with us!\n\n- MyBank",
                name, accountNumber, amount
        );
        sendEmail(email, subject, body);
    }


    public void sendWithdrawalNotification(String name, String accountNumber, String email, String amount) {
        String subject = "Withdrawal Successful - Account " + accountNumber;
        String body = String.format(
                "Dear %s,\n\nYour account %s has been debited with %s.\n\nThank you for banking with us!\n\n- MyBank",
                name, accountNumber, amount
        );
        sendEmail(email, subject, body);
    }

    public void sendTransferNotification(
            String senderName, String senderAcc, String senderEmail,
            String receiverName, String receiverAcc, String receiverEmail,
            String amount
    ) {
    
        String senderSubject = "Amount Transferred - Account " + senderAcc;
        String senderBody = String.format(
                "Dear %s,\n\nYou have successfully transferred %s to account %s.\n\nThank you for banking with us!\n\n- MyBank",
                senderName, amount, receiverAcc
        );
        sendEmail(senderEmail, senderSubject, senderBody);

       
        String receiverSubject = "Amount Received - Account " + receiverAcc;
        String receiverBody = String.format(
                "Dear %s,\n\nYour account %s has been credited with %s from account %s.\n\nThank you for banking with us!\n\n- MyBank",
                receiverName, receiverAcc, amount, senderAcc
        );
        sendEmail(receiverEmail, receiverSubject, receiverBody);
    }
}
