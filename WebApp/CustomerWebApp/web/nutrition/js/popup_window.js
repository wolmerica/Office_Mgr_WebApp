/*
   Multi-window logic for popups.
*/
var newwindow = '';

function putinpopup(url) {
	if (!newwindow.closed && newwindow.location) {
		newwindow.location.href = url;
	}
	else {
		newwindow=window.open(url, 'PopupPage', 'height=250,width=320,scrollbars=yes,resizable=yes');
		if (!newwindow.opener) newwindow.opener = self;
	}
	if (window.focus) {newwindow.focus()}
	return false;
}