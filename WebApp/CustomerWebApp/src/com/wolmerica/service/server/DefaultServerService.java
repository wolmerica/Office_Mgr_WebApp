package com.wolmerica.service.server;

import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */

public class DefaultServerService implements ServerService {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }


  public boolean validateServer()
   throws Exception {

    boolean validServerId = true;
//--------------------------------------------------------------------------------
// Get the piracy key value from the appropriate resource.
//--------------------------------------------------------------------------------
    try {
      String appCode = getPropertyService().getFeatureProperties("application.service.key").toUpperCase();
      String appKey = new String("");
//--------------------------------------------------------------------------------
// Extract the scrambled MAC address values out of the piracy key.
//--------------------------------------------------------------------------------
      String sepChar = getPropertyService().getFeatureProperties("application.separator.key");
      appKey = appCode.substring(12,13);
      appKey = appKey + appCode.substring(29,30);
      appKey = appKey + sepChar;
      appKey = appKey + appCode.substring(8,9);
      appKey = appKey + appCode.substring(31,32);
      appKey = appKey + sepChar;
      appKey = appKey + appCode.substring(17,18);
      appKey = appKey + appCode.substring(3,4);
      appKey = appKey + sepChar;
      appKey = appKey + appCode.substring(26,27);
      appKey = appKey + appCode.substring(19,20);
      appKey = appKey + sepChar;
      appKey = appKey + appCode.substring(5,6);
      appKey = appKey + appCode.substring(11,12);
      appKey = appKey + sepChar;
      appKey = appKey + appCode.substring(34,35);
      appKey = appKey + appCode.substring(21,22);

//      cat.debug(this.getClass().getName() + ": appKey = " + appKey);
//--------------------------------------------------------------------------------
// Map each digit back to it's original value.
//--------------------------------------------------------------------------------
      appCode = "";
      int i = 0;
      for (i=0; i<appKey.length(); i++) {
	if(appKey.substring(i,i+1).compareTo("A") == 0) appCode = appCode + "0";
	else if(appKey.substring(i,i+1).compareTo("9") == 0) appCode = appCode + "1";
	else if(appKey.substring(i,i+1).compareTo("B") == 0) appCode = appCode + "2";
	else if(appKey.substring(i,i+1).compareTo("1") == 0) appCode = appCode + "3";
	else if(appKey.substring(i,i+1).compareTo("C") == 0) appCode = appCode + "4";
	else if(appKey.substring(i,i+1).compareTo("8") == 0) appCode = appCode + "5";
	else if(appKey.substring(i,i+1).compareTo("D") == 0) appCode = appCode + "6";
	else if(appKey.substring(i,i+1).compareTo("3") == 0) appCode = appCode + "7";
	else if(appKey.substring(i,i+1).compareTo("E") == 0) appCode = appCode + "8";
	else if(appKey.substring(i,i+1).compareTo("4") == 0) appCode = appCode + "9";
	else if(appKey.substring(i,i+1).compareTo("0") == 0) appCode = appCode + "A";
	else if(appKey.substring(i,i+1).compareTo("6") == 0) appCode = appCode + "B";
	else if(appKey.substring(i,i+1).compareTo("F") == 0) appCode = appCode + "C";
	else if(appKey.substring(i,i+1).compareTo("5") == 0) appCode = appCode + "D";
	else if(appKey.substring(i,i+1).compareTo("7") == 0) appCode = appCode + "E";
	else if(appKey.substring(i,i+1).compareTo("2") == 0) appCode = appCode + "F";
	else appCode = appCode + appKey.substring(i,i+1);
      }

      try {
        cat.debug(this.getClass().getName() + " Network info");
        cat.debug(this.getClass().getName() + " Operating System.: " + System.getProperty("os.name"));
        cat.debug(this.getClass().getName() + " IP/Localhost.....: " + InetAddress.getLocalHost().getHostAddress());
        cat.debug(this.getClass().getName() + " MAC Address......: " + getMacAddress());
        cat.debug(this.getClass().getName() + " Server Code......: " + appCode);
        validServerId = (appCode.compareToIgnoreCase(getMacAddress()) == 0);
      } catch(Throwable t) {
        t.printStackTrace();
      }
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
    }

    return validServerId;
  }

  public final static String getMacAddress() {
    String mac = "";
    String os = System.getProperty("os.name");

    try {
      if(os.startsWith("Windows")) {
        mac = windowsParseMacAddress(windowsRunIpConfigCommand());
      } else if(os.startsWith("Linux")) {
        mac = linuxParseMacAddress(linuxRunIfConfigCommand());
      } else if(os.startsWith("Mac OS X")) {
        mac = osxParseMacAddress(osxRunIfConfigCommand());
      }
    } catch(Exception ex) {
        ex.printStackTrace();
    }
    return mac;
  }


