// retrieve the RM custom properties - for display as meta-data fields etc.
var conn = remote.connect("alfresco");
var res = conn.get("/api/classes/rmc_customProperties/properties");
model.meta = eval('(' + res + ')');