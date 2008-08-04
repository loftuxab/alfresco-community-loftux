//ask search & swf preview

YAHOO.namespace('com.alfresco');
function init() {
	var handleCancel = function(o){
		this.cancel();
	}
	YAHOO.com.alfresco.dialog = new YAHOO.widget.Dialog("lightbox_display",
		{ 
			width : "550px",
			fixedcenter : false,
			visible : false,
			close: true,
			draggable: true,
			modal: true,
			y: 25,
			constraintoviewport : false
		}
	);
	
	YAHOO.util.Event.addListener('closeImg', 'click', function(o){YAHOO.com.alfresco.dialog.hide()});	
	YAHOO.com.alfresco.dialog.render();

	var ondialogShow = function(e, args, o)
	{
		o.body.id = 'videoCon';
		//flash embed script, more information: http://blog.deconcept.com/swfobject/#download
		var so = new SWFObject(strObj[selectedVar], "sotester", "500", "690", "9", "#000000");
		so.write("videoCon");
	};
	YAHOO.com.alfresco.dialog.showEvent.subscribe(ondialogShow, YAHOO.com.alfresco.dialog);
	var ondialogHide = function(e, args, o)
	{
		o.setBody('');
	};
	YAHOO.com.alfresco.dialog.hideEvent.subscribe(ondialogHide, YAHOO.com.alfresco.dialog);
	YAHOO.util.Dom.setStyle(['lightbox_display'], 'display', 'block');
};
YAHOO.util.Event.addListener(window, "load", init);

//Script for Search
var article_type     = '';
var searchText       = '';
var alfresco_version = '';
var strObj = new Array;
var selectedVar = 0;

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

function setSelectedVar(i)
{
	selectedVar = i;
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
		    var y = oRecord.getData("swflink").indexOf("/d/a");
		    if(y > -1)
		    {
		       swfLink = '/extranet/proxy/alfresco/api/node/content' + oRecord.getData("swflink").substring(x+4);
		    }	
		    strObj[oRecord.getData("id")] = swfLink;
		    //elCell.innerHTML = "<a href=\"#void\" onclick=\"setSelectedVar(" + oRecord.getData("id") +");YAHOO.com.alfresco.dialog.show();\"><img src=\"images/preview.gif\" style=\"border-width:0px;vertical-align:middle;\"></img></a>&nbsp;<a href=\"" + dataLink + "\">" + sData + "&nbsp;<img src=\""+oRecord.getData("icon")+"\" alt=\"download\" style=\"border-width:0px;vertical-align:right;\"></img></a>";
		    elCell.innerHTML = "<a href=\"#void\" onclick=\"setSelectedVar(" + oRecord.getData("id") +");YAHOO.com.alfresco.dialog.show();\"><img src=\"images/preview.gif\" style=\"border-width:0px;vertical-align:middle;\"></img></a>&nbsp;<a href=\"" + dataLink + "\">" + sData + "&nbsp;<img src=\"proxy/alfresco-webapp"+oRecord.getData("icon")+"\" alt=\"download\" style=\"border-width:0px;vertical-align:right;\"></img></a>";
		};
		
		
		var myColumnDefs = [
		    { key:"id", label:"S.No", sortable:true },
		    { key:"title", label:"Title", sortable:true, formatter:this.formatUrl },
		    { key:"description", label:"Description", sortable:true },
		    { key:"authorname", label:"Creator", sortable:true },
		    { key:"modifier", label:"Modified", sortable:true },
		    { key:"type", label:"Type", sortable:true },
		    { key:"updated", label:"updated", sortable:true },
		    { key:"version", label:"Version", sortable:true },
		    { key:"tags", label:"Tags", sortable:true }
		];
		
		//var url = "/extranet/proxy/alfresco/kb/advancedsearchresults.json?searchText="+document.getElementById('searchText').value+"&article_type="+document.getElementById('article_type').value+"&alfresco_version="+alfresco_version+"&";
		var url = "/extranet/proxy/alfresco/kb2/advancedsearchresults?searchText="+document.getElementById('searchText').value+"&article_type="+document.getElementById('article_type').value+"&alfresco_version="+alfresco_version+"&";
		
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
			paginator: new YAHOO.widget.Paginator({ rowsPerPage: 10 })
		};

		this.myDataTable = new YAHOO.widget.DataTable("paginated", myColumnDefs, this.myDataSource, oConfigs);
	    };
	});
}