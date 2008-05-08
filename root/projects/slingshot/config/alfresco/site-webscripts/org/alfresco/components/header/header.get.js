//var json = remote.call("/site/someuser");
var json = '[{"name":"Engineering site", "nodeRef":"1"},{"name":"Marketing Site", "nodeRef":"2"},{"name":"Product Site", "nodeRef":"3"},{"name":"Support Site", "nodeRef":"4"},{"name":"Another Site", "nodeRef":"5"}]';
var sites = eval('(' + json + ')');
model.sites = sites;

var json = '[{"name":"Dave Smith", "nodeRef":"1"},{"name":"Jeff Potts", "nodeRef":"2"},{"name":"Kevin Roast", "nodeRef":"3"}]';
var persons = eval('(' + json + ')');
model.persons = persons;