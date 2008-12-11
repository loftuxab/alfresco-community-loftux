model.trash = config.scoped["content"]["form"].modelOverrideProperties.get(0).name;

//var itemNames = config.global["constraint-handlers"].itemNames;
//for (var i = 0; i < itemNames.size; i++)
//{
//	nextKey = itemNames.get(i);
//	nextType = config.global["constraint-handlers"].items[nextKey].type;
//	nextVH = config.global["constraint-handlers"].items[nextKey].validationHandler;
//	nextM = config.global["constraint-handlers"].items[nextKey].message;
//	nextMId = config.global["constraint-handlers"].items[nextKey].messageId;
//}


//Constraint Handlers
model.constraints = [];

var constraintNames = config.global["constraint-handlers"].itemNames;
for (var x = 0; x < constraintNames.size(); x++)
{
	var constName = constraintNames.get(x);
	model.constraints.push(constName);
	model.constraints.push(config.global["constraint-handlers"].items[constName].message);
}

//Default Controls
model.defaultcontrols = [];

var controlNames = config.global["default-controls"].itemNames;
// TODO Move this to a hash-based data structure.
//model.defaultcontrols['names'] = controlNames;
for (var x = 0; x < controlNames.size(); x++)
{
	var constName = controlNames.get(x);
	model.defaultcontrols.push(constName);
	model.defaultcontrols.push(config.global["default-controls"].items[constName].template);
	model.defaultcontrols.push(config.global["default-controls"].items["abc"].controlParams.get(2).value);
//	model.defaultcontrols.push(config.global["default-controls"].items[constName].controlParams);
}





model.submitLabel = "OK";


var fakedJson = "{   \"foo\" : \"bar\", " +
" \"items\" : [ \"it1\", \"it2\", \"it3\", \"it4\" ] }";
var data = eval('(' + fakedJson + ')');
model.rubbish = data.foo;

model.fields = [];
for (var x=0; x < data.items.length; x++)
{
   model.fields.push(data.items[x]);
}
              
// model.form = form;
