<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   </#if>
</#list>

<#if args.search?exists>
   <#assign searchString = args.search>
   <#if searchString != "">
      <#assign queryString = "TEXT:\"${searchString}\" @cm\\:title:${searchString}">
   </#if>
<#else>
   <#assign searchString = "">
   <#assign queryString = "">
</#if>

<#if searchString != "">
   <#if args.maxresults?exists>
      <#assign maxresults=args.maxresults?number>
   <#else>
      <#assign maxresults=10>
   </#if>

   <#assign rescount=1>


<!-- Start output -->
          <table>
                 <tbody style="font-family: tahoma, sans-serif; font-size: 11px;">
   <#assign results = companyhome.childrenByLuceneSearch[queryString] >
   <#if results?size = 0>
                 <tr><td>No results found.</td></tr>
   <#else>
      <#list results as child>
            <!-- lb: start repeat -->
         <#if child.isDocument>
            <#if child.name?ends_with(".pdf")>
               <#assign openURL = "/alfresco${child.url}">
               <#assign hrefExtra = " target=\"_blank\"">
            <#else>
               <#assign webdavPath = (child.displayPath?substring(13) + '/' + child.name)?url('ISO-8859-1')?replace('%2F', '/')?replace('\'', '\\\'') />
               <#assign openURL = "#">
               <#assign hrefExtra = " onClick=\"window.external.openDocument('${webdavPath}')\"">
            </#if>
         <#else>
            <#assign openURL = "/alfresco/template/workspace/SpacesStore/${child.id}/workspace/SpacesStore/${office_browse}?search=${searchString}&maxresults=${maxresults}">
            <#assign hrefExtra = "">
         </#if>
                 <tr>
                     <td>
                    <a href="${openURL}" ${hrefExtra}><img src="/alfresco${child.icon32}" border="0" alt="Open ${child.name}" /></a>
                     </td>
                     <td width="100%">
                     <a href="${openURL}" ${hrefExtra} title="Open ${child.name}">${child.name}</a><br/>
         <#if child.properties.description?exists>
                ${child.properties.description}<br/>
         </#if>
         <#if child.isDocument>
                Modified: ${child.properties.modified?datetime}, Size: ${child.size / 1024} Kb<br/>
         </#if>
                       </td>
                     </tr>
            <!-- lb: end repeat -->
         <#if rescount = maxresults>
            <#break>
         </#if>
         <#assign rescount=rescount + 1>
      </#list>
	</#if>
          </tbody>
          </table>
<!-- End output -->

</#if>
<!-- End of returning search results -->

<!-- Display Search UI -->
<#if !args.search?exists>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Search</title>

<link rel="stylesheet" type="text/css"
href="/alfresco/css/taskpane.css" />

<script type="text/javascript">

var xmlHttp

function GetXmlHttpObject()
{
   var objXMLHttp=null;
   if (window.XMLHttpRequest)
   {
      objXMLHttp=new XMLHttpRequest()
   }
   else if (window.ActiveXObject)
   {
       objXMLHttp=new ActiveXObject("Microsoft.XMLHTTP")
   }

   return objXMLHttp;
}

function showStatus(url)
{
//   alert(url);
   xmlHttp=GetXmlHttpObject();
   if (xmlHttp==null)
   {
       alert("Browser does not support HTTP Request");
       return;
   }
   xmlHttp.onreadystatechange=stateChanged;
   xmlHttp.open("GET",url+"&sid="+Math.random(),true);
   xmlHttp.send(null);
}

function stateChanged()
{
   if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete")
   {
      if (xmlHttp.responseText.indexOf("System Error") > 0)
      {
          var myWindow = window.open("", "_blank", "scrollbars,height=500,width=400");
          myWindow.document.write(xmlHttp.responseText);
      }
      else
      {
          document.getElementById("searchResultsList").innerHTML=xmlHttp.responseText;
          stripe('searchResultsList', '#fff', '#f6f8fa');
      }
      document.getElementById("statusArea").innerHTML="";
   }
}

function runSearch()
{
   document.getElementById("statusArea").innerHTML="Searching...";
   searchString = document.getElementById("searchText").value;
   maxcount = document.getElementById("maxresults").value;
   doSearch(searchString, maxcount);
}

function doSearch(searchString, maxcount)
{
   showStatus("/alfresco/template/workspace/SpacesStore/${office_search}/workspace/SpacesStore/${office_search}?search=" + searchString + "&maxresults=" + maxcount);
}

function handleTextEnter(e) {
   var keycode;

   // get the keycode
   if (window.event) 
   {
      keycode = window.event.keyCode;
   }
   else if (e)
   {
      keycode = e.which;
   }   
   // if ENTER was pressed execute the query
   if (keycode == 13)
   {  
      runSearch();
      return false;
   }
   return true;
}


function getWindowHeight() {
			var windowHeight = 0;
			if (typeof(window.innerHeight) == 'number') {
				windowHeight = window.innerHeight;
			}
			else {
				if (document.documentElement && document.documentElement.clientHeight) {
					windowHeight = document.documentElement.clientHeight;
				}
				else {
					if (document.body && document.body.clientHeight) {
						windowHeight = document.body.clientHeight;
					}
				}
			}
			return windowHeight;
		}

