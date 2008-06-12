// 1 - ""
// 2 - "yui-g" "yui-gc" "yui-gd" "yui-ge" "yui-gf"
// 3 - "yui-gb"
// 4 - "yui-g"
// 5 - "yui-gb" // note, will leave an empty column to the right
// 6 - "yui-gb"

model.gridClass = template.properties.gridClass;
//[{components: 3}, {components: 3}, {components: 3}, {components: 3}];//parseInt(template.properties.column);
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
