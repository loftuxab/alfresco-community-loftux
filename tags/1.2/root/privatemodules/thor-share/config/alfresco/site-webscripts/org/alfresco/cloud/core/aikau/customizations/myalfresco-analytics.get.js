// This extension adds in the GetSatisfaction and GoSquared analytics tools to full Aikau pages...
var footer = widgetUtils.findObject(model.jsonModel.widgets, "id", "ALF_STICKY_FOOTER");
if (footer && footer.config && footer.config.widgetsForFooter)
{
   var widgets = footer.config.widgetsForFooter.push(
      {
         name: "myalfresco/analytics/GetSatisfaction",
         config: {
            id: "GetSatisfaction"
         }
      },
      {
         name: "myalfresco/analytics/GoSquared",
         config: {
            id: "GoSquared"
         }
      }
   );
}