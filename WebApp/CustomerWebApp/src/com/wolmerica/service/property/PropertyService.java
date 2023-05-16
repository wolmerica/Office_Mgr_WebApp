package com.wolmerica.service.property;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author  Richard Wolschlager
 * @date    August 29, 2010
 */
public interface PropertyService {

  public String getCustomerProperties(HttpServletRequest request,
                                      String key)
  throws IOException;

  public String getFeatureProperties(String key)
  throws IOException;

  public String getLicenseProperties(String key)
  throws IOException;
}
