/*
 * PromotionDetailListHeadForm.java
 *
 * Created on April 04, 2009, 11:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.promotiondetail;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.util.ArrayList;

public class PromotionDetailListHeadForm extends MasterForm
{
  private String promotionKey;
  private String permissionStatus;
  private String recordCount;
  private String firstRecord;
  private String lastRecord;
  private String previousPage;
  private String nextPage;
  private ArrayList promotionDetailForm;

  public void setPromotionKey(String promotionKey) {
    this.promotionKey = promotionKey;
  }

  public String getPromotionKey() {
    return promotionKey;
  }

  public void setPermissionStatus(String permissionStatus) {
    this.permissionStatus = permissionStatus;
  }

  public String getPermissionStatus() {
    return permissionStatus;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setFirstRecord(String firstRecord) {
    this.firstRecord = firstRecord;
  }

  public String getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(String lastRecord) {
    this.lastRecord = lastRecord;
  }

  public String getLastRecord() {
    return lastRecord;
  }

  public void setPreviousPage(String previousPage ) {
    this.previousPage = previousPage;
  }

  public String getPreviousPage() {
    return previousPage;
  }

  public void setNextPage(String nextPage ) {
    this.nextPage = nextPage;
  }

  public String getNextPage() {
    return nextPage;
  }
  
  public void setPromotionDetailForm(ArrayList promotionDetail){
  this.promotionDetailForm=promotionDetail;
  }

  public ArrayList getPromotionDetailForm(){
  return promotionDetailForm;
  }

    @Override
  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    EmployeesActionMapping employeesMapping = (EmployeesActionMapping)mapping;

    // Does this action require the user to login
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {

        // return null to force action to handle login
        // error
        return null;
      }
    }

    errors = super.validate(mapping, request);

    // Post a global message instructing user to clean up
    // validation errors and resubmit
    if (errors.size() > 0) {
      ActionMessage message =
          new ActionMessage("message.validation");
      ActionMessages messages = new ActionMessages();
          messages.add(ActionMessages.GLOBAL_MESSAGE, message);
      request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

    // Set the disableEdit attribute to false when errors encountered.
    request.setAttribute("disableEdit", false);

    return errors;
  }
}