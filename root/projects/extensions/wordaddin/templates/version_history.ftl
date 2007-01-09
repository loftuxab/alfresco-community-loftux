<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Version History</title>

<link rel="stylesheet" type="text/css"
href="css/taskpane.css" />

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
					var versionListHeaderElement = document.getElementById('versionListheader');
					var versionListElement = document.getElementById('versionList');
					var tabBarElement = document.getElementById('tabBar');
                                        var bottomMarginElement = document.getElementById('bottomMargin');
                                        var documentActionsElement = document.getElementById('documentActions');

					var versionListHeight = versionListElement.offsetHeight;
					var versionListHeaderHeight = versionListHeaderElement.offsetHeight;
					var tabBarHeight = tabBarElement.offsetHeight;
					var bottomMarginHeight = bottomMarginElement.offsetHeight;
                                        var documentActionsHeight = documentActionsElement.offsetHeight;

					if (windowHeight > 0) {
						versionListElement.style.height = (windowHeight- (tabBarHeight + versionListHeaderHeight + documentActionsHeight + bottomMarginHeight)) /2 + 'px';
						documentActionsElement.style.height = (windowHeight- (tabBarHeight + versionListHeaderHeight + documentActionsHeight + bottomMarginHeight)) /2 + 'px';
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
      <li><a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/my_alfresco.ftl&contextPath=/Company%20Home/Data%20Dictionary/Presentation%20Templates"><img src="images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li><a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/navigation.ftl&contextPath=/Company%20Home/Data%20Dictionary/Presentation%20Templates"><img src="images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li style="padding-right:6px;"><a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/search.ftl&contextPath=/Company%20Home/Data%20Dictionary/Presentation%20Templates"><img src="images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li><a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Presentation%20Templates/office/document_details.ftl&contextPath=/Company%20Home/Data%20Dictionary/Presentation%20Templates"><img src="images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li id="current"><a href="#"><img src="images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
      <li><a href="#"><img src="images/taskpane/workflow.gif" border="0" alt="View Workflow Info" /></a></li>
    </ul>
  </div>

<div id="versionListHeader"><span style="font-weight:bold">Version History for [name]</span></div>

<div id="versionList">
          <table>
                 <tbody>
                 <!-- lb: start repeat row -->
                   <tr>
                       <td valign="top">
                       <a href="#"><img src="images/taskpane/document.gif" border="0" alt="Open [doc name](version)"/></a>
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <a href="#" title="Open [doc name](version)"><span style="font-weight:bold;">Version 1.3 (Current)</span></a><br/>
                       Author: [name]<br/>
                       Date: [date]<br/>
                       Notes: [notes]<br/>
                       </td>
                   </tr>
                   <!-- lb: end repeat row -->
                 <!-- lb: start repeat row -->
                   <tr>
                       <td valign="top">
                       <a href="#"><img src="images/taskpane/document.gif" border="0" alt="Open [doc name](version)"/></a>
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <a href="#" title="Open [doc name](version)"><span style="font-weight:bold;">Version 1.2</span></a><br/>
                       Author: [name]<br/>
                       Date: [date]<br/>
                       Notes: [notes]<br/>
                       </td>
                   </tr>
                   <!-- lb: end repeat row -->
                   <!-- lb: start repeat row -->
                   <tr>
                       <td valign="top">
                       <a href="#"><img src="images/taskpane/document.gif" border="0" alt="Open [doc name](version)"/></a>
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <a href="#" title="Open [doc name](version)"><span style="font-weight:bold;">Version 1.1</span></a><br/>
                       Author: [name]<br/>
                       Date: [date]<br/>
                       Notes: [notes]<br/>
                       </td>
                   </tr>
                   <!-- lb: end repeat row -->
                   <!-- lb: start repeat row -->
                   <tr>
                       <td valign="top">
                       <a href="#"><img src="images/taskpane/document.gif" border="0" alt="Open [doc name](version)"/></a>
                       </td>
                       <td style="line-height:16px;" width="100%">
                       <a href="#" title="Open [doc name](version)"><span style="font-weight:bold;">Version 1.0</span></a><br/>
                       Author: [name]<br/>
                       Date: [date]<br/>
                       Notes: [notes]<br/>
                       </td>
                   </tr>
                   <!-- lb: end repeat row -->
                 </tbody>
          </table>
</div>

<div id="documentActions">
<span style="font-weight:bold;">Document Actions</span><br/>
<ul>
    <li><a href="#"><img src="images/taskpane/checkout.gif" border="0" style="padding-right:6px;" alt="Check out">Checkout</a></li>
    <li><a href="#"><img src="images/taskpane/update.gif" border="0" style="padding-right:6px;" alt="Update Alfresco Copy">Update Alfresco Copy</a></li>
    <li><a href="#"><img src="images/taskpane/edit_properties.gif" border="0" style="padding-right:6px;" alt="Edit Properties">Edit Properties</a></li>
    <li><a href="#"><img src="images/taskpane/start_workflow.gif" border="0" style="padding-right:6px;" alt="Start Workflow">Start Workflow</a></li>
    <li><a href="#"><img src="images/taskpane/transform_to_pdf.gif" border="0" style="padding-right:6px;" alt="Transform to PDF">Transform to PDF</a></li>
    <li><a href="#"><img src="images/taskpane/run_script.gif" border="0" style="padding-right:6px;" alt="Run a script">Run a Script</a></li>
    <li><a href="#"><img src="images/taskpane/add_aspect.gif" border="0" style="padding-right:6px;" alt="Add aspect">Add Aspect</a></li>
</ul>
</div>

<div id="bottomMargin">&nbsp;</div>


</body>
</html>