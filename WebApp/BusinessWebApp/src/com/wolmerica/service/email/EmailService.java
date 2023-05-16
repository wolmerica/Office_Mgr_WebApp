package com.wolmerica.service.email;

import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */
public interface EmailService {

  public HashMap<String, Object> postMail(HashMap<String, Object> sendToMap,
                          String recipients[ ],
                          String subject,
                          String message,
                          String from,
                          String smtpHost,
                          String smtpPort,
                          String smtpUser,
                          String smtpPassword)
  throws Exception;



}
