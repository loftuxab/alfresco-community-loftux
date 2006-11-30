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

function showHide(show,hide) {

	document.getElementById(show).style.display = 'block';
	document.getElementById(hide).style.display = 'none';
}

var NewWin;


var closed
var closer

function expandNav(id) {


if (closed == 1) {
document.getElementById(closer).style.display = 'none';

}

var DivId = id; 
if (document.getElementById(DivId).style.display != 'block') {
	document.getElementById(DivId).style.display = 'block';
	} else {
	document.getElementById(DivId).style.display = 'none';
	}


closer = DivId
closed = 1;
}

function OpenNewSW(url,Name,w,h) {
closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',resizable=yes,toolbar=no,scrollbars=yes,menubar=yes,status=yes';
NewWin =  window.open(url,Name,winprops);
NewWin.focus();
}


function OpenNew(url,Name,w,h) {
closeWin ()
var winl = (screen.width - w) / 2;
var wint = (screen.height - h) / 2;
winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars=yes,status=yes,resizeable=yes';
NewWin =  window.open(url,Name,winprops);
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


function closeWin () {
  if (NewWin && (! NewWin.closed)) {
    NewWin.close ();
  } else {
    return false;
  }
}

function blockRollOver(id,color) {
document.getElementById(id).style.backgroundColor = color;
}

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
if (document.getElementById(DivId)) {
  if (document.getElementById(DivId).style.display != 'block') {
	document.getElementById(DivId).style.display = 'block';
	} else {
	document.getElementById(DivId).style.display = 'none';
	}
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

function hideClose() {

	if (document.images) {
		document.getElementById("window-close").style.display = "block";
	}
}

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

function bookmark(){
var chr = 'CTRL-D'; 
var agt=navigator.userAgent.toLowerCase(); 
if(agt.indexOf("opera")!= -1) chr = 'CTRL-T'; 
if (window.external) 
window.external.AddFavorite(self.location,document.title); 
else 
alert('Press '+chr+' to bookmark this page.'); 

}