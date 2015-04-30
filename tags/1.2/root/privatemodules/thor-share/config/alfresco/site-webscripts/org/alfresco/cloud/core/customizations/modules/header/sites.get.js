function main()
{
   // Make sure only internal users can create sites
   model.showCreateSite = !user.properties["isExternal"];
}

main();
