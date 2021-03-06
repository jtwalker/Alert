package edu.westga.justinwalker.alert.mailer;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.westga.justinwalker.alert.models.SharedConstants;

/**
 * Sets up a mailer session, creates a message, and sends the email through an async task.
 *
 * Author: Justin Walker
 */
public class Mailer {

    /**
     * Send an email using the given parameters.
     *
     * @param email the address to send the email to
     * @param subject the subject of the email
     * @param messageBody the message of the email
     */
    public void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up access to the Gmail SMTP server. Authenticate using a gmail account.
     */
    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SharedConstants.ALERT_USERNAME, SharedConstants.ALERT_PASSWORD);
            }
        });
    }

    /**
     * Creates an e-mail using the given parameters.
     * @param email
     * @param subject
     * @param messageBody
     * @param session
     * @return
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SharedConstants.ALERT_USERNAME, SharedConstants.ALERT_UPDATE));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    /**
     * Since emails cannot be sent on the UI thread, this makes an async task to send the email.
     */
    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = ProgressDialog.show(Mailer.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

