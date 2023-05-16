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
import java.util.Calendar;

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


  public String validateServer()
   throws Exception {
    String validLicenseStatus = "03/2025";
/*
    if (!(validateServerKey()))
      throw new Exception("Office Wizard invalid server key detected.");
*/
//--------------------------------------------------------------------------------
// validateLicenseKey will return the following codes:
//     Invalid : indicates an invalid license key.
//     Expired : indicates an expired license key.
//     mm/yyyy : indicates an up to date license key.
//--------------------------------------------------------------------------------
/*
    validLicenseStatus = validateLicenseKey();
    if (validLicenseStatus.equalsIgnoreCase("Invalid"))
      throw new Exception("Office Wizard invalid license key detected.");
    else if (validLicenseStatus.equalsIgnoreCase("Expired"))
      cat.error(this.getClass().getName() + " Expired license key.");
*/
    return validLicenseStatus;
  }


  public boolean validateServerKey()
   throws Exception {

    boolean validServerId = true;
//--------------------------------------------------------------------------------
// Get the piracy key value from the appropriate resource.
//--------------------------------------------------------------------------------
    try {
      String appCode = getPropertyService().getLicenseProperties("application.service.key").toUpperCase();
      String appKey = "";
//--------------------------------------------------------------------------------
// Extract the scrambled MAC address values out of the piracy key.
//--------------------------------------------------------------------------------
      String sepChar = getPropertyService().getLicenseProperties("application.separator.key");
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

      cat.debug(this.getClass().getName() + ": appKey = " + appKey);
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
      throw new Exception(e.getMessage());
    }

    return validServerId;
  }


  public String validateLicenseKey()
   throws Exception {

    String validLicenseStatus = "Valid";
//--------------------------------------------------------------------------------
// Get the piracy key value from the appropriate resource.
//--------------------------------------------------------------------------------
    try {
      String appService = getPropertyService().getLicenseProperties("application.service.key").toUpperCase();
      String appLicense = getPropertyService().getLicenseProperties("application.license.key").toUpperCase();

//--------------------------------------------------------------------------------
// Extract the scrambled month, year and piracy key fragment.
//--------------------------------------------------------------------------------
      int monthIndex = decodeHex(appLicense.substring(9,10));
      int yearIndex = decodeHex(appLicense.substring(10,11));
      int year2Index = decodeHex(appLicense.substring(11,12));

//--------------------------------------------------------------------------------
// Confirm the license key and server key are in synch.
//--------------------------------------------------------------------------------
      int keyIndex = decodeHex(appLicense.substring(12,13));
      String serviceSub = appService.substring(keyIndex,keyIndex+1)
                        + appService.substring(keyIndex+2,keyIndex+3)
                        + appService.substring(keyIndex+3,keyIndex+4);
      String licenseSub = appLicense.substring(15,16) + appLicense.substring(13,14) + appLicense.substring(14,15);
      cat.debug(this.getClass().getName() + " Service key index..: " + keyIndex);
      cat.debug(this.getClass().getName() + " Service sub value..: " + serviceSub);
      cat.debug(this.getClass().getName() + " License sub value..: " + licenseSub);
      if (!(licenseSub.equalsIgnoreCase(serviceSub))) {
        validLicenseStatus = "Invalid";
      }
      else {
//--------------------------------------------------------------------------------
// Decode the month and year values from the license key.
//--------------------------------------------------------------------------------
        int monthValue = decodeHex(appLicense.substring(monthIndex,monthIndex+1));
        int yearValue = 1988 + (decodeHex(appLicense.substring(yearIndex,yearIndex+1)) * 10) + decodeHex(appLicense.substring(year2Index,year2Index+1));
        Calendar rightNow = Calendar.getInstance();
        Calendar expireNow = Calendar.getInstance();
        expireNow.set(yearValue,monthValue,1);
        expireNow.add(Calendar.DATE,-1);
        if (expireNow.compareTo(rightNow) < 0)
          validLicenseStatus  = "Expired";
        else
          validLicenseStatus = monthValue + "/" + yearValue;

        cat.debug(this.getClass().getName() + " License Key.....: " + appLicense);
        cat.debug(this.getClass().getName() + " Expires after...: " + monthValue + "/" + yearValue);
        cat.debug(this.getClass().getName() + " Current date....: " + rightNow.getTime());
        cat.debug(this.getClass().getName() + " Expiration date.: " + expireNow.getTime());
      }
    }
    catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    return validLicenseStatus;
  }


  public final static int decodeHex(String hexValue) {
    int number = 0;

    if(hexValue.compareTo("C") == 0) number = 0;
    else if (hexValue.compareTo("9") == 0) number = 1;
    else if (hexValue.compareTo("D") == 0) number = 2;
    else if (hexValue.compareTo("8") == 0) number = 3;
    else if (hexValue.compareTo("B") == 0) number = 4;
    else if (hexValue.compareTo("3") == 0) number = 5;
    else if (hexValue.compareTo("A") == 0) number = 6;
    else if (hexValue.compareTo("5") == 0) number = 7;
    else if (hexValue.compareTo("F") == 0) number = 8;
    else if (hexValue.compareTo("6") == 0) number = 9;
    else if (hexValue.compareTo("E") == 0) number = 10;
    else if (hexValue.compareTo("4") == 0) number = 11;
    else if (hexValue.compareTo("0") == 0) number = 12;
    else if (hexValue.compareTo("1") == 0) number = 13;
    else if (hexValue.compareTo("2") == 0) number = 14;
    else if (hexValue.compareTo("7") == 0) number = 15;

    return number;
  }


  public final static String getMacAddress() {
    String mac = "";
    String os = System.getProperty("os.name");

    try {
      if(os.startsWith("Windows")) {
        mac = windowsParseMacAddress(windowsRunIpConfigCommand());
      } else if(os.startsWith("Linux")) {
        mac = linuxRunIfConfigCommand();
        // Debugging necessary for Fedora Core 17 upgrade on 01/18/2013
        //cat.warn(this.getClass().getName() + " linuxRunIfConfigCommand() " + mac);        
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

// see if line contains MAC address
      //cat.warn(this.getClass().getName() + " line..: " + line);
      int macAddressPosition = line.indexOf("ether ");
      //cat.warn(this.getClass().getName() + " macAddressPostion..: " + macAddressPosition);
      if(macAddressPosition < 0) continue;
      String macAddressCandidate = line.substring(macAddressPosition+6,23).trim();
      //cat.warn(this.getClass().getName() + " macAddressCandidate..: " + macAddressCandidate);
      if(linuxIsMacAddress(macAddressCandidate)) {
        lastMacAddress = macAddressCandidate;
        //cat.warn(this.getClass().getName() + "lastMacAddress..: " + lastMacAddress);
        return lastMacAddress;
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
    // Fedora 19 upgrade required ifconfig without interface name (eth0)
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
    //cat.debug(this.getClass().getName() + " linuxRunIfConfigCommand() " + outputText);

    return outputText;
  }


/*
 * Windows stuff
 */
  private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException {

    try {
      String localHost = InetAddress.getLocalHost().getHostAddress();
    } catch(java.net.UnknownHostException ex) {
      ex.printStackTrace();
      throw new ParseException(ex.getMessage(), 0);
    }

    StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
    String lastMacAddress = null;

    while(tokenizer.hasMoreTokens()) {
      String line = tokenizer.nextToken().trim();

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