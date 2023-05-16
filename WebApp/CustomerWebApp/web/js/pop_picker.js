/*
  Title: pop_picker
  Description: See the demo at url
*/
function show_list(str_target_key, str_target_description) {
	

  var vWinPicker = window.open(window.open("CustomerLookUp.do?filterLimit=A",
                               "PopupPage",
                               "height=300, width=285, scrollbars=yes, resizable=yes, top=200, left=200"));
  vWinPicker.opener = self;
  var pick_doc = vWinPicker.document;
  pick_doc.close();
	

  alert(str_target_key);
  alert(str_target_description);
	
}