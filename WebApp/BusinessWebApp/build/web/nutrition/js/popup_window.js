/*
<!-- Multi-window logic for popups. -->
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


function helppopup(url) {
	if (!newwindow.closed && newwindow.location) {
		newwindow.location.href = url;
	}
	else {
		newwindow=window.open(url, 'PopupPage', 'height=400,width=575,scrollbars=yes,resizable=yes');
		if (!newwindow.opener) newwindow.opener = self;
	}
	if (window.focus) {newwindow.focus()}
	return false;
}

function pricingpopup(url) {
	if (!newwindow.closed && newwindow.location) {
		newwindow.location.href = url;
	}
	else {
		newwindow=window.open(url, 'PopupPage', 'height=300,width=440,scrollbars=no,resizable=no');
		if (!newwindow.opener) newwindow.opener = self;
	}
	if (window.focus) {newwindow.focus()}
	return false;
}