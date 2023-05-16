/*
 * PdfCustomerInvoiceAction.java
 *
 * Created on November 8, 2005, 3:26 PM
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
import com.wolmerica.customerinvoiceitem.CustomerInvoiceItemDO;
import com.wolmerica.customerinvoiceservice.CustomerInvoiceServiceDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.CurrencyFormatter;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.tools.formatter.IntegerFormatter;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;


import java.io.IOException;
import java.util.HashMap;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
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


public class PdfCustomerInvoiceAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int headerSize = 12;
  int fontSize = 9;
  int termSize = 8;
  int totalSize = 12;
  String fontType = FontFactory.TIMES_ROMAN;

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

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

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  
  public void doReportGenerator(HttpServletRequest request,
                                HttpServletResponse response,
                                Integer ciKey,
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
                                ciKey,
                                pageItemCount);
        boolean moreItemId = true;
        boolean moreServiceId = false;
        int itemCount = 0;
        int serviceCount = 0;

        while ( ++currentPage <= lastPage )
        {
//--------------------------------------------------------------------------------
// Advance to the next page if more line items to display.
//--------------------------------------------------------------------------------
          if (currentPage > 1)
            document.add(Chunk.NEXTPAGE);

          doPageHeader(request, conn, document, currentPage, lastPage, ciKey);

          doItemHeader(request, document);

          cat.debug(this.getClass().getName() + ": Start=" + startRow + " End=" + endRow);
//--------------------------------------------------------------------------------
// First display all the customer invoice item rows.
//--------------------------------------------------------------------------------
          if (moreItemId) {
            itemCount = doCustomerInvoiceItemDetail(request, conn, document, ciKey, startRow, endRow);
            if (itemCount != endRow) {
              moreItemId = false;
              moreServiceId = true;
            }
          }

//--------------------------------------------------------------------------------
// Next display all the customer invoice item rows.
//--------------------------------------------------------------------------------
          if (moreServiceId) {
            serviceCount = doCustomerInvoiceServiceDetail(request, conn, document, ciKey, startRow-itemCount, endRow-itemCount);
            if (serviceCount != endRow-itemCount) {
              moreServiceId = false;
            }
          }

          startRow = endRow + 1;
          endRow = (endRow + pageItemCount) - 1;
        }
        doPageFooter(request, conn, document, ciKey);
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
                          Integer ciKey,
                          Integer pageItemCount)
   throws Exception, IOException, SQLException {

    int invoiceItemCount = 0;
    int invoiceServiceCount = 0;
    int invoiceTotalCount = 0;
    int fullPageCount = 0;

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the customer invoice items count.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS rowCount "
                   + "FROM customerinvoiceitem "
                   + "WHERE customerinvoice_key = ? "
                   + "AND order_qty != 0";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();
      if (rs.next()) {
        invoiceItemCount = rs.getInt("rowCount");
      }

//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the customer invoice services count.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS rowCount "
            + "FROM customerinvoiceservice "
            + "WHERE customerinvoice_key = ? "
            + "AND order_qty != 0";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();
      if (rs.next()) {
        invoiceServiceCount = rs.getInt("rowCount");
      }

      cat.debug(this.getClass().getName() + ": Item Count = " + invoiceItemCount);
      cat.debug(this.getClass().getName() + ": Service Count = " + invoiceServiceCount);

//--------------------------------------------------------------------------------
// Figure out how many more pages are needed to present detail.
//--------------------------------------------------------------------------------
      invoiceTotalCount = invoiceItemCount + invoiceServiceCount;
      fullPageCount = (invoiceTotalCount / pageItemCount);
      if (invoiceTotalCount % pageItemCount > 0)
        ++fullPageCount;
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

  private int doCustomerInvoiceItemDetail(HttpServletRequest request,
                                          Connection conn,
                                          Document document,
                                          Integer ciKey,
                                          int firstRecordCount,
                                          int lastRecordCount)
   throws Exception, IOException, SQLException {

    CustomerInvoiceItemDO ciiRow = null;

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    IntegerFormatter integerFormatter = new IntegerFormatter();

    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int recordCount = 0;
    String sTaxCode = "";

    try {
      String query = "SELECT customerinvoiceitem.thekey,"
                   + "customerinvoiceitem.customerinvoice_key,"
                   + "customerinvoiceitem.itemdictionary_key,"
                   + "itemdictionary.brand_name,"
                   + "itemdictionary.generic_name,"
                   + "priceattributebyitem.size,"
                   + "itemdictionary.size_unit,"
                   + "itemdictionary.dose,"
                   + "customerinvoiceitem.order_qty,"
                   + "customerinvoice.sales_tax_key,"
                   + "customerinvoiceitem.sales_tax_id "
                   + "FROM customerinvoice, customerinvoiceitem, itemdictionary, priceattributebyitem "
                   + "WHERE customerinvoice.thekey = ? "
                   + "AND customerinvoice.thekey = customerinvoiceitem.customerinvoice_key "
                   + "AND customerinvoiceitem.thekey = customerinvoiceitem.master_key "
                   + "AND customerinvoiceitem.itemdictionary_key = itemdictionary.thekey "
                   + "AND customerinvoiceitem.thekey = customerinvoiceitem.master_key "
                   + "AND customerinvoiceitem.order_qty != 0 "
                   + "AND priceattributebyitem.itemdictionary_key = itemdictionary.thekey "
                   + "AND priceattributebyitem.pricetype_key = customerinvoiceitem.pricetype_key "
                   + "ORDER by customerinvoiceitem.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

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
        while ( rs.next() && ++recordCount <=lastRecordCount ) {
          if (recordCount >= firstRecordCount)
          {
            ciiRow = new CustomerInvoiceItemDO();

            ciiRow.setKey(rs.getInt("thekey"));
            ciiRow.setCustomerInvoiceKey(rs.getInt("customerinvoice_key"));
            ciiRow.setItemDictionaryKey(rs.getInt("itemdictionary_key"));
            ciiRow.setBrandName(rs.getString("brand_name"));
            ciiRow.setGenericName(rs.getString("generic_name"));
            ciiRow.setSize(rs.getBigDecimal("size"));
            ciiRow.setSizeUnit(rs.getString("size_unit"));
            ciiRow.setDose(rs.getBigDecimal("dose"));
            ciiRow.setOrderQty(rs.getShort("order_qty"));

//--------------------------------------------------------------------------------
// Special condition of the customer invoice being enabled for sales tax.
// 05/21/2006 apply discount to the pricing scheme when it exists.  (ie != 0)
//--------------------------------------------------------------------------------
            ciiRow.setEnableSalesTaxId(rs.getBoolean("sales_tax_key"));
            ciiRow.setSalesTaxId(rs.getBoolean("sales_tax_key") && rs.getBoolean("sales_tax_id"));

//--------------------------------------------------------------------------------
// 08/04/2006 Call the GetCustomerInvoiceItemTotalsByCII stored procedure to compute values.
//--------------------------------------------------------------------------------
//  1) OrderQty              summation of order quantity for the master_key.
//  2) DiscountAmt           summation of discounts for the item.
//  2) ExtendPrice           final price after discounts.
//--------------------------------------------------------------------------------
            ciiRow = getCustomerInvoiceItemTotalsByMasterKey(conn, ciiRow, ciKey);


            chunk = new Chunk(ciiRow.getBrandName().trim() + " " + ciiRow.getGenericName().trim(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            datatable.addCell(new Phrase(chunk));

            chunk = new Chunk(ciiRow.getSize()+" "+ ciiRow.getSizeUnit(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// disable dose 03/11/2006 for Wolmerica purposes.
//            chunk = new Chunk(rs.getString("dose"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
//            cell = new PdfPCell(new Phrase(chunk));
//            cell.setBorder(0);
//            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
//            datatable.addCell(cell);
//--------------------------------------------------------------------------------
            chunk = new Chunk(integerFormatter.format(ciiRow.getOrderQty().intValue()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(ciiRow.getThePrice()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(ciiRow.getDiscountAmount().negate()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(ciiRow.getExtendPrice()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Set the sales tax code depending on if the item is sales tax applicable or not.
//--------------------------------------------------------------------------------
            if (ciiRow.getSalesTaxId())
              sTaxCode = "T";
            else
              sTaxCode = " ";

            chunk = new Chunk(sTaxCode, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
          }
        }
        document.add(datatable);
//--------------------------------------------------------------------------------
// End of While Loop
//--------------------------------------------------------------------------------
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
    return recordCount;
  }


  public CustomerInvoiceItemDO getCustomerInvoiceItemTotalsByMasterKey(Connection conn,
                                                                       CustomerInvoiceItemDO formDO,
                                                                       Integer ciKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
  //--------------------------------------------------------------------------------
  // Call a procedure with one IN parameter
  //--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceItemTotalsByCII(?,?,?,?,?,?,?)}");

  //--------------------------------------------------------------------------------
  // Set the value for the IN parameter
  //--------------------------------------------------------------------------------
      cStmt.setInt(1, ciKey);
      cStmt.setInt(2, formDO.getKey());

  //--------------------------------------------------------------------------------
  // Execute the stored procedure
  //--------------------------------------------------------------------------------
      cStmt.execute();

  //--------------------------------------------------------------------------------
  // Retrieve the return values
  //--------------------------------------------------------------------------------
      formDO.setOrderQty(cStmt.getShort("orderQty"));
      formDO.setThePrice(cStmt.getBigDecimal("itemPrice"));
      formDO.setDiscountRate(cStmt.getBigDecimal("discountRate"));
      formDO.setDiscountAmount(cStmt.getBigDecimal("discountAmt"));
      formDO.setExtendPrice(cStmt.getBigDecimal("extendAmt"));

  //--------------------------------------------------------------------------------
  // Debug messages if necessary.
  //--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": orderQty : " + cStmt.getShort("orderQty"));
      cat.debug(this.getClass().getName() + ": discountRate : " + cStmt.getBigDecimal("discountRate"));
      cat.debug(this.getClass().getName() + ": discountAmt : " + cStmt.getBigDecimal("discountAmt"));
      cat.debug(this.getClass().getName() + ": extendAmt : " + cStmt.getBigDecimal("extendAmt"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceItemTotalsByCII() " + e.getMessage());
    }
    finally
    {
    try
    {
      cStmt.close();
    }
    catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceItemTotalsByCII() " + e.getMessage());
      }
    }
    return formDO;
  }


  private int doCustomerInvoiceServiceDetail(HttpServletRequest request,
                                             Connection conn,
                                             Document document,
                                             Integer ciKey,
                                             int firstRecordCount,
                                             int lastRecordCount)
   throws Exception, IOException, SQLException {

    CustomerInvoiceServiceDO cisRow = null;

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    IntegerFormatter integerFormatter = new IntegerFormatter();

    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int recordCount = 0;

    try {
      String query = "SELECT customerinvoiceservice.thekey,"
                   + "customerinvoiceservice.customerinvoice_key,"
                   + "customerinvoiceservice.servicedictionary_key,"
                   + "servicedictionary.name AS sd_name,"
                   + "pricetype.thekey AS pt_key,"
                   + "pricetype.name AS pt_name,"
                   + "customerinvoiceservice.order_qty "
                   + "FROM customerinvoiceservice, servicedictionary, pricetype "
                   + "WHERE customerinvoiceservice.customerinvoice_key = ? "
                   + "AND customerinvoiceservice.servicedictionary_key = servicedictionary.thekey "
                   + "AND customerinvoiceservice.thekey = customerinvoiceservice.master_key "                   
                   + "AND customerinvoiceservice.order_qty != 0 "
                   + "AND customerinvoiceservice.pricetype_key = pricetype.thekey "
                   + "ORDER by customerinvoiceservice.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

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
        while ( rs.next() && ++recordCount <=lastRecordCount ) {
          if (recordCount >= firstRecordCount)
          {
            cisRow = new CustomerInvoiceServiceDO();

            cisRow.setKey(rs.getInt("thekey"));
            cisRow.setCustomerInvoiceKey(rs.getInt("customerinvoice_key"));
            cisRow.setServiceDictionaryKey(rs.getInt("servicedictionary_key"));
            cisRow.setServiceName(rs.getString("sd_name"));
            cisRow.setPriceTypeKey(rs.getByte("pt_key"));
            cisRow.setPriceTypeName(rs.getString("pt_name"));
            cisRow.setOrderQty(rs.getShort("order_qty"));

//--------------------------------------------------------------------------------
// 08/04/2006 Call the GetCustomerInvoiceServiceTotalsByMasterKey stored procedure to compute values.
//--------------------------------------------------------------------------------
//  1) OrderQty              summation of order quantity for the master_key.
//  2) DiscountAmt           summation of discounts for the service.
//  2) ExtendPrice           final price after discounts.
//--------------------------------------------------------------------------------
            cisRow = getCustomerInvoiceServiceTotalsByMasterKey(conn, cisRow, ciKey);


            chunk = new Chunk(cisRow.getServiceName(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            datatable.addCell(new Phrase(chunk));

            chunk = new Chunk(cisRow.getPriceTypeName(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// disable dose 03/11/2006 for Wolmerica purposes.
//            chunk = new Chunk(rs.getString("dose"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
//            cell = new PdfPCell(new Phrase(chunk));
//            cell.setBorder(0);
//            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
//            datatable.addCell(cell);
//--------------------------------------------------------------------------------
            chunk = new Chunk(integerFormatter.format(cisRow.getOrderQty().intValue()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(cisRow.getThePrice()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(cisRow.getDiscountAmount().negate()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(cisRow.getExtendPrice()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);

            chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
          }
        }
        document.add(datatable);
//--------------------------------------------------------------------------------
// End of While Loop
//--------------------------------------------------------------------------------
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
    return recordCount;
  }


  public CustomerInvoiceServiceDO getCustomerInvoiceServiceTotalsByMasterKey(Connection conn,
                                                                             CustomerInvoiceServiceDO formDO,
                                                                             Integer ciKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceServiceTotalsByCII(?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, ciKey);
      cStmt.setInt(2, formDO.getKey());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setOrderQty(cStmt.getShort("orderQty"));
      formDO.setThePrice(cStmt.getBigDecimal("servicePrice"));
      formDO.setDiscountRate(cStmt.getBigDecimal("discountRate"));
      formDO.setDiscountAmount(cStmt.getBigDecimal("discountAmt"));
      formDO.setExtendPrice(cStmt.getBigDecimal("extendAmt"));

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": orderQty : " + cStmt.getShort("orderQty"));
      cat.debug(this.getClass().getName() + ": discountRate : " + cStmt.getBigDecimal("discountRate"));
      cat.debug(this.getClass().getName() + ": discountAmt : " + cStmt.getBigDecimal("discountAmt"));
      cat.debug(this.getClass().getName() + ": extendAmt : " + cStmt.getBigDecimal("extendAmt"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceServiceTotalsByCII() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceServiceTotalsByCII() " + e.getMessage());
      }
    }
    return formDO;
  }


  private void doPageHeader(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            int currentPage,
                            int lastPage,
                            Integer ciKey)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Collect purchaseorder, vendor, and customer invoice details for the header.
//--------------------------------------------------------------------------------
      String query = "SELECT purchaseorder.vendor_key,"
                   + "customerinvoice.customer_key,"
                   + "purchaseorder.purchase_order_num,"
                   + "vendor.name,"
                   + "customerinvoice.invoice_num "
                   + "FROM customerinvoice, vendorinvoice, purchaseorder, vendor "
                   + "WHERE customerinvoice.thekey = ? "
                   + "AND vendorinvoice_key = vendorinvoice.thekey "
                   + "AND purchaseorder_key = purchaseorder.thekey "
                   + "AND vendor_key = vendor.thekey ";

      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

      Integer vendorKey = new Integer("-1");
      String poNumber = "";
      String vendorName = "";
      String vendorInvoiceNumber = "";

      if (rs.next()) {
        vendorKey = rs.getInt("vendor_key");
        poNumber = rs.getString("purchase_order_num");
        vendorName = rs.getString("name");
        vendorInvoiceNumber = rs.getString("invoice_num");
      }
//--------------------------------------------------------------------------------
// FIX - Need to rethink this.
//      } else {
//        cat.error(this.getClass().getName() + ": ciKey=" + ciKey + " : Unable to retrieve VENDOR details and PURCHASE ORDER number!");
//        throw new Exception("Unable to retrieve VENDOR details and PURCHASE ORDER number!");
//      }
//--------------------------------------------------------------------------------

//--------------------------------------------------------------------------------
// Collect customer and customer invoice specific information for the header.
//--------------------------------------------------------------------------------
      query = "SELECT customer.acct_name,"
            + "customerinvoice.invoice_num,"
            + "customerinvoice.customertype_key,"
            + "customerinvoice.sourcetype_key,"
            + "customerinvoice.source_key,"
            + "customer.ship_to,"
            + "customer.acct_num,"
            + "customer.address,"
            + "customer.address2,"
            + "customer.city,"
            + "customer.state,"
            + "customer.zip,"
            + "customerinvoice.create_stamp,"
            + "customer.ledger_id "
            + "FROM customerinvoice, customer "
            + "WHERE customerinvoice.thekey=? "
            + "AND customer_key = customer.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

      String accountName = "";
      String customerInvoiceNumber = "";
      Byte customerTypeKey = null;
      Byte sourceTypeKey = null;
      Integer sourceKey = null;
      String sourceName = "";
      String attributeToName = "";
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
        customerInvoiceNumber = rs.getString("invoice_num");
        customerTypeKey = rs.getByte("customertype_key");
        sourceTypeKey = rs.getByte("sourcetype_key");
        sourceKey = rs.getInt("source_key");
        shipToName = rs.getString("ship_to");
        accountNumber = rs.getString("acct_num");
        billToAddress = rs.getString("address");
        billToAddress2 = rs.getString("address2");
        billToCity = rs.getString("city");
        billToState = rs.getString("state");
        billToZip = rs.getString("zip");
        postStamp = rs.getTimestamp("create_stamp");
        ledgerId = rs.getBoolean("ledger_id");
      } else {
        cat.error(this.getClass().getName() + ": ciKey=" + ciKey + " : Unable to retrieve CUSTOMER details and INVOICE number!");
        throw new Exception("Unable to retrieve CUSTOMER details and INVOICE number!");
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
        throw new Exception("Unable to retrieve SOLD BY details for invoice!");
      }

//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
      if (sourceTypeKey.compareTo(new Byte("-1")) > 0) {
        HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                  sourceTypeKey,
                                                  sourceKey);
        sourceName = nameMap.get("sourceName").toString();
        attributeToName = nameMap.get("attributeToName").toString();
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
// Begin listing the seller name with the invoice header to the right.
//--------------------------------------------------------------------------------
        chunk = new Chunk(sellerName, FontFactory.getFont(fontType, headerSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        titleTable.addCell(cell);
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.title"), FontFactory.getFont(fontType, headerSize, Font.BOLD));
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
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.invoiceNum"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
//--------------------------------------------------------------------------------
// Display the pet, system, or vehicle attributed to name if applicable.
//--------------------------------------------------------------------------------
      if (sourceTypeKey.compareTo(new Byte("-1")) > 0) {
        chunk = new Chunk(sourceName + ":", FontFactory.getFont(fontType, fontSize, Font.BOLD));
        phrase = new Phrase(chunk);
        chunk = new Chunk(" " + attributeToName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        phrase.add(chunk);
        cell = new PdfPCell(phrase);
        cell.setBorder(0);
        cell.setColspan(2);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
      } else {
        datatable.addCell("");
        datatable.addCell("");
      }

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

        if (ledgerId)
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.chargeInvoice"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        else
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.cashInvoice"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
                            Integer ciKey)
   throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "SELECT customerinvoice.note_line1,"
                   + "customerinvoice.note_line2,"
                   + "customerinvoice.note_line3,"
                   + "sub_total,"
                   + "description,"
                   + "sales_tax_rate,"
                   + "sales_tax,"
                   + "debit_adjustment,"
                   + "packaging,"
                   + "freight,"
                   + "miscellaneous,"
                   + "credit_adjustment,"
                   + "invoice_total,"
                   + "ledger_id "
                   + "FROM customerinvoice, customer, taxmarkup "
                   + "WHERE customerinvoice.thekey = ? "
                   + "AND customer_key = customer.thekey "
                   + "AND sales_tax_key = taxmarkup.thekey";

      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

      String noteLine1 = null;
      String noteLine2 = null;
      String noteLine3 = null;
      String salesTaxState = null;
      BigDecimal subTotal = null;
      BigDecimal salesTaxCost = null;
      BigDecimal salesTaxRate = null;
      BigDecimal debitAdjustment = null;
      BigDecimal packagingCost = null;
      BigDecimal freightCost = null;
      BigDecimal miscellaneousCost = null;
      BigDecimal creditAdjustment = null;
      BigDecimal invoiceTotal = null;
      Boolean ledgerId = false;
      MathContext mc = new MathContext(4);

      if (rs.next()) {
        noteLine1 = rs.getString("note_line1");
        noteLine2 = rs.getString("note_line2");
        noteLine3 = rs.getString("note_line3");
        subTotal = rs.getBigDecimal("sub_total");
        salesTaxState = rs.getString("description");
        salesTaxRate = rs.getBigDecimal("sales_tax_rate");
        salesTaxCost = rs.getBigDecimal("sales_tax");
        debitAdjustment = rs.getBigDecimal("debit_adjustment");
        packagingCost = rs.getBigDecimal("packaging");
        freightCost = rs.getBigDecimal("freight");
        miscellaneousCost = rs.getBigDecimal("miscellaneous");
        creditAdjustment = rs.getBigDecimal("credit_adjustment");
        invoiceTotal = rs.getBigDecimal("invoice_total");
        ledgerId = rs.getBoolean("ledger_id");
      } else {
        cat.error(this.getClass().getName() + ": ciKey=" + ciKey + " : Unable to retrieve VENDOR details and PURCHASE ORDER number!");
        throw new Exception("Unable to retrieve VENDOR details and PURCHASE ORDER number!");
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
//--------------------------------------------------------------------
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
// Invoice Total $
//--------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.summary.invoiceTotal"), FontFactory.getFont(fontType, totalSize, Font.BOLD));
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
// End Of Customer Invoice
//--------------------------------------------------------------------
        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"invoice.report.message.endReport"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
