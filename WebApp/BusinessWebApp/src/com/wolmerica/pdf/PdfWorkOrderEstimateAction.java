/*
 * PdfWorkOrderEstimateAction.java
 *
 * Created on September 21, 2009, 8:40 PM
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
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.IntegerFormatter;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;
import com.wolmerica.workorder.WorkOrderListHeadDO;
import com.wolmerica.workorder.WorkOrderDO;
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
import java.math.MathContext;
import java.sql.Timestamp;
import java.net.URL;
import java.util.ArrayList;

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


public class PdfWorkOrderEstimateAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 9;
  int termSize = 8;
  int totalSize = 12;
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
                                Integer schKey,
                                String presentationType)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// step 1 declare variables.
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
        Integer pageItemCount = new Integer(getPropertyService().getCustomerProperties(request,"invoice.report.page.count"));
        int currentPage = 0;
        int lastPage = 0;
        int startRow = 1;
        int endRow = pageItemCount;
        lastPage = getPageCount(conn,
                                schKey,
                                pageItemCount);
cat.debug(this.getClass().getName() + ": lastPage: " + lastPage);
        boolean moreWorkOrderId = true;
        int woCount = 0;
        while ( ++currentPage <= lastPage )
        {
//--------------------------------------------------------------------------------
// Advance to the next page if more line items to display.
//--------------------------------------------------------------------------------
          if (currentPage > 1)
            document.add(Chunk.NEXTPAGE);
cat.debug(this.getClass().getName() + ": doPageHeader()");
          doPageHeader(request, conn, document, currentPage, lastPage, schKey);
cat.debug(this.getClass().getName() + ": doItemHeader()");
          doItemHeader(request, document);

          cat.debug(this.getClass().getName() + ": Start=" + startRow + " End=" + endRow);
//--------------------------------------------------------------------------------
// First display all the work order rows.
//--------------------------------------------------------------------------------
          if (moreWorkOrderId) {
cat.debug(this.getClass().getName() + ": doWorkOrderDetail()");
            woCount = doWorkOrderDetail(request, conn, document, schKey, startRow, endRow);
            if (woCount != endRow) {
              moreWorkOrderId = false;
            }
          }

          startRow = endRow + 1;
          endRow = (endRow + pageItemCount) - 1;
        }
cat.debug(this.getClass().getName() + ": doPageFooter()");
        doPageFooter(request, conn, document, schKey);
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

  public int getPageCount(Connection conn,
                          Integer schKey,
                          Integer pageItemCount)
   throws Exception, IOException, SQLException {

    int workorderCount = 0;
    int fullPageCount = 0;

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the customer invoice items count.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS rowCount "
                   + "FROM workorder "
                   + "WHERE schedule_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();
      if (rs.next()) {
        workorderCount = rs.getInt("rowCount");
      }
      cat.debug(this.getClass().getName() + ": Work Order Count = " + workorderCount);

//--------------------------------------------------------------------------------
// Figure out how many more pages are needed to present detail.
//--------------------------------------------------------------------------------
      fullPageCount = (workorderCount / pageItemCount);
      if (workorderCount % pageItemCount > 0)
        ++fullPageCount;
      cat.debug(this.getClass().getName() + ": Full Page Count = " + fullPageCount);
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
    return fullPageCount;
  }

  private int doWorkOrderDetail(HttpServletRequest request,
                                Connection conn,
                                Document document,
                                Integer schKey,
                                int firstRecordCount,
                                int lastRecordCount)
   throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    IntegerFormatter integerFormatter = new IntegerFormatter();

    int recordCount = 0;
    String sTaxCode = "";

    try {
      PdfPTable datatable = new PdfPTable(7);
      int headerwidths[] = { 54, 10, 7, 9, 9, 10, 1 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(1);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      PdfPCell cell = null;

      WorkOrderListHeadDO formHDO = (WorkOrderListHeadDO) request.getSession().getAttribute("workorderHDO");

//--------------------------------------------------------------------------------
// Traverse the work order object.
//--------------------------------------------------------------------------------
      ArrayList woList = formHDO.getWorkOrderForm();
      WorkOrderDO formDO = null;

      cat.debug(this.getClass().getName() + ": woList.size() =" + woList.size());
      cat.debug(this.getClass().getName() + ": firstRecordCount =" + firstRecordCount);
      cat.debug(this.getClass().getName() + ": lastRecordCount =" + lastRecordCount);
//--------------------------------------------------------------------------------
// Iterate through the rows in the work order list.
//--------------------------------------------------------------------------------
      int currentIndex = 0;
      for (int j=0; j < woList.size(); j++) {
        formDO = (WorkOrderDO) woList.get(j);

        currentIndex = j + 1;
        cat.debug(this.getClass().getName() + ": currentIndex =" + currentIndex);

        if ((currentIndex >= firstRecordCount) && (currentIndex <= lastRecordCount))
        {
          ++recordCount;

          chunk = new Chunk(formDO.getSourceName().trim() + " " + formDO.getCategoryName().trim(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          datatable.addCell(new Phrase(chunk));
          chunk = new Chunk(formDO.getSize()+" "+ formDO.getSizeUnit(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);

          chunk = new Chunk(integerFormatter.format(formDO.getOrderQty().intValue()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);

          chunk = new Chunk(currencyFormatter.format(formDO.getEstimatedPrice()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);

          chunk = new Chunk(currencyFormatter.format(new BigDecimal("0")), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);

          chunk = new Chunk(currencyFormatter.format(formDO.getExtendedPrice()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Set the sales tax code depending on if the item is sales tax applicable or not.
//--------------------------------------------------------------------------------
          // if (ciiRow.getSalesTaxId())
          if (false)
            sTaxCode = "T";
          else
            sTaxCode = " ";

          chunk = new Chunk(sTaxCode, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
        }
//--------------------------------------------------------------------------------
// End of For Loop
//--------------------------------------------------------------------------------
      }
      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }

    return recordCount;
  }

  private void doPageHeader(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            int currentPage,
                            int lastPage,
                            Integer schKey)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      Integer vendorKey = new Integer("-1");
      String poNumber = "";
      String vendorName = "";
      String vendorInvoiceNumber = "";

//--------------------------------------------------------------------------------
// Collect customer and customer invoice specific information for the header.
//--------------------------------------------------------------------------------
      String query = "SELECT customer.acct_name,"
                   + "schedule.thekey AS schedule_key,"
                   + "customer.customertype_key,"
                   + "customer.ship_to,"
                   + "customer.acct_num,"
                   + "customer.address,"
                   + "customer.address2,"
                   + "customer.city,"
                   + "customer.state,"
                   + "customer.zip,"
                   + "schedule.start_stamp,"
                   + "customer.ledger_id "
                   + "FROM schedule, customer "
                   + "WHERE schedule.thekey = ? "
                   + "AND customer_key = customer.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, schKey);
      rs = ps.executeQuery();

      String accountName = "";
      String customerInvoiceNumber = "";
      Byte customerTypeKey = null;
      String shipToName = "";
      String accountNumber = "";
      Timestamp postStamp = null;
      String billToAddress = "";
      String billToAddress2 = "";
      String billToCity = "";
      String billToState = "";
      String billToZip = "";
      Boolean ledgerId = false;

      if (rs.next()) {
        accountName = rs.getString("acct_name");
        customerInvoiceNumber = rs.getString("schedule_key");
        customerTypeKey = rs.getByte("customertype_key");
        shipToName = rs.getString("ship_to");
        accountNumber = rs.getString("acct_num");
        billToAddress = rs.getString("address");
        billToAddress2 = rs.getString("address2");
        billToCity = rs.getString("city");
        billToState = rs.getString("state");
        billToZip = rs.getString("zip");
        postStamp = rs.getTimestamp("start_stamp");
        ledgerId = rs.getBoolean("ledger_id");
      } else {
        cat.error(this.getClass().getName() + ": schKey=" + schKey + " : Unable to retrieve SCHEDULE details!");
        throw new Exception("Unable to retrieve SCHEDULE and CUSTOMER details!");
      }

//--------------------------------------------------------------------------------
// Collect customer and customer invoice specific information for the header.
//--------------------------------------------------------------------------------
      query = "SELECT customer.acct_name,"
            + "customer.address,"
            + "customer.address2,"
            + "customer.city,"
            + "customer.state,"
            + "customer.zip,"
            + "customer.phone_num "
            + "FROM customer, customertype "
            + "WHERE customer.thekey = customertype.sold_by_key "
            + "AND customertype.thekey = ? "
            + "AND clinic_id";
      ps = conn.prepareStatement(query);
      ps.setByte(1, customerTypeKey);
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
        cat.error(this.getClass().getName() + ": customerTypeKey=" + customerTypeKey + " : Unable to retrieve SOLD BY details!");
        throw new Exception("Unable to retrieve SOLD BY details for estimate!");
      }

      try {
        DateFormatter df = new DateFormatter();
        String dateString = df.format(postStamp);

        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = { 15,15,40,15,15 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        PdfPTable titleTable = new PdfPTable(3);
        int titlewidths[] = { 34,15,15 }; // percentage
        titleTable.setWidths(titlewidths);
        titleTable.setWidthPercentage(64); // percentage
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
// Begin listing the seller name with the ESTIMATE header to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerName, FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"estimate.report.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Seller address with invoice number and invoice date value to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerAddress, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"estimate.report.estimateNum"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        titleTable.addCell(cell);
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.date"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Seller city, state, and zip code with invoice number and date to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerCity + ", " + sellerState + " " + sellerZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
        chunk = new Chunk(" " + customerInvoiceNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        titleTable.addCell(cell);
        chunk = new Chunk(" " + dateString, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        titleTable.addCell(cell);
//--------------------------------------------------------------------------------
// Seller phone number.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerPhone, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
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
        document.add(datatable);
//--------------------------------------------------------------------------------
// Define another table to capture the display of the heading element.
//--------------------------------------------------------------------------------
        datatable = new PdfPTable(5);
        int header2widths[] = { 8,28,34,15,15 }; // percentage
        datatable.setWidths(header2widths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
//--------------------------------------------------------------------------------
// Place a pair of blank lines before beginning to list the Vendor & Bill To details.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(""));
        cell.setBorder(0);
        cell.setColspan(5);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
        datatable.addCell(cell);

//--------------------------------------------------------------------------------
// List the associated purchase order number when a vendor invoice is available.
//--------------------------------------------------------------------------------
        if (vendorKey >= 0) {
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.vendor"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
          chunk = new Chunk(" " + vendorName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          phrase = new Phrase(chunk);
          datatable.addCell(phrase);

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.vendorInvoiceNum"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
          chunk = new Chunk(" " + vendorInvoiceNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          phrase = new Phrase(chunk);
          datatable.addCell(phrase);
        }
/*
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.poNumber"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(4);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
          chunk = new Chunk(" " + poNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          phrase = new Phrase(chunk);
          datatable.addCell(phrase);
*/

