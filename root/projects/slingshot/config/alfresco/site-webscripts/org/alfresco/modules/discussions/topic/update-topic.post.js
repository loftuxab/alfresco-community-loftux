<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">

function main()
{
   var site = "" + json.get("site");
   var container = "" + json.get("container");
   var path = "";
   var topicId = "" + json.get("topicId");
   var title = "" + json.get("title");
   var content =  "" + json.get("content");
   var htmlid = "" + json.get("htmlid");
   var browseTopicUrl = "" + json.get("browseTopicUrl");
   var tags = [];
   if (json.has("tags"))
   {
      var tagsJSONArray = json.get("tags");
      for (var x=0; x<tagsJSONArray.length(); x++)
      {
         tags.push(tagsJSONArray.get(x));
      }
   }
    
   // update topic and assign returned data
   updateAndAssignTopic(site, container, path, topicId, title, content, tags, browseTopicUrl);
    
   // set additional model data
   model.site = site;
   model.container = container;
   model.htmlid = htmlid;
}

main();
