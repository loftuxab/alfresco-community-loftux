<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

var webProjectFolder = getWebProjectFolder();
var queryString = "";

var i = url.full.indexOf("?");
if(i > -1)
	queryString = "&" + url.full.substring(i+1, url.full.length());

// redirect

model.redirectUrl = "/alfresco/c/ui/editwebcontent?webproject="+webProjectFolder.id+queryString;
