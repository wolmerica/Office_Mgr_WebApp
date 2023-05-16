/*
 * HelpHeadForm.java
 *
 * Created on April 09, 2006, 6:44 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.help;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.MasterForm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import java.util.ArrayList;

public class HelpHeadForm extends MasterForm
{
  private String levelKey;
  private String packageKey;
  private String packageName;
  private String operationKey;
  private String operationName;
  private String recordCount;
  private ArrayList helpForm;

  public HelpHeadForm() {
    addRequiredFields(new String[] { "packageName", "operationName", "recordCount" } );
  }

  public void setLevelKey(String levelKey) {
    this.levelKey = levelKey;
  }

  public String getLevelKey() {
    return levelKey;
  }

  public void setPackageKey(String packageKey) {
    this.packageKey = packageKey;
  }

  public String getPackageKey() {
    return packageKey;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setOperationKey(String operationKey) {
    this.operationKey = operationKey;
  }

  public String getOperationKey() {
    return operationKey;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setRecordCount(String recordCount) {
    this.recordCount = recordCount;
  }

  public String getRecordCount() {
    return recordCount;
  }

  public void setHelpForm(ArrayList helpForm) {
  this.helpForm=helpForm;
  }

  public ArrayList getHelpForm() {
  return helpForm;
  }

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