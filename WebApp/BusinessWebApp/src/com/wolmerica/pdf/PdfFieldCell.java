/*
 * PdfFieldCell.java
 *
 * Created on August 10, 2007, 6:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.pdf;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfContentByte;

public class PdfFieldCell implements PdfPCellEvent {

  PdfFormField formField;
  PdfWriter writer;
  int width;

  public PdfFieldCell(PdfFormField formField, int width, PdfWriter writer){
    this.formField = formField;
    this.width = width;
    this.writer = writer;
  }

  public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvas){
    try{
    // delete cell border
      PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
      cb.reset();

      formField.setWidget(new Rectangle(rect.LEFT,
                          rect.BOTTOM,
                          rect.LEFT+width,
                          rect.TOP),
                          PdfAnnotation.HIGHLIGHT_NONE);

      writer.addAnnotation(formField);

    }
    catch(Exception e){
      System.out.println(e);
    }
  }
}