function setContent() {
			if (document.getElementById) {
				var windowHeight = getWindowHeight();
				if (windowHeight > 0) {

                                        var tabBarElement = document.getElementById('tabBar');
                                        var bottomMarginElement = document.getElementById('bottomMargin');
                                        var searchResultsListElement = document.getElementById('searchResultsList');
                                        var searchHeightElement = document.getElementById('search');
                                        var searchResultsListHeaderElement = document.getElementById('searchResultsListHeader');

					var tabBarHeight = tabBarElement.offsetHeight;
					var bottomMarginHeight = bottomMarginElement.offsetHeight;
					var searchResultsListHeight = searchResultsListElement.offsetHeight;
                                        var searchHeight = searchHeightElement.offsetHeight;
                                        var searchResultsListHeaderHeight = searchResultsListHeaderElement.offsetHeight;

					if (windowHeight > 0) {
						searchResultsListElement.style.height = windowHeight- (tabBarHeight + searchHeight + searchResultsListHeaderHeight + bottomMarginHeight) + 'px';
					}

				}
			}
		}
		window.onload = function() {
			setContent();
			stripe('searchResultsList', '#fff', '#f6f8fa');
		}
		window.onresize = function() {
			setContent();
		}
		</script>

             <script type="text/javascript">



  // this function is need to work around
  // a bug in IE related to element attributes
  function hasClass(obj) {
     var result = false;
     if (obj.getAttributeNode("class") != null) {
         result = obj.getAttributeNode("class").value;
     }
     return result;
  }

function stripe(id) {

    // the flag we'll use to keep track of
    // whether the current row is odd or even
    var even = false;

    // if arguments are provided to specify the colours
    // of the even & odd rows, then use the them;
    // otherwise use the following defaults:
    var evenColor = arguments[1] ? arguments[1] : "#fff";
    var oddColor = arguments[2] ? arguments[2] : "#eee";

    // obtain a reference to the desired table
    // if no such table exists, abort
    var table = document.getElementById(id);
    if (! table) { return; }

    // by definition, tables can have more than one tbody
    // element, so we'll have to get the list of child
    // &lt;tbody&gt;s
    var tbodies = table.getElementsByTagName("tbody");

    // and iterate through them...
    for (var h = 0; h < tbodies.length; h++) {

     // find all the &lt;tr&gt; elements...
      var trs = tbodies[h].getElementsByTagName("tr");

      // ... and iterate through them
      for (var i = 0; i < trs.length; i++) {

	    // avoid rows that have a class attribute
        // or backgroundColor style
	    if (!hasClass(trs[i]) && ! trs[i].style.backgroundColor) {

         // get all the cells in this row...
          var tds = trs[i].getElementsByTagName("td");

          // and iterate through them...
          for (var j = 0; j < tds.length; j++) {

            var mytd = tds[j];

            // avoid cells that have a class attribute
            // or backgroundColor style
	        if (! hasClass(mytd) && ! mytd.style.backgroundColor) {

		      mytd.style.backgroundColor = even ? evenColor : oddColor;

            }
          }
        }
        // flip from odd to even, or vice-versa
        even =  ! even;
      }
    }
  }

</script>

</head>
   <#if args.searchagain?exists>
      <#assign onLoad = "onLoad = \"doSearch('${args.searchagain}', '${args.maxresults}');\"">
   <#else>
      <#assign onLoad = "">
   </#if>
<body ${onLoad}>



<div id="tabBar">
    <ul>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_home}"><img src="/alfresco/images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_browse}"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li id="current" style="padding-right:6px;"><a href="#"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_details}"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_history}"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
    </ul>
</div>

<div id="search">
<table width="100%" border="0" style="font-family: tahoma, sans-serif; font-size: 11px;">
   <tr valign="top">
      <td align="left" valign="middle">
         Search for <input type="text" id="searchText" name="searchText" value="" maxlength='1024' style='width:140px;font-size:10px' onkeyup="return handleTextEnter(event);" /><input type="button" name="simpleSearchButton" id="simpleSearchButton" class="button" onClick="javascript:runSearch();" value="Search"/><br/>
<label><SELECT id="maxresults" NAME="maxresults" onchange="javascript:runSearch();">
        <option id="5" name="5" value=5>5</option>
        <option id="10" name="10" value=10>10</option>
        <option id="15" name="15" value=15>15</option>
        <option id="20" name="20" value=20>20</option>
        <option id="50" name="50" value=50>50</option>
        </select>&#160;Items</label><br/>
      </td>
   </tr>
</table>
</div>

<div id="searchResultsListHeader"><span style="font-weight:bold">Items Found</span></div>

<div id="searchResultsList">
   <table>
      <tbody>
      </tbody>
   </table>
</div>

<div id="bottomMargin" style="height:24px;"><span id="statusArea">&nbsp;</span>
</div>

</body>
</html>
</#if>
<!-- End of Search UI -->