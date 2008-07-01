<import resource="/org/alfresco/web/site/include/utils.js">

default xml namespace = "http://www.alfresco.org/adw/1.0";


/*****************************************************
 * ADS OBJECT HELPERS - WRAP THE JAVASCRIPT EXTENSIONS
 *****************************************************/

function save(modelObject)
{
	if(modelObject != null)
	{
		modelObject.save();
	}
}

function remove(modelObject)
{
	if(modelObject != null)
	{
		modelObject.remove();
	}
}

function removeObjects(array)
{
	if(array != null)
	{
		for(var i = 0; i < array.length; i++)
		{
			remove(array[i]);
		}
	}
}

function assertSiteConfiguration(websiteName, websiteDescription)
{
	// create a new root page
	var rootPage = sitedata.newPage();
	rootPage.setTitle("Home");
	rootPage.setProperty("description", "Home Page for '" + websiteName + "'");
	rootPage.save();
	
	// create a new site configuration
	var siteConfiguration = sitedata.newConfiguration("site");
	siteConfiguration.setTitle(websiteName);
	siteConfiguration.setDescription(websiteDescription);	
	siteConfiguration.setProperty("root-page", rootPage.getId());
	siteConfiguration.setId("dynamicwebsite.site.configuration");	
	siteConfiguration.save();	
}


function assertPage(pageId, pageName, pageDescription)
{
	var page = sitedata.getPage(pageId);
	if(page != null)
	{
		page.setTitle(pageName);
		page.setDescription(pageDescription);
		page.save();
	}
}


function addChildPage(parentPageId, pageName, pageDescription)
{
	var childPage = sitedata.newPage();
	childPage.setTitle(pageName);
	childPage.setDescription(pageDescription);
	childPage.save();
	
	// associate to parent with child relationship
	if(parentPageId != null)
		sitedata.associatePage(parentPageId, childPage.getId());
		
	return childPage;
}


function removeChildPage(parentPageId, childPageId, recurse)
{
	sitedata.unassociatePage(parentPageId, childPageId);
}

function newPage(name, parentPage)
{
	var page = sitedata.newPage();
	page.setTitle(name);
	page.save();	
	if(parentPage != null)
		sitedata.associatePage(parentPage.getId(), page.getId());		
	return page;
}


function newTemplate(name, templateTypeId)
{
	var template = null;
	var templateType = sitedata.getTemplateType(templateTypeId);
	if(templateType != null)
	{
		var template = sitedata.newTemplate();
		template.setTitle(name);
		template.setProperty("template-type", templateTypeId);		
		template.save();
	}
	return template;
}

function newFreemarkerTemplate(name, uri)
{
	var template = sitedata.newTemplate();
	template.setTitle(name);
	template.setProperty("template-type", "freemarker");
	template.setProperty("uri", uri);
	save(template);
	
	return template;
}

function associateTemplate(page, template, formatId)
{
	sitedata.associateTemplate(template.getId(), page.getId());
}

function associateContent(contentId, pageId, formatId)
{
	sitedata.associateContent(contentId, pageId, formatId);
}

function associateContentType(contentTypeId, pageId, formatId)
{
	sitedata.associateContentType(contentTypeId, pageId, formatId);
}

function newComponent(name, componentTypeId)
{
	var c = sitedata.newComponent();
	c.setTitle(name);
	c.setProperty("component-type-id", componentTypeId);
	c.save();
	return c;
}

function associateSiteComponent(component, regionId)
{
	associateGlobalComponent(component, regionId);
}

function associateGlobalComponent(component, regionId)
{
	sitedata.bindComponent(component.getId(), "global", regionId, "global");
}

function associateTemplateComponent(component, template, regionId)
{
	sitedata.bindComponent(component.getId(), "template", regionId, template.getId());
}

function associatePageComponent(component, page, regionId)
{	
	sitedata.bindComponent(component.getId(), "page", regionId, page.getId());
}

function setConfig(o, propertyName, propertyValue)
{
	o.setProperty(propertyName, propertyValue);
}

function newImageComponent(name, imageUrl)
{
	var c = newComponent(name, "ct-imageComponent");
	setConfig(c, "imageLocation", imageUrl);
	save(c);
	return c;
}

function newNavComponent(name, orientation, style)
{
	var c = newComponent(name, "ct-navComponent");
	if(orientation == null)
		orientation = "horizontal";
	setConfig(c, "orientation", orientation);
	if(style == null)
		style = "0";
	setConfig(c, "style", style);
	save(c);
	return c;
}

function newItemComponent(name, itemType, itemPath, howToRender, renderData, endpointId)
{
	var c = newComponent(name, "ct-itemComponent");
	
	if(itemType == null)
		itemType = "specific";
	if(itemPath == null)
		itemPath = "";
	if(howToRender == null)
		howToRender = "templateTitle";
	if(renderData == null)
		renderData = "article-list";
	if(endpointId == null)
		endpointId = "alfresco-webuser";
		
	c.setProperty("itemType", itemType);
	c.setProperty("itemPath", itemPath);
	c.setProperty("howToRender", howToRender);
	c.setProperty("renderData", renderData);
	c.setProperty("endpoint-id", endpointId);
	save(c);

	return c;
}


function newWebScriptComponent(name, uri)
{
	var c = newComponent(name, "webscript");		
	c.setProperty("uri", uri);
	save(c);

	return c;
}









/*****************************************************
 * CONTENT FORMS AND WEB PROJECTS
 *****************************************************/

function getStagingStoreId(storeId)
{
	if(storeId == null)
		return null;
		
	var i = storeId.indexOf("--");
	if(i == -1)
		return storeId;

	storeId = storeId.substring(0,i);		
	return storeId;	
}

function getWebProjectFolder()
{
	var storeId = avmStore.name;	
	return getWebProjectFolderForStore(storeId);
}

function getWebProjectFolderForStore(storeId)
{
	storeId = getStagingStoreId(storeId);
	
	var wcmFolder = companyhome.childByNamePath("Web Projects");
	if(wcmFolder != null)
	{
		for(var i = 0; i < wcmFolder.children.length; i++)
		{
			var webProjectFolder = wcmFolder.children[i];
			if(webProjectFolder != null)
			{
				var webProjectStoreId = webProjectFolder.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}avmstore"];
				if(webProjectStoreId != null && webProjectStoreId == storeId)
				{
					return webProjectFolder;
				}
			}
		}
	}
	return null;
}

function getContentFormNames()
{
	var formNames = new Array();
	var webProjectFolder = getWebProjectFolder();
	if(webProjectFolder != null)
	{
		for(var i = 0; i < webProjectFolder.children.length; i++)
		{
			var child = webProjectFolder.children[i];
			if(child.type == "{http://www.alfresco.org/model/wcmappmodel/1.0}webform")
			{
				var formName = child.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}formname"];
				formNames[formNames.length] = formName;
			}
		}
	}
	return formNames;
}

function getSystemContentFormNames()
{
	var formNames = new Array();
	formNames[formNames.length] = "component";
	formNames[formNames.length] = "component-type";
	formNames[formNames.length] = "configuration";	
	formNames[formNames.length] = "content-association";		
	formNames[formNames.length] = "endpoint";
	formNames[formNames.length] = "page";
	formNames[formNames.length] = "page-association";
	formNames[formNames.length] = "template";
	formNames[formNames.length] = "template-type";
	return formNames;
}

function getNonSystemContentFormNames()
{
	var newFormNames = new Array();
	var illegalNames = getSystemContentFormNames();
	
	// TODO: This is a pretty bad way to do this
	
	var formNames = getContentFormNames();	
	for(var i = 0; i < formNames.length; i++)
	{
		var formName = formNames[i];
		
		var okay = true;
		for(var j = 0; j < illegalNames.length; j++)
		{
			var illegalName = illegalNames[j];
			if(illegalName == formName)
				okay = false;
		}
		if(okay)
		{
			newFormNames[newFormNames.length] = formName;
		}
	}
	return newFormNames;
}

function getContentForm(formName)
{
	var webProjectFolder = getWebProjectFolder();
	if(webProjectFolder != null)
	{
		for(var i = 0; i < webProjectFolder.children.length; i++)
		{
			var child = webProjectFolder.children[i];
			if(child.type == "{http://www.alfresco.org/model/wcmappmodel/1.0}webform")
			{
				var thisFormName = child.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}formname"];
				if(thisFormName == formName)
					return child;
			}
		}
	}
	return null;
}

function getContentFormTitle(formName)
{
	var form = getContentForm(formName);
	if(form != null)
		return form.properties["cm:title"];
	return null;
}

function getContentFormDescription(formName)
{
	var form = getContentForm(formName);
	if(form != null)
		return form.properties["cm:description"];
	return null;
}
