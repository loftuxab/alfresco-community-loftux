<import resource="/include/support.js">

// incoming
var pageId = wizard.request("pageId");
var formatId = wizard.request("formatId");

wizard.setResponseTitle("Are you sure?");
wizard.setDialogHTML("Are you sure that you want to remove this association?");

