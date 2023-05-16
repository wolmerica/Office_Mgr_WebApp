/*
 * PdfBoardingFormAction.java
 *
 * Created on September 19, 2007, 7:00 PM
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
import com.wolmerica.bundledetail.BundleDetailDO;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.speciesbreed.SpeciesBreedService;
import com.wolmerica.service.speciesbreed.DefaultSpeciesBreedService;
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


public class PdfBoardingFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 10;
  int termSize = 9;
  int timeSize = 8;

  String fontType = FontFactory.TIMES_ROMAN;

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private SpeciesBreedService speciesBreedService = new DefaultSpeciesBreedService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
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

  
  public void doReportGenerator(HttpServletRequest request,
                                HttpServletResponse response,
                                Integer schKey,
                                String presentationType)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// step 1 declare variables and creation of a document object.
//--------------------------------------------------------------------------------
    DataSource ds = null;
    Connection conn = null;
    Document document = new Document();
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
// Boarding admission form
//--------------------------------------------------------------------------------
        doPageHeader(request, conn, document, "boarding.release.title");
        String ownerName = doOwnerInfo(request, conn, document, schKey);
        String petName = doPetInfo(request, conn, document, schKey);
        String checkOutDateTime = doBoardingInfo(request, conn, document, schKey);
        doBoardingQA(request, conn, document, schKey);
        doPageBody(request, conn, document);
        doPageFooter(request, conn, document);
//--------------------------------------------------------------------------------
// Advance to the next page and boarding worksheet.
//--------------------------------------------------------------------------------
        document.add(Chunk.NEXTPAGE);
        doPageHeader(request, conn, document, "boarding.worksheet.title");
        doPetOwnerInfo(request, conn, document, petName, ownerName, checkOutDateTime);
        doPetDietMedInfo(request, conn, document);
        doAdditionalInfo(request, conn, document, schKey, "boarding.worksheet.title1", "boarding.worksheet.bundle1");
        doAdditionalInfo(request, conn, document, schKey, "boarding.worksheet.title2", "boarding.worksheet.bundle2");
        doAdditionalInfo(request, conn, document, schKey, "boarding.worksheet.title3", "boarding.worksheet.bundle3");
        doOtherInfo(request, conn, document);
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
                            Document document,
                            String boardingTitle)
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
        int headerwidths[] = { 15,15,40,10,20 }; // percentage
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
// Add four blank lines at the top of the petexam.
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
//--------------------------------------------------------------------------------
// Insert the titleTable into the datatable.
//--------------------------------------------------------------------------------
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
// Display the boarding title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,boardingTitle), FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Format the vaccination and expiration date values.
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      String sDate = df.format(new Date());
//--------------------------------------------------------------------------------
// Display the current date.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.date")
              + " " + sDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
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


  private String doOwnerInfo(HttpServletRequest request,
                             Connection conn,
                             Document document,
                             Integer schKey)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

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

    try {
//--------------------------------------------------------------------------------
// Collect information about the pet owner.
//--------------------------------------------------------------------------------
      String query = "SELECT acct_name,"
                   + "address,"
                   + "address2,"
                   + "city,"
                   + "state,"
                   + "zip,"
                   + "phone_num "
                   + "FROM customer "
                   + "WHERE thekey IN (SELECT customer_key "
                                    + "FROM schedule "
                                    + "WHERE thekey = ?)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();
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

      try {
        PdfPTable datatable = new PdfPTable(4);
        int headerwidths[] = { 10,40,8,42 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the "Owner" line
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.owner"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Display the phone label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.phone"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Display the address label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.address"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
        cell.setColspan(2);
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
// Skip this field
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the Street label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.street"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip this field
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the City, State, Zip label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.citystatezip"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip this field
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
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
    return ownerName;
  }

  private String doPetInfo(HttpServletRequest request,
                           Connection conn,
                           Document document,
                           Integer schKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

//--------------------------------------------------------------------------------
// Define the pet variables.
//--------------------------------------------------------------------------------
    String petName = "";
    String petSpecies = "";
    String petBreed =  "";
    String petColor = "";
    String petSex = "Male";
    String petAge = "";
    Date birthDate = null;
    String petBirthDate = "";

    try {
//--------------------------------------------------------------------------------
// Query to retrieve the pet information.
//--------------------------------------------------------------------------------
      String query = "SELECT name,"
                   + "species_key,"
                   + "breed_key,"
                   + "color,"
                   + "sex_id,"
                   + "(YEAR(CURDATE())-YEAR(birth_date)) "
                   + " - (RIGHT(CURDATE(),5)<RIGHT(birth_date,5)) AS age, "
                   + "birth_date "
                   + "FROM pet "
                   + "WHERE pet.thekey IN (SELECT source_key "
                                        + "FROM schedule "
                                        + "WHERE thekey = ?)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Collect the pet values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        petName = rs.getString("name");
        petSpecies = getSpeciesBreedService().getSpeciesName(conn, rs.getInt("species_key"));
        petBreed = getSpeciesBreedService().getBreedName(conn, rs.getInt("breed_key"));
	petColor = rs.getString("color");
	if (rs.getBoolean("sex_id"))
          petSex = "Female";
	petAge = rs.getString("age");
	birthDate = rs.getDate("birth_date");
      } else {
        throw new Exception("Unable to retrieve PET details.");
      }
//--------------------------------------------------------------------------------
// Format the birthday value to mm/dd/yyyy
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      petBirthDate = df.format(birthDate);

      try {
        PdfPTable datatable = new PdfPTable(7);
        int headerwidths[] = { 10,17,12,20,17,8,16 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the "Pet ID" line
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.petid"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet.name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the species.species_name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petSpecies, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the breed.breed_name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petBreed, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet.color value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petColor, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet.sex_id value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petSex, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the calculated pet age and pet.birth_date value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petAge + getPropertyService().getCustomerProperties(request,"boarding.release.year") + " " + petBirthDate , FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// "Pet ID" line
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// pet.name label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.name"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// pet.species_name label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.species"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// pet.breed_name label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.breed"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// pet.color label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.color"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// pet.sex_id label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.sex"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the calculated pet age and pet.birth_date value
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.birthdate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
    return petName;
  }

  private String doBoardingInfo(HttpServletRequest request,
                                Connection conn,
                                Document document,
                                Integer schKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

//--------------------------------------------------------------------------------
// Define string variables for boarding.
//--------------------------------------------------------------------------------
    String emptyString = getPropertyService().getCustomerProperties(request,"boarding.release.emptystring");
    String checkInDateTime = "";
    String checkOutDateTime = "";
    String checkOutTo = emptyString;
    String boardReason = emptyString;
    String boardInstruct = emptyString + emptyString + emptyString + emptyString
                         + emptyString + emptyString + emptyString + emptyString
                         + emptyString + emptyString + emptyString;
    String emergencyName = emptyString;
    String emergencyPhone = emptyString;
    DateFormatter df = new DateFormatter();

    try {
//--------------------------------------------------------------------------------
// Collect pet boarding values related to the Q&A section.
//--------------------------------------------------------------------------------
      String query = "SELECT DATE(schedule.start_stamp) AS check_in_date,"
                   + "HOUR(schedule.start_stamp) AS check_in_hour,"
                   + "MINUTE(schedule.start_stamp) AS check_in_minute,"
                   + "check_out_to,"
                   + "board_reason,"
                   + "board_instruction,"
                   + "emergency_name,"
                   + "emergency_phone "
                   + "FROM schedule "
                   + "LEFT JOIN petboarding "
                   + "ON petboarding.thekey = schedule.thekey "
                   + "WHERE schedule.thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Populate the boarding values if they exist.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        checkInDateTime = df.format(rs.getDate("check_in_date")) + " "
                        + formatTime(rs.getInt("check_in_hour"),
                                     rs.getInt("check_in_minute"));
        if (rs.getString("check_out_to") != null)
          checkOutTo =  rs.getString("check_out_to");
        if (rs.getString("board_reason") != null)
          boardReason = rs.getString("board_reason");
        if (rs.getString("board_instruction") != null)
          boardInstruct = rs.getString("board_instruction");
        if (rs.getString("emergency_name") != null)
          emergencyName = rs.getString("emergency_name");
        if (rs.getString("emergency_phone") != null)
          emergencyPhone = rs.getString("emergency_phone");
      }

//--------------------------------------------------------------------------------
// Get the check-out date, hour, and minute values from work order record.
//--------------------------------------------------------------------------------
      query = "SELECT DATE(MAX(end_stamp)) AS check_out_date,"
            + "HOUR(MAX(end_stamp)) AS check_out_hour,"
            + "MINUTE(MAX(end_stamp)) AS check_out_minute "
            + "FROM workorder "
            + "WHERE schedule_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Populate the boarding check out values if they exist.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        checkOutDateTime = df.format(rs.getDate("check_out_date")) + " "
                         + formatTime(rs.getInt("check_out_hour"),
                                      rs.getInt("check_out_minute"));
      }

      try {
//--------------------------------------------------------------------------------
// Define a two column table for the body.
//--------------------------------------------------------------------------------
        PdfPTable datatable = new PdfPTable(4);
        int headerwidths[] = { 20,20,20,30 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        PdfPTable subtable = new PdfPTable(2);
        int subwidths[] = { 20,20 }; // percentage
        subtable.setWidths(subwidths);
        subtable.setWidthPercentage(40); // percentage
        subtable.getDefaultCell().setPadding(1);
        subtable.getDefaultCell().setBorderWidth(0);
        subtable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-in date and time label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.datetimein"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-in date and time value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(checkInDateTime, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Boarding instructions label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.instruction"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-out date and time label.  (subtable)
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.datetimeout"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-out date and time value.  (subtable)
//--------------------------------------------------------------------------------
        chunk = new Chunk(checkOutDateTime, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-out to name label.  (subtable)
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.dischargeto"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-out to name value.  (subtable)
//--------------------------------------------------------------------------------
        chunk = new Chunk(checkOutTo, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Other than owner label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.other"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Insert the subtable into the datatable.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(subtable);
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Boarding instructions value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(boardInstruct, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Emergency header.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.eheading"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Emergency contact name label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.ename"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Emergency contact name.
//--------------------------------------------------------------------------------
        chunk = new Chunk(emergencyName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Emergency phone.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.ephone"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Emergency phone.
//--------------------------------------------------------------------------------
        chunk = new Chunk(emergencyPhone, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
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
    return checkOutDateTime;
  }


  private void doBoardingQA(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            Integer schKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect pet boarding values related to the Q&A section.
//--------------------------------------------------------------------------------
      String query = "SELECT vaccination_id,"
                   + "special_diet_id,"
                   + "medication_id,"
                   + "service_id "
                   + "FROM petboarding "
                   + "WHERE schedule_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();

      String onString = getPropertyService().getCustomerProperties(request,"boarding.release.onstring");
      String offString = getPropertyService().getCustomerProperties(request,"boarding.release.offstring");
      String vacYes = offString;
      String vacNo = offString;
      String dietYes = offString;
      String dietNo = offString;
      String medYes = offString;
      String medNo = offString;
      String serviceYes = offString;
      String serviceNo = offString;

      if (rs.next()) {
        if (rs.getBoolean("vaccination_id"))
          vacYes = onString;
        else
          vacNo = onString;
        if (rs.getBoolean("special_diet_id"))
          dietYes = onString;
        else
          dietNo = onString;
        if (rs.getBoolean("medication_id"))
          medYes = onString;
        else
          medNo = onString;
        if (rs.getBoolean("service_id"))
          serviceYes = onString;
        else
          serviceNo = onString;
      }

      try {
//--------------------------------------------------------------------------------
// Define a two column table for the body.
//--------------------------------------------------------------------------------
        PdfPTable datatable = new PdfPTable(3);
        int headerwidths[] = { 50,15,15 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(80); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Release response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.response"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Vaccination line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.vaccination"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Yes response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.yes") + vacYes, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// No response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.no") + vacNo, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Diet line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.diet"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Yes response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.yes") + dietYes, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// No response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.no") + dietNo, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Diet note line.
//--------------------------------------------------------------------------------
        chunk = new Chunk("     " + getPropertyService().getCustomerProperties(request,"boarding.release.dietnote"), FontFactory.getFont(fontType, fontSize, Font.ITALIC));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Medication line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.medication"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Yes response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.yes") + medYes, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// No response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.no") + medNo, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Service line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.service"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Yes response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.yes") + serviceYes, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// No response.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.no") + serviceNo, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
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


  private void doPageBody(HttpServletRequest request,
                          Connection conn,
                          Document document)
   throws Exception, IOException, SQLException {

    try {
//--------------------------------------------------------------------------------
// Define a two column table for the body.
//--------------------------------------------------------------------------------
      PdfPTable datatable = new PdfPTable(2);
      int headerwidths[] = { 48,47 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(95); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;
//--------------------------------------------------------------------------------
// process page one
//--------------------------------------------------------------------------------
      Integer messageCount = new Integer("0");
      String messageKey = "boarding.release.count";
      String message = "";
      Integer messageMax = new Integer(getPropertyService().getCustomerProperties(request,messageKey));
      int fontStyle = Font.NORMAL;
      while (++messageCount <= messageMax)
      {
        messageKey = "boarding.release.section" + messageCount;
        message = getPropertyService().getCustomerProperties(request,messageKey);
        if (!(message.equalsIgnoreCase(messageKey)))
        {
//--------------------------------------------------------------------------------
// sections with puppy proofing recommendations.
//--------------------------------------------------------------------------------
          chunk = new Chunk("     " + message, FontFactory.getFont(fontType, fontSize, fontStyle));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);
        }
      }
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Signature line.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.signature"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Signature label line.
//--------------------------------------------------------------------------------
      chunk = new Chunk("     " + getPropertyService().getCustomerProperties(request,"boarding.release.signaturelabel"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Footnote.
//--------------------------------------------------------------------------------
      chunk = new Chunk("      " + getPropertyService().getCustomerProperties(request,"boarding.release.footnote"), FontFactory.getFont(fontType, termSize, Font.NORMAL));
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


  private void doPageFooter(HttpServletRequest request,
                            Connection conn,
                            Document document)
   throws Exception, IOException, SQLException {
//--------------------------------------------------------------------------------
// Nothing defined for the footer.
//--------------------------------------------------------------------------------

  }


  public String formatTime(int eventHour,
                           int eventMinute)
   throws Exception, IOException {

    String timeOfDay = "            .";
    if (eventHour > 0) {
      timeOfDay = " am";
      if (eventHour > 11)
        timeOfDay = " pm";
      if (eventHour > 12)
        eventHour = eventHour - 12;
      if (eventMinute < 10)
        timeOfDay = eventHour + ":0" + eventMinute + timeOfDay;
      else
        timeOfDay = eventHour + ":" + eventMinute + timeOfDay;
    }

    return timeOfDay;
  }


  private void doPetOwnerInfo(HttpServletRequest request,
                              Connection conn,
                              Document document,
                              String petName,
                              String ownerName,
                              String checkOutDateTime)
   throws Exception, IOException {

    try {
      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 10,40,8,42 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the "Pet ID" line
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.petid"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet.name value
//--------------------------------------------------------------------------------
      chunk = new Chunk(petName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the "Owner" line
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.owner"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Check-out date and time label.  (subtable)
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.release.datetimeout"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Check-out date and time value.  (subtable)
//--------------------------------------------------------------------------------
      chunk = new Chunk(checkOutDateTime, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }

  private void doPetDietMedInfo(HttpServletRequest request,
                                Connection conn,
                                Document document)
   throws Exception, IOException {

    try {
//--------------------------------------------------------------------------------
// Set time of day string value.
//--------------------------------------------------------------------------------
      String timeOfDay = getPropertyService().getCustomerProperties(request,"boarding.worksheet.am");
      timeOfDay = timeOfDay + "/" + getPropertyService().getCustomerProperties(request,"boarding.worksheet.pm");
        
      PdfPTable datatable = new PdfPTable(8);
      int headerwidths[] = { 14,12,12,12,12,12,12,14 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(1);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Display the special diet label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.diet"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the special diet value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.dietvalue"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the diet instruction label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.instruction"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(1);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the diet instruction value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.instructionvalue1"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the diet instruction value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.instructionvalue2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(8);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the medication label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.medication"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the medication value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.medicationvalue"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the treatment label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.treatment"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(8);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the drug label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.drug"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the drug value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.drugvalue"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the dose label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.dose"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the dose value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.dosevalue"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the supply label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.supply"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(5);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the supply value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.supplyvalue"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip a row.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(8);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the day/date label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.daydate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the timerx label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.timerx"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.by"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the timerx label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.timerx"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.by"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the timerx label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.timerx"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.by"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip last column in table.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);

      Integer x = 0;
      for (x=1; x<8; x++) {
//--------------------------------------------------------------------------------
// Display the day/date value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(x.toString() + ".", FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the time of day value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(timeOfDay, FontFactory.getFont(fontType, timeSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the time of day value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(timeOfDay, FontFactory.getFont(fontType, timeSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);        
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the time of day value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(timeOfDay, FontFactory.getFont(fontType, timeSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);        
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip last column in table.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
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


  private void doAdditionalInfo(HttpServletRequest request,
                                Connection conn,
                                Document document,
                                Integer schKey,
                                String bundleTitle,
                                String bundleName)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

//--------------------------------------------------------------------------------
// Get the bundle title and name values.
//--------------------------------------------------------------------------------
    bundleTitle = getPropertyService().getCustomerProperties(request, bundleTitle);
    bundleName = getPropertyService().getCustomerProperties(request, bundleName);
    BundleDetailDO bundleDetail = null;
    DateFormatter df = new DateFormatter();

    try {
//--------------------------------------------------------------------------------
// Collect the bundle information.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "bundle_key,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "order_qty "
                   + "FROM bundledetail "
                   + "WHERE bundle_key IN (SELECT thekey "
                                        + "FROM bundle "
                                        + "WHERE name = ?)";
      ps = conn.prepareStatement(query);
      ps.setString(1, bundleName);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Check the workorder for details regarding services rendered for the appointment.
//--------------------------------------------------------------------------------
      query = "SELECT DATE(start_stamp) AS start_date,"
            + "resource.name AS resource_name "
            + "FROM workorder "
            + "LEFT JOIN resourceinstance "
            + "ON resourceinstance.workorder_key = workorder.thekey "
            + "LEFT JOIN resource "
            + "ON resource.thekey = resourceinstance.resource_key "
            + "WHERE workorder.schedule_key = ? "
            + "AND workorder.sourcetype_key = ? "
            + "AND workorder.source_key = ?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Define the Pdf table.
//--------------------------------------------------------------------------------
      try {
        PdfPTable datatable = new PdfPTable(8);
        int headerwidths[] = { 20,10,8,7,20,10,8,7 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Skip eight columns for a blank row.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ",  FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(8);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the additional header.
//--------------------------------------------------------------------------------
        chunk = new Chunk(bundleTitle,  FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the day/date label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.daydate"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.by"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the currency label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.currency"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display a blank additional header.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ",  FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the day/date label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.daydate"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.by"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the currency label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.currency"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Collect the bundle detail values.
//--------------------------------------------------------------------------------
        String defaultDate = getPropertyService().getCustomerProperties(request,"boarding.worksheet.byvalue");
        String defaultBy = getPropertyService().getCustomerProperties(request,"boarding.worksheet.byvalue");
        String defaultCost= getPropertyService().getCustomerProperties(request,"boarding.worksheet.currencyvalue");
        String dateValue = "";
        String byValue = "";
        String costValue = "";
        while (rs.next()) {
          bundleDetail = new BundleDetailDO();
          bundleDetail.setKey(rs.getInt("thekey"));
          bundleDetail.setBundleKey(rs.getInt("bundle_key"));
          bundleDetail.setSourceTypeKey(rs.getByte("sourcetype_key"));
          bundleDetail.setSourceKey(rs.getInt("source_key"));
          bundleDetail.setOrderQty(rs.getShort("order_qty"));

          bundleDetail = getItemAndSrvService().getItemOrServiceName(conn, bundleDetail);

//--------------------------------------------------------------------------------
// Default the set the date, by, and cost values
//--------------------------------------------------------------------------------
          dateValue = defaultDate;
          byValue = defaultBy;
          costValue = defaultCost;
          ps.setInt(1, schKey);
          ps.setByte(2, bundleDetail.getSourceTypeKey());
          ps.setInt(3, bundleDetail.getSourceKey());
          rs2 = ps.executeQuery();
          if (rs2.next()) {
            dateValue = df.format(rs2.getDate("start_date"));
            if (rs2.getString("resource_name") != null)
              byValue = rs2.getString("resource_name");
          }

//--------------------------------------------------------------------------------
// Display the service name.
//--------------------------------------------------------------------------------
          chunk = new Chunk(bundleDetail.getSourceName(), FontFactory.getFont(fontType, termSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the day/date value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(dateValue, FontFactory.getFont(fontType, termSize, Font.NORMAL));
          if (dateValue.compareToIgnoreCase(defaultDate) != 0)
            chunk.setUnderline(0.2f, -2f);          
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the by value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(byValue, FontFactory.getFont(fontType, termSize, Font.NORMAL));
          if (byValue.compareToIgnoreCase(defaultBy) != 0)
            chunk.setUnderline(0.2f, -2f);          
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the currency value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(costValue, FontFactory.getFont(fontType, termSize, Font.NORMAL));
          if (costValue.compareToIgnoreCase(defaultCost) != 0)
            chunk.setUnderline(0.2f, -2f);  
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          datatable.addCell(cell);
        }
//--------------------------------------------------------------------------------
// Skip four columns to finish an incomplete row.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ",  FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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


  private void doOtherInfo(HttpServletRequest request,
                           Connection conn,
                           Document document)
   throws Exception, IOException {

    try {
      PdfPTable datatable = new PdfPTable(8);
      int headerwidths[] = { 22,8,8,7,22,8,8,7 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(90); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(1);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Skip eight columns for a blank row.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ",  FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(8);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the other label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.other"), FontFactory.getFont(fontType, termSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the other value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.othervalue"), FontFactory.getFont(fontType, termSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(7);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the item label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.item"), FontFactory.getFont(fontType, termSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the item value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"boarding.worksheet.itemvalue"), FontFactory.getFont(fontType, termSize, Font.NORMAL));
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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

      String theType = null;
      if (!(request.getParameter("presentationType") == null)) {
        theType = new String(request.getParameter("presentationType"));
      }
      else {
        throw new Exception("Request getParameter [presentationType] not found!");
      }


      doReportGenerator(request, response, theKey, theType);
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
