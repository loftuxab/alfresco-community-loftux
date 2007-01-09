<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   </#if>
</#list>

<#if args.maxresults?exists>
    <#assign maxresults=args.maxresults?number>
<#else>
    <#assign maxresults=10>
</#if>
<#assign rescount=1>

<#if args.search1?exists><!-- If we have a search string display the search result -->
    <#assign searchString = "TEXT:" + args.search1 + "*">
    <#assign search1=args.search1>
<#else>
    <#assign searchString="">
    <#assign search1="">
</#if>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Search</title>

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
<body>



<div id="tabBar">
    <ul>
      <li><a href="/alfresco/template/workspace/SpacesStore/${office_home}/workspace/SpacesStore/${office_home}"><img src="/alfresco/images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${office_browse}/workspace/SpacesStore/${office_browse}"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li id="current" style="padding-right:6px;"><a href="#"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${office_details}/workspace/SpacesStore/${office_details}"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li><a href="#"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
      <li><a href="#"><img src="/alfresco/images/taskpane/workflow.gif" border="0" alt="View Workflow Info" /></a></li>
    </ul>
</div>

<div id="search">
<form id="myinputform" name="simpleSearch" method="get" action="/alfresco/template/workspace/SpacesStore/${template.id}/workspace/SpacesStore/${template.id}" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded">
Search for <input id="search1" type="text" maxlength='1024' style='width:140px;font-size:10px' value="${search1}"/><input id="simpleSearchButton" name="simpleSearchButton" type="submit" value="Search" onclick="" class="button"/><br/>
<br/>
Show me results for<br/>
<label><input type="radio" name="SearchFilter" checked="checked" value="all" />&#160;All Items</label><br/>
<label><input type="radio" name="SearchFilter" value="files_text" />&#160;File names and contents</label><br/>
<label><input type="radio" name="SearchFilter" value="files" />&#160;File names only</label><br/>
<label><input type="radio" name="SearchFilter" value="folders" />&#160;Space names only</label><br/>
<label><SELECT id="maxresults" NAME="maxresults" onchange="javascript:document.myinputform.submit();">
        <OPTION id="10" name="10">10</OPTION>
        <OPTION id="15" name="15">15</OPTION>
        <OPTION id="20" name="20">20</OPTION>
        <OPTION id="50" name="50">50</OPTION>
        <OPTION id="100" name="100">100</OPTION>
     </SELECT>&#160;results maximum</label><br/>
</form>
</div>

<div id="searchResultsListHeader"><span style="font-weight:bold">Items Found</span></div>

<div id="searchResultsList">
<#if args.search1?exists><!-- If we have a search string display the search result -->
<#assign search1 = args.search1>
<#assign searchString = "TEXT:" + search1 + "*">
<!-- <p>Search String: ${searchString}</p> -->
<!-- <p>${search1}</p> -->

          <table>
                 <tbody>
<#list companyhome.childrenByLuceneSearch[searchString] as child>
            <!-- lb: start repeat -->
                 <tr>
                     <td>
                    <a href="#"><img src="/alfresco${child.icon32}" border="0" alt="Open ${child.name}" /></a>
                     </td>
                     <td width="100%">
                     <a href="#" title="Open ${child.name}">${child.name}</a><br/>
<#if child.properties.description?exists>
		${child.properties.description}<br/>
</#if>
<#if child.isDocument>
                Modified: ${child.properties.modified?datetime}, Size: ${child.size / 1024} Kb<br/>
</#if>
                       </td>
                     </tr>
            </tr>
            <!-- lb: end repeat -->
<#if rescount = maxresults>
    <#break>
</#if>
<#assign rescount=rescount + 1>
</#list>
          </tbody>
          </table>
<#else> <!-- No Search term arguement -->
<p>No Search text given.</p>
</#if>
</div>

<div id="bottomMargin">&nbsp;</div>

</body>
</html>