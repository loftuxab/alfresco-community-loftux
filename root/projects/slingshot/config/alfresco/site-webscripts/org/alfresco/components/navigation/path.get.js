var nodeRef = page.url.args.nodeRef;

if (nodeRef !== null)
{
   var idx = nodeRef.indexOf("://");
   if (idx != -1)
   {
      // get node info
      var url = "/slingshot/doclib/doclist/documents/node/" + nodeRef.replace(":/", "") + "?filter=node";
      var json = remote.call(url);
      
      // create the model
      var doc = eval('(' + json + ')');
      
      // pass the location on to the template
      model.location = doc.items[0].location;
      
      // create an array of paths
      var path = doc.items[0].location.path;
      var folders = path.substring(1, path.length).split("/");
      model.folders = folders;
   }
}