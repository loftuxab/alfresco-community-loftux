function main()
{
   /*
    Add filters:
     id - unique key to reference filter in array for extensions
     filterId - the id for the filter set, usefule if multiple filters will be used in the future
     filterData - The actual filter in the filter set
     urlParameters - the parameters that will be added to the search url
     label - ui label
   */
   model.filters = [
      { id: "all-filter",     filterId: "users", filterData: "all",      urlParameters: "",                                   label: msg.get("filter.all") },
      { id: "admins-filter",  filterId: "users", filterData: "admins",   urlParameters: "internal=true&networkAdmin=true",    label: msg.get("filter.admins") },
      { id: "members-filter", filterId: "users", filterData: "members",  urlParameters: "internal=true&networkAdmin=false",   label: msg.get("filter.members") },
      { id: "invited-filter", filterId: "users", filterData: "invited",  urlParameters: "internal=false",                     label: msg.get("filter.invited") }
   ];
}
main();