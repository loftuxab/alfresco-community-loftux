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
      // collect up custom props under the rmc:customProperties marker
      for (var k in node.properties)
      {
         if (k.match("^{http://www.alfresco.org/model/recordsmanagement/1.0}") == "{http://www.alfresco.org/model/recordsmanagement/1.0}")
         {
            item.properties["rma_" + k.split('}')[1]] = node.properties[k];
         }
         else if (k.match("^{http://www.alfresco.org/model/rmcustom/1.0}") == "{http://www.alfresco.org/model/rmcustom/1.0}")
         {
            item.properties["rmc_" + k.split('}')[1]] = node.properties[k];
         }
      }
      
      // generated properties
      item.modifiedBy = getPersonDisplayName(item.modifiedByUser);
      item.createdBy = getPersonDisplayName(item.createdByUser);
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
function getSearchResults(query, terms, maxResults, siteId)
{
   // rm doclib fileplan site path
   var path = SITES_SPACE_QNAME_PATH + "cm:" + search.ISO9075Encode(siteId) + "/cm:documentLibrary/";
	
   // query for records only
   var alfQuery = "";
   if (terms !== null && terms.length !== 0)
   {
      var tokens = terms.split(/\s/), i, j, t;
      
      for (i = 0, j = tokens.length; i < j; i++)
      {
         t = tokens[i];
         // TODO: add support for quoted terms later
         // remove quotes
         t = t.replace(/\"/g, "");
         if (t.length !== 0)
         {
            switch (t.toLowerCase())
            {
               case "and":
                  if (i < j - 1 && terms[i + 1].length !== 0)
                  {
                     alfQuery += "AND ";
                  }
                  break;
               
               case "or":
                  break;
               
               case "not":
                  if (i < j - 1 && terms[i + 1].length !== 0)
                  {
                     alfQuery += "NOT ";
                  }
                  break;
               
               default:
                  alfQuery += '(TEXT:"' + t + '" @cm\\:name:"*' + t + '*")';
            }
         }
      }
   }
   
   var nodes;
   
   // suffix the PATH query and the ASPECT clause
   if (alfQuery.length !== 0)
   {
      alfQuery = '+(' + alfQuery + ') ';
   }
   if (query != null)
   {
      alfQuery += query;
   }
   alfQuery += ' +ASPECT:"{http://www.alfresco.org/model/recordsmanagement/1.0}record"';
   alfQuery += ' +PATH:"' + path + '/*"';
   alfQuery += ' -TYPE:"{http://www.alfresco.org/model/content/1.0}thumbnail"';
   
   nodes = search.query({query: alfQuery, language: "lucene"});
   
   return processResults(nodes, maxResults, siteId);
}


function main()
{
   var siteId = args.site;
   
   // query is direct lucene format
   var query = args.query;
   
   // terms are full text search terms
   var terms = args.terms;
   
   // maximum results to return - or use default
   var maxResults = (args.maxResults !== null) ? parseInt(args.maxResults) : DEFAULT_MAX_RESULTS;
   
   model.data = getSearchResults(query, terms, maxResults, siteId);
}

main();