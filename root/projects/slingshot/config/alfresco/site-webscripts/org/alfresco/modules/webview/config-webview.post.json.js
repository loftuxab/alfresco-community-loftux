var c = sitedata.getComponent(url.templateArgs.componentId);

var uri = String(json.get("url"));
var webviewTitle = String(json.get("webviewTitle"));
var height = String(json.get("height"));
c.properties["webviewTitle"] = webviewTitle;
model.webviewTitle = (webviewTitle == "") ? null : webviewTitle;

c.properties["height"] = height;
model.height = (height == "") ? null : height;

if (uri !== "")
{
   var re = /^(http|https):\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }

   c.properties["webviewURI"] = uri;

   model.uri = uri;
}

c.save();
