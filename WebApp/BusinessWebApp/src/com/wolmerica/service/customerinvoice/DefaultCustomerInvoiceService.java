package com.wolmerica.service.customerinvoice;

import com.wolmerica.customerinvoice.CustomerInvoiceDO;
import com.wolmerica.util.common.EnumCustomerInvoiceScenario;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    February 18, 2011
 */

public class DefaultCustomerInvoiceService implements CustomerInvoiceService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getCustomerInvoiceTotalsByCiKey(conn, customerInvoiceDO, ciKey)
//--------------------------------------------------------------------------------
  public CustomerInvoiceDO getCustomerInvoiceTotalsByCiKey(Connection conn,
                                                           CustomerInvoiceDO formDO,
                                                           Integer ciKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and seventeen OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceTotalsByCI(?,?,?,?,?,?,?,?,?,?,"
                                                                + "?,?,?,?,?,?,?,?,?,?,"
                                                                + "?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, ciKey);
      cStmt.setBigDecimal(2, formDO.getSalesTaxRate());
      cStmt.setBigDecimal(3, formDO.getServiceTaxRate());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": itemGrossAmt : " + cStmt.getBigDecimal("itemGrossAmt"));
      cat.debug(this.getClass().getName() + ": itemDiscountAmt : " + cStmt.getBigDecimal("itemDiscountAmt"));
      cat.debug(this.getClass().getName() + ": itemNetAmt : " + cStmt.getBigDecimal("itemNetAmt"));
      cat.debug(this.getClass().getName() + ": serviceGrossAmt : " + cStmt.getBigDecimal("serviceGrossAmt"));
      cat.debug(this.getClass().getName() + ": serviceDiscountAmt : " + cStmt.getBigDecimal("serviceDiscountAmt"));
      cat.debug(this.getClass().getName() + ": serviceNetAmt : " + cStmt.getBigDecimal("serviceNetAmt"));
      cat.debug(this.getClass().getName() + ": subTotalAmt : " + cStmt.getBigDecimal("subTotalAmt"));
      cat.debug(this.getClass().getName() + ": grossProfitAmt : " + cStmt.getBigDecimal("grossProfitAmt"));
      cat.debug(this.getClass().getName() + ": netProfitAmt : " + cStmt.getBigDecimal("netProfitAmt"));
      cat.debug(this.getClass().getName() + ": taxableAmt : " + cStmt.getBigDecimal("taxableAmt"));
      cat.debug(this.getClass().getName() + ": salesTaxAmt : " + cStmt.getBigDecimal("salesTaxAmt"));
      cat.debug(this.getClass().getName() + ": serviceTaxableAmt : " + cStmt.getBigDecimal("serviceTaxableAmt"));
      cat.debug(this.getClass().getName() + ": serviceTaxAmt : " + cStmt.getBigDecimal("serviceTaxAmt"));
      cat.debug(this.getClass().getName() + ": debitAdjAmt : " + cStmt.getBigDecimal("debitAdjAmt"));
      cat.debug(this.getClass().getName() + ": packagingAmt : " + cStmt.getBigDecimal("packagingAmt"));
      cat.debug(this.getClass().getName() + ": freightAmt : " + cStmt.getBigDecimal("freightAmt"));
      cat.debug(this.getClass().getName() + ": miscAmt : " + cStmt.getBigDecimal("miscAmt"));
      cat.debug(this.getClass().getName() + ": creditAdjAmt : " + cStmt.getBigDecimal("creditAdjAmt"));
      cat.debug(this.getClass().getName() + ": invoiceAmt : " + cStmt.getBigDecimal("invoiceAmt"));
      cat.debug(this.getClass().getName() + ": SUM(orderQty) : " + cStmt.getBigDecimal("orderQty"));
//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setItemGrossAmount(cStmt.getBigDecimal("itemGrossAmt"));
      formDO.setItemDiscountAmount(cStmt.getBigDecimal("itemDiscountAmt"));
      formDO.setItemNetAmount(cStmt.getBigDecimal("itemNetAmt"));
      formDO.setServiceGrossAmount(cStmt.getBigDecimal("serviceGrossAmt"));
      formDO.setServiceDiscountAmount(cStmt.getBigDecimal("serviceDiscountAmt"));
      formDO.setServiceNetAmount(cStmt.getBigDecimal("serviceNetAmt"));
      formDO.setSubTotal(cStmt.getBigDecimal("subTotalAmt"));
      formDO.setGrossProfitAmount(cStmt.getBigDecimal("grossProfitAmt"));
      formDO.setNetProfitAmount(cStmt.getBigDecimal("netProfitAmt"));
      formDO.setTaxableTotal(cStmt.getBigDecimal("taxableAmt"));
      formDO.setSalesTaxCost(cStmt.getBigDecimal("salesTaxAmt"));
      formDO.setServiceTaxableTotal(cStmt.getBigDecimal("serviceTaxableAmt"));
      formDO.setServiceTaxCost(cStmt.getBigDecimal("serviceTaxAmt"));
      formDO.setDebitAdjustment(cStmt.getBigDecimal("debitAdjAmt"));
      formDO.setPackagingCost(cStmt.getBigDecimal("packagingAmt"));
      formDO.setFreightCost(cStmt.getBigDecimal("freightAmt"));
      formDO.setMiscellaneousCost(cStmt.getBigDecimal("miscAmt"));
      formDO.setCreditAdjustment(cStmt.getBigDecimal("creditAdjAmt"));
      formDO.setInvoiceTotal(cStmt.getBigDecimal("invoiceAmt"));
      formDO.setItemCount(cStmt.getInt("orderQty"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceTotalsByCiKey() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceTotalsByCiKey() " + e.getMessage());
      }
    }
    return formDO;
  }

