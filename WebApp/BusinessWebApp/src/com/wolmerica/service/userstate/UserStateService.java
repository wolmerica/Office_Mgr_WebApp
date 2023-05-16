package com.wolmerica.service.userstate;

import com.wolmerica.permission.PermissionListDO;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author  Richard Wolschlager
 * @date    Sept 4, 2010
 */
public interface UserStateService {

  public Integer getNoKey();

  public String getLogin();

  public String getUserName();

  public String getAdmin();

  public String getUser();

  public String getToken();

  public String getLicense();

  public String getExpired();

  public String getDefaultInstanceName();

  public String getInstanceName();

  public String getPermission();

  public String getLocked();

  public String getReadOnly();

  public String getProhibited();

  public String getDisableEdit();

  public String getAccessDenied();

  public Integer getFeatureKey();

  public void setFeatureKey(Integer featureKey);

  public Integer createUserToken(Connection conn,
                                 String userName,
                                 String userIpAddress)
  throws Exception, SQLException;

  public String getUserToken(HttpServletRequest request,
                             Connection conn,
                             String fAction,
                             Integer fKey)
  throws Exception, SQLException;

  public PermissionListDO getUserListToken(HttpServletRequest request,
                                           Connection conn,
                                           String fAction,
                                           Integer fKey)
  throws Exception, SQLException;

  public void releaseExpiredUserToken(Connection conn)
  throws Exception, SQLException;

  public void deleteUserToken(Connection conn,
                              Integer usKey)
  throws Exception, SQLException;

  public void sessionAttributeCleanUp(HttpServletRequest request)
  throws Exception;

  public void sessionInstanceValidate(HttpServletRequest request)
  throws Exception;

}
