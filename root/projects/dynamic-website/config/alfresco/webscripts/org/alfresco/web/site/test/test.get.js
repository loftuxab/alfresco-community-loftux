<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

// test url
// http://localhost:8080/alfresco/service/ads/test/ads--admin/www/avm_webapps/ROOT/data/site/site-configuration.xml
// avm node -> avmNode
// avm store -> avmStore

/*
var siteConfiguration = site.getSiteConfiguration();

var newComponent = site.newComponent();

newComponent.setProperty("name", "TEST NAME");
var p1 = newComponent.getProperty("name");

newComponent.setSetting("testSetting", "TEST VALUE");
var s1 = newComponent.getSetting("testSetting");

newComponent.save();
*/




// create objects

var c1 = site.newComponent();
c1.setProperty("componentTypeId", "ct-test");
c1.save();

var l1 = site.newLayout();
l1.setProperty("layoutTypeId", "lt-test");
l1.save();

var t1 = site.newTemplate();
t1.setProperty("layoutId", l1.getProperty("id"));
t1.save();

var ca1 = site.newComponentAssociation();
ca1.setProperty("scope", "template");
ca1.setProperty("sourceId", t1.getProperty("id"));
ca1.setProperty("regionId", "header");
ca1.setProperty("componentId", c1.getProperty("id"));
ca1.save();


// verify: 1
var as1 = site.findComponentAssociations(null, null, t1.getProperty("id"), null);
logger.log("ASSOCIATION SIZE: " + as1.length);


// do an unassociate
site.unassociateComponent(c1.getProperty("id"), "template", t1.getProperty("id"), "header" );

// verify: 0
var as2 = site.findComponentAssociations(null, null, t1.getProperty("id"), null);
logger.log("ASSOCIATION SIZE: " + as2.length);


// clean up
c1.remove();
l1.remove();
t1.remove();
ca1.remove();


