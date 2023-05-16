/*
 * LookUpListForm.java
 *
 * Created on June 22, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.lookup;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;

public class LookUpListDO extends AbstractDO implements Serializable {

  private String lookUpName;
  private String lookUpInfo;
  private Integer lookUpCount;

  public void setLookUpName(String lookUpName) {
    this.lookUpName = lookUpName;
  }

  public String getLookUpName() {
    return lookUpName;
  }
  
  public void setLookUpInfo(String lookUpInfo) {
    this.lookUpInfo = lookUpInfo;
  }

  public String getLookUpInfo() {
    return lookUpInfo;
  }  

  public void setLookUpCount(Integer lookUpCount) {
    this.lookUpCount = lookUpCount;
  }

  public Integer getLookUpCount() {
    return lookUpCount;
  }

}

