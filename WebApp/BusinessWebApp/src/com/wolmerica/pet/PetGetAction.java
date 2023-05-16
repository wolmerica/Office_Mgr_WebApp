/*
 * PetGetAction.java
 *
 * Created on December 09, 2005, 12:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.pet;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.speciesbreed.SpeciesBreedService;
import com.wolmerica.service.speciesbreed.DefaultSpeciesBreedService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PetGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private AttributeToService attributeToService = new DefaultAttributeToService();
  private PropertyService propertyService = new DefaultPropertyService();
  private SpeciesBreedService speciesBreedService = new DefaultSpeciesBreedService();
  private UserStateService userStateService = new DefaultUserStateService();
  
  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public SpeciesBreedService getSpeciesBreedService() {
      return speciesBreedService;
  }

  public void setSpeciesBreedService(SpeciesBreedService speciesBreedService) {
      this.speciesBreedService = speciesBreedService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private PetDO buildPetForm(HttpServletRequest request,
                             Integer pKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    PetDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT pet.customer_key,"
                   + "customer.client_name,"
                   + "pet.name,"
                   + "pet.species_key,"
                   + "pet.breed_key,"
                   + "pet.color,"
                   + "pet.sex_id,"
                   + "pet.weight,"
                   + "pet.birth_date,"
                   + "pet.neutered_id,"
                   + "pet.neutered_date,"
                   + "pet.disposition,"
                   + "pet.identification_tag_number,"
                   + "pet.rabies_tag_number,"
                   + "pet.dvm_resource_key,"
                   + "r1.name AS dvm_resource_name,"
                   + "pet_memo,"
                   + "pet.last_check_date,"
                   + "pet.active_id,"
                   + "pet.create_user,"
                   + "pet.create_stamp,"
                   + "pet.update_user,"
                   + "pet.update_stamp "
                   + "FROM pet, customer, resource r1 "
                   + "WHERE pet.thekey=? "
                   + "AND pet.customer_key = customer.thekey "
                   + "AND pet.dvm_resource_key = r1.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, pKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {

        formDO = new PetDO();

        formDO.setKey(pKey);
        formDO.setCustomerKey(rs.getInt("customer_key"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setPetName(rs.getString("name"));
        formDO.setSpeciesKey(rs.getInt("species_key"));
        formDO.setBreedKey(rs.getInt("breed_key"));
        formDO.setPetColor(rs.getString("color"));
        formDO.setPetSexId(rs.getByte("sex_id"));
        formDO.setPetWeight(rs.getBigDecimal("weight"));
        formDO.setBirthDate(rs.getDate("birth_date"));
        formDO.setLastCheckDate(rs.getDate("last_check_date"));
        formDO.setNeuteredId(rs.getByte("neutered_id"));
        formDO.setNeuteredDate(rs.getDate("neutered_date"));
        formDO.setIdentificationTagNumber(rs.getString("identification_tag_number"));
        formDO.setRabiesTagNumber(rs.getString("rabies_tag_number"));
        formDO.setDisposition(rs.getString("disposition"));
        formDO.setDvmResourceKey(rs.getInt("dvm_resource_key"));
        formDO.setDvmResourceName(rs.getString("dvm_resource_name"));
        formDO.setPetMemo(rs.getString("pet_memo"));
        formDO.setActiveId(rs.getBoolean("active_id"));
        formDO.setDocumentServerURL(getPropertyService().getCustomerProperties(request, "fileupload.virtual.directory"));
        formDO.setPhotoFileName(getAttachmentService().getAttachmentPhoto(conn,
                                                      getUserStateService().getFeatureKey().byteValue(),
                                                      formDO.getKey()));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));
        
//--------------------------------------------------------------------------------
// Preserve the pet filter values while in view/edit mode.
//--------------------------------------------------------------------------------        
        if (request.getParameter("clientNameFilter") != null)
          formDO.setClientNameFilter(request.getParameter("clientNameFilter"));
        if (request.getParameter("petNameFilter") != null)
          formDO.setPetNameFilter(request.getParameter("petNameFilter"));
        if (request.getParameter("pageNo") != null)
          formDO.setCurrentPage(new Integer(request.getParameter("pageNo")));        

//--------------------------------------------------------------------------------
// Get the count of attachments associated with this PET.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));

//--------------------------------------------------------------------------------
// Look up the species and breed names give the respective keys.
//--------------------------------------------------------------------------------
        formDO.setSpeciesName(getSpeciesBreedService().getSpeciesName(conn, formDO.getSpeciesKey()));
        formDO.setBreedName(getSpeciesBreedService().getBreedName(conn, formDO.getBreedKey()));

//--------------------------------------------------------------------------------
// Get the pet's age.
//--------------------------------------------------------------------------------
        formDO.setPetAge(getAttributeToService().getAttributeToAgeCode(conn,
                                                                       new Byte("4"),
                                                                       formDO.getKey()));
      }
      else {
        throw new Exception("Pet " + pKey.toString() + " not found!");
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
    return formDO;
  }


    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

    // Default target to success
    String target = "success";

    EmployeesActionMapping employeesMapping =
      (EmployeesActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.login.required"));
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form.
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
//--------------------------------------------------------------------------------
// Forward to the request to the login screen.
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        PetDO formDO = buildPetForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("pet", formDO);
        PetForm formStr = new PetForm();
        formStr.populate(formDO);

        form = formStr;
      }
      catch (FormattingException fe) {
            fe.getMessage();
        }

      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
      }
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("errors.database.error", e.getMessage()));
//--------------------------------------------------------------------------------
// Report any ActionMessages
//--------------------------------------------------------------------------------
      if (!actionMessages.isEmpty()) {
        saveMessages(request, actionMessages);
      }
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}