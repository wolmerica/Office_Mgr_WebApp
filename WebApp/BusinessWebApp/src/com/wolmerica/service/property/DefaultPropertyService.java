package com.wolmerica.service.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 29, 2010
 */

public class DefaultPropertyService implements PropertyService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getCustomerProperties(request, propertyKey)
// 02/28/2007 - Add the instance name to provide support multiple instances.
//--------------------------------------------------------------------------------
  public String getCustomerProperties(HttpServletRequest request, String key)
    throws IOException {

    String strFileName = "/" + request.getSession().getAttribute("INSTANCENAME")
                       + "-CustomerResources.properties";
    cat.debug("File Name.....: " + strFileName);
    Properties props = new Properties();
    URL url = this.getClass().getResource(strFileName);
    FileInputStream pin = new FileInputStream(url.getFile());

    try
    {
      props.load(pin);
    }
    catch (IOException ie)
    {
      cat.error("NO CUSTOMER PROPERTY FOR : " + key);
    }

    String val = props.getProperty(key);

    // Check to see if the customer property exists in the file.
    if (val == null)
    {
      val = key;
      cat.debug("RETURN KEY : " + key);
    }
    pin.close();
    return(val);
  }

  public String getFeatureProperties(String key)
    throws IOException {

    String strFileName = "/FeatureResources.properties";
    cat.debug("FILE NAME : " + strFileName);
    Properties props = new Properties();
    URL url = this.getClass().getResource(strFileName);
    FileInputStream pin = new FileInputStream(url.getFile());

    try
    {
      props.load(pin);
    }
    catch (IOException ie)
    {
      cat.error("NO LICENSE PROPERTY FOR : " + key);
    }

    String val = props.getProperty(key);

    // Check to see if the feature property exists in the file.
    if (val == null)
    {
      val = key;
      cat.debug("RETURN KEY : " + key);
    }
    pin.close();
    return(val);
  }

  public String getLicenseProperties(String key)
    throws IOException {

    String strFileName = "/LicenseResources.properties";
    cat.debug("FILE NAME : " + strFileName);
    Properties props = new Properties();
    URL url = this.getClass().getResource(strFileName);
    FileInputStream pin = new FileInputStream(url.getFile());

    try
    {
      props.load(pin);
    }
    catch (IOException ie)
    {
      cat.error("NO LICENSE PROPERTY FOR : " + key);
    }

    String val = props.getProperty(key);

    // Check to see if the license property exists in the file.
    if (val == null)
    {
      val = key;
      cat.debug("RETURN KEY : " + key);
    }
    pin.close();
    return(val);
  }

}