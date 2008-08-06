<#import "include/globals.ftl" as global />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>${page.title}</title>
	<@global.header/>
	${head}
	
<link rel="stylesheet" type="text/css" href="yui/container/assets/container.css" />
<link rel="stylesheet" type="text/css" href="yui/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="yui/datatable/assets/skins/sam/datatable.css" />

<script type="text/javascript" src="yui/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="yui/connection/connection-min.js"></script>
<script type="text/javascript" src="yui/json/json-min.js"></script>
<script type="text/javascript" src="yui/element/element-beta-min.js"></script>
<script type="text/javascript" src="yui/datasource/datasource-beta-min.js"></script>
<script type="text/javascript" src="yui/datatable/datatable-beta-min.js"></script>
<script type="text/javascript" src="scripts/kbadvancedsearch.js"></script>
<script type="text/javascript" src="yui/button/button-min.js"></script>
<script type="text/javascript" src="yui/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="yui/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="yui/container/container-min.js"></script>
<script type="text/javascript" src="scripts/swfobject.js"></script>

<style type="text/css">
	.mask 
	{
		-moz-opacity: 0.8;
		opacity:.80;
		filter: alpha(opacity=80);
		background-color:#2f2f2f;
	}
	#paginated {
        text-align: center;
    }
    #paginated table {
        margin-left:auto; margin-right:auto;
    }
    #paginated .yui-pg-container a {
        color: #00d;
    }
    #paginated .yui-pg-pages a {
        text-decoration: underline;
    }
    #paginated, #paginated .yui-dt-loading {
        text-align: center; background-color: transparent;
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
		<div id="bd" class="yui-skin-sam">
		
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