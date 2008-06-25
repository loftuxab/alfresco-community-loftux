// Test data until proper service exists

var dashlets = [
   {id: "1", name: "Doc Summary"}, {id: "2", name: "My Messages"}, {id: "3", name: "My Documents"},
   {id: "4", name: "Doc Filters"}, {id: "5", name: "My Activities"}, {id: "6", name: "My Adresses"},
   {id: "7", name: "Doc Recent"}, {id: "8", name: "My Filters"}, {id: "9", name: "My Phone numbers"},
   {id: "7", name: "Doc Recent"}, {id: "8", name: "My Filters"}, {id: "9", name: "My Phone numbers"},
   {id: "7", name: "Doc Recent"}, {id: "8", name: "My Filters"}, {id: "9", name: "My Phone numbers"},
   {id: "10", name: "Doc List"}, {id: "11", name: "My Calendars"}
];
model.dashlets = dashlets;

var columns = [];
columns[0] = [{id: "10", name: "My Profile 1"}, {id: "11", name: "My Sites 1"}, {id: "12", name: "My Calendar 1"}];
columns[1] = [{id: "13", name: "My Profile 2"}, {id: "14", name: "My Sites 2"}, {id: "15", name: "My Calendar 2"}];
columns[2] = [{id: "16", name: "My Profile 3"}, {id: "17", name: "My Sites 3"}, {id: "18", name: "My Calendar 3"}];
columns[3] = [{id: "17", name: "My Profile 4"}, {id: "18", name: "My Sites 4"}, {id: "19", name: "My Calendar 4"}];
model.columns = columns;
