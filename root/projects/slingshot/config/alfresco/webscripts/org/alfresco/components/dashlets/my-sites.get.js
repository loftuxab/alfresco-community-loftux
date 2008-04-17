//var json = remote.call("/site/someuser");
var json = '[{"name":"Engineering site", "icon":"/images/filetypes32/_default.gif"},{"name":"Marketing Site", "icon":"/images/filetypes32/_default.gif"},{"name":"Product Site", "icon":"/images/filetypes32/_default.gif"},{"name":"Support Site", "icon":"/images/filetypes32/_default.gif"},{"name":"Another Site", "icon":"/images/filetypes32/_default.gif"}]';
var sites = eval('(' + json + ')');
model.sites = sites;
