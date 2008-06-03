<#import "include/globals.ftl" as global />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<title><@pageTitle/></title>
	<@global.header/>
	${head}
	
  <script type="text/javascript">
   
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
    
    
    function processXML(obj)
    {
    	document.getElementById ('dataArea').visible = true;
    	
     	var dataArray = obj.getElementsByTagName('entry');
		var dataArrayLen = dataArray.length;
		var insertData = '<table style="width:833px; border: solid 3px #cde"><tr><th>' + 'Search Results for the Keyword : '+ document.kbsearch.search.value +'</th></tr>';
     	 for (var i=0; i<dataArrayLen; i++)
     	 {
			 var summary = dataArray[i].getElementsByTagName('summary');
	 		 var updated = dataArray[i].getElementsByTagName('updated');
	 		 var title = dataArray[i].getElementsByTagName('title');	
	 		 var category = dataArray[i].getElementsByTagName('category');
         	 insertData += '<tr><td style="color:red; font-size: 1.5em;"><a href=#>' + title[0].firstChild.data + '</a></td></tr>';
         	 insertData += '<tr><td>' + summary[0].firstChild.data + '</td></tr>';
	 		 insertData += '<tr><td>' + updated[0].firstChild.data + '--- Category : '+ category[0].firstChild.data + '</td></tr>';
	 		 insertData += '<tr><td><hr color:red;background:red;height:1px;border:0;/></td></tr>';
     	}
      insertData += '</table><br/><br/>';
      
      document.getElementById ('dataArea').innerHTML = insertData;
    }
	
	function clearData()
	{
		//document.getElementById ('dataArea').innerHTML = '';
		//document.getElementById ('search').value ='';
		document.getElementById ('dataArea').visible = false;
		return false;
	}
	
    </script>
	
</head>

<body>
	<div id="custom-doc" class="yui-t6">
	
		<!-- HEADER start -->
		<div id="hd">
			<@region id="header" scope="theme"/>
		</div>
		<!-- HEADER end -->
		
		<!-- NAVIGATION start -->
		<div id="nav">
			<@region id="navigation" scope="global"/>
		</div>
		<!-- NAVIGATION end -->



		<!-- BODY -->
		<div id="bd">
		
			<@region id="search" scope="page"/>
			
			<hr/>
			
			<@region id="body" scope="page"/>
			
		</div>
			
		<!-- BODY end -->
		
		
		
		
		
		<!-- FOOTER start -->
		<div id="ft">
			<@region id="footer" scope="global"/>
		</div>
		<!-- FOOTER end -->
		
	</div>

</body>
</html>
