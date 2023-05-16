/*
 * PdfAccountStatementAction.java
 *
 * Created on February 8, 2008, 7:15 PM
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
import com.wolmerica.customeraccounting.CustomerAccountingListDO;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.CurrencyFormatter;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.IntegerFormatter;
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
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.net.URL;

import com.itextpdf.text.BaseColor;
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

public class PdfAccountStatementAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 8;
  int termSize = 10;
  String fontType = FontFactory.TIMES_ROMAN;

//--------------------------------------------------------------------------------
// Define the seller variables.
//--------------------------------------------------------------------------------
  String sellerName = "";
  String sellerAddress = "";
  String sellerAddress2 = "";
  String sellerCity = "";
  String sellerState = "";
  String sellerZip = "";
  String sellerPhone = "";
//--------------------------------------------------------------------------------
// Define the customer variables.
//--------------------------------------------------------------------------------
  Integer customerKey = null;
  String customerName = "";
  String customerAddress = "";
  String customerAddress2 = "";
  String customerCity = "";
  String customerState = "";
  String customerZip = "";
  String customerPhone = "";
//--------------------------------------------------------------------------------
// Define the account statement variables.
//--------------------------------------------------------------------------------
  String accountNumber = "";
  Date endDate = null;
  Date prevEndDate = null;
  BigDecimal previousBalance = null;
  BigDecimal creditBalance = null;
  BigDecimal debitBalance = null;
  BigDecimal financeBalance = null;
  BigDecimal newBalance = null;
  Integer newCount = null;
  Integer numberOfDays = null;
  Integer daysToPay = null;
  Date paymentDate = null;
  String sEndDate = "";
  String sPrevEndDate = "";
  String sPaymentDate = "";

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
                                String slipNum,
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
        int currentPage = 1;
        int lastPage = 0;
        int page1ItemCount = new Integer(getPropertyService().getCustomerProperties(request,"account.statement.page1.count")).intValue();
        int pageItemCount = new Integer(getPropertyService().getCustomerProperties(request,"account.statement.page.count")).intValue();
//--------------------------------------------------------------------------------
// Write the business name and address in page header.
//--------------------------------------------------------------------------------
        doPageHeader(request,
                     conn,
                     document);
//--------------------------------------------------------------------------------
// Write the customer information.
//--------------------------------------------------------------------------------
        customerKey = doCustomerInfo(request,
                                     conn,
                                     document,
                                     slipNum);
//--------------------------------------------------------------------------------
// Load the account information into the class variables.
//--------------------------------------------------------------------------------
        loadAccountInfo(request,
                        conn,
                        document,
                        customerKey,
                        slipNum);
//--------------------------------------------------------------------------------
// Load the account transaction values into an ArrayList.
//--------------------------------------------------------------------------------
        ArrayList accountRows = loadAccountDetail(request,
                                                  conn,
                                                  slipNum,
                                                  customerKey,
                                                  prevEndDate,
                                                  endDate);
//--------------------------------------------------------------------------------
// Calculate the last page number.
//--------------------------------------------------------------------------------
        lastPage = getPageCount(accountRows.size(),
                                page1ItemCount,
                                pageItemCount);
        cat.debug(this.getClass().getName() + ": Rows..... : " + accountRows.size());
        cat.debug(this.getClass().getName() + ": Page1.....: " + page1ItemCount);
        cat.debug(this.getClass().getName() + ": PageX.....: " + pageItemCount);
        cat.debug(this.getClass().getName() + ": Last page.: " + lastPage);
//--------------------------------------------------------------------------------
// Write the account summary information.
//--------------------------------------------------------------------------------
        doAccountSummary(request,
                         conn,
                         document,
                         slipNum,
                         currentPage,
                         lastPage);
//--------------------------------------------------------------------------------
// Write the account activity information.
//--------------------------------------------------------------------------------
        doAccountActivity(request,
                          conn,
                          document);
//--------------------------------------------------------------------------------
// Write the line item details for the account on subsequent pages.
// The first page will list fewer lines due to the remit slip.
//--------------------------------------------------------------------------------
        int startCnt = 1;
        int endCnt = page1ItemCount;
        while (currentPage < (lastPage+1)) {
          cat.debug(this.getClass().getName() + ": endCnt...: " + endCnt);
          doAccountDetail(request,
                          conn,
                          document,
                          accountRows,
                          startCnt,
                          endCnt);
          startCnt = endCnt + 1;
          endCnt = endCnt + pageItemCount;
//--------------------------------------------------------------------------------
// Write the remit slip at the bottom of the first page.
//--------------------------------------------------------------------------------
          if (currentPage == 1) {
            doRemitSlip(request,
                        conn,
                        document,
                        slipNum);
          }
//--------------------------------------------------------------------------------
// Write the page footer at the end of each page.
//--------------------------------------------------------------------------------
          doPageFooter(request,
                       conn,
                       document);
          ++currentPage;
//--------------------------------------------------------------------------------
// Advance to the next page and output a header if an additional page is needed.
//--------------------------------------------------------------------------------
          if (currentPage < (lastPage+1)) {
            document.add(Chunk.NEXTPAGE);
//--------------------------------------------------------------------------------
// Write the business name and address in page header.
//--------------------------------------------------------------------------------
            doPageHeader(request,
                         conn,
                         document);
          }
        }
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


  private int getPageCount(int lineItemCnt,
                           int page1ItemCount,
                           int pageItemCount)
   throws Exception, IOException {

	int currentCnt = page1ItemCount;
	int lastPage = 1;

	while (lineItemCnt > currentCnt) {
      lastPage++;
      currentCnt = currentCnt + pageItemCount;
    }
    return lastPage;
  }


  private void doAccountDetail(HttpServletRequest request,
                               Connection conn,
                               Document document,
                               ArrayList accountRows,
                               int startCnt,
                               int endCnt)
   throws Exception, IOException {

    CurrencyFormatter cf = new CurrencyFormatter();
    DateFormatter df = new DateFormatter();

    try {
      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 15,14,48,15 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(80); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;
//--------------------------------------------------------------------------------
// Skip the first two rows.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      datatable.addCell(cell);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the header for the account details.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.transactiontitle"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(4);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the TRANSACTION DATE heading for each column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.transactiondate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the INVOICE NUMBER heading for each column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.invoicenumber"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the DESCRIPTION heading for each column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.description"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the AMOUNT heading for each column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.amount"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);

      CustomerAccountingListDO accountRow = null;
      int currentLine = 0;
      while (currentLine < endCnt) {
        if (currentLine < accountRows.size()) {
          accountRow = (CustomerAccountingListDO) accountRows.get(currentLine);
//--------------------------------------------------------------------------------
// Write the TRANSACTION DATE value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(df.format(accountRow.getPostDate()), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the INVOICE NUMBER value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(accountRow.getNumber(), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the DESCRIPTION value.
//--------------------------------------------------------------------------------
          chunk = new Chunk(accountRow.getTransactionTypeDescription(), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Write the AMOUNT value.
//--------------------------------------------------------------------------------
          chunk = new Chunk("$"+cf.format(accountRow.getAmount()), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
        }
        else
        {
//--------------------------------------------------------------------------------
// Display a blank row to fill in the page.
//--------------------------------------------------------------------------------
          chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(4);
          datatable.addCell(cell);
	    }
        currentLine++;
      }
      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
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

      if (rs.next()) {
        sellerName = rs.getString("acct_name");
        sellerAddress = rs.getString("address");
        sellerAddress2 = rs.getString("address2");
        sellerCity = rs.getString("city");
        sellerState = rs.getString("state");
        sellerZip = rs.getString("zip");
        sellerPhone = pnf.format(rs.getString("phone_num"));
      } else
        throw new Exception("Unable to retrieve SOLD BY details.");

      try {
        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = { 15,15,40,10,20 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
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
        cell.setColspan(5);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setBorder(0);
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
        cell.setColspan(2);
        cell.setBorder(0);
        datatable.addCell(cell);

        cell = new PdfPCell(titleTable);
        cell.setColspan(3);
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the account statement title.
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(5);
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

  private Integer doCustomerInfo(HttpServletRequest request,
                                 Connection conn,
                                 Document document,
                                 String slipNum)
   throws Exception, IOException, SQLException {

    PhoneNumberFormatter pnf = new PhoneNumberFormatter();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect information about the pet owner.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "acct_name,"
                   + "address,"
                   + "address2,"
                   + "city,"
                   + "state,"
                   + "zip,"
                   + "phone_num "
                   + "FROM customer "
                   + "WHERE customer.thekey IN (SELECT DISTINCT customer_key "
                                             + "FROM ledger "
                                             + "WHERE slip_num = ?)";
      ps = conn.prepareStatement(query);
      ps.setString(1, slipNum);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Collect the customer values.
//--------------------------------------------------------------------------------
      if (rs.next()) {
        customerKey = rs.getInt("thekey");
        customerName = rs.getString("acct_name");
        customerAddress = rs.getString("address");
        customerAddress2 = rs.getString("address2");
        customerCity = rs.getString("city");
        customerState = rs.getString("state");
        customerZip = rs.getString("zip");
        customerPhone = pnf.format(rs.getString("phone_num"));
      } else
        throw new Exception("Unable to retrieve CUSTOMER details.");

      try {
        PdfPTable datatable = new PdfPTable(3);
        int headerwidths[] = { 40,12,48 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip entire row.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(3);
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer label
//--------------------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.customer"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer name value
//--------------------------------------------------------------------------------
        chunk = new Chunk(customerName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first two columns.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(2);
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer address value (2 - fields)
//--------------------------------------------------------------------------------
        chunk = new Chunk(customerAddress + " " + customerAddress2, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first two columns.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(2);
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer city, state, and zip value
//--------------------------------------------------------------------------------
        chunk = new Chunk(customerCity + ", " + customerState + " " + customerZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first two columns.
//--------------------------------------------------------------------------------
        chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(2);
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
    return customerKey;
  }


  private void loadAccountInfo(HttpServletRequest request,
                               Connection conn,
                               Document document,
                               Integer customerKey,
                               String slipNum)
   throws Exception, IOException, SQLException {

    Calendar endCal = Calendar.getInstance();
    Calendar prevEndCal = Calendar.getInstance();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// (1a) Retrieve the ACCOUNT NUMBER.
//--------------------------------------------------------------------------------
      String query = "SELECT acct_num "
                   + "FROM customer "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (1b) Collect the ACCOUNT NUMBER value.
//--------------------------------------------------------------------------------
      accountNumber = "";
      if (rs.next())
        accountNumber = new String(rs.getString("acct_num"));
      else
        throw new Exception("Unable to retrieve CUSTOMER account detail.");
      cat.debug(this.getClass().getName() + ": Account number.: " + accountNumber);
//--------------------------------------------------------------------------------
// (2a) Retrieve the END DATE from the ledger for the slip number.
//--------------------------------------------------------------------------------
      query = "SELECT DATE(post_stamp) AS end_date "
            + "FROM ledger "
            + "WHERE slip_num = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, slipNum);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (2b) Collect the END DATE value.
//--------------------------------------------------------------------------------
      endDate = null;
      if (rs.next())
        endDate = rs.getDate("end_date");
      else
        throw new Exception("Unable to retrieve LEDGER end date details.");
      cat.debug(this.getClass().getName() + ": End date.......: " + endDate);
//--------------------------------------------------------------------------------
// (3a) Retrieve the PREVIOUS END DATE from the ledger.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS prev_count,"
            + "DATE(MAX(post_stamp)) AS prev_date "
            + "FROM ledger "
            + "WHERE customer_key = ? "
            + "AND DATE(post_stamp) < ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      ps.setDate(2, new java.sql.Date(endDate.getTime()));
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (3b) Collect the PREVIOUS END DATE value.
//--------------------------------------------------------------------------------
      prevEndDate = null;
      if (rs.next()) {
        if (rs.getInt("prev_count") > 0)
          prevEndDate = rs.getDate("prev_date");
        else {
//--------------------------------------------------------------------------------
// (4a) If there are no previous ledger slip assignments we default to first ledger entry.
//--------------------------------------------------------------------------------
          query = "SELECT COUNT(*) AS prev_count,"
                + "DATE(MIN(create_stamp)) AS prev_date "
                + "FROM ledger "
                + "WHERE customer_key = ? "
                + "AND slip_num = ?";
          ps = conn.prepareStatement(query);
          ps.setInt(1, customerKey);
          ps.setString(2, slipNum);
          rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (4b) Collect the PREVIOUS END DATE value.
//--------------------------------------------------------------------------------
          if (rs.next()) {
            if (rs.getInt("prev_count") > 0)
              prevEndDate = rs.getDate("prev_date");
            else
              throw new Exception("Unable to locate the minimum LEDGER invoice date.");
          } else
            throw new Exception("Unable to retrieve minimum LEDGER invoice date.");
        }
      } else
        throw new Exception("Unable to retrieve previous LEDGER slip date.");
//--------------------------------------------------------------------------------
// (4c) Make sure the END DATE and PREVIOUS END DATE values are not equal.
//--------------------------------------------------------------------------------
      if (endDate.compareTo(prevEndDate) == 0) {
        prevEndCal.setTimeInMillis(prevEndDate.getTime());
        prevEndCal.add(Calendar.DATE, -1);
        prevEndDate = prevEndCal.getTime();
      }
      cat.debug(this.getClass().getName() + ": Previous date..: " + prevEndDate);
//--------------------------------------------------------------------------------
// (5a) Retrieve the PREVIOUS BALANCE from customer accounting.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS prev_count,"
            + "SUM(amount) AS prev_balance "
            + "FROM customeraccounting "
            + "WHERE customer_key = ? "
            + "AND DATE(create_stamp) <= ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      ps.setDate(2, new java.sql.Date(prevEndDate.getTime()));
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (5b) Collect the PREVIOUS BALANCE value.
//--------------------------------------------------------------------------------
      previousBalance = new BigDecimal("0");
      if (rs.next()) {
        if (rs.getInt("prev_count") > 0)
          previousBalance = rs.getBigDecimal("prev_balance");
      }
      else
        throw new Exception("Unable to retrieve CUSTOMER ACCOUNTING previous balance.");
      cat.debug(this.getClass().getName() + ": Previous bal...: " + previousBalance);
//--------------------------------------------------------------------------------
// (6a) Retrieve the transaction count and NEW BALANCE from customer accounting.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS new_count,"
            + "SUM(amount) AS new_balance "
            + "FROM customeraccounting "
            + "WHERE customer_key = ? "
            + "AND DATE(create_stamp) <= ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      ps.setDate(2, new java.sql.Date(endDate.getTime()));
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (6b) Collect the NEW BALANCE value.
//--------------------------------------------------------------------------------
      newCount = null;
      newBalance = new BigDecimal("0");
      if (rs.next()) {
        newCount = rs.getInt("new_count");
        newBalance = rs.getBigDecimal("new_balance");
      }
      else
        throw new Exception("Unable to retrieve CUSTOMER ACCOUNTING new balance.");
      cat.debug(this.getClass().getName() + ": New balance....: " + newBalance);
      cat.debug(this.getClass().getName() + ": Trans count....: " + newCount);
//--------------------------------------------------------------------------------
// (7a) Calculate the BILLING CYCLE DAYS between the end and previous end date.
//      Add 365 days to a negative numberOfDays value until it's positive.
//--------------------------------------------------------------------------------
      endCal.setTimeInMillis(endDate.getTime());
      prevEndCal.setTimeInMillis(prevEndDate.getTime());
      numberOfDays = endCal.get(Calendar.DAY_OF_YEAR) - prevEndCal.get(Calendar.DAY_OF_YEAR);
      while(numberOfDays < 0) {
        numberOfDays = numberOfDays + 365;
      }
      cat.debug(this.getClass().getName() + ": endCal.get()....: " + endCal.get(Calendar.DAY_OF_YEAR));
      cat.debug(this.getClass().getName() + ": prevEndCal.get(): " + prevEndCal.get(Calendar.DAY_OF_YEAR));
      cat.debug(this.getClass().getName() + ": Days in cycle...: " + numberOfDays);
//--------------------------------------------------------------------------------
// (8a) Calculate the PAYMENT DUE DATE from the end date.
//--------------------------------------------------------------------------------
      daysToPay = new Integer(getPropertyService().getCustomerProperties(request,"account.statement.daystopay"));
      endCal.add(Calendar.DATE, daysToPay);
      paymentDate = endCal.getTime();
//--------------------------------------------------------------------------------
// Format the date value to mm/dd/yyyy
//--------------------------------------------------------------------------------
      DateFormatter df = new DateFormatter();
      sEndDate = df.format(endDate);
      sPrevEndDate = df.format(prevEndDate);
      sPaymentDate = df.format(paymentDate);
      cat.debug(this.getClass().getName() + ": Statement date.: " + sEndDate);
      cat.debug(this.getClass().getName() + ": Previous date..: " + sPrevEndDate);
      cat.debug(this.getClass().getName() + ": Payment date...: " + sPaymentDate);
//--------------------------------------------------------------------------------
// (9a) Retrieve the CREDIT activity.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS credit_count,"
            + "SUM(amount) AS credit_balance "
            + "FROM customeraccounting "
            + "WHERE customer_key = ? "
            + "AND DATE(create_stamp) > ? "
            + "AND DATE(create_stamp) <= ? "
            + "AND transactiontype_key IN (2,3)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      ps.setDate(2, new java.sql.Date(prevEndDate.getTime()));
      ps.setDate(3, new java.sql.Date(endDate.getTime()));
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (9b) Collect the CREDIT balance value.
//--------------------------------------------------------------------------------
      creditBalance = new BigDecimal("0");
      if (rs.next()) {
        if (rs.getInt("credit_count") > 0)
          creditBalance = rs.getBigDecimal("credit_balance");
      }
      else
        throw new Exception("Unable to retrieve CUSTOMER ACCOUNTING credit balance.");
      cat.debug(this.getClass().getName() + ": Credit balance....: " + creditBalance);
//--------------------------------------------------------------------------------
// (10a) Retrieve the DEBIT activity.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS debit_count,"
            + "SUM(amount) AS debit_balance "
            + "FROM customeraccounting "
            + "WHERE customer_key = ? "
            + "AND DATE(create_stamp) > ? "
            + "AND DATE(create_stamp) <= ? "
            + "AND transactiontype_key IN (1,5)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      ps.setDate(2, new java.sql.Date(prevEndDate.getTime()));
      ps.setDate(3, new java.sql.Date(endDate.getTime()));
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// (10b) Collect the DEBIT balance value.
//--------------------------------------------------------------------------------
      debitBalance = new BigDecimal("0");
      if (rs.next()) {
        if (rs.getInt("debit_count") > 0)
          debitBalance = rs.getBigDecimal("debit_balance");
      }
      else
        throw new Exception("Unable to retrieve CUSTOMER ACCOUNTING debit balance.");
      cat.debug(this.getClass().getName() + ": Debit balance....: " + debitBalance);
//--------------------------------------------------------------------------------
// (10a) Retrieve the FINANCE activity.
//--------------------------------------------------------------------------------
      financeBalance = new BigDecimal("0");
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


  private void doAccountSummary(HttpServletRequest request,
                                Connection conn,
                                Document document,
                                String slipNum,
                                int currentPage,
                                int lastPage)
   throws Exception, IOException, SQLException {

    CurrencyFormatter cf = new CurrencyFormatter();
    IntegerFormatter intf = new IntegerFormatter();

//--------------------------------------------------------------------------------
// Define the pdf data table.
//--------------------------------------------------------------------------------
    try {
      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 16,14,16,14 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(60); // percentage
      datatable.getDefaultCell().setPadding(5);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip the entire row.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      datatable.addCell(cell);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Account summary title
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.summarytitle"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(4);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the statement date label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.stmtdate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the statement date value
//--------------------------------------------------------------------------------
      chunk = new Chunk(sEndDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer account number label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.number"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer account number value
//--------------------------------------------------------------------------------
      chunk = new Chunk(accountNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the previous balance label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.previousbalance"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the previous balance value
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(previousBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the number of days in billing cycle label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.billingcycle"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the number of days in billing cycle
//--------------------------------------------------------------------------------
      chunk = new Chunk(intf.format(numberOfDays), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the new balance label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.newbalance"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the new balance value
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(newBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the third and forth column
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the payment due date label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.duedate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the payment due date
//--------------------------------------------------------------------------------
      chunk = new Chunk(sPaymentDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the current page number and total page count label
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.page"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the current page number and total page count
//--------------------------------------------------------------------------------
      chunk = new Chunk(currentPage + " of " + lastPage, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }


  private void doAccountActivity(HttpServletRequest request,
                                 Connection conn,
                                 Document document)
   throws Exception, IOException, SQLException {

    CurrencyFormatter cf = new CurrencyFormatter();

//--------------------------------------------------------------------------------
// Define the pdf data table.
//--------------------------------------------------------------------------------
    try {
      PdfPTable datatable = new PdfPTable(5);
      int headerwidths[] = { 14,14,14,14,14 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(70); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_CENTER);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip the first two rows.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
      cell.setBorder(0);
      datatable.addCell(cell);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Account activity title
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.activitytitle"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Define a subTable to format the previous balance.
//--------------------------------------------------------------------------------
      PdfPTable subTable = new PdfPTable(1);
      int titlewidths[] = { 16 }; // percentage
      subTable.setWidths(titlewidths);
      subTable.setWidthPercentage(16); // percentage
      subTable.getDefaultCell().setPadding(2);
      subTable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
//--------------------------------------------------------------------------------
// Add the previous balance label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.previousbalance"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add a blank two rows.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the previous balance value.
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(previousBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the subTable to the datatable.
//--------------------------------------------------------------------------------
      cell = new PdfPCell(subTable);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Define a subTable to format the credit balance.
//--------------------------------------------------------------------------------
      subTable = new PdfPTable(1);
      subTable.setWidths(titlewidths);
      subTable.setWidthPercentage(16); // percentage
      subTable.getDefaultCell().setPadding(2);
      subTable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
//--------------------------------------------------------------------------------
// Add the account credit label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" - " + getPropertyService().getCustomerProperties(request,"account.statement.credit"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add a blank row.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the credit balance value.
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(creditBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the subTable to the datatable.
//--------------------------------------------------------------------------------
      cell = new PdfPCell(subTable);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Define a subTable to format the debit balance.
//--------------------------------------------------------------------------------
      subTable = new PdfPTable(1);
      subTable.setWidths(titlewidths);
      subTable.setWidthPercentage(16); // percentage
      subTable.getDefaultCell().setPadding(2);
      subTable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
//--------------------------------------------------------------------------------
// Add the debit balance label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" + " + getPropertyService().getCustomerProperties(request,"account.statement.debit"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add a blank row.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the debit balance value.
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(debitBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the subTable to the datatable.
//--------------------------------------------------------------------------------
      cell = new PdfPCell(subTable);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Define a subTable to format the finance balance.
//--------------------------------------------------------------------------------
      subTable = new PdfPTable(1);
      subTable.setWidths(titlewidths);
      subTable.setWidthPercentage(16); // percentage
      subTable.getDefaultCell().setPadding(2);
      subTable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
//--------------------------------------------------------------------------------
// Add the finance balance label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" + " + getPropertyService().getCustomerProperties(request,"account.statement.finance"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add two blank rows.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the finance balance value.
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(financeBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the subTable to the datatable.
//--------------------------------------------------------------------------------
      cell = new PdfPCell(subTable);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Define a subTable to format the new balance.
//--------------------------------------------------------------------------------
      subTable = new PdfPTable(1);
      subTable.setWidths(titlewidths);
      subTable.setWidthPercentage(16); // percentage
      subTable.getDefaultCell().setPadding(2);
      subTable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
//--------------------------------------------------------------------------------
// Add the new balance label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.newbalance"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add two blank rows.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      subTable.addCell(cell);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the new balance value.
//--------------------------------------------------------------------------------
      chunk = new Chunk("$"+cf.format(newBalance), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      subTable.addCell(cell);
//--------------------------------------------------------------------------------
// Add the subTable to the datatable.
//--------------------------------------------------------------------------------
      cell = new PdfPCell(subTable);
      datatable.addCell(cell);

      document.add(datatable);
    }
      catch(DocumentException de) {
        de.printStackTrace();
        cat.error(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }


  private ArrayList loadAccountDetail(HttpServletRequest request,
                                      Connection conn,
                                      String slipNum,
                                      Integer customerKey,
                                      Date prevEndDate,
                                      Date endDate)

   throws Exception, IOException, SQLException {

    CustomerAccountingListDO accountRow = null;
    ArrayList<CustomerAccountingListDO> accountRows = new ArrayList<CustomerAccountingListDO>();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect the customers account activity from the ledger and customer accounting.
//--------------------------------------------------------------------------------
      String query = "SELECT '14' AS sourcetype_key,"
                   + "customerinvoice.thekey AS source_key,"
                   + "DATE(ledger.create_stamp) AS transaction_date,"
                   + "ledger.invoice_num AS invoice_number,"
                   + "customerinvoice.note_line1 AS description,"
                   + "ledger.invoice_total AS amount "
                   + "FROM ledger, customerinvoice "
                   + "WHERE slip_num = ? "
                   + "AND ledger.customerinvoice_key = customerinvoice.thekey "
                   + "UNION "
                   + "SELECT sourcetype_key,"
                   + "source_key,"
                   + "DATE(customeraccounting.create_stamp) AS transaction_date,"
                   + "at1.description AS invoice_number,"
                   + "CONCAT(at2.description, ' ', number) AS description,"
                   + "amount "
                   + "FROM customeraccounting, accountingtype at1, accountingtype at2 "
                   + "WHERE customeraccounting.customer_key = ? "
                   + "AND DATE(customeraccounting.create_stamp) > ? "
                   + "AND DATE(customeraccounting.create_stamp) <= ? "
                   + "AND customeraccounting.transactiontype_key = at1.thekey "
                   + "AND sourcetype_key = at2.thekey "
                   + "AND transactiontype_key IN (3,4) "
                   + "ORDER BY transaction_date";
      ps = conn.prepareStatement(query);
      ps.setString(1, slipNum);
      ps.setInt(2, customerKey);
      ps.setDate(3, new java.sql.Date(prevEndDate.getTime()));
      ps.setDate(4, new java.sql.Date(endDate.getTime()));
      rs = ps.executeQuery();

      while (rs.next()) {
        accountRow = new CustomerAccountingListDO();

        accountRow.setSourceTypeKey(rs.getByte("sourcetype_key"));
        accountRow.setSourceKey(rs.getInt("source_key"));
        accountRow.setPostDate(rs.getDate("transaction_date"));
        accountRow.setNumber(rs.getString("invoice_number"));
        accountRow.setTransactionTypeDescription(rs.getString("description"));
        accountRow.setAmount(rs.getBigDecimal("amount"));

        accountRows.add(accountRow);
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
    return accountRows;
  }

  private void doRemitSlip(HttpServletRequest request,
                           Connection conn,
                           Document document,
                           String slipNum)
   throws Exception, IOException, SQLException {
      
    CurrencyFormatter cf = new CurrencyFormatter();      
//--------------------------------------------------------------------------------
// Define the pdf data table.
//--------------------------------------------------------------------------------
    try {
      PdfPTable datatable = new PdfPTable(5);
      int headerwidths[] = { 33,10,14,8,15 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(80); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip the entire row.
//--------------------------------------------------------------------------------      
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
      cell.setBorder(0);
      datatable.addCell(cell);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the DOTTED LINE label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.dottedline"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer name value
//--------------------------------------------------------------------------------
      chunk = new Chunk(customerName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(2);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the BILLING DATE label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.billingdate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the statement date value
//--------------------------------------------------------------------------------
      chunk = new Chunk(sEndDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(2);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer address value (2 - fields)
//--------------------------------------------------------------------------------
      chunk = new Chunk(customerAddress + " " + customerAddress2, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(2);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the ACCOUNT NUMBER label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.number"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer account number value
//--------------------------------------------------------------------------------
      chunk = new Chunk(accountNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(2);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the customer city, state, and zip value
//--------------------------------------------------------------------------------
      chunk = new Chunk(customerCity + ", " + customerState + " " + customerZip, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(2);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the SLIP NUMBER label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.slipnumber"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the slip number value
//--------------------------------------------------------------------------------
      chunk = new Chunk(slipNum, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(2);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the entire row.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the first column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the TOTAL DUE label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.totaldue"), FontFactory.getFont(fontType, headerSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(3);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the payment due date.
//--------------------------------------------------------------------------------
      chunk = new Chunk(sPaymentDate, FontFactory.getFont(fontType, headerSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the ADDRESS 1 label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.address1"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the PAYABLE TO label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.payableto"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase = new Phrase(chunk);      
//--------------------------------------------------------------------------------
// Display the seller name value.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" " + sellerName, FontFactory.getFont(fontType, fontSize, Font.BOLD));
      phrase.add(new Phrase(chunk));
      cell = new PdfPCell(phrase);      
      cell.setColspan(4);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the ADDRESS 2 label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.address2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the REMIT SLIP label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.remitslip"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(3);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the new balance value
//--------------------------------------------------------------------------------
      chunk = new Chunk("$" + cf.format(newBalance), FontFactory.getFont(fontType, headerSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the ADDRESS 3 label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.address3"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the next four columns.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(4);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the EMAIL label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.email"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Skip the next 2nd column.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the PAYMENT SENT label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.paymentsent"), FontFactory.getFont(fontType, headerSize, Font.NORMAL));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the CHANGE LINE 1 label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.changeline1"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the CHANGE LINE 2 label.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.changeline2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(5);
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
   throws Exception, IOException, SQLException {
//--------------------------------------------------------------------------------
// Define the pdf data table.
//--------------------------------------------------------------------------------
    try {
      PdfPTable datatable = new PdfPTable(3);
      int headerwidths[] = { 35,30,35 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      Phrase phrase = null;
      PdfPCell cell = null;

//--------------------------------------------------------------------------------
// Skip the entire row.
//--------------------------------------------------------------------------------
      chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(3);
      cell.setBorder(0);
      datatable.addCell(cell);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the statement footer.
//--------------------------------------------------------------------------------
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"account.statement.footer"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setColspan(3);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setBorder(0);
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
      String slipNum = "";
      if (request.getParameter("slipNum") != null) {
        slipNum = new String(request.getParameter("slipNum"));
      }
      else
        throw new Exception("Request getParameter [slipNum] not found!");

      String theType = null;
      if (request.getParameter("presentationType") != null) {
        theType = new String(request.getParameter("presentationType"));
      }
      else
        throw new Exception("Request getParameter [presentationType] not found!");

      doReportGenerator(request, response, slipNum, theType);
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
