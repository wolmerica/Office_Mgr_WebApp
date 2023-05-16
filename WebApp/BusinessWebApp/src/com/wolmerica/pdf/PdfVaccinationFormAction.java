/*
 * PdfVaccinationFormAction.java
 *
 * Created on June 12, 2007, 8:26 PM
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


public class PdfVaccinationFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 8;
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
                                Integer vacKey,
                                String presentationType)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// step 1 declare variables and creation of a document object.
//--------------------------------------------------------------------------------
    DataSource ds = null;
    Connection conn = null;
    Rectangle pageSize = new Rectangle(450, 625);
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
        doVacInfo(request, conn, document, vacKey);
        doPetInfo(request, conn, document, vacKey);
        doReminderInfo(request, conn, document, vacKey);
        doOwnerInfo(request, conn, document, vacKey);
        doPageFooter(request, conn, document, vacKey);
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
// Display the vaccination form title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
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
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.date")
              + " " + sDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Blank line .
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

  private void doVacInfo(HttpServletRequest request,
                         Connection conn,
                         Document document,
                         Integer vacKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Query to retrieve the pet vaccination details.
//--------------------------------------------------------------------------------
      String query = "SELECT DATE(schedule.start_stamp) AS vac_date,"
                   + "rabies_id,"
                   + "rabies_tag_number,"
                   + "vac_route_number,"
                   + "vac_name,"
                   + "vac_serial_number,"
                   + "vac_expiration_date,"
                   + "canine_distemper_id,"
                   + "corunna_id,"
                   + "feline_distemper_id,"
                   + "feline_leukemia_id,"
                   + "other_id,"
                   + "petvaccination.bundle_key "
                   + "FROM petvaccination, schedule "
                   + "WHERE petvaccination.thekey = ? "
                   + "AND schedule_key = schedule.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, vacKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet variables.
//--------------------------------------------------------------------------------
      Date vacDate = null;
      String sVacDate = "";
      String rabiesId = "_";
      String rabiesTag = "          .";
      String vacRoute = "          .";
      String vacName = "        .";
      String vacSerial = "        .";
      Date vacExpDate = null;
      String sVacExpDate = "";
      String canineDisId = "_";
      String corunnaId = "_";
      String felineDisId = "_";
      String felineLeuId = "_";
      String otherId = "_";
      Integer bundleKey = null;
      String otherName = "";
//--------------------------------------------------------------------------------
// Collect the pet vaccination values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        vacDate = rs.getDate("vac_date");
        if (rs.getByte("rabies_id") == 1)
          rabiesId = "X";
        if (rs.getString("rabies_tag_number").length() > 0)
          rabiesTag = rs.getString("rabies_tag_number");
        if (rs.getString("vac_route_number").length() > 0)
          vacRoute = rs.getString("vac_route_number");
        if (rs.getString("vac_name").length() > 0)
          vacName = rs.getString("vac_name");
        if (rs.getString("vac_serial_number").length() > 0)
          vacSerial = rs.getString("vac_serial_number");
        vacExpDate = rs.getDate("vac_expiration_date");
        if (rs.getByte("canine_distemper_id") == 1)
          canineDisId = "X";
        if (rs.getByte("corunna_id") ==1)
          corunnaId = "X";
        if (rs.getByte("feline_distemper_id") == 1)
          felineDisId = "X";
        if (rs.getByte("feline_leukemia_id") == 1)
          felineLeuId = "X";
        if (rs.getByte("other_id") == 1)
          otherId = "X";
        bundleKey = rs.getInt("bundle_key");
      } else {
        throw new Exception("Unable to retrieve PET VACCINATION details.");
      }

//--------------------------------------------------------------------------------
// Look-up the bundle name if the other vaccination indicator is set.
//--------------------------------------------------------------------------------
        if ((otherId.equalsIgnoreCase("x")) && (bundleKey > 0)) {
          query = "SELECT name "
                + "FROM bundle "
                + "WHERE thekey = ?";
          ps = conn.prepareStatement(query);
          ps.setInt(1, bundleKey);
          rs = ps.executeQuery();
          if ( rs.next() )
            otherName = rs.getString("name");
          else
            throw new Exception("Bundle " + bundleKey.toString() + " not found!");
        }
//--------------------------------------------------------------------------------
// Format the vaccination and expiration date values.
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      sVacDate = df.format(vacDate);
      sVacExpDate = df.format(vacExpDate);

      try {
        PdfPTable datatable = new PdfPTable(6);
        int headerwidths[] = { 5,10,12,23,25,25 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Leave a blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the certify label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.certify"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the vaccination date.
//--------------------------------------------------------------------------------
        chunk = new Chunk("          " + sVacDate + "          .", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the certify label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.for"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Leave a blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the rabies indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.leftBracket")
              + rabiesId
              + getPropertyService().getCustomerProperties(request,"vaccination.form.rightBracket"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the rabies indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.rabiesLabel"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the rabies tag value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(rabiesTag, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the vaccination route value
//--------------------------------------------------------------------------------
        chunk = new Chunk(vacRoute, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the vaccine name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(vacName + " " + vacSerial, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the vaccination expiration date.
//--------------------------------------------------------------------------------
        chunk = new Chunk("          " + sVacExpDate + "          .", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Skip the first two columns.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the number label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.number"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the route label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.route"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the vaccine label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.vaccine"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the expiration date label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.vacExpDate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Leave a blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the canine distemper indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.leftBracket")
              + canineDisId
              + getPropertyService().getCustomerProperties(request,"vaccination.form.rightBracket"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the canine distemper label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.canineLabel"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the canine include label.
//--------------------------------------------------------------------------------
        chunk = new Chunk("     " + getPropertyService().getCustomerProperties(request,"vaccination.form.canineInclude"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the corunna indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.leftBracket")
              + corunnaId
              + getPropertyService().getCustomerProperties(request,"vaccination.form.rightBracket")
              + " "
              + getPropertyService().getCustomerProperties(request,"vaccination.form.corunnaLabel"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Leave a blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the feline distemper indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.leftBracket")
              + felineDisId
              + getPropertyService().getCustomerProperties(request,"vaccination.form.rightBracket"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the feline distemper label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.felineLabel"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the feline include label.
//--------------------------------------------------------------------------------
        chunk = new Chunk("     " + getPropertyService().getCustomerProperties(request,"vaccination.form.felineInclude"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Leave a blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the feline leukemia indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.leftBracket")
              + felineLeuId
              + getPropertyService().getCustomerProperties(request,"vaccination.form.rightBracket"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the feline leukemia indicator label.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.felineLeukemia"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the other indicator value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.leftBracket")
              + otherId
              + getPropertyService().getCustomerProperties(request,"vaccination.form.rightBracket")
              + "   "
              + getPropertyService().getCustomerProperties(request,"vaccination.form.otherLabel")
              + "     ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the other name value.
//--------------------------------------------------------------------------------
        chunk = new Chunk(otherName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Last line will be a blank.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, headerSize, Font.BOLD));
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

  private void doPetInfo(HttpServletRequest request,
                         Connection conn,
                         Document document,
                         Integer vacKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Query to retrieve the pet details.
//--------------------------------------------------------------------------------
      String query = "SELECT pet.name,"
                   + "pet.species_key,"
                   + "pet.breed_key,"
                   + "pet.color,"
                   + "pet.sex_id,"
                   + "pet.neutered_id,"
                   + "(YEAR(CURDATE())-YEAR(pet.birth_date)) "
                   + " - (RIGHT(CURDATE(),5)<RIGHT(pet.birth_date,5)) AS age, "
                   + "pet.birth_date "
                   + "FROM pet, schedule "
                   + "WHERE schedule.source_key = pet.thekey "
                   + "AND schedule.thekey IN (SELECT schedule_key "
                                             + "FROM petvaccination "
                                             + "WHERE thekey = ?)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, vacKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the pet variables.
//--------------------------------------------------------------------------------
      String petName = "";
      String petSpecies = "";
      String petDog="_";
      String petCat="_";
      String petBreed =  "";
      String petColor = "";
      String petSex = "Male";
      String petNeutered = "No";
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

        if (petSpecies.equalsIgnoreCase("canine"))
          petDog = "X";
        if (petSpecies.equalsIgnoreCase("feline"))
          petCat = "X";
	petColor = rs.getString("color");
	if (rs.getBoolean("sex_id"))
          petSex = "Female";
	if (rs.getBoolean("neutered_id"))
          petNeutered = "Yes";
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
        int headerwidths[] = { 22,18,30,10,10,10 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Display the herewith phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.herewith"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the dog phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.dog1") + petDog + getPropertyService().getCustomerProperties(request,"vaccination.form.dog2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the cat phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.cat1") + petCat + getPropertyService().getCustomerProperties(request,"vaccination.form.cat2") + " " + getPropertyService().getCustomerProperties(request,"vaccination.form.described"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
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
// Display the pet.neutered_id value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petNeutered, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the calculated pet age value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petAge, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Three blank columns.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the sex phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.sex"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the unsex phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.unsexed"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the age phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.age"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the pet.breed_name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(petBreed, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
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
// Display the breed phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.breed"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the color phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.color"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the name phrase.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.name"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Last line will be a blank.
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

  private void doReminderInfo(HttpServletRequest request,
                              Connection conn,
                              Document document,
                              Integer vacKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect DVM name information for the vaccination.
//--------------------------------------------------------------------------------
      String query = "SELECT schedule_key,"
                   + "resource.name "
                   + "FROM petvaccination, resource "
                   + "WHERE petvaccination.thekey = ? "
                   + "AND resource_key = resource.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, vacKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define the owner variables.
//--------------------------------------------------------------------------------
      Integer scheduleKey = null;
      String dvmName = "";
      Date vacReminder = null;
      String dateString = "N/A";
//--------------------------------------------------------------------------------
// Collect the owner values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        scheduleKey = rs.getInt("schedule_key");
        dvmName = rs.getString("name");
      } else {
        throw new Exception("Unable to retrieve vaccination DVM details.");
      }

//--------------------------------------------------------------------------------
// Collect reminder date from the event schedule.
//--------------------------------------------------------------------------------
      query = "SELECT DATE(start_stamp) AS reminder_date "
            + "FROM schedule "
            + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, scheduleKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Collect the reminder date value.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        vacReminder = rs.getDate("reminder_date");
        DateFormatter df = new DateFormatter();
        dateString = df.format(vacReminder);
      }

      try {
        PdfPTable datatable = new PdfPTable(4);
        int headerwidths[] = { 25,25,25,25 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Line two of the reminder info.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.booster"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Display the reminder date value.
//--------------------------------------------------------------------------------
        chunk = new Chunk("          " + dateString + "          .", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the second and third column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the DVM name value.
//--------------------------------------------------------------------------------
        chunk = new Chunk("                  " + dvmName + "     " + getPropertyService().getCustomerProperties(request,"vaccination.form.dvm"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        chunk.setUnderline(0.2f, -2f);
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setBorder(0);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Line two of the reminder info.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.reminder"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Line three of the reminder info.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner phone value
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.thankyou"), FontFactory.getFont(fontType, headerSize, Font.ITALIC));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Leave another blank line.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Line four of the reminder info.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.retain"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Last line will be a blank.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
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
                           Integer vacKey)
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
                   + "FROM customer, schedule "
                   + "WHERE schedule.customer_key = customer.thekey "
                   + "AND schedule.thekey IN (SELECT schedule_key "
                                             + "FROM petvaccination "
                                             + "WHERE thekey = ?)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, vacKey);
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

      try {
        PdfPTable datatable = new PdfPTable(2);
        int headerwidths[] = { 10,80 }; // percentage
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
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"vaccination.form.owner"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerName, FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner address value (2 - fields)
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerAddress + " " + ownerAddress2, FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner city, state, and zip value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerCity + ", " + ownerState + " " + ownerZip, FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, termSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the owner phone value
//--------------------------------------------------------------------------------
        chunk = new Chunk(ownerPhone, FontFactory.getFont(fontType, termSize, Font.NORMAL));
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
//--------------------------------------------------------------------------------
// Pet vaccination key value.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

//--------------------------------------------------------------------------------
// Presentation type value.
//--------------------------------------------------------------------------------
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
