/*
 * ItemImportForm.java
 *
 * Created on August 29, 2005, 09:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools;

/**
 *
 * @author Richard
 */

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.wolmerica.employee.EmployeesActionMapping;

public class ItemImportForm extends ActionForm {
     
   private Integer key;
   private String brandName;
   private String genericName;
   private String itemNum;
   private String manufacturer;
   private boolean reportId;
   private String itemName;
   private BigDecimal size;
   private String sizeUnit;
   private BigDecimal dose;
   private String doseUnit;
   private String other;
   private String itemMemo;
   private BigDecimal carryFactor;
   private BigDecimal percentUse;
   private BigDecimal firstCost;
   private BigDecimal prevFirstCost;
   private BigDecimal unitCost;
   private BigDecimal prevUnitCost;
   private BigDecimal muAdditional;
   private BigDecimal muVendor;
   private BigDecimal labelCost;
   private BigDecimal discount;
   private BigDecimal qtyOnHand;
   private BigDecimal orderThreshold;
   private BigDecimal muBeef;
   private BigDecimal muFarm;
   private BigDecimal muDairy;
   private BigDecimal muSmallAnimal;
   private BigDecimal labelBeef;
   private BigDecimal labelDairy;
   private BigDecimal addFarm;
   private BigDecimal addSmallAnimal;
   private BigDecimal perBeef;
   private BigDecimal perFarm;
   private BigDecimal perDairy;
   private BigDecimal perSmallAnimal;
   private BigDecimal poBeef;
   private BigDecimal poFarm;
   private BigDecimal poDairy;
   private BigDecimal poSmallAnimal;
   private BigDecimal overPoBeef;
   private BigDecimal overPoFarm;
   private BigDecimal overPoDairy;
   private BigDecimal overPoSmallAnimal;
   private BigDecimal resale1;
   private BigDecimal disBeef1;
   private BigDecimal disFarm1;
   private BigDecimal disDairy1;
   private BigDecimal disSmallAnimal1;
   private BigDecimal overBeef1;
   private BigDecimal overFarm1;
   private BigDecimal overDairy1;
   private BigDecimal overSmallAnimal1;
   private BigDecimal resale2;
   private BigDecimal disBeef2;
   private BigDecimal disFarm2;
   private BigDecimal disDairy2;
   private BigDecimal disSmallAnimal2;
   private BigDecimal overBeef2;
   private BigDecimal overFarm2;
   private BigDecimal overDairy2;
   private BigDecimal overSmallAnimal2;
   private BigDecimal resale3;
   private BigDecimal disBeef3;
   private BigDecimal disFarm3;
   private BigDecimal disDairy3;
   private BigDecimal disSmallAnimal3;
   private BigDecimal overBeef3;
   private BigDecimal overFarm3;
   private BigDecimal overDairy3;
   private BigDecimal overSmallAnimal3;
   private String createUser;
   private Timestamp createStamp;
   private String updateUser;
   private Timestamp updateStamp;
   
  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }
  
  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public String getGenericName() {
    return genericName;
  }

  public void setItemNum(String itemNum) {
    this.itemNum = itemNum;
  }

  public String getItemNum() {
    return itemNum;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setReportId(boolean reportId) {
    this.reportId = reportId;
  }

  public boolean getReportId() {
    return reportId;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getItemName() {
    return itemName;
  }

  public void setSize(BigDecimal size) {
    this.size = size;
  }

  public BigDecimal getSize() {
    return size;
  }

  public void setSizeUnit(String sizeUnit) {
    this.sizeUnit = sizeUnit;
  }

  public String getSizeUnit() {
    return sizeUnit;
  }

  public void setDose(BigDecimal dose) {
    this.dose = dose;
  }

  public BigDecimal getDose() {
    return dose;
  }

  public void setDoseUnit(String doseUnit) {
    this.doseUnit = doseUnit;
  }

  public String getDoseUnit() {
    return doseUnit;
  }

  public void setQtyOnHand(BigDecimal qtyOnHand) {
    this.qtyOnHand = qtyOnHand;
  }

  public BigDecimal getQtyOnHand() {
    return qtyOnHand;
  }

  public void setOrderThreshold(BigDecimal orderThreshold) {
    this.orderThreshold = orderThreshold;
  }

  public BigDecimal getOrderThreshold() {
    return orderThreshold;
  }  
  
  public void setOther(String other) {
    this.other = other;
  }

  public String getOther() {
    return other;
  }


  public void setItemMemo(String itemMemo) {
    this.itemMemo = itemMemo;
  }

  public String getItemMemo() {
    return itemMemo;
  }

  public void setCarryFactor(BigDecimal carryFactor) {
    this.carryFactor = carryFactor;
  }

  public BigDecimal getCarryFactor(){
    return carryFactor;
  }

  public void setPercentUse(BigDecimal percentUse) {
    this.percentUse = percentUse;
  }

  public BigDecimal getPercentUse(){
    return percentUse;
  }

  public void setFirstCost(BigDecimal firstCost) {
    this.firstCost = firstCost;
  }

  public BigDecimal getFirstCost() {
    return firstCost;
  }

  public void setPrevFirstCost(BigDecimal prevFirstCost) {
    this.prevFirstCost = prevFirstCost;
  }

  public BigDecimal getPrevFirstCost() {
    return prevFirstCost;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public void setPrevUnitCost(BigDecimal prevUnitCost) {
    this.prevUnitCost = prevUnitCost;
  }

  public BigDecimal getPrevUnitCost() {
    return prevUnitCost;
  }

  public void setMuAdditional(BigDecimal muAdditional) {
    this.muAdditional = muAdditional;
  }

  public BigDecimal getMuAdditional() {
    return muAdditional;
  }

  public void setMuBeef(BigDecimal muBeef) {
    this.muBeef = muBeef;
  }

  public BigDecimal getMuBeef() {
    return muBeef;
  }

  public void setMuFarm(BigDecimal muFarm) {
    this.muFarm = muFarm;
  }

  public BigDecimal getMuFarm() {
    return muFarm;
  }

  public void setMuDairy(BigDecimal muDairy) {
    this.muDairy = muDairy;
  }

  public BigDecimal getMuDairy() {
    return muDairy;
  }

  public void setMuSmallAnimal(BigDecimal muSmallAnimal) {
    this.muSmallAnimal = muSmallAnimal;
  }

  public BigDecimal getMuSmallAnimal() {
    return muSmallAnimal;
  }

  public void setMuVendor(BigDecimal muVendor) {
    this.muVendor = muVendor;
  }

  public BigDecimal getMuVendor() {
    return muVendor;
  }

  public void setLabelCost(BigDecimal labelCost) {
    this.labelCost = labelCost;
  }

  public BigDecimal getLabelCost() {
    return labelCost;
  }

    public void setLabelBeef(BigDecimal labelBeef) {
    this.labelBeef = labelBeef;
  }

   public BigDecimal getLabelBeef() {
    return labelBeef;
  }

  public void setLabelDairy(BigDecimal labelDairy) {
    this.labelDairy = labelDairy;
  }

  public BigDecimal getLabelDairy() {
    return labelDairy;
  }

   public void setAddFarm(BigDecimal addFarm) {
    this.addFarm = addFarm;
  }

  public BigDecimal getAddFarm() {
    return addFarm;
  }

   public void setAddSmallAnimal(BigDecimal addSmallAnimal) {
    this.addSmallAnimal = addSmallAnimal;
  }

  public BigDecimal getAddSmallAnimal() {
    return addSmallAnimal;
  }

  public void setDiscount(BigDecimal discount) {
    this.discount = discount;
  }

  public BigDecimal getDiscount() {
    return discount;
  }

  public void setPerBeef(BigDecimal perBeef) {
    this.perBeef = perBeef;
  }

  public BigDecimal getPerBeef() {
    return perBeef;
  }

  public void setPerFarm(BigDecimal perFarm) {
    this.perFarm = perFarm;
  }

  public BigDecimal getPerFarm() {
    return perFarm;
  }

  public void setPerDairy(BigDecimal perDairy) {
    this.perDairy = perDairy;
  }

  public BigDecimal getPerDairy() {
    return perDairy;
  }

  public void setPerSmallAnimal(BigDecimal perSmallAnimal) {
    this.perSmallAnimal = perSmallAnimal;
  }

  public BigDecimal getPerSmallAnimal() {
    return perSmallAnimal;
  }

  public void setPoBeef(BigDecimal poBeef) {
    this.poBeef = poBeef;
  }

  public BigDecimal getPoBeef() {
    return poBeef;
  }

  public void setPoFarm(BigDecimal poFarm) {
    this.poFarm = poFarm;
  }

  public BigDecimal getPoFarm() {
    return poFarm;
  }

  public void setPoDairy(BigDecimal poDairy) {
    this.poDairy = poDairy;
  }

  public BigDecimal getPoDairy() {
    return poDairy;
  }

  public void setPoSmallAnimal(BigDecimal poSmallAnimal) {
    this.poSmallAnimal = poSmallAnimal;
  }

  public BigDecimal getPoSmallAnimal() {
    return poSmallAnimal;
  }

  public void setOverPoBeef(BigDecimal overPoBeef) {
    this.overPoBeef = overPoBeef;
  }

  public BigDecimal getOverPoBeef() {
    return overPoBeef;
  }

  public void setOverPoFarm(BigDecimal overPoFarm) {
    this.overPoFarm = overPoFarm;
  }

  public BigDecimal getOverPoFarm() {
    return overPoFarm;
  }

  public void setOverPoDairy(BigDecimal overPoDairy) {
    this.overPoDairy = overPoDairy;
  }

  public BigDecimal getOverPoDairy() {
    return overPoDairy;
  }

  public void setOverPoSmallAnimal(BigDecimal overPoSmallAnimal) {
    this.overPoSmallAnimal = overPoSmallAnimal;
  }

  public BigDecimal getOverPoSmallAnimal() {
    return overPoSmallAnimal;
  }

  public void setResale1(BigDecimal resale1) {
    this.resale1 = resale1;
  }

  public BigDecimal getResale1(){
    return resale1;
  }

  public void setDisBeef1(BigDecimal disBeef1) {
    this.disBeef1 = disBeef1;
  }

  public BigDecimal getDisBeef1(){
    return disBeef1;
  }

  public void setDisFarm1(BigDecimal disFarm1) {
    this.disFarm1 = disFarm1;
  }

  public BigDecimal getDisFarm1(){
    return disFarm1;
  }

  public void setDisDairy1(BigDecimal disDairy1) {
    this.disDairy1 = disDairy1;
  }

  public BigDecimal getDisDairy1(){
    return disDairy1;
  }

  public void setDisSmallAnimal1(BigDecimal disSmallAnimal1) {
    this.disSmallAnimal1 = disSmallAnimal1;
  }

  public BigDecimal getDisSmallAnimal1(){
    return disSmallAnimal1;
  }

  public void setOverBeef1(BigDecimal overBeef1) {
    this.overBeef1 = overBeef1;
  }

  public BigDecimal getOverBeef1(){
    return overBeef1;
  }

  public void setOverFarm1(BigDecimal overFarm1) {
    this.overFarm1 = overFarm1;
  }

  public BigDecimal getOverFarm1(){
    return overFarm1;
  }

  public void setOverDairy1(BigDecimal overDairy1) {
    this.overDairy1 = overDairy1;
  }

  public BigDecimal getOverDairy1(){
    return overDairy1;
  }

  public void setOverSmallAnimal1(BigDecimal overSmallAnimal1) {
    this.overSmallAnimal1 = overSmallAnimal1;
  }

  public BigDecimal getOverSmallAnimal1(){
    return overSmallAnimal1;
  }

  public void setResale2(BigDecimal resale2) {
    this.resale2 = resale2;
  }

  public BigDecimal getResale2(){
    return resale2;
  }

  public void setDisBeef2(BigDecimal disBeef2) {
    this.disBeef2 = disBeef2;
  }

  public BigDecimal getDisBeef2(){
    return disBeef2;
  }

  public void setDisFarm2(BigDecimal disFarm2) {
    this.disFarm2 = disFarm2;
  }

  public BigDecimal getDisFarm2(){
    return disFarm2;
  }

  public void setDisDairy2(BigDecimal disDairy2) {
    this.disDairy2 = disDairy2;
  }

  public BigDecimal getDisDairy2(){
    return disDairy2;
  }

  public void setDisSmallAnimal2(BigDecimal disSmallAnimal2) {
    this.disSmallAnimal2 = disSmallAnimal2;
  }

  public BigDecimal getDisSmallAnimal2(){
    return disSmallAnimal2;
  }

  public void setOverBeef2(BigDecimal overBeef2) {
    this.overBeef2 = overBeef2;
  }

  public BigDecimal getOverBeef2(){
    return overBeef2;
  }

  public void setOverFarm2(BigDecimal overFarm2) {
    this.overFarm2 = overFarm2;
  }

  public BigDecimal getOverFarm2(){
    return overFarm2;
  }

  public void setOverDairy2(BigDecimal overDairy2) {
    this.overDairy2 = overDairy2;
  }

  public BigDecimal getOverDairy2(){
    return overDairy2;
  }

  public void setOverSmallAnimal2(BigDecimal overSmallAnimal2) {
    this.overSmallAnimal2 = overSmallAnimal2;
  }

  public BigDecimal getOverSmallAnimal2(){
    return overSmallAnimal2;
  }

  public void setResale3(BigDecimal resale3) {
    this.resale3 = resale3;
  }

  public BigDecimal getResale3(){
    return resale3;
  }

  public void setDisBeef3(BigDecimal disBeef3) {
    this.disBeef3 = disBeef3;
  }

  public BigDecimal getDisBeef3(){
    return disBeef3;
  }

  public void setDisFarm3(BigDecimal disFarm3) {
    this.disFarm3 = disFarm3;
  }

  public BigDecimal getDisFarm3(){
    return disFarm3;
  }

  public void setDisDairy3(BigDecimal disDairy3) {
    this.disDairy3 = disDairy3;
  }

  public BigDecimal getDisDairy3(){
    return disDairy3;
  }

  public void setDisSmallAnimal3(BigDecimal disSmallAnimal3) {
    this.disSmallAnimal3 = disSmallAnimal3;
  }

  public BigDecimal getDisSmallAnimal3(){
    return disSmallAnimal3;
  }

  public void setOverBeef3(BigDecimal overBeef3) {
    this.overBeef3 = overBeef3;
  }

  public BigDecimal getOverBeef3(){
    return overBeef3;
  }

  public void setOverFarm3(BigDecimal overFarm3) {
    this.overFarm3 = overFarm3;
  }

  public BigDecimal getOverFarm3(){
    return overFarm3;
  }

  public void setOverDairy3(BigDecimal overDairy3) {
    this.overDairy3 = overDairy3;
  }

  public BigDecimal getOverDairy3(){
    return overDairy3;
  }

  public void setOverSmallAnimal3(BigDecimal overSmallAnimal3) {
    this.overSmallAnimal3 = overSmallAnimal3;
  }

  public BigDecimal getOverSmallAnimal3(){
    return overSmallAnimal3;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getCreateUser() {
    return createUser;
  }  
  
  public void setCreateStamp(Timestamp createStamp) {
    this.createStamp = createStamp;    
  }
  
  public Timestamp getCreateStamp() {
    return createStamp;
  }
  
  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }  
  
  public void setUpdateStamp(Timestamp updateStamp) {
    this.updateStamp = updateStamp;    
  }
  
  public Timestamp getUpdateStamp() {
       
    return updateStamp;
  } 

  // This method is called with every request. It resets the Form
  // attribute prior to setting the values in the new request.
    @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {

    this.key = null;
    this.brandName = "";
    this.genericName = "";
    this.itemNum = "";
    this.manufacturer = "";
    this.reportId = false;
    this.itemName = "";
    this.size = new BigDecimal("0.0");
    this.sizeUnit = "";
    this.dose = new BigDecimal("0.0");
    this.doseUnit = "";
    this.qtyOnHand = new BigDecimal("0");
    this.orderThreshold = new BigDecimal("0");
    this.other = "";
    this.itemMemo = "";
    this.carryFactor = new BigDecimal("1.00");
    this.percentUse = new BigDecimal("1.00");
    this.firstCost = new BigDecimal("0.00");
    this.prevFirstCost = new BigDecimal("0.00");
    this.unitCost = new BigDecimal("0.00");
    this.prevUnitCost = new BigDecimal("0.00");
    this.muAdditional = new BigDecimal("1.00");
    this.muBeef = new BigDecimal("1.00");
    this.muFarm = new BigDecimal("1.00");
    this.muDairy = new BigDecimal("1.00");
    this.muSmallAnimal = new BigDecimal("1.00");
    this.muVendor = new BigDecimal("1.00");
    this.labelCost = new BigDecimal("0.00");
    this.labelBeef = new BigDecimal("0.00");
    this.labelDairy = new BigDecimal("0.00");
    this.addFarm = new BigDecimal("1.00");
    this.addSmallAnimal = new BigDecimal("1.00");
    this.discount = new BigDecimal("0.00");
    this.perBeef = new BigDecimal("1.00");
    this.perFarm = new BigDecimal("1.00");
    this.perDairy = new BigDecimal("1.00");
    this.perSmallAnimal = new BigDecimal("1.00");
    this.poBeef = new BigDecimal("0.00");
    this.poFarm = new BigDecimal("0.00");
    this.poDairy = new BigDecimal("0.00");
    this.poSmallAnimal = new BigDecimal("0.00");
    this.overPoBeef = new BigDecimal("0.00");
    this.overPoFarm = new BigDecimal("0.00");
    this.overPoDairy = new BigDecimal("0.00");
    this.overPoSmallAnimal = new BigDecimal("0.00");
    this.resale1 = new BigDecimal("0.0");
    this.disBeef1 = new BigDecimal("0.00");
    this.disFarm1 = new BigDecimal("0.00");
    this.disDairy1 = new BigDecimal("0.00");
    this.disSmallAnimal1 = new BigDecimal("0.00");
    this.overBeef1 = new BigDecimal("0.00");
    this.overFarm1 = new BigDecimal("0.00");
    this.overDairy1 = new BigDecimal("0.00");
    this.overSmallAnimal1 = new BigDecimal("0.00");
    this.resale2 = new BigDecimal("0.0");
    this.disBeef2 = new BigDecimal("0.00");
    this.disFarm2 = new BigDecimal("0.00");
    this.disDairy2 = new BigDecimal("0.00");
    this.disSmallAnimal2 = new BigDecimal("0.00");
    this.overBeef2 = new BigDecimal("0.00");
    this.overFarm2 = new BigDecimal("0.00");
    this.overDairy2 = new BigDecimal("0.00");
    this.overSmallAnimal2 = new BigDecimal("0.00");
    this.resale3 = new BigDecimal("0.0");
    this.disBeef3 = new BigDecimal("0.00");
    this.disFarm3 = new BigDecimal("0.00");
    this.disDairy3 = new BigDecimal("0.00");
    this.disSmallAnimal3 = new BigDecimal("0.00");
    this.overBeef3 = new BigDecimal("0.00");
    this.overFarm3 = new BigDecimal("0.00");
    this.overDairy3 = new BigDecimal("0.00");
    this.overSmallAnimal3 = new BigDecimal("0.00");
    this.createUser = (String) request.getSession().getAttribute("USERNAME");
    this.createStamp = new Timestamp(new Date().getTime());
    this.updateUser = this.createUser;
    this.updateStamp = this.createStamp;
    }

    @Override
  public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();

    EmployeesActionMapping employeesMapping = (EmployeesActionMapping)mapping;

    // Does this action require the user to login
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {

        // return null to force action to handle login
        // error
        return null;
      }
    }

    if ( (this.brandName == null ) || (this.brandName.length() == 0) ) {

      errors.add("brandName", new ActionMessage("errors.itemdictionary.brandName.required"));
    }
    if ( (this.genericName == null ) || (this.genericName.length() == 0) ) {

      errors.add("genericName", new ActionMessage("errors.itemdictionary.genericName.required"));
    }
    if ( (this.itemNum == null ) || (this.itemNum.length() == 0) ) {

      errors.add("itemNum", new ActionMessage("errors.itemdictionary.itemNum.required"));
    }
    if ( (this.manufacturer == null ) || (this.manufacturer.length() == 0) ) {

      errors.add("manufacturer", new ActionMessage("errors.itemdictionary.manufacturer.required"));
    }
    if ( (this.itemName == null ) || (this.itemName.length() == 0) ) {

      errors.add("itemName", new ActionMessage("errors.itemdictionary.itemName.required"));
    }
    if  (this.size == null ) {

      errors.add("size", new ActionMessage("errors.itemdictionary.size.required"));
    }
    if ( (this.sizeUnit == null ) || (this.sizeUnit.length() == 0) ) {

      errors.add("sizeUnit", new ActionMessage("errors.itemdictionary.sizeUnit.required"));
    }
    if (this.carryFactor == null ) {

      errors.add("carryFactor", new ActionMessage("errors.itemdictionary.carryFactor.required"));
    }
    if (this.percentUse == null ) {

      errors.add("percentUse", new ActionMessage("errors.itemdictionary.percentUse.required"));
    }
    if (this.firstCost == null ) {

      errors.add("firstCost", new ActionMessage("errors.itemdictionary.firstCost.required"));
    }
    if (this.unitCost == null ) {

      errors.add("unitCost", new ActionMessage("errors.itemdictionary.unitCost.required"));
    }

    return errors;
  }
}

