/* 
<!------------------------------------------------------------------------------->
<!-- Permission Slip Transformation from text to radio buttons  07/21/2006     -->
<!------------------------------------------------------------------------------->
<!-- www.wolmerica.com  permission_slip.js                                     -->
<!-- Copyright (C) 2006 by Richard Wolschlager                                 -->
<!------------------------------------------------------------------------------->
*/
// Description:
// A text field named permissionSlip contains a list of permissions.
// Each character represents the permission for a single feature.
// The following functions will first set radio button values to
// show the current permission settings for the user.  The second
// function will save any changes made with the radio buttons back
// into the text field that will be saved in the database.
//--------------------------------------------------------------------------------

//--------------------------------------------------------------------------------
// Transform a text string of values into the appropriate radio button
// sequence.  Radio buttons provide the best interface for setting permissions.    
//--------------------------------------------------------------------------------
  function initPerm() {
    for(r=0; r<50; r++) {
      pname = 'document.PermissionForm.f' + r;
      pname = eval(pname);
      if (pname == null || pname == undefined) {
        break;
      } else {
        pval = document.PermissionForm.permissionSlip.value.substr(r,1);
        for (i=0; i<pname.length; i++) {
          if (pname[i].value == pval) {
            pname[i].checked = true;
            break;
          }
        }
      }
    }
  }
//--------------------------------------------------------------------------------
// Transform the radio button setting back into the text permissionSlip string.
//--------------------------------------------------------------------------------
  function setPermissionSlip(obj) {
    pname = obj.name;
    pval = obj.value;
    r = pname.substr(1,pname.length) * 1;
    pslip = document.PermissionForm.permissionSlip.value;
    frt = pslip.substr(0,r);
    bck = pslip.substr(r+1,pslip.length);
    pslip = frt + pval + bck;
    // Assign the new permission slip value to the hidden field in the form.
    document.PermissionForm.permissionSlip.value = pslip;
//    alert(document.PermissionForm.permissionSlip.value);
  }