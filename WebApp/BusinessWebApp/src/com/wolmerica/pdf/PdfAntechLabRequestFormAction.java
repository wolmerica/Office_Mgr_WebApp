/*
 * PdfAntechLabRequestFormAction.java
 *
 * Created on February 6, 2010, 8:12 AM
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
import com.zoasis.service.zoasislab.ZoasisLabService;
import com.zoasis.service.zoasislab.DefaultZoasisLabService;

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
import java.net.URL;
import java.util.HashMap;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;

import org.apache.log4j.Logger;


public class PdfAntechLabRequestFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  int fontSize = 10;
  String fontType = FontFactory.TIMES_ROMAN;

  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private UserStateService userStateService = new DefaultUserStateService();
  private ZoasisLabService zoasisLabService = new DefaultZoasisLabService();

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

  public ZoasisLabService getZoasisLabService() {
      return zoasisLabService;
  }

  public void setZoasisLabService(ZoasisLabService zoasisLabService) {
      this.zoasisLabService = zoasisLabService;
  }


  public void doReportGenerator(HttpServletRequest request,
                                HttpServletResponse response,
                                Integer poKey,
                                String presentationType)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
    String poNum = "";
    Integer vKey = null;
    Integer cKey = null;
    Byte attributeToEntityKey = null;
    Integer attributeToKey = null;
//--------------------------------------------------------------------------------
// step 1 declare variables.
//--------------------------------------------------------------------------------
    Document document = new Document();
    PdfWriter writer = null;
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
//--------------------------------------------------------------------------------
// Use the PurchaseOrderKit methods to get the purchase order keys.
//--------------------------------------------------------------------------------
      HashMap pokMap = getPurchaseOrderService().getPurchaseOrderKeys(conn, poKey);
      poNum = pokMap.get("purchaseOrderNum").toString();
      vKey = new Integer(pokMap.get("vendorKey").toString());
      cKey = new Integer(pokMap.get("customerKey").toString());
      attributeToEntityKey = new Byte(pokMap.get("attributeToEntityKey").toString());
      attributeToKey = new Integer(pokMap.get("attributeToKey").toString());

      try {
//--------------------------------------------------------------------------------
// step 2: set the ContentType to pdf, htlm, or rtf.
//--------------------------------------------------------------------------------
        if ("pdf".equals(presentationType)) {
          response.setContentType("application/pdf");
          writer = PdfWriter.getInstance(document, response.getOutputStream());
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
          cat.debug("doPageHeader");
          doPageHeader(request,
                       conn,
                       document,
                       writer,
                       currentPage,
                       lastPage,
                       poNum,
                       poKey,
                       attributeToEntityKey,
                       attributeToKey);
          cat.debug("doPurchaseOrderHeader");
          doPurchaseOrderHeader(request,
                                document);
          cat.debug("doPurchaseOrderDetail()");
          doPurchaseOrderDetail(request, 
                                conn,
                                document,
                                poKey, 
                                startRow,
                                endRow);
          startRow = endRow + 1;
          endRow = (endRow + nextPageCount) - 1;
        }
        cat.debug("doPageFooter()");
        doPageFooter(request,
                     conn,
                     document,
                     poKey,
                     attributeToEntityKey,
                     attributeToKey);
      }
      catch(DocumentException de) {
        de.getMessage();
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

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String query = "(SELECT poi.thekey,"
                   + "id.item_num,"
                   + "id.brand_name,"
                   + "id.size_unit,"
                   + "poi.order_qty,"
                   + "poi.note_line1 "
                   + "FROM purchaseorderitem poi, itemdictionary id "
                   + "WHERE poi.purchaseorder_key = ? "
                   + "AND poi.itemdictionary_key = id.thekey) "
                   + " UNION "
                   + "(SELECT pos.thekey,"
                   + "sd.service_num AS item_num,"
                   + "sd.name AS brand_name,"
                   + "pt.name AS size_unit,"
                   + "pos.order_qty,"
                   + "pos.note_line1 "
                   + "FROM purchaseorderservice pos, servicedictionary sd, pricetype pt "
                   + "WHERE pos.purchaseorder_key = ? "
                   + "AND pos.servicedictionary_key = sd.thekey "
                   + "AND pos.pricetype_key = pt.thekey)";
      cat.debug(this.getClass().getName() + ": Query = " + query);
      ps = conn.prepareStatement(query);
      cat.debug("poKey: " + poKey);
      ps.setInt(1, poKey);
      ps.setInt(2, poKey);
      rs = ps.executeQuery();

      try {
        PdfPTable datatable = new PdfPTable(5);
        int headerwidths[] = {3,17,20,18,32};
        datatable.setWidths(headerwidths);
        datatable.setWidthPercentage(90);
        datatable.getDefaultCell().setPadding(2);
        datatable.getDefaultCell().setBorderWidth(0);
        datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        int recordCount = 0;
        Chunk chunk = null;
        PdfPCell cell = null;
        String description = " ";
        while ( rs.next() && ++recordCount <=lastRecordCount ) {
          if (recordCount >= firstRecordCount)
          {
            cat.debug("item num: " + rs.getString("item_num"));
            chunk = new Chunk(rs.getString("item_num"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            datatable.addCell("");
	    datatable.addCell(cell);

            chunk = new Chunk(rs.getString("brand_name"), FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
            cell.setColspan(2);
	    datatable.addCell(cell);

            description = rs.getString("note_line1");
            if (description == null) {
              description = " ";
            }
            chunk = new Chunk(description, FontFactory.getFont(fontType, fontSize , Font.NORMAL));
            cell = new PdfPCell(new Phrase(chunk));
            cell.setBorder(0);
	    datatable.addCell(cell);
          }
        }
        document.add(datatable);
        cat.debug(this.getClass().getName() + ": Finished details");
      }
      catch(DocumentException de) {
        de.getMessage();
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

  private Image getBarCodePDF417(PdfWriter writer,
                                 String labRequest)
   throws Exception, IOException {

    PdfContentByte cb = writer.getDirectContent();
    BarcodePDF417 codePDF417 = null;
    Image imagePDF417 = null;

    try {
//--------------------------------------------------------------------------------
// Generate the 2D bar code following the PDF417 code type.
//--------------------------------------------------------------------------------
      codePDF417 = new BarcodePDF417();
      codePDF417.setText(labRequest);
      cat.debug("Start codePDF417.getImage()");
      imagePDF417 = codePDF417.getImage();
      imagePDF417.scalePercent(70, 70 * codePDF417.getYHeight());
      cat.debug("Done codePDF417.getImage()");
    }
    catch(Exception e) {
      System.out.println("e:"+e);
      e.getMessage();
    }
    return imagePDF417;
  }

  private Image getBarCode128(PdfWriter writer,
                              String labRequisitionId)
   throws Exception, IOException {

    PdfContentByte cb = writer.getDirectContent();
    Barcode128 code128 = null;
    Image image128 = null;

    try {
//--------------------------------------------------------------------------------
// Generate the bar code following the CODE128 code type.
//--------------------------------------------------------------------------------
      code128 = new Barcode128();
      code128.setCode(labRequisitionId);
//      code128.setCodeType(Barcode128.CODE128);
//      code128.setCode(barCodeNumber + "\uffffReq No: " + barCodeNumber);
      code128.setBarHeight(40f);
      cat.debug("Start createImageWithBarcode()");
      image128 = code128.createImageWithBarcode(cb, null, null);
      cat.debug("Done createImageWithBarcode()");
    }
    catch(Exception e) {
      System.out.println("e:"+e);
      e.getMessage();
    }
    return image128;
  }

  private void doPageHeader(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            PdfWriter writer,
                            int currentPage,
                            int lastPage,
                            String poNumber,
                            Integer poKey,
                            Byte attributeToEntityKey,
                            Integer attributeToKey)
   throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(6);
      int headerwidths[] = {3,17,20,18,12,20}; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(90); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

//--------------------------------------------------------------------------------
// Insert the company logo at the top of the invoice.
// as though it resides in the webapp folder, making the relative
// reference to the images subdirectory work.
//--------------------------------------------------------------------------------
      String p_strCurrentURL = new String(request.getRequestURL());
      Image imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/Wolmerica_Logo.gif"));
      imageObj.scalePercent(70,70);
      PdfPCell cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// A thick full line break in the page.
//--------------------------------------------------------------------------------
      imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/ThickFullLineBreak.gif"));
      imageObj.scalePercent(65,50);
      cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the lab name.
//--------------------------------------------------------------------------------
      Chunk chunk = new Chunk("Antech Diagnostics", FontFactory.getFont(fontType, fontSize+5, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      datatable.addCell(cell);

//--------------------------------------------------------------------------------
// Generate either a PDF417 or a CODE128 bar code for the lab request
//--------------------------------------------------------------------------------
      String labRequisitionId = getZoasisLabService().getAntechRequisitionId(request,
                                                           conn,
                                                           poKey,
                                                           attributeToEntityKey,
                                                           attributeToKey);
      if (getPropertyService().getCustomerProperties(request,"po.lab.request.barcode.type").equalsIgnoreCase("PDF417")) {
        String labRequest = getZoasisLabService().getAntech2dTextString(request,
                                                      conn,
                                                      labRequisitionId,
                                                      poKey,
                                                      attributeToEntityKey,
                                                      attributeToKey);
        cat.debug("2D Lab Request String..: " + labRequest);
        imageObj = getBarCodePDF417(writer, labRequest);
      } else {
        if (getPropertyService().getCustomerProperties(request,"po.lab.request.barcode.type").equalsIgnoreCase("CODE128")) {
          cat.debug("poKey............: " + poKey);
          cat.debug("labRequisitionId.: " + labRequisitionId);
          imageObj = getBarCode128(writer,
                                   labRequisitionId);
        }
      }
      chunk = new Chunk(imageObj, 0, 0);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
      // Display the requisition id under the PDF417 barcode.
      if (getPropertyService().getCustomerProperties(request,"po.lab.request.barcode.type").equalsIgnoreCase("PDF417")) {
        chunk = new Chunk("Req No: " + labRequisitionId, FontFactory.getFont(fontType, fontSize+2, Font.BOLD));
        cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(0);
        cell.setColspan(6);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        datatable.addCell(cell);
      }
//--------------------------------------------------------------------------------
// A thick full line break in the page.
//--------------------------------------------------------------------------------
      imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/ThickFullLineBreak.gif"));
      imageObj.scalePercent(65,50);
      cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
      // clinicAcctNum
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.clinic.accountid"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      Phrase phrase = new Phrase(chunk);
      chunk = new Chunk("  " + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicAcctNum"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      cell = new PdfPCell(phrase);
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell(cell);
      // dateSampleTaken
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "dateSampleTaken"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(1);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      datatable.addCell(cell);
      // clinicName
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicName"), FontFactory.getFont(fontType, fontSize+3, Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(6);
      datatable.addCell(cell);
      // clinicAddress
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicAddress"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell("");
      datatable.addCell(cell);
      // clinicCity
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicCity"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase = new Phrase(chunk);
      // clinicState
      chunk = new Chunk(", " + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicState"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      // clinicZip
      chunk = new Chunk(" " + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicZip"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      cell = new PdfPCell(phrase);
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell("");
      datatable.addCell(cell);
      // Phone label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.clinic.phone"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      phrase = new Phrase(chunk);
      // clinicAreaCode
      chunk = new Chunk(" " + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicAreaCode"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      // clinicPhoneNum
      chunk = new Chunk("-" + getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicPhoneNum"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      phrase.add(chunk);
      cell = new PdfPCell(phrase);
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell("");
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// A thin full line break in the page.
//--------------------------------------------------------------------------------
      imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/ThickFullLineBreak.gif"));
      imageObj.scalePercent(65,10);
      cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
      
      // *PMS ID label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.clinic.pmsid"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // animalMRN
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalMRN"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(4);
      datatable.addCell(cell);
      // *Clinic doctor label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.clinic.doctor"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // clinicSubmittingVeterinarian
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "clinicSubmittingVeterinarian"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // Pet species label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.pet.species"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // animalSpecies
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalSpecies"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // *Owner last name label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.owner.lastname"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // ownerLastName
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "ownerLastName"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // Pet breed label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.pet.breed"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // animalBreed
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalBreed"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // *Owner first name label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.owner.firstname"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // ownerFirstName
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "ownerFirstName"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // Pet sex label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.pet.sex"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // animalSex
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalSex"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // *Pet name label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.pet.name"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // animalName
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalName"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      // Pet age label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.pet.age"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // animalDOB
      chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "animalDOB"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// A thin full line break in the page.
//--------------------------------------------------------------------------------
      imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/ThickFullLineBreak.gif"));
      imageObj.scalePercent(65,10);
      cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// Display the Lab Use Only section.
//--------------------------------------------------------------------------------
      imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/LabUseOnly.gif"));
      imageObj.scalePercent(68,68);
      cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
//--------------------------------------------------------------------------------
// A thin full line break in the page.
//--------------------------------------------------------------------------------
      imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/ThickFullLineBreak.gif"));
      imageObj.scalePercent(65,10);
      cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
      
      document.add(datatable);
    }
    catch(DocumentException de) {
      de.getMessage();
      cat.error(this.getClass().getName() + ": document: " + de.getMessage());
    }
  }

private void doPurchaseOrderHeader(HttpServletRequest request,
                                   Document document)
   throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(5);
      int headerwidths[] = {3,17,20,18,32};
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(90);
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

      // Ordered Items label
      Chunk chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.test.ordered"), FontFactory.getFont(fontType, fontSize+2, Font.BOLD));
      PdfPCell cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell(cell);
      // item/service number label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.test.code"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell("");
      datatable.addCell(cell);
      // item/service name label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.test.name"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(2);
      datatable.addCell(cell);
      // item/service description label
      chunk = new Chunk(getPropertyService().getCustomerProperties(request,"po.lab.request.test.description"), FontFactory.getFont(fontType, fontSize , Font.BOLD));
      chunk.setUnderline(0.2f, -2f);
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      datatable.addCell(cell);
      
      document.add(datatable);
    }
    catch(DocumentException de) {
      de.getMessage();
      cat.debug(this.getClass().getName() + ": document = " + de.getMessage());
    }
  }

  private void doPageFooter(HttpServletRequest request,
                            Connection conn,
                            Document document,
                            Integer poKey,
                            Byte attributeToEntityKey,
                            Integer attributeToKey)
   throws Exception, IOException, SQLException {

    try {
      PdfPTable datatable = new PdfPTable(6);
      int headerwidths[] = {3,17,20,18,12,20}; // percentage
      datatable.setWidths(headerwidths);
      datatable.setWidthPercentage(90); // percentage
      datatable.getDefaultCell().setPadding(2);
      datatable.getDefaultCell().setBorderWidth(0);
      datatable.getDefaultCell().setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

//--------------------------------------------------------------------------------
// A thick full line break in the page.
//--------------------------------------------------------------------------------
      String p_strCurrentURL = new String(request.getRequestURL());
      Image imageObj = Image.getInstance(new URL(new URL(p_strCurrentURL),"images/ThickFullLineBreak.gif"));
      imageObj.scalePercent(65,50);
      PdfPCell cell = new PdfPCell(imageObj);
      cell.setColspan(6);
      cell.setBorder(0);
      datatable.addCell(cell);
      // labOrderComment
      Chunk chunk = new Chunk(getPurchaseOrderService().getVendorOrderFormDetail(conn, poKey, attributeToEntityKey, attributeToKey, "labOrderComment"), FontFactory.getFont(fontType, fontSize, Font.NORMAL));
      cell = new PdfPCell(new Phrase(chunk));
      cell.setBorder(0);
      cell.setColspan(5);
      datatable.addCell("");
      datatable.addCell(cell);

      document.add(datatable);
    }
    catch(DocumentException de) {
      de.getMessage();
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
// Purchase Order Key
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [key] not found!");
      }

//--------------------------------------------------------------------------------
// iText presentation type
//--------------------------------------------------------------------------------
      String theType = null;
      if (request.getParameter("presentationType") != null) {
        theType = request.getParameter("presentationType").toString();
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