//--------------------------------------------------------------------------------
// getCustomerInvoiceAllowCredit(conn, customerInvoiceDO)
//--------------------------------------------------------------------------------
  public CustomerInvoiceDO getCustomerInvoiceAllowCredit(Connection conn,
                                                         CustomerInvoiceDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and seventeen OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerInvoiceAllowCredit(?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, formDO.getGenesisKey());
      cStmt.setInt(2, formDO.getScenarioKey());
      cStmt.setBoolean(3, formDO.getActiveId());
      cStmt.setBoolean(4, formDO.getCreditId());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": invoiceQty : " + cStmt.getInt("invoiceQty"));
      cat.debug(this.getClass().getName() + ": allowCreditId : " + cStmt.getBoolean("allowCreditId"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setAllowCreditId(cStmt.getBoolean("allowCreditId"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerInvoiceAllowCredit() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerInvoiceAllowCredit() " + e.getMessage());
      }
    }
    return formDO;
  }

//--------------------------------------------------------------------------------
// creditCustomerInvoiceForm(customerInvoiceDO)
//--------------------------------------------------------------------------------
  public CustomerInvoiceDO creditCustomerInvoiceForm(CustomerInvoiceDO formDO)
   throws Exception {

    try {
      formDO.setKey(null);
//    formDO.setCustomerInvoiceNumber(null);
//--------------------------------------------------------------------------------
// The scenario key is set inside the "CustomerInvoiceAddAction" module.
//--------------------------------------------------------------------------------
      formDO.setScenarioKey(new Byte(EnumCustomerInvoiceScenario.ReturnCredit.getValue()));
      formDO.setNoteLine1(null);
      formDO.setNoteLine2(null);
      formDO.setNoteLine3(null);
//    formDO.setSalesTaxKey(rs.getByte("sales_tax_key"));
//    formDO.setSalesTaxRate(rs.getBigDecimal("sales_tax_rate"));
      formDO.setTaxableTotal(new BigDecimal("0"));
      formDO.setSalesTaxCost(new BigDecimal("0"));
      formDO.setDebitAdjustment(new BigDecimal("0"));
      formDO.setPackagingCost(new BigDecimal("0"));
      formDO.setFreightCost(new BigDecimal("0"));
      formDO.setMiscellaneousCost(new BigDecimal("0"));
      formDO.setCreditAdjustment(new BigDecimal("0"));
      formDO.setInvoiceTotal(new BigDecimal("0"));
      formDO.setActiveId(true);
//    formDO.setGenesisKey(rs.getInt("genesis_key"));
      formDO.setCreditId(true);
      formDO.setAllowCreditId(false);
      formDO.setCreateUser("");
      formDO.setCreateStamp(null);
      formDO.setUpdateUser("");
      formDO.setUpdateStamp(null);
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      throw new Exception("creditCustomerInvoiceForm() " + e.getMessage());
    }


    return formDO;
  }

}