var endpoint = "alfresco-wiki";
model.endpoint = endpoint;

var connector = remote.connect(model.endpoint);
var wiki = connector.call("/wiki/Web_Scripts");
model.wiki = wiki;
