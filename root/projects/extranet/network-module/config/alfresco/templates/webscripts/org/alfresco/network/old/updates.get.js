function toLuceneDateString(month, day, year)
{
	// convert the current date to lucene string
	var str = "";
	str += year;
	str += "\\-";
	
	var monthString = "" + month;
	if(monthString.length < 2)
	{
		monthString = "0" + monthString;
	}
	str += monthString;
	str += "\\-";
		
	var dayString = "" + day;
	if(dayString.length < 2)
	{
		dayString = "0" + dayString;
	}
	str += dayString;
	
	str += "T00:00:00";
	
	return str;
}

function extract(contents, car, cdr)
{
	var result = "";
	
	var x1 = contents.indexOf(car);
	if(x1 > -1)
	{
		var newContents = contents.substring(x1 + car.length);
		var x2 = newContents.indexOf(cdr);
		if(x2 > -1)
		{
			result = newContents.substring(0, x2);
			result = cleanup(result);
		}
	}
	
	return result;
}

function cleanup(str)
{
	str = str.replace("\"", "'");
	str = str.replace("\r", "");
	str = str.replace("\n", "");
	str = str.trim();
	
	return str;
}




var category = args["category"];
if(category == null)
{
	category = "HEAD";
}

// convert to lucene dates

// now
var now = new Date();
var luceneNow = toLuceneDateString(now.getMonth() + 1, now.getDate(), now.getFullYear());

// then
var then = new Date(now.getTime() - (7*24*60*60*1000)); // a week ago
var luceneThen = toLuceneDateString(then.getMonth() + 1, then.getDate(), then.getFullYear());



// run the lucene query
var array = new Array();

var query = "+PATH:\"/app:company_home//cm:Network/cm:Enterprise/cm:JiraNotifications/cm:Categorized/cm:" + category + "/*\"";
query += " ";
query += "+@cm\\:modified:[" + luceneThen + " TO " + luceneNow + "]";

var children = search.luceneSearch(query);
for(var i = 0; i < children.length; i++)
{
	var item = { };
	
	var content = children[i].content;
	var x1 = content.indexOf("---");
	if(x1 > -1)
	{
		var title = content.substring(0,x1);
		title = cleanup(title);
		
		item["title"] = title;
	}
	
	item["key"] = extract(content, "Key:", "\r");
	item["url"] = extract(content, "URL:", "\r");
	item["category"] = extract(content, "HEAD or Branch:", "\r");
	item["components"] = extract(content, "Components:", "\r");
	item["overview"] = extract(content, "--Overview--", "--Details--");
	item["details"] = extract(content, "--Details--", "--");
	
	array[array.length] = item;
}
model.objects = array;

model.query = query;