function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}


// This file contains all of the javascript that is common throughout templated pages on the site

var random_number =  (Math.random() * 100000000 ); 

function go_back()
{
  if (document.referrer != '')
  {
    location.replace(document.referrer);
  } else {
  history.back(-1);
  }
}

function SetRadioHidden(FormnameObj,FieldnameObj,ValueObj) {
	document.forms[FormnameObj].elements[FieldnameObj].value = ValueObj;
}

var closed
var closer

function expandItem(id) {


if (closed == 1) {
document.getElementById(closer).style.display = 'none';
}

var DivId = "block"+id; 
if (document.getElementById(DivId).style.display != 'block') {
	document.getElementById(DivId).style.display = 'block';
	} else {
	document.getElementById(DivId).style.display = 'none';
	}

closer = DivId
closed = 1;
}

function expandNav() {
var args;

for (args=0;args<arguments.length;args++) {
if (document.getElementById(arguments[args]).style.display != 'block') {
	document.getElementById(arguments[args]).style.display = 'block';
	} else {
	document.getElementById(arguments[args]).style.display = 'none';
	}
}
}

function rollOver(id,style) {
document.getElementById(id).className=style;
}

function blockRollOver(id,color) {
document.getElementById(id).style.backgroundColor = color;
}


var NewWin;

function OpenNew(url,Name,w,h) {
closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=yes,status=yes,resizeable=yes';
NewWin =  window.open(url,Name,winprops);
NewWin.focus();
}

function OpenPension(url,Name,w,h) {
closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=yes,status=yes,resizeable=yes';
var cookieString = document.cookie;
cookieStrPos = cookieString.indexOf('VM-Pension-Banner=');
if (cookieStrPos !=-1 )
	{	
	newCookieString= cookieString.slice(cookieStrPos + 18);
	CookieAry = newCookieString.split(';');
	Banner = CookieAry[0];
                url = url + '&banner=' + Banner;        
	}

NewWin =  window.open(url,Name,winprops);
NewWin.focus();
}

function OpenNewWithTools(url,Name,w,h) {
closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=1,location=1,toolbar=1,resizable=1,status=1';
NewWin =  window.open(url,Name,winprops);
NewWin.focus();
}

function OpenNewWithReferrer(url,Name,w,h) {

//Uncomment the swapMovie() function when movie needs swapping when app launched

closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=yes,status=yes,resizeable=yes';
var ref = window.location.href;
var text = "&referrer=";
fullUrl = url;
fullUrl += text += ref ;
NewWin = window.open(fullUrl,Name, winprops);
NewWin.focus();
}

function OpenWithCookie(url,Name,w,h,cookieLabel) {

closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=yes,status=yes,resizeable=yes';
var cookieValue = getCookieValue(cookieLabel);
var ref = window.location.href;
var text = "&referrer=";
fullUrl = url;
fullUrl += text += cookieValue += ref ;
NewWin = window.open(fullUrl,Name, winprops);
NewWin.focus();
}

function getCookieValue(cookieLabel){

var cookieString = document.cookie; 
cookieStrPos = cookieString.indexOf(cookieLabel);
if (cookieStrPos !=-1 )
   {      
   if (cookieString.indexOf(';') !=-1)
	{  	
	ourCookieString = cookieString.slice(cookieStrPos);	
	cookieArray = ourCookieString.split(";");
	cookieValue = cookieArray[0];	
	return cookieValue;
	}  	
   }   
else
   {   
   return "";
   }
}

function OpenNewWithRedirect(url,Name,w,h,redirect) {

closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=yes,status=yes,resizeable=yes';
var ref = window.location.href;
var text = "&referrer=";
fullUrl = url;
//fullUrl += text += ref ;
NewWin = window.open(fullUrl,Name, winprops);
NewWin.focus();
location.replace(redirect)
}

function OpenBlank(url) {
NewWin =  window.open(url);
NewWin.focus();
}

function closeWin () {
  if (NewWin && (! NewWin.closed)) {
    NewWin.close ();
  } else {
    return false;
  }
}

function shares() {

window.parent.location.href = '/sharedealing/shares.shtml';
}

function OpenHelpWin(url,w,h) {

Help = window.open(url,'Help','width=' + w + ',height=' + h + ',top=100,left=100,scrollbars=1,toolbar=0,location=0');
Help.window.focus();
}

function OpenApp(pram1,pram2,pram3) {

url = '/isa-app/pages/isa_about_you.jsp-SSL?newsession=true&product=' + pram1 + '&fund=' + pram2 + '&investinto=' + pram3;

if (window.screen){
  var w = screen.width;
  if(w<835){
   NewWin = window.open(url, 'NewWin','width=620, height=520, left=0, top=0, scrollbars=yes,toolbar=no,location=no,resizable=no,status=yes');
  } else {
   NewWin = window.open(url, 'NewWin','width=620, height=520, left=100, top=100, scrollbars=yes,toolbar=no,location=no,resizable=no,status=yes');
  }
} else {
NewWin = window.open(url, 'NewWin','width=680, height=520, left=100, top=100, scrollbars=yes,toolbar=no,location=no,resizable=no,status=yes');
}

NewWin.window.focus();
}



// end of additions 06/08/03
window.defaultStatus="Virgin Money"

//WPM Functions to append 'wpm@' at the start of the URL

