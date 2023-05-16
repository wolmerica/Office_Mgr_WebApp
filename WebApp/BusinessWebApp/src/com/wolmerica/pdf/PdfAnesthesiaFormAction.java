/*
 * PdfAnesthesiaFormAction.java
 *
 * Created on August 31, 2007, 9:00 PM
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
import com.wolmerica.tools.formatter.CurrencyFormatter;
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
import java.math.BigDecimal;
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


public class PdfAnesthesiaFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 11;
  int termSize = 10;
  String fontType = FontFactory.TIMES_ROMAN;

  private PropertyService propertyService = new DefaultPropertyService();
  private SpeciesBreedService speciesBreedService = new DefaultSpeciesBreedService();
  private UserStateService userStateService = new DefaultUserStateService();

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
                                Integer petExamKey,
                                Integer petKey,
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
// step 4
//--------------------------------------------------------------------------------
        doPageHeader(request, conn, document);
        doOwnerPetInfo(request, conn, document, petKey);
        doPetExamInfo(request, conn, document, petExamKey);
        doPetAnestheticInfo(request, conn, document, petExamKey, new Byte("1"));
        doPetAnestheticInfo(request, conn, document, petExamKey, new Byte("2"));
        doPetAnestheticInfo(request, conn, document, petExamKey, new Byte("3"));
        doPetVitalSignsInfo(request, conn, document, petExamKey);
        doPetExamTimeInfo(request, conn, document, petExamKey);
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
// Insert the company logo at the top of the page as though it resides in the
// webapp folder, making the relative reference to the images subdirectory work.
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
// Display the euthanasia form title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
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
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.date")
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


  private void doOwnerPetInfo(HttpServletRequest request,
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
                   + "FROM customer "
                   + "WHERE thekey IN (SELECT customer_key "
                                    + "FROM pet "
                                    + "WHERE thekey = ?)";
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
      } else {
        throw new Exception("Unable to retrieve OWNER details.");
      }

//--------------------------------------------------------------------------------
// Query to retrieve the page body.
//--------------------------------------------------------------------------------
      query = "SELECT name,"
            + "species_key,"
            + "breed_key,"
            + "color,"
            + "sex_id,"
            + "(YEAR(CURDATE())-YEAR(birth_date)) "
            + " - (RIGHT(CURDATE(),5)<RIGHT(birth_date,5)) AS age, "
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
      String petSpecies = "";
      String petBreed =  "";
      String petColor = "";
      String petSex = "Male";
      String petAge = "";
      Date birthDate = null;
      String petBirthDate = "";
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
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.owner"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Display the pet name label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.petid"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Display the phone label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.phone"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Display the breed label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.breed"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
// Display the pet color value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petColor, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
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
        cell.setColspan(6);
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


  private void doPetExamInfo(HttpServletRequest request,
                             Connection conn,
                             Document document,
                             Integer petExamKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

//--------------------------------------------------------------------------------
// Collect information about the pet exam.
//--------------------------------------------------------------------------------
      String query = "SELECT petexam.thekey,"
                   + "DATE(schedule.start_stamp) AS treatment_date,"
                   + "schedule.subject,"
                   + "r1.name AS dvm_resource_name,"
                   + "r2.name AS tech_resource_name,"
                   + "petexam.heart_rate,"
                   + "petexam.respt_rate,"
                   + "petexam.cap_refill_time,"
                   + "petexam.temperature,"
                   + "petexam.body_weight,"
                   + "petexam.general_condition,"
                   + "petexam.medical_data "
                   + "FROM customer, pet, petexam, resource r1, resource r2, schedule "
                   + "WHERE petexam.thekey = ? "
                   + "AND petexam.schedule_key = schedule.thekey "
                   + "AND schedule.source_key = pet.thekey "
                   + "AND schedule.customer_key = customer.thekey "
                   + "AND petexam.dvm_resource_key = r1.thekey "
                   + "AND petexam.tech_resource_key = r2.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petExamKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet exam variable.
//--------------------------------------------------------------------------------
      String treatmentDate = "";
      Date dateValue = null;
      String dvmName = "";
      String techName =  "";
      String subject = "";
      String heartRate = "";
      String resptRate = "";
      String capRefill = "";
      BigDecimal numValue = null;
      String temperature = "";
      String bodyWeight= "";
      String generalCondition = "";
      String medicalData = "";
//--------------------------------------------------------------------------------
// Collect the pet exam values and perform the required formatting.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        dateValue = rs.getDate("treatment_date");
//--------------------------------------------------------------------------------
// Format the date value to MM/DD/YYYY
//--------------------------------------------------------------------------------
        DateFormatter df = new DateFormatter();
        treatmentDate = df.format(dateValue);
        dvmName = rs.getString("dvm_resource_name");
        techName = rs.getString("tech_resource_name");
        subject = rs.getString("subject");
        heartRate = rs.getString("heart_rate");
        resptRate = rs.getString("respt_rate");
        capRefill = rs.getString("cap_refill_time");
        numValue = rs.getBigDecimal("temperature");
//--------------------------------------------------------------------------------
// Format the decimal value to two digit decimal.
//--------------------------------------------------------------------------------
        CurrencyFormatter cf = new CurrencyFormatter();
        temperature = cf.format(numValue);
        numValue = rs.getBigDecimal("body_weight");
        bodyWeight = cf.format(numValue);
        generalCondition = rs.getString("general_condition");
        medicalData = rs.getString("medical_data");
      } else {
        throw new Exception("Unable to retrieve PET EXAM details.");
      }

      try {
        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = { 15,25,10,20,20 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        PdfPTable subtable = new PdfPTable(3);
        int titlewidths[] = { 20,10,10 }; // percentage
        subtable.setWidths(titlewidths);
        subtable.setWidthPercentage(40); // percentage
        subtable.getDefaultCell().setPadding(1);
        subtable.getDefaultCell().setBorderWidth(0);
        subtable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
//--------------------------------------------------------------------------------
// Define a cell to allow data to span across multiple cells
//--------------------------------------------------------------------------------
        PdfPCell cell = new PdfPCell(phrase);
//--------------------------------------------------------------------------------
// Exam date label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.examdate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Exam date value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(treatmentDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// DVM and tech label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.resource"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// DVM and tech value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(dvmName + " / " + techName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip one row.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Preanesthetic exam label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.examtitle"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Medical data label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.medicaldata"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Subtable to display to the left of the medical data.
// Heart rate label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.heartrate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Heart rate value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(heartRate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Respitory rate label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.resptrate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Respitory rate value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(resptRate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Cap refill rate label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.caprefill"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Cap refill rate value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(capRefill, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Temperature label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.temperature"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Temperature value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(temperature, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Body weight label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.bodyweight"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Body weight value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(bodyWeight, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// General condition label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.condition"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// General condition value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(generalCondition, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Blank column.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        subtable.addCell(cell);
//--------------------------------------------------------------------------------
// Insert the subtable into the datatable.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(subtable);
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Medical data value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(medicalData, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
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


  private void doPetAnestheticInfo(HttpServletRequest request,
                                   Connection conn,
                                   Document document,
                                   Integer petExamKey,
                                   Byte appType)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect information about the pet anesthetic.
//--------------------------------------------------------------------------------
      String query = "SELECT DATE(schedule.start_stamp) AS treatment_date,"
                   + "itemdictionary.brand_name,"
                   + "petanesthetic.dose,"
                   + "petanesthetic.dose_unit,"
                   + "petanesthetic.route,"
                   + "HOUR(petanesthetic.start_stamp) AS start_hour,"
                   + "MINUTE(petanesthetic.start_stamp) AS start_minute,"
                   + "resource.name AS resource_name "
                   + "FROM petanesthetic, petexam, itemdictionary, resource, schedule "
                   + "WHERE petanesthetic.petexam_key = ? "
                   + "AND petanesthetic.application_type = ? "
                   + "AND petanesthetic.petexam_key = petexam.thekey "
                   + "AND petexam.schedule_key = schedule.thekey "
                   + "AND petanesthetic.itemdictionary_key = itemdictionary.thekey "
                   + "AND petanesthetic.resource_key = resource.thekey "
                   + "ORDER BY petanesthetic.start_stamp";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petExamKey);
      ps.setByte(2, appType);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet anesthetic variable.
//--------------------------------------------------------------------------------

      try {
        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = { 36,12,12,12,18 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
//--------------------------------------------------------------------------------
// Define a cell to allow data to span across multiple cells
//--------------------------------------------------------------------------------
        PdfPCell cell = new PdfPCell(phrase);
//--------------------------------------------------------------------------------
// Blank row.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// One of three possible agent label(s).
//--------------------------------------------------------------------------------
        String agentKey = "anesthetic.form.agent" + appType;
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,agentKey), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Dose label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.dose"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Route label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.route"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Time label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.time"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Admin by label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.adminby"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);

        int rowCnt=0;
        while( rs.next() ) {
          ++rowCnt;
//--------------------------------------------------------------------------------
// Agent value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("brand_name"), FontFactory.getFont(fontType, termSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Dose value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("dose") + " " + rs.getString("dose_unit"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Route value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("route"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Administered time value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(formatTime(rs.getInt("start_hour"),
                                       rs.getInt("start_minute")),
                                       FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Admin by value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("resource_name"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
        }

//--------------------------------------------------------------------------------
// At least one blank row for per application type.
//--------------------------------------------------------------------------------
        for (int i=rowCnt; i<1; i++) {
          cell = new PdfPCell(new Phrase(" "));
          datatable.addCell(cell);
          datatable.addCell(cell);
          datatable.addCell(cell);
          datatable.addCell(cell);
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


  private void doPetVitalSignsInfo(HttpServletRequest request,
                                   Connection conn,
                                   Document document,
                                   Integer petExamKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Get the pet vital signs records for the pet exam.
//--------------------------------------------------------------------------------
      String query = "SELECT HOUR(petvitalsigns.start_stamp) AS start_hour,"
                   + "MINUTE(petvitalsigns.start_stamp) AS start_minute,"
                   + "petvitalsigns.heart_rate,"
                   + "petvitalsigns.respt_rate,"
                   + "petvitalsigns.note_line1 "
                   + "FROM petexam, petvitalsigns, schedule "
                   + "WHERE petexam_key = ? "
                   + "AND petexam_key = petexam.thekey "
                   + "AND petexam.schedule_key = schedule.thekey "
                   + "ORDER BY petvitalsigns.start_stamp";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petExamKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet anesthetic variable.
//--------------------------------------------------------------------------------

      try {
        PdfPTable datatable = new PdfPTable(4);
        int headerwidths[] = { 12,12,12,40 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(76); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
//--------------------------------------------------------------------------------
// Define a cell to allow data to span across multiple cells
//--------------------------------------------------------------------------------
        PdfPCell cell = new PdfPCell(phrase);
//--------------------------------------------------------------------------------
// Blank row.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Procedure time label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.proctime"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Heart rate label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.hr"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Respitory rate label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.rr"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Note label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.note"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        datatable.addCell(cell);

        int rowCnt=0;
        while( rs.next() ) {
          ++rowCnt;
//--------------------------------------------------------------------------------
// Format the time value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(formatTime(rs.getInt("start_hour"),
                                       rs.getInt("start_minute")),
                                       FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Heart rate value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("heart_rate"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Respitory rate value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("respt_rate"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Note value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(rs.getString("note_line1"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
        }

//--------------------------------------------------------------------------------
// At least one blank row for per application type.
//--------------------------------------------------------------------------------
        for (int i=rowCnt; i<1; i++) {
          cell = new PdfPCell(new Phrase(" "));
          datatable.addCell(cell);
          datatable.addCell(cell);
          datatable.addCell(cell);
          datatable.addCell(cell);
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


  private void doPetExamTimeInfo(HttpServletRequest request,
                                 Connection conn,
                                 Document document,
                                 Integer petExamKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect timing information about the pet exam.
//--------------------------------------------------------------------------------
      String query = "SELECT HOUR(start_stamp) AS start_hour,"
                   + "MINUTE(start_stamp) AS start_minute,"
                   + "HOUR(surgery_stamp) AS surgery_hour,"
                   + "MINUTE(surgery_stamp) AS surgery_minute,"
                   + "HOUR(end_stamp) AS end_hour,"
                   + "MINUTE(end_stamp) AS end_minute,"
                   + "HOUR(reflex_stamp) AS reflex_hour,"
                   + "MINUTE(reflex_stamp) AS reflex_minute,"
                   + "HOUR(recovery_stamp) AS recovery_hour,"
                   + "MINUTE(recovery_stamp) AS recovery_minute "
                   + "FROM petexam "
                   + "WHERE petexam.thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petExamKey);
      rs = ps.executeQuery();
      if (!(rs.next()))
        throw new Exception("Pet Exam for key " + petExamKey + " not found!");

      try {
        PdfPTable datatable = new PdfPTable(6);
        int headerwidths[] = { 20,10,20,10,20,10 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
//--------------------------------------------------------------------------------
// Define a cell to allow data to span across multiple cells
//--------------------------------------------------------------------------------
        PdfPCell cell = new PdfPCell(phrase);
//--------------------------------------------------------------------------------
// Blank row.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Anesthesia induction label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.induction"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Anesthesia induction value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(formatTime(rs.getInt("start_hour"),
                                     rs.getInt("start_minute")),
                                     FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Surgery time label.
//-----------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.surgery"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Surgery time value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(formatTime(rs.getInt("surgery_hour"),
                                     rs.getInt("surgery_minute")),
                                     FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// End anesthesia time label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.endanesthesia"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// End anesthesia time value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(formatTime(rs.getInt("end_hour"),
                                     rs.getInt("end_minute")),
                                     FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Reflex recovery time label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.reflex"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Reflex time value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(formatTime(rs.getInt("reflex_hour"),
                                     rs.getInt("reflex_minute")),
                                     FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Recovery time label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"anesthetic.form.recovery"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Recovery time value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(formatTime(rs.getInt("recovery_hour"),
                                     rs.getInt("recovery_minute")),
                                     FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Complete the row to display previous columns.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(" "));
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
                            Integer petExamKey)
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
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

      Integer petKey = null;
      if (request.getParameter("petKey") != null) {
        petKey = new Integer(request.getParameter("petKey"));
      }
      else {
        throw new Exception("Request getParameter [petKey] not found!");
      }

      String theType = null;
      if (request.getParameter("presentationType") != null) {
        theType = new String(request.getParameter("presentationType"));
      }
      else {
        throw new Exception("Request getParameter [presentationType] not found!");
      }

      doReportGenerator(request, response, theKey, petKey, theType);
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
