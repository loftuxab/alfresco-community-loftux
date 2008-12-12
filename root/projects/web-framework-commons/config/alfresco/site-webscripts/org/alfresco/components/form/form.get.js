//Constraint Handlers
model.constraints = [];
var constraintTypes = config.global["constraint-handlers"].itemNames;
model.constraints['types'] = constraintTypes;
model.constraints['handlers'] = [];
model.constraints['messages'] = [];
model.constraints['messageIDs'] = [];

for (var i = 0; i < constraintTypes.size(); i++)
{
	var constName = constraintTypes.get(i);
	model.constraints['handlers'][constName] = config.global["constraint-handlers"].items[constName].validationHandler;
	model.constraints['messages'][constName] = config.global["constraint-handlers"].items[constName].message;
	model.constraints['messageIDs'][constName] = config.global["constraint-handlers"].items[constName].messageId;
}

//Default Controls
model.defaultcontrols = [];

var controlNames = config.global['default-controls'].itemNames;
model.defaultcontrols['names'] = controlNames;

model.defaultcontrols['templates'] = [];
model.defaultcontrols['control-params'] = [];

for (var x = 0; x < controlNames.size(); x++)
{
	var ctrlName = controlNames.get(x);
	model.defaultcontrols['templates'][ctrlName] = config.global['default-controls'].items[ctrlName].template;

	model.defaultcontrols['control-params'][ctrlName] = [];
	model.defaultcontrols['control-params'][ctrlName]['names'] = [];
	model.defaultcontrols['control-params'][ctrlName]['values'] = [];

	var params = config.global['default-controls'].items[ctrlName].controlParams;
	for (var p = 0; p < params.size(); p++)
	{
		var nextParam = params.get(p);
		model.defaultcontrols['control-params'][ctrlName]['names'].push(nextParam.name);
		model.defaultcontrols['control-params'][ctrlName]['values'][nextParam.name] = nextParam.value;
	}
}

// Some representative Form data just to show it works.
model.form = [];
model.form['submissionURL'] = config.scoped['content']['form'].submissionURL;
model.form.viewFieldNames = config.scoped['content']['form'].visibleViewFieldNames;
model.form.viewFields = config.scoped['content']['form'].visibleViewFields;



model.submitLabel = 'OK';
	




var fakedJson = "{   \"foo\" : \"bar\", " +
" \"items\" : [ \"it1\", \"it2\", \"it3\", \"it4\" ] }";
var data = eval('(' + fakedJson + ')');
model.rubbish = data.foo;

model.fields = [];
for (var x=0; x < data.items.length; x++)
{
   model.fields.push(data.items[x]);
}