function getStatusValue(properties)
{
   var statusValue = null;
   var propsString = properties.toString();
   var searchString = "{http://www.alfresco.org/model/knowledgebase/1.0}status=";

   var index = propsString.indexOf(searchString);
   if (index != -1)
   {
      var endIndex = propsString.indexOf(",", index);
      if (endIndex == -1)
      {
         endIndex = propsString.indexOf("}", index);
      }
      statusValue = propsString.substring(index + searchString.length, endIndex);
   }
   
   return statusValue;
}

// Get the article
var node = behaviour.args[0];
var before = getStatusValue(behaviour.args[1]);
var after = node.properties[kb.PROP_STATUS].nodeRef.toString();

if (before != after && after == "workspace://SpacesStore/kb:status-published")
{
   // Update the SWF rendition
   logger.log("Generate the rendition");
   kb.updatePublishedArticle(node);
}

