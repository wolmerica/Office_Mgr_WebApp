/*
 * PermissionListForm.java
 *
 * Created on July 31, 2006, 6:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.permission;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;

public class PermissionListDO extends AbstractDO implements Serializable {

  private Boolean lockAvailableId = true;
  private Boolean myLockId = false;
  private Boolean listId = false;
  private Boolean viewId = false;
  private Boolean editId = false;
  private Boolean addId = false;
  private Boolean deleteId = false;
  private String lockedBy = "";

  public void setLockAvailableId(Boolean lockAvailableId) {
    this.lockAvailableId = lockAvailableId;
  }

  public Boolean getLockAvailableId() {
    return lockAvailableId;
  }

  public void setMyLockId(Boolean myLockId) {
    this.myLockId = myLockId;
  }

  public Boolean getMyLockId() {
    return myLockId;
  }

  public void setListId(Boolean listId) {
    this.listId = listId;
  }

  public Boolean getListId() {
    return listId;
  }

  public void setViewId(Boolean viewId) {
    this.viewId = viewId;
  }

  public Boolean getViewId() {
    return viewId;
  }

  public void setEditId(Boolean editId) {
    this.editId = editId;
  }

  public Boolean getEditId() {
    return editId;
  }

  public void setAddId(Boolean addId) {
    this.addId = addId;
  }

  public Boolean getAddId() {
    return addId;
  }

  public void setDeleteId(Boolean deleteId) {
    this.deleteId = deleteId;
  }

  public Boolean getDeleteId() {
    return deleteId;
  }

  public void setLockedBy(String lockedBy) {
    this.lockedBy = lockedBy;
  }

  public String getLockedBy() {
    return lockedBy;
  }
}

