<import resource="/include/support.js">

var object = instance.object;

// source resource
var source = instance.object.resources.get("source");
if(source == null)
{
	source = instance.object.resources.add("source");
}

// set properties
for each(field in formdata.fields)
{
	if(field.id != null)
	{	
		if ((field.id.length >= 6) && (field.id.substring(0,6) == "source"))
		{
			var attributeName = field.id.substring(6).toLowerCase();
			source[attributeName] = field.value;
		}
		else
		{
			object.setProperty(field.id, field.value);
		}
	}
}

object.save();
