/*
 * CustomerActionMapping.java
 *
 * Created on December 05, 2006, 12:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customer;

/**
 *
 * @author Richard
 */

import org.apache.struts.action.ActionMapping;

public class CustomerActionMapping extends ActionMapping {

  private boolean loginRequired = false;

  public CustomerActionMapping() {

    super();
  }

  public void setLoginRequired(boolean loginRequired) {

    this.loginRequired = loginRequired;
  }

  public boolean isLoginRequired() {

    return loginRequired;
  }
}
