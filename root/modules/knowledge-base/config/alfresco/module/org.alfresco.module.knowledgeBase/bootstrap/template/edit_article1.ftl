<#macro print_field_select id name valuelist selectvalue multiple=false valueProp="" labelProp="">
<select name="${id}" id="${name}"<#if multiple == true> multiple="multiple"</#if>>
<@print_field_select_options valuelist, selectvalue, valueProp, labelProp />
</select>
</#macro>
<#macro print_field_select_options valuelist selectvalue valueProp="" labelProp="">
<#list valuelist as value>
	<option value="<#if value?is_hash && valueProp != "">${value[valueProp]}<#else>${value}</#if>"<#if (selectvalue?is_sequence && (value?is_hash && valueProp != "" && selectvalue?seq_contains(value[valueProp]) || selectvalue?seq_contains(value))) || (!selectvalue?is_sequence && (value?is_hash && valueProp && selectvalue[valueProp] == value || selectvalue == value))> selected="selected"</#if>><#if value?is_hash && labelProp != "">${value[labelProp]}<#else>${value}</#if></option>
</#list>
</#macro>
<html>
<title>Edit ASK Article</title>
<#assign articleStyleSheet = companyhome.childByNamePath["Data Dictionary/Knowledge Base/css/ask_article.css"] >
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
var debug = 0;

function checkform() {
	if (document.createarticle.category.value == "") {
		// 
		alert('You must select a Category');
		return false;
	}
	//if (document.createarticle.article_name.value == "") {
		// 
		//alert('You must enter an Article Name');
		//return false;
	//}
	if (document.createarticle.article_title.value == "") {
		// 
		alert('You must enter an Article Title');
		document.createarticle.article_title.focus();
		return false;
	}

  // Set the category_list hidden field, this is to overcome the limitation
  // that we can't pass multi-value fields to Alfresco
  if (debug == 1) { alert('in checkform'); }
  
  var categoryList = "";
	for (var i = 0; i < document.createarticle.category.options.length; i++) {
  	if (document.createarticle.category.options[i].selected) {
    	if (debug == 1) { alert(document.createarticle.category.options[i].value); }
    	if (debug == 1) { alert(i); }
    		if(categoryList != "") {
    			categoryList = categoryList + "|";
    		}
    	categoryList = categoryList  + document.createarticle.category.options[i].value;
    	}
	}
  //alert(document.createarticle.category.value);
  
  if (debug == 1) { alert("Full list: " + categoryList); }
//alert(document.createarticle.category_list);
  document.createarticle.category_list.value = categoryList;
  if (debug == 1) { alert("category_list value: " + document.createarticle.category_list.value); }
  
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
   
	// If the script makes it to here, everything is OK,
	// so you can submit the form

	return true;

}
</script>
<head>
</head>
<body>
<div id="container">
<h1>Edit Article</h1>
<#if document?exists>
<#if document.mimetype = "text/xml">
<#if document.xmlNodeModel?exists>
<#assign dom=document.xmlNodeModel>

<#assign fields = {}>

<#-- Get the article name -->
<#assign fields = fields + {"article_name":dom.article.article_name}>

<#-- Get the article type -->
<#if document.properties["ask:article_type"]?exists>
	<#assign fields = fields + {"article_type":document.properties["ask:article_type"]}>
<#else>
	<#assign fields = fields + {"article_type":dom.article.article_type}>
</#if>

<#if document.properties.categories?exists>
	<#assign categories = []>
	<#list document.properties.categories as cat>
		<#assign categories = categories + [cat]>
	</#list>
	<#assign fields = fields + {"category":categories}>
<#else>
	<#-- <#assign fields = fields + {"category":dom.article.category}> -->
	<#assign fields = fields + {"category":[]}>
</#if>

<#-- Get the article version -->
<#if document.properties["cm:alfresco_version"]?exists>
	<#assign versions = []>
	<#list document.properties["cm:alfresco_version"] as version>
		<#assign versions = versions + [version]>
	</#list>
	<#assign fields = fields + {"alfresco_version":versions}>
<#else>
	<#-- <#assign fields = fields + {"alfresco_version":dom.article.alfresco_version}> -->
	<#assign fields = fields + {"alfresco_version":[]}>
</#if>

<#-- Get the article tags -->
<#if document.properties["ask:tags"]?exists>
	<#assign categories_str = "">
	<#list document.properties["ask:tags"] as tag>
		<#if categories_str != ""><#assign categories_str = categories_str + ","></#if>
		<#assign categories_str = categories_str + tag>
	</#list>
	<#assign fields = fields + {"article_tags":categories_str}>
<#else>
	<#assign fields = fields + {"article_tags":dom.article.article_tags}>
</#if>

<#-- Get the article title -->
<#if document.properties.title?exists>
	<#assign fields = fields + {"article_title":document.properties.title}>
<#else>
	<#assign fields = fields + {"article_title":dom.article.article_title}>
</#if>

<#-- Get the article body -->
<#if dom.article.editor?exists>
	<#assign fields = fields + {"editor":dom.article.editor}>
<#else>
	<#assign fields = fields + {"editor":dom.article.article_body}>
</#if>

<#-- Get the article status -->
<#if document.properties["ask:status"]?exists>
	<#assign fields = fields + {"status":document.properties["ask:status"]}>
<#else>
	<#assign fields = fields + {"status":dom.article.status}>
</#if>

<#-- Get the article visibility -->
<#if document.properties["ask:visibility"]?exists>
	<#assign fields = fields + {"visibility":document.properties["ask:visibility"]}>
