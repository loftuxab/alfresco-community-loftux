var xhr = false;

var maxresults       = 5;
var status           = 'Any';
var askid	         = '';
var article_type     = '';
var article_modifier = '';
var category		 = '';
var modified         = '';
var searchText   = '';
var visibility         ='';
var alfresco_version='';

function setMaxresults(){
maxresults = document.getElementById("maxresults").value;
//send();
}

function setStatus(){
status    = document.getElementById("status").value;
//alert(status);
send();

}

function setAlfrescoversion(){
alfresco_version= document.getElementById("alfresco_version").value;
send();
}

function setArticletype(){
article_type  =  document.getElementById("article_type").value;
//alert(article_type);
send();
}

function setModifier(){
article_modifier = document.getElementById("article_modifier").value;
//alert(article_modifier);
send();
}

function setVisibility(){
visibility=document.getElementById('visibility').value;
send();


}

function askIDSearch(e)	{
	var keycode;
	if (window.event) keycode = window.event.keyCode;
	else if (e) keycode = e.which;
		if (keycode == 13) {
			askidsearch('');
			return false;
	}

	return true;
	}

function categorydisplay() {
	
	vista = (document.getElementById("categorydisplay").style.display == 'none') ? 'block' : 'none';
	document.getElementById("categorydisplay").style.display = vista;
	
	//Change the text on the basic/advanced hyperlink
	toggle = (document.getElementById("toggleadvanced").innerHTML == 'Advanced&gt;&gt;') ? '&lt;&lt;Basic' : 'Advanced&gt;&gt;';
	document.getElementById("toggleadvanced").innerHTML = toggle;
	if (toggle == "Advanced&gt;&gt;") {
			resetCategories();
		}
	}

<!-- For Yahoo YUI Calendar control -->
			
		YAHOO.namespace("example.calendar");

		function init() {
      //Modified Date Handler
			YAHOO.example.calendar.cal2 = new YAHOO.widget.Calendar("YAHOO.example.calendar.cal2", "cal2Container");
			YAHOO.example.calendar.cal2.onSelect = function(selected) {
                                document.getElementById("cal2Container").style.display = 'none';
				var dateString = selected;
				var dateArray=dateString.toString().split(",");
				var theYear = dateArray[0];
				var theMonth = dateArray[1];
				var theDay = dateArray[2];
				document.getElementById("modified").value = theDay + "/" + theMonth + "/" + theYear;
                                modified=  document.getElementById("modified").value;
                              //  alert(modified);
                                send();
			}
                       

			YAHOO.example.calendar.cal2.onDeselect = function(deselected) {
				//alert("deselected: " + deselected);
			}

			YAHOO.example.calendar.cal2.render();
		}


		YAHOO.util.Event.addListener(window, "load", init);
	
	
	function showCalendar2() {
			var pos = YAHOO.util.Dom.getXY("link2");
			document.getElementById("cal2Container").style.display = 'block';
                        
		}


<!-- End of calender handling stuff -->
	
function resetCategories() {
	//Reset the selected category
	for (var i = 0; i < document.getElementById("category").options.length; i++) {
		document.getElementById("category").options[i].selected = false;
   	}
   document.getElementById("category").options[0].selected = true;
document.getElementById("alfresco_version").options[0].selected = true;
   document.getElementById("visibility").options[0].selected = true;
document.getElementById("cal2Container").style.display='none'
       document.getElementById("modified").value ='';
	}

function resetSearchArticles() {
	document.getElementById("alfresco_version").options[0].selected = true;
    	document.getElementById("status").options[0].selected = true;
    	document.getElementById("article_modifier").options[0].selected = true;
        document.getElementById("visibility").options[0].selected = true;
    	
	document.getElementById("article_type").options[0].selected = true;
        document.getElementById("maxresults").options[0].selected = true;
	resetCategories('');
	
	document.getElementById("categorydisplay").style.display = 'none'
	document.getElementById("toggleadvanced").innerHTML = 'Advanced&gt;&gt;';
	
	document.getElementById("modified").value = "";
	document.getElementById("searchText").value = "";
	document.getElementById("askid").value = "";
	
	//sndReq('reset');
	//document.getElementById("searcharticles").reset();
        document.getElementById("searchResults").innerHTML='';
        maxresults       = 5;
         status           = 'Any';
         askid	         = '';
         article_type     = '';
         article_modifier = '';
         category		 = '';
         modified         = '';
        searchText   = '';
        visibility         ='';
        alfresco_version='';
	document.getElementById("cal2Container").style.display='none'
       document.getElementById("modified").value ='';
	}

