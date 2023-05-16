/*
 * CustomerAttributeByServiceHeadForm.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customerattributebyservice;

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

public class CustomerAttributeByServiceHeadForm extends MasterForm
{
  private String serviceDictionaryKey;
  private String serviceName;
  private ArrayList customerAttributeByServiceForm;

  public CustomerAttributeByServiceHeadForm() {
    addRequiredFields(new String[] { "key", "serviceName" } );
  }

  public void setServiceDictionaryKey(String serviceDictionaryKey) {
    this.serviceDictionaryKey = serviceDictionaryKey;
  }

  public String getServiceDictionaryKey() {
    return serviceDictionaryKey;
  }

  public void setServiceName(String serviceName ) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setCustomerAttributeByServiceForm(ArrayList customerAttributeByServiceForm){
    this.customerAttributeByServiceForm = customerAttributeByServiceForm;
  }

  public ArrayList getCustomerAttributeByServiceForm(){
    return customerAttributeByServiceForm;
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