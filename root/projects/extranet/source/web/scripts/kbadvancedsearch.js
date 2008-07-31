var article_type     = '';
var searchText       = '';
var alfresco_version = '';

function explodeDomainName(link)
{
    var domain = new Array();
    domain = link.split('/');
    var url ='';
    for(var i=0; i<domain.length; i++)
    {
        if(i>=3)
        {
        	url +=  '/' + domain[i] ;
        }
    }
    return url;
}

function categorydisplay() 
{
	vista = (document.getElementById("categorydisplay").style.display == 'none') ? 'block' : 'none';
	document.getElementById("categorydisplay").style.display = vista;
	toggle = (document.getElementById("toggleadvanced").innerHTML == 'Advanced&gt;&gt;') ? '&lt;&lt;Basic' : 'Advanced&gt;&gt;';
	document.getElementById("toggleadvanced").innerHTML = toggle;
	if (toggle == "Advanced&gt;&gt;") 
	{
		resetCategories();
	}
}

function resetCategories() 
{
	document.getElementById("alfresco_version").options[0].selected = true;
	document.getElementById("article_type").options[0].selected = true;
}

function resetSearchArticles() 
{
	document.getElementById("alfresco_version").options[0].selected = true;
	document.getElementById("article_type").options[0].selected = true;
	resetCategories('');
	document.getElementById("categorydisplay").style.display = 'none';
	document.getElementById("toggleadvanced").innerHTML = 'Advanced&gt;&gt;';
	document.getElementById("searchText").value = "";
	article_type     = '';
        searchText  	 = '';
        alfresco_version = '';
}

function setAlfrescoversion()
{
	alfresco_version= document.getElementById("alfresco_version").value;
	textsearch();
}

function setArticletype()
{
	article_type  =  document.getElementById("article_type").value;
	textsearch();
}

function textsearch()
{
	YAHOO.util.Event.onDOMReady(function() 
	{
	    YAHOO.example.ClientPagination = new function() 
	    {
		this.formatUrl = function(elCell, oRecord, oColumn, sData) 
		{
		    var x = oRecord.getData("originallink").indexOf("/d/a");
		    if(x > -1)
		    {
		        dataLink = '/extranet/proxy/alfresco/api/node/content' + oRecord.getData("originallink").substring(x+4);
		    }
		    elCell.innerHTML = "<a href='" + dataLink + "'>" + sData + "&nbsp;<img src=\""+oRecord.getData("icon")+"\" alt=\"download\" style=\"border-width:0px;vertical-align:right;\"></img></a>";
		};
		
		
		var myColumnDefs = [
		    {key:"id", label:"S.No",sortable:true},
		    {key:"title", label:"Title",sortable:true, formatter:this.formatUrl},
		    {key:"description", label:"Description",sortable:true},
		    {key:"authorname", label:"Creator",sortable:true},
		    {key:"modifier", label:"Modified",sortable:true},
		    {key:"type", label:"Type",sortable:true},
		    {key:"updated", label:"updated",sortable:true},
		    {key:"version", label:"Version",sortable:true},
		    {key:"tags", label:"Tags",sortable:true}
		];
		
		var url = "/extranet/proxy/alfresco/kb/advancedsearchresults.json?searchText="+document.getElementById('searchText').value+"&article_type="+document.getElementById('article_type').value+"&alfresco_version="+alfresco_version+"&";

		this.myDataSource = new YAHOO.util.DataSource(url);
		this.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
		this.myDataSource.responseSchema = {
		    resultsList: "records",
		    fields: ["id","title","description","authorname","modifier","type","updated","version","tags","swflink","originallink","icon"],
		    metaFields : {
		    	totalRecords: 'totalRecords' // The totalRecords meta field is
						 // a "magic" meta, and will be passed
						 // to the Paginator.
		    }
		};

		var oConfigs = 
		{
			paginator: new YAHOO.widget.Paginator({ rowsPerPage: 15 })
		};

		this.myDataTable = new YAHOO.widget.DataTable("paginated", myColumnDefs, this.myDataSource, oConfigs);
				
	    };
	});
}