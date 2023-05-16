/*
 * PdfServiceBlueBookAction.java
 *
 * Created on April 14, 2007, 10:15 AM
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
import com.wolmerica.tools.formatter.CurrencyFormatter;
import com.wolmerica.tools.formatter.PercentageFormatter;


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
import java.sql.Timestamp;
import java.util.Date;
import java.net.URL;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.log4j.Logger;


public class PdfServiceBlueBookAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int fontSize = 7;
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
                                String presentationType)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// step 1: define a landscape document.
//--------------------------------------------------------------------------------
    Document document = new Document(PageSize.A4.rotate());
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
        if ("pdf".equals(presentationType)) {
          response.setContentType("application/pdf");
          PdfWriter.getInstance(document, response.getOutputStream());
        }
        else {
          response.sendRedirect("http://itextdocs.itextpdf.com/tutorial/general/webapp/index.html");
        }
//--------------------------------------------------------------------------------
// step 2.5 establish header.
// Too many issues with headers and page numbers.
// It's easier to handle this behavior on our own.
//--------------------------------------------------------------------------------
        document.addCreator(createUser);
        document.addCreationDate();
        document.addTitle(getPropertyService().getCustomerProperties(request,"bluebook.report.service.title"));
//--------------------------------------------------------------------------------
// step 3
//--------------------------------------------------------------------------------
        document.open();
//--------------------------------------------------------------------------------
// step 4
//--------------------------------------------------------------------------------
        Integer pageCount = new Integer(getPropertyService().getCustomerProperties(request,"bluebook.report.page.count"));
        int currentPage = 1;
        int lastPage = 0;
        lastPage = getPageCount(conn,
                              pageCount);
        cat.debug(this.getClass().getName() + ": Last page=" + lastPage);

        doPageHeader(request, document, currentPage, lastPage);
        doItemHeader(request, document);
        doOrderDetail(request, conn, document, pageCount, currentPage, lastPage);
      }
      catch(DocumentException de) {
        de.printStackTrace();
        cat.error(this.getClass().getName() + ": document: " + de.getMessage());
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

  public int getPageCount(Connection conn,
                          Integer pageItemCount)
   throws Exception, IOException, SQLException {

    int serviceCount = 0;
    int priceTypeCount = 0;
    int customerTypeCount = 0;
    int orderItemCount = 0;
    int fullPageCount = 0;

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Get the count of services in the service dictionary.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS rowCount "
                   + "FROM servicedictionary";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        serviceCount = rs.getInt("rowCount");
      }
//--------------------------------------------------------------------------------
// Get the number of price type(s) eligible for the blue book report.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS rowCount "
            + "FROM pricetype "
            + "WHERE domain_id = 1 "
            + "AND bluebook_id";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        priceTypeCount = rs.getInt("rowCount");
      }
//--------------------------------------------------------------------------------
// Get the count of customer type(s) eligible for the blue book report.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS rowCount "
            + "FROM customertype "
            + "WHERE bluebook_id";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        customerTypeCount = rs.getInt("rowCount");
        orderItemCount = serviceCount * priceTypeCount * customerTypeCount;
        cat.debug(this.getClass().getName() + ": Row Count = " + orderItemCount);
//--------------------------------------------------------------------------------
// Figure out how many more pages are needed to present detail.
//--------------------------------------------------------------------------------
        fullPageCount = orderItemCount / pageItemCount;
        if (orderItemCount % pageItemCount > 0)
          ++fullPageCount;
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLError: " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try { rs.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try { ps.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        ps = null;
      }
    }
    return fullPageCount;
  }

  private void doPageHeader(HttpServletRequest request,
                            Document document,
                            int currentPage,
                            int lastPage)
   throws Exception, IOException, SQLException {

    try {
      Timestamp postStamp = new Timestamp(new Date().getTime());
      String dateString = postStamp.toString().substring(5,7) + "/"
                        + postStamp.toString().substring(8,10)+ "/"
                        + postStamp.toString().substring(0,4);

      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 15,20,50,15 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

//--------------------------------------------------------------------------------
// Insert the company logo at the top of the page as though it resides in the
// webapp folder, making the relative reference to the images subdirectory work.
//--------------------------------------------------------------------------------
      String p_strCurrentURL = new String(request.getRequestURL());
      Image companyLogo = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/Business_Logo.gif"));
      companyLogo.scalePercent(40,40);
      PdfPCell cell = new PdfPCell(companyLogo);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// The first line will display the logo, title, one blank cell, and the page count.
//--------------------------------------------------------------------------------
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      datatable.addCell(new Phrase(chunk));
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.page"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      Phrase phrase = new Phrase(chunk);
      chunk = new Chunk(" " + currentPage + " of " + lastPage, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);
//--------------------------------------------------------------------------------
// The second line will skip the first three cells and list the report date only.
//--------------------------------------------------------------------------------
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.date"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      phrase = new Phrase(chunk);
      chunk = new Chunk(" " + dateString, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document: " + de.getMessage());
    }
  }

private void doItemHeader(HttpServletRequest request,
                          Document document)
   throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(18);
      int headerwidths[] = { 30,8,8,8,8,8,8,8,8,9,8,7,8,8,8,8,9,9 };
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(1);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
//--------------------------------------------------------------------------------
// The lineitem.title is displayed once in the doCustomerDetail.
//--------------------------------------------------------------------------------
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head8"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head9"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head10"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head11"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head12"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head13"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head14"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head15"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head16"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head17"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line1.head18"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head8"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head9"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head10"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head11"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head12"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head13"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head14"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head15"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head16"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head17"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.service.line2.head18"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }

  private void doOrderDetail(HttpServletRequest request,
                             Connection conn,
                             Document document,
                             int pageCount,
                             int currentPage,
                             int lastPage)
   throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    PercentageFormatter percentageFormatter = new PercentageFormatter();
    Float fShade = new Float("0.80f");

    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    PreparedStatement ps3 = null;
    ResultSet rs3 = null;
    PreparedStatement ps4 = null;
    ResultSet rs4 = null;

    try {
//--------------------------------------------------------------------------------
// Query to retrieve all the service dictionary records.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "category,"
                   + "name,"
                   + "duration_hours,"
                   + "duration_minutes,"
                   + "labor_cost,"
                   + "service_cost,"
                   + "fee1_cost,"
                   + "fee2_cost,"
                   + "markup1_rate,"
                   + "markup2_rate "
                   + "FROM servicedictionary "
                   + "ORDER by category, name";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Query to retrieve all the price type records with the bluebook indicator set.
//--------------------------------------------------------------------------------
      query = "SELECT thekey, name, markup_rate "
            + "FROM pricetype "
            + "WHERE domain_id = 1 "
            + "AND bluebook_id "
            + "ORDER BY precedence";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Query to retrieve all the customer type records with the bluebook indicator set
// for the selected service dictionary key.
//--------------------------------------------------------------------------------
      query = "SELECT customerattributebyservice.thekey,"
            + "name,"
            + "fee1_cost,"
            + "fee2_cost,"
            + "markup1_rate,"
            + "markup2_rate,"
            + "discount_threshold,"
            + "discount_rate,"
            + "precedence "
            + "FROM customertype, customerattributebyservice "
            + "WHERE servicedictionary_key = ? "
            + "AND customertype_key = customertype.thekey "
            + "AND bluebook_id "
            + "ORDER BY precedence";
      ps3 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Query to retrieve price by service values based on the price type and the
// customer attribute by service key.
//--------------------------------------------------------------------------------
      query = "SELECT computed_price, "
                   + "over_ride_price "
            + "FROM pricebyservice "
            + "WHERE pricetype_key = ? "
            + "AND customerattributebyservice_key = ?";
      ps4 = conn.prepareStatement(query);

      try {
        PdfPTable datatable = new PdfPTable(18);
        int headerwidths[] = { 30,8,8,8,8,8,8,8,8,9,8,7,8,8,8,8,9,9 };
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(1);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        int recordCount = 0;
        Chunk chunk = null;
        PdfPCell cell = null;

        Integer sdKey = null;
        String sdName = "";
        String sdDuration = "";
        String sdLaborCost = "";
        String sdServiceCost = "";
        String sdFee1Cost = "";
        String sdFee2Cost = "";
        String sdMarkUp1Rate = "";
        String sdMarkUp2Rate = "";
        Integer ptKey = null;
        String pbsBillBy = "";
        String ptMarkUp = "";
        Integer cabsKey = null;
        String ctName = "";
        String cabsFee1Cost = "";
        String cabsFee2Cost = "";
        String cabsMarkUp1Rate = "";
        String cabsMarkUp2Rate = "";
        String cabsDiscountThreshold = "";
        String cabsDiscount = "";
        String pbsComputedPrice = "";
        String pbsOverRidePrice = "";
//--------------------------------------------------------------------------------
// Service Dictionary Rows
//--------------------------------------------------------------------------------
        while ( rs.next() ) {
          sdKey = rs.getInt("thekey");
          sdName = rs.getString("name");
          if (sdName.length() > 32)
            sdName = sdName.substring(0,32);
//          pbsBillBy = rs.getString("size_unit");
//--------------------------------------------------------------------------------
// Service duration in hours and minutes formatted as H:MM
//--------------------------------------------------------------------------------
          sdDuration = rs.getString("duration_hours");
          sdDuration = sdDuration + ":";
          if (rs.getByte("duration_minutes") < 10)
            sdDuration = sdDuration + "0";
          sdDuration = sdDuration + rs.getString("duration_minutes");

          sdLaborCost = currencyFormatter.format(rs.getBigDecimal("labor_cost"));
          sdServiceCost = currencyFormatter.format(rs.getBigDecimal("service_cost"));
          sdFee1Cost = currencyFormatter.format(rs.getBigDecimal("fee1_cost"));
          sdFee2Cost = currencyFormatter.format(rs.getBigDecimal("fee2_cost"));
          sdMarkUp1Rate = percentageFormatter.format(rs.getBigDecimal("markup1_rate"));
          sdMarkUp2Rate = percentageFormatter.format(rs.getBigDecimal("markup2_rate"));
//--------------------------------------------------------------------------------
// Price Type
//--------------------------------------------------------------------------------
          rs2 = ps.executeQuery();
          while ( rs2.next() ) {
            ptKey = new Integer(rs2.getString("thekey"));
            pbsBillBy = rs2.getString("name");
            ptMarkUp = rs2.getString("markup_rate");
//--------------------------------------------------------------------------------
// Customer Type and Customer Attribute By Service
//--------------------------------------------------------------------------------
            ps3.setInt(1, sdKey);
            rs3 = ps3.executeQuery();
            while ( rs3.next() ) {
              cabsKey = rs3.getInt("thekey");
              ctName = rs3.getString("name");
              cabsFee1Cost = currencyFormatter.format(rs3.getBigDecimal("fee1_cost"));
              cabsFee2Cost = currencyFormatter.format(rs3.getBigDecimal("fee2_cost"));
              cabsMarkUp1Rate = percentageFormatter.format(rs3.getBigDecimal("markup1_rate"));
              cabsMarkUp2Rate = percentageFormatter.format(rs3.getBigDecimal("markup2_rate"));
              cabsDiscountThreshold  = percentageFormatter.format(rs3.getBigDecimal("discount_threshold"));
              cabsDiscount = percentageFormatter.format(rs3.getBigDecimal("discount_rate"));
//--------------------------------------------------------------------------------
// Price By Service
//--------------------------------------------------------------------------------
              ps4.setInt(1, ptKey);
              ps4.setInt(2, cabsKey);
              rs4 = ps4.executeQuery();
              while ( rs4.next() ) {

                pbsComputedPrice = currencyFormatter.format(rs4.getBigDecimal("computed_price"));
                pbsOverRidePrice = currencyFormatter.format(rs4.getBigDecimal("over_ride_price"));

                if (recordCount >= pageCount) {
                  recordCount = 0;
                  document.add(datatable);
                  document.add(Chunk.NEXTPAGE);
                  doPageHeader(request, document, ++currentPage, lastPage);
                  doItemHeader(request, document);

                  datatable = new PdfPTable(18);
                  datatable.setWidths(headerwidths);
                  datatable.setWidthPercentage(100); // percentage
                  datatable.getDefaultCell().setPadding(1);
                  datatable.getDefaultCell().setBorderWidth(0);
                  datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
                }
                ++recordCount;
                if (recordCount % 2 == 0)
                  fShade = new Float("0.90f");
                else
                  fShade = new Float("0.80f");
                datatable.getDefaultCell().setGrayFill(0.0f);
//--------------------------------------------------------------------------------
// Field 1 - Service dictionary name
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdName, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 2 - Service duration HH:MM
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdDuration, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 3 - Service billing rate as Service, Hour, Minute
//--------------------------------------------------------------------------------
                chunk = new Chunk(pbsBillBy, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 4 - Labor cost for the service.
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdLaborCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 5 - Base cost from which the service pricing is based.
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdServiceCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 6 - Service based fee.
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdFee1Cost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 7 - Additional service based fee.
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdFee2Cost.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 8 - Service based mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdMarkUp1Rate.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 9 - Additonal service based mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(sdMarkUp2Rate.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 10 - Customer type name.
//--------------------------------------------------------------------------------
                chunk = new Chunk(ctName, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 11 - Customer attribute by service based fee.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabsFee1Cost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 12 - Additional customer attribute by service based fee.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabsFee2Cost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 13 - Customer attribute by service based mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabsMarkUp1Rate.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 14 - Additional customer attribute by service based mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabsMarkUp2Rate.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 15 - Customer attribute by service based discount threshold.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabsDiscountThreshold.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 16 - Customer attribute by service based discount percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabsDiscount.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 17 - Price by service computed price.
//--------------------------------------------------------------------------------
                chunk = new Chunk(pbsComputedPrice, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 18 - Price by service over-ride price.
//--------------------------------------------------------------------------------
                chunk = new Chunk(pbsOverRidePrice, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);

                sdName = " ";
                sdDuration = " ";
                sdLaborCost = " ";
                sdServiceCost = " ";
                sdFee1Cost = " ";
                sdFee2Cost = " ";
                sdMarkUp1Rate = " ";
                sdMarkUp2Rate = " ";
                pbsBillBy = " ";
                ptMarkUp = " ";
              }
            }
          }
        }
        document.add(datatable);
//--------------------------------------------------------------------------------
// End of while loop
//--------------------------------------------------------------------------------
      }
      catch(DocumentException de) {
        de.printStackTrace();
        cat.error(this.getClass().getName() + ": document = " + de.getMessage());
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLError: " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try { rs.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try { ps.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        ps = null;
      }
    }
  }

  private void doPageFooter(HttpServletRequest request,
                            Connection conn,
                            Document document)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 15, 20, 20, 45}; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
//--------------------------------------------------------------------------------
// Put a blank row between line items and the Message header.
//--------------------------------------------------------------------------------
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");

      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.message.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
//--------------------------------------------------------------------------------
// Define a cell to allow data to span across multiple cells
//--------------------------------------------------------------------------------
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      datatable.addCell(cell);

      Integer messageCount = new Integer("0");
      Integer messageMax = new Integer(getPropertyService().getCustomerProperties(request,"bluebook.report.message.count"));
      String messageKey = null;
      String message = null;
      while (++messageCount <= messageMax)
      {
        messageKey = "bluebook.report.message.line" + messageCount;
        message = getPropertyService().getCustomerProperties(request,messageKey);
        if (!(message.equalsIgnoreCase(messageKey)))
        {
          datatable.addCell("");
          chunk = new Chunk(message, FontFactory.getFont(fontType, fontSize, Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(4);
          datatable.addCell(cell);
        }
      }
      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document: " + de.getMessage());
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

      doReportGenerator(request, response, theType);
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
