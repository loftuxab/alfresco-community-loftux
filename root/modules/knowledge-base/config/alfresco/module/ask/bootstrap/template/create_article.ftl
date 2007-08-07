<html>
<head>

<title>Create ASK Article</title>

<#assign articleStyleSheet = space.childByNamePath["css/ask_article.css"] >
<link rel="stylesheet" type="text/css" href="/alfresco${articleStyleSheet.url}" />
	
	
<script language="javascript" type="text/javascript" src="/alfresco/scripts/tiny_mce/tiny_mce.js"></script>

<script language="javascript" type="text/javascript">

tinyMCE.init({
theme : "advanced",
mode : "exact",
elements : "editor",
plugins : "table",
theme_advanced_toolbar_location : "top",
theme_advanced_toolbar_align : "left",
theme_advanced_buttons1_add : "fontselect,fontsizeselect",
theme_advanced_buttons2_add : "separator,forecolor,backcolor",
theme_advanced_buttons3_add_before : "tablecontrols,separator",
theme_advanced_disable: "styleselect",
extended_valid_elements : "a[href|target|name],font[face|size|color|style],span[class|align|style]"
});

</script>

<script>

function checkform() {

	if (document.createarticle.category.value == "") { 
		alert('You must select a Category');
		return false;
	}
	//if (document.createarticle.article_name.value == "") {
	//	alert('You must enter an Article Name');
	//	return false;
	//}
	
	if (document.createarticle.article_title.value == "") {
		alert('You must enter an Article Title');
		return false;
	}

  // Set the categories_list hidden field, this is to overcome the fact
  // that we can't pass multi-value fields
  
  var categoryList = "";
	for (var i = 0; i < document.createarticle.category.options.length; i++) {
  	if (document.createarticle.category.options[i].selected) {
    		if(categoryList != "") {
    			categoryList = categoryList + "|";
    		}
    	categoryList = categoryList  + document.createarticle.category.options[i].value;
    	}
	}
  
  document.createarticle.category_list.value = categoryList;
   //alert(categoryList);
	// If the script makes it to here, everything is OK,
	// so we can submit the form
	
	// Set the version_list hidden field, this is to overcome the fact
  // that we can't pass multi-value fields
  
  var versionList = "";
	for (var i = 0; i < document.createarticle.alfresco_version.options.length; i++) {
  	if (document.createarticle.alfresco_version.options[i].selected) {
    		if(versionList != "") {
    			versionList = versionList + "|";
    		}
    	versionList = versionList  + document.createarticle.alfresco_version.options[i].value;
    	}
	}
	document.createarticle.version_list.value = versionList;

	return true;

}

</script>	
	
</head>
<body>
	<form name="createarticle" class="awesome" onsubmit="return checkform()" method="post" action="/alfresco/command/script/execute/?scriptPath=/Company%20Home/Knowledge%20Base/create_update_article.js/&contextPath=/Company%20Home/Knowledge%20Base">
	<input type="hidden" name="category_list" id="category_list" />
	<input type="hidden" name="version_list" id="version_list" />
	
		<h3>Create Article</h3>

		<label for="article_title"><span>*</span>Article Title</label>
		<input type="text" name="article_title" id="article_title" /><br />
		<p class="desc">Enter the article title here</p>
		
<table border="0" valign="top" class="awesome">
<tr>
<td class="awesome">
	<label for="article_type"><span>*</span>Article Type</label>
	<select name="article_type" id="article_type">
		<option>Article</option>
		<option>FAQ</option>
		<option>White Paper</option>
	</select>
</td>
<td>
<!--
	<label for="article_name"><span>*</span>Article Name</label>
	<input style="width:200px;" type="text" name="article_name" id="article_name" />
-->
<tr><td>&nbsp;</td><td></td></tr>
</td>
</tr>
<tr>
<td valign="top">
<label for="category"><span>*</span>Category(s)</label>
<select name="category" id="category" multiple="multiple">	
<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base/cm:Article_x0020_Category/*\""] as child>  
         <option value="${child.nodeRef}">${child.properties.name}</option>
    </#list>   
		<p for="category" class="desc">Select 1 or more categories. Use SHIFT to (de)select multiple</p>
</select>

</td>


<#-- Category based version selector -->
<td valign="top">
<label for="alfresco_version"><span>*</span>Alfresco Version(s)</label>
	<select name="alfresco_version" id="alfresco_version" multiple="multiple">
	<option value="Any">Any</option>
	<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base/cm:Alfresco_x0020_Versions/*\""] as child>                    
  
          <option value="${child.properties.name}">${child.properties.name}</option>                   
  </#list>
	</select>
</td>

</tr>
</table>
	<br/>
	<p for="category" class="desc">Use SHIFT to (de)select multiple categories and/or versions</p>

		<label for="article_tags"><span>*</span>Tag(s)</label>
		<input type="text" name="article_tags" id="article_tags" /><br />
		<p class="desc">Enter 1 or more tags here, separate multiple entries with commas e.g. tag1, tag2, tag3</p>
				
		<label for="article_body">Article Body</label>
		<!-- <textarea class="long" rows="20" cols="80" id="article_body" name="article_body"></textarea><br /><br /> -->
		
		<div id='editor' name='editor' style='height:360px'></div> 
		<!-- <input type="hidden" name="editorContent" id="editorContent"/> -->

<!--
		<label for="status">Status</label>
		<select name="status" id="status">
			<option>Draft</option>
			<option>Pending Approval</option>
			<option>Current</option>
			<option>Archived</option>
		</select><br />
		<p for="status" class="desc">Please select the articles Approval status</p>
-->

		<label for="visibility">Visibility</label>
		<select name="visibility" id="visibility">
			<option>Internal</option>
			<option>Partners</option>
			<option>Partners and Customers</option>
		</select><br />
		<p for="status" class="desc">Defines who can view the article</p>


  <br/>
		<table>
		<tr><td><input type="submit" style="width:150px;" value="Submit"/></td><td><input type="button" style="width:150px;" value="Close" onclick="javascript:window.close();"/>
		</td></tr>

	</form>


</body>
</html>