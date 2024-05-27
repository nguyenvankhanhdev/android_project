package com.example.test.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private String mEmail;
    private String mSubject;
    private String mMessage;

    // Constructor
    public JavaMailAPI(Context context, String email, String subject, String message) {
        mContext = context;
        mEmail = email;
        mSubject = subject;
        mMessage = message;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Gmail thông tin xác thực
        final String username = "vuongtran975@gmail.com";
        final String password = "flco mtdx nwhc mcyy";

        // Cài đặt thông tin máy chủ và thuộc tính
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Tạo phiên gửi email
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress(username));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            mm.setSubject(mSubject);

            // Tùy chỉnh nội dung email ở đây
            String htmlMessage = "<html><body>" +
                    "<h1 style=\"color: #4CAF50;\">Confirmation of Order</h1>" +
                    "<p>Thank you for placing your order. Your order has been confirmed.</p>" +
                    "<p>Below is a beautiful image:</p>" +
                    "<img src=\"https://cf.shopee.vn/file/79da351e31db9b37635b22fe3e668569\" alt=\"\">" +
                    "<p>And here is some long text to make the email look more interesting. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla.</p>" +
                    "</body></html>";
            mm.setContent(htmlMessage, "text/html; charset=utf-8");

            Transport.send(mm);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Hiển thị thông báo sau khi gửi email thành công
        Toast.makeText(mContext, "Email sent successfully.", Toast.LENGTH_SHORT).show();
    }
}
