<import resource="/include/support.js">

var object = instance.object;

for each(field in formdata.fields)
{
	object.setProperty(field.id, field.value);
}

object.save();
