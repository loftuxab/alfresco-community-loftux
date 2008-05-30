<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="content-type" content="text/html;charset=iso-8859-1">
  <link rel="stylesheet" href="./css/extranet.css" type="text/css">
  <title>Alfresco Extranet</title>
  
  ${head}
  <script type="text/javascript">
   
     function ajaxRead()
     {
		file = "/extranet/proxy/alfresco/kb/search.atom?q=" + document.myform.search.value ;
 		
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
		var insertData = '<table style="width:833px; border: solid 3px #cde"><tr><th>' + 'Search Results for the Keyword : '+ document.myform.search.value +'</th></tr>';
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

<div style="width: 829px;" class="content">

	<div class="header">
	
		<div class="top_info_right">

			<!-- Start Top Info Right -->
			<@region id="user-tools" scope="global"/>
			<!-- End Top Info Right -->

		</div>

		<div class="logo">

			<!-- Start Top Info Left -->
			<@region id="logo" scope="global"/>
			<!-- End Top Info Left -->
		
		</div>

	</div>

	<div class="bar">

		<@region id="navigation" scope="global" />

	</div>

	<div class="search_field">
	
		<@region id="search" scope="global" />

	</div>


	<div align="center">
		<br/>
		<br/>
		TODO:  The knowledge base integration!
		<br/>
		<br/>
		<br/>
	</div>
	
	<div>
	
		<@region id="kbsearch" scope="global" />

	</div>
	
	<!-- FOOTER -->
	<div class="footer">
	
		<@region id="footer" scope="global"/>

	</div>
</div>

</body>
</html>
