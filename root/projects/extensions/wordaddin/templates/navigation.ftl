<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   <#elseif child.name = "doc_actions.js"><#assign doc_actions = child.id>
   </#if>
</#list>
<#if document.isDocument>
   <#assign thisSpace = document.parent>
<#else>
   <#assign thisSpace = document>
</#if>
<html>
<head>
<title>Basic Navigation</title>
 
<link rel="stylesheet" type="text/css"
href="/alfresco/css/taskpane.css" />

<script type="text/javascript">

var xmlHttp

function showStatus(url)
{
   xmlHttp=GetXmlHttpObject()
   if (xmlHttp==null)
   {
       alert ("Browser does not support HTTP Request")
       return
   }        
   xmlHttp.onreadystatechange=stateChanged 
   xmlHttp.open("GET",url,true)
   xmlHttp.send(null)
} 

function stateChanged() 
{ 
   if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete")
   { 
      document.getElementById("statusArea").innerHTML=xmlHttp.responseText 
      window.location.reload();
   } 
} 

function GetXmlHttpObject()
{ 
   var objXMLHttp=null
   if (window.XMLHttpRequest)
   {
     objXMLHttp=new XMLHttpRequest()
   }
   else if (window.ActiveXObject)
  {
     objXMLHttp=new ActiveXObject("Microsoft.XMLHTTP")
  }
   return objXMLHttp
} 

   function runAction(Action, Doc, Msg)
   {
      if (Msg != "" && !confirm(Msg))
      {
          return;
      }
      document.getElementById("statusArea").innerHTML="Running action...";
      showStatus("/alfresco/command/script/execute/workspace/SpacesStore/${doc_actions}/workspace/SpacesStore/" + Doc + "?action=" + Action);
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
					var spaceListElement = document.getElementById('spaceList');
					var contentListElement = document.getElementById('contentList');
					var tabBarElement = document.getElementById('tabBar');
                                        var currentSpaceInfoElement = document.getElementById('currentSpaceInfo');
                                        var spaceListHeaderElement = document.getElementById('spaceListHeader');
                                        var contentListHeaderElement = document.getElementById('contentListHeader');
                                        var bottomMarginElement = document.getElementById('bottomMargin');

					var spaceListHeight = spaceListElement.offsetHeight;
					var contentListHeight = contentListElement.offsetHeight;
					var tabBarHeight = tabBarElement.offsetHeight;
					var currentSpaceInfoHeight = currentSpaceInfoElement.offsetHeight;
					var spaceListHeaderHeight = spaceListHeaderElement.offsetHeight;
					var contentListHeaderHeight = contentListHeaderElement.offsetHeight;
					var bottomMarginHeight = bottomMarginElement.offsetHeight;


					if (windowHeight > 0) {
						spaceListElement.style.height = (windowHeight- (tabBarHeight + currentSpaceInfoHeight + spaceListHeaderHeight + contentListHeaderHeight + bottomMarginHeight)) /2 + 'px';
						contentListElement.style.height = (windowHeight- (tabBarHeight + currentSpaceInfoHeight + spaceListHeaderHeight + contentListHeaderHeight + bottomMarginHeight)) /2 + 'px';
					}

				}
			}
		}
		window.onload = function() {
			setContent();
			stripe('spaceList', '#fff', '#f6f8fa');
                        stripe('contentList', '#fff', '#f6f8fa');
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
<body>

<div id="tabBar">
    <ul>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_home}"><img src="/alfresco/images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li id="current"><a href="#"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li style="padding-right:6px;"><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_search}"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_details}"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_history}"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
    </ul>
  </div>


<div id="currentSpaceInfo">
<table>
            <tr>
                <td rowspan=2 class="valign">
                In:<img src="/alfresco${thisSpace.icon32}" border="0"/>
                </td>
                <td>
                <span style="font-weight:bold;">${thisSpace.name}</span>
                </td>
                </tr>
                <tr>
                <td>
<#if space.properties.description?exists>
		${thisSpace.properties.description}
</#if>
                </td>
                <td>
                &nbsp;&nbsp;&nbsp;&nbsp;
<#if thisSpace = companyhome>
<#else>
<img src="/alfresco/images/taskpane/go_up.gif" border="0" width="16" height="16"  alt="go up to parent space"/><a href="/alfresco/template/workspace/SpacesStore/${thisSpace.parent.id}/workspace/SpacesStore/${template.id}"><span title="Go up to parent space">Go Up</span></a>
</#if>
                </td>
            </tr>
          </table>
</div>

<div id="spaceListHeader"><span style="font-weight:bold">Spaces in ${thisSpace.name}</span></div>

<div id="spaceList">
          <table>
          <tbody>
<#list thisSpace.children as child>
   <#if child.isContainer>
            <!-- lb: start repeat -->
            <tr>
                <td>
                <a href="#"><img src="/alfresco${child.icon32}" border="0" alt="Open ${child.name}" /></a>
                </td>
                <td width="100%">
                <a href="/alfresco/template/workspace/SpacesStore/${child.id}/workspace/SpacesStore/${template.id}" title="Open ${child.name}">${child.name}</a><br/>
<#if child.properties.description?exists>
		${child.properties.description}
</#if>
                </td>
            </tr>
   </#if>
</#list>
            <!-- lb: end repeat -->
            </tbody>
          </table>
</div>

<div id="contentListHeader"><span style="font-weight:bold;">Documents in ${thisSpace.name}</span>
</div>

<div id="contentList">
          <table>
          <tbody>
<#list thisSpace.children as child>
   <#if child.isDocument>
            <!-- lb: start repeat -->
<#assign webdavPath = (child.displayPath?substring(13) + '/' + child.name)?url('ISO-8859-1')?replace('%2F', '/') />
            <tr>
                <td>
                <a href="#" onClick="window.external.openDocument('${webdavPath}')"><img src="/alfresco${child.icon32}" border="0" alt="Open ${child.name}" /></a>
                </td>
                <td style="line-height:16px;" width="100%">
                <a href="#" onClick="window.external.openDocument('${webdavPath}')" title="Open ${child.name}">${child.name}</a><br/>
<#if child.properties.description?exists>
		${child.properties.description}<br/>
</#if>
                Modified: ${child.properties.modified?datetime}, Size: ${child.size / 1024} Kb<br/>

                <a href="#" onClick="javascript:runAction('checkout','${child.id}', '');"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding:3px 6px 2px 0px;" alt="Check Out"></a><a href="#" onClick="javascript:runAction('makepdf','${child.id}', '');"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding:3px 6px 2px 0px;" alt="Make PDF..."></a><a href="#" onClick="javascript:runAction('delete','${child.id}', 'Are you sure you want to delete this document?');"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding:3px 6px 2px 0px;" alt="Delete..."></a>
                </td>
            </tr>
            <!-- lb: end repeat -->
   </#if>
</#list>
           </tbody>
          </table>
</div>
<spanid="statusArea">&nbsp;</span>
<div id="bottomMargin">&nbsp;</div>


</body>
</html>