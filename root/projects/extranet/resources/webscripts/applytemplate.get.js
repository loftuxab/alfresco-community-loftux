// get the template
var templatePath = args["templatePath"];
var template = companyhome.childByNamePath(templatePath);

// get the document
var docPath = args["docPath"];
var document = companyhome.childByNamePath(docPath);

// transform
var output = document.processTemplate(template);
model.output = output;