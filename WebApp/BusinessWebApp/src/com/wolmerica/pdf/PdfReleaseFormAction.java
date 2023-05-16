/*
 * PdfReleaseFormAction.java
 *
 * Created on August 26, 2007, 7:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.pdf;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;

import java.io.IOException;
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
import java.util.Date;
import java.net.URL;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.log4j.Logger;


public class PdfReleaseFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 12;
  int titleSize = 14;
  String fontType = FontFactory.TIMES_ROMAN;

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public void doReportGenerator(HttpServletRequest request,
                                HttpServletResponse response,
                                Integer customerKey,
                                Byte sourceTypeKey,
                                Integer sourceKey,
                                Integer scheduleKey,
                                String presentationType)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// step 1 declare variables and creation of a document object.
//--------------------------------------------------------------------------------
    Document document = new Document();

    DataSource ds = null;
    Connection conn = null;

    try {
//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        cat.debug("No request attribute to set inside PDF reports.");
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      ds = getDataSource(request);
      conn = ds.getConnection();
      try {
//--------------------------------------------------------------------------------
// step 2: set the ContentType to pdf, htlm, or rtf.
//--------------------------------------------------------------------------------
        if (presentationType.equals("pdf")) {
          response.setContentType("application/pdf");
          PdfWriter.getInstance(document, response.getOutputStream());
        }
        else {
          throw new Exception("Invalid presentationType value!");
        }
//--------------------------------------------------------------------------------
// step 2.5 establish header.
// Too many issues with headers and page numbers.
// It's easier to handle this behavior on our own.
//--------------------------------------------------------------------------------
        document.addCreator(createUser);
        document.addCreationDate();
//--------------------------------------------------------------------------------
// step 3
//--------------------------------------------------------------------------------
        document.open();
//--------------------------------------------------------------------------------
// step 4
//--------------------------------------------------------------------------------
        doPageHeader(request, conn, document);
        doAttributeToInfo(request, conn, document, customerKey, sourceTypeKey, sourceKey);
        doPageBodyBegin(request, conn, document);
        doServiceInfo(request, conn, document, scheduleKey);
        doPageBodyEnd(request, conn, document);
        doPageFooter(request, conn, document);
      }
      catch(DocumentException de) {
      de.printStackTrace();
      cat.debug(this.getClass().getName() + ": document: " + de.getMessage());
    }
//--------------------------------------------------------------------------------
// step 10: we close the document (the outputstream is also closed internally)
//--------------------------------------------------------------------------------
    document.close();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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
  }

  private void doPageHeader(HttpServletRequest request,
                            Connection conn,
                            Document document)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect customer and customer invoice specific information for the header.
//--------------------------------------------------------------------------------
      String query = "SELECT customer.acct_name,"
                   + "customer.address,"
                   + "customer.address2,"
                   + "customer.city,"
                   + "customer.state,"
                   + "customer.zip,"
                   + "customer.phone_num "
                   + "FROM customer, customertype "
                   + "WHERE customer.thekey = customertype.sold_by_key "
                   + "AND customertype.thekey = 1 "
                   + "AND clinic_id";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      String sellerName = "";
      String sellerAddress = "";
      String sellerAddress2 = "";
      String sellerCity = "";
      String sellerState = "";
      String sellerZip = "";
      String sellerPhone = "";

      if (rs.next()) {
        sellerName = rs.getString("acct_name");
	sellerAddress = rs.getString("address");
	sellerAddress2 = rs.getString("address2");
	sellerCity = rs.getString("city");
	sellerState = rs.getString("state");
	sellerZip = rs.getString("zip");
	sellerPhone = pnf.format(rs.getString("phone_num"));
      } else {
        throw new Exception("Unable to retrieve SOLD BY details.");
      }

      try {
        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = { 15,15,30,10,20 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        PdfPTable titleTable = new PdfPTable(1);
        int titlewidths[] = { 40 }; // percentage
        titleTable.setWidths(titlewidths);
        titleTable.setWidthPercentage(40); // percentage
        titleTable.getDefaultCell().setPadding(1);
        titleTable.getDefaultCell().setBorderWidth(0);
        titleTable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
//--------------------------------------------------------------------------------
// Define a cell to allow data to span across multiple cells
//--------------------------------------------------------------------------------
        PdfPCell cell = new PdfPCell(phrase);
//--------------------------------------------------------------------------------
// Add four blank lines at the top of the invoice.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(""));
        cell.setBorder(0);
        cell.setColspan(5);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
        datatable.addCell(cell);
        datatable.addCell(cell);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Begin listing the seller name with the invoice header to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerName, FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Seller address with invoice number and invoice date value to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerAddress, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Seller city, state, and zip code with invoice number and date to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerCity + ", " + sellerState + " " + sellerZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Seller phone number.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerPhone, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Insert the company logo at the top of the invoice.
// as though it resides in the webapp folder, making the relative
// reference to the images subdirectory work.
//--------------------------------------------------------------------------------
        String p_strCurrentURL = new String(request.getRequestURL());
        Image companyLogo = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/Business_Logo.gif"));
        companyLogo.scalePercent(70,70);
        cell = new PdfPCell(companyLogo);
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

        cell = new PdfPCell(titleTable);
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Blank line before the title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the release form title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Format the vaccination and expiration date values.
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      String sDate = df.format(new Date());
//--------------------------------------------------------------------------------
// Display the current date.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.date")
              + " " + sDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);

        document.add(datatable);
      }
      catch(DocumentException de) {
        de.printStackTrace();
        cat.error(this.getClass().getName() + ": document = " + de.getMessage());
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
    }
  }

  private void doAttributeToInfo(HttpServletRequest request,
                                 Connection conn,
                                 Document document,
                                 Integer customerKey,
                                 Byte sourceTypeKey,
                                 Integer sourceKey)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect information about the owner.
//--------------------------------------------------------------------------------
      String query = "SELECT customer.acct_name,"
                   + "customer.address,"
                   + "customer.address2,"
                   + "customer.city,"
                   + "customer.state,"
                   + "customer.zip,"
                   + "customer.phone_num "
                   + "FROM customer "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the owner variables.
//--------------------------------------------------------------------------------
      String ownerName = "";
      String ownerAddress = "";
      String ownerAddress2 = "";
      String ownerCity = "";
      String ownerState = "";
      String ownerZip = "";
      String ownerPhone = "";
//--------------------------------------------------------------------------------
// Collect the owner values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        ownerName = rs.getString("acct_name");
        ownerAddress = rs.getString("address");
        ownerAddress2 = rs.getString("address2");
        ownerCity = rs.getString("city");
        ownerState = rs.getString("state");
        ownerZip = rs.getString("zip");
        ownerPhone = pnf.format(rs.getString("phone_num"));
      } else {
        throw new Exception("Unable to retrieve OWNER details.");
      }

//--------------------------------------------------------------------------------
// Query to retrieve the page body.
//--------------------------------------------------------------------------------
      switch(sourceTypeKey)
        {
        /* Pet */
        case 4: query = "SELECT pet.name,"
                      + "breed_name as name2,"
                      + "species_name as name3,"                 
                      + "color,"
                      + "sex_id,"
                      + "(YEAR(CURDATE())-YEAR(birth_date)) "
                      + " - (RIGHT(CURDATE(),5)<RIGHT(birth_date,5)) AS age,"
                      + "birth_date AS create_date "
                      + "FROM pet LEFT JOIN species ON (species.thekey = pet.species_key) "
                      + "LEFT JOIN breed ON (breed.thekey = pet.breed_key) "
                      + "WHERE pet.thekey = ?";
                break;
        /* System */
        case 24: query = "SELECT name,"
                       + "processor AS name2,"
                       + "' ' AS name3,"
                       + "'none' AS color,"
                       + "false AS sex_id,"
                       + "(YEAR(CURDATE())-YEAR(system_date)) "
                       + " - (RIGHT(CURDATE(),5)<RIGHT(system_date,5)) AS age,"
                       + "system_date AS create_date "
                       + "FROM system "
                       + "WHERE thekey = ?";
                break;
        /* Vehicle */
        case 31: query = "SELECT vehicle.year AS name,"
	                   + "vehicle.make AS name2,"
                       + "vehicle.model AS name3,"
                       + "color,"
                       + "false AS sex_id,"
                       + "(YEAR(CURDATE())-YEAR(vehicle_date)) "
                       + " - (RIGHT(CURDATE(),5)<RIGHT(vehicle_date,5)) AS age,"
                       + "vehicle_date AS create_date "
                       + "FROM vehicle "
                       + "WHERE thekey = ?";
                break;
      }
      ps = conn.prepareStatement(query);
      ps.setInt(1, sourceKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet variables.
//--------------------------------------------------------------------------------
      String attributeToName = "";
      String attributeToName2 = "";
      String attributeToName3 =  "";
      String attributeToColor = "";
      String attributeToSex = "";
      String attributeToAge = "";
      Date createDate = null;
      String attributeToDate = "";
//--------------------------------------------------------------------------------
// Collect the attributeTo values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        attributeToName = rs.getString("name");
        attributeToName2 = rs.getString("name2");
        attributeToName3 = rs.getString("name3");
        attributeToColor = rs.getString("color");
        if (rs.getBoolean("sex_id"))
          attributeToSex = "Female";
        else
          attributeToSex = "Male";
        attributeToAge = rs.getString("age");
        createDate = rs.getDate("create_date");
      }

//--------------------------------------------------------------------------------
// Format the birthday value to mm/dd/yyyy
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      attributeToDate = df.format(createDate);

      try {
        PdfPTable datatable = new PdfPTable(6);
        int headerwidths[] = { 10,32,20,23,5,10 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the owner label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.owner"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the attributeTo name label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form."+sourceTypeKey+".name"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the attributeTo.name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(attributeToName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the address label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.address"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner address value (2 - fields)
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerAddress + " " + ownerAddress2, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the name2 label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form."+sourceTypeKey+".name2"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the attributeTo.name2 value
//--------------------------------------------------------------------------------
        chunk = new Chunk(attributeToName2, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the attributeTo.name3 value
//--------------------------------------------------------------------------------
        chunk = new Chunk(attributeToName3, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip this field
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner city, state, and zip value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerCity + ", " + ownerState + " " + ownerZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the age label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.age"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the calculated attributeTo age and attributeToDate value
//--------------------------------------------------------------------------------
        chunk = new Chunk(attributeToAge + getPropertyService().getCustomerProperties(request,"release.form.year") + " " + attributeToDate , FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the sex label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.sex"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the attributeTo.sex_id value
//--------------------------------------------------------------------------------
        chunk = new Chunk(attributeToSex, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the phone label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.phone"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner phone value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerPhone, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the color label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.color"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the attributeTo.sex_id value
//--------------------------------------------------------------------------------
        chunk = new Chunk(attributeToColor, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip this line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
        datatable.addCell(cell);
        datatable.addCell(cell);
        datatable.addCell(cell);
        datatable.addCell(cell);
        datatable.addCell(cell);

        document.add(datatable);
      }
      catch(DocumentException de) {
        de.printStackTrace();
        cat.error(this.getClass().getName() + ": document = " + de.getMessage());
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
    }
  }

  private void doPageBodyBegin(HttpServletRequest request,
                               Connection conn,
                               Document document)
   throws Exception, IOException {

    try {
//--------------------------------------------------------------------------------
// Define a two column table for the body.
//--------------------------------------------------------------------------------
      PdfPTable datatable = new PdfPTable(2);
      int headerwidths[] = { 30,60 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(90); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// section one of the body
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.section1"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// section two of the body
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.section2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }


  private void doServiceInfo(HttpServletRequest request,
                             Connection conn,
                             Document document,
                             Integer scheduleKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect information about the services assigned to the bundle for the petexam.
//--------------------------------------------------------------------------------
      String query = "SELECT servicedictionary.name,"
                   + "servicedictionary.description "
                   + "FROM workorder, customerattributebyservice, pricebyservice, servicedictionary "
                   + "WHERE servicedictionary.thekey = customerattributebyservice.servicedictionary_key "
                   + "AND servicedictionary.release_id "
                   + "AND customerattributebyservice.thekey = pricebyservice.customerattributebyservice_key "
                   + "AND pricebyservice.thekey = workorder.source_key "
                   + "AND workorder.schedule_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, scheduleKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the service variables.
//--------------------------------------------------------------------------------
      String serviceName = "";
      String serviceDescription = "";

      try {
        PdfPTable datatable = new PdfPTable(2);
        int headerwidths[] = { 7,83 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the service values for the petexam.
//--------------------------------------------------------------------------------
        while (rs.next()) {
          serviceName = rs.getString("name");
          serviceDescription = rs.getString("description");
//--------------------------------------------------------------------------------
// Skip this line.
//--------------------------------------------------------------------------------
          chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the service label.
//--------------------------------------------------------------------------------
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.indicator"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          chunk.setUnderline(0.2f, -2f);
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the service name and description
//--------------------------------------------------------------------------------
          chunk = new Chunk(serviceName + "; " + serviceDescription, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          datatable.addCell(cell);
        }

        document.add(datatable);
      }
      catch(DocumentException de) {
        de.printStackTrace();
        cat.error(this.getClass().getName() + ": document = " + de.getMessage());
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
    }
  }


  private void doPageBodyEnd(HttpServletRequest request,
                             Connection conn,
                             Document document)
   throws Exception, IOException {

    try {
//--------------------------------------------------------------------------------
// Define a two column table for the body.
//--------------------------------------------------------------------------------
      PdfPTable datatable = new PdfPTable(2);
      int headerwidths[] = { 30,60 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(90); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// section one of the body
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.section3"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// section two of the body
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.section4"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// section two of the body
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.section5"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// section two of the body
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.section6"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// put in a blank row for spacing
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// date line label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.dateline"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// signature line
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.signatureline"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// skip the first column
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// signature line label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"release.form.signaturelabel"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }


  private void doPageFooter(HttpServletRequest request,
                            Connection conn,
                            Document document)
   throws Exception, IOException {
//--------------------------------------------------------------------------------
// Nothing defined for the footer.
//--------------------------------------------------------------------------------

  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		               ActionForm form,
		               HttpServletRequest request,
		               HttpServletResponse response)
   throws Exception, IOException, SQLException {

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
      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        customerKey = new Integer(request.getParameter("customerKey"));
      }
      else {
        throw new Exception("Request getParameter [customerKey] not found!");
      }
      
      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }
      
      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceKey] not found!");
      }

      Integer scheduleKey = null;
      if (request.getParameter("scheduleKey") != null) {
        scheduleKey = new Integer(request.getParameter("scheduleKey"));
      }
      else {
        throw new Exception("Request getParameter [scheduleKey] not found!");
      }

      String theType = null;
      if (request.getParameter("presentationType") != null) {
        theType = new String(request.getParameter("presentationType"));
      }
      else {
        throw new Exception("Request getParameter [presentationType] not found!");
      }

      doReportGenerator(request,
                        response,
                        customerKey,
                        sourceTypeKey,
                        sourceKey,
                        scheduleKey,
                        theType);
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
