
function removeRepositorySearchScope()
{
   var scopeSelectionMenu = widgetUtils.findObject(model.jsonModel.widgets, "id", "FCTSRCH_SCOPE_SELECTION_MENU_GROUP");
   if (scopeSelectionMenu && scopeSelectionMenu.config && scopeSelectionMenu.config.widgets)
   {
      // Remove the repository option...
      var w = scopeSelectionMenu.config.widgets;
      if (w.length === 3)
      {
         w.splice(2, 1);
      }
      else if (w.length === 2)
      {
         w.splice(1, 1);
      }

      // Change the value of All Sites to actually be REPO...
      // var l = w.length;
      // if (l > 0)
      // {
      //    w[l-1].config.value = "REPO";
      //    w[l-1].config.publishPayload.value = "REPO";
      //    w[l-1].config.checked = true;
      // }
   }
}

removeRepositorySearchScope();

// Add in a service for capturing analytics...
model.jsonModel.services.push("myalfresco/analytics/FacetedSearchAnalyticsService");