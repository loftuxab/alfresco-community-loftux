//http://localhost:8080/alfresco/service/slingshot/search?term=mobile&site=&container=&maxResults=101

// {
//  "items":
//  [
//    {
//      "index": 0,
//      "nodeRef": "workspace:\/\/SpacesStore\/4318a4bc-ab2c-47d3-a3e5-2709f3a604de",
//      "type": "document",
//      "name": "iPhone Wireframes - User Story XX - creating a wiki page outside a site.pdf",
//      "displayName": "iPhone Wireframes - User Story XX - creating a wiki page outside a site.pdf",
//      "description": "Wireframe of the iPhone Creating a wiki page from outside a story",
//      "modifiedOn": "09 Apr 2009 17:30:21 GMT+0100 (BST)",
//      "modifiedByUser": "admin",
//      "modifiedBy": "Administrator ",
//      "size": 215396,
//      "tags": ["iphone","wireframe"],
//      "browseUrl": "document-details?nodeRef=workspace:\/\/SpacesStore\/4318a4bc-ab2c-47d3-a3e5-2709f3a604de",
//      "site":
//      {
//        "shortName": "mobile",
//        "title": "Mobile"
//      },
//      "container": "documentLibrary"
//    },
//    ...

function getDocType(doc)
{
  var displayType = '';
  if (doc.type == 'document')
  {
    displayType = doc.name.match(/([^\/\\]+)\.(\w+)$/)[2] 
  }
  else
  {
    displayType = doc.type; 
  }
  return displayType;
}

function getContentSearchResults(term)
{
  var data  = remote.call("/slingshot/search?term="+stringUtils.urlEncode(term)+"&site=&container=&maxResults=26");
  data = eval('('+ data+')');
  for (var i=0,len=data.items.length;i<len;i++)
  {
    var doc = data.items[i];
    doc.modifiedOn = new Date(doc.modifiedOn);
    doc.displayType = getDocType(doc);
    doc.doclink =  "api/node/content/"+doc.nodeRef.replace(':/','')+'/'+stringUtils.urlEncode(doc.name);
    data.items[i]=doc;
  } 
  //work out if there we need pagination 
  if (data.items.length===26)
  {
    data.hasMore = true;
    //remove last
    data.items.pop();
  }
  return data;
}

function getSiteResults(term)
{
  var data =remote.call("/api/sites?size=25&nf=" + stringUtils.urlEncode(term));
  return eval('('+ data+')');
}

function getPeopleResults(term)
{
  var data = remote.call("/api/people?filter="+ stringUtils.urlEncode(term) +"&maxResults=26")
  return eval('('+ data+')');  
}

var query = page.url.args.term;
model.contentResults = getContentSearchResults(query);
model.siteResults = getSiteResults(query);
model.pplResults = getPeopleResults(query);

model.backButton = true;
//debug stuff
model.data = model.pplResults.toSource();