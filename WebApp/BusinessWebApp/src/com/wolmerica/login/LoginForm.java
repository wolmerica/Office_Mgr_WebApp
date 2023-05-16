/*
 * LoginForm.java
 *
 * Created on August 14, 2005, 9:13 PM
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

  private String userName = null;
  private String password = null;

// Username Accessors
  public String getUserName() {
    return (this.userName);
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
   
// Password Accessors
  public String getPassword() {
    return (this.password);
  }

  public void setPassword(String password) {
    this.password = password;
  }

// This method is called with every request. It resets the Form
// attribute prior to setting the values in the new request.
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.userName = null;
    this.password = null;    
  }

  public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();
    if ( (userName == null ) || (userName.length() == 0) ) {
      errors.add("userName", new ActionMessage("errors.userName.required"));
    }
    
    if ( (password == null ) || (password.length() == 0) ) {
      errors.add("password", new ActionMessage("errors.password.required"));
    }
    return errors;
  }
}
