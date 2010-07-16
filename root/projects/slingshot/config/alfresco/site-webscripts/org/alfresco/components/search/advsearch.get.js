/**
 * Advanced Search component GET method
 */

function main()
{
   // fetch the request params required by the advanced search component template
   var siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
   
   // get the search forms from the config
   var forms = config.scoped["AdvancedSearch"]["advanced-search"].getChild("forms").childrenMap["form"];
   var searchForms = [];
   for (var i = 0, form, formId, formDesc; i < forms.size(); i++)
   {
      form = forms.get(i);
      
      // get optional attributes and resolved description text
      formId = form.attributes["id"];
      formDesc = form.attributes["description"];
      if (formDesc == null)
      {
         formDesc = form.attributes["descriptionId"];
         if (formDesc != null)
         {
            formDesc = msg.get(formDesc);
         }
      }
      
      // create the model object to represent the form definition
      searchForms.push(
      {
         id: formId ? formId : "search",
         type: form.value,
         description: formDesc ? formDesc : formId
      });
   }
   
   // Prepare the model
   model.siteId = siteId;
   model.searchForms = searchForms;
}

main();