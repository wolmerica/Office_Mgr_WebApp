/*
 * PromotionDetailForm.java
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
import java.math.BigDecimal;

public class PromotionDetailForm extends MasterForm
{
  private String key;
  private String promotionKey;
  private String bundleKey;
  private String bundleName;
  private String bundleCategory;
  private String comboDiscountId;
  private String discountRate;
  private String createUser;
  private String createStamp;
  private String updateUser;
  private String updateStamp;

  public PromotionDetailForm() {
    addRange("discountRate", new BigDecimal("0"), new BigDecimal("999.99"));
  }


  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setPromotionKey(String promotionKey) {
    this.promotionKey = promotionKey;
  }

  public String getPromotionKey() {
    return promotionKey;
  }

  public void setBundleKey(String bundleKey) {
    this.bundleKey = bundleKey;
  }

  public String getBundleKey() {
    return bundleKey;
  }

  public void setBundleName(String bundleName) {
    this.bundleName = bundleName;
  }

  public String getBundleName() {
    return bundleName;
  }

  public void setBundleCategory(String bundleCategory) {
    this.bundleCategory = bundleCategory;
  }

  public String getBundleCategory() {
    return bundleCategory;
  }

  public void setComboDiscountId(String comboDiscountId) {
    this.comboDiscountId = comboDiscountId;
  }

  public String getComboDiscountId() {
    return comboDiscountId;
  }

  public void setDiscountRate(String discountRate) {
    this.discountRate = discountRate;
  }

  public String getDiscountRate(){
    return discountRate;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateStamp(String createStamp) {
    this.createStamp = createStamp;
  }

  public String getCreateStamp() {
    return createStamp;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(String updateStamp) {
    this.updateStamp = updateStamp;
  }

  public String getUpdateStamp() {
    return updateStamp;
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

    return errors;
  }
}