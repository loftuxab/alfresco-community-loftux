<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">



var userSites = getUserSites();
model.sites = userSites.sites;
model.favSites = userSites.favSites;
model.allSites = getAllSites();
model.backButton = true