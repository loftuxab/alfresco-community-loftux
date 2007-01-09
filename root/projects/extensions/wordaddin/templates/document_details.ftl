<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   </#if>
</#list>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Document Details</title>

<link rel="stylesheet" type="text/css"
href="/alfresco/css/taskpane.css" />

<!-- lb: start of local styles -->
<style type="text/css">



</style>
<!-- lb: end of local styles -->

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
					var detailsListHeaderElement = document.getElementById('detailsListheader');
					var detailsListElement = document.getElementById('detailsList');
					var tabBarElement = document.getElementById('tabBar');
                                        var bottomMarginElement = document.getElementById('bottomMargin');
                                        var documentActionsElement = document.getElementById('documentActions');

					var detailsListHeight = detailsListElement.offsetHeight;
					var detailsListHeaderHeight = detailsListHeaderElement.offsetHeight;
					var tabBarHeight = tabBarElement.offsetHeight;
					var bottomMarginHeight = bottomMarginElement.offsetHeight;
                                        var documentActionsHeight = documentActionsElement.offsetHeight;

					if (windowHeight > 0) {
						detailsListElement.style.height = ((windowHeight- (tabBarHeight + detailsListHeaderHeight + documentActionsHeight + bottomMarginHeight)) /3) * 2 + 'px';
						documentActionsElement.style.height = (windowHeight- (tabBarHeight + detailsListHeaderHeight + documentActionsHeight + bottomMarginHeight)) /3 + 'px';
					}

				}
			}
		}
		window.onload = function() {
			setContent();
		}
		window.onresize = function() {
			setContent();
		}
		</script>


</head>
<body>

<div id="tabBar">
    <ul>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_home}"><img src="/alfresco/images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_browse}"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li style="padding-right:6px;"><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_search}"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li id="current"><a href="#"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li><a href="#"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
    </ul>
  </div>

<div id="detailsListHeader"><span style="font-weight:bold">Details</span></div>

<div id="detailsList">
          <table>
                 <tbody>

                   <tr>
                       <td valign="top">
<#if document.isDocument && document != template>
                      <img src="/alfresco${document.icon32}" border="0" alt="${document.name}" />
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <span style="font-weight:bold;">${document.name}</span><br/>
 <#if document.properties.description?exists>
		      ${document.properties.description}<br/>
<#else>
                      Description:<br/>
</#if>
                       Name: ${document.name}<br/>
<#if document.properties.title?exists>
		      Title: ${document.properties.title}<br/>
<#else>
                      Title:<br/>
</#if>
<#if document.properties.description?exists>
		      Description: ${document.properties.description}<br/>
<#else>
                      Description:<br/>
</#if>
                       Creator: [name]<br/>
                       Created: [date]<br/>
                       Modifier: [name]<br/>
                       Modified:[Date], Size:[Size]<br/>
                       Version: 1.0 (Current)<br/>
                       Categories: [category], [category]<br/>
<#else>
                       The current document is not managed by Alfresco.
</#if>  
                       </td>
                       </tr>

                 </tbody>
          </table>
</div>

<div id="documentActions">
<span style="font-weight:bold;">Document Actions</span><br/>
<#if document.isDocument && document != template>
<ul>
    <li><a href="#"><img src="/alfresco/images/taskpane/checkout.gif" border="0" style="padding-right:6px;" alt="Check Out">Check Out</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/update.gif" border="0" style="padding-right:6px;" alt="Update Alfresco Copy">Update Alfresco Copy</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/edit_properties.gif" border="0" style="padding-right:6px;" alt="Edit Properties">Edit Properties</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/start_workflow.gif" border="0" style="padding-right:6px;" alt="Start Workflow">Start Workflow</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/transform_to_pdf.gif" border="0" style="padding-right:6px;" alt="Transform to PDF">Transform to PDF</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/run_script.gif" border="0" style="padding-right:6px;" alt="Run a script">Run a Script</a></li>
    <li><a href="#"><img src="/alfresco/images/taskpane/add_aspect.gif" border="0" style="padding-right:6px;" alt="Add aspect">Add Aspect</a></li>
</ul>
</div>

<#else>
                       No actions available.
</#if>

<div id="bottomMargin">&nbsp;</div>


</body>
</html>