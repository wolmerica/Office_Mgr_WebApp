package com.zoasis.service.zoasislab;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

//for BaseWLSSLAdapter
import weblogic.webservice.tools.wsdlp.WSDLParseException;
import zoasis.ZoasisServicesPort;

// Web Services Stuff
import zoasis.ws.datamodel.general.LoginObject;
import zoasis.ws.datamodel.laborders.LabOrderChartId;
/**
 * @author  Richard Wolschlager
 * @date    September 26, 2010
 */
public interface ZoasisLabService {

  public LoginObject getLoginObject(HttpServletRequest request)
  throws Exception;

  public ZoasisServicesPort getInitializedZoasisPort(HttpServletRequest request)
  throws Exception, WSDLParseException;


  public Boolean isAntechLabOrder(HttpServletRequest request,
                                  Connection conn,
                                  Integer poKey)
  throws Exception;

  public Boolean isTrackResultOrder(Connection conn,
                                    Integer poKey)
  throws Exception;

  public String createAntechLabOrder(HttpServletRequest request,
                                     Connection conn,
                                     Integer poKey)
  throws Exception;

  public LabOrderChartId loadAntechLabOrderChartId(HttpServletRequest request,
                                                   Connection conn,
                                                   Integer poKey)
  throws Exception, SQLException;

  public String getAntechRequisitionId(HttpServletRequest request,
                                       Connection conn,
                                       Integer poKey,
                                       Byte attributeToEntityKey,
                                       Integer attributeToKey)
  throws Exception;

  public String getAntech2dTextString(HttpServletRequest request,
                                      Connection conn,
                                      String labRequisitionId,
                                      Integer poKey,
                                      Byte attributeToEntityKey,
                                      Integer attributeToKey)
  throws Exception, SQLException;

  public HashMap<String, Object> validateAntechLabOrderCode(HttpServletRequest request,
                                            Connection conn,
                                            Integer vendorKey,
                                            String serviceNum)
  throws Exception, SQLException;

  public String exceptionFilter(HttpServletRequest request,
                                String exceptionMsg)
  throws Exception;


}