<#else>
	<#assign fields = fields + {"visibility":dom.article.visibility}>
</#if>

<#-- Get the article categories 
<#if document.properties.categories?exists>
	<#assign categories = []>
	<#list document.properties.categories as cat>
		<#assign articleCategory="${'${cat}'?replace(':','\\\\:')}">
		<#assign articleCategory="${'${articleCategory}'?replace('/','\\\\/')}">
		<#assign articleCategoryQuery="ID:" + "${articleCategory}">
		<#assign articleCategoryNode = companyhome.childrenByLuceneSearch["${articleCategoryQuery}"][0]>
		<#assign categories = categories + [articleCategoryNode.nodeRef]>
	</#list>
	<#assign fields = fields + {"category":categories}>
<#else>
	<#assign fields = fields + {"category":[]}>
</#if>
-->

<#-- Get the full category list -->
<#--
<#assign all_categories=[]>
<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""] as child>            
  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
    <#if rootnode.nodeRef = child.nodeRef>
    	<#assign all_categories = all_categories + rootnode.subCategories>
    </#if>
  </#list>
</#list>
-->

<#-- Get the full category list -->

<#assign all_categories=[]>
<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""] as child>            
  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
    <#if rootnode.nodeRef = child.nodeRef>
    	<#assign all_categories = all_categories + rootnode.subCategories>
    </#if>
  </#list>
</#list>

<#assign categories = []>
<#assign categories = all_categories>
<#list all_categories?sort_by("name")?reverse as cat>
	<#assign categories = categories + [cat.name]>
</#list>


<#-- Get the full version list -->
<#assign all_alfresco_versions=[]>
<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Versions\""] as child>            
  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
    <#if rootnode.nodeRef = child.nodeRef>
    	<#assign all_alfresco_versions = all_alfresco_versions + rootnode.subCategories>
    </#if>
  </#list>
</#list>

<#assign alfresco_version_list=[]>
<#assign alfresco_version_list = alfresco_version_list + ["Any"]>
<#list all_alfresco_versions?sort_by("name")?reverse as version>
	<#assign alfresco_version_list = alfresco_version_list + [version.name]>
</#list>

<form name="createarticle" class="awesome" onsubmit="return checkform();" 
 method="post" action="/alfresco/command/script/execute?scriptPath=/Company%20Home/Knowledge%20Base/create_update_article.js&contextPath=${document.displayPath}/${document.name}">

<input type="hidden" name="category_list" id="category_list" />
<input type="hidden" name="version_list" id="version_list" />
<input type="hidden" name="document_id" id="document_id" value="" />

<label for="article_title"><span>*</span>Article Title</label>
<input type="text" name="article_title" id="article_title" value="${fields.article_title}" /><br />
<p class="desc">Enter the article title here</p>

<table border="0" valign="top" class="awesome">

<#--
<tr>
	<td><label for="article_name"><span>*</span>Article Name</label></td>
	<td><input type="text" name="article_name" id="article_name" value="${fields.article_name}" /></td>
</tr>
-->

<tr><td>&nbsp;</td><td></td></tr>
<tr>
	<td class="awesome"><label for="article_type"><span>*</span>Article Type</label>
	<@print_field_select "article_type", "article_type", ["Article", "FAQ", "White Paper"], fields.article_type, false /></td>
</tr>

<tr><td>&nbsp;</td><td></td></tr>

<tr>
	<td ><label for="alfresco_version"><span>*</span>Alfresco Version(s)</label>
	<@print_field_select "alfresco_version", "alfresco_version", alfresco_version_list, fields.alfresco_version, true />
		<br/>
	<p for="alfresco_version" class="desc">Select 1 or more versions. Use CTRL to (de)select multiple</p>
	</td>
</tr>
<tr><td>&nbsp;</td><td></td></tr>
<tr>
<td><label for="category"><span>*</span>Categories</label>

<@print_field_select "category", "category", all_categories, fields.category, true, "nodeRef", "name" />
<br/>
<p for="category" class="desc">Select 1 or more categories. Use SHIFT to (de)select multiple</p></td>
</tr>
<tr><td>&nbsp;</td><td></td></tr>
<tr>
	<td><label for="article_tags"><span>*</span>Tag(s)</label>
	<input type="text" name="article_tags" id="article_tags" value="${fields.article_tags}" /><br />
	<p class="desc">Enter 1 or more tags here, separate multiple entries with commas e.g. tag1, tag2, tag3</p></td>
</tr>

</table>
	<br/>

	<label for="editor">Article Body</label>
	<div id='editor' name='editor' style='height:360px'>${fields.editor}</div>

<table border="0" valign="top" class="awesome">
<tr>
	<td><label for="status">Status</label>
	<@print_field_select "status", "status", ["Draft", "Pending Approval", "Current", "Archived"], fields.status, false /></td>
</tr>
<tr><td>&nbsp;</td><td></td></tr>
<tr>
	<td><label for="visibility">Visibility</label>
	<@print_field_select "visibility", "visibility", ["Internal", "Partners", "Partners and Customers"], fields.visibility, false />
	<br/><p for="status" class="desc">Defines who can view the article</p>
	</td>
</tr>

</table>

<table>
	<tr>
		<td><input type="submit" style="width:150px;" value="Save"/></td>
		<td>
		<input type="button" style="width:150px;" value="Close" onclick="javascript:window.close();"/>
		</td>
	</tr>
</table>
</form>


 <#else>
 	<p>ERROR: Article content could not be parsed.</p>
 </#if>
 <#else>
 	<p>ERROR: Article content is not XML.</p>
 </#if>
 </#if>
</div>
</body>
</html>