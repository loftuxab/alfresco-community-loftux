<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   <#elseif child.name = "doc_actions.js"><#assign doc_actions = child.id>
   </#if>
</#list>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Version History</title>

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
          document.getElementById("statusArea").innerHTML=""; 
      }
      else
      {
          document.getElementById("statusArea").innerHTML=xmlHttp.responseText; 
          window.location.reload();
      }
   } 
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
					var versionListHeaderElement = document.getElementById('versionListheader');
					var versionListElement = document.getElementById('versionList');
					var tabBarElement = document.getElementById('tabBar');
                                        var bottomMarginElement = document.getElementById('bottomMargin');
 
					var versionListHeight = versionListElement.offsetHeight;
					var versionListHeaderHeight = versionListHeaderElement.offsetHeight;
					var tabBarHeight = tabBarElement.offsetHeight;
					var bottomMarginHeight = bottomMarginElement.offsetHeight;
 
					if (windowHeight > 0) {
						versionListElement.style.height = (windowHeight- (tabBarHeight + versionListHeaderHeight + bottomMarginHeight)) + 'px';
					}

				}
			}
		}
		window.onload = function() {
			setContent();
			stripe('versionList', '#fff', '#f6f8fa');
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
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_browse}"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li style="padding-right:6px;"><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_search}"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_details}"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li id="current"><a href="#"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
    </ul>
  </div>

<div id="versionListHeader"><span style="font-weight:bold">Version History for ${document.name}</span></div>

<div id="versionList">
          <table>
                 <tbody>
<#if document.isDocument >
   <#if hasAspect(document, "cm:versionable") == 1 >
                 <!-- lb: start repeat row -->
      <#list document.versionHistory as record>
         <#assign webdavPath = (child.displayPath?substring(13) + '/' + child.name)?url('ISO-8859-1')?replace('%2F', '/') />
                   <tr>
                       <td valign="top">
                       <a href="#" onClick="window.external.openDocument('${webdavPath}')"><img src="/alfresco/images/taskpane/document.gif" border="0" alt="Open ${record.versionLabel}"/></a>
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <a href="#" title="Open ${record.versionLabel}"><span style="font-weight:bold;">${record.versionLabel}</span></a><br/>
                       Author: ${record.creator}<br/>
                       Date: ${record.createdDate?datetime}<br/>
                       Notes: [notes]<br/>
                       </td>
                   </tr>
      </#list>
   <#else>
                   <tr>
                       <td valign="top">
The current document is not versioned.<br>
<a href="#" onClick="javascript:runAction('makeversion','${document.id}', '');">Make Versionable</a>
                       </td>
                   </tr>
   </#if>
                   <!-- lb: end repeat row -->
<#else>
                   <tr>
                       <td valign="top">
The current document is not being managed by Alfresco.
                       </td>
                   </tr>
</#if>
                 </tbody>
          </table>
</div>

<div id="bottomMargin" style="height:24px; padding-left:6px;"><span id="statusArea">&nbsp;</span>
</div>


</body>
</html>