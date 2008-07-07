// Test data until proper service exists

var currentLayout = {id: "dashboard-3-columns", noOfColumns: 3, description: "<b>Three</b> column: narrow left, wide centre, narrow right"};

var layouts = [
   {id: "dashboard-1-column", noOfColumns: 1, description: "<b>One</b> column"},
   {id: "dashboard-2-columns-wide-right", noOfColumns: 2, description: "<b>Two</b> columns: narrow left, wide right"},
   {id: "dashboard-2-columns-wide-left", noOfColumns: 2, description: "<b>Two</b> columns: wide left, narrow right"},
   {id: "dashboard-3-columns", noOfColumns: 3, description: "<b>Three</b> column: narrow left, wide centre, narrow right"},
   {id: "dashboard-4-columns", noOfColumns: 4, description: "<b>Four</b> columns"}
];

model.currentLayout = currentLayout;
model.layouts = layouts;

