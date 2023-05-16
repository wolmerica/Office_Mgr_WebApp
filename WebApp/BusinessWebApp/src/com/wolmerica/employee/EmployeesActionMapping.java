/*
 * EmployeesActionMapping.java
 *
 * Created on August 15, 2005, 7:54 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.employee;

/**
 *
 * @author Richard
 */

import org.apache.struts.action.ActionMapping;

public class EmployeesActionMapping extends ActionMapping {

  private boolean loginRequired = false;

  public EmployeesActionMapping() {

    super();
  }

  public void setLoginRequired(boolean loginRequired) {

    this.loginRequired = loginRequired;
  }

  public boolean isLoginRequired() {

    return loginRequired;
  }
}
