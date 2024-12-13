package com.example.socialmediatdcproject.captcha;

import android.os.AsyncTask;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_ADDRESS = "22211tt4044@mail.tdc.edu.vn"; // Thay bằng địa chỉ email của bạn
    private static final String EMAIL_PASSWORD = "22211tt4044@mail.tdc.edu.vn";  // Thay bằng mật khẩu của email

    public static void sendVerificationCode(String recipientEmail) {
        String code = generateVerificationCode();
        String subject = "Mã xác thực đăng ký";
        String message = "Mã xác thực của bạn là: " + code;

        new SendEmailTask(recipientEmail, subject, message).execute();
    }

    // Tạo mã xác thực 6 chữ số ngẫu nhiên
    private static String generateVerificationCode() {
        int min = 100000;
        int max = 999999;
        int randomCode = (int) (Math.random() * (max - min + 1)) + min;
        return String.valueOf(randomCode);
    }

    // AsyncTask để gửi email trên một luồng khác ngoài UI thread
    private static class SendEmailTask extends AsyncTask<Void, Void, Void> {

        private String recipientEmail;
        private String subject;
        private String message;

        public SendEmailTask(String recipientEmail, String subject, String message) {
            this.recipientEmail = recipientEmail;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Cấu hình các thông số gửi email
                Properties properties = new Properties();
                properties.put("mail.smtp.host", SMTP_SERVER);
                properties.put("mail.smtp.port", SMTP_PORT);
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");

                // Thiết lập xác thực và gửi email
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
                    }
                });

                // Tạo và gửi email
                Message emailMessage = new MimeMessage(session);
                emailMessage.setFrom(new InternetAddress(EMAIL_ADDRESS));
                emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                emailMessage.setSubject(subject);
                emailMessage.setText(message);

                Transport.send(emailMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
