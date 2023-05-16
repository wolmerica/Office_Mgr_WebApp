package com.wolmerica.service.userstate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author  Richard Wolschlager
 * @date    Sept 4, 2010
 */
public interface UserStateService {

  public String getAcctKey();

  public String getAcctNum();

  public String getAcctName();

  public String getMultiAcct();

  public String getDefaultInstanceName();

  public String getInstanceName();

  public void SessionAttributeCleanUp(HttpServletRequest request)
  throws Exception;

  public void SessionInstanceValidate(HttpServletRequest request)
  throws Exception;
}