//--------------------------------------------------------------------------------
// Begin to list the Bill To details.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.billTo"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
        chunk = new Chunk(" " + accountName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.accountNum"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
        chunk = new Chunk(" " + accountNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        phrase = new Phrase(chunk);
        datatable.addCell(phrase);

        datatable.addCell("");
        chunk = new Chunk(billToAddress, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
        datatable.addCell("");
        datatable.addCell("");

        datatable.addCell("");
        chunk = new Chunk(billToCity + ", " + billToState + " " + billToZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);
        datatable.addCell("");
        datatable.addCell("");

//--------------------------------------------------------------------------------
// One blank line and the the line item title and charge or cash indicator.
//--------------------------------------------------------------------------------
        cell = new PdfPCell(new Phrase(""));
        cell.setBorder(0);
        cell.setColspan(5);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);

        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.title"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

        chunk = new Chunk(new String(" "), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
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


  private void doItemHeader(HttpServletRequest request,
                            Document document)
   throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(7);
      int headerwidths[] = { 54, 10, 7, 9, 9, 10, 1 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      PdfPCell cell = new PdfPCell(new Phrase(""));
      cell.setBorder(2);
      cell.setColspan(7);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// disable dose 03/11/2006 for Wolmerica purposes.
//--------------------------------------------------------------------------------
//      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
//      chunk.setUnderline(0.2f, -2f);
//      cell = new PdfPCell(new Phrase(chunk));
//      cell.setBorder(0);
//      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
//      datatable.addCell(cell);
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.lineitem.line1.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);


      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.debug(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }

  private void doPageFooter(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            Integer schKey)
   throws Exception, IOException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    BigDecimal subTotal = new BigDecimal("0");
    BigDecimal salesTaxCost = new BigDecimal("0");
    BigDecimal salesTaxRate = new BigDecimal("0");
    BigDecimal debitAdjustment = new BigDecimal("0");
    BigDecimal packagingCost = new BigDecimal("0");
    BigDecimal freightCost = new BigDecimal("0");
    BigDecimal miscellaneousCost = new BigDecimal("0");
    BigDecimal creditAdjustment = new BigDecimal("0");
    BigDecimal invoiceTotal = new BigDecimal("0");
    String noteLine1 = " ";
    String noteLine2 = " ";
    String noteLine3 = " ";
    String salesTaxState = " ";
    Boolean ledgerId = false;
    MathContext mc = new MathContext(4);

//--------------------------------------------------------------------------------
// Traverse the work order object.
//--------------------------------------------------------------------------------
    WorkOrderListHeadDO formHDO = (WorkOrderListHeadDO) request.getSession().getAttribute("workorderHDO");
    ArrayList woList = formHDO.getWorkOrderForm();
    WorkOrderDO formDO = null;
    for (int j=0; j < woList.size(); j++) {
      formDO = (WorkOrderDO) woList.get(j);
      subTotal = subTotal.add(formDO.getExtendedPrice());
      invoiceTotal = invoiceTotal.add(formDO.getExtendedPrice());
    }

    try {
      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 70, 17, 12, 1 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
//--------------------------------------------------------------------
// Put a blank row between line items and the Message header.
//--------------------------------------------------------------------
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      document.add(datatable);
//--------------------------------------------------------------------
// Notes To Client
//--------------------------------------------------------------------
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.notes.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
//--------------------------------------------------------------------
// Line Item Total $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.subTotal") + " ", FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(subTotal)  + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// Note Line #1
//--------------------------------------------------------------------
      chunk = new Chunk(noteLine1, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
//--------------------------------------------------------------------
// Sales Tax $
//--------------------------------------------------------------------
      if (salesTaxRate.intValue() > 0) {
        chunk = new Chunk(salesTaxState + " " + salesTaxRate.toString() + " " + getPropertyService().getCustomerProperties(request,"invoice.report.summary.percent"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
        chunk = new Chunk(currencyFormatter.format(salesTaxCost) + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
        datatable.addCell("");
      }
      else
      {
        datatable.addCell("");
        datatable.addCell("");
        datatable.addCell("");
      }
//--------------------------------------------------------------------
// Note Line #2
//--------------------------------------------------------------------
      chunk = new Chunk(noteLine2, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
//--------------------------------------------------------------------
// Debit Adjustment $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.debitAdjustment"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(debitAdjustment) + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// Note Line #3
//--------------------------------------------------------------------
      chunk = new Chunk(noteLine3, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      datatable.addCell(cell);
//-------------------------------------------------------------------
// Packaging $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.packagingCost"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(packagingCost) + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// Freight $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.freightCost"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(freightCost) + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// Line #1 of customer invoice credit terms.
//--------------------------------------------------------------------
      if (ledgerId) {
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.chargeTerms.line1"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        datatable.addCell(cell);
      }
      else {
        datatable.addCell("");
	  }
//--------------------------------------------------------------------
// Miscellaneous $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.miscellaneousCost"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(miscellaneousCost) + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// Line #2 of customer invoice credit terms.
//--------------------------------------------------------------------
      if (ledgerId) {
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.chargeTerms.line2"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        datatable.addCell(cell);
      }
      else {
        datatable.addCell("");
	  }
//--------------------------------------------------------------------
// Credit $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.creditAdjustment"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(creditAdjustment) + " ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// Line #3 of customer invoice credit terms.
//--------------------------------------------------------------------
      if (ledgerId) {
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.chargeTerms.line3"), FontFactory.getFont(fontType, termSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        datatable.addCell(cell);
      }
      else {
        datatable.addCell("");
	  }
//--------------------------------------------------------------------
// Estimate Total $
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"estimate.report.summary.estimateTotal"), FontFactory.getFont(fontType, totalSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(currencyFormatter.format(invoiceTotal) + " ", FontFactory.getFont(fontType, totalSize , Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      datatable.addCell("");
//--------------------------------------------------------------------
// End Of work order Estimate
//--------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"estimate.report.message.endReport"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      datatable.addCell(cell);

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