function nextPage(url) 
{
   fullUrl = url;
     //alert('fullUrl  = '+fullUrl);
     //alert('this host = '+ location.hostname );
   window.location.href ='http://vmaw@' + location.hostname + fullUrl;
}

//this is for loan and card,BannerRecorder-servlet for thirdparty tracking
function next(url) 
{
 
  
  fullUrl = url;
  window.location.href ='http://' + location.hostname + fullUrl +'active/BannerRecorder?source=VMY-UN-20181-21924';
  //alert('this host = http://' + location.hostname + fullUrl +'active/BannerRecorder?source=VMY-UN-20181-21924');
}

var arrayLength = 1;
var start = 1
var timerID;

function ShowNavMenu(id,timer) {

	for (i=start;i<=arrayLength;i++) {
		if ('drop'+i == id) {

	if (agt.indexOf("mac")>=0){

		if (app.indexOf("microsoft")>=0) {

			if (ver.indexOf("5")>=0) {

				document.getElementById('drop'+i).style.top = "124px";
			}
		}
	}

		document.getElementById('drop'+i).style.visibility = "visible";
		clearTimeout(timerID)
		timerID = setTimeout ("hideNavMenu('"+id+"')",timer)
		} else {
		document.getElementById('drop'+i).style.visibility = "hidden";
		
		
		}
	
	}

}

function hideNavMenu(id) {
document.getElementById(id).style.visibility = "hidden";
}

function clearTimer() {
clearTimeout(timerID)
}

function setTimer(id) {
timerID = setTimeout ("hideNavMenu('"+id+"')",3000)
}

function accessNav(id) {

for (i=start;i<=arrayLength;i++) {
		if ('drop'+i == id) {

	if (agt.indexOf("mac")>=0){

		if (app.indexOf("microsoft")>=0) {

			if (ver.indexOf("5")>=0) {

				document.getElementById('drop'+i).style.top = "124px";
			}
		}
	}

		document.getElementById('drop'+i).style.visibility = "visible";
		document.getElementById('drop'+i).focus();
		} else {
		document.getElementById('drop'+i).style.visibility = "hidden";
		
		
		}
	
	}




}

function focusNav(id) {
document.getElementById(id).focus();
}




var agt = navigator.userAgent.toLowerCase();
var app = navigator.appName.toLowerCase();
var ver = navigator.appVersion.toLowerCase();

function detectSeenWarning() {
var cookieString = document.cookie;
var noNews = true;

	for (i=0;i<document.forms.length;i++) {
		var cook = document.forms[i].name;

		if (cookieString.indexOf(cook) !=-1) {

		
	               document.getElementById(cook).style.display = 'none';
	                } else {
	                document.getElementById(cook).style.display = 'block';
                                noNews = false; 
		}
	}
                if (noNews) {
                              document.getElementById("news").style.display = 'none';
                }
}


function setSeenWarning(url) {

	var date = new Date;
	date.setDate(date.getDate()+30);

		for (i=0;i<document.forms.length;i++) {
			if (document.forms[i].yes.checked == true) {
				document.cookie = document.forms[i].name+"=true;expires="+date;
			}

		}


}


function hideItem() {

	if (closerNew) {

	document.getElementById(closerNew).style.display = "none";
	//closed = 0;
	}
}

var closedNew
var closerNew

var globalclosed
var globalcloser
var DivId
var defaultnav


function expandGlobalItem(id) {


if (globalclosed == 1) {
document.getElementById(globalcloser).style.display = 'none';
}

DivId = id;
if (document.getElementById(DivId).style.display != 'block') {
	document.getElementById(DivId).style.display = 'block';
	} else {
	document.getElementById(DivId).style.display = 'none';
	}

globalcloser = DivId
globalclosed = 1;
}



function loadExpandGlobalItem(id) {


if (globalclosed == 1) {
document.getElementById(globalcloser).style.display = 'none';
}

DivId = id;
defaultnav = id;
if (document.getElementById(DivId).style.display != 'block') {
	document.getElementById(DivId).style.display = 'block';
	} else {
	document.getElementById(DivId).style.display = 'none';
	}

globalcloser = DivId
globalclosed = 1;



}



function hideGlobalItem(layer) {





		if (globalcloser) {

			document.getElementById(globalcloser).style.display = "none";
			
			if (defaultnav && layer == 0) {
			document.getElementById(defaultnav).style.display = "block";
			globalcloser = defaultnav
			}
			


		}



}

var hide;
var navtimerID

function timer() {

	if (document.getElementById("selected-product")) {
			document.getElementById("selected-product").style.color = "#FFFFFF"
	}

}


function showSelected(nav) {

clearTimeout(navtimerID)
navtimerID = setTimeout("timer()", 500)

}






function hideSelected(nav) {

	if (document.getElementById("selected-product")) {

		document.getElementById("selected-product").style.color = "#000000";

	}

}

function setLayer() {
clearTimeout(navtimerID)
}




var layer
var theme
var layertimer
var layerhide
var timerID

function timedShowLayer(layer,theme) {

clearTimeout(timerID)

layerhide = 0
showLayer = "timedDefaultLayerShow('"+layer+"','"+theme+"')"
timerID = setTimeout(showLayer, 3000)


}

function timedDefaultLayerShow(layer,theme) {

	if (layerhide==0) {

		blockRollOver('global-sub-nav',theme)
		expandGlobalItem(layer)

	}


}


function resetLayerTimer() {

clearTimeout(timerID)

}