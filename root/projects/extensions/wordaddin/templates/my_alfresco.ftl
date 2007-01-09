<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   </#if>
</#list>
<#if document?exists>
   <#if document = template>
      <#assign thisContext = companyhome.id>
    <#else>
      <#assign thisContext = document.id>
   </#if>
<#else>
   <#assign thisContext = space.id>
</#if>  
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>My Alfresco</title>

<link rel="stylesheet" type="text/css"
href="/alfresco/css/taskpane.css" />

<script type="text/javascript">

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
					var mycheckedoutdocsListElement = document.getElementById('mycheckedoutdocsList');
					var mytodoListElement = document.getElementById('mytodoList');
					var tabBarElement = document.getElementById('tabBar');
                                        var mycheckedoutdocsListHeaderElement = document.getElementById('mycheckedoutdocsListHeader');
                                        var mytodoListHeaderElement = document.getElementById('mytodoListHeader');
                                        var bottomMarginElement = document.getElementById('bottomMargin');
                                        var documentActionsElement = document.getElementById('documentActions');

					var mycheckedoutdocsListHeight = mycheckedoutdocsListElement.offsetHeight;
					var mytodoListHeight = mytodoListElement.offsetHeight;
					var tabBarHeight = tabBarElement.offsetHeight;
					var mycheckedoutdocsListHeaderHeight = mycheckedoutdocsListHeaderElement.offsetHeight;
					var mytodoListHeaderHeight = mytodoListHeaderElement.offsetHeight;
					var bottomMarginHeight = bottomMarginElement.offsetHeight;
                                        var documentActionsHeight = documentActionsElement.offsetHeight;

					if (windowHeight > 0) {
						mycheckedoutdocsListElement.style.height = (windowHeight- (tabBarHeight + mytodoListHeaderHeight + mycheckedoutdocsListHeaderHeight + documentActionsHeight + bottomMarginHeight)) /2 + 'px';
						mytodoListElement.style.height = (windowHeight- (tabBarHeight + mytodoListHeaderHeight + mycheckedoutdocsListHeaderHeight + documentActionsHeight + bottomMarginHeight)) /2 + 'px';
					}

				}
			}
		}
		window.onload = function() {
			setContent();
			stripe('mytodoList', '#fff', '#f6f8fa');
                        stripe('mycheckedoutdocsList', '#fff', '#f6f8fa');
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
      <li id="current"><a href="#"><img src="/alfresco/images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${thisContext}/workspace/SpacesStore/${office_browse}"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li style="padding-right:6px;"><a href="/alfresco/template/workspace/SpacesStore/${thisContext}/workspace/SpacesStore/${office_search}"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${thisContext}/workspace/SpacesStore/${office_details}"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${thisContext}/workspace/SpacesStore/${office_history}"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
      <li><a href="#"><img src="/alfresco/images/taskpane/workflow.gif" border="0" alt="View Workflow Info" /></a></li>
    </ul>
  </div>

<div id="mycheckedoutdocsListHeader"><span style="font-weight:bold">My checked out documents</span></div>

<div id="mycheckedoutdocsList">
          <table>
                 <tbody>
<#assign query="@cm\\:lockOwner:${person.properties.userName}">
   <#list companyhome.childrenByLuceneSearch[query] as child>
      <#if child.isDocument>
                   <!-- lb: start repeat -->
                   <tr>
                       <td>
                       <a href="#"><img src="/alfresco/images/taskpane/document.gif" border="0" alt="Open ${child.name}" /></a>
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <a href="#" title="Open ${child.name}">${child.name}</a><br/>
<#if child.properties.description?exists>
		${child.properties.description}<br/>
</#if>
                Modified: ${child.properties.modified?datetime}, Size: ${child.size / 1024} Kb<br/>
                       <a href="#"><a href="#"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding:3px 6px 2px 0px;" alt="Move to..."></a><a href="#"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding:3px 6px 2px 0px;" alt="Copy to..."></a>
                </td>
            </tr>
            <!-- lb: end repeat -->
      </#if>
   </#list>

                 </tbody>
          </table>
</div>
<div id="mytodoListHeader"><span style="font-weight:bold;">My to-do list</span>
</div>

<div id="mytodoList">
          <table>
          <tbody>
            <!-- lb: start repeat -->
            <tr>
                <td>
                <a href="#"><img src="/alfresco/images/taskpane/task.gif" border="0" alt="Manage [task description]" /></a>
                </td>
                <td style="line-height:16px;" width="100%">
                <a href="#" title="Manage [task name]">[task description]</a><br/>
                Type:[type], ID:[id]<br/>
                Created:[Date], Due Date:[Size]<br/>
                </td>
            </tr>
            <!-- lb: end repeat -->
             <!-- lb: start repeat -->
            <tr>
                <td>
                <a href="#"><img src="/alfresco/images/taskpane/task.gif" border="0" alt="Manage [task description]" /></a>
                </td>
                <td style="line-height:16px;" width="100%">
                <a href="#" title="Manage [task name]">[task description]</a><br/>
                Type:[type], ID:[id]<br/>
                Created:[Date], Due Date:[Size]<br/>
                </td>
            </tr>
            <!-- lb: end repeat -->
             <!-- lb: start repeat -->
            <tr>
                <td>
                <a href="#"><img src="/alfresco/images/taskpane/task.gif" border="0" alt="Manage [task description]" /></a>
                </td>
                <td style="line-height:16px;" width="100%">
                <a href="#" title="Manage [task name]">[task description]</a><br/>
                Type:[type], ID:[id]<br/>
                Created:[Date], Due Date:[Size]<br/>
                </td>
            </tr>
            <!-- lb: end repeat -->
             <!-- lb: start repeat -->
            <tr>
                <td>
                <a href="#"><img src="/alfresco/images/taskpane/task.gif" border="0" alt="Manage [task description]" /></a>
                </td>
                <td style="line-height:16px;" width="100%">
                <a href="#" title="Manage [task name]">[task description]</a><br/>
                Type:[type], ID:[id]<br/>
                Created:[Date], Due Date:[Size]<br/>
                </td>
            </tr>
            <!-- lb: end repeat -->
             <!-- lb: start repeat -->
            <tr>
                <td>
                <a href="#"><img src="/alfresco/images/taskpane/task.gif" border="0" alt="Manage [task description]" /></a>
                </td>
                <td style="line-height:16px;" width="100%">
                <a href="#" title="Manage [task name]">[task description]</a><br/>
                Type:[type], ID:[id]<br/>
                Created:[Date], Due Date:[Size]<br/>
                </td>
            </tr>
            <!-- lb: end repeat -->
          </table>
</div>

<div id="documentActions">
<span style="font-weight:bold;">Document Actions</span><br/>
<ul>
    <li><a href="#"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding-right:6px;" alt="Save to Alfresco">Save to Alfresco</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/placeholder.gif" border="0" style="padding-right:6px;" alt="[action]">Action</a></li>
</ul>
</div>

<div id="bottomMargin">&nbsp;</div>


</body>
</html>