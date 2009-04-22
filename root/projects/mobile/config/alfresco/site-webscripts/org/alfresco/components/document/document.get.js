/*
{
   "totalRecords": 1,
   "startIndex": 0,
   "metadata":
   {
      "permissions":
      {
         "userRole": "SiteManager",
         "userAccess":
         {
            "create" : true,
            "edit" : true,
            "delete" : true
         }
      },
      "onlineEditing": false
   },
   "items":
   [
      {
         "index": 0,
         "nodeRef": "workspace://SpacesStore/b0468312-e317-4ad1-b406-96f0ad5855e4",
         "type": "document",
         "isLink": false,
         "mimetype": "image\/png",
         "icon32": "\/images\/filetypes32\/png.gif",
         "fileName": "grayButton.png",
         "displayName": "grayButton.png",
         "status": "",
         "lockedBy": "",
         "lockedByUser": "",
         "title": "grayButton.png",
         "description": "",
         "author": "",
         "createdOn": "17 Apr 2009 10:59:26 GMT+0100 (BST)",
         "createdBy": "Administrator",
         "createdByUser": "admin",
         "modifiedOn": "17 Apr 2009 10:59:26 GMT+0100 (BST)",
         "modifiedBy": "Administrator",
         "modifiedByUser": "admin",
         "size": "1361",
         "version": "1.0",
         "contentUrl": "api/node/content/workspace/SpacesStore/b0468312-e317-4ad1-b406-96f0ad5855e4/grayButton.png",
         "actionSet": "document",
         "tags": [],
         "activeWorkflows": "",
         "location":
         {
            "site": "mobile",
            "container": "documentLibrary",
            "path": "\/",
            "file": "grayButton.png"
         },
         "permissions":
         {
            "inherited": true,
            "roles":
            [
               "ALLOWED;GROUP_site_mobile_SiteConsumer;SiteConsumer",
               "ALLOWED;GROUP_EVERYONE;SiteConsumer",
               "ALLOWED;GROUP_site_mobile_SiteCollaborator;SiteCollaborator",
               "ALLOWED;GROUP_site_mobile_SiteManager;SiteManager",
               "ALLOWED;GROUP_site_mobile_SiteContributor;SiteContributor",
               "ALLOWED;GROUP_EVERYONE;ReadPermissions"
            ],
            "userAccess":
            {
               "create": true,
               "edit": true,
               "delete": true,
               "permissions": true
            }
         }
      }
   ]
}


*/
function getDocDetails(nodeRef) 
{
   var data = remote.call('/slingshot/doclib/doclist/documents/node/' + nodeRef.replace(":/", "") + '?filter=node');
   data = eval('(' + data + ')');
   var imgTypes = 'png,gif,jpg,jpeg,tiff,bmp';
   for (var i=0,len=data.items.length; i<len; i++)
   {
      var doc = data.items[i];
      doc.modifiedOn = new Date(doc.modifiedOn);
      doc.createdOn = new Date(doc.createdOn);
      
      var type = doc.mimetype.split('/')[1];
      if (imgTypes.indexOf(type)!=-1)
      {
         doc.type = 'img';
      }
      else if (type == 'pdf')
      {
         doc.type = 'pdf';
      }
      else if (type == 'msword')
      {
         doc.type = 'doc';
      }
      else if (type == 'msexcel')
      {
         doc.type = 'xls';
      }      
      else if (type == 'mspowerpoint')
      {
         doc.type = 'ppt';
      }
      else
      {
         doc.type = 'unknown';
      }
      
      doc.tags = doc.tags.join(' ');
      
      data.items[i]=doc;
   }
   return data;
}

model.doc = getDocDetails(page.url.args.nodeRef).items[0];
model.backButton = true;