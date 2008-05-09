var result = remote.call("/api/sites?size=5&pos=0");
var sites = eval('(' + result + ')');
model.sites = sites;
