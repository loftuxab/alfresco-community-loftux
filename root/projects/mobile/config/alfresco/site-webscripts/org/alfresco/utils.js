//http://localhost:8080/alfresco/service/slingshot/doclib/doclist/{type}/site/{sites}/{container}/?
//where type=documents, site=mobile and container=documentLibrary. 
//path args = filter=all|path|editingMe|editingOthers|recentlyModified|node

/*
{
   "totalRecords": 13,
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
         "nodeRef": "workspace://SpacesStore/e8367807-1ace-4ed2-8ee9-99f7dc6b7823",
         "type": "document",
         "isLink": false,
         "mimetype": "image\/png",
         "icon32": "\/images\/filetypes32\/png.gif",
         "fileName": "iPhone Visual - DocLib.png",
         "displayName": "iPhone Visual - DocLib.png",
         "status": "",
         "lockedBy": "",
         "lockedByUser": "",
         "title": "iPhone Visual - DocLib.png",
         "description": "",
         "author": "",
         "createdOn": "09 Apr 2009 11:13:27 GMT+0100 (BST)",
         "createdBy": "Administrator",
         "createdByUser": "admin",
         "modifiedOn": "09 Apr 2009 11:13:27 GMT+0100 (BST)",
         "modifiedBy": "Administrator",
         "modifiedByUser": "admin",
         "size": "215013",
         "version": "1.0",
         "contentUrl": "api/node/content/workspace/SpacesStore/e8367807-1ace-4ed2-8ee9-99f7dc6b7823/iPhone%20Visual%20-%20DocLib.png",
         "actionSet": "document",
         "tags": [],
         "activeWorkflows": "",
         "location":
         {
            "site": "mobile",
            "container": "documentLibrary",
            "path": "\/",
            "file": "iPhone Visual - DocLib.png"
         },
         "permissions":
         {
            "inherited": true,
            "roles":
            [
               "ALLOWED;GROUP_site_mobile_SiteManager;SiteManager",
               "ALLOWED;GROUP_site_mobile_SiteContributor;SiteContributor",
               "ALLOWED;GROUP_EVERYONE;ReadPermissions",
               "ALLOWED;GROUP_site_mobile_SiteConsumer;SiteConsumer",
               "ALLOWED;GROUP_EVERYONE;SiteConsumer",
               "ALLOWED;GROUP_site_mobile_SiteCollaborator;SiteCollaborator"
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
// http://localhost:8080/alfresco/service/slingshot/doclib/doclist/{type}/site/{sites}/{container}/?
function getDocuments(site,container,filter,amount)
{
    var uri = '/slingshot/doclib/doclist/documents/site/'+site+'/'+container+'/?filter='+filter+'&size='+amount;
    var data  = remote.call(uri);
    var data = eval('('+ data+')');

    var imgTypes = 'png,gif,jpg,jpeg,tiff,bmp';
    for (var i=0,len=data.items.length;i<len;i++)
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
      else {
        doc.type = 'unknown';
      }
      //make valid dom id using docTitle - prob needs fixing for unicode characters
      doc.domId = doc.title.replace(/ /g,'').match(/^[a-zA-Z][a-zA-Z0-9\-\_]+/g)[0];
      data.items[i]=doc;
    }

    return data;
}

//http://localhost:8080/alfresco/service/slingshot/dashlets/my-tasks?filter={filter?}&date={date?}
function getUserTasks(filter)
{
  var filter = filter || 'all';
  var data = remote.call("/slingshot/dashlets/my-tasks?filter=" + filter);
  data = eval('(' + data + ')');
  return data;
}

//http://localhost:8080/alfresco/service/calendar/events/user
function getUserEvents()
{
  var data = remote.call("/calendar/events/user");
  return eval('(' + data + ')');
}

const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

// http://localhost:8080/alfresco/service/api/sites
function getAllSites()
{
  var data  = remote.call("/api/sites");
  return eval('('+ data+')');
}
//http://localhost:8080/alfresco/service/api/people/admin/sites?size={pagesize?}&pos={position?}
function getUserSites(u)
{
   var userName = u || user.name;
   // Call the repo for sites the user is a member of
   var result = remote.call("/api/people/" + stringUtils.urlEncode(userName) + "/sites");
   if (result.status == 200)
   {
      var i, ii, j, jj;

      // Create javascript objects from the server response
      var sites = eval('(' + result + ')'), site, favourites = {}, userfavs = [];

      if (sites.length > 0)
      {
         // Call the repo for the user's favourite sites
         result = remote.call("/api/people/" + stringUtils.urlEncode(userName) + "/preferences?pf=" + PREF_FAVOURITE_SITES);
         if (result.status == 200 && result != "{}")
         {
            var prefs = eval('(' + result + ')');
            
            // Populate the favourites object literal for easy look-up later
            favourites = eval('(prefs.' + PREF_FAVOURITE_SITES + ')');
            if (typeof favourites != "object")
            {
               favourites = {};
            }
         }

         for (i = 0, ii = sites.length; i < ii; i++)
         {
            site = sites[i];
            
            // Is this site a user favourite?
            if (favourites[site.shortName]) 
            {
              site.isFavourite = true;
              userfavs.push(site);
            }
            site.isFavourite = !!(favourites[site.shortName]);
         }
         var userSites =
         {
            sites : sites,
            favSites: userfavs
         };
         return userSites;
      }
      return {
         sites:[],
         favSites:[]
      };
   }
};
