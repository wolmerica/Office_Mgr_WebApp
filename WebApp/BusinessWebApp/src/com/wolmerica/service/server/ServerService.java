package com.wolmerica.service.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */

public interface ServerService {

  public String validateServer()
  throws Exception;

  public boolean validateServerKey()
  throws Exception;

  public String validateLicenseKey()
  throws Exception;

}