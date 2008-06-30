/**
 * This function sets all data necessary to render the paginator.
 * 
 * @param data the data object containing the pagination data (startIndex, pageSize, itemCount, total)
 */
function getPaginatorRenderingData(data)
{
    var startIndex = data.startIndex;
    var pageSize = data.pageSize;
    var itemCount = data.itemCount;
    var total = data.total;

    // don't do anything if pageSize < 1
    if (pageSize < 1) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "pageSize must not be smaller than 1");
        return null;
    }

    // prepare data containers
    var p = {};
    p.pages = new Array();

    // general data
    p.startIndex = startIndex;
    p.pageSize = pageSize;
    p.itemCount = itemCount;
    p.total = total;
    
    // simplify calculation if there are no elements
    if (total < 1 && pageSize)
    {
        p.showFirst = false;
        p.showLast = false;
        p.numPages = 1;
        p.currPage = 1;
        p.pages.push(1); 
    }
    else
    {
        // show first and last link
        p.showFirst = startIndex > 0;
        p.showLast = (startIndex + pageSize) < total
    
        // number of pages
        var numPages = parseInt(total / pageSize) + 1;
        if ((total % pageSize) == 0) {
            numPages--;
        }
        p.numPages = numPages;
        
        // current page
        var currPage = 1;
        if (startIndex > 0) {
            currPage = (startIndex / pageSize) + 1;
        }
        p.currPage = currPage;
        
        // assign pages that should have a direct link.
        // we show by max 9 links (4 before and 4 after)
        var x = currPage - 4;
        if (x < 1) {
            x = 1;
        }
        for ( ; x < 9 && x <= numPages; x++)
        {
            p.pages.push(x);
        }   
    }
    
    return p;
}
