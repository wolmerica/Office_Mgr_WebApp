package com.zoasis.service.zoasislab;

import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.purchaseorder.PurchaseOrderService;
import com.wolmerica.service.purchaseorder.DefaultPurchaseOrderService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//for BaseWLSSLAdapter
import weblogic.webservice.tools.wsdlp.WSDLParseException;
import weblogic.webservice.client.BaseWLSSLAdapter;
import zoasis.ZoasisServices_Impl;
import zoasis.ZoasisServicesPort;;

// Web Services Stuff
import zoasis.ws.datamodel.general.LoginObject;
import zoasis.ws.datamodel.laborders.LabOrderCode;
import zoasis.ws.datamodel.laborders.LabOrderResults;
import zoasis.ws.datamodel.laborders.LabOrderChartId;
import zoasis.ws.datamodel.laborders.LabOrder;
import zoasis.ws.datamodel.laborders.LabOrderItems;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    September 26, 2010
 */

public class DefaultZoasisLabService implements ZoasisLabService {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private VendorService VendorService = new DefaultVendorService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public PurchaseOrderService getPurchaseOrderService() {
      return purchaseOrderService;
  }

  public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
      this.purchaseOrderService = purchaseOrderService;
  }

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }


  public LoginObject getLoginObject(HttpServletRequest request)
   throws Exception {

    LoginObject loginObj = null;
    try {
      loginObj = new LoginObject();
      loginObj.setClinicId(new Integer(getPropertyService().getCustomerProperties(request,"webservice.antech.clinicid")));
      loginObj.setCorporateId(new Integer(getPropertyService().getCustomerProperties(request,"webservice.antech.corporateid")));
      loginObj.setPassword(getPropertyService().getCustomerProperties(request,"webservice.antech.password"));
      loginObj.setUserName(getPropertyService().getCustomerProperties(request,"webservice.antech.username"));

      cat.debug(this.getClass().getName() + ": loginObj.getClinicId()....: " + loginObj.getClinicId());
      cat.debug(this.getClass().getName() + ": loginObj.getCorporateId().: " + loginObj.getCorporateId());
      cat.debug(this.getClass().getName() + ": loginObj.getPassword()....: " + loginObj.getPassword());
      cat.debug(this.getClass().getName() + ": loginObj.getUserName()....: " + loginObj.getUserName());
    }
    catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    return loginObj;
  }

  public ZoasisServicesPort getInitializedZoasisPort(HttpServletRequest request)
   throws Exception, WSDLParseException {

    ZoasisServicesPort zsp = null;
    String wsdlURL = "";
    try {
      wsdlURL = getPropertyService().getCustomerProperties(request,"webservice.antech.wsdlurl");
      cat.debug("wsdlURL = " + wsdlURL);

      // Setup the global JAXM message factory
      System.setProperty("javax.xml.soap.MessageFactory",
                         "weblogic.webservice.core.soap.MessageFactoryImpl");
      // Setup the global JAX-RPC service factory
      System.setProperty( "javax.xml.rpc.ServiceFactory",
                          "weblogic.webservice.core.rpc.ServiceFactoryImpl");

      BaseWLSSLAdapter.setStrictCheckingDefault(false);
      cat.debug("Before antechServices_Impl() " + wsdlURL);
      ZoasisServices_Impl ws = new ZoasisServices_Impl(wsdlURL);
      cat.debug("Before getZoasisServicesPort() wsuser zoasiswsuser");
      zsp  = ws.getZoasisServicesPort("wsuser","zoasiswsuser");
    }
    catch (WSDLParseException e) {
      throw new Exception(e.toString());
    }

    return zsp;
  }


  public Boolean isAntechLabOrder(HttpServletRequest request,
                                  Connection conn,
                                  Integer poKey)
   throws Exception {
    Boolean isWebServiceEnabled = false;
    try {
      HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, poKey);
      Integer vKey = new Integer(pokMap.get("vendorKey").toString());

      // Check if the vendor is configured to use web services
      // and the lab request bar code type is configured to "CODE128".
      isWebServiceEnabled = ((getVendorService().isWebServiceSupported(conn, vKey)) &&
        (getPropertyService().getCustomerProperties(request,"po.lab.request.barcode.type").equalsIgnoreCase("CODE128")));
    }
    catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    return isWebServiceEnabled;
  }

  public Boolean isTrackResultOrder(Connection conn,
                                    Integer poKey)
   throws Exception {
    Boolean isTrackResultEnabled = false;
    try {
      HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, poKey);
      Integer vKey = new Integer(pokMap.get("vendorKey").toString());
      isTrackResultEnabled = getVendorService().isTrackResultSupported(conn, vKey);
    }
    catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    return isTrackResultEnabled;
  }

  public String createAntechLabOrder(HttpServletRequest request,
                                     Connection conn,
                                     Integer poKey)
   throws Exception {
    cat.debug(this.getClass().getName() + ": Start createAntechLabOrder()");
    String orderStatus = "Invalid";
    LabOrder labOrder = null;

    try {
      ZoasisServicesPort zsp = getInitializedZoasisPort(request);

      LoginObject loginObj = getLoginObject(request);

      LabOrderChartId labOrderChartId = loadAntechLabOrderChartId(request,
                                                                  conn,
                                                                  poKey);
      labOrder = labOrderChartId.getLabOrder();
      cat.debug(this.getClass().getName() + "-----------------------------------------------------------------");
      cat.debug(this.getClass().getName() + ": getChartId()........: " + labOrderChartId.getChartId());
      cat.debug(this.getClass().getName() + ": getAntechAccountId(): " + labOrder.getAntechAccountId());
      cat.debug(this.getClass().getName() + ": getClientExtId()....: " + labOrder.getClientExtId());
      cat.debug(this.getClass().getName() + ": getClientName().....: " + labOrder.getClientName());
      cat.debug(this.getClass().getName() + ": getDoctorName().....: " + labOrder.getDoctorName());
      cat.debug(this.getClass().getName() + ": getIsCriticalFlag().: " + labOrder.getIsCriticalFlag());
      cat.debug(this.getClass().getName() + ": getLabId()..........: " + labOrder.getLabId());
      cat.debug(this.getClass().getName() + ": getLabOrderItems()..: " + labOrder.getLabOrderItems().length);
      cat.debug(this.getClass().getName() + ": getPetAge().........: " + labOrder.getPetAge());
      cat.debug(this.getClass().getName() + ": getPetBreed().......: " + labOrder.getPetBreed());
      cat.debug(this.getClass().getName() + ": getPetExtId().......: " + labOrder.getPetExtId());
      cat.debug(this.getClass().getName() + ": getPetName()........: " + labOrder.getPetName());
      cat.debug(this.getClass().getName() + ": getPetSex().........: " + labOrder.getPetSex());
      cat.debug(this.getClass().getName() + ": getPetSpecies().....: " + labOrder.getPetSpecies());
      cat.debug(this.getClass().getName() + ": getRequisitionId()..: " + labOrder.getRequisitionId());
      cat.debug(this.getClass().getName() + "-----------------------------------------------------------------");

      LabOrderResults labOrderResult = new LabOrderResults();
      // Original implementation used createLabOrder().
//      labOrderResult = zsp.createLabOrder(loginObj, labOrder);
      labOrderResult = zsp.createLabOrder_ChartId(loginObj, labOrderChartId);
      cat.debug(this.getClass().getName() + ": getLabOrderResults(): " + labOrderResult.getLabOrderResults());
      String successMessage = getPropertyService().getCustomerProperties(request,"webservice.antech.createlaborder.success");
      if (labOrderResult.getLabOrderResults().equals(successMessage))
        orderStatus = successMessage;

      cat.debug(this.getClass().getName() + ": Finish createAntechLabOrder() " + orderStatus);
    }
    catch (Exception e) {
      orderStatus = exceptionFilter(request,
                                    e.toString());
      if (orderStatus.length() == 0)
        throw new Exception(e);
    }
    cat.debug(this.getClass().getName() + ": Finish createAntechLabOrder() " + orderStatus);
    return orderStatus;
  }

  public LabOrderChartId loadAntechLabOrderChartId(HttpServletRequest request,
                                                   Connection conn,
                                                   Integer poKey)
   throws Exception, SQLException {
    cat.debug(this.getClass().getName() + ": Start getAntechLabOrder()");
    LabOrderChartId labOrderChartId = new LabOrderChartId();
    LabOrder labOrder = new LabOrder();
    try {
      HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, poKey);
      String poNum = pokMap.get("purchaseOrderNum").toString();
      Integer vKey = new Integer(pokMap.get("vendorKey").toString());
      Integer cKey = new Integer(pokMap.get("customerKey").toString());
      Byte attributeToEntityKey = new Byte(pokMap.get("attributeToEntityKey").toString());
      Integer attributeToKey = new Integer(pokMap.get("attributeToKey").toString());

      String labRequisitionId = this.getAntechRequisitionId(request,
                                                            conn,
                                                            poKey,
                                                            attributeToEntityKey,
                                                            attributeToKey);
      DecimalFormat myFormat = new java.text.DecimalFormat("000000");
      labOrder.setAntechAccountId(getPropertyService().getCustomerProperties(request,"webservice.antech.accountid"));
      labOrder.setLabId(getPropertyService().getCustomerProperties(request,"webservice.antech.labid"));
      labOrder.setClientExtId(myFormat.format(cKey));
      labOrder.setClientName(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "ownerFullName"));
      labOrder.setDoctorName(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicSubmittingVeterinarian"));
      labOrder.setIsCriticalFlag(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labOrderPriority"));
      labOrder.setPetAge(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalAgeCode"));
      labOrder.setPetBreed(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalBreedCode"));
      labOrder.setPetExtId(myFormat.format(attributeToKey));
      labOrder.setPetName(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalName"));
      labOrder.setPetSex(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalSex"));
      labOrder.setPetSpecies(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalSpeciesCode"));
      labOrder.setRequisitionId(labRequisitionId);

      ArrayList<LabOrderItems> serviceList = getLabOrderServices(conn, poKey);
      LabOrderItems[] lois = new LabOrderItems[serviceList.size()];
      serviceList.toArray(lois);

      cat.debug(this.getClass().getName() + ": LabOrderItems[] : " + lois.length);
      labOrder.setLabOrderItems(lois);

      // setChartId() method to assign the purchase order number.
      labOrderChartId.setChartId(poNum);
      labOrderChartId.setLabOrder(labOrder);
    }
    catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    cat.debug(this.getClass().getName() + ": Finish getAntechLabOrder()");
    return labOrderChartId;
  }


  public String getAntechRequisitionId(HttpServletRequest request,
                                       Connection conn,
                                       Integer poKey,
                                       Byte attributeToEntityKey,
                                       Integer attributeToKey)
   throws Exception {
    cat.debug(this.getClass().getName() + ": Start getAntechRequisitionId()");
    String labRequisitionId = "";

    try {
      DecimalFormat myFormat = new java.text.DecimalFormat("00000000");
      String clinicId = getPropertyService().getCustomerProperties(request,"webservice.antech.clinicid");
      String pmsId = getPropertyService().getCustomerProperties(request,"webservice.antech.pmsid");
      String poValue = myFormat.format(poKey);
      labRequisitionId = clinicId + pmsId + poValue;
    }
    catch(Exception e) {
      cat.error(this.getClass().getName() + ": SQL : " + e.getMessage());
    }
    cat.debug(this.getClass().getName() + ": Finish getAntechRequisitionId()");
    return labRequisitionId;
  }

  public ArrayList<LabOrderItems> getLabOrderServices(Connection conn,
                                                      Integer poKey)
   throws Exception, SQLException {
    cat.debug(this.getClass().getName() + ": Start getLabOrderServices()");
    ArrayList<LabOrderItems> serviceList = new ArrayList<LabOrderItems>();
    LabOrderItems loi = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the purchase order item count.
//--------------------------------------------------------------------------------
      String query = "SELECT servicedictionary.service_num,"
                   + "purchaseorderservice.note_line1 "
                   + "FROM purchaseorderservice, servicedictionary "
                   + "WHERE purchaseorder_key = ? "
                   + "AND purchaseorderservice.servicedictionary_key = servicedictionary.thekey "
                   + "AND servicedictionary.billable_id";
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      rs = ps.executeQuery();
      while(rs.next()) {
        loi = new LabOrderItems();
        loi.setOrderCode(rs.getString("service_num"));
        loi.setNotes(rs.getString("note_line1"));
        cat.debug(this.getClass().getName() + ": labOrder: " + loi.getOrderCode() + "|" + loi.getNotes());
        serviceList.add(loi);
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
    }
    cat.debug(this.getClass().getName() + ": Finish getLabOrderServices()");
    return serviceList;
   }

  public String getAntech2dTextString(HttpServletRequest request,
                                      Connection conn,
                                      String labRequisitionId,
                                      Integer poKey,
                                      Byte attributeToEntityKey,
                                      Integer attributeToKey)
   throws Exception, SQLException {
    cat.debug(this.getClass().getName() + ": Start getAntech2dTextString()");
    String labRequest = getPropertyService().getCustomerProperties(request,"webservice.antech.zoa-tag");
    String delimTag = getPropertyService().getCustomerProperties(request,"webservice.antech.delim-tag");
    String endTag = getPropertyService().getCustomerProperties(request,"webservice.antech.end-tag");
    try {
      labRequest += getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicAcctNum");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labOrderPriority");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalMRN");
      labRequest += delimTag + labRequisitionId;
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicSubmittingVeterinarian");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "ownerFullName");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalName");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalSpeciesCode");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalBreedCode");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalSex");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalAgeCode");
      labRequest += delimTag;  // blank line in barcode necessary for Antech Internal Use
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labRecheckId");
      labRequest += delimTag + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labOrderCodes");
      String commentFlag = getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labOrderCommentFlag");
      if (!(commentFlag.length() > 0)) {
        labRequest += getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labOrderCommentFlag");
        labRequest += endTag;
      }
    }
    catch(SQLException e) {
      cat.error(this.getClass().getName() + ": SQL : " + e.getMessage());
    }
    cat.debug(this.getClass().getName() + ": Finish getAntech2dTextString()");
    return labRequest;
  }

  public HashMap<String, Object> validateAntechLabOrderCode(HttpServletRequest request,
                                            Connection conn,
                                            Integer vendorKey,
                                            String serviceNum)
   throws Exception, SQLException {
    cat.debug(this.getClass().getName() + ": Start validateAntechLabOrderCode()");
    HashMap<String, Object> validateMap = new HashMap<String, Object>();
    validateMap.put("serviceKey", "0");

    try {
      if (getVendorService().isWebServiceSupported(conn, vendorKey)) {
        try {
          ZoasisServicesPort zsp = getInitializedZoasisPort(request);
          LoginObject loginObj = getLoginObject(request);

          LabOrderCode loc = new LabOrderCode();
          loc.setLabId(getPropertyService().getCustomerProperties(request,"webservice.antech.labid"));
          loc.setOrderCode(serviceNum);
          String labOrderCodeResult = zsp.validateLabOrderCode(loginObj, loc);
          cat.debug(this.getClass().getName() + ": validateLabOrderCode result: " + labOrderCodeResult);
          if (!(labOrderCodeResult.equals(getPropertyService().getCustomerProperties(request,"webservice.antech.validatelabordercode.success"))))
            validateMap.put("serviceKey", "-10");
        }
        catch (Exception e) {
          String webServiceStatus = exceptionFilter(request,
                                               e.getMessage());
          if (webServiceStatus.length() == 0)
            throw new Exception(e);
          else
            validateMap.put("serviceKey", "-99");
            validateMap.put("webServiceStatus", webServiceStatus);
          }
      }
    }
    catch(SQLException e) {
      cat.error(this.getClass().getName() + ": SQL : " + e.getMessage());
    }
    cat.debug(this.getClass().getName() + ": Finish validateAntechLabOrderCode()");
    return validateMap;
  }

  public String exceptionFilter(HttpServletRequest request,
                                String exceptionMsg)
   throws Exception {

    String orderStatus = "";
    try {
      cat.debug("exceptionFilter exceptionMsg...: " + exceptionMsg);
      String phrase = getPropertyService().getCustomerProperties(request,"webservice.antech.exception.phrase");
      String tempPhrase = "";
      String errorMessage = "";
      Integer phraseMax = new Integer(getPropertyService().getCustomerProperties(request,"webservice.antech.exception.count"));
      Integer phraseCount = 0;
      while (++phraseCount <= phraseMax) {
        tempPhrase = phrase + phraseCount.toString();
        errorMessage = getPropertyService().getCustomerProperties(request,tempPhrase);
        if (exceptionMsg.toString().contains(errorMessage)) {
          tempPhrase = tempPhrase.replaceFirst("phrase", "message");
          orderStatus = getPropertyService().getCustomerProperties(request,tempPhrase);
        }
      }
      cat.debug("exceptionFilter orderStatus...: " + orderStatus);
    }
    catch (Exception e) {
      throw new Exception(e.getMessage());
    }
    return orderStatus;
  }
}