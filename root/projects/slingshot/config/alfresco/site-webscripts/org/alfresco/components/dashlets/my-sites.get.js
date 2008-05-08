var result = remote.call("/api/sites");
var sites = eval('(' + result + ')');
model.sites = sites;
