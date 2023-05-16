/*
 * CustomerAttributeByItemHeadForm.java
 *
 * Created on January 31, 2005, 10:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.customerattributebyitem;

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

public class CustomerAttributeByItemHeadForm extends MasterForm
{
  private String itemDictionaryKey;
  private String brandName;
  private String size;
  private String sizeUnit;
  private ArrayList customerAttributeByItemForm;

  public CustomerAttributeByItemHeadForm() {
    addRequiredFields(new String[] { "key", "brandName", "size" } );
  }

  public void setItemDictionaryKey(String itemDictionaryKey) {
    this.itemDictionaryKey = itemDictionaryKey;
  }

  public String getItemDictionaryKey() {
    return itemDictionaryKey;
  }

  public void setBrandName(String brandName ) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setSize(String size ) {
    this.size = size;
  }

  public String getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit ) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

  public void setCustomerAttributeByItemForm(ArrayList customerAttributeByItemForm){
    this.customerAttributeByItemForm = customerAttributeByItemForm;
  }

  public ArrayList getCustomerAttributeByItemForm(){
    return customerAttributeByItemForm;
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