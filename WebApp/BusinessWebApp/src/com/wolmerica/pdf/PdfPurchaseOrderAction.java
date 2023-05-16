/*
 * PdfPurchaseOrderAction.java
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
import com.wolmerica.service.purchaseorder.PurchaseOrderService;
import com.wolmerica.service.purchaseorder.DefaultPurchaseOrderService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.CurrencyFormatter;
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.log4j.Logger;


public class PdfPurchaseOrderAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int fontSize = 11;
  String fontType = FontFactory.TIMES_ROMAN;

  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public PurchaseOrderService getPurchaseOrderService() {
      return purchaseOrderService;
  }

  public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
      this.purchaseOrderService = purchaseOrderService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  
  public void doReportGenerator(HttpServletRequest request,
                                HttpServletResponse response,
                                Integer poKey,
                                String presentationType)
   throws Exception, IOException, SQLException {


    String createUser = (String) request.getSession().getAttribute("USERNAME");

//--------------------------------------------------------------------------------
// step 1 declare variables.
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

      HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, poKey);
      String poNum = pokMap.get("purchaseOrderNum").toString();
      Integer vKey = new Integer(pokMap.get("vendorKey").toString());
      Integer cKey = new Integer(pokMap.get("customerKey").toString());

      try {
//--------------------------------------------------------------------------------
// step 2: set the ContentType to pdf, htlm, or rtf.
//--------------------------------------------------------------------------------
        if ("pdf".equals(presentationType)) {
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
        document.addTitle("PO: " + poNum);
//--------------------------------------------------------------------------------
// step 3
//--------------------------------------------------------------------------------
        document.open();
//--------------------------------------------------------------------------------
// step 4
//--------------------------------------------------------------------------------
        Integer firstPageCount = new Integer(getPropertyService().getCustomerProperties(request,"po.report.first.page.count"));
        Integer nextPageCount = new Integer(getPropertyService().getCustomerProperties(request,"po.report.next.page.count"));
        int currentPage = 0;
        int lastPage = 0;
        int startRow = 1;
        int endRow = firstPageCount;
        lastPage = getPageCount(conn,
                                poKey,
                                firstPageCount,
                                nextPageCount);
        cat.debug(this.getClass().getName() + ": Last page=" + lastPage);
        while ( ++currentPage <= lastPage )
        {
//--------------------------------------------------------------------------------
// Advance to the next page if more line items to display.
//--------------------------------------------------------------------------------
          if (currentPage > 1)
            document.add(Chunk.NEXTPAGE);

          doPageHeader(request, document, currentPage, lastPage, poNum);
//--------------------------------------------------------------------------------
// Only display Vendor and Customer details on the first page.
//--------------------------------------------------------------------------------
          if (currentPage == 1)
          {
            doVendorDetail(request, conn, document, vKey);
            doCustomerDetail(request, conn, document, cKey);
          }

          doItemHeader(request, document);
          cat.debug(this.getClass().getName() + ": Start=" + startRow + " End=" + endRow);
          doPurchaseOrderDetail(request, conn, document,
                                poKey, startRow, endRow);
          startRow = endRow + 1;
          endRow = (endRow + nextPageCount) - 1;
        }
        doPageFooter(request, conn, document, cKey);
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
                          Integer poKey,
                          Integer firstPageCount,
                          Integer nextPageCount)
   throws Exception, IOException, SQLException {

    int totalPageCount = 1;
    int orderItemCount = 0;
    int orderServiceCount = 0;
    int fullPageCount = 0;

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the purchase order item count.
//--------------------------------------------------------------------------------        
      String query = "SELECT count(*) as rowCount "
                   + "FROM purchaseorderitem "
                   + "WHERE purchaseorder_key=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      rs = ps.executeQuery();
      if (rs.next()) {
        orderItemCount = rs.getInt("rowCount");
      }
      
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the purchase order service count.
//--------------------------------------------------------------------------------
      query = "SELECT COUNT(*) AS rowCount "
            + "FROM purchaseorderservice "
            + "WHERE purchaseorder_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      rs = ps.executeQuery();
      if (rs.next()) {
        orderServiceCount = rs.getInt("rowCount");
      }
      cat.debug(this.getClass().getName() + ": Item Count = " + orderItemCount);
      cat.debug(this.getClass().getName() + ": Service Count = " + orderServiceCount);      
      
//--------------------------------------------------------------------------------
// Is there more than one page of order item and service detail?
//--------------------------------------------------------------------------------      
      int orderTotalCount = orderItemCount + orderServiceCount;
      if (orderTotalCount > firstPageCount)
      {
        orderTotalCount = orderTotalCount - firstPageCount;
          
//--------------------------------------------------------------------------------                
// Figure out how many more pages are needed to present detail.
//--------------------------------------------------------------------------------          
        fullPageCount = orderTotalCount/nextPageCount;
        totalPageCount = totalPageCount + fullPageCount;
        if (orderTotalCount % nextPageCount > 0)
          ++totalPageCount;
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
    return totalPageCount;
  }

  private void doPurchaseOrderDetail(HttpServletRequest request,
                                     Connection conn,
                                     Document document,
                                     Integer poKey,
                                     int firstRecordCount,
                                     int lastRecordCount)
   throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    IntegerFormatter integerFormatter = new IntegerFormatter();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "(SELECT poi.thekey,"
                   + "id.brand_name,"
                   + "id.size_unit,"
                   + "id.dose,"
                   + "id.manufacturer,"
                   + "id.item_num,"
                   + "poi.order_qty,"
                   + "id.first_cost "
                   + "FROM purchaseorderitem poi, itemdictionary id "
                   + "WHERE poi.purchaseorder_key = ? "
                   + "AND poi.itemdictionary_key = id.thekey) "
                   + " UNION "
                   + "(SELECT pos.thekey,"
                   + "sd.name AS brand_name,"
                   + "pt.name AS size_unit,"
                   + "0 AS dose,"
                   + "' ' AS manufacturer,"
                   + "sd.service_num AS item_num,"
                   + "pos.order_qty,"
                   + "sd.labor_cost AS first_cost "
                   + "FROM purchaseorderservice pos, servicedictionary sd, pricetype pt "
                   + "WHERE pos.purchaseorder_key = ? "
                   + "AND pos.servicedictionary_key = sd.thekey "
                   + "AND pos.pricetype_key = pt.thekey)";
      cat.debug(this.getClass().getName() + ": Query = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      ps.setInt(2, poKey);      
      rs = ps.executeQuery();

      try {
        PdfPTable datatable = new PdfPTable(7);
        int headerwidths[] = { 41, 12, 13, 10, 6, 8, 10 }; 
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); 
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        int recordCount = 0;
        Chunk chunk = null;
        PdfPCell cell = null;
        String manufacturer = "";
        cat.debug(this.getClass().getName() + ": poKey = " + poKey);
        while ( rs.next() && ++recordCount <=lastRecordCount ) {
          cat.debug(this.getClass().getName() + ": recordCount = " + recordCount);
          if (recordCount >= firstRecordCount)
          {
            cat.debug(this.getClass().getName() + ": name = " + rs.getString("brand_name"));
            chunk = new Chunk(rs.getString("brand_name"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
	    datatable.addCell(new Phrase(chunk));
            cat.debug(this.getClass().getName() + ": manufacturer = " + rs.getString("manufacturer"));
            manufacturer = rs.getString("manufacturer");
            if (manufacturer.length() > 7)
              manufacturer = manufacturer.substring(0,7);
            chunk = new Chunk(manufacturer, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            datatable.addCell(new Phrase(chunk));
            cat.debug(this.getClass().getName() + ": number = " + rs.getString("item_num"));
            chunk = new Chunk(rs.getString("item_num"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            datatable.addCell(new Phrase(chunk));
            cat.debug(this.getClass().getName() + ": size_unit = " + rs.getString("size_unit"));
            chunk = new Chunk(rs.getString("size_unit"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    datatable.addCell(cell);
            cat.debug(this.getClass().getName() + ": dose = " + rs.getBigDecimal("dose"));
            chunk = new Chunk(currencyFormatter.format(rs.getBigDecimal("dose")), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    datatable.addCell(cell);
            cat.debug(this.getClass().getName() + ": order qty = " + rs.getBigDecimal("order_qty"));
            chunk = new Chunk(integerFormatter.format(rs.getBigDecimal("order_qty")), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    datatable.addCell(cell);

            chunk = new Chunk(currencyFormatter.format(rs.getBigDecimal("first_cost")), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    datatable.addCell(cell);
            cat.debug(this.getClass().getName() + ": end of while loop");            
          }
        }
        document.add(datatable);
        cat.debug(this.getClass().getName() + ": Finished details");
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

  private void doPageHeader(HttpServletRequest request,
                           Document document,
                           int currentPage,
                           int lastPage,
                           String poNumber)
   throws Exception, IOException, SQLException {

    try {
      Timestamp postStamp = new Timestamp(new Date().getTime());
      String dateString = postStamp.toString().substring(5,7) + "/"
                        + postStamp.toString().substring(8,10)+ "/"
                        + postStamp.toString().substring(0,4);

      PdfPTable datatable = new PdfPTable(3);
      int headerwidths[] = { 30, 40, 30 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.date"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      Phrase phrase = new Phrase(chunk);
      chunk = new Chunk(" " + dateString, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);
      datatable.addCell("");
      chunk = new Chunk("                            " + getPropertyService().getCustomerProperties(request,"po.report.page"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      phrase = new Phrase(chunk);
      chunk = new Chunk(" " + currentPage + " of " + lastPage, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.poNumber"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      phrase = new Phrase(chunk);
      chunk = new Chunk(" " + poNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);
      datatable.addCell("");
      datatable.addCell("");

      chunk = new Chunk("     " + getPropertyService().getCustomerProperties(request,"po.report.header1"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
/** Define a cell to allow data to span across multiple cells **/
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      datatable.addCell(cell);

      chunk = new Chunk("     " + getPropertyService().getCustomerProperties(request,"po.report.header2"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(3);
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.printStackTrace();
      cat.error(this.getClass().getName() + ": document: " + de.getMessage());
    }
  }

  private void doVendorDetail(HttpServletRequest request,
                              Connection conn,
                              Document document,
                              Integer vKey)
   throws Exception, IOException, SQLException {
    PhoneNumberFormatter pnf = new PhoneNumberFormatter();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "SELECT name,"
                   + "contact_name,"
                   + "phone_num,"
                   + "fax_num,"
                   + "acct_num "
                   + "FROM vendor "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, vKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {

        try {
          PdfPTable datatable = new PdfPTable(3);
          int headerwidths[] = { 15, 20, 65 }; // percentage
          datatable.setWidths(headerwidths);
          datatable.setWidthPercentage(100); // percentage
          datatable.getDefaultCell().setPadding(2);
          datatable.getDefaultCell().setBorderWidth(0);
          datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

          Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.vendor.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          chunk.setUnderline(0.2f, -2f);
          datatable.addCell(new Phrase(chunk));
          datatable.addCell("");
          datatable.addCell("");

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.vendor.company"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
          datatable.addCell(new Phrase(chunk));
          chunk = new Chunk(rs.getString("name"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          datatable.addCell(new Phrase(chunk));

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.vendor.contact"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          datatable.addCell(new Phrase(chunk));
          chunk = new Chunk(rs.getString("contact_name"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          datatable.addCell(new Phrase(chunk));

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.vendor.phone"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          datatable.addCell(new Phrase(chunk));
          chunk = new Chunk(pnf.format(rs.getString("phone_num")), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          datatable.addCell(new Phrase(chunk));

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.vendor.fax"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          datatable.addCell(new Phrase(chunk));
          chunk = new Chunk(pnf.format(rs.getString("fax_num")), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          datatable.addCell(new Phrase(chunk));

          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          chunk.setUnderline(0.2f, -2f);
          datatable.addCell(new Phrase(chunk));
          datatable.addCell("");
          datatable.addCell("");

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.account"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          datatable.addCell(new Phrase(chunk));
          chunk = new Chunk(rs.getString("acct_num"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          datatable.addCell(new Phrase(chunk));

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.company"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          PdfPCell cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.address"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.citystzip"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
     	  datatable.addCell(cell);

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.phone"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.contact"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);

          datatable.addCell("");
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.bill.contact2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(2);
          datatable.addCell(cell);

          document.add(datatable);
        }
        catch(DocumentException de) {
          de.printStackTrace();
          cat.debug(this.getClass().getName() + ": document = " + de.getMessage());
        }
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

  private void doCustomerDetail(HttpServletRequest request,
                                Connection conn,
                                Document document,
                                Integer cKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "SELECT code_num, ship_to, address, "
                   + "address2, city, state, zip "
                   + "FROM customer "
                   + "WHERE thekey = ?";

      ps = conn.prepareStatement(query);

      ps.setInt(1, cKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        try {
          PdfPTable datatable = new PdfPTable(3);
          int headerwidths[] = { 15, 20, 65 }; // percentage
          datatable.setWidths(headerwidths);
          datatable.setWidthPercentage(100); // percentage
          datatable.getDefaultCell().setPadding(2);
          datatable.getDefaultCell().setBorderWidth(0);
          datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

          Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.customer.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          chunk.setUnderline(0.2f, -2f);
          datatable.addCell(new Phrase(chunk));
          datatable.addCell("");
          datatable.addCell("");

          if (cKey == 0)
          {
            datatable.addCell("");
            chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.customer.name"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
	    datatable.addCell(new Phrase(chunk));
            chunk = new Chunk(rs.getString("ship_to"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
	    datatable.addCell(new Phrase(chunk));
          }
          else
          {
            datatable.addCell("");
            chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.customer.codeNumber"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
	    datatable.addCell(new Phrase(chunk));
            chunk = new Chunk(rs.getString("code_num"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
	    datatable.addCell(new Phrase(chunk));

            datatable.addCell("");
            chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.customer.name"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
	    datatable.addCell(new Phrase(chunk));
            chunk = new Chunk(rs.getString("ship_to"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
	    datatable.addCell(new Phrase(chunk));

            datatable.addCell("");
	    datatable.addCell("");
            chunk = new Chunk(rs.getString("address"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
	    datatable.addCell(new Phrase(chunk));

            if (rs.getString("address2").length() > 0)
            {
            datatable.addCell("");
	    datatable.addCell("");
            chunk = new Chunk(rs.getString("address2"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
	    datatable.addCell(new Phrase(chunk));
            }

            if (rs.getString("city").length() > 0)
            {
              chunk = new Chunk(rs.getString("city"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              Phrase phrase = new Phrase(chunk);
              chunk = new Chunk(", " + rs.getString("state"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              phrase.add(chunk);
              chunk = new Chunk(" " + rs.getString("zip"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              phrase.add(chunk);
              datatable.addCell("");
	      datatable.addCell("");
	      datatable.addCell(phrase);
            }
          }
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.attention"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
	  datatable.addCell("");
          datatable.addCell(new Phrase(chunk));
          datatable.addCell("");

          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
          chunk.setUnderline(0.2f, -2f);
          datatable.addCell(new Phrase(chunk));
          datatable.addCell("");
          datatable.addCell("");

          document.add(datatable);
        }
        catch(DocumentException de) {
           de.printStackTrace();
           cat.debug(this.getClass().getName() + ": document = " + de.getMessage());
        }
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

private void doItemHeader(HttpServletRequest request,
                          Document document)
   throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(7);
      int headerwidths[] = { 41, 12, 13, 10, 6, 8, 10 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      // The lineitem.title is displayed once in the doCustomerDetail.
      Chunk chunk = null;
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");

      datatable.addCell("");
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line1.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      datatable.addCell(new Phrase(chunk));
      datatable.addCell("");
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line1.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line1.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.lineitem.line2.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
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
                            Integer cKey)
   throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
    String shipType = new String("drop");

    try {
      String query = "SELECT clinic_id "
                   + "FROM customer "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, cKey);
      rs = ps.executeQuery();

      if (rs.next()) {
		if (rs.getBoolean("clinic_id"))
          shipType = new String("clinic");
      }
      else {
        throw new Exception("Customer  " + cKey.toString() + " not found!");
      }

      try {
        PdfPTable datatable = new PdfPTable(4);
        int headerwidths[] = { 15, 20, 20, 45}; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

// Put a blank row between line items and the Message header.
        datatable.addCell("");
        datatable.addCell("");
        datatable.addCell("");
        datatable.addCell("");

        Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.report.message.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        chunk.setUnderline(0.2f, -2f);
/** Define a cell to allow data to span across multiple cells **/
        PdfPCell cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(4);
        datatable.addCell(cell);

// Put a blank row between message header and the messages.
        datatable.addCell("");
        datatable.addCell("");
        datatable.addCell("");
        datatable.addCell("");

        Integer messageCount = new Integer("0");
        Integer messageMax = new Integer(getPropertyService().getCustomerProperties(request,"po.report.message.count"));
        String messageKey = null;
        String message = null;
        while (++messageCount <= messageMax)
        {
          messageKey = "po.report.message." + shipType + ".line" + messageCount;
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
// Purchase Order Key
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

//--------------------------------------------------------------------------------
// iText presentation type
//--------------------------------------------------------------------------------
      String theType = null;
      if (!(request.getParameter("presentationType") == null)) {
        theType = new String(request.getParameter("presentationType"));
      }
      else {
        throw new Exception("Request getParameter [presentationType] not found!");
      }

      doReportGenerator(request,
                        response,
                        theKey,
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
