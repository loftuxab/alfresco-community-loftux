	//Javascript for ask search
	
	//Global variables	
	var strObj = new Array;
	var selectedVar = '';
	
	//namespace	
	YAHOO.namespace('com.alfresco');
	function init() 
	{
		var handleCancel = function(o)
		{
			this.cancel();
		}
	
		YAHOO.com.alfresco.dialog6 = new YAHOO.widget.Dialog("lightbox_display",
		{ 
			width : "550px",
			fixedcenter : false,
			visible : false,
			close: true,
			draggable: true,
			modal: true,
			y: 25,
			constraintoviewport : true
		});
			
		YAHOO.util.Event.addListener('closeImg', 'click', function(o){YAHOO.com.alfresco.dialog6.hide()});
		YAHOO.com.alfresco.dialog6.render();

		var onDialog6Show = function(e, args, o)
		{
			o.body.id = 'videoCon';
	
			//flash embed script, more information: http://blog.deconcept.com/swfobject/#download
			var so = new SWFObject(strObj[selectedVar], "sotester", "500", "680", "9", "#000000");
	
			so.write("videoCon");
		};

		YAHOO.com.alfresco.dialog6.showEvent.subscribe(onDialog6Show, YAHOO.com.alfresco.dialog6);

		var onDialog6Hide = function(e, args, o)
		{
			o.setBody('');
		};
		YAHOO.com.alfresco.dialog6.hideEvent.subscribe(onDialog6Hide, YAHOO.com.alfresco.dialog6);

	
		YAHOO.util.Dom.setStyle(['lightbox_display'], 'display', 'block');
	};

	YAHOO.util.Event.addListener(window, "load", init);

     function ajaxRead()
     {
		file = "/extranet/proxy/alfresco/kb/search.atom?q=" + document.kbsearch.search.value ;
 		
		var xmlObj = null;
      	if(window.XMLHttpRequest)
      	{
          xmlObj = new XMLHttpRequest();
      	}
      	else if(window.ActiveXObject)
      	{
          		xmlObj = new ActiveXObject("Microsoft.XMLHTTP");
      	} else
		alert("not supported");

		//prepare the xmlhttprequest object
		xmlObj.open("GET",file,true);
		xmlObj.setRequestHeader("Cache-Control", "no-cache");
		xmlObj.setRequestHeader("Pragma", "no-cache");
		xmlObj.onreadystatechange = function() {
		if (xmlObj.readyState == 4)
		{
			if (xmlObj.status == 200)
			{
				if (xmlObj.responseText != null)
					processXML(xmlObj.responseXML);
				else
				{
					alert("Failed to receive RSS file from the server - file not found.");
					return false;
				}
			}
			else
				alert("Error code " + xmlObj.status + " received: " + xmlObj.statusText);
		}
	}

	//send the request
	xmlObj.send(null);
    }
    
    function explodeDomainName(link)
	{
		var domain = new Array();
		domain = link.split('/');
		var url ='';
		for(var i=0; i<domain.length; i++)
		{
			if(i>=3) url +=  '/' + domain[i] ;
		}
		return 	url;
	}
	
    function setSelectedVar(i)
	{
		selectedVar = i;
	}
    
    function processXML(obj)
    {
    	document.getElementById ('dataArea').visible = true;
    	 
     	var dataArray = obj.getElementsByTagName('entry');
		var dataArrayLen = dataArray.length;
		var insertData = '<table style=\"width:833px; align:centre;\"><tr><th><strong>' + 'Search Results for the Keyword : '+ document.kbsearch.search.value +'</strong></th></tr>';
     	for (var i=0; i<dataArrayLen; i++)
	     	 {
			 var summary = dataArray[i].getElementsByTagName('summary');
	 		 var updated = dataArray[i].getElementsByTagName('updated');
	 		 var title = dataArray[i].getElementsByTagName('title');	
	 		 var category = dataArray[i].getElementsByTagName('category');
	 		 var link = dataArray[i].getElementsByTagName('link');
			 var originallink = dataArray[i].getElementsByTagName('originallink');
			 var icon = dataArray[i].getElementsByTagName('icon');
			 var url = 'http://hosted13.alfresco.com';
			
			
			 // Changed by Uzi:  Build a link that proxies through the endpoint servlet
			 //swfHyperLink = link[0].attributes.getNamedItem("href").nodeValue;
			 //swfHyperLink = 'http://hosted13.alfresco.com' + explodeDomainName(swfHyperLink);
			 swfHyperLink = link[0].attributes.getNamedItem("href").nodeValue;
			 var y = swfHyperLink.indexOf("/d/a");
			 if(y > -1)
			 {
				swfHyperLink = '/extranet/proxy/alfresco/api/node/content' + swfHyperLink.substring(y+4);
			 }
			 
			 // Changed by Uzi:  Build a link that proxies through the endpoint servlet			 
			//pdfHyperLink = originallink[0].attributes.getNamedItem("href").nodeValue;
			//pdfHyperLink = 'http://hosted13.alfresco.com' + explodeDomainName(pdfHyperLink);
			pdfHyperLink = originallink[0].attributes.getNamedItem("href").nodeValue;
			var x = pdfHyperLink.indexOf("/d/a");
			if(x > -1)
			{
				pdfHyperLink = '/extranet/proxy/alfresco/api/node/content' + pdfHyperLink.substring(x+4);
			}


			//http://hosted13.alfresco.com/alfresco/service/api/path/content/workspace/SpacesStore/Company%20Home/Alfresco/AlfrescoPhonelist.pdf

			iconLink = icon[0].firstChild.data;
			iconLink = 'http://hosted13.alfresco.com' + explodeDomainName(iconLink)

			strObj[i] = swfHyperLink;
			insertData += "<tr><td><a id=\""+ i +"\" href=\"#void\" onclick=\"setSelectedVar("+i+");YAHOO.com.alfresco.dialog6.show();\"><img src=\"images/preview.gif\" style=\"border-width:0px;vertical-align:middle;\"></img></a>&nbsp;"
			insertData += "<a href=\""+ pdfHyperLink+ "\">" + title[0].firstChild.data + "<img src=\""+ iconLink + "\" style=\"border-width:0px;vertical-align:right;\" alt=\"Download\"></img> </a></td></tr>";
			insertData += "<tr><td>" + summary[0].firstChild.data + "</td></tr>";
	 		insertData += "<tr><td>" + updated[0].firstChild.data + "--- Category : "+ category[0].firstChild.data + "</td></tr>";
	 		insertData += "<tr><td><hr style=\"color:red;background:red;height:1px;border:0;\"/></td></tr>";
     		}
		//end of for loop

	      insertData += "</table><br/><br/>";
   
		document.getElementById ('dataArea').innerHTML = insertData;
    }
	
	function clearData()
	{
		document.getElementById ('dataArea').innerHTML = "";
	}