function askidsearch()
{
askid=document.getElementById('askid').value;
document.getElementById('askid').value='';

if(askid!=''){
var url="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/search_results.ftl&askid="+askid+"&maxresults="+maxresults;
makeRequest(url);
askid='';
}
else
{
alert("ASKID cannot be null");
return false;
}
}

function pagination(p)
{
    
     var url="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/search_results.ftl&searchText="+searchText+"&askid="+askid+"&maxresults="+maxresults+"&status="+status+"&modifier="+article_modifier+"&article_type="+article_type+"&article_modifier="+article_modifier+"&visibility="+visibility+"&modified="+modified+"&alfresco_version="+alfresco_version+"&p="+p;
makeRequest(url);
	

return false;


}
function send(){
searchText=document.getElementById('searchText').value;
var url="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/search_results.ftl&searchText="+searchText+"&askid="+askid+"&maxresults="+maxresults+"&status="+status+"&modifier="+article_modifier+"&article_type="+article_type+"&article_modifier="+article_modifier+"&visibility="+visibility+"&modified="+modified+"&alfresco_version="+alfresco_version;
makeRequest(url);
	return false;
}

function textsearch(){
searchText=document.getElementById('searchText').value;
//if(searchText!=''){
var url="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/search_results.ftl&searchText="+searchText+"&askid="+askid+"&maxresults="+maxresults+"&status="+status+"&modifier="+article_modifier+"&article_type="+article_type+"&article_modifier="+article_modifier+"&visibility="+visibility+"&modified="+modified+"&alfresco_version="+alfresco_version;
makeRequest(url);
//searchText='';
//document.getElementById('searchText').value='';
//}
//else
//{
//alert("Search text cannot be null");
return false;
//}
}

function searchcheckWFForm(nodeid, returnid) {

	var reviewer = document.getElementById("workflowAssignee").value;
	
    if (document.getElementById("workflowAssignee").options[0].selected == true)	{
    	alert("Please select a reviewer");
    	//return false;
    	}
    else {		
    			var url="/alfresco/command/script/execute?scriptPath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/script/start_workflow.js&workflowAssignee=" + reviewer + "&nodeid=" + nodeid + "&returnid=" + returnid;
    			makeRequest(url);
   	}

}

function searchCancelWFForm(nodeid,returnid) {

	document.getElementById('searchResults').innerHTML = "";

}

function searchsndWFReq(nodeid) {
	    
	   var url ="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/start_ask_approval_workflow.ftl&returnid=search" + nodeid + "&nodeid=" + nodeid+"";
    	makeRequest(url);
}   

function getNewFile() {
//var askid=document.getElementById("askid").value;
      
	makeRequest(url);
	return false;
}

function makeRequest(url) {
	if (window.XMLHttpRequest) {
		xhr = new XMLHttpRequest();
	}
	else {
		if (window.ActiveXObject) {
			try {
				xhr = new ActiveXObject("Microsoft.XMLHTTP");
			}
			catch (e) { }
		}
	}

	if (xhr) {
		xhr.onreadystatechange = showContents;
		xhr.open("POST", url, true);
		xhr.send(null);
	}
	else {
		document.getElementById("searchResults").innerHTML = "Sorry, but I couldn't create an XMLHttpRequest";
	}
}

function showContents() {
	if (xhr.readyState == 4) {
		if (xhr.status == 200) {
			var outMsg = (xhr.responseXML && xhr.responseXML.contentType=="text/xml") ? xhr.responseXML.getElementsByTagName("choices")[0].textContent : xhr.responseText;
		}
		else {
			var outMsg = "There was a problem with the request " + xhr.status;
		}
		document.getElementById("searchResults").innerHTML = outMsg;
	}
}

function reset(){
document.getElementById("askid").value='';
document.getElementById("searchResults").innerHTML ="";
}

function test(){


var alfresco_version=document.getElementById('alfresco_version').value;



var versionList = "";
	for (var i = 0; i < document.getElementById("alfresco_version").options.length; i++) {
  	if (document.getElementById("alfresco_version").options[i].selected) {
    		if(versionList != "") {
    			versionList = versionList + ",";
    		}
    	versionList = versionList  + document.getElementById("alfresco_version").options[i].value;
    	}
	}
alert(versionList);
     var url="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/t1.ftl&alfresco_version="+versionList;
makeRequest(url);
	

return false;


}