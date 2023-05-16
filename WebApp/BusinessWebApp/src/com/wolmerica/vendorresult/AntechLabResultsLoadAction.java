/*
 * AntechLabResultsLoadAction.java
 *
 * Created on January 16, 2009, 9:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorresult;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.purchaseorder.PurchaseOrderService;
import com.wolmerica.service.purchaseorder.DefaultPurchaseOrderService;
import com.wolmerica.service.speciesbreed.SpeciesBreedService;
import com.wolmerica.service.speciesbreed.DefaultSpeciesBreedService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.permission.PermissionListDO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;

import org.apache.log4j.Logger;

public class AntechLabResultsLoadAction extends Action {

  
  Logger cat = Logger.getLogger("WOWAPP");

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private CustomerService CustomerService = new DefaultCustomerService();
  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private SpeciesBreedService speciesBreedService = new DefaultSpeciesBreedService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
  }

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

  public SpeciesBreedService getSpeciesBreedService() {
      return speciesBreedService;
  }

  public void setSpeciesBreedService(SpeciesBreedService speciesBreedService) {
      this.speciesBreedService = speciesBreedService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }  
  
  private void parseXmlResults(String outputFilename, String elementTagName)
   throws Exception, IOException {

//--------------------------------------------------------------------------------
// Parse the lab results XML.
//--------------------------------------------------------------------------------
    try {
      DOMParser parser = new DOMParser();
      parser.parse(outputFilename);
      Document doc = parser.getDocument();

      NodeList nodes = doc.getElementsByTagName(elementTagName);
      Node node = null;
      for (int k=0; k<nodes.getLength(); k++) {
        node = nodes.item(k);
        int type = node.getNodeType();
        switch (type) {
            case Node.ELEMENT_NODE:
              cat.debug("ELEMENT_NODE: " + node.getNodeName());
              NamedNodeMap attrs = node.getAttributes();
              int len = attrs.getLength();
              for (int l=0; l<len; l++) {
                Attr attr = (Attr)attrs.item(l);
                cat.debug(attr.getNodeName() + " = " + attr.getNodeValue());
              }
              break;
            case Node.ENTITY_REFERENCE_NODE:
              cat.debug("ENTITY_REFERENCE_NODE: " + node.getNodeValue());
              break;
            case Node.CDATA_SECTION_NODE:
              cat.debug("CDATA_SECTION_NODE: " + node.getNodeValue());
              break;
            case Node.TEXT_NODE:
              cat.debug("TEXT_NODE: " + node.getNodeValue());
              break;
            case Node.PROCESSING_INSTRUCTION_NODE:
              break;
        }
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXmlResults() " + e.getMessage());
    }
  }

  private ArrayList<HashMap<String,Object>> parseXmlLabResults(String outputFilename)
   throws Exception, IOException {
    cat.debug("Start parseXmlLabResults()");
    ArrayList<HashMap<String,Object>> labRows = new ArrayList<HashMap<String,Object>>();
    HashMap<String, Object> resultMap = null;
    HashMap<String, Object> logisticMap = new HashMap<String, Object>();
    try {
      cat.debug("Create DOMParser.: " + outputFilename);
      String elementTagName = "TestCode";
      DOMParser parser = new DOMParser();
      parser.parse(outputFilename);
      Document doc = parser.getDocument();
      NamedNodeMap attrs;

      // Get the import file name
      File importFile = new File(outputFilename);
      cat.debug("put Import-File...: " + importFile.getName());
      logisticMap.put("Import-File", importFile.getName());

      // Get the "Clinic" information
      logisticMap = parseXMLClinic(doc, logisticMap);

      // Get the "LabLocation" information
      logisticMap = parseXMLLabLocation(doc, logisticMap);

      NodeList nodes = doc.getElementsByTagName(elementTagName);
      Node node = null;
      String tcMapName = "";
      for (int k=0; k<nodes.getLength(); k++) {
        resultMap = new HashMap<String, Object>();
        resultMap = (HashMap<String, Object>) logisticMap.clone();
        node = nodes.item(k);
        if (node.getNodeType() == Node.ELEMENT_NODE){
          attrs = node.getAttributes();
          int len = attrs.getLength();
          for (int i=0; i<len; i++) {
            Attr attr = (Attr)attrs.item(i);
            tcMapName = "TC-" + attr.getNodeName();
            resultMap.put(tcMapName, attr.getNodeValue());
            cat.debug("parseXmlLabResults() " + tcMapName + " = " + resultMap.get(tcMapName));
          }
          // Make sure the "TC-Abnormal" value is populated.
          if (!(resultMap.containsKey("TC-Abnormal")))
            resultMap.put("TC-Abnormal",  resultMap.get("TC-Status").toString());
          cat.debug("TC-Abnormal = " + resultMap.get("TC-Abnormal"));
          // Get the "Accession" information
          Node accessionNode = node.getParentNode().getParentNode();
          resultMap = parseXMLAccession(accessionNode, resultMap);

          // Get the "AccessionHeader" information
          Element ele = (Element)accessionNode;
          NodeList nl = ele.getElementsByTagName("AccessionHeader");
          if (nl != null && nl.getLength() > 0) {
            Node accessionHeaderNode = nl.item(0);
            resultMap = parseXMLAccessionHeader(accessionHeaderNode, resultMap);
          }

          // Get the "UnitCode" information
          Node unitCodeNode = node.getParentNode();
          resultMap = parseXMLUnitCode(unitCodeNode, resultMap);

          // Collect the "TestCode" details.
          Element testResult = (Element)node;
          if (node.hasChildNodes()) {
            Node childNode = node.getFirstChild();
            while (childNode != node.getLastChild()) {
              childNode = childNode.getNextSibling();
              if (childNode.getNodeType() == Node.TEXT_NODE){
                resultMap.put(childNode.getPreviousSibling().getNodeName(),
                              getTextValue(testResult, childNode.getPreviousSibling().getNodeName()));
              }
            }
            labRows.add(resultMap);
          } else {
            throw new Exception("Parsing Error - TestCode expected to contain CHILD NODES.");
          }
        } else {
          throw new Exception("Parsing Error - TestCode expected to be ELEMENT_NODE type.");
        }
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXmlLabResults() " + e.getMessage());
    }
    cat.debug("Finish parseXmlLabResults()");
    return labRows;
  }

  private HashMap<String, Object> parseXMLClinic(Document doc, HashMap<String, Object> logisticMap)
   throws Exception, IOException {

    try {
      NodeList clinicNodes = doc.getElementsByTagName("Clinic");
      Node clinicNode = clinicNodes.item(0);
      if (clinicNode.getNodeType() == Node.ELEMENT_NODE){
        NamedNodeMap attrs = clinicNode.getAttributes();
        logisticMap.put("AccountNumber", attrs.getNamedItem("AccountNumber").getNodeValue());
        logisticMap.put("Ext-ID", attrs.getNamedItem("Ext-ID").getNodeValue());
        logisticMap.put("Status", attrs.getNamedItem("Status").getNodeValue());
      } else {
        throw new Exception("Parsing Error - LabReport expected to contain Clinic node.");
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXMLClinic() " + e.getMessage());
    }
    return logisticMap;
  }

  private HashMap<String, Object> parseXMLLabLocation(Document doc, HashMap<String, Object> logisticMap)
   throws Exception, IOException {

    try {
      NodeList labNodes = doc.getElementsByTagName("LabLocation");
      Node labNode = labNodes.item(0);
      if (labNode.getNodeType() == Node.ELEMENT_NODE){
        NamedNodeMap attrs = labNode.getAttributes();
        logisticMap.put("LabId", attrs.getNamedItem("LabId").getNodeValue());
        logisticMap.put("LabLocationId", attrs.getNamedItem("LabLocationId").getNodeValue());

        // Collect the "Lab" child node details.
        Element testResult = (Element)labNode;
        if (labNode.hasChildNodes()) {
          Node childNode = labNode.getFirstChild();
          while (childNode != labNode.getLastChild()) {
            childNode = childNode.getNextSibling();
            if (childNode.getNodeType() == Node.TEXT_NODE){
              logisticMap.put(childNode.getPreviousSibling().getNodeName(),
                            getTextValue(testResult, childNode.getPreviousSibling().getNodeName()));
            }
          }
        } else {
          throw new Exception("Parsing Error - LabLocation expected to contain CHILD NODES.");
        }
      } else {
        throw new Exception("Parsing Error - LabReport expected to contain LabLocation node.");
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXMLLabLocation() " + e.getMessage());
    }
    return logisticMap;
  }

  private HashMap<String, Object> parseXMLUnitCode(Node node, HashMap<String, Object> resultMap)
   throws Exception, IOException {

    try {
      if (node.getNodeName().equalsIgnoreCase("UnitCode") &&
          node.getNodeType() == Node.ELEMENT_NODE){
        NamedNodeMap attrs = node.getAttributes();
        String ucMapName = "";
        int len = attrs.getLength();
        for (int i=0; i<len; i++) {
          Attr attr = (Attr)attrs.item(i);
          ucMapName = "UC-" + attr.getNodeName();
          resultMap.put(ucMapName, attr.getNodeValue());
          cat.debug("parseXMLUnitCode() " + ucMapName + " = " + resultMap.get(ucMapName));
        }
        // Make sure the "UC-Profile-ID" value is populated.
        if (!(resultMap.containsKey("UC-Profile-ID"))) {
          resultMap.put("UC-Profile-ID", resultMap.get("UC-Ext-ID").toString().toString());
        }

        Element testResult = (Element)node;
        if (node.hasChildNodes()) {
          String mapKey = "UC-Name";
          resultMap.put(mapKey, getTextValue(testResult, "Name"));

          Node childNode = node.getFirstChild();
          while (childNode != node.getLastChild()) {
            childNode = childNode.getNextSibling();

            if (childNode.getNodeName().equalsIgnoreCase("TimeStamp") &&
                childNode.getNodeType() == Node.ELEMENT_NODE){
              cat.debug("Loading UnitCode TimeStamp");
              attrs = childNode.getAttributes();
              resultMap.put(attrs.getNamedItem("Type").getNodeValue(),
                            attrs.getNamedItem("Value").getNodeValue());
            }
          }
        }
      } else {
        throw new Exception("Parsing error - UnitCode tag not found in parseXMLAccession()");
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXMLUnitCode() " + e.getMessage());
    }
    return resultMap;
  }

  private HashMap<String, Object> parseXMLAccession(Node node, HashMap<String, Object> resultMap)
   throws Exception, IOException {

    try {
      if (node.getNodeName().equalsIgnoreCase("Accession") &&
          node.getNodeType() == Node.ELEMENT_NODE){
        NamedNodeMap attrs = node.getAttributes();
        resultMap.put("A-Acc-Result-ID", attrs.getNamedItem("Acc-Result-ID").getNodeValue());
        resultMap.put("A-Lab-ID", attrs.getNamedItem("Lab-ID").getNodeValue());
        resultMap.put("A-LabClinicExt-ID", attrs.getNamedItem("LabClinicExt-ID").getNodeValue());
        resultMap.put("A-Location-ID", attrs.getNamedItem("Location-ID").getNodeValue());
        resultMap.put("A-Order-Status", attrs.getNamedItem("Order-Status").getNodeValue());
      } else {
        throw new Exception("Parsing error - Accession tag not found in parseXMLAccession()");
      }

    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXMLAccession() " + e.getMessage());
    }
    return resultMap;
  }

  private HashMap<String, Object> parseXMLAccessionHeader(Node node, HashMap<String, Object> resultMap)
   throws Exception, IOException {

    try {
      if (node.getNodeName().equalsIgnoreCase("AccessionHeader")) {
        if (node.hasChildNodes()) {
          NamedNodeMap attrs;
          Node childNode = node.getFirstChild();
          while (childNode != node.getLastChild()) {
            childNode = childNode.getNextSibling();

            if (childNode.getNodeName().equalsIgnoreCase("Accession-ID") &&
                childNode.getNodeType() == Node.ELEMENT_NODE){
              cat.debug("Loading AccessionHeader Accession-ID");
              attrs = childNode.getAttributes();
              resultMap.put(attrs.getNamedItem("Type").getNodeValue(),
                            attrs.getNamedItem("ID").getNodeValue());
            }
            if (childNode.getNodeName().equalsIgnoreCase("TimeStamp") &&
                childNode.getNodeType() == Node.ELEMENT_NODE){
              cat.debug("Loading AccessionHeader TimeStamp");
              attrs = childNode.getAttributes();
              resultMap.put(attrs.getNamedItem("Type").getNodeValue(),
                            attrs.getNamedItem("Value").getNodeValue());
            }
            if (childNode.getNodeName().equalsIgnoreCase("Pet")) {
              cat.debug("Loading AccessionHeader Pet");
              resultMap = parseXMLPet(childNode, resultMap);
            }
          }
        }
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXMLAccessionHeader() " + e.getMessage());
    }
    return resultMap;
  }

  private HashMap<String, Object> parseXMLPet(Node node, HashMap<String, Object> resultMap)
   throws Exception, IOException {

    try {
      if (node.getNodeName().equalsIgnoreCase("Pet")) {
        Element testResult = (Element)node;
        if (node.hasChildNodes()) {
          String mapKey = "";
          Node childNode = node.getFirstChild();
          while (childNode != node.getLastChild()) {
            childNode = childNode.getNextSibling();
            if (childNode.getNodeType() == Node.TEXT_NODE){
              mapKey = "Pet-" + childNode.getPreviousSibling().getNodeName();
              resultMap.put(mapKey,
                            getTextValue(testResult, childNode.getPreviousSibling().getNodeName()));
            }
          }
        }
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("parseXMLPet() " + e.getMessage());
    }
    return resultMap;
  }

  private String getTextValue(Element ele, String tagName) {
    String textVal = "";
    NodeList nl = ele.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {
      Element el = (Element)nl.item(0);
      if (el.hasChildNodes()) {
        textVal = el.getFirstChild().getNodeValue();
      }
    }
    return textVal;
  }

  private VendorResultListHeadDO populateVendorDetail(Connection conn,
                                                      Integer vendorKey,
                                                      VendorResultListHeadDO formHDO)
   throws Exception, SQLException {

    try {
      cat.debug("formHDO.setVendorKey()");
      formHDO.setVendorKey(vendorKey);
      formHDO.setVendorName(getVendorService().getVendorName(conn, vendorKey));

//---------------------------------------------------------------------------
// Get the vendor results attachments for the specific vendor.
//---------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": getAttachmentCount(): " + formHDO.getVendorKey());
      cat.debug(this.getClass().getName() + ": featureKey..........: " + getUserStateService().getFeatureKey());
      formHDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                       getUserStateService().getFeatureKey().byteValue(),
                                                       formHDO.getVendorKey()));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("populateVendorDetail() " + e.getMessage());
    }
    return formHDO;
  }

  private VendorResultListHeadDO reviewLabResults(HttpServletRequest request,
                                                  Integer vendorKey,
                                                  String outputFilename)
   throws Exception, FormattingException, IOException, SQLException {

    VendorResultListHeadDO formHDO = new VendorResultListHeadDO();
    ArrayList<VendorResultListDO> vendorResultRows = new ArrayList<VendorResultListDO>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();
    PermissionListDO permissionRow = null;
    DateFormatter dateFormatter = new DateFormatter();

    DataSource ds = null;
    Connection conn = null;
    CallableStatement cStmt = null;
    CallableStatement cStmtUpdate = null;
    int resultCount = 0;
    Date resultDate = null;
    Date receiveDate = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Prepare stored procedure to verify a vendor result and perform update.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetVendorResultFind(?,?)}");
      cStmtUpdate = conn.prepareCall("{call SetVendorResultUpdate(?,?,?,?,?,?,?,?,?,?,"
                                                               + "?,?,?,?,?,?,?,?,?,?,"
                                                               + "?,?,?,?)}");

      String pmsId = getPropertyService().getCustomerProperties(request,"webservice.antech.pmsid");
      String clinicId = getPropertyService().getCustomerProperties(request,"webservice.antech.clinicid");

      cat.debug("Loading arrayList of hashmap from...: " + outputFilename);
      ArrayList<HashMap<String,Object>> testRows = parseXmlLabResults(outputFilename);
      cat.debug("ArrayList of hashmap is loaded from..: " + outputFilename);
      String requisitionId = "";
      String chartId = "";
      Integer poKey = null;
      for (HashMap<String, Object> resultMap :  testRows) {
        resultCount++;
//        doDebugResultMap(resultMap);
        resultMap.put("vendorKey", vendorKey);
        receiveDate = (Date) dateFormatter.unformat(resultMap.get("Order Received DateTime").toString());
        cat.debug("Recieve Date...: " + resultMap.get("Order Received DateTime"));
        resultDate = (Date) dateFormatter.unformat(resultMap.get("Latest Results Received DateTime").toString().substring(0,10));
        cat.debug("Result Date....: " + resultMap.get("Latest Results Received DateTime"));
//--------------------------------------------------------------------------------
// Call a stored procedure to verify a vendor result record exists.
// Add the corresponding purchase order service and vendor result if needed.
//--------------------------------------------------------------------------------
        requisitionId = "";
        if (resultMap.containsKey("Requisition-ID")) {
          requisitionId = resultMap.get("Requisition-ID").toString();
          cat.debug("Requisition-ID EXISTS!!! " + requisitionId);
        }
        chartId = "";
        if (resultMap.containsKey("Chart-ID")) {
          chartId = resultMap.get("Chart-ID").toString();
          cat.debug("Chart-ID EXISTS!!! " + chartId);
        }
        resultMap = getPurchaseOrderKeyByMetaData(conn,
                                                  resultMap,
                                                  pmsId,
                                                  requisitionId,
                                                  chartId);
        cat.debug("P.O. Key.....: " + resultMap.get("poKey"));
        cat.debug("Test Code Id.: " + resultMap.get("TC-Ext-ID"));
        poKey = new Integer(resultMap.get("poKey").toString());
        if (poKey > 0) {
          cat.debug("call SetVendorResultFind()");
          cStmt.setInt(1, poKey);
          cStmt.execute();
          cat.debug("SetVendorResultFind() poKey..: " + cStmt.getInt("poKey"));
          if (cStmt.getInt("poKey") > 0) {
//-------------------------------------------------------------------------------
//Call a stored procedure to update the vendor result with the lab results.
//-------------------------------------------------------------------------------
            cat.debug("call SetVendorResultUpdate()");
            cStmtUpdate.setInt(1, poKey);
            cStmtUpdate.setString(2, resultMap.get("Import-File").toString());
            cStmtUpdate.setString(3, resultMap.get("A-Order-Status").toString());
            cStmtUpdate.setString(4, resultMap.get("LocationName").toString());
            cat.debug("cStmtUpdate 'ReceiveDate' " + receiveDate.toString());
            cStmtUpdate.setDate(5, new java.sql.Date(receiveDate.getTime()));
            cat.debug("cStmtUpdate 'Requisition-ID'");
            cStmtUpdate.setString(6, requisitionId);
            cStmtUpdate.setDate(7, new java.sql.Date(resultDate.getTime()));
            cStmtUpdate.setString(8, resultMap.get("A-Acc-Result-ID").toString());
            cat.debug("cStmtUpdate 'UC-Profile-ID' " + resultMap.get("UC-Profile-ID").toString());
            cStmtUpdate.setString(9, resultMap.get("UC-Profile-ID").toString());
            cStmtUpdate.setString(10, resultMap.get("UC-Status").toString());
            cStmtUpdate.setString(11, resultMap.get("UC-Ext-ID").toString());
            cStmtUpdate.setString(12, resultMap.get("UC-Name").toString());
            cat.debug("cStmtUpdate 'TC-Abnormal' " + resultMap.get("TC-Abnormal").toString());
            cStmtUpdate.setString(13, resultMap.get("TC-Abnormal").toString());
            cat.debug("cStmtUpdate 'TC-Status' " + resultMap.get("TC-Status").toString());
            cStmtUpdate.setString(14, resultMap.get("TC-Status").toString());
            cat.debug("cStmtUpdate 'TC-Ext-ID' " + resultMap.get("TC-Ext-ID").toString());
            cStmtUpdate.setString(15, resultMap.get("TC-Ext-ID").toString());
            cat.debug("cStmtUpdate 'Name'");
            cStmtUpdate.setString(16, resultMap.get("Name").toString());
            cStmtUpdate.setString(17, resultMap.get("Value").toString());
            cStmtUpdate.setString(18, resultMap.get("Units").toString());
            cStmtUpdate.setString(19, resultMap.get("Range").toString());
            if (resultMap.containsKey("Comment"))
              if (resultMap.get("Comment").toString().length() > 1256)
                cStmtUpdate.setString(20, resultMap.get("Comment").toString().substring(0,1255));
              else
                cStmtUpdate.setString(20, resultMap.get("Comment").toString());
            else
              cStmtUpdate.setString(20, "");
            cat.debug("cStmtUpdate 'Chart-ID'");
            cStmtUpdate.setString(21, resultMap.get("Chart-ID")+"|"
                    + resultMap.get("Pet-Owner").toString()+"|"
                    + resultMap.get("Pet-Name").toString()+"|"
                    + resultMap.get("Pet-Age").toString()+"|"
                    + resultMap.get("Pet-Sex").toString()+"|"
                    + resultMap.get("Pet-Species").toString()+"|"
                    + resultMap.get("Pet-Breed").toString()+"|"
                    + resultMap.get("Pet-Doctor").toString()+"|");
            cStmtUpdate.setString(22, request.getSession().getAttribute("USERNAME").toString());
            cStmtUpdate.execute();
            resultMap.put("Load-State", "Success");
            resultMap.put("vrKey", cStmtUpdate.getInt("vrKey"));
          } else { resultMap.put("Load-State", "Undefined PO key: " + poKey);}
        } else {
          // A negative poKey value indicates incomplete order meta data.
          cat.debug("Get the User action for scenario.: " + resultMap.get("scenarioKey"));
          resultMap.put("Load-State", getLabResultUserAction(new Integer(resultMap.get("scenarioKey").toString())));
        }

        permissionRow = getUserStateService().getUserListToken(request,
                                                   conn,
                                                   this.getClass().getName(),
                                                   poKey);
        permissionRows.add(permissionRow);

        vendorResultRows.add(populateVendorResult(conn, resultMap, resultDate));
      }
      formHDO = populateVendorDetail(conn, vendorKey, formHDO);
      formHDO.setXmlFileName(outputFilename);
      formHDO.setXmlImportName(new File(outputFilename).getName());
      formHDO.setRecordCount(vendorResultRows.size());
      formHDO.setFirstRecord(1);
      formHDO.setLastRecord(vendorResultRows.size());
      formHDO.setPreviousPage(0);
      formHDO.setNextPage(0);

      if (vendorResultRows.isEmpty()) {
        vendorResultRows.add(new VendorResultListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
        formHDO.setFirstRecord(0);
      }

      formHDO.setVendorResultListForm(vendorResultRows);
      formHDO.setPermissionListForm(permissionRows);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("reviewLabResults() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("reviewLabResults() " + e.getMessage());
      }
      try
      {
        cStmtUpdate.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("reviewLabResults() " + e.getMessage());
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
    return formHDO;
  }

  private String getClinicId(String pmsId,
                             String requisitionId)
   throws Exception, IOException {

    String clinicId = "";
    try {
    // Antech assigned clinicId, followed by "-", three letter PMS Id,
    // and finally the purchase order key: 98950-WOW00000057
      int idxValue = requisitionId.indexOf(pmsId);
      if (idxValue > 0) {
        idxValue += -1;
        clinicId = requisitionId.substring(0,idxValue);
      } else {
        throw new Exception("Unable to identify the Clinic ID in requestion id: " + requisitionId);
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("getClinicId() " + e.getMessage());
    }
    return clinicId;
  }

  private HashMap<String, Object> getPurchaseOrderKeyByMetaData(Connection conn,
                                                HashMap<String, Object> resultMap,
                                                String pmsId,
                                                String requisitionId,
                                                String chartId)
   throws Exception, IOException, SQLException {

    String clinicId = "";
    Integer scenarioKey = null;
    cat.debug("Start getPurchaseOrderKeyByMetaData()..: " + requisitionId);
    try {
    // Antech assigned clinicId, followed by "-", three letter PMS Id,
    // and finally the purchase order key: 98950-WOW00000057
      int idxValue = requisitionId.indexOf(pmsId);
      if (idxValue > 0) {
        clinicId = requisitionId.substring(0,(idxValue-1));
        idxValue += pmsId.length();
        resultMap.put("poKey", requisitionId.substring(idxValue));
        cat.debug("Found PO key inside requisitionId " + resultMap.get("poKey"));
        HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, new Integer(resultMap.get("poKey").toString()));
        resultMap.put("vendorKey", pokMap.get("vendorKey"));
        resultMap.put("customerKey", pokMap.get("customerKey"));
        resultMap.put("petKey", pokMap.get("attributeToKey"));
      } else {
          CallableStatement cStmt = null;
          try {
            cStmt = conn.prepareCall("{call GetPurchaseOrderByMetaData(?,?,?,?,?,?,?,?,?,?,?,?)}");
            DateFormatter dateFormatter = new DateFormatter();
            cat.debug("begin dateFormatter birthDate " + resultMap.get("Pet-Age").toString());
            // Problem using birth date when age in years is recorded by lab.
            // Do we convert an age value such as, "13Y", into a date?.
            Date birthDate = new Date();
            cat.debug("begin dateFormatter receiveDate " + resultMap.get("Order Received DateTime").toString());
            Date receivedDate = (Date) dateFormatter.unformat(resultMap.get("Order Received DateTime").toString());
            cStmt.setString(1, chartId);
            cStmt.setString(2, resultMap.get("Pet-Name").toString());
            cStmt.setDate(3, new java.sql.Date(birthDate.getTime()));
            cStmt.setString(4, resultMap.get("Pet-Species").toString());
            cStmt.setString(5, resultMap.get("Pet-Sex").toString());
            cStmt.setString(6, resultMap.get("Pet-Owner").toString());
            cStmt.setDate(7, new java.sql.Date(receivedDate.getTime()));
            cat.debug("Execute GetPurchaseOrderByMetaData() with values:");
            cStmt.execute();
            cat.debug(" chartId......: [" + chartId + "]");
            cat.debug(" petName......: [" + resultMap.get("Pet-Name").toString() + "]");
            cat.debug(" birthDate....: [" + birthDate.toString() + "]");
            cat.debug(" petSpecies...: [" + resultMap.get("Pet-Species").toString() + "]");
            cat.debug(" petSex.......: [" + resultMap.get("Pet-Sex").toString() + "]");
            cat.debug(" petOwner.....: [" + resultMap.get("Pet-Owner").toString() + "]");
            cat.debug(" receiveDate..: [" + receivedDate.toString() + "]");
//------------------------------------------------------------------------------
// Add the new discovered values to the resultMap so they may be returned.
//------------------------------------------------------------------------------
            resultMap.put("poKey", cStmt.getInt("poKey"));
            resultMap.put("customerKey", cStmt.getInt("customerKey"));
            resultMap.put("petKey", cStmt.getInt("petKey"));
            resultMap.put("scenarioKey", cStmt.getInt("scenarioKey"));
            resultMap.put("scenario", cStmt.getString("scenario"));

            cat.debug("GetPurchaseOrderByMetaData() return values:");
            cat.debug(" poKey........: [" + resultMap.get("poKey") + "]");
            cat.debug(" customerKey..: [" + resultMap.get("customerKey") + "]");
            cat.debug(" petKey.......: [" + resultMap.get("petKey") + "]");
            cat.debug(" scenarioKey..: [" + resultMap.get("scenarioKey") + "]");
            cat.debug(" scenario.....: [" + resultMap.get("scenario") + "]");
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("getPurchaseOrderKeyByMetaData " + e.getMessage());
        }
        finally
        {
          try
          {
            cStmt.close();
          }
          catch (SQLException e) {
            cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
            throw new Exception(e.getMessage());
          }
        }
        cat.debug("Finish getPurchaseOrderKeyByMetaData()");
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("getPurchaseOrderKeyByMetaData() " + e.getMessage());
    }
    return resultMap;
  }

  private String getLabResultUserAction(Integer scenarioKey)
    throws Exception {
    cat.debug("Start getLabResultUserAction().: " + scenarioKey);
    String errorMsg = "Call the system administrator";
    try {
      if (scenarioKey == -1)
        errorMsg = "Add a new customer record.";
      else if (scenarioKey == -2)
        errorMsg = "Add a new pet record.";
      else if (scenarioKey == -3)
        errorMsg = "Multiple pet records were found.";
      else if (scenarioKey == -4)
        errorMsg = "Add a new purchase order.";
      else if (scenarioKey == -5)
        errorMsg = "Multiple purchase orders were found.";
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("getLabResultUserAction()" + e.getMessage());
    }
    cat.debug("Finish getLabResultUserAction().: " + errorMsg);
  return errorMsg;
  }

  private VendorResultListDO populateVendorResult(Connection conn,
                                                  HashMap resultMap,
                                                  Date resultDate)
    throws Exception, IOException {

    cat.debug("Start populateVendorResult().: " + resultDate);
    VendorResultListDO formDO = new VendorResultListDO();
    try {
      if (resultMap.containsKey("vrKey"))
        formDO.setKey(new Integer(resultMap.get("vrKey").toString()));
      cat.debug("populateVendorResult() #1-setKey()");
      if (resultMap.containsKey("vendorKey")) {
        formDO.setVendorKey(new Integer(resultMap.get("vendorKey").toString()));
        cat.debug("populateVendorResult() #1.5-vendorKey() " + formDO.getVendorKey());
        formDO.setVendorName(getVendorService().getVendorName(conn, formDO.getVendorKey()));
      }
      if (resultMap.containsKey("LocationName"))
        formDO.setVendorName(resultMap.get("LocationName").toString());
      cat.debug("populateVendorResult() #2:setVendorName()");
      if (resultMap.containsKey("Pet-Owner")) {
        HashMap nameMap = this.getFirstAndLastName(resultMap.get("Pet-Owner").toString());
        formDO.setCustomerFirstName(nameMap.get("firstName").toString());
        formDO.setCustomerLastName(nameMap.get("lastName").toString());
        formDO.setCustomerName(formDO.getCustomerFirstName() + " "
                             + formDO.getCustomerLastName());
      }
      if (resultMap.containsKey("customerKey")) {
        formDO.setCustomerKey(new Integer(resultMap.get("customerKey").toString()));
        if (formDO.getCustomerKey() > 0) {
          cat.debug("populateVendorResult() #2.5-CustomerKey");
          HashMap entity = getCustomerService().getCustomerAttributeToEntity(conn, formDO.getCustomerKey());
          if (entity.containsKey("key"))
            formDO.setAttributeToEntityKey(new Byte(entity.get("key").toString()));
          if (entity.containsValue("name"))
            formDO.setAttributeToEntityName(entity.get("name").toString());
          // Over-ride the customer name if we have identified the customer.
          formDO.setCustomerName(getCustomerService().getClientName(conn, formDO.getCustomerKey()));
        }
      }
      cat.debug("populateVendorResult() #3-setCustomerName()");
      if (resultMap.containsKey("petKey"))
        formDO.setAttributeToKey(new Integer(resultMap.get("petKey").toString()));
      cat.debug("populateVendorResult() #3.25-setAttributeToKey()");
      if (resultMap.containsKey("Pet-Name"))
        formDO.setAttributeToName(resultMap.get("Pet-Name").toString());
      cat.debug("populateVendorResult() #3.5-setAttributeToName()");
      if (resultMap.containsKey("Pet-Age"))
        formDO.setPetAge(resultMap.get("Pet-Age").toString());
      cat.debug("populateVendorResult() #3.75-setPetAge() " + resultMap.get("Pet-Age"));

      if (resultMap.containsKey("Pet-Sex")) {
        cat.debug("populateVendorResult() #4-setPetSex() " + resultMap.get("Pet-Sex"));
        HashMap sexNeuteredMap = getSpeciesBreedService().GetSexAndNeuterIdByCode(conn, resultMap.get("Pet-Sex").toString());
        if (sexNeuteredMap.containsKey("neuteredId"))
          formDO.setNeuteredId(new Byte(sexNeuteredMap.get("neuteredId").toString()));
        cat.debug("populateVendorResult() #4-setNeuteredId()");
        if (sexNeuteredMap.containsKey("sexId"))
          formDO.setPetSexId(new Byte(sexNeuteredMap.get("sexId").toString()));
      }
      if (resultMap.containsKey("Pet-Species")) {
        formDO.setSpeciesName(resultMap.get("Pet-Species").toString());
        formDO.setSpeciesKey(getSpeciesBreedService().getSpeciesKeyByName(conn, formDO.getSpeciesName()));
        cat.debug("populateVendorResult() #5-setSpeciesKey() " + formDO.getSpeciesKey());
      }
      if (resultMap.containsKey("Pet-Breed")) {
        formDO.setBreedName(resultMap.get("Pet-Breed").toString());
        formDO.setBreedKey(getSpeciesBreedService().getSpeciesKeyByName(conn, formDO.getBreedName()));
      }
      if (resultMap.containsKey("sdKey"))
        formDO.setSourceKey(new Integer(resultMap.get("sdKey").toString()));
      if (resultMap.containsKey("TC-Ext-ID"))
        formDO.setTestCode(resultMap.get("TC-Ext-ID").toString());
      if (resultMap.containsKey("Name"))
        formDO.setTestName(resultMap.get("Name").toString());
      cat.debug("populateVendorResult() #6-setTestName()");
      if (resultMap.containsKey("TC-Status"))
        formDO.setTestStatus(resultMap.get("TC-Status").toString());
      if (resultMap.containsKey("TC-Abnormal"))
        formDO.setAbnormalStatus(resultMap.get("TC-Abnormal").toString());
      if (resultMap.containsKey("Value"))
        formDO.setTestValue(resultMap.get("Value").toString());
      if (resultMap.containsKey("Units"))
        formDO.setTestUnits(resultMap.get("Units").toString());
      cat.debug("populateVendorResult() #7-setTestUnits()");
      if (resultMap.containsKey("Range"))
        formDO.setTestRange(resultMap.get("Range").toString());
      formDO.setResultDate(new java.sql.Date(resultDate.getTime()));
      if (resultMap.containsKey("Load-State"))
        formDO.setErrorMessage(resultMap.get("Load-State").toString());
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("populateVendorResult() " + e.getMessage());
    }
    cat.debug("Finish populateVendorResult()");
  return formDO;
  }

  private void doDebugResultMap(HashMap resultMap)
   throws Exception, IOException {

    try {
      cat.debug("Clinic "
             + resultMap.get("AccountNumber")+"|"
             + resultMap.get("Ext-ID")+"|"
             + resultMap.get("Status")+"|");
      cat.debug("Lab "
             + resultMap.get("LabId")+"|"
             + resultMap.get("LabLocationId")+"|"
             + resultMap.get("LocationCode")+"|"
             + resultMap.get("LocationName")+"|");
      cat.debug("Accession "
             + resultMap.get("A-Acc-Result-ID")+"|"
             + resultMap.get("A-Lab-ID")+"|"
             + resultMap.get("A-LabClinicExt-ID")+"|"
             + resultMap.get("A-Location-ID")+"|"
             + resultMap.get("A-Order-Status")+"|");
      cat.debug("AH "
             + resultMap.get("Clinic-AccID")+"|"
             + resultMap.get("Requisition-ID")+"|"
             + resultMap.get("Chart-ID")+"|"
             + resultMap.get("Lab-AccID")+"|"
             + resultMap.get("Order Received DateTime")+"|"
             + resultMap.get("Latest Results Received DateTime")+"|"
             + resultMap.get("Printed DateTime")+"|");
      cat.debug("Pet "
             + resultMap.get("Pet-Name")+"|"
             + resultMap.get("Pet-Age")+"|"
             + resultMap.get("Pet-Sex")+"|"
             + resultMap.get("Pet-Species")+"|"
             + resultMap.get("Pet-Breed")+"|"
             + resultMap.get("Pet-Owner")+"|"
             + resultMap.get("Pet-Doctor")+"|");
      cat.debug("UC "
             + resultMap.get("UC-Profile-ID")+"|"
             + resultMap.get("UC-Ext-ID")+"|"
             + resultMap.get("UC-Order-Control-Status")+"|"
             + resultMap.get("UC-Status")+"|"
             + resultMap.get("UC-Name")+"|"
             + resultMap.get("Released Datetime")+"|"
             + resultMap.get("Viewed Datetime")+"|");
      cat.debug("TC "
             + resultMap.get("TC-Abnormal")+"|"
             + resultMap.get("TC-Ext-ID")+"|"
             + resultMap.get("TC-Status")+"|"
             + resultMap.get("Name")+"|"
             + resultMap.get("Value")+"|"
             + resultMap.get("Units")+"|"
             + resultMap.get("Range")+"|");
      if (resultMap.containsKey("Comment")) {
        cat.debug("TComment ["
               + resultMap.get("Comment").toString().length() + "] "
               + resultMap.get("Comment"));
      }
    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("doDebugResultMap() " + e.getMessage());
    }
  }

  private VendorResultListHeadDO emptyLabResults(HttpServletRequest request,
                                                 Integer vendorKey)
   throws Exception, SQLException {

    VendorResultListHeadDO formHDO = new VendorResultListHeadDO();
    ArrayList<VendorResultListDO> vendorResultRows = new ArrayList<VendorResultListDO>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      formHDO.setRecordCount(0);
      formHDO.setFirstRecord(0);
      formHDO.setLastRecord(0);
      formHDO.setPreviousPage(0);
      formHDO.setNextPage(0);

      if (vendorResultRows.isEmpty()) {
        vendorResultRows.add(new VendorResultListDO());
        permissionRows.add(new PermissionListDO());
        formHDO.setFirstRecord(0);
      }
      formHDO = populateVendorDetail(conn, vendorKey, formHDO);
      formHDO.setVendorResultListForm(vendorResultRows);
      formHDO.setPermissionListForm(permissionRows);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("emptyLabResults() " + e.getMessage());
    }
    finally
    {
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
    return formHDO;
  }

  private HashMap<String, String> getFirstAndLastName(String fullName)
   throws Exception {

    HashMap<String, String> nameMap = new HashMap<String, String>();
    try {
      String lastName = fullName;
      String firstName = "";
      if (fullName.length() > 0) {
        // Check for a comma
        if (fullName.indexOf(",") > 0) {
          lastName = fullName.substring(0, fullName.indexOf(",")).trim();
          firstName = fullName.substring(fullName.indexOf(",") + 1).trim();
        }
        else if(fullName.indexOf(" ") > 0) {
            lastName = fullName.substring(0, fullName.indexOf(" ")).trim();
            firstName = fullName.substring(fullName.indexOf(" ") + 1).trim();
        }
      }
      nameMap.put("lastName", lastName);
      nameMap.put("firstName", firstName);

    } catch(Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
      throw new Exception("getFirstAndLastName() " + e.getMessage());
    }
    return nameMap;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

    // Default target to success
    String target = "success";

    EmployeesActionMapping employeesMapping =
      (EmployeesActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {
      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.login.required"));
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form.
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
//--------------------------------------------------------------------------------
// Preserving the vendor key value for attachments.
//--------------------------------------------------------------------------------
      Integer vendorKey = null;
      if (request.getParameter("vendorKey") != null) {
        if (!(request.getParameter("vendorKey").equalsIgnoreCase(""))) {
          vendorKey = new Integer(request.getParameter("vendorKey"));
          cat.debug(this.getClass().getName() + ": getParameter[vendorKey] = " + vendorKey.toString());
        }
      }
      else {
        if (request.getAttribute("vendorKey") != null) {
          if (!(request.getAttribute("vendorKey").toString().equalsIgnoreCase(""))) {
            vendorKey = new Integer(request.getAttribute("vendorKey").toString());
            cat.debug(this.getClass().getName() + ": getAttribute[vendorKey] = " + vendorKey.toString());
          }
        }
      }

//--------------------------------------------------------------------------------
// Pass along the message value from the resultUploadMessage
//--------------------------------------------------------------------------------
      String resultUploadState = "";
      if (request.getAttribute("resultUploadMessage") != null) {
        resultUploadState = request.getAttribute("resultUploadMessage").toString();
      }

//--------------------------------------------------------------------------------
// XML file containing the Antech lab results.
//--------------------------------------------------------------------------------
      String xmlFileName = "";
      if (request.getAttribute("xmlFileName") != null) {
        xmlFileName = request.getAttribute("xmlFileName").toString();
        cat.debug(this.getClass().getName() + ": getAttribute[xmlFileName] = " + xmlFileName);
      }
      else {
        if (request.getParameter("xmlFileName") != null) {
          xmlFileName = request.getParameter("xmlFileName").toString();
          cat.debug(this.getClass().getName() + ": getParameter[xmlFileName] = " + xmlFileName);
        }
        else {
          throw new Exception("Request Attribute/Parameter [xmlFileName] not found!");
        }
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              vendorKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
        VendorResultListHeadDO formHDO = emptyLabResults(request,
                                                         vendorKey);
        cat.debug("resultUploadState..: " + resultUploadState);
        if (resultUploadState.length() == 0) {
          cat.debug("xmlFileName...: " + xmlFileName);
          cat.debug("AntechLabResultsLoad file.: " + xmlFileName);
          if (xmlFileName.equalsIgnoreCase("NoLabResultsToDownload")) {
            cat.debug("set noLabResults..: true");
            request.setAttribute("noLabResults", true);
          } else {
            cat.debug("Start reviewLabResults()");
            formHDO = reviewLabResults(request,
                                       vendorKey,
                                       xmlFileName);
            cat.debug("Finish reviewLabResults()");
          }
        } else {
          cat.debug("AntechLabResultsLoad..: " + resultUploadState);
          request.setAttribute("resultUploadMessage", resultUploadState);
        }
        request.getSession().setAttribute("vendorResultListHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for Vendor list.
//--------------------------------------------------------------------------------
        VendorResultListHeadForm formHStr = new VendorResultListHeadForm();
        formHStr.populate(formHDO);
        form = formHStr;
      }
      catch (FormattingException fe) {
        fe.getMessage();
      }

      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
      }

    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("errors.database.error", e.getMessage()));
//--------------------------------------------------------------------------------
// Report any ActionMessages
//--------------------------------------------------------------------------------
      if (!actionMessages.isEmpty()) {
        saveMessages(request, actionMessages);
      }
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}