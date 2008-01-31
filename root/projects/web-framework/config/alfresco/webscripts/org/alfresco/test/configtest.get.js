// test global config
logger.log("testing global config...");
var foo = config.global.foo;
if (foo != null)
{
   logger.log("foo element is present");
   logger.log("foo name = " + foo.attributes["name"]);
   logger.log("foo title = " + foo.attributes["title"]);
   var children = foo.children;
   var childCount = children.size();
   logger.log("foo has " + childCount + " children:");
   for (var i = 0; i < childCount; i++)
   {
      logger.log("bar with id: " + children.get(i).attributes["id"]);
   }
   logger.log("value of bar3Id = " + children.get(2).value);
   
   // TODO: show example of getting children via a map
}

// test scoped config
logger.log("testing scoped config...");
var scopedCfg = config.scoped["NotModelAwareTest"];
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


// TODO: test getting config via ModelAware implementation


// TODO: test component config

















