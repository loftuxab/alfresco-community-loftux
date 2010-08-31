<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

// Discover SharePoint (Vti) server port
var vtiServerJson = "{}";

result = remote.call("/api/vti/serverDetails");
if (result.status == 200 && result != "")
{
   vtiServerJson = result;
}

model.vtiServer = vtiServerJson;