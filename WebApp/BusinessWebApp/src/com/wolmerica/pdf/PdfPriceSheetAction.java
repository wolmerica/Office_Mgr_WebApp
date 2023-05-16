/*
 * PdfPriceSheetAction.java
 *
 * Created on July 4, 2005, 8:35 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 02/06/2006 - Add page headers for all the pages and make sure the order headings
 *              only appear at the bottom of the page with multiple row items.
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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.log4j.Logger;


public class PdfPriceSheetAction extends Action {

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
                                Integer psKey)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    String createUser = (String) request.getSession().getAttribute("USERNAME");
//--------------------------------------------------------------------------------
// we retrieve the presentationtype
//--------------------------------------------------------------------------------
    String presentationtype = request.getParameter("presentationType");
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
        if ("pdf".equals(presentationtype)) {
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
        document.addTitle(getPropertyService().getCustomerProperties(request,"pricesheet.report.title"));
//--------------------------------------------------------------------------------
// step 3
//--------------------------------------------------------------------------------
        document.open();
//--------------------------------------------------------------------------------
// step 4
//--------------------------------------------------------------------------------
        Integer pageCount = new Integer(getPropertyService().getCustomerProperties(request,"pricesheet.report.page.count"));
        int currentPage = 1;
        doPageHeader(request, document, currentPage);
        doTraverseOrders(request, conn, document, psKey, pageCount, currentPage);
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

  private void doPageHeader(HttpServletRequest request,
                            Document document,
                            int currentPage)
   throws Exception, IOException, SQLException {

    try {
      Timestamp postStamp = new Timestamp(new Date().getTime());
      String dateString = postStamp.toString().substring(5,7) + "/"
                        + postStamp.toString().substring(8,10)+ "/"
                        + postStamp.toString().substring(0,4);

      PdfPTable datatable = new PdfPTable(4);
      int headerwidths[] = { 15,30,40,15 }; // percentage
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
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      datatable.addCell(new Phrase(chunk));
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.page"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      Phrase phrase = new Phrase(chunk);
      chunk = new Chunk(" " + currentPage, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);
//--------------------------------------------------------------------------------
// The second line will skip the first three cells and list the report date only.
//--------------------------------------------------------------------------------
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.date"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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

private void doTraverseOrders(HttpServletRequest request,
                              Connection conn,
                              Document document,
                              Integer psKey,
                              Integer pageCount,
                              int currentPage)
  throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "SELECT customerinvoice.thekey AS ci_key,"
                   + "purchaseorder.purchase_order_num,"
                   + "DATE(purchaseorder.update_stamp) AS po_date,"
                   + "vendor.name AS vendor_name,"
                   + "vendorinvoice.invoice_num AS vendor_invoice_num "
                   + "FROM purchaseorder,vendorinvoice,customerinvoice,vendor "
                   + "WHERE customerinvoice.scenario_key = 4 "
                   + "AND customerinvoice.sourcetype_key IN "
                                             + "(SELECT thekey "
                                             + "FROM accountingtype "
                                             + "WHERE name = 'SOURCE' "
                                             + "AND description = 'Price Sheet') "
                   + "AND customerinvoice.source_key = ? "
                   + "AND NOT customerinvoice.active_id "
                   + "AND vendorinvoice_key=vendorinvoice.thekey "
                   + "AND purchaseorder_key=purchaseorder.thekey "
                   + "AND vendor_key=vendor.thekey "
                   + "ORDER by po_date,purchase_order_num,vendorinvoice.invoice_num";
      ps = conn.prepareStatement(query);
      ps.setInt(1, psKey);
      rs = ps.executeQuery();

      try {
//--------------------------------------------------------------------------------
// New inventory rows from the customer invoice table.
//--------------------------------------------------------------------------------
        int recordCount = 2;
        while ( rs.next() ) {
          if (recordCount + 6 > pageCount) {
            currentPage++;
            recordCount = 2;
            document.add(Chunk.NEXTPAGE);
            doPageHeader(request, document, currentPage);
          }
          doOrderHeader(request, conn,document, rs.getInt("ci_key"));
          doOrderItemHeader(request, document);
          recordCount = recordCount + 4;
          ArrayList sheetInfo = doOrderItemDetail(request,
                                                  conn,
                                                  document,
                                                  rs.getInt("ci_key"),
                                                  pageCount,
                                                  recordCount,
                                                  currentPage);
          recordCount = new Integer(sheetInfo.get(0).toString()).intValue();
          currentPage = new Integer(sheetInfo.get(1).toString()).intValue();
        }
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

private void doOrderHeader(HttpServletRequest request,
                           Connection conn,
                           Document document,
                           Integer ciKey)
  throws Exception, IOException, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      PdfPTable datatable = new PdfPTable(5);
      int headerwidths[] = { 15,15,40,15,15 };
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(1);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      String poNumber = "";
      String poDate = "";
      String vendorName = "";
      String viNumber = "";

      String query = "SELECT purchaseorder.purchase_order_num,"
                   + "DATE(purchaseorder.update_stamp) AS po_date,"
                   + "vendor.name AS vendor_name,"
                   + "vendorinvoice.invoice_num AS vendor_invoice_num "
                   + "FROM purchaseorder,vendorinvoice,customerinvoice,vendor "
                   + "WHERE customerinvoice.thekey = ? "
                   + "AND vendorinvoice_key=vendorinvoice.thekey "
                   + "AND purchaseorder_key=purchaseorder.thekey "
                   + "AND vendor_key=vendor.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();
      if (rs.next()) {
		poNumber = rs.getString("purchase_order_num");
		poDate = rs.getString("po_date");
		vendorName = rs.getString("vendor_name");
		viNumber = rs.getString("vendor_invoice_num");
      }
      else {
        throw new Exception("Customer invoice key: " + ciKey + " not found!");
      }

      try {
        Chunk chunk = null;
        PdfPCell cell = null;
// Blank line between Order Header and the previous text.
        chunk = new Chunk(" ");
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(5);
        datatable.addCell(cell);

        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.poNumber"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

        chunk = new Chunk(poNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.poDate"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

        chunk = new Chunk(poDate, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.vendorName"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

        chunk = new Chunk(vendorName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(2);
        datatable.addCell(cell);

        chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.vendorInvoiceNumber"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        datatable.addCell(cell);

        chunk = new Chunk(viNumber, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
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

  private void doOrderItemHeader(HttpServletRequest request,
                                 Document document)
    throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(9);
      int headerwidths[] = { 30,10,10,8,8,8,8,10,10 };
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(1);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      Chunk chunk = null;
      PdfPCell cell = null;

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head8"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"pricesheet.report.lineitem.line1.head9"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
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

  private ArrayList doOrderItemDetail(HttpServletRequest request,
                                      Connection conn,
                                      Document document,
                                      Integer ciKey,
                                      Integer pageCount,
                                      int recordCount,
                                      int currentPage)
    throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    Float fShade = new Float("0.80f");

    ArrayList<Object> sheetInfo = new ArrayList<Object>();
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    try {
//--------------------------------------------------------------------------------
// Prepare a SQL statement to select all the items from a specific stock invoice.
//--------------------------------------------------------------------------------
      String query = "SELECT itemdictionary.thekey AS id_key,"
                   + "itemdictionary.brand_name,"
                   + "itemdictionary.size,"
                   + "itemdictionary.size_unit,"
                   + "itemdictionary.lastuncost,"
                   + "itemdictionary.unit_cost,"
                   + "itemdictionary.customertype_key "
                   + "FROM customerinvoiceitem,itemdictionary "
                   + "WHERE customerinvoice_key = ? "
                   + "AND itemdictionary_key = itemdictionary.thekey "
                   + "ORDER by brand_name,size";
      ps = conn.prepareStatement(query);
      ps.setInt(1, ciKey);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Prepare a SQL statement to get the price type name and price attribute by item size.
//--------------------------------------------------------------------------------
      query = "SELECT priceattributebyitem.thekey AS pabi_key,"
            + "name AS pt_name,"
            + "size AS pabi_size "
            + "FROM pricetype, priceattributebyitem "
            + "WHERE pricetype_key = pricetype.thekey "
            + "AND itemdictionary_key =? "
            + "AND pricetype.thekey > 0";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Prepare a SQL statement eligible customer types for price sheet presentation.
//--------------------------------------------------------------------------------
      query = "SELECT customertype.thekey AS ct_key,"
            + "customertype.name AS ct_name,"
            + "customerattributebyitem.label_cost,"
            + "customerattributebyitem.admin_cost,"
            + "pricebyitem.computed_price,"
            + "pricebyitem.over_ride_price "
            + "FROM customertype, customerattributebyitem, pricebyitem "
            + "WHERE pricesheet_id "
            + "AND customerattributebyitem.customertype_key = customertype.thekey "
            + "AND itemdictionary_key = ? "
            + "AND pricebyitem.customertype_key = customertype.thekey "
            + "AND priceattributebyitem_key = ?";
      ps2 = conn.prepareStatement(query);

      try {
        PdfPTable datatable = new PdfPTable(9);
        int headerwidths[] = { 30,10,10,8,8,8,8,10,10 };
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(1);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        PdfPCell cell = null;

        String idName = "";
        String idSize = "";
        Byte idCustomerType = null;
        String ctName = "";
        String prevUnitCost = "";
        String unitCost = "";
        String ptName = "";
        String pabiSize = "";
        while ( rs.next() ) {
          if (recordCount >= pageCount) {
            recordCount = 6;
            currentPage++;
            document.add(datatable);
            document.add(Chunk.NEXTPAGE);
            doPageHeader(request, document, currentPage);
            doOrderHeader(request, conn, document, ciKey);
            doOrderItemHeader(request, document);

            datatable = new PdfPTable(9);
            datatable.setWidths(headerwidths);
            datatable.setWidthPercentage(100); // percentage
            datatable.getDefaultCell().setPadding(1);
            datatable.getDefaultCell().setBorderWidth(0);
            datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
          }

          idName = rs.getString("brand_name");
          idSize = currencyFormatter.format(rs.getBigDecimal("size")) + rs.getString("size_unit");
          prevUnitCost = currencyFormatter.format(rs.getBigDecimal("lastuncost"));
          unitCost = currencyFormatter.format(rs.getBigDecimal("unit_cost"));
          idCustomerType = rs.getByte("customertype_key");

          ps.setInt(1, rs.getInt("id_key"));
          rs2 = ps.executeQuery();
          while ( rs2.next() ) {
            ptName = "***** " + rs2.getString("pt_name");
            pabiSize = currencyFormatter.format(rs2.getBigDecimal("pabi_size")) + rs.getString("size_unit");
            ps2.setInt(1, rs.getInt("id_key"));
            ps2.setInt(2, rs2.getInt("pabi_key"));
            rs3 = ps2.executeQuery();
            while ( rs3.next() ) {
              ++recordCount;
              if (recordCount % 2 == 0)
                fShade = new Float("0.9f");
              else
                fShade = new Float("0.80f");
                datatable.getDefaultCell().setGrayFill(0.0f);
//--------------------------------------------------------------------------------
// Add an asterick next to the customer type name when it is the primary buyer.
//--------------------------------------------------------------------------------
              ctName = rs3.getString("ct_name");
              if (idCustomerType == rs3.getByte("ct_key"))
                ctName = "*" + ctName;

              if (rs.getString("size").compareToIgnoreCase(rs2.getString("pabi_size")) == 0) {
                chunk = new Chunk(idName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                datatable.addCell(cell);
                chunk = new Chunk(idSize, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                datatable.addCell(cell);
              }
              else {
                chunk = new Chunk(ptName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                datatable.addCell(cell);
                chunk = new Chunk(pabiSize, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setGrayFill(fShade);
                datatable.addCell(cell);
              }

              chunk = new Chunk(ctName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              datatable.addCell(cell);

              chunk = new Chunk(currencyFormatter.format(rs3.getBigDecimal("label_cost")), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
              datatable.addCell(cell);

              chunk = new Chunk(currencyFormatter.format(rs3.getBigDecimal("admin_cost")), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
              datatable.addCell(cell);

              chunk = new Chunk(currencyFormatter.format(rs3.getBigDecimal("computed_price")), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
              datatable.addCell(cell);

              chunk = new Chunk(currencyFormatter.format(rs3.getBigDecimal("over_ride_price")), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
              datatable.addCell(cell);

              chunk = new Chunk(prevUnitCost, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
              datatable.addCell(cell);

              chunk = new Chunk(unitCost, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setGrayFill(fShade);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
              datatable.addCell(cell);

              idName = "";
              idSize = "";
              prevUnitCost = "";
              unitCost = "";
              ptName = "";
              pabiSize = "";
            }
          }
        }
        document.add(datatable);
        sheetInfo.add(recordCount);
        sheetInfo.add(currentPage);
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
      if (rs2 != null) {
        try { rs2.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        rs2 = null;
      }
      if (rs3 != null) {
        try { rs3.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        rs3 = null;
      }
      if (ps != null) {
        try { ps.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        ps = null;
      }
      if (ps2 != null) {
        try { ps2.close(); }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLError: " + sqle.getMessage());
        }
        ps2 = null;
      }
    }
    return sheetInfo;
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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

      doReportGenerator(request, response, theKey);
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
