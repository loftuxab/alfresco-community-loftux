package org.alfresco.service.cmr.search;

import org.alfresco.api.AlfrescoPublicApi;


/**
 * This is the common interface for both row (Alfresco node) and column (CMIS style property or function) based results.
 * The meta-data for the results sets contains the detailed info on what columns are available. For row based result
 * sets there is no selector - all the nodes returned do not have to have a specific type or aspect. For example, an FTS
 * search on properties of type d:content has no type constraint implied or otherwise. Searches against properties have
 * an implied type, but as there can be more than one property -> more than one type or aspect implied (eg via OR in FTS
 * or lucene) they are ignored An iterable result set from a searcher query.<b/> Implementations must implement the
 * indexes for row lookup as zero-based.<b/>
 * 
 * @author andyh
 */
@AlfrescoPublicApi
public interface ResultSet extends ResultSetSPI<ResultSetRow, ResultSetMetaData> // Specific iterator over ResultSetRows
{


}
