// Get the args
var filter = args["filter"];
var sortBy = args["sortBy"];
var sortAsc = args["sortAsc"];
var maxResults = args["maxResults"];
var skipCount = args["skipCount"];

// deprecated - to be removed (support console will need to use /internal/cloud/people)
var internalArg = args["internal"];        
var networkAdminArg = args["networkAdmin"];

// Get the collection of people - the APIs below us do not support arbitrary
// predicates, such as personNodeRef.hasAspect
var allPeople = people.getPeoplePaging(filter, 
                                       utils.createPaging((maxResults != null ? parseInt(maxResults) : 250), (skipCount != null ? parseInt(skipCount) : 0)),
                                       sortBy, 
                                       (((sortAsc == null) || (sortAsc == "true")) ? true : false));

// Filter the collection.
//
// Note that this is filtering the result set AFTER the result set has been truncated - if the maxItems arg is specified.
// So there is a chance of some imperfect behaviour here.
//
// If client code asks for e.g. /api/people?internal=true&maxResults=100
// and there are 200 internal persons distributed uniformly over a result set containing 400 persons
// then we could find that the above 'allPeople' resultSet would be truncated to 100 people, then filtered
// below to yield only 50 internal people.
// Therefore the client code might presume that there are no more internal people - he asked for 100 and only got 50.
// But that would be an artifact of the use of post-filtering.
//
// This is all because we're limited by the existing people.get API and particularly by the People.java and PersonService impls.
// There isn't full support for paging in the existing PersonService REST API and the CannedQueries underneath don't support
// the per-filtering based on hasAspect.
// TODO We might one day want to or need to address the above limitation. The right way to do this is probably to deprecate the
//      the personService REST API, add or extend a service to provide 'proper' paging of cm:people results - it would have to include
//      hasAspect predicates in the CannedQuery for these cloud use cases.

var filteredPeople = [],
    i, ii, scriptNode, hasMarkerAspect,
    internalQuery, adminQuery,
    passesFilters;

for (i = 0, ii = allPeople.length; i < ii; i++)
{
   // The elements of the allPeople array are cm:person NodeRefs, but not ScriptNodes.
   scriptNode = search.findNode(allPeople[i]);
   
   // Assume initially that the next ScriptNode does pass the filters.
   passesFilters = true;
   
   if (internalArg != null)
   {
      // Are we looking for internal people? or external people?
      internalQuery = (internalArg == "true");
      
      // All external persons will have a marker aspect on their cm:person nodes in this tenant.
      hasMarkerAspect = scriptNode.hasAspect("cloud:personExternal");
      if (hasMarkerAspect == internalQuery)
      {
         passesFilters = false;
      }
   }
   
   if (networkAdminArg != null)
   {
      // Are we looking for networkAdmins or non-networkAdmins?
      adminQuery = (networkAdminArg == "true");
      
      // All admin persons will have a marker aspect on their cm:person nodes in this tenant.
      hasMarkerAspect = scriptNode.hasAspect("cloud:networkAdmin");
      if (hasMarkerAspect != adminQuery)
      {
         passesFilters = false;
      }
   }
   
   if (passesFilters)
   {
      filteredPeople.push(allPeople[i]);
   }
}


// Pass the queried people to the template
model.peoplelist = filteredPeople;
