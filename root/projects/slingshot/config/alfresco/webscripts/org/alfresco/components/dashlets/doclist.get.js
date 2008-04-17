//var json = remote.call("/docs/recent/someuser");
var json = '[{"name":"SPK-Requirements.odt", "icon":"/images/filetypes32/_default.gif"},{"name":"Jeff Potts use case.doc", "icon":"/images/filetypes32/_default.gif"},{"name":"Dojo manual.txt", "icon":"/images/filetypes32/_default.gif"},{"name":"New document.txt", "icon":"/images/filetypes32/_default.gif"}]';
var docs = eval('(' + json + ')');
model.docs = docs;
