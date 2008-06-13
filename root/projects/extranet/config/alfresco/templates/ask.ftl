<#import "include/globals.ftl" as global />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<title><@pageTitle/></title>
	<@global.header/>
	${head}

<script type="text/javascript" src="yui/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="yui/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="yui/container/container-min.js"></script>
<script type="text/javascript" src="scripts/swfobject.js"></script>
	
<script type="text/javascript" src="scripts/ask-kbsearch.js"></script>
<link rel="stylesheet" type="text/css" href="yui/container/assets/container.css" />
<link rel="stylesheet" type="text/css" media="screen" href="style.css" />

   
	
<style type="text/css">
	.mask 
	{
		-moz-opacity: 0.8;
		opacity:.80;
		filter: alpha(opacity=80);
		background-color:#2f2f2f;
	}
</style>

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
