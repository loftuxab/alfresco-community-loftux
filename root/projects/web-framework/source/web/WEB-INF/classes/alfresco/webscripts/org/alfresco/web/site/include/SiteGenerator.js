<import resource="/org/alfresco/web/site/include/ads-support.js">

function clearSite()
{
	clearCurrentSite();
}

function clearCurrentSite()
{
	// note: we do not remove TemplateTypes or ComponentTypes
	// since those are reusable
	removeObjects(site.getComponents());
	removeObjects(site.getConfigurations());
	removeObjects(site.getContentAssociations());
	removeObjects(site.getEndpoints());
	removeObjects(site.getPages());
	removeObjects(site.getPageAssociations());
	removeObjects(site.getTemplates());
}

function generateSite(siteType)
{
	// no generation
	if("0" == siteType)	
	{
		return;
	}
	
	// basic public web site
	if("1" == siteType)
	{
		buildBasicPublicWebsite();
	}
	
	// green energy sample site
	if("2" == siteType)
	{
		buildGreenEnergy();
	}

	// project work site
	if("3" == siteType)
	{
		buildProjectWorksite();
	}
}

function buildBasicPublicWebsite()
{
	var rootPage = site.getRootPage();
	
	// create root navigation nodes
	var nd1 = newPage("Products", rootPage);
	var nd2 = newPage("Services", rootPage);
	var nd3 = newPage("Customers", rootPage);
	var nd4 = newPage("About Us", rootPage);
	var nd11 = newPage("Product A", nd1);
	var nd12 = newPage("Product B", nd1);

	// generate the templates
	var t1 = newTemplate("Home Template", "tt-basic-home-template");
	var t2 = newTemplate("Landing Template", "tt-basic-landing-template");
	var t3 = newTemplate("Content Template", "tt-basic-content-template");
	var t4 = newTemplate("Print Template", "tt-basic-print-template");

	// associate templates
	associateTemplate(rootPage, t1, "default");
	associateTemplate(nd1, t2, "default");
	associateTemplate(nd2, t2, "default");
	associateTemplate(nd3, t2, "default");
	associateTemplate(nd4, t2, "default");
	
	// associate content templates
	associateContentType("article", t3, "default");
	associateContentType("article", t4, "print");
	
	// set up site scoped components
	var c1 = newImageComponent("Header", "/build/basic/images/header.jpg");
	var c4 = newImageComponent("Footer", "/build/basic/images/footer.jpg");
	associateSiteComponent(c1, "header");
	associateSiteComponent(c4, "footer");
	
	// set up the home page	
	var c2 = newNavComponent("Top Navigation");
	var c3 = newImageComponent("Home: Blurb", "/build/basic/images/blurb.jpg");
	var c4 = newImageComponent("Home: Our Services", "/build/basic/images/our_services.jpg");
	var c5 = newImageComponent("Home: New Arrivals", "/build/basic/images/new_arrivals.jpg");
	var c6 = newImageComponent("Home: Gifts and Gadgets", "/build/basic/images/gifts_and_gadgets.jpg");
	var c7 = newImageComponent("Home: Latest News", "/build/basic/images/latest_news.jpg");
	associateTemplateComponent(c2, t1, "topNav");
	associatePageComponent(c3, t1, rootPage, "blurb");
	associatePageComponent(c4, t1, rootPage, "leftContent1");
	associatePageComponent(c5, t1, rootPage, "rightContent1");
	associatePageComponent(c6, t1, rootPage, "leftContent2");
	associatePageComponent(c7, t1, rootPage, "content");
	
	// set up the products page
	var c11 = newNavComponent("Horz Nav");
	var c12 = newNavComponent("Vert Nav", "vertical", "1");
	var c13 = newImageComponent("Popular Links", "/build/basic/images/popular_links.jpg");
	associateTemplateComponent(c11, t2, "topNav");
	associateTemplateComponent(c12, t2, "leftNav");
	associateTemplateComponent(c13, t2, "rightContent");
	
	// set up the about us page
	var c43 = newItemComponent("Article List", "specific", "/test/articles/sample1.xml", "templateTitle", "article-list", "alfresco-webuser");
	associatePageComponent(c43, t2, nd4, "content");
	
	// set up the content template (for item views)
	var c91 = newItemComponent("Item Viewer", "current", null, "templateTitle", "article-full", "alfresco-webuser");
	associateTemplateComponent(c91, t3, "contentitem");
	associateTemplateComponent(c11, t3, "topNav");
	associateTemplateComponent(c12, t3, "leftNav");	
	
	// set up the print template (for item views)
	associateTemplateComponent(c91, t4, "contentitem");
	
	// build the default endpoints
	var ep1 = newEndpoint("alfresco-webuser", "http", "localhost", "8080", "/alfresco/service", "specificuser", "basic", "admin", "admin");
	var ep2 = newEndpoint("alfresco", "http", "localhost", "8080", "/alfresco/service", "specificuser", "basic", "admin", "admin");
}

function buildGreenEnergy()
{
	// TODO
}

function buildProjectWorksite()
{
	// TODO
}