package com.wolmerica.service.log;

import java.io.IOException;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */
public interface LogService {

  public String getContents(String logFileName)
  throws IOException;

}
