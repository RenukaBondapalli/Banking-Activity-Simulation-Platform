package com.example.bank.test;

import com.example.bank.service.NotificationService;
import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void testDepositEmail() {
        NotificationService service = new NotificationService();

        String name = "Renuka";
        String accountNumber = "1234567890";
        String email = "deposit@example.com"; 
        String amount = "5000";

        service.sendDepositNotification(name, accountNumber, email, amount);
    }

    @Test
    public void testWithdrawalEmail() {
        NotificationService service = new NotificationService();

        String name = "Renuka";
        String accountNumber = "1234567890";
        String email = "withdrawal@example.com"; 
        String amount = "2000";

        service.sendWithdrawalNotification(name, accountNumber, email, amount);
    }

    @Test
    public void testTransferEmail() {
        NotificationService service = new NotificationService();

        String senderName = "Renuka";
        String senderAcc = "1234567890";
        String senderEmail = "sender@example.com";

        String receiverName = "Receiver";
        String receiverAcc = "9876543210";
        String receiverEmail = "receiver@example.com";

        String amount = "3000";

        service.sendTransferNotification(senderName, senderAcc, senderEmail,
                                         receiverName, receiverAcc, receiverEmail,
                                         amount);
    }
}