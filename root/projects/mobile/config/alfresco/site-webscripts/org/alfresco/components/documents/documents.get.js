<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

model.recentDocs = getDocuments(page.url.args.site,'documentLibrary','recentlyModified',3).items;
model.allDocs = getDocuments(page.url.args.site,'documentLibrary','all',30).items;
model.myDocs = getDocuments(page.url.args.site,'documentLibrary','editingMe',3).items;
model.backButton = true;
