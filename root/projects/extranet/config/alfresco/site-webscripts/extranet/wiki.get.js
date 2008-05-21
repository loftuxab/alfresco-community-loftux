var endpoint = "alfresco-wiki";
model.endpoint = endpoint;

var connector = remote.connect("alfresco-wiki");
var wiki = connector.call("/wiki/Web_Scripts");
model.wiki = wiki;
