/**
 * Record Search Component: rmsearch
 *
 * Inputs:
 *   mandatory: site = site ID to search in
 *   optional:  query = the query to perform.
 *   optional:  maxResults = max items to return.
 * 
 * Outputs:
 *  data.items/data.error - object containing list of search results
 */
const DEFAULT_MAX_RESULTS = 100;
const SITES_SPACE_QNAME_PATH = "/app:company_home/st:sites/";


/**
 * Returns person display name string as returned to the user.
 * 
 * Caches the person full name to avoid repeatedly querying the repository.
 */
var personDataCache = [];
function getPersonDisplayName(userId)
{
   if (personDataCache[userId] != undefined)
   {
      return personDataCache[userId];
   }
   
   var displayName = "";
   var person = people.getPerson(userId);
   if (person != null)
   {
      displayName = person.properties.firstName + " " + person.properties.lastName;
   }
   personDataCache[userId] = displayName;
   return displayName;
}

/**
 * Returns a record from the fileplan container with properties attached.
 */
function getRecord(siteId, node)
{
   // check whether this is a valid file record
   var item = null;
   if (node.hasAspect("rma:record"))
   {
      item =
      {
         nodeRef: node.nodeRef.toString(),
         name: node.name,
         title: node.properties["cm:title"],
         description: node.properties["cm:description"],
         modifiedOn: node.properties["cm:modified"],
         modifiedByUser: node.properties["cm:modifier"],
         createdOn: node.properties["cm:created"],
         createdByUser: node.properties["cm:creator"],
         size: node.size,
         properties: {}
      };
      
      // collect up the RMA namespace properties
      // TODO: collect up props under aspects extending rma:customRMData marker
      for (var k in node.properties)
      {
         if (k.match("^{http://www.alfresco.org/model/recordsmanagement/1.0}") == "{http://www.alfresco.org/model/recordsmanagement/1.0}")
         {
            item.properties[k.split('}')[1]] = node.properties[k];
         }
      }
      
      // generated properties
      item.modifiedBy = getPersonDisplayName(item.modifiedByUser);
      item.createdBy = getPersonDisplayName(item.createdByUser);
      // TODO: check this is right in RM doclib!
      item.browseUrl = "document-details?nodeRef=" + node.nodeRef.toString();
   }
   
   return item;
}

/**
 * Processes the search results. Filters out unnecessary nodes
 * 
 * @return the final search results object
 */
function processResults(nodes, maxResults, siteId)
{    
   var results = [],
      added = 0,
      item,
      i, j;
   
   for (i = 0, j = nodes.length; i < j && added < maxResults; i++)
   {
      item = getRecord(siteId, nodes[i]);
      if (item !== null)
      {
         results.push(item);
         added++;
      }
   }
   
   return (
   {
      items: results
   });
}

/**
 * Return Search results with the given search terms
 * Terms are split on whitespace characters.
 * 
 * AND, OR and NOT are supported - as their Lucene equivalent.
 */
function getSearchResults(query, maxResults, siteId)
{
   // rm doclib fileplan site path
   // TODO: is this rma:documentLibrary?
   var path = SITES_SPACE_QNAME_PATH + "cm:" + search.ISO9075Encode(siteId) + "/cm:documentLibrary/";
	
   var luceneQuery = "";
   // TODO: add query built from supplied info and query itself
   // TODO: temp!
   luceneQuery = "TEXT:\"record*\"";
   /*if (query !== null && query.length !== 0)
   {
      luceneQuery += "TEXT:\"" + t + "\"" +        // full text
                     " @cm\\:name:\"" + t + "\"";
   }*/
   
   var nodes;
   
   // if we processed the search terms, then suffix the PATH query
   if (luceneQuery.length !== 0)
   {
      luceneQuery = "+PATH:\"" + path + "/*\" +(" + luceneQuery + ") ";
      luceneQuery += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\"";
      nodes = search.luceneSearch(luceneQuery);
   }
   else
   {
      // failed to process the search string - empty list returned
      nodes = [];
   }
   
   return processResults(nodes, maxResults, siteId);
}


function main()
{
   var siteId = args.site;
   var query = args.query;
   var maxResults = (args.maxResults !== null) ? parseInt(args.maxResults) : DEFAULT_MAX_RESULTS;
   
   model.data = getSearchResults(query, maxResults, siteId);
}

main();