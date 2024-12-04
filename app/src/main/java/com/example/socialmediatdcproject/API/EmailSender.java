package com.example.socialmediatdcproject.API;

import android.util.Log;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailSender {

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "22211tt4044@mail.tdc.edu.vn";
    private static final String SMTP_PASSWORD = "scun wnbz vtea kysm";

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(); // ExecutorService

    public static void sendEmail(String recipientEmail, String subject, String body, EmailCallback callback) {
        // Cấu hình properties cho việc gửi email
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Thiết lập phiên làm việc với email
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        // Gửi email trong một luồng nền bằng ExecutorService
        executorService.execute(() -> {
            try {
                // Tạo email
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SMTP_USER));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setText(body);

                // Gửi email
                Transport.send(message);
                callback.onSuccess();

            } catch (AuthenticationFailedException e) {
                Log.e("EmailSender", "Authentication failed", e);
                callback.onFailure(e);
            } catch (MessagingException e) {
                Log.e("EmailSender", "Email sending failed", e);
                callback.onFailure(e);
            }
        });
    }

    public interface EmailCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
