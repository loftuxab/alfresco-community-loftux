// test global config
logger.log("testing global config...");
var foo = config.global.foo;
if (foo != null)
{
   logger.log("foo element is present");
   // get attributes
   logger.log("foo name = " + foo.attributes["name"]);
   logger.log("foo title = " + foo.attributes["title"]);
   
   // deal with children as list
   var children = foo.children;
   var childCount = children.size();
   logger.log("foo has " + childCount + " children:");
   for (var i = 0; i < childCount; i++)
   {
      logger.log("element with name: " + children.get(i).name);
   }
   
   logger.log("id of first child = " + children.get(0).attributes["id"]);
   logger.log("value of bar3Id = " + children.get(2).value);
   logger.log("value of baz = " + children.get(3).value);
   
   // deal with children as map
   var childMap = foo.childrenMap;
   var bars = childMap["bar"];
   logger.log("There are " + bars.size() + " bar elements");
   logger.log("value of single baz element is: " + childMap["baz"].get(0).value);
}

// test scoped config
logger.log("testing scoped config...");
var scopedCfg = config.scoped["ServerConfigElementTest"];
if (scopedCfg != null)
{
   var server = scopedCfg.server;
   if (server != null)
   {
      logger.log("server element is present");
      logger.log("scheme = " + server.scheme);
      logger.log("hostname = " + server.hostName);
      logger.log("port = " + server.port);
   }
}

// test retrieval of overridden config
logger.log("testing overridden global config...");
logger.log("param in global config (should be 'hello') = " + config.global["param"].value);
logger.log("param in scoped config (should be 'goodbye') = " + config.scoped["OverrideTest"].param.value);


// TODO: test component config

















