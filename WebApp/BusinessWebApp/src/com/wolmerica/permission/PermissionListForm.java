/*
 * PermissionListForm.java
 *
 * Created on July 31, 2006, 9:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.permission;

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

public class PermissionListForm extends MasterForm {

  private String lockAvailableId;
  private String myLockId;
  private String listId;
  private String viewId;
  private String editId;
  private String addId;
  private String deleteId;
  private String lockedBy;

  public void setLockAvailableId(String lockAvailableId) {
    this.lockAvailableId = lockAvailableId;
  }

  public String getLockAvailableId() {
    return lockAvailableId;
  }

  public void setMyLockId(String myLockId) {
    this.myLockId = myLockId;
  }

  public String getMyLockId() {
    return myLockId;
  }

  public void setListId(String listId) {
    this.listId = listId;
  }

  public String getListId() {
    return listId;
  }

  public void setViewId(String viewId) {
    this.viewId = viewId;
  }

  public String getViewId() {
    return viewId;
  }

  public void setEditId(String editId) {
    this.editId = editId;
  }

  public String getEditId() {
    return editId;
  }

  public void setAddId(String addId) {
    this.addId = addId;
  }

  public String getAddId() {
    return addId;
  }

  public void setDeleteId(String deleteId) {
    this.deleteId = deleteId;
  }

  public String getDeleteId() {
    return deleteId;
  }

  public void setLockedBy(String lockedBy) {
    this.lockedBy = lockedBy;
  }

  public String getLockedBy() {
    return lockedBy;
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

