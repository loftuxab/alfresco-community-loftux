// the 'columns' object is supplied in the model from our custom template config
var colcount = parseInt(columns);
// a naff example of generating some freemarker friendly objects to represent enabled columns
model.columns = new Array(colcount);
for (var i=0; i<colcount; i++)
{
   model.columns[i] = new Object();
   model.columns[i].enabled = true;
}