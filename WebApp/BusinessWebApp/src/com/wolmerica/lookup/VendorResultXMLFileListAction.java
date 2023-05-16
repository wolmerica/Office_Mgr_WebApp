/*
 * VendorResultXMLFileListAction.java
 *
 * Created on March 23, 2010, 10:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/08/2006 - Add pagination to LookUp list display.
 */

package com.wolmerica.lookup;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class VendorResultXMLFileListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private PropertyService propertyService = new DefaultPropertyService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }


  private LookUpListHeadDO getVendorResultXMLFile(HttpServletRequest request,
                                                  Byte sourceTypeKey,
                                                  Integer sourceKey,
                                                  String lookUpNameFilter,
                                                  Integer pageNo,
                                                  Boolean jsonId)
    throws Exception, IOException {

    LookUpListHeadDO formHDO = new LookUpListHeadDO();
    LookUpListDO lookUpRow = null;
    ArrayList<LookUpListDO> lookUpRows = new ArrayList<LookUpListDO>();

    try {
     // ps.setString(1, "%" + lookUpNameFilter.toUpperCase().trim() + "%");
      Integer pageMax;
      if (!jsonId)
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"category.list.size"));
      else
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));


      int firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      int lastRecord = firstRecord + (pageMax - 1);

      int recordCount = 0;
      String outputPath = getAttachmentService().getAttachmentPath(request, sourceTypeKey, sourceKey);
      File folder = new File(outputPath);
      File[] listOfFiles = folder.listFiles();

      // Sort the file names in reverse order to get the most recent first.
      Arrays.sort(listOfFiles);
      List<File> fileList = Arrays.asList(listOfFiles);
      Collections.reverse(fileList);

      for (int i=0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
          ++recordCount;
          if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
            lookUpRow = new LookUpListDO();

            lookUpRow.setLookUpName(listOfFiles[i].getName());
            lookUpRow.setLookUpInfo(listOfFiles[i].getPath());
            lookUpRow.setLookUpCount(Integer.valueOf((int)listOfFiles[i].length()));

            lookUpRows.add(lookUpRow);
          }
       }
     }

//------------------------------------------------------------------------------
// Pagination logic to figure out what the previous and next page
// values will be for the next screen to be displayed.
//------------------------------------------------------------------------------
      Integer prevPage=0;
      Integer nextPage=0;
      if (recordCount > lastRecord)
        nextPage = pageNo + 1;
      else
        lastRecord = recordCount;
      if (firstRecord > 1)
        prevPage = pageNo - 1;
      if (recordCount == 0)
        firstRecord=0;
//------------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//------------------------------------------------------------------------------
      formHDO.setLookUpNameFilter(lookUpNameFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//------------------------------------------------------------------------------
      if (lookUpRows.isEmpty()) {
        lookUpRows.add(new LookUpListDO());
      }
      formHDO.setLookUpListForm(lookUpRows);
    }
    catch (IOException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    return formHDO;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {
//------------------------------------------------------------------------------
// Default target to success
//------------------------------------------------------------------------------
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
// Forward to the request to the login screen.
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
//--------------------------------------------------------------------------------
// Requires the source key value for attachments.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (!(request.getParameter("sourceKey") == null)) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceKey] not found!");
      }

//--------------------------------------------------------------------------------
// Requires the source type key value for attachments.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (!(request.getParameter("sourceTypeKey") == null)) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }

      String lookUpNameFilter = "";
      if (request.getParameter("lookUpNameFilter") != null) {
        lookUpNameFilter = request.getParameter("lookUpNameFilter");
      }

      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

      Boolean jsonId = false;
      if (request.getParameter("json") != null) {
        jsonId = new Boolean(request.getParameter("json").toString());
      }
//------------------------------------------------------------------------------
// New nested object coding.
//------------------------------------------------------------------------------
      try {
        // Instantiate a formHDO object to populate formHStr.
        LookUpListHeadDO formHDO = getVendorResultXMLFile(request,
                                                          sourceTypeKey,
                                                          sourceKey,
                                                          lookUpNameFilter,
                                                          pageNo,
                                                          jsonId);
        if (jsonId) {
          JSONArray jsonItems = new JSONArray();
          JSONObject obj = null;
          if (formHDO.getRecordCount() > 0) {
            ArrayList myArray = formHDO.getLookUpListForm();
            LookUpListDO myDO = new LookUpListDO();
            for (int i = 0; i < myArray.size(); i++) {
              myDO = (LookUpListDO) myArray.get(i);

              obj = new JSONObject();
              obj.put("id", myDO.getLookUpInfo());
              obj.put("value", myDO.getLookUpName());
              obj.put("info", myDO.getLookUpCount().toString());
              jsonItems.put(obj);
            }
          }
          JSONObject json = new JSONObject();
          json.put("results", jsonItems);

          response.setContentType("application/json");
          response.setHeader("Cache-Control", "no-cache");
          response.setDateHeader("Expires", 0);
          response.setHeader("Pragma", "no-cache");
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentLength(json.toString().length());
          response.getWriter().write(json.toString());
          response.getWriter().flush();
        }
        else {
          request.getSession().setAttribute("lookUpListHDO", formHDO);

          // Create the wrapper object for lookUp list.
          LookUpListHeadForm formHStr = new LookUpListHeadForm();
          formHStr.populate(formHDO);
          form = formHStr;
        }
      }
      catch (FormattingException fe) {
            fe.getMessage();
      }

      if (!jsonId) {
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
