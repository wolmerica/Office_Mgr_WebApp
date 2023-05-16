/*
 * PdfItemInventoryAction.java
 *
 * Created on April 05, 2006, 8:56 PM
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
import com.wolmerica.itemdictionary.ItemDictionaryDO;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
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
import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;
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


public class PdfItemInventoryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int fontSize = 9;
  String fontType = FontFactory.TIMES_ROMAN;

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
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
                                String presentationType)
   throws Exception, IOException, SQLException {


    String createUser = (String) request.getSession().getAttribute("USERNAME");

//--------------------------------------------------------------------------------
// step 1: define a landscape document.
//--------------------------------------------------------------------------------
    Document document = new Document(PageSize.A4.rotate());
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
//--------------------------------------------------------------------------------
// step 3
//--------------------------------------------------------------------------------
        document.open();
//--------------------------------------------------------------------------------
// step 4
//--------------------------------------------------------------------------------
        Integer firstPageCount = new Integer(getPropertyService().getCustomerProperties(request,"inventory.report.first.page.count"));

        doPageHeader(request, document, 1);
        doItemHeader(request, document);
        doInventoryValueDetail(request, conn, document);
        doPageFooter(request, conn, document);
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

  private void doInventoryValueDetail(HttpServletRequest request,
                                      Connection conn,
                                      Document document)
   throws Exception, IOException, SQLException {

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    Float fShade = new Float("0.80f");

    PreparedStatement ps = null;
    ResultSet rs = null;

    String vendorName = "";
    Integer orderedQty = null;
    Integer recommendedQty = null;
    BigDecimal stockValue = new BigDecimal("0");
    BigDecimal recommendedCost = new BigDecimal("0");
    BigDecimal vendorStockValue = new BigDecimal("0");
    BigDecimal vendorRecommendedCost = new BigDecimal("0");
    BigDecimal totalStockValue = new BigDecimal("0");
    BigDecimal totalRecommendedCost = new BigDecimal("0");

    try {
//--------------------------------------------------------------------------------
// Prepare a SQL query to select all the rows from item dictionary.
//--------------------------------------------------------------------------------
      String query = "SELECT vendor.name,"
                   + "itemdictionary.thekey,"
                   + "itemdictionary.brand_name,"
                   + "itemdictionary.size,"
                   + "itemdictionary.size_unit,"
                   + "itemdictionary.generic_name,"
                   + "itemdictionary.item_num,"
                   + "itemdictionary.manufacturer,"
                   + "itemdictionary.report_id,"
                   + "itemdictionary.item_name,"
                   + "itemdictionary.first_cost,"
                   + "itemdictionary.unit_cost "
                   + "FROM itemdictionary, vendor "
                   + "WHERE vendor_key = vendor.thekey "
                   + "ORDER by vendor.name, generic_name, brand_name";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Instantiate the item and service method to retrieve threshold quantity
// inventory quantity, ordered quantity, and forecast quantity
//--------------------------------------------------------------------------------
      ItemDictionaryDO formDO = new ItemDictionaryDO();
      Integer forecastDays = new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.forecast.days"));
//--------------------------------------------------------------------------------
// Get the quantity on hand, ordered quantity, and forecast quantity values.
//--------------------------------------------------------------------------------
      try {
        PdfPTable datatable = new PdfPTable(10);
        int headerwidths[] = { 32, 30, 10, 13, 6, 9, 6, 6, 6, 8 }; // percentage
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(100); // percentage
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        Integer pageNumber = 1;
        int lineCount = 4;
        int vendorCount = 1;
        Chunk chunk = null;
        PdfPCell cell = null;
        while (rs.next()) {
//--------------------------------------------------------------------------------
// Truncate values that exceed the length of the display field.
//--------------------------------------------------------------------------------
          formDO.setVendorName(rs.getString("name"));
          formDO.setKey(rs.getInt("thekey"));
          formDO.setBrandName(rs.getString("brand_name"));
          formDO.setGenericName(rs.getString("generic_name"));
          formDO.setItemNum(rs.getString("item_num"));
          formDO.setManufacturer(rs.getString("manufacturer"));
          formDO.setReportId(rs.getByte("report_id"));
          formDO.setItemName(rs.getString("item_name"));
          formDO.setSize(rs.getBigDecimal("size"));
          formDO.setSizeUnit(rs.getString("size_unit"));
          formDO.setFirstCost(rs.getBigDecimal("first_cost"));
          formDO.setUnitCost(rs.getBigDecimal("unit_cost"));

//--------------------------------------------------------------------------------
// Retrieve the threshold, inventory, ordered, and forecast quantity for the item.
//--------------------------------------------------------------------------------
          formDO = getItemAndSrvService().getItemInventory(conn,
                                                           formDO,
                                                           forecastDays);

//--------------------------------------------------------------------------------
// Retrieve the recommended purchase order quantity from the current inventory state.
//--------------------------------------------------------------------------------
          recommendedQty = getItemAndSrvService().getRecommendedPOQuantity(formDO.getKey(),
                                                         formDO.getQtyOnHand().intValue(),
                                                         formDO.getOrderedQty().intValue(),
                                                         formDO.getForecastQty().intValue(),
                                                         formDO.getOrderThreshold().intValue());

//--------------------------------------------------------------------------------
// Calculate the aproximate value of the inventory in stock as well as the recommeded order.
//--------------------------------------------------------------------------------
          stockValue = formDO.getQtyOnHand().multiply(formDO.getUnitCost());
          recommendedCost = new BigDecimal(recommendedQty).multiply(formDO.getFirstCost());

//--------------------------------------------------------------------------------
// Only display the rows with inventory, ordered, forecast, or recommended quantity.
// Change the intValue() comparison to a BigDecimal compareTo to display any quantity
// that is greater than zero.  The intValue() would not display anything less than one.
//--------------------------------------------------------------------------------
          if ((formDO.getQtyOnHand().compareTo(new BigDecimal("0")) > 0) ||
             (formDO.getOrderedQty().compareTo(new BigDecimal("0")) > 0) ||
             (formDO.getForecastQty().compareTo(new BigDecimal("0")) > 0) ||
             (recommendedQty > 0 )) {

//--------------------------------------------------------------------------------
// Check if the page is full and if so advance to the next page.
//--------------------------------------------------------------------------------
            if (lineCount > 39) {
cat.debug(this.getClass().getName() + ": (1) lineCount : " + lineCount);
              ++pageNumber;
              document.add(datatable);
              document.add(Chunk.NEXTPAGE);
              lineCount = 6;
              doPageHeader(request, document, pageNumber);
              doItemHeader(request, document);

              datatable = new PdfPTable(10);
              datatable.setWidths(headerwidths);
              datatable.setWidthPercentage(100); // percentage
              datatable.getDefaultCell().setPadding(1);
              datatable.getDefaultCell().setBorderWidth(0);
              datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

//--------------------------------------------------------------------------------
// Leave a blank line before the next vendor name.
//--------------------------------------------------------------------------------
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setColspan(10);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
              datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the new vendor name for the batch of items.
//--------------------------------------------------------------------------------
              chunk = new Chunk(formDO.getVendorName() + "  continued...", FontFactory.getFont(fontType, fontSize , Font.BOLD));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setColspan(10);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
              datatable.addCell(cell);
            }
//--------------------------------------------------------------------------------
// Found a new vendor name for the batch of items.
//--------------------------------------------------------------------------------
            if (vendorName.compareToIgnoreCase(formDO.getVendorName()) != 0) {
              if (vendorName.equalsIgnoreCase("")) {
                // do nothing.
              }
              else {
                ++lineCount;
//--------------------------------------------------------------------------------
// Display the vendor totals for all items.
//--------------------------------------------------------------------------------
                chunk = new Chunk("Totals for " + vendorName, FontFactory.getFont(fontType, fontSize , Font.BOLD));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setColspan(5);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Vendor summation of the quantity on hand items.
//--------------------------------------------------------------------------------
                chunk = new Chunk(currencyFormatter.format(vendorStockValue), FontFactory.getFont(fontType, fontSize , Font.BOLD));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Check if the page is full and if so advance to the next page.
//--------------------------------------------------------------------------------
                if (lineCount >= 37) {
cat.debug(this.getClass().getName() + ": (2) lineCount : " + lineCount);
                  ++pageNumber;
                  document.add(datatable);
                  document.add(Chunk.NEXTPAGE);
                  lineCount = 4;
                  doPageHeader(request, document, pageNumber);
                  doItemHeader(request, document);
                  datatable = new PdfPTable(10);
                  datatable.setWidths(headerwidths);
                  datatable.setWidthPercentage(100); // percentage
                  datatable.getDefaultCell().setPadding(1);
                  datatable.getDefaultCell().setBorderWidth(0);
                  datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
                }
//--------------------------------------------------------------------------------
// Skip three columns.
//--------------------------------------------------------------------------------
                chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setColspan(3);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Vendor summation of the recommended purchase cost for items if greater than zero.
//--------------------------------------------------------------------------------
                if (vendorRecommendedCost.intValue() > 0)
                  chunk = new Chunk(currencyFormatter.format(vendorRecommendedCost), FontFactory.getFont(fontType, fontSize , Font.BOLD));
                else
                  chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.BOLD));
                cell = new PdfPCell(new Phrase(chunk));
                cell.setBorder(0);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                datatable.addCell(cell);
              }
//--------------------------------------------------------------------------------
// Initialize the values when a new vendor is encountered.
//--------------------------------------------------------------------------------
              vendorCount = 1;
              vendorName = formDO.getVendorName();
              vendorStockValue = new BigDecimal("0");
              vendorRecommendedCost = new BigDecimal("0");

              lineCount = lineCount + 2;
//--------------------------------------------------------------------------------
// Leave a blank line before the next vendor name.
//--------------------------------------------------------------------------------
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setColspan(10);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
              datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the new vendor name for the batch of items.
//--------------------------------------------------------------------------------
              chunk = new Chunk(formDO.getVendorName(), FontFactory.getFont(fontType, fontSize , Font.BOLD));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setColspan(10);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
              datatable.addCell(cell);
            }

//--------------------------------------------------------------------------------
// Summation of the vendor totals.
//--------------------------------------------------------------------------------
            vendorStockValue = vendorStockValue.add(stockValue);
            vendorRecommendedCost = vendorRecommendedCost.add(recommendedCost);
            totalStockValue = totalStockValue.add(stockValue);
            totalRecommendedCost = totalRecommendedCost.add(recommendedCost);

//--------------------------------------------------------------------------------
// Shade every other row in the report for easier reading.
//--------------------------------------------------------------------------------
            ++lineCount;
            ++vendorCount;
            if (vendorCount % 2 == 0)
              fShade = new Float("0.90f");
            else
              fShade = new Float("0.80f");
            datatable.getDefaultCell().setGrayFill(0.0f);

//--------------------------------------------------------------------------------
// Brand name of the item.
//--------------------------------------------------------------------------------
            chunk = new Chunk(formDO.getBrandName(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Item generic name.
//--------------------------------------------------------------------------------
            chunk = new Chunk(formDO.getGenericName(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Item size and size unit.
//--------------------------------------------------------------------------------
            chunk = new Chunk(currencyFormatter.format(formDO.getSize()) + formDO.getSizeUnit(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Item manufacturer.
//--------------------------------------------------------------------------------
            chunk = new Chunk(formDO.getManufacturer(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Current quantity on hand for the item.
//--------------------------------------------------------------------------------
            if (formDO.getQtyOnHand().compareTo(new BigDecimal("0")) > 0)
              chunk = new Chunk(currencyFormatter.format(formDO.getQtyOnHand()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            else
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Value of the quantity on hand for the item.
//--------------------------------------------------------------------------------
            if (formDO.getQtyOnHand().compareTo(new BigDecimal("0")) > 0)
              chunk = new Chunk(currencyFormatter.format(stockValue), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            else
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Ordered quantity awaiting delivery from vendor.
//--------------------------------------------------------------------------------
            orderedQty = formDO.getOrderedQty().intValue();
            if (orderedQty > 0)
              chunk = new Chunk(orderedQty.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            else
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Forecast quantity for upcoming appointments.
//--------------------------------------------------------------------------------
            if (formDO.getForecastQty().compareTo(new BigDecimal("0")) > 0)
              chunk = new Chunk(currencyFormatter.format(formDO.getForecastQty()), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            else
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Recommended quantity to order to fulfill forecast.
//--------------------------------------------------------------------------------
            if (recommendedQty > 0)
              chunk = new Chunk(recommendedQty.toString(), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            else
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Estimated cost of the recommended order quantity.
//--------------------------------------------------------------------------------
            if (recommendedQty > 0)
              chunk = new Chunk(currencyFormatter.format(recommendedCost), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            else
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setGrayFill(fShade);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            datatable.addCell(cell);
          }
        }

        if (!(vendorName.equalsIgnoreCase(""))) {
          ++lineCount;
//--------------------------------------------------------------------------------
// Display the vendor totals for all items.
//--------------------------------------------------------------------------------
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.vendorTotal") + " " + vendorName, FontFactory.getFont(fontType, fontSize , Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(5);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Vendor summation of the quantity on hand items.
//--------------------------------------------------------------------------------
          chunk = new Chunk(currencyFormatter.format(vendorStockValue), FontFactory.getFont(fontType, fontSize , Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
//- ------------------------------------------------------------------------------
// Skip three columns.
//--------------------------------------------------------------------------------
          chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(3);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Vendor summation of the recommended purchase cost for items if greater than zero.
//--------------------------------------------------------------------------------
          if (vendorRecommendedCost.intValue() > 0)
            chunk = new Chunk(currencyFormatter.format(vendorRecommendedCost), FontFactory.getFont(fontType, fontSize , Font.BOLD));
          else
            chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
        }
//--------------------------------------------------------------------------------
// Leave a blank line between the vendor total and the grand total.
//--------------------------------------------------------------------------------
              chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
              cell = new PdfPCell(new Phrase(chunk));
              cell.setBorder(0);
              cell.setColspan(10);
              cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
              datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the vendor totals for all items.
//--------------------------------------------------------------------------------
          chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.grandTotal"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(5);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Summation of the quantity on hand items.
//--------------------------------------------------------------------------------
          chunk = new Chunk(currencyFormatter.format(totalStockValue), FontFactory.getFont(fontType, fontSize , Font.BOLD));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
          datatable.addCell(cell);
//- ------------------------------------------------------------------------------
// Skip three columns.
//--------------------------------------------------------------------------------
          chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.NORMAL));
          cell = new PdfPCell(new Phrase(chunk));
          cell.setBorder(0);
          cell.setColspan(3);
          cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
          datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Summation of the recommended purchase cost for items if greater than zero.
//--------------------------------------------------------------------------------
        if (totalRecommendedCost.intValue() > 0)
          chunk = new Chunk(currencyFormatter.format(totalRecommendedCost), FontFactory.getFont(fontType, fontSize , Font.BOLD));
        else
          chunk = new Chunk(" ", FontFactory.getFont(fontType, fontSize , Font.BOLD));
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
                            Integer pageNumber)
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
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.title"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      datatable.addCell(new Phrase(chunk));
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.page"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
      Phrase phrase = new Phrase(chunk);
      chunk = new Chunk(" " + pageNumber.toString(), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      datatable.addCell(phrase);
//--------------------------------------------------------------------------------
// The second line will skip the first three cells and list the report date only.
//--------------------------------------------------------------------------------
      datatable.addCell("");
      datatable.addCell("");
      datatable.addCell("");
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.date"), FontFactory.getFont(fontType, fontSize, Font.BOLD));
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
      PdfPTable datatable = new PdfPTable(10);
      int headerwidths[] = { 32, 30, 10, 13, 6, 9, 6, 6, 6, 8 }; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(100); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

//--------------------------------------------------------------------------------
// The lineitem.title is displayed once in the doCustomerDetail.
//--------------------------------------------------------------------------------
      PdfPCell cell = null;
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
      datatable.addCell("");

      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head1"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head2"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head3"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head4"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      datatable.addCell(new Phrase(chunk));
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head5"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head6"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head7"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head8"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head9"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"inventory.report.lineitem.line1.head10"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
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
                            Document document)
   throws Exception, IOException, SQLException {

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
