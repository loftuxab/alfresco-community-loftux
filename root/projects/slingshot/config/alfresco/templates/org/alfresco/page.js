
model.gridClass = template.properties.gridClass;
var columns = [];
for(var i = 0; true; i++)
{
   var noOfComponents = template.properties["gridColumn" + (i + 1)];
   if(noOfComponents)
   {
      columns[i] = {components: parseInt(noOfComponents)};
   }
   else{
      break;
   }
}
model.gridColumns = columns;
