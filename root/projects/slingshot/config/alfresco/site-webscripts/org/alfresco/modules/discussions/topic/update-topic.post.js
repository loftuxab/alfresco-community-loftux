<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/discussions/topic.lib.js">

function main()
{
   var site = "" + json.get("site");
   var container = "discussions";
   var path = "";
   var topicId = "" + json.get("topicId");
   var title = "" + json.get("title");
   var content =  "" + json.get("content");
   var htmlid = "" + json.get("htmlid");
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
   updateAndAssignTopic(site, container, path, topicId, title, content, tags);
    
   // set additional model data
   model.site = json.get("site");
   model.htmlid = htmlid;
}

main();
