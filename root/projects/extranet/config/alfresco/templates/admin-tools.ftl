<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<title>${page.title}</title>
	${head}
</head>

<body>
	<div id="custom-doc" class="yui-t6">
	
		<!-- HEADER start -->
		<div id="hd">
			<@region id="header" scope="page"/>
		</div>
		<!-- HEADER end -->
		
		<!-- BODY  -->
		<div id="bd">		
			<@region id="body" scope="page"/>
		</div>
		<!-- BODY end -->
				
	</div>

</body>
</html>
