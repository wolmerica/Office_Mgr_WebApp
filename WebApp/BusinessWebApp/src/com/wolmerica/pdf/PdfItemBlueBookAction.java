/*
 * PdfItemBlueBookAction.java
 *
 * Created on October 6, 2005, 4:58 PM
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


public class PdfItemBlueBookAction extends Action {

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
        document.addTitle(getPropertyService().getCustomerProperties(request,"bluebook.report.item.title"));
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

    int priceTypeCount = 0;
    int customerTypeCount = 0;
    int orderItemCount = 0;
    int fullPageCount = 0;

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "SELECT COUNT(*) AS rowCount "
                   + "FROM pricetype, priceattributebyitem "
                   + "WHERE domain_id = 0 "
                   + "AND bluebook_id "
                   + "AND pricetype.thekey = priceattributebyitem.pricetype_key";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        priceTypeCount = rs.getInt("rowCount");
      }
      query = "SELECT COUNT(*) AS rowCount "
            + "FROM customertype "
            + "WHERE bluebook_id";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        customerTypeCount = rs.getInt("rowCount");
        orderItemCount = priceTypeCount * customerTypeCount;
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
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
      PdfPTable datatable = new PdfPTable(19);
      int headerwidths[] = { 8,30,8,8,8,8,8,8,8,6,9,7,6,6,7,7,7,9,9 };
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(1);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
//--------------------------------------------------------------------------------
// The item.title is displayed once in the doCustomerDetail.
//--------------------------------------------------------------------------------
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      cell = new PdfPCell(new Phrase(new Chunk("")));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head8"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head9"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head10"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head11"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head12"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head13"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head14"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head15"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head16"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head17"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head18"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line1.head19"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head8"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head9"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head10"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head11"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head12"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head13"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head14"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head15"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head16"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head17"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head18"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"bluebook.report.item.line2.head19"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
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
// Query to retrieve all the item dictionary records.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "carry_factor,"
                   + "brand_name,"
                   + "size,"
                   + "size_unit,"
                   + "first_cost,"
                   + "unit_cost,"
                   + "unit_cost + (unit_cost * (carry_factor/100)) as final_cost,"
                   + "muadd,"
                   + "dose,"
                   + "dose_unit,"
                   + "label_cost,"
                   + "muvendor "
                   + "FROM itemdictionary "
                   + "ORDER by brand_name";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Query to retrieve all the price attribute by item records the bluebook indicator set.
//--------------------------------------------------------------------------------
      query = "SELECT priceattributebyitem.thekey, "
                   + "size,"
                   + "markup_rate "
            + "FROM pricetype, priceattributebyitem "
            + "WHERE itemdictionary_key=? "
            + "AND pricetype_key = pricetype.thekey "
            + "AND domain_id = 0 "
            + "AND bluebook_id "
            + "ORDER BY precedence ";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Query to retrieve all the customer type records with the bluebook indicator set
// for the selected item dictionary key.
//--------------------------------------------------------------------------------
      query = "SELECT customertype.thekey,"
                   + "customertype.name, "
                   + "label_cost,"
                   + "admin_cost,"
                   + "markup_rate, "
                   + "add_markup_rate,"
                   + "discount_threshold,"
                   + "discount_rate,"
                   + "precedence "
            + "FROM customertype, customerattributebyitem "
            + "WHERE itemdictionary_key=? "
            + "AND customertype_key = customertype.thekey "
            + "AND customertype.bluebook_id "
            + "ORDER BY precedence ";
      ps3 = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Query to retrieve price by service values based on the price attribute by item
// and customer attribute by item key.
//--------------------------------------------------------------------------------
      query = "SELECT computed_price, "
                   + "over_ride_price "
            + "FROM pricebyitem "
            + "WHERE priceattributebyitem_key=? "
            + "AND customertype_key = ? ";
      ps4 = conn.prepareStatement(query);

      try {
        PdfPTable datatable = new PdfPTable(19);
        int headerwidths[] = { 8,30,8,8,8,8,8,8,8,6,9,7,6,6,7,7,7,9,9 };
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(1);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        int recordCount = 0;
        Chunk chunk = null;
        PdfPCell cell = null;

        Integer idKey = null;
        String idCarryFactor = null;
        String idBrandName = null;
        String idSize = null;
        String idSizeUnit = null;
        String idFirstCost = null;
        String idUnitCost = null;
        String idFinalCost = null;
        String idAdditionalMarkUp = null;
        String idDose = null;
        String idDoseUnit = null;
        String idLabelCost = null;
        String idVendorMarkUp = null;
        Integer pabiKey = null;
        String pabiSize = null;
        String ptMarkUp = null;
        Integer ctKey = null;
        String ctName = null;
        String cabiLabelCost = null;
        String cabiAdminCost = null;
        String cabiMarkUp = null;
        String cabiAdditionalMarkUp = null;
        String cabiDiscountThreshold = null;
        String cabiDiscount = null;
        String pbiComputedPrice = null;
        String pbiOverRidePrice = null;
//--------------------------------------------------------------------------------
// Item Dictionary Rows
//--------------------------------------------------------------------------------
        while ( rs.next() ) {
          idKey = rs.getInt("thekey");
          idCarryFactor = rs.getString("carry_factor");
          idBrandName = rs.getString("brand_name");
          if (idBrandName.length() > 32)
            idBrandName = idBrandName.substring(0,32);
          idSize  = percentageFormatter.format(rs.getBigDecimal("size"));
          idSizeUnit = rs.getString("size_unit");
          idFirstCost = currencyFormatter.format(rs.getBigDecimal("first_cost"));
          idUnitCost = currencyFormatter.format(rs.getBigDecimal("unit_cost"));
          idFinalCost = currencyFormatter.format(rs.getBigDecimal("final_cost"));
          idAdditionalMarkUp = percentageFormatter.format(rs.getBigDecimal("muadd"));
          idDose  = percentageFormatter.format(rs.getBigDecimal("dose"));
          idDoseUnit = rs.getString("dose_unit");
          idLabelCost = currencyFormatter.format(rs.getBigDecimal("label_cost"));
          idVendorMarkUp = rs.getString("muvendor");
//--------------------------------------------------------------------------------
// Price Attribute By Item
//--------------------------------------------------------------------------------
          ps.setInt(1, idKey);
          rs2 = ps.executeQuery();
          while ( rs2.next() ) {
            pabiKey = new Integer(rs2.getString("thekey"));
            pabiSize = rs2.getString("size");
            ptMarkUp = rs2.getString("markup_rate");
//--------------------------------------------------------------------------------
// Customer Type and Customer Attribute By Item
//--------------------------------------------------------------------------------
            ps3.setInt(1, idKey);
            rs3 = ps3.executeQuery();
            while ( rs3.next() ) {
              ctKey = rs3.getInt("thekey");
              ctName = rs3.getString("name");
              cabiLabelCost = currencyFormatter.format(rs3.getBigDecimal("label_cost"));
              cabiAdminCost = currencyFormatter.format(rs3.getBigDecimal("admin_cost"));
              cabiMarkUp = percentageFormatter.format(rs3.getBigDecimal("markup_rate"));
              cabiAdditionalMarkUp = percentageFormatter.format(rs3.getBigDecimal("add_markup_rate"));
              cabiDiscountThreshold  = percentageFormatter.format(rs3.getBigDecimal("discount_threshold"));
              cabiDiscount = percentageFormatter.format(rs3.getBigDecimal("discount_rate"));
//--------------------------------------------------------------------------------
// Price By Item
//--------------------------------------------------------------------------------
              ps4.setInt(1, pabiKey);
              ps4.setInt(2, ctKey);
              rs4 = ps4.executeQuery();
              while ( rs4.next() ) {
                pbiComputedPrice = currencyFormatter.format(rs4.getBigDecimal("computed_price"));
                pbiOverRidePrice = currencyFormatter.format(rs4.getBigDecimal("over_ride_price"));

                if (recordCount >= pageCount) {
                  recordCount = 0;
                  document.add(datatable);
                  document.add(Chunk.NEXTPAGE);
                  doPageHeader(request, document, ++currentPage, lastPage);
                  doItemHeader(request, document);

                  datatable = new PdfPTable(19);
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
// Field 1 - Item carry factor percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idCarryFactor, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 2 - Item dictionary brand name.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idBrandName, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 3 - Item size and unit value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idSize + idSizeUnit, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 4 - Price attribute by item sales size.
//--------------------------------------------------------------------------------
                chunk = new Chunk(pabiSize, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 5 - Item dictionary first cost value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idFirstCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 6 - Item dictionary unit cost value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idUnitCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 7 - Item dictionary final cost value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idFinalCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 8 - Item dictionary additional mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idAdditionalMarkUp.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 9 - Item dictionary label cost value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idLabelCost.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 10 - Item dictionary vendor mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(idVendorMarkUp.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 11 - Customer type name.
//--------------------------------------------------------------------------------
                chunk = new Chunk(ctName, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 12 - Customer attribute by item label cost value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabiLabelCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 13 - Customer attribute by item admin cost value.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabiAdminCost, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 14 - Customer attribute by item mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabiMarkUp.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 15 - Customer attribute by item additional mark-up percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabiAdditionalMarkUp.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 16 - Customer attribute by item based discount threshold.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabiDiscountThreshold.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 17 - Customer attribute by item based discount percentage.
//--------------------------------------------------------------------------------
                chunk = new Chunk(cabiDiscount.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 18 - Price by item computed price.
//--------------------------------------------------------------------------------
                chunk = new Chunk(pbiComputedPrice, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Field 19 - Price by item over-ride price.
//--------------------------------------------------------------------------------
                chunk = new Chunk(pbiOverRidePrice, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);

                idCarryFactor = " ";
                idBrandName = " ";
                idSize = " ";
                idSizeUnit = " ";
                idFirstCost = " ";
                idUnitCost = " ";
                idFinalCost = " ";
                idAdditionalMarkUp = " ";
                idLabelCost = " ";
                idVendorMarkUp = " ";
                idDose = " ";
                idDoseUnit = " ";
                pabiSize = " ";
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