/*
 * Linux stuff
 */
  private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException {
    String localHost = null;
    try {
      localHost = InetAddress.getLocalHost().getHostAddress();
    } catch(java.net.UnknownHostException ex) {
      ex.printStackTrace();
      throw new ParseException(ex.getMessage(), 0);
    }

    StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
    String lastMacAddress = null;

    while(tokenizer.hasMoreTokens()) {
      String line = tokenizer.nextToken().trim();
      boolean containsLocalHost = line.indexOf(localHost) >= 0;

// see if line contains IP address
      if(containsLocalHost && lastMacAddress != null) {
        return lastMacAddress;
      }

// see if line contains MAC address
      int macAddressPosition = line.indexOf("HWaddr");
      if(macAddressPosition <= 0) continue;
      String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
      if(linuxIsMacAddress(macAddressCandidate)) {
        lastMacAddress = macAddressCandidate;
        continue;
      }
    }

    ParseException ex = new ParseException
    ("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
    ex.printStackTrace();
    throw ex;
  }


  private final static boolean linuxIsMacAddress(String macAddressCandidate) {
    Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
    Matcher m = macPattern.matcher(macAddressCandidate);
    return m.matches();
  }


  private final static String linuxRunIfConfigCommand() throws IOException {
    Process p = Runtime.getRuntime().exec("ifconfig");
    InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

    StringBuffer buffer= new StringBuffer();
    for (;;) {
      int c = stdoutStream.read();
      if (c == -1) break;
      buffer.append((char)c);
    }
    String outputText = buffer.toString();

    stdoutStream.close();

    return outputText;
  }

/*
 * Windows stuff
 */
  private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException {
    String localHost = null;
    try {
      localHost = InetAddress.getLocalHost().getHostAddress();
    } catch(java.net.UnknownHostException ex) {
      ex.printStackTrace();
      throw new ParseException(ex.getMessage(), 0);
    }

    StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
    String lastMacAddress = null;

    while(tokenizer.hasMoreTokens()) {
      String line = tokenizer.nextToken().trim();

// see if line contains IP address
//      if(line.endsWith(localHost) && lastMacAddress != null) {
      if (lastMacAddress != null) {
        return lastMacAddress;
      }

// see if line contains MAC address
      int macAddressPosition = line.indexOf(":");
      if(macAddressPosition <= 0) continue;

      String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
      if(windowsIsMacAddress(macAddressCandidate)) {
        lastMacAddress = macAddressCandidate;

        return lastMacAddress;
//        continue;
      }
    }

    ParseException ex = new ParseException("cannot read MAC address from [" + ipConfigResponse + "]", 0);
//    ex.printStackTrace();
    throw ex;
  }

  private final static boolean windowsIsMacAddress(String macAddressCandidate) {
    Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
    Matcher m = macPattern.matcher(macAddressCandidate);
    return m.matches();
  }

  private final static String windowsRunIpConfigCommand() throws IOException {
    Process p = Runtime.getRuntime().exec("ipconfig /all");
    InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

    StringBuffer buffer= new StringBuffer();
    for (;;) {
      int c = stdoutStream.read();
      if (c == -1) break;
      buffer.append((char)c);
    }
    String outputText = buffer.toString();

    stdoutStream.close();

    return outputText;
  }

/*
 * Mac OS X Stuff
 */
  private final static String osxParseMacAddress(String ipConfigResponse) throws ParseException {
    String localHost = null;

    try {
      localHost = InetAddress.getLocalHost().getHostAddress();
    } catch(java.net.UnknownHostException ex) {
      ex.printStackTrace();
      throw new ParseException(ex.getMessage(), 0);
    }

    StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");

    while(tokenizer.hasMoreTokens()) {
      String line = tokenizer.nextToken().trim();
      boolean containsLocalHost = line.indexOf(localHost) >= 0;
// see if line contains MAC address
      int macAddressPosition = line.indexOf("ether");
      if(macAddressPosition != 0) continue;
      String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
      if(osxIsMacAddress(macAddressCandidate)) {
        return macAddressCandidate;
      }
    }

    ParseException ex = new ParseException
      ("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
    ex.printStackTrace();
    throw ex;
  }

  private final static boolean osxIsMacAddress(String macAddressCandidate) {
    Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
    Matcher m = macPattern.matcher(macAddressCandidate);
    return m.matches();
  }

  private final static String osxRunIfConfigCommand() throws IOException {
    Process p = Runtime.getRuntime().exec("ifconfig");
    InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
    StringBuffer buffer= new StringBuffer();
    for (;;) {
      int c = stdoutStream.read();
      if (c == -1) break;
      buffer.append((char)c);
    }
    String outputText = buffer.toString();
    stdoutStream.close();
    return outputText;
  }
  
}