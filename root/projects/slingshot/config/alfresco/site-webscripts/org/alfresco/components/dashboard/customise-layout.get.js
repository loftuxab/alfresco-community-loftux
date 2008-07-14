
function getNoOfColumns(template)
{
   var noOfColumns = 0;
   while(template.properties["gridColumn" + (noOfColumns + 1)] !== null)
   {
      noOfColumns++;
   }                                       
   return noOfColumns;
}

// Test data until proper service exists

// Get current template
var dashboardPage = "user/" + user.name + "/dashboard";
var currentTemplate = sitedata.findTemplate(dashboardPage);
var currentNoOfColumns = getNoOfColumns(currentTemplate);
var currentLayout = {templateId: currentTemplate.id, noOfColumns: currentNoOfColumns, description: currentTemplate.description};

var layouts = [
   {templateId: "dashboard-1-column",             noOfColumns: 1, description: "<b>One</b> column"},
   {templateId: "dashboard-2-columns-wide-right", noOfColumns: 2, description: "<b>Two</b> columns: narrow left, wide right"},
   {templateId: "dashboard-2-columns-wide-left",  noOfColumns: 2, description: "<b>Two</b> columns: wide left, narrow right"},
   {templateId: "dashboard-3-columns",            noOfColumns: 3, description: "<b>Three</b> column: narrow left, wide centre, narrow right"},
   {templateId: "dashboard-4-columns",            noOfColumns: 4, description: "<b>Four</b> columns"}
];

// Prepeare model for template
model.currentLayout = currentLayout;
model.layouts = layouts;

