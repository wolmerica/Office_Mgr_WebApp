/*
 * PdfNeuterFormAction.java
 *
 * Created on July 13, 2007, 9:25 PM
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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.log4j.Logger;


public class PdfNeuterFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 8;
  int termSize = 10;
  String fontType = FontFactory.TIMES_ROMAN;

  private PropertyService propertyService = new DefaultPropertyService();
  private SpeciesBreedService speciesBreedService = new DefaultSpeciesBreedService();
  private UserStateService userStateService = new DefaultUserStateService();

  public SpeciesBreedService getSpeciesBreedService() {
      return speciesBreedService;
  }

  public void setSpeciesBreedService(SpeciesBreedService speciesBreedService) {
      this.speciesBreedService = speciesBreedService;
  }

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
                                Integer petKey,
                                String presentationType)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// step 1 declare variables and creation of a document object.
//--------------------------------------------------------------------------------
    DataSource ds = null;
    Connection conn = null;
    Rectangle pageSize = new Rectangle(450, 525);
//    pageSize.setBackgroundColor(new java.awt.Color(0xFF, 0xFF, 0xDE));
    Document document = new Document(pageSize);
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
        doNeuterInfo(request, conn, document, petKey);
        doPetInfo(request, conn, document, petKey);
        doOwnerInfo(request, conn, document, petKey);
        doPageFooter(request, conn, document, petKey);
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
// Display the neuter form title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
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
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.date")
              + " " + sDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        cell.setHorizontalAlignment(PdfPTable.ALIGN_RIGHT);
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

  private void doNeuterInfo(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            Integer petKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect information about the neutered date.
//--------------------------------------------------------------------------------
      String query = "SELECT neutered_date "
                   + "FROM pet "
                   + "WHERE pet.thekey = ? "
                   + "AND neutered_id";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the neutered date variables.
//--------------------------------------------------------------------------------
      Date neuteredDate = new Date();
      String petNeuteredDate = "";
//--------------------------------------------------------------------------------
// Collect the pet values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        neuteredDate = rs.getDate("neutered_date");
      }
      else {
        throw new Exception("Unable to retrieve NEUTERED PET details.");
      }

//--------------------------------------------------------------------------------
// Format the neutered date value to mm/dd/yyyy
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      petNeuteredDate = df.format(neuteredDate);


      try {
        PdfPTable datatable = new PdfPTable(2);
        int headerwidths[] = { 25,75 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the certify statement
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.certify"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the neutered date value
//--------------------------------------------------------------------------------
        chunk = new Chunk("      " + petNeuteredDate + "     .", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip one line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the first line of the section.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.section1"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the second line of the section.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.section2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the third line of the section.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.section3"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
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

  private void doPetInfo(HttpServletRequest request,
                         Connection conn,
                         Document document,
                         Integer petKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Query to retrieve pet details including the age at the time of neutering in months.
//--------------------------------------------------------------------------------
      String query = "SELECT name,"
                   + "identification_tag_number,"
                   + "species_key,"
                   + "breed_key,"
                   + "(YEAR(neutered_date) * 12 + MONTH(neutered_date)) "
                   + " - (YEAR(birth_date) * 12 + MONTH(birth_date)) as age,"
                   + "color,"
                   + "birth_date "
                   + "FROM pet "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet variables.
//--------------------------------------------------------------------------------
      String petName = "";
      String petTagNumber = "";
      String petSpecies = "";
      String petBreed =  "";
      String petAge = "";
      String petColor = "";
      Date birthDate = null;
      String petBirthDate = "";
//--------------------------------------------------------------------------------
// Collect the pet values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        petName = rs.getString("name");
        petTagNumber = rs.getString("identification_tag_number");
        petSpecies = getSpeciesBreedService().getSpeciesName(conn, rs.getInt("species_key"));
        petBreed = getSpeciesBreedService().getBreedName(conn, rs.getInt("breed_key"));
        petAge = rs.getString("age");
        petColor = rs.getString("color");
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
        PdfPTable datatable = new PdfPTable(6);
        int headerwidths[] = { 10,23,10,24,10,23 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip one line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet name title
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.name"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet tag title
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.tag"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet tag value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petTagNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip one line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet species title
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.species"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet species value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petSpecies, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet breed title
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.breed"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet breed value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petBreed, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet age title
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.age"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the calculated pet age and pet.birth_date value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petAge + " " + getPropertyService().getCustomerProperties(request,"neuter.form.month") + " " + petBirthDate , FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip one line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet.color title
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.color"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the pet.color value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petColor, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
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

  private void doOwnerInfo(HttpServletRequest request,
                           Connection conn,
                           Document document,
                           Integer petKey)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect information about the pet owner.
//--------------------------------------------------------------------------------
      String query = "SELECT customer.acct_name,"
                   + "customer.address,"
                   + "customer.address2,"
                   + "customer.city,"
                   + "customer.state,"
                   + "customer.zip,"
                   + "customer.phone_num "
                   + "FROM pet, customer "
                   + "WHERE pet.thekey = ? "
                   + "AND customer.thekey = customer_key";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petKey);
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
      }
      else {
        throw new Exception("Unable to retrieve OWNER details.");
      }

      try {
        PdfPTable datatable = new PdfPTable(3);
        int headerwidths[] = { 20,20,60 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip one line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the "Owned By" line
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.owner"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the address label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.address"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner address value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerAddress + " " + ownerAddress2, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first cell.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner city, state, and zip value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerCity + ", " + ownerState + " " + ownerZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip one line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first cell.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the Signed label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"neuter.form.signed"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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


  private void doPageFooter(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            Integer petKey)
   throws Exception, IOException, SQLException {
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
