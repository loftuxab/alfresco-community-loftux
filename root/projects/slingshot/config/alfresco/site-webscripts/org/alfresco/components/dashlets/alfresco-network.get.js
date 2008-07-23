var conf = new XML(config.script);

// configuration data
var includeNews = conf.notifications.news;
var includeDownloads = conf.notifications.downloads;

var url = "http://network.alfresco.com/extranet/s/c/service.networknews-dashlet1";
model.url = url;
