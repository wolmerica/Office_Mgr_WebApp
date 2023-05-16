/*
 * PdfBarCodeSheetAction.java
 *
 * Created on February 26, 2007, 8:35 PM
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
import java.util.ListIterator;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;

import org.apache.log4j.Logger;


public class PdfBarCodeSheetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int priceSize = 8;
  int fontSize = 6;
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
    PdfWriter writer = null;
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
          writer = PdfWriter.getInstance(document, response.getOutputStream());
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
        document.addTitle(getPropertyService().getCustomerProperties(request, "pricesheet.report.title"));
//--------------------------------------------------------------------------------
// step 3
//--------------------------------------------------------------------------------
        document.open();
//--------------------------------------------------------------------------------
// step 4
//--------------------------------------------------------------------------------
        doTraverseOrders(conn, document, writer, psKey);
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


private void doTraverseOrders(Connection conn,
                              Document document,
                              PdfWriter writer,
                              Integer psKey)
    throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

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
                   + "FROM customerinvoice,customerinvoiceitem,itemdictionary "
                   + "WHERE customerinvoice.scenario_key = 4 "
                   + "AND customerinvoice.sourcetype_key IN "
                                             + "(SELECT thekey "
                                             + "FROM accountingtype "
                                             + "WHERE name = 'SOURCE' "
                                             + "AND description = 'Price Sheet') "
                   + "AND customerinvoice.source_key = ? "
                   + "AND NOT customerinvoice.active_id "
                   + "AND customerinvoice.thekey = customerinvoice_key "
                   + "AND itemdictionary_key = itemdictionary.thekey "
                   + "ORDER by brand_name,size";
      ps = conn.prepareStatement(query);
      ps.setInt(1, psKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Prepare an SQL statement to get the RESALE computed price for WOLMERICA.
//--------------------------------------------------------------------------------
      query = "SELECT computed_price "
            + "FROM pricebyitem p, priceattributebyitem pb "
            + "WHERE p.priceattributebyitem_key = pb.thekey "
            + "AND pb.itemdictionary_key = ? "
            + "AND p.customertype_key = ? "
            + "AND pb.pricetype_key IN (SELECT thekey "
                                       + "FROM pricetype "
                                       + "WHERE full_size_id "
                                       + "AND unit_cost_base_id "
                                       + "AND active_id)";
      ps = conn.prepareStatement(query);

      try {
        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = { 20,20,20,20,20 };
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Chunk chunk = null;
        Phrase phrase = null;
        PdfPCell cell = null;

        String idPrice = "";
        String idName = "";
        String idSize = "";
        ArrayList<Phrase> info = new ArrayList<Phrase>();
        ListIterator iterator;
        int row = 0;
        String barCodeNumber = "";
        BarcodeEAN codeEAN = null;
        Image imageEAN = null;
        PdfContentByte cb = writer.getDirectContent();
//--------------------------------------------------------------------------------
// Pad the price sheet key with zeros to produce a 5 digit number.
//--------------------------------------------------------------------------------
        String psNumber = psKey.toString();
        if (psKey < 10)
          psNumber = "9786" + psNumber;
        else if (psKey < 100)
          psNumber = "978" + psNumber;
        else if (psKey < 1000)
          psNumber = "97" + psNumber;
        else if (psKey < 10000)
          psNumber = "9" + psNumber;
//--------------------------------------------------------------------------------
// Iterate through the items assigned to the price sheet.
//--------------------------------------------------------------------------------
        while (rs.next()) {
          row++;
          idName = " " + rs.getString("brand_name");
          idSize = " " + currencyFormatter.format(rs.getBigDecimal("size"))
                 + rs.getString("size_unit");
//--------------------------------------------------------------------------------
// Pass the item dictionary key and the primary buyer type to get the price.
//--------------------------------------------------------------------------------
          ps.setInt(1, rs.getInt("id_key"));
          ps.setByte(2, rs.getByte("customertype_key"));
          rs2 = ps.executeQuery();
          if (rs2.next())
            idPrice = "$" + currencyFormatter.format(rs2.getBigDecimal("computed_price"));
          else
            throw new Exception("Computed price not found!");

//--------------------------------------------------------------------------------
// Pad the item dictionary with zeros to produce a 8 digit item code.
//--------------------------------------------------------------------------------
          barCodeNumber = rs.getString("id_key");
          if (rs.getInt("id_key") < 10)
            barCodeNumber = "7978564" + barCodeNumber;
          else if (rs.getInt("id_key") < 100)
            barCodeNumber = "697856" + barCodeNumber;
          else if (rs.getInt("id_key") < 1000)
            barCodeNumber = "59785" + barCodeNumber;
          else if (rs.getInt("id_key") < 10000)
            barCodeNumber = "4978" + barCodeNumber;
          else if (rs.getInt("id_key") < 100000)
            barCodeNumber = "397" + barCodeNumber;
          else if (rs.getInt("id_key") < 1000000)
            barCodeNumber = "29" + barCodeNumber;
          else
            barCodeNumber = "1" + barCodeNumber;

          barCodeNumber = psNumber + barCodeNumber;
          cat.debug(this.getClass().getName() + ": barCodeNumber: " + barCodeNumber);

//--------------------------------------------------------------------------------
// Generate the bar code following the EAN8 code type.
//--------------------------------------------------------------------------------
          codeEAN = new BarcodeEAN();
//        codeEAN.setCodeType(Barcode.EAN8);
          codeEAN.setCodeType(Barcode.EAN13);
//        codeEAN.setCodeType(Barcode.CODE128);
          codeEAN.setCode(barCodeNumber);
          imageEAN = codeEAN.createImageWithBarcode(cb, null, null);
//--------------------------------------------------------------------------------
// Put the collected brand name, size, and bar code value into the PDF table.
//--------------------------------------------------------------------------------
          chunk = new Chunk(imageEAN, 0, 0);
          phrase = new Phrase(chunk);
          info.add(phrase);
//--------------------------------------------------------------------------------
// Assign the price, brand, and size to a phrase.
//--------------------------------------------------------------------------------
          chunk = new Chunk(idPrice, FontFactory.getFont(fontType, priceSize, Font.BOLD));
          phrase = new Phrase(chunk);
          chunk = new Chunk(idName, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          phrase.add(chunk);
          chunk = new Chunk(idSize, FontFactory.getFont(fontType, fontSize, Font.NORMAL));
          phrase.add(chunk);
          cell = new PdfPCell(phrase);
          cell.setBorder(0);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Output the price, brand, and size info to the table following the bar codes.
//--------------------------------------------------------------------------------
          if (row == 5) {
            iterator = info.listIterator();
            do {
//--------------------------------------------------------------------------------
// Assigns the 'next' value in the ArrayList to phrase temp.
// Rrom here you would go about doing something with each of these objects
//--------------------------------------------------------------------------------
              phrase = (Phrase) iterator.next();
              cell = new PdfPCell(phrase);
              cell.setBorder(0);
              datatable.addCell(cell);
            } while (iterator.hasNext());
            row=0;
	    info.clear();
	  }
        }
//--------------------------------------------------------------------------------
// Complete the row to display the price, brand name, and size values below.
//--------------------------------------------------------------------------------
        cat.debug(this.getClass().getName() + ": wrapping up");
        int fill = row;
        while (fill++ != 5) {
          datatable.addCell("");
        }
//--------------------------------------------------------------------------------
// Output the price, brand, and size info to the table following the bar codes.
//--------------------------------------------------------------------------------
        cat.debug(this.getClass().getName() + ": final iterator");
        if(!(info.isEmpty())) {
          iterator = info.listIterator();
          do {
            phrase = (Phrase) iterator.next();
            cell = new PdfPCell(phrase);
            cell.setBorder(0);
            datatable.addCell(cell);
          } while (iterator.hasNext());
        }
//--------------------------------------------------------------------------------
// Complete the row if necessary.
//--------------------------------------------------------------------------------
        while (row++ != 5) {
          datatable.addCell("");
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
        throw new Exception("Request getParameter/getAttribute [key] not found!");
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
