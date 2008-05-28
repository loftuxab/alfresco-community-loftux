// A webscript that returns a list has been asked for on the wiki.
var contentTypes = [{id: "Content", value: "Content"}];
if(contentTypes === undefined || contentTypes.length < 1)
{
   status.code = 400;
   status.message = "Could not get contentTypes from the system";
   status.redirect = true;
}
model.contentTypes = contentTypes;