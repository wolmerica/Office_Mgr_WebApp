/*
 * LoginForm.java
 *
 * Created on December 05, 2006, 12:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.login;

/**
 *
 * @author Richard
 */

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class LoginForm extends ActionForm {

  private String acctNum = null;
  private String lastName = null;

// Account Number Accessors
  public String getAcctNum() {
    return (this.acctNum);
  }

  public void setAcctNum(String acctNum) {
    this.acctNum = acctNum;
  }
   
// Last Name Accessors
  public String getLastName() {
    return (this.lastName);
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

// This method is called with every request. It resets the Form
// attribute prior to setting the values in the new request.
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.acctNum = null;
    this.lastName = null;    
  }

  public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();
    if ( (acctNum == null ) || (acctNum.length() == 0) ) {
      errors.add("acctNum", new ActionMessage("errors.acctNum.required"));
    }
    
    if ( (lastName == null ) || (lastName.length() == 0) ) {
      errors.add("lastName", new ActionMessage("errors.lastName.required"));
    }
    return errors;
  }
}
