// Remove Groups link
for (var i = 0; i < model.links.length; i++)
{
   if (model.links[i].id == "site-groups-link")
   {
      model.links.splice(i, 1);
   }
}