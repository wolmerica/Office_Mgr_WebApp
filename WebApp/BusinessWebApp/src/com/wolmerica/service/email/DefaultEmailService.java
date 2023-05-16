package com.wolmerica.service.email;

import java.util.HashMap;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */

public class DefaultEmailService implements EmailService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// postMail using javax.mail
//--------------------------------------------------------------------------------
  public HashMap<String, Object> postMail(HashMap<String, Object> sendToMap,
                          String recipients[ ],
                          String subject,
                          String message,
                          String from,
                          String smtpHost,
                          String smtpPort,
                          String smtpUser,
                          String smtpPassword)
   throws Exception {

    boolean debug = false;

    try {
      cat.debug("JavaMailKit.postMail() : " + subject);
//--------------------------------------------------------------------------------
//Set the host smtp address
//--------------------------------------------------------------------------------
      Properties props = new Properties();
      props.put("mail.smtp.host", smtpHost);
      props.put("mail.smtp.auth", "true");
      props.put("mail.debug", "false");
      props.put("mail.smtp.port", smtpPort);
      props.put("mail.smtp.socketFactory.port", smtpPort);
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.socketFactory.fallback", "false");
//--------------------------------------------------------------------------------
// create some properties and get the default Session
//--------------------------------------------------------------------------------
      Authenticator auth = new SmtpAuthenticator(smtpUser, smtpPassword);
      Session session = Session.getDefaultInstance(props, auth);
      session.setDebug(debug);
      cat.debug("JavaMailKit.postMail() : session set");
//--------------------------------------------------------------------------------
// create a message
//--------------------------------------------------------------------------------
      javax.mail.Message msg = new MimeMessage(session);
//--------------------------------------------------------------------------------
// set the from and to address
//--------------------------------------------------------------------------------
      InternetAddress addressFrom = new InternetAddress(from);
      msg.setFrom(addressFrom);

      cat.debug("JavaMailKit.postMail() recipients : " + recipients.length);
      InternetAddress[] addressTo = new InternetAddress[recipients.length];
      for (int i = 0; i < recipients.length; i++)
      {
        addressTo[i] = new InternetAddress(recipients[i]);
        sendToMap = countSendTo(recipients[i], sendToMap);
        cat.debug("JavaMailKit.postMail() addressTo : " + addressTo[i]);
      }
      msg.setRecipients(Message.RecipientType.TO, addressTo);

//--------------------------------------------------------------------------------
// Optional : You can also set your custom headers in the Email if you Want
//--------------------------------------------------------------------------------
      msg.addHeader("MyHeaderName", "myHeaderValue");
//--------------------------------------------------------------------------------
// Setting the Subject and Content Type
//--------------------------------------------------------------------------------
      msg.setSubject(subject);
      msg.setContent(message, "text/plain");
      Transport.send(msg);
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": postMail() throw Exception: " + e.getMessage());
      throw new Exception(e.getMessage());
    }

    return sendToMap;
  }


  private HashMap<String, Object> countSendTo(String sendTo,
                              HashMap<String, Object> sendToMap)
   throws Exception {

    int was = 0;
    Object gotIt;

    try {
      if ((gotIt = sendToMap.get(sendTo)) != null)
        was = (((Integer)gotIt).intValue());

      sendToMap.put(sendTo, Integer.valueOf(was+1));
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": countSendTo() throw Exception: " + e.getMessage());
      throw new Exception(e.getMessage());
    }
    return sendToMap;
  }

  public class SmtpAuthenticator extends javax.mail.Authenticator {

    String pass = "";
    String login = "";

    public SmtpAuthenticator() {
      super();
    }

    public SmtpAuthenticator(String login, String pass) {
      super();

      this.login = login;
      this.pass = pass;
    }

        @Override
    public PasswordAuthentication getPasswordAuthentication() {
      if (pass.equals(""))
        return null;
      else
        return new PasswordAuthentication(login, pass);
    }
  }

}