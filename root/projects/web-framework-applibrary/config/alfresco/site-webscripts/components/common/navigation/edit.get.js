<import resource="/components/common/js/component.js">

// bind core properties and source
WebStudio.Component.bind();

// bind custom properties
WebStudio.Component.bindProperty("style");
WebStudio.Component.bindProperty("orientation");

WebStudio.Component.bindProperty("startingPage");
WebStudio.Component.bindProperty("topPage");
WebStudio.Component.bindProperty("childSiblings");


// default values
if(model.style.value == null || model.style.value == "")
{
	model.style.value = "tabbed";
}
if(model.orientation.value == null || model.orientation.value == "")
{
	model.orientation.value = "horizontal";
}
if(model.startingPage.value == null || model.startingPage.value == "")
{
	model.startingPage.value = "siteroot";
}
if(model.topPage.value == null || model.topPage.value == "")
{
	model.topPage.value = "show";
}
if(model.childSiblings.value == null || model.childSiblings.value == "")
{
	model.childSiblings.value = "showChildren";
}

